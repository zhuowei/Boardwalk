package net.zhuoweizhang.boardwalk;

import java.io.*;
import java.util.*;

import android.content.*;
import android.os.*;

import dalvik.system.DexClassLoader;

import net.zhuoweizhang.boardwalk.downloader.*;
import net.zhuoweizhang.boardwalk.model.*;
import net.zhuoweizhang.boardwalk.util.*;

public class LaunchMinecraftTask extends AsyncTask<Void, String, String> {

	public static final int MY_VERSION = 9;

	private Context context;
	private boolean forceDex;
	private Listener listener;
	private Thread assetsThread;

	public LaunchMinecraftTask(Context context, Listener listener) {
		this.context = context;
		this.listener = listener;
	}

	public String doInBackground(Void... params) {
		// first, we extract our own dex files.
		// second, we fetch the login lib, rename it, load it, and use it to auth.
		// third, we take the version selected, download the version manifest, and use it to download libraries
		// finally we use it to download Minecraft itself, and create a full dex.
		// we return with the dex path to load, as well as the command line arguments to run MainActivity with.
		//android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
		try {
			setupWorkingDir(context);
			forceDex = getLauncherDirVersion() != MY_VERSION || new File("/sdcard/boardwalk/dexme").exists();
			String selectedVersion = context.getSharedPreferences("launcher_prefs", 0).
				getString("selected_version", MainActivity.VERSION_TO_LAUNCH);
			MinecraftVersion version = getMinecraftVersion(selectedVersion);
			System.out.println("Can use existing dex pack: " + MinecraftLaunch.canUseExistingDexPack(version));
			startAssetsDownload(version);
			if (forceDex || !MinecraftLaunch.canUseExistingDexPack(version)) {
				populateWorkingDir();
				downloadLibraries(version);
				downloadMinecraft(version);
			}
			waitForAssetsDownload();
			listener.waitForExtras();
			extractDefaultOptions();
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
		System.gc();
		return null;
	}

	public static void setupWorkingDir(Context context) {
		File workingDir = new File(Environment.getExternalStorageDirectory(), "boardwalk/gamedir");
		workingDir.mkdirs();
		MinecraftLaunch.init(workingDir);
		MinecraftLaunch.javaVMCmd = Arrays.asList("dalvikvm", "-Djava.library.path=" + System.getProperty("java.library.path"),
			"-classpath", context.getPackageCodePath());
	}

	private int getLauncherDirVersion() {
		return context.getSharedPreferences("launcher_prefs", 0).getInt("working_dir_version", 0);
	}

	private void writeLauncherDirVersion() {
		context.getSharedPreferences("launcher_prefs", 0).edit().putInt("working_dir_version", MY_VERSION).apply();
	}

	private void populateWorkingDir() throws IOException {
		publishProgress(context.getResources().getString(R.string.convert_populating_working_directory));
		File dexLibDir = MinecraftLaunch.dexDir;
		if (true) {
			writeLauncherDirVersion();
		}
	}

	private MinecraftVersion getMinecraftVersion(String name) throws IOException {
		return MinecraftDownloader.getVersionInfo(name);
	}

	private void downloadLibraries(MinecraftVersion version) throws Exception {
		for (DependentLibrary library: version.libraries) {
			String[] parts = library.name.split(":");
			if (LibrariesRepository.needsDownload(parts[0], parts[1], parts[2])) {
				try {
					publishProgress(context.getResources().getString(R.string.convert_downloading) + " " + library.name);
					MinecraftDownloader.downloadLibrary(library);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void downloadMinecraft(MinecraftVersion version) throws Exception {
		File minecraftJar = MinecraftDownloader.getMinecraftVersionFile(version);
		if (!minecraftJar.exists()) {
			publishProgress(context.getResources().getString(R.string.convert_downloading) + " Minecraft " + version.id);
			MinecraftDownloader.downloadMinecraftVersion(version);
		}
	}

	private void extractDefaultOptions() throws IOException {
		File boardwalkDir = new File(Environment.getExternalStorageDirectory(), "boardwalk");
		File gameDir = new File(boardwalkDir, "gamedir");
		gameDir.mkdirs();
		File optionsFile = new File(gameDir, "options.txt");
		if (!optionsFile.exists()) {
			AssetsUtil.extractFileFromAssets(context, "options.txt", optionsFile);
		}
		File nomediaFile = new File(boardwalkDir, ".nomedia");
		if (!nomediaFile.exists()) {
			nomediaFile.createNewFile();
		}
	}

	private void startAssetsDownload(MinecraftVersion version) {
		final String assetsVersion = version.assets;
		assetsThread = new Thread(new Runnable() {
			public void run() {
				try {
					MinecraftAssetsDownloader.downloadAssets(assetsVersion,
						new File(Environment.getExternalStorageDirectory(), "boardwalk/gamedir/assets"));
				} catch (IOException ie) {
					ie.printStackTrace();
				}
			}
		});
		assetsThread.start();
	}

	private void waitForAssetsDownload() {
		publishProgress("Waiting for sound and language files to download...");
		try {
			assetsThread.join();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

	protected void onProgressUpdate(String... progress) {
		listener.onProgressUpdate("Installing - this should take about 4 minutes. " +
			"Please don't leave this application during the install process. " + progress[0]);
	}

	protected void onPostExecute(String result) {
		if (result != null) {
			listener.onProgressUpdate("Device: " + Build.MANUFACTURER + " " + Build.MODEL + 
				" Android " + Build.VERSION.RELEASE + "\nError: " + result);
			listener.onLaunchError();
		} else {
			PostExitActivity.doLaunch = true;
			context.startActivity(new Intent(context, PostExitActivity.class));
		}
	}

	public static interface Listener {
		public void onProgressUpdate(String s);
		public void onLaunchError();
		public void waitForExtras();
	}

}
