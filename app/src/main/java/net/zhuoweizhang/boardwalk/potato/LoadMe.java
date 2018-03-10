package net.zhuoweizhang.boardwalk.potato;
import java.io.*;

public class LoadMe {
	public static native void potatoExec(byte[] auxv, String[] args);
	public static native void setenv(String name, String val);
	public static native void redirectStdio();
	public static native void setupBridge();
	public static native void setupBridgeEGL();
	public static native int chdir(String path);
	public static native int setupSigSys();
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
	public static void exec(String mcClassPath, String[] backArgs) {
		try {
			setenv("LD_LIBRARY_PATH", 
				runtimePath + "/newglibc/lib:" +
				runtimePath);
			//setenv("LD_DEBUG", "all");
			setenv("LD_PRELOAD", runtimePath + "/libboardwalk_preload.so:" +
				runtimePath + "/jvm/jdk1.8.0_33/jre/lib/arm/libjsig.so");
			setenv("OVERRIDE_PROC_SELF_EXE", runtimePath + "/jvm/jdk1.8.0_33/jre/bin/java");

			setenv("LIBGL_MIPMAP", "3");
			setenv("HOME", "/sdcard/boardwalk");

			setupBridge();
			byte[] auxv = readAuxV();
			String[] frontArgs = {runtimePath + "/newglibc/lib/ld-linux-armhf.so.3",
				runtimePath + "/jvm/jdk1.8.0_33/jre/bin/java",
				"-server", "-Xms450M", "-Xmx450M",
				"-cp", mcClassPath,
				"-Djava.library.path=" + runtimePath};
			String[] fullArgs = new String[frontArgs.length + backArgs.length];
			System.arraycopy(frontArgs, 0, fullArgs, 0, frontArgs.length);
			System.arraycopy(backArgs, 0, fullArgs, frontArgs.length, backArgs.length);
			// DEBUG
			//fullArgs = new String[] {"/data/data/net.zhuoweizhang.boardwalk/app_runtime/busybox"};
			// END DEBUG
			System.out.println("Preparing to exec");
			redirectStdio();
			if (android.os.Build.VERSION.SDK_INT >= 26) {
				// Android Oreo and up has seccomp; trap and emulate banned syscalls
				setupSigSys();
			}
			chdir("/sdcard/boardwalk/gamedir");
			potatoExec(auxv, fullArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static {
		System.loadLibrary("boardwalk_masterpotato");
	}
}
