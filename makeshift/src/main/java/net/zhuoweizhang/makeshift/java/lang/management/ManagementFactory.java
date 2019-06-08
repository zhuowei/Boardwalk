package net.zhuoweizhang.makeshift.java.lang.management;
import java.util.*;
public class ManagementFactory {
	private static class RuntimeMXBeanImpl implements RuntimeMXBean {
		public List<String> getInputArguments() {
			return Collections.emptyList();
		}
	}
	private static RuntimeMXBean runtimeMXBeanInstance = new RuntimeMXBeanImpl();
	public static RuntimeMXBean getRuntimeMXBean() {
		return runtimeMXBeanInstance;
	}
}
