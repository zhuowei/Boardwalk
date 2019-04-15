package net.zhuoweizhang.boardwalk;

import java.io.*;
import java.util.*;

/* a repository of local Jars. */
public class LibrariesRepository {
	public static final String MOJANG_MAVEN_REPO = "";

	public static File[] localDirs, localDexDirs;
	public static File downloadDir, dexOutputDir;
	public static List<String> builtInLibs = Arrays.asList("lwjgl", "lwjgl_util",
		/* LWJGL 3 */"lwjgl-glfw", "lwjgl-jemalloc", "lwjgl-openal", "lwjgl-opengl", "lwjgl-stb");

	public static void setLocalLibPath(File[] locals, File downloadDir) {
		localDirs = locals;
		LibrariesRepository.downloadDir = downloadDir;
	}

	public static void setLocalDexPath(File[] locals, File outputDir) {
		localDexDirs = locals;
		LibrariesRepository.dexOutputDir = outputDir;
	}

	public static boolean needsDownload(String group, String artifact, String version) {
		if (isBuiltInToLauncher(group, artifact, version)) return false;
		if (getLocalPath(group, artifact, version) != null) return false;
		if (isBlackListedLibrary(group, artifact, version)) return false;
		return true;
	}

	public static String artifactToPath(String group, String artifact, String version) {
		String path = group.replaceAll("\\.", "/") + "/" + artifact + "/" + version + "/" + artifact + "-" + version + ".jar";
		return path;
	}

	public static boolean isBuiltInToLauncher(String group, String artifact, String version) {
		return builtInLibs.contains(artifact);
	}

	public static boolean isBlackListedLibrary(String group, String artifact, String version) {
		return artifact.endsWith("-platform");
	}

	public static File getLocalPath(String group, String artifact, String version) {
		String artifactPath = artifactToPath(group, artifact, version);
		for (File dir: localDirs) {
			File localPath = new File(dir, artifactPath);
			if (localPath.exists()) return localPath;
		}
		return null;
	}

	public static File getDexLocalPath(String group, String artifact, String version) {
		String artifactPath = artifactToPath(group, artifact, version);
		for (File dir: localDexDirs) {
			File localPath = new File(dir, artifactPath);
			if (localPath.exists()) return localPath;
		}
		return null;
	}

	public static File getDownloadTargetPath(String group, String artifact, String version) {
		return new File(downloadDir, artifactToPath(group, artifact, version));
	}

	public static File getDexTargetPath(String group, String artifact, String version) {
		return new File(dexOutputDir, artifactToPath(group, artifact, version));
	}
}
