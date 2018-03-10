package net.zhuoweizhang.boardwalk.potato;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class InputEventSender implements Runnable {
	public Socket sock;
	public OutputStream os;
	public BlockingDeque<byte[]> deque = new LinkedBlockingDeque<byte[]>();
	public ServerSocket serverSock;
	private static final int CACHE_SIZE = 128;
	private static final int MESSAGE_SIZE = 24;
	private Deque<byte[]> cachedObjs = new ArrayDeque<byte[]>(CACHE_SIZE);
	public boolean running;
	public boolean grab = false;
	public int runServer() throws IOException {
		serverSock = new ServerSocket();
		serverSock.bind(new InetSocketAddress("127.0.0.1", 0));
		new Thread(this).start();
		return serverSock.getLocalPort();
	}

	public void run() {
		running = true;
		try {
			sock = serverSock.accept();
			serverSock.close();
			os = sock.getOutputStream();
			new Thread(new InputThread()).start();
			while (running) {
				byte[] event = deque.take();
				os.write(event);
				recycle(event);
			}
			sock.close();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (InterruptedException inte) {
			inte.printStackTrace();
		}
		System.out.println("Exiting input event sender");
	}

	private byte[] obtain() {
		byte[] msg = cachedObjs.poll();
		if (msg == null) msg = new byte[MESSAGE_SIZE];
		return msg;
	}

	private void recycle(byte[] msg) {
		if (cachedObjs.size() >= CACHE_SIZE) return;
		cachedObjs.add(msg);
	}

	private static void writeInt(byte[] b, int offset, int value) {
		b[offset++] = (byte) ((value >>> 24) & 0xff);
		b[offset++] = (byte) ((value >>> 16) & 0xff);
		b[offset++] = (byte) ((value >>> 8) & 0xff);
		b[offset++] = (byte) ((value) & 0xff);
	}

	public void putMouseEventWithCoords(byte button, byte down, int x, int y, int what, long time) {
		byte[] msg = obtain();
		msg[0] = (byte) 'p';
		msg[1] = button;
		msg[2] = down;
		writeInt(msg, 3, x);
		writeInt(msg, 7, y);
		writeInt(msg, 11, what);
		writeInt(msg, 15, (int) ((time >>> 32) & 0xffffffff));
		writeInt(msg, 19, (int) (time & 0xffffffff));
		deque.add(msg);
	}

	public void setMouseCoords(int x, int y) {
		byte[] msg = obtain();
		msg[0] = (byte) 'c';
		writeInt(msg, 1, x);
		writeInt(msg, 5, y);
		deque.add(msg);
	}

	public void setWindowSize(int x, int y) {
		byte[] msg = obtain();
		msg[0] = (byte) 'w';
		writeInt(msg, 1, x);
		writeInt(msg, 5, y);
		deque.add(msg);
	}

	public void setMouseButtonInGrabMode(byte button, byte status) {
		byte[] msg = obtain();
		msg[0] = (byte) 'g';
		msg[1] = button;
		msg[2] = status;
		deque.add(msg);
	}

	public void setKey(int keycode, char keyChar, boolean status) {
		byte[] msg = obtain();
		msg[0] = (byte) 'k';
		writeInt(msg, 1, keycode);
		writeInt(msg, 5, keyChar);
		msg[9] = status? (byte) 1: (byte) 0;
		deque.add(msg);
	}

	private class InputThread implements Runnable {
		public void run() {
			try {
				InputStream is = sock.getInputStream();
				while(running) {
					int cmd = is.read();
					switch (cmd) {
						case 'g':
							grab = false;
							break;
						case 'G':
							grab = true;
							break;
						default:
							running = false;
							break;
					}
				}
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
	}

}
