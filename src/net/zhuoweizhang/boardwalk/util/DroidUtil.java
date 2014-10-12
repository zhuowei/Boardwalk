package net.zhuoweizhang.boardwalk.util;

import android.app.*;
import android.content.*;
import android.net.*;

public class DroidUtil {

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnected();
	}
}
