package net.zhuoweizhang.boardwalk.downloader;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import net.zhuoweizhang.boardwalk.*;
import net.zhuoweizhang.boardwalk.util.*;

public class DownloadUtils {
	public static final String USER_AGENT = "Boardwalk";
	public static final Charset utf8 = Charset.forName("UTF-8");
	public static void download(String url, OutputStream os) throws IOException {
		URL urlObj;
		try {
			urlObj = new URL(url);
		} catch (MalformedURLException malformed) {
			throw new RuntimeException(malformed);
		}
		download(urlObj, os);
	}
	public static void download(URL url, OutputStream os) throws IOException {
		InputStream is = null;
		HttpURLConnection conn;
		byte[] buf = new byte[0x4000];

		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();

			IoUtil.pipe(is, os, buf);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
	}
	public static String downloadString(String url) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		download(url, bos);
		String out = new String(bos.toByteArray(), utf8);
		return out;
	}

	public static void downloadFile(String url, File out) throws IOException {
		out.getParentFile().mkdirs();
		File tempOut = File.createTempFile(out.getName(), ".part", out.getParentFile());
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(tempOut));
			download(url, bos);
			tempOut.renameTo(out);
		} finally {
			if (bos != null) {
				bos.close();
			}
			if (tempOut.exists()) {
				tempOut.delete();
			}
		}
	}
}
