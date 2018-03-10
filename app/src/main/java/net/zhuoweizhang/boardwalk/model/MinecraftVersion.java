package net.zhuoweizhang.boardwalk.model;
import java.util.*;
public class MinecraftVersion extends MinecraftVersionList.Version {
	public MinecraftArguments arguments;
	public String minecraftArguments;
	public DependentLibrary[] libraries;
	public String mainClass;
	public int minimumLauncherVersion;
	public String assets;
	public String inheritsFrom;
	public String jar;
}
