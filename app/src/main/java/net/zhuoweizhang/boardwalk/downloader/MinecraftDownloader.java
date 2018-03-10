package net.zhuoweizhang.boardwalk.downloader;

import java.io.*;
import java.nio.charset.Charset;

import com.google.gson.*;
import net.zhuoweizhang.boardwalk.*;
import net.zhuoweizhang.boardwalk.model.*;

public class MinecraftDownloader {

	public static final String MINECRAFT_ASSETS = "http://s3.amazonaws.com/Minecraft.Download/";

	public static boolean useMavenCentral = false;

	public static Gson gson = new Gson();

	public static File versionsDir;

	public static MinecraftVersionList downloadVersionList() throws IOException {
		String versions = DownloadUtils.downloadString(MINECRAFT_ASSETS + "versions/versions.json");
		MinecraftVersionList list = gson.fromJson(versions, MinecraftVersionList.class);
		return list;
	}

	public static MinecraftVersion downloadVersionInfo(String versionName) throws IOException {
		String versionJson = DownloadUtils.downloadString(MINECRAFT_ASSETS + "versions/" + versionName + "/" + versionName + ".json");
		MinecraftVersion version = gson.fromJson(versionJson, MinecraftVersion.class);
		String pathName = version.id + "/" + version.id + ".json";
		File versionFile = new File(versionsDir, pathName);
		versionFile.getParentFile().mkdirs();
		FileOutputStream os = new FileOutputStream(versionFile);
		os.write(versionJson.getBytes(Charset.forName("UTF-8")));
		os.close();
		return version;
	}

	public static void downloadLibraries(MinecraftVersion version) throws IOException {
		for (DependentLibrary library: version.libraries) {
			String[] parts = library.name.split(":");
			if (LibrariesRepository.needsDownload(parts[0], parts[1], parts[2])) {
				downloadLibrary(parts[0], parts[1], parts[2]);
			}
		}
	}

	public static void downloadLibrary(DependentLibrary library) throws IOException {
		// TODO: parse path
		String[] parts = library.name.split(":");
		downloadLibrary(parts[0], parts[1], parts[2]);
	}

	public static void downloadLibrary(String group, String artifact, String version) throws IOException {
		File outputFile = LibrariesRepository.getDownloadTargetPath(group, artifact, version);
		String url = getLibraryDownloadUrl(group, artifact, version);
		DownloadUtils.downloadFile(url, outputFile);
	}

	public static String getLibraryDownloadUrl(String group, String artifact, String version) {
		String path = LibrariesRepository.artifactToPath(group, artifact, version);
		if (useMavenCentral) {
			return "https://repo1.maven.org/maven2/" + path;
		}
		return "https://libraries.minecraft.net/" + path;
	}

	public static File getMinecraftVersionFile(MinecraftVersion version) {
		String pathName = version.id + "/" + version.id + ".jar";
		return new File(versionsDir, pathName);
	}

	public static void downloadMinecraftVersion(MinecraftVersion version) throws IOException {
		String pathName = version.id + "/" + version.id + ".jar";
		DownloadUtils.downloadFile(MINECRAFT_ASSETS + "versions/" + pathName, new File(versionsDir, pathName));
	}

	public static MinecraftVersion getVersionInfo(String versionName) throws IOException {
		String pathName = versionName + "/" + versionName + ".json";
		File versionFile = new File(versionsDir, pathName);
		if (!versionFile.exists()) return downloadVersionInfo(versionName);
		byte[] versionDat = new byte[(int) versionFile.length()];
		FileInputStream is = new FileInputStream(versionFile);
		is.read(versionDat);
		is.close();
		String versionJson = new String(versionDat, Charset.forName("UTF-8"));
		MinecraftVersion version = gson.fromJson(versionJson, MinecraftVersion.class);
		return version;
	}
}
