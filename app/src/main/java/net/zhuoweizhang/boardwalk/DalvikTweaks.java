package net.zhuoweizhang.boardwalk;

public class DalvikTweaks {

	public static void setDefaultStackSize(int size) {
		nativeSetDefaultStackSize(size, android.os.Build.VERSION.SDK_INT);
	}
	public static native void nativeSetDefaultStackSize(int size, int androidBuild);
	public static void setHeapMaxFree(long size) {
		nativeSetHeapMaxFree(size, android.os.Build.VERSION.SDK_INT);
	}
	public static native void nativeSetHeapMaxFree(long size, int androidBuild);

	public static void setHeapMinFree(long size) {
		nativeSetHeapMinFree(size, android.os.Build.VERSION.SDK_INT);
	}
	public static native void nativeSetHeapMinFree(long size, int androidBuild);

	public static native void crashTheLogger();

	public static boolean isDalvik() {
		String version = System.getProperty("java.vm.version");
		System.out.println(version);
		if (version == null) return true;
		String[] versionParts = version.split("\\.");
		return Integer.parseInt(versionParts[0]) == 1;
	}

	public static native void setenv(String name, String value, boolean override);

	static {
		System.loadLibrary("boardwalk");
	}

}
