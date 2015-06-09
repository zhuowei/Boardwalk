package net.zhuoweizhang.boardwalk.potato;
import java.io.*;

public class LoadMe {
	public static native void potatoExec(byte[] auxv, String[] args);
	public static native void setenv(String name, String val);
	public static native void redirectStdio();
	public static native void setupBridge();
	public static native void setupBridgeEGL();
	public static native int chdir(String path);
	public static String runtimePath;

	public static byte[] readAuxV() throws IOException {
		FileInputStream fis = new FileInputStream(new File("/proc/self/auxv"));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[0x1000];
		int i;
		while((i = fis.read(buf)) != -1) {
			bos.write(buf, 0, i);
		}
		fis.close();
		return bos.toByteArray();
	}

	public static String ROOTFS_ARCH = "arm-linux-gnueabihf";
	public static void exec(String mcClassPath) {
		String ROOTFS_PATH = runtimePath + "/ubuntu";
		try {
			setenv("LD_LIBRARY_PATH", 
				runtimePath + "/newglibc/lib:" +
				runtimePath);
			//setenv("LD_DEBUG", "all");
			setenv("LD_PRELOAD", runtimePath + "/libboardwalk_preload.so");
			setenv("OVERRIDE_PROC_SELF_EXE", runtimePath + "/jvm/jdk1.8.0_33/jre/bin/java");

			setenv("LIBGL_MIPMAP", "3");
			setenv("HOME", "/sdcard/boardwalk");

			setupBridge();
			byte[] auxv = readAuxV();
			String[] newargs = {runtimePath + "/newglibc/lib/ld-linux-armhf.so.3",
				runtimePath + "/jvm/jdk1.8.0_33/jre/bin/java",
				"-server", "-Xms450M", "-Xmx450M",
				"-cp", "/sdcard/winprogress/potato/lwjgl/libs/lwjgl.jar:" +
				"/sdcard/winprogress/potato/lwjgl/libs/lwjgl_test.jar:" +
				"/sdcard/winprogress/potato/lwjgl/libs/lwjgl_util.jar:" +
				"/sdcard/winprogress/potato/librarylwjglopenal-20100824.jar:" +
				mcClassPath,
				"-Djava.library.path=" + runtimePath,
				"net.minecraft.client.main.Main",
				"--accessToken", "0", "--userProperties", "{}", "--version", "mcp",
				"--gameDir", "/sdcard/boardwalk/gamedir"};
			System.out.println("Preparing to exec");
			redirectStdio();
			chdir("/sdcard/boardwalk/gamedir");
			potatoExec(auxv, newargs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		exec(null);
	}
	static {
		System.loadLibrary("boardwalk_masterpotato");
	}
}
