package net.zhuoweizhang.boardwalk;

import java.util.*;

import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import net.zhuoweizhang.boardwalk.yggdrasil.*;

public class LoginTask extends AsyncTask<String, Void, String> {
	private LauncherActivity activity;
	private YggdrasilAuthenticator authenticator = new YggdrasilAuthenticator();
	public LoginTask(LauncherActivity activity) {
		this.activity = activity;
	}

	private UUID getClientId() {
		SharedPreferences prefs = activity.getSharedPreferences("launcher_prefs", 0);
		String out = prefs.getString("auth_clientId", null);
		boolean needsRegenUUID = prefs.getBoolean("auth_importedCredentials", false);
		UUID retval;
		if (out == null || needsRegenUUID) {
			retval = UUID.randomUUID();
			prefs.edit().putString("auth_clientId", retval.toString()).
				putBoolean("auth_importedCredentials", false).
				apply();
		} else {
			retval = UUID.fromString(out);
		}
		return retval;
	}

	public void onPreExecute() {
		activity.progressBar.setVisibility(View.VISIBLE);
		activity.loginButton.setEnabled(false);
		activity.importCredentialsButton.setEnabled(false);
		activity.progressText.setText(R.string.login_logging_in);
		SharedPreferences prefs = activity.getSharedPreferences("launcher_prefs", 0);
		//prefs.edit().putString("auth_lastEmail", activity.usernameText.getText().toString()).apply();
	}

	public String doInBackground(String... args) {
		try {
			AuthenticateResponse response = authenticator.authenticate(args[0], args[1], getClientId());
			if (response == null) return "Response is null?";
			if (response.selectedProfile == null) return activity.getResources().getString(R.string.login_is_demo_account);
			SharedPreferences prefs = activity.getSharedPreferences("launcher_prefs", 0);
			prefs.edit().
				putString("auth_accessToken", response.accessToken).
				putString("auth_profile_name", response.selectedProfile.name).
				putString("auth_profile_id", response.selectedProfile.id).
				apply();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	public void onPostExecute(String result) {
		activity.progressBar.setVisibility(View.GONE);
		activity.loginButton.setEnabled(true);
		if (result == null) {
			// success
			activity.progressText.setText("");
			activity.passwordText.setText(""); // clear the password textbox
		} else {
			activity.progressText.setText(activity.getResources().getString(R.string.login_error) + " " + result);
		}
		activity.updateUiWithLoginStatus();
	}
}
