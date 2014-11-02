package net.zhuoweizhang.boardwalk;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;

import net.zhuoweizhang.boardwalk.util.IoUtil;

public class ImportResourcePackTask extends AsyncTask<File, Void, String> {
	private LauncherActivity activity;
	public ImportResourcePackTask(LauncherActivity activity) {
		this.activity = activity;
	}

	public void onPreExecute() {
		activity.importResourcePackButton.setEnabled(false);
	}

	public String doInBackground(File... args) {
		try {
			File file = args[0];
			File outFile = new File("/sdcard/boardwalk/gamedir/resourcepacks", file.getName());
			byte[] buf = new byte[0x10000];
			IoUtil.copy(file, outFile, buf);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	public void onPostExecute(String result) {
		activity.importResourcePackButton.setEnabled(true);
		if (result == null) {
			// success
			activity.progressText.setText("Added your pack to the list of resource packs");
		} else {
			activity.progressText.setText("Unable to import resource pack: " + result);
		}
	}
}
