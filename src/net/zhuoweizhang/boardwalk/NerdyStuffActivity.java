package net.zhuoweizhang.boardwalk;

import android.app.*;
import android.content.*;
import android.os.*;

import java.util.*;

public class NerdyStuffActivity extends Activity {
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		new Thread(new QueueBench()).start();
	}

	public static class Bench {
		public long lastMillis = 0;
		public void tic() {
			lastMillis = System.currentTimeMillis();
		}
		public void toc() {
			System.out.println("Elapsed time: " + (System.currentTimeMillis() - lastMillis));
		}
	}

	public static final class QueueBench extends Bench implements Runnable {

		public void run() {
			try {
				Thread.sleep(1000);
			} catch (Exception e){}
			System.out.println("Apache queue: ");
			Comparator<Integer> comp = new Comparator<Integer>() {
				public int compare(Integer a, Integer b) {
					return b.compareTo(a);
				}
			};
			tic();
			for (int i = 0; i < 100; i++) benchQueue(new java.util.PriorityQueue<Integer>(100000, comp));
			toc();
			System.out.println("OpenJDK queue: ");
			tic();
			for (int i = 0; i < 100; i++) benchQueue(new net.zhuoweizhang.qfactor.java.util.PriorityQueue<Integer>(100000, comp));
			toc();
		}

		public void benchQueue(Queue<Integer> queue) {
			Random rand = new Random("yoloswag".hashCode());
			for (int i = 0; i < 100000; i++) {
				queue.add(rand.nextInt());
			}
			for (int i = 0; i < 100000; i++) {
				queue.remove();
			}
		}

		public void benchQueue2(Queue<Integer> queue) {
			for (int i = 0; i < 100000; i++) {
				queue.add(i);
			}
			for (int i = 0; i < 100000; i++) {
				queue.remove();
			}
		}

	}
}
