package net.zhuoweizhang.boardwalk;

import java.io.File;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import net.zhuoweizhang.boardwalk.util.PlatformUtils;

public class LauncherActivity extends Activity implements View.OnClickListener, LaunchMinecraftTask.Listener {

	public TextView usernameText, passwordText;
	public Button loginButton;
	public TextView progressText;
	public ProgressBar progressBar;
	public Button playButton;
	public TextView recommendationText;
	public boolean refreshedToken = true; // TODO false;

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
		updateUiWithLoginStatus();
		updateRecommendationText();
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences prefs = this.getSharedPreferences("launcher_prefs", 0);
		prefs.edit().putString("auth_lastEmail", usernameText.getText().toString()).apply();
	}

	@Override
	protected void onResume() {
		super.onResume();
		usernameText.setText(getSharedPreferences("launcher_prefs", 0).getString("auth_lastEmail", ""));
	}

	public void updateUiWithLoginStatus() {
		boolean loggedIn = isLoggedIn();
		usernameText.setEnabled(!loggedIn);
		passwordText.setVisibility(loggedIn? View.GONE: View.VISIBLE);
		playButton.setText(getResources().getText(loggedIn? (refreshedToken? R.string.play_regular : R.string.play_offline)
			: R.string.play_demo));
		loginButton.setVisibility(loggedIn? View.GONE: View.VISIBLE);
	}

	public void onClick(View v) {
		if (v == loginButton) {
			doLogin();
		} else if (v == playButton) {
			doLaunch();
		}
	}

	public void doLogin() {
		new LoginTask(this).execute(usernameText.getText().toString(), passwordText.getText().toString());
	}

	public void doLaunch() {
		progressBar.setVisibility(View.VISIBLE);
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
		if (PlatformUtils.getTotalMemory() < (900000L * 1024L)) { // 900MB
			builder.append(getResources().getText(R.string.recommendation_memory)).append("\n");
		}
		if (DalvikTweaks.isDalvik() && new File("/system/lib/libart.so").exists()) {
			builder.append(getResources().getText(R.string.recommendation_art)).append("\n");
		}
		recommendationText.setText(builder.toString());
	}
}
