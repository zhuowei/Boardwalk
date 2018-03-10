package net.zhuoweizhang.boardwalk;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;

import com.google.gson.*;

import net.zhuoweizhang.boardwalk.yggdrasil.*;
import net.zhuoweizhang.boardwalk.model.vanillalauncher.*;

public class ImportVanillaAuthTask extends AsyncTask<String, Void, String> {
	private LauncherActivity activity;
	private YggdrasilAuthenticator authenticator = new YggdrasilAuthenticator();
	private Gson gson = new Gson();
	public ImportVanillaAuthTask(LauncherActivity activity) {
		this.activity = activity;
	}

	public void onPreExecute() {
		activity.progressBar.setVisibility(View.VISIBLE);
		activity.loginButton.setEnabled(false);
		activity.progressText.setText(R.string.login_logging_in);
		SharedPreferences prefs = activity.getSharedPreferences("launcher_prefs", 0);
		//prefs.edit().putString("auth_lastEmail", activity.usernameText.getText().toString()).apply();
	}

	public String doInBackground(String... args) {
		try {
			LauncherProfiles theProfiles = getAuth(new File(args[0]));

			LauncherAuth auth = theProfiles.authenticationDatabase.get(theProfiles.selectedUser);

			RefreshResponse response = authenticator.refresh(auth.accessToken, theProfiles.clientToken);
			if (response == null) return "Response is null?";
			if (response.selectedProfile == null) return activity.getResources().getString(R.string.login_is_demo_account);
			SharedPreferences prefs = activity.getSharedPreferences("launcher_prefs", 0);
			prefs.edit().
				putString("auth_clientToken", response.clientToken.toString()).
				putString("auth_accessToken", response.accessToken).
				putString("auth_profile_name", response.selectedProfile.name).
				putString("auth_profile_id", response.selectedProfile.id).
				putBoolean("auth_importedCredentials", true).
				apply();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	private LauncherProfiles getAuth(File file) throws IOException {
		FileInputStream fis = null;
		byte[] fileContents = new byte[(int) file.length()];
		try {
			fis = new FileInputStream(file);
			fis.read(fileContents);
		} finally {
			if (fis != null) fis.close();
		}
		String theString = new String(fileContents, Charset.forName("UTF-8"));
		return gson.fromJson(theString, LauncherProfiles.class);
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
