package net.zhuoweizhang.boardwalk;

import android.app.*;
import android.content.*;
import android.os.*;

import java.util.*;

public class NerdyStuffActivity extends Activity {
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
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
}
