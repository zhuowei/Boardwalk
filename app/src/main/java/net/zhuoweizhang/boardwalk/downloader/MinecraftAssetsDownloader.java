package net.zhuoweizhang.boardwalk.downloader;

import java.io.*;
import java.nio.charset.Charset;
import net.zhuoweizhang.boardwalk.model.*;

public class MinecraftAssetsDownloader {

	public static final String MINECRAFT_RES = "http://resources.download.minecraft.net/";

	public static MinecraftAssets downloadIndex(String versionName, File output) throws IOException {
		String versionJson = DownloadUtils.downloadString(MinecraftDownloader.MINECRAFT_ASSETS +
			"indexes/" + versionName + ".json");
		MinecraftAssets version = MinecraftDownloader.gson.fromJson(versionJson, MinecraftAssets.class);
		output.getParentFile().mkdirs();
		FileOutputStream os = new FileOutputStream(output);
		os.write(versionJson.getBytes(Charset.forName("UTF-8")));
		os.close();
		return version;
	}

	public static void downloadAsset(MinecraftAssetInfo asset, File objectsDir) throws IOException {
		String assetPath = asset.hash.substring(0, 2) + "/" + asset.hash;
		File outFile = new File(objectsDir, assetPath);
		if (outFile.exists()) return;
		DownloadUtils.downloadFile(MINECRAFT_RES + assetPath, outFile);
	}

	public static void downloadAssets(String assetsVersion, File outputDir) throws IOException {
		File hasDownloadedFile = new File(outputDir, "downloaded/" + assetsVersion + ".downloaded");
		if (hasDownloadedFile.exists()) return;
		System.out.println("Assets begin time: " + System.currentTimeMillis());
		// download index
		File indexFile = new File(outputDir, "indexes/" + assetsVersion + ".json");
		MinecraftAssets assets = downloadIndex(assetsVersion, indexFile);
		File objectsDir = new File(outputDir, "objects");
		for (MinecraftAssetInfo asset: assets.objects.values()) {
			downloadAsset(asset, objectsDir);
		}
		hasDownloadedFile.getParentFile().mkdirs();
		hasDownloadedFile.createNewFile();
		System.out.println("Assets end time: " + System.currentTimeMillis());
	}

	public static void main(String[] args) throws IOException {
		downloadAssets(args[0], new File(args[1]));
	}

}
