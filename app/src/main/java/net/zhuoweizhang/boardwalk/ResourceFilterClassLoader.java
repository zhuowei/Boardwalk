package net.zhuoweizhang.boardwalk;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import dalvik.system.DexClassLoader;

public class ResourceFilterClassLoader extends DexClassLoader {
	public ResourceFilterClassLoader(String dexPath, String cachePath, String nativePath, ClassLoader delegate) {
		super(dexPath, cachePath, nativePath, delegate);
	}

	@Override
	protected URL findResource(String name) {
		return super.findResource(filterName(name));
	}

	@Override
	protected Enumeration<URL> findResources(String name) {
		return super.findResources(filterName(name));
	}

	private String filterName(String name) {
		System.out.println(name);
		if (name.charAt(0) == '/') {
			return name.substring(1);
		}
		return name;
	}
}
