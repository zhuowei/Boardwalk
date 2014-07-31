package net.zhuoweizhang.boardwalk;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class LauncherActivity extends Activity implements View.OnClickListener, LaunchMinecraftTask.Listener {

	private TextView usernameText, passwordText;
	private Button loginButton;
	private TextView progressText;
	private ProgressBar progressBar;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.launcher_layout);
		loginButton = (Button) findViewById(R.id.launcher_login_button);
		usernameText = (TextView) findViewById(R.id.launcher_username_text);
		passwordText = (TextView) findViewById(R.id.launcher_password_text);
		progressText = (TextView) findViewById(R.id.launcher_progress_text);
		progressBar = (ProgressBar) findViewById(R.id.launcher_progress_bar);
		loginButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v == loginButton) {
			doLogin();
		}
	}

	public void doLogin() {
		progressBar.setVisibility(View.VISIBLE);
		new LaunchMinecraftTask(this, this).execute();
	}

	public void onProgressUpdate(String s) {
		progressText.setText(s);
	}
}
