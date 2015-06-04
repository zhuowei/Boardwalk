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

	public static String mcClassPath = "/sdcard/boardwalk/gamedir2/libraries/oshi-project/oshi-core/1.1/oshi-core-1.1.jar:/sdcard/boardwalk/gamedir2/libraries/net/java/dev/jna/jna/3.4.0/jna-3.4.0.jar:/sdcard/boardwalk/gamedir2/libraries/net/java/dev/jna/platform/3.4.0/platform-3.4.0.jar:/sdcard/boardwalk/gamedir2/libraries/com/ibm/icu/icu4j-core-mojang/51.2/icu4j-core-mojang-51.2.jar:/sdcard/boardwalk/gamedir2/libraries/net/sf/jopt-simple/jopt-simple/4.6/jopt-simple-4.6.jar:/sdcard/boardwalk/gamedir2/libraries/com/paulscode/codecjorbis/20101023/codecjorbis-20101023.jar:/sdcard/boardwalk/gamedir2/libraries/com/paulscode/codecwav/20101023/codecwav-20101023.jar:/sdcard/boardwalk/gamedir2/libraries/com/paulscode/libraryjavasound/20101123/libraryjavasound-20101123.jar:" + 
/*/sdcard/boardwalk/gamedir2/libraries/com/paulscode/librarylwjglopenal/20100824/librarylwjglopenal-20100824.jar:*/
"/sdcard/winprogress/potato/librarylwjglopenal-20100824.jar:/sdcard/boardwalk/gamedir2/libraries/com/paulscode/soundsystem/20120107/soundsystem-20120107.jar:/sdcard/boardwalk/gamedir2/libraries/io/netty/netty-all/4.0.23.Final/netty-all-4.0.23.Final.jar:/sdcard/boardwalk/gamedir2/libraries/com/google/guava/guava/17.0/guava-17.0.jar:/sdcard/boardwalk/gamedir2/libraries/org/apache/commons/commons-lang3/3.3.2/commons-lang3-3.3.2.jar:/sdcard/boardwalk/gamedir2/libraries/commons-io/commons-io/2.4/commons-io-2.4.jar:/sdcard/boardwalk/gamedir2/libraries/commons-codec/commons-codec/1.9/commons-codec-1.9.jar:/sdcard/boardwalk/gamedir2/libraries/net/java/jinput/jinput/2.0.5/jinput-2.0.5.jar:/sdcard/boardwalk/gamedir2/libraries/net/java/jutils/jutils/1.0.0/jutils-1.0.0.jar:/sdcard/boardwalk/gamedir2/libraries/com/google/code/gson/gson/2.2.4/gson-2.2.4.jar:/sdcard/boardwalk/gamedir2/libraries/com/mojang/authlib/1.5.21/authlib-1.5.21.jar:/sdcard/boardwalk/gamedir2/libraries/com/mojang/realms/1.7.19/realms-1.7.19.jar:/sdcard/boardwalk/gamedir2/libraries/org/apache/commons/commons-compress/1.8.1/commons-compress-1.8.1.jar:/sdcard/boardwalk/gamedir2/libraries/org/apache/httpcomponents/httpclient/4.3.3/httpclient-4.3.3.jar:/sdcard/boardwalk/gamedir2/libraries/commons-logging/commons-logging/1.1.3/commons-logging-1.1.3.jar:/sdcard/boardwalk/gamedir2/libraries/org/apache/httpcomponents/httpcore/4.3.2/httpcore-4.3.2.jar:/sdcard/boardwalk/gamedir2/libraries/org/apache/logging/log4j/log4j-api/2.0-beta9/log4j-api-2.0-beta9.jar:/sdcard/boardwalk/gamedir2/libraries/org/apache/logging/log4j/log4j-core/2.0-beta9/log4j-core-2.0-beta9.jar:" +
 /*/sdcard/boardwalk/gamedir2/libraries/org/lwjgl/lwjgl/lwjgl/2.9.4-nightly-20150209/lwjgl-2.9.4-nightly-20150209.jar:/sdcard/boardwalk/gamedir2/libraries/org/lwjgl/lwjgl/lwjgl_util/2.9.4-nightly-20150209/lwjgl_util-2.9.4-nightly-20150209.jar:*/
"/sdcard/boardwalk/gamedir2/libraries/tv/twitch/twitch/6.5/twitch-6.5.jar:/sdcard/boardwalk/gamedir2/versions/1.8.6/1.8.6.jar";;

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
	public static void exec(String[] args) {
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
				"-server", "-cp", "/sdcard/winprogress/potato/lwjgl/libs/lwjgl.jar:" +
				"/sdcard/winprogress/potato/lwjgl/libs/lwjgl_test.jar:" +
				"/sdcard/winprogress/potato/lwjgl/libs/lwjgl_util.jar:" +
				mcClassPath,
				"-Djava.library.path=" + runtimePath,
				"net.minecraft.client.main.Main",
				"--accessToken", "0", "--userProperties", "{}", "--version", "mcp",
				"--gameDir", "/sdcard/boardwalk/gamedir2"};
			System.out.println("Preparing to exec");
			redirectStdio();
			chdir("/sdcard/boardwalk/gamedir2");
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
