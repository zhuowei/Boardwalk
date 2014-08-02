package net.zhuoweizhang.boardwalk;

import java.io.*;
import java.util.*;

import android.content.*;
import android.os.*;

import dalvik.system.DexClassLoader;

import net.zhuoweizhang.boardwalk.downloader.*;
import net.zhuoweizhang.boardwalk.model.*;
import net.zhuoweizhang.boardwalk.util.*;

public class LaunchMinecraftTask extends AsyncTask<Void, String, Void> {

	public static final int MY_VERSION = 1;

	private Context context;
	private boolean forceDex;
	private Listener listener;

	public LaunchMinecraftTask(Context context, Listener listener) {
		this.context = context;
		this.listener = listener;
	}

	public Void doInBackground(Void... params) {
		// first, we extract our own dex files.
		// second, we fetch the login lib, rename it, load it, and use it to auth.
		// third, we take the version selected, download the version manifest, and use it to download libraries
		// finally we use it to download Minecraft itself, and create a full dex.
		// we return with the dex path to load, as well as the command line arguments to run MainActivity with.
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
		try {
			setupWorkingDir(context);
			forceDex = getLauncherDirVersion() != MY_VERSION || new File("/sdcard/boardwalk/dexme").exists();
			// todo login
			MinecraftVersion version = getMinecraftVersion("1.7.10");
			if (forceDex || !MinecraftLaunch.canUseExistingDexPack(version)) {
				populateWorkingDir();
				downloadLibraries(version);
				downloadMinecraft(version);
				dexOptMinecraft(version);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		publishProgress("Populating working directory");
		File dexLibDir = MinecraftLaunch.dexDir;
		if (forceDex) {
			IoUtil.clearDirectory(dexLibDir);
			AssetsUtil.extractDirFromAssets(context, "dexed_libraries", dexLibDir);
			AssetsUtil.extractFileFromAssets(context, "jarjarrules.txt", new File(MinecraftLaunch.launcherDir,
				"jarjarrules.txt"));
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
					publishProgress("Downloading " + library.name);
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
			publishProgress("Converting " + library.name);
			File outputPath = LibrariesRepository.getDexTargetPath(parts[0], parts[1], parts[2]);
			MinecraftLaunch.runConvert(localPath, outputPath, MinecraftLaunch.libsToRename.contains(parts[1]));
		}
	}

	private void downloadMinecraft(MinecraftVersion version) throws Exception {
		File minecraftJar = MinecraftDownloader.getMinecraftVersionFile(version);
		if (!minecraftJar.exists()) {
			publishProgress("Downloading Minecraft " + version.id);
			MinecraftDownloader.downloadMinecraftVersion(version);
		}
		if (forceDex || !MinecraftLaunch.canUseExistingDexPack(version)) {
			publishProgress("Converting Minecraft " + version.id);
			MinecraftLaunch.createDexPack(version);
		}
	}

	private void dexOptMinecraft(MinecraftVersion version) {
		publishProgress("Preparing to load Minecraft");
		File optDir = context.getDir("dalvik-cache", 0);
		optDir.mkdirs();
		DexClassLoader classLoader = new DexClassLoader(MinecraftLaunch.getClassPath(version), 
			optDir.getAbsolutePath(), "", this.getClass().getClassLoader());
	}

	protected void onProgressUpdate(String... progress) {
		listener.onProgressUpdate(progress[0]);
	}

	protected void onPostExecute(Void result) {
		context.startActivity(new Intent(context, MainActivity.class));
	}

	public static interface Listener {
		public void onProgressUpdate(String s);
	}

}
