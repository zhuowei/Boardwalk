package net.zhuoweizhang.boardwalk.util;

import java.io.*;
import android.content.*;
import android.content.res.*;

public class AssetsUtil {

	public static void extractDirFromAssets(Context context, String path, File outputDir) throws IOException {
		AssetManager assets = context.getAssets();
		byte[] buffer = new byte[0x4000];
		String[] entries = assets.list(path);
		extractDirImpl(assets, path, entries, outputDir, buffer);
	}
	private static void extractDirImpl(AssetManager assets, String path, String[] entries, File outputDir, byte[] buffer)
		throws IOException {
		for (String s: entries) {
			System.out.println(s);
			String[] subentries = assets.list(path + "/" + s);
			if (subentries.length >= 1) {
				extractDirImpl(assets, path + "/" + s, subentries, new File(outputDir, s), buffer);
				continue;
			}
			OutputStream os = null;
			InputStream is = null;
			File output = new File(outputDir, s);
			output.getParentFile().mkdirs();
			try {
				is = assets.open(path + "/" +  s);
				os = new FileOutputStream(output);
				IoUtil.pipe(is, os, buffer);
			} finally {
				if (is != null) is.close();
				if (os != null) os.close();
			}
		}
	}

	public static void extractFileFromAssets(Context context, String path, File outputFile) throws IOException {
		AssetManager assets = context.getAssets();
		byte[] buffer = new byte[0x2000];
		InputStream is = assets.open(path);
		outputFile.getParentFile().mkdirs();
		OutputStream os = new FileOutputStream(outputFile);
		IoUtil.pipe(is, os, buffer);
		is.close();
		os.close();
	}
}
