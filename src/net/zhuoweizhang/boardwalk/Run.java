package net.zhuoweizhang.boardwalk;
import java.lang.reflect.*;
import java.util.Arrays;
public class Run {

	public static void main(String[] args) throws Exception {
		dalvik.system.VMRuntime.getRuntime().startJitCompilation(); //DAE JIT?
		String[] args2 = Arrays.copyOfRange(args, 1, args.length);
		Class<?> clazz = Class.forName(args[0]);
		Method method = clazz.getMethod("main", String[].class);
		method.invoke(null, (Object) args2);
	}
}
