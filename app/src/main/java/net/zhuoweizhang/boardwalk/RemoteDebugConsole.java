package net.zhuoweizhang.boardwalk;

import java.io.*;
import java.net.*;
import java.util.*;
import dalvik.system.VMRuntime;

public class RemoteDebugConsole implements Runnable {
	public ServerSocket serverSock;
	public void run() {
		while (true) {
			try {
				runOnce();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void runOnce() throws Exception {
		if (serverSock != null) serverSock.close();
		serverSock = new ServerSocket(15554);
		while (true) {
			Socket sock = serverSock.accept();
			InputStream is = sock.getInputStream();
			OutputStream os = sock.getOutputStream();
			Scanner scan = new Scanner(is);
			PrintStream out = new PrintStream(os);
			String theLine;
			while ((theLine = scan.nextLine()) != null) {
				String[] parts = theLine.split(" ");
				if (parts.length < 1) continue;
				String c = parts[0];
				if (c.equals("heapmaxfree")) {
					DalvikTweaks.setHeapMaxFree(parseM(parts[1]));
				} else if (c.equals("heapminfree")) {
					DalvikTweaks.setHeapMinFree(parseM(parts[1]));
				} else if (c.equals("utilset")) {
					VMRuntime.getRuntime().setTargetHeapUtilization(Float.parseFloat(parts[1]));
				} else if (c.equals("utilget")) {
					out.println(VMRuntime.getRuntime().getTargetHeapUtilization());
				} else if (c.equals("gc")) {
					System.gc();
				} else if (c.equals("alloc")) {
					byte[] theByte = new byte[(int) parseM(parts[1])];
				} else {
					out.println("?");
				}
			}
			sock.close();
		}
	}

	public static long parseM(String s) {
		int end = s.length() - 1;
		int mult;
		switch(s.charAt(end)) {
			case 'M':
			case 'm':
				mult = 1024*1024;
				break;
			case 'K':
			case 'k':
				mult = 1024;
				break;
			default:
				mult = 1;
				end++;
		}
		return Long.parseLong(s.substring(0, end)) * mult;
	}

	public static void start() {
		new Thread(new RemoteDebugConsole()).start();
	}
}
