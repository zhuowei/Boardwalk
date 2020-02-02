package net.zhuoweizhang.boardwalk.potato;

import java.io.*;
import android.content.*;
import android.content.res.AssetManager;
import android.os.Environment;
import net.zhuoweizhang.boardwalk.util.*;
import org.apache.commons.compress.archivers.examples.Expander;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

public class ExtractRuntime implements Runnable {
	private Context context;
	private File runtimeDir;
	private File tempDir;
	public static final String VERSION_FLAG_NAME = "version2";
	public ExtractRuntime(Context context) {
		this.context = context;
		this.runtimeDir = context.getDir("runtime", 0);
		this.tempDir = new File(runtimeDir, "extract_tmp");
	}

	private void extractAsset(String name) throws IOException {
		File outf = new File(runtimeDir, name);
		AssetsUtil.extractFileFromAssets(context, name, outf);
		outf.setExecutable(true);
	}

	public void run() {
		try {
			File versionFile = new File(runtimeDir, VERSION_FLAG_NAME);
			versionFile.delete();
			extractTar("jre.tar.xz", new File(runtimeDir, "jvm").getAbsolutePath());
			extractAsset("lwjgl_override.jar");
			extractTar("lwjgl3.tar.xz", new File(runtimeDir, "lwjgl3").getAbsolutePath());
/* FIXME!
			extractAsset("libboardwalk_preload.so");
			extractAsset("liblwjgl.so");
			extractAsset("libGLESv1_CM.so");
			extractAsset("libglshim.so");
			extractAsset("libgcc_s.so.1");
			extractAsset("lwjgl.jar");
			extractAsset("lwjgl_util.jar");
			extractAsset("librarylwjglopenal-20100824.jar");
*/
			versionFile.createNewFile();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void extractExtras() {
		/*
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}

	public void extractTar(String tar, String out) throws Exception {
		File outFile = new File(out);
		outFile.mkdirs();
		IoUtil.clearDirectory(outFile);

		AssetManager assets = context.getAssets();
		InputStream is = null;
		try {
			is = assets.open(tar);
			TarArchiveInputStream tarIs = new TarArchiveInputStream(
					new XZCompressorInputStream(new BufferedInputStream(is)));

			new Expander().expand(tarIs, outFile);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}
}
