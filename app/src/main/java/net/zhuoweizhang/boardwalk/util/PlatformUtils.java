package net.zhuoweizhang.boardwalk.util;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/* Must not have Android dependencies. Reflection is OK.
 * Stuff that links directly to Android stuff goes in DroidUtils
 */
public class PlatformUtils {
	/** @return amount of physical memory in bytes */
	public static long getTotalMemory() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("/proc/meminfo"));
			String tempLine = reader.readLine().trim();
			do {
				tempLine = tempLine.replaceAll("  ", " ");
			} while (tempLine.contains("  "));
			String[] theLine = tempLine.split(" ");
			if (!theLine[0].equals("MemTotal:")) return 0;
			return Long.parseLong(theLine[1]) * 1024;
		} catch (IOException e) {
			return 0;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException notanotherone){}
			}
		}
	}

	/** @return number of logical CPUs on this device */
	public static int getNumCores() {
		// http://stackoverflow.com/questions/10133570/availableprocessors-returns-1-for-dualcore-phones

		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				return Pattern.matches("cpu[0-9]+", pathname.getName());
			}
		};

		try {
			File dir = new File("/sys/devices/system/cpu/");
			return dir.listFiles(filter).length;
		} catch (Exception e) {
			return Math.max(1, Runtime.getRuntime().availableProcessors());
		}
	}

	public static int getAndroidVersion() {
		try {
			return Class.forName("android.os.Build$VERSION").getField("SDK_INT").getInt(null);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
