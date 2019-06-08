package net.zhuoweizhang.boardwalk;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import net.zhuoweizhang.boardwalk.downloader.*;
import net.zhuoweizhang.boardwalk.model.*;

public class RefreshVersionListTask extends AsyncTask<String, Void, String> {
	private LauncherActivity activity;
	private MinecraftVersionList versionList;
	private List<String> versionStringList;
	public RefreshVersionListTask(LauncherActivity activity) {
		this.activity = activity;
	}

	public void onPreExecute() {
	}

	public String doInBackground(String... args) {
		try {
			versionList = MinecraftDownloader.downloadVersionList();
			versionStringList = new ArrayList<String>(versionList.versions.length);
			for (MinecraftVersionList.Version v: versionList.versions) {
				versionStringList.add(v.id);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	public void onPostExecute(String result) {
		if (result == null) {
			activity.addToVersionSpinner(versionStringList);
		}
	}
}
