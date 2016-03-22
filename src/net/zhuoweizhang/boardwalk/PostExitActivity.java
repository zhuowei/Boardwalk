package net.zhuoweizhang.boardwalk;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import com.google.android.gms.ads.*;

/**
 * an activity that launches after app exit.
 * Currently used to display ads.
*/

public class PostExitActivity extends Activity {

	private InterstitialAd interstitial;
	private ProgressBar progressBar;

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		progressBar = new ProgressBar(this);
		progressBar.setIndeterminate(true);
		setContentView(progressBar, new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-2652482030334356/5468611825");
		AdRequest adRequest = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
			.addTestDevice(AdvertConstants.DEVICE_ID_TESTER)
			.addTestDevice(AdvertConstants.DEVICE_ID_TESTER_L)
			.addTestDevice(AdvertConstants.DEVICE_ID_TESTER_NEW)
			.build();
		interstitial.setAdListener(new PostExitAdListener());
		interstitial.loadAd(adRequest);

	}

	private class PostExitAdListener extends AdListener {
		public void onAdFailedToLoad(int errorCode) {
			System.err.println("Ad load fail: " + errorCode);
			finish();
		}

		public void onAdLoaded() {
			interstitial.show();
			finish();
		}
	}
}
