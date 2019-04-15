package net.zhuoweizhang.boardwalk;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;

/*
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
*/

import com.google.android.gms.ads.*;

import net.zhuoweizhang.boardwalk.potato.*;
import net.zhuoweizhang.boardwalk.util.PlatformUtils;

public class LauncherActivity extends Activity implements View.OnClickListener, LaunchMinecraftTask.Listener,
	AdapterView.OnItemSelectedListener {

	public static final String[] versionsSupported = {"18w11a"};

	public TextView usernameText, passwordText;
	public Button loginButton;
	public TextView progressText;
	public ProgressBar progressBar;
	public Button playButton;
	public TextView recommendationText;
	public boolean refreshedToken = false;
	public boolean isLaunching = false;
	public Button logoutButton;
	public Button importCredentialsButton;
	public Button importResourcePackButton;
	public Spinner versionSpinner;
	public List<String> versionsStringList = new ArrayList<String>();
	public ArrayAdapter<String> versionSpinnerAdapter;

	private AdView adView;
	private InterstitialAd interstitial;
	private static Thread extractThread;

	public static final int REQUEST_BROWSE_FOR_CREDENTIALS = 1013; // date when this constant was added
	public static final int REQUEST_BROWSE_FOR_RESOURCE_PACK = 1014;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			enableLaunchButton();
		}
	};

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.launcher_layout);
		loginButton = (Button) findViewById(R.id.launcher_login_button);
		usernameText = (TextView) findViewById(R.id.launcher_username_text);
		passwordText = (TextView) findViewById(R.id.launcher_password_text);
		progressText = (TextView) findViewById(R.id.launcher_progress_text);
		progressBar = (ProgressBar) findViewById(R.id.launcher_progress_bar);
		loginButton.setOnClickListener(this);
		playButton = (Button) findViewById(R.id.launcher_play_button);
		playButton.setOnClickListener(this);
		recommendationText = (TextView) findViewById(R.id.launcher_recommendation_text);
		logoutButton = (Button) findViewById(R.id.launcher_logout_button);
		logoutButton.setOnClickListener(this);
		importCredentialsButton = (Button) findViewById(R.id.launcher_import_credentials_button);
		importCredentialsButton.setOnClickListener(this);
		importResourcePackButton = (Button) findViewById(R.id.launcher_import_resource_pack_button);
		importResourcePackButton.setOnClickListener(this);
		versionSpinner = (Spinner) findViewById(R.id.launcher_version_spinner);
		versionSpinner.setOnItemSelectedListener(this);
		updateVersionSpinner();
		updateUiWithLoginStatus();
		updateRecommendationText();
		if (!BuildConfig.DEBUG) {
			playButton.setEnabled(false);
		}
		handler.sendEmptyMessageDelayed(1337, 1000*30); // 30 seconds
		initAds();
		refreshToken();

		File runtimeDir = getDir("runtime", 0);
		File versionFile = new File(runtimeDir, ExtractRuntime.VERSION_FLAG_NAME);
		if (!versionFile.exists() || new File("/sdcard/boardwalk/extract").exists()) {
			if (extractThread == null) {
				extractThread = new Thread(new ExtractRuntime(this));
				extractThread.start();
			}
		}
	}

	@Override
	protected void onPause() {
		adView.pause();
		super.onPause();
		SharedPreferences prefs = this.getSharedPreferences("launcher_prefs", 0);
		prefs.edit().putString("auth_lastEmail", usernameText.getText().toString()).apply();
	}

	@Override
	protected void onResume() {
		super.onResume();
		usernameText.setText(getSharedPreferences("launcher_prefs", 0).getString("auth_lastEmail", ""));
		adView.resume();
	}

	@Override
	protected void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}

	public void updateUiWithLoginStatus() {
		boolean loggedIn = isLoggedIn();
		usernameText.setEnabled(!loggedIn);
		passwordText.setVisibility(loggedIn? View.GONE: View.VISIBLE);
		playButton.setText(getResources().getText(loggedIn? (refreshedToken? R.string.play_regular : R.string.play_offline)
			: R.string.play_demo));
		loginButton.setVisibility(loggedIn? View.GONE: View.VISIBLE);
		logoutButton.setVisibility(loggedIn? View.VISIBLE: View.GONE);
		importCredentialsButton.setVisibility(loggedIn? View.GONE: View.VISIBLE);
	}

	public void onClick(View v) {
		if (v == loginButton) {
			doLogin();
		} else if (v == playButton) {
			doPreLaunch();
		} else if (v == logoutButton) {
			doLogout();
		} else if (v == importCredentialsButton) {
			doBrowseForCredentials();
		} else if (v == importResourcePackButton) {
			doBrowseForResourcePack();
		}
	}

	public void doLogin() {
		new LoginTask(this).execute(usernameText.getText().toString(), passwordText.getText().toString());
	}

	public void doPreLaunch() {
		// Do we have an interstitial loaded?
		if (!BuildConfig.DEBUG && interstitial.isLoaded()) {
			interstitial.show();
		} else {
			doLaunch();
		}
	}

	public void doLaunch() {
		isLaunching = true;
		adView.pause();
		adView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		loginButton.setVisibility(View.GONE);
		logoutButton.setVisibility(View.GONE);
		playButton.setVisibility(View.GONE);
		versionSpinner.setVisibility(View.GONE);
		importCredentialsButton.setVisibility(View.GONE);
		importResourcePackButton.setVisibility(View.GONE);
		new LaunchMinecraftTask(this, this).execute();
	}

	public void onProgressUpdate(String s) {
		progressText.setText(s);
	}

	public boolean isLoggedIn() {
		return getSharedPreferences("launcher_prefs", 0).getString("auth_accessToken", null) != null;
	}

	public void updateRecommendationText() {
		StringBuilder builder = new StringBuilder();
		if (PlatformUtils.getNumCores() < 2) {
			builder.append(getResources().getText(R.string.recommendation_dual_core)).append("\n");
		}
		/*if (PlatformUtils.getTotalMemory() < (900000L * 1024L)) { // 900MB
			builder.append(getResources().getText(R.string.recommendation_memory)).append("\n");
		}*/
		recommendationText.setText(builder.toString());
	}

	private void initAds() {
		adView = (AdView) findViewById(R.id.ad);
		AdRequest adRequest = AdUtils.addTestDevices(new AdRequest.Builder())
			.build();
		adView.loadAd(adRequest);
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-2652482030334356/4318313426");
		AdRequest adRequest2 = AdUtils.addTestDevices(new AdRequest.Builder())
			.build();
		interstitial.setAdListener(new LauncherAdListener());
		interstitial.loadAd(adRequest2);
	}

	public void onBackPressed() {
		if (isLaunching) return;
		super.onBackPressed();
	}

	private void doLogout() {
		getSharedPreferences("launcher_prefs", 0).edit().
			remove("auth_accessToken").
			remove("auth_profile_name").
			remove("auth_profile_id").
			apply();
		updateUiWithLoginStatus();
	}

	public void onLaunchError() {
		isLaunching = false;
		playButton.setVisibility(View.VISIBLE);
		importResourcePackButton.setVisibility(View.VISIBLE);
		versionSpinner.setVisibility(View.VISIBLE);
		adView.resume();
		adView.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		updateUiWithLoginStatus();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(getResources().getString(R.string.about_app));
		/*if (Build.VERSION.SDK_INT >= 16) { // Jelly Bean
			menu.add(getResources().getString(R.string.export_log));
		}*/
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		CharSequence itemName = item.getTitle();
		if (itemName.equals(getResources().getString(R.string.about_app))) {
			startActivity(new Intent(this, AboutAppActivity.class));
			return true;
		} else if (itemName.equals(getResources().getString(R.string.export_log))) {
			doExportLog();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public void doBrowseForCredentials() {
/*
		new AlertDialog.Builder(this).setMessage(R.string.login_import_credentials_info).
			setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogI, int button) {
						Intent target = FileUtils.createGetContentIntent();
						target.setType("application/json");
						target.setClass(LauncherActivity.this, FileChooserActivity.class);

						startActivityForResult(target, REQUEST_BROWSE_FOR_CREDENTIALS);
					}
			}).
			setNegativeButton(android.R.string.cancel, null).
			show();
*/
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
/*
			case REQUEST_BROWSE_FOR_CREDENTIALS:
				if (resultCode == RESULT_OK) {  
					final Uri uri = data.getData();
					File file = FileUtils.getFile(uri);
					new ImportVanillaAuthTask(this).execute(file.getAbsolutePath());
				}
				break;
			case REQUEST_BROWSE_FOR_RESOURCE_PACK:
				if (resultCode == RESULT_OK) {
					final Uri uri = data.getData();
					File file = FileUtils.getFile(uri);
					new ImportResourcePackTask(this).execute(file);
				}
				break;
*/
			default:
				super.onActivityResult(requestCode, resultCode, data);
				break;
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent == versionSpinner) {
			String theVersion = versionsStringList.get(position);
			SharedPreferences prefs = this.getSharedPreferences("launcher_prefs", 0);
			if (prefs.getString("selected_version", MainActivity.VERSION_TO_LAUNCH).equals(theVersion)) return;
			prefs.edit().putString("selected_version", theVersion).apply();
			System.out.println("Version: " + theVersion);
		}
	}
	public void onNothingSelected(AdapterView<?> parent) {
	}


	private void doExportLog() {
		try {
			Runtime.getRuntime().exec(new String[] {"logcat", "-d", "-f", "/sdcard/boardwalk/log.txt"});
			progressText.setText("Log exported to /sdcard/boardwalk/log.txt");			
		} catch (Exception e) {
			e.printStackTrace();
			// Sigh.
		}
	}

	private void doBrowseForResourcePack() {
/*
		Intent target = FileUtils.createGetContentIntent();
		target.setType("application/zip");
		target.setClass(LauncherActivity.this, FileChooserActivity.class);

		startActivityForResult(target, REQUEST_BROWSE_FOR_RESOURCE_PACK);
*/
	}

	private String[] listVersionsInstalled() {
		File versionsDir = new File(Environment.getExternalStorageDirectory(), "boardwalk/gamedir/versions");
		String[] retval = versionsDir.list();
		if (retval == null) retval = new String[0];
		return retval;
	}

	private void updateVersionSpinner() {
		versionsStringList.addAll(Arrays.asList(versionsSupported));
		for (String s: listVersionsInstalled()) {
			if (!versionsStringList.contains(s)) versionsStringList.add(s);
		}
		String selectedVersion = getSharedPreferences("launcher_prefs", 0).
			getString("selected_version", MainActivity.VERSION_TO_LAUNCH);
		if (!versionsStringList.contains(selectedVersion)) versionsStringList.add(selectedVersion);
		versionSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, versionsStringList);
		versionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		versionSpinner.setAdapter(versionSpinnerAdapter);

		versionSpinner.setSelection(versionsStringList.indexOf(selectedVersion));
		new RefreshVersionListTask(this).execute();
	}

	public void addToVersionSpinner(List<String> newVersions) {
		String selectedVersion = getSharedPreferences("launcher_prefs", 0).
			getString("selected_version", MainActivity.VERSION_TO_LAUNCH);
		versionsStringList.clear();
		versionsStringList.addAll(newVersions);
		for (String s: listVersionsInstalled()) {
			if (!versionsStringList.contains(s)) versionsStringList.add(s);
		}

		int selectedVersionIndex = versionsStringList.indexOf(selectedVersion);
		System.out.println("Selected version: " + selectedVersion + " index: " + selectedVersionIndex);
		versionSpinnerAdapter.notifyDataSetChanged();
		versionSpinner.setSelection(selectedVersionIndex);
	}

	private void enableLaunchButton() {
		playButton.setEnabled(true);
	}

	private void refreshToken() {
		if (!isLoggedIn()) return;
		new RefreshAuthTokenTask(this).execute();
	}

	public void waitForExtras() {
		// from Listener class.
		if (extractThread != null) {
			try {
				extractThread.join();
			} catch (InterruptedException lolnope) {
				lolnope.printStackTrace();
			}
		}
	}

	private class LauncherAdListener extends AdListener {
		public void onAdClosed() {
			doLaunch();
		}
		public void onAdFailedToLoad(int error) {
			enableLaunchButton();
		}
		public void onAdLoaded() {
			enableLaunchButton();
		}
	}

}
