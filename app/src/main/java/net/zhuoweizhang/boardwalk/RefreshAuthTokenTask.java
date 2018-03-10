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

public class RefreshAuthTokenTask extends AsyncTask<String, Void, String> {
	private LauncherActivity activity;
	private YggdrasilAuthenticator authenticator = new YggdrasilAuthenticator();
	private Gson gson = new Gson();
	public RefreshAuthTokenTask(LauncherActivity activity) {
		this.activity = activity;
	}

	public void onPreExecute() {
	}

	public String doInBackground(String... args) {
		try {
			SharedPreferences prefs = activity.getSharedPreferences("launcher_prefs", 0);
			String clientId = prefs.getString("auth_clientId", prefs.getString("auth_clientToken", null));
			RefreshResponse response = authenticator.refresh(prefs.getString("auth_accessToken", null),
				UUID.fromString(clientId));
			if (response == null) return "Response is null?";
			if (response.selectedProfile == null) return activity.getResources().getString(R.string.login_is_demo_account);
			prefs.edit().
				putString("auth_clientId", response.clientToken.toString()).
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
		if (result == null) {
			// success
			activity.refreshedToken = true;
		} else {
			activity.refreshedToken = false;
			activity.progressText.setText(activity.getResources().getString(R.string.login_error) + " " + result);
		}
		activity.updateUiWithLoginStatus();
	}
}
