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

	public static final int MY_VERSION = 8;

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
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
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
				dexOptMinecraft(version);
			}
			waitForAssetsDownload();
			extractDefaultOptions();
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
		System.gc();
		return null;
	}

	public static void setupWorkingDir(Context context) {
		File workingDir = context.getDir("working_dir", 0);
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
			if (forceDex) IoUtil.clearDirectory(dexLibDir);
			AssetsUtil.extractDirFromAssets(context, "dexed_libraries", dexLibDir);
			AssetsUtil.extractFileFromAssets(context, "jarjarrules.txt", new File(MinecraftLaunch.launcherDir,
				"jarjarrules.txt"));
			AssetsUtil.extractFileFromAssets(context, "jarjarrules_minecraft.txt", new File(MinecraftLaunch.launcherDir,
				"jarjarrules_minecraft.txt"));
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
		for (DependentLibrary library: version.libraries) {
			String[] parts = library.name.split(":");
			File localPath = LibrariesRepository.getLocalPath(parts[0], parts[1], parts[2]);
			if (localPath == null) continue;
			File localDexPath = LibrariesRepository.getDexLocalPath(parts[0], parts[1], parts[2]);
			if (localDexPath != null) {
				continue;
			}
			publishProgress(context.getResources().getString(R.string.convert_converting) + " " + library.name);
			File outputPath = LibrariesRepository.getDexTargetPath(parts[0], parts[1], parts[2]);
			MinecraftLaunch.runConvert(localPath, outputPath, /*MinecraftLaunch.libsToRename.contains(parts[1])*/true, null);
		}
	}

	private void downloadMinecraft(MinecraftVersion version) throws Exception {
		File minecraftJar = MinecraftDownloader.getMinecraftVersionFile(version);
		if (!minecraftJar.exists()) {
			publishProgress(context.getResources().getString(R.string.convert_downloading) + " Minecraft " + version.id);
			MinecraftDownloader.downloadMinecraftVersion(version);
		}
		if (forceDex || !MinecraftLaunch.canUseExistingDexPack(version)) {
			publishProgress(context.getResources().getString(R.string.convert_converting) + " Minecraft " + version.id);
			MinecraftLaunch.createDexPack(version);
		}
	}

	private void dexOptMinecraft(MinecraftVersion version) {
		publishProgress(context.getResources().getString(R.string.convert_preparing_to_launch_minecraft));
		File optDir = context.getDir("dalvik-cache", 0);
		optDir.mkdirs();
		DexClassLoader classLoader = new DexClassLoader(MinecraftLaunch.getClassPath(version), 
			optDir.getAbsolutePath(), "", this.getClass().getClassLoader());
	}

	private void extractDefaultOptions() throws IOException {
		File boardwalkDir = new File("/sdcard/boardwalk");
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
					MinecraftAssetsDownloader.downloadAssets(assetsVersion, new File("/sdcard/boardwalk/gamedir/assets"));
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
			context.startActivity(new Intent(context, MainActivity.class));
		}
	}

	public static interface Listener {
		public void onProgressUpdate(String s);
		public void onLaunchError();
	}

}
