package net.zhuoweizhang.boardwalk;

public class DalvikTweaks {

	public static native void setDefaultStackSize(int size);
	public static native void crashTheLogger();

	public static boolean isDalvik() {
		String version = System.getProperty("java.vm.version");
		System.out.println(version);
		if (version == null) return true;
		String[] versionParts = version.split("\\.");
		return Integer.parseInt(versionParts[0]) == 1;
	}

	static {
		System.loadLibrary("boardwalk");
	}

}
