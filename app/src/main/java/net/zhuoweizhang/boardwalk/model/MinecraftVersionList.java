package net.zhuoweizhang.boardwalk.model;

import java.util.*;

public class MinecraftVersionList {

	public static final String TYPE_SNAPSHOT = "snapshot";
	public static final String TYPE_RELEASE = "release";
	public static final String TYPE_OLD_BETA = "old_beta";
	public static final String TYPE_OLD_ALPHA = "old_alpha";

	public static class Version {
		public String id;
		public String /*Date*/ time;
		public String /*Date*/ releaseTime;
		public String type;
	}

	public Map<String, String> latest;

	public Version[] versions;
}
