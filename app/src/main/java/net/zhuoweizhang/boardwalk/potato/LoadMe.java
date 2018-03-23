package net.zhuoweizhang.boardwalk.potato;
import java.io.*;
import java.util.*;

import android.os.Environment;
import android.system.*;
import com.oracle.dalvik.VMLauncher;

public final class LoadMe {
	private LoadMe() {
	}

	public static native int chdir(String path);
	public static native void setupBridgeEGL();
	private static native boolean nativeDlopen(String path);
	public static String runtimePath;
	public static String internalNativeLibPath;
	private static List<String> propertyArgs = new ArrayList<String>();

	private static void redirectStdio() throws ErrnoException {
		File extStorage = Environment.getExternalStorageDirectory();
		File logFile = new File(extStorage, "boardwalk/log_boardwalk2.txt");

		FileDescriptor fd = Os.open(logFile.getAbsolutePath(),
			OsConstants.O_WRONLY | OsConstants.O_CREAT | OsConstants.O_TRUNC,
			0666);
		Os.dup2(fd, OsConstants.STDERR_FILENO);
		Os.dup2(fd, OsConstants.STDOUT_FILENO);
	}

	private static void dlopen(String path) {
		if (!nativeDlopen(path)) {
			throw new RuntimeException("Cannot load " + path);
		}
	}

	private static void loadLibraries(String javaHome) {
		// It's a pain to set LD_LIBRARY_PATH; pull libnet in by hand
		dlopen(new File(javaHome, "lib/jli/libjli.so").getAbsolutePath());
		dlopen(new File(javaHome, "lib/server/libjvm.so").getAbsolutePath());
		dlopen(new File(javaHome, "lib/libverify.so").getAbsolutePath());
		dlopen(new File(javaHome, "lib/libjava.so").getAbsolutePath());
		dlopen(new File(javaHome, "lib/libnet.so").getAbsolutePath());
	}

	public static void setProperty(String propertyName, String propertyValue) {
		propertyArgs.add("-Dboardwalk." + propertyName + "=" + propertyValue);
	}

	public static void exec(String mcClassPath, String[] backArgs) {
		try {
			Thread.currentThread().setName("BoardwalkMain");
			Os.setenv("LIBGL_MIPMAP", "3", true);

			String javaHome = runtimePath + "/jvm";

			// todo: LWJGL 3 vs 2 path

			String[] frontArgs = {"java",
				"-Djava.home=" + javaHome,
				"-Xms450M", "-Xmx450M",
				"-classpath", mcClassPath,
				"-Djava.library.path=" + runtimePath + ":" + internalNativeLibPath,
				"-Dos.name=Linux", "-Dorg.lwjgl.util.Debug=true",
				"-Dorg.lwjgl.opengl.libname=libglshim.so"};
			String[] propertyArgsArr = propertyArgs.toArray(new String[propertyArgs.size()]);
			String[] fullArgs = new String[frontArgs.length + propertyArgsArr.length + backArgs.length];
			System.arraycopy(frontArgs, 0, fullArgs, 0, frontArgs.length);
			System.arraycopy(propertyArgsArr, 0, fullArgs, frontArgs.length, propertyArgsArr.length);
			System.arraycopy(backArgs, 0, fullArgs, frontArgs.length + propertyArgsArr.length, backArgs.length);
			redirectStdio();
			chdir("/sdcard/boardwalk/gamedir");
			loadLibraries(javaHome);
			PrintWriter print = new PrintWriter(new File("/sdcard/boardwalk/jvmargs"));
			print.println(Arrays.toString(fullArgs));
			print.close();
			VMLauncher.launchJVM(fullArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static {
		System.loadLibrary("boardwalk2_jni");
	}
}
