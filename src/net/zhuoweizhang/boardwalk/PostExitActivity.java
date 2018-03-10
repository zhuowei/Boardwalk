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
	public static boolean doLaunch = false;

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// this is probably a serious abuse of the Android activity lifecycle, but:
		// LauncherActivity sets doLaunch to true when launching PostExitActivity
		// in this mode we chain to MainActivity
		// when MainActivity dies, doLaunch is no longer true in the new JVM created after death
		// so we show an ad instead
		if (doLaunch) {
			launch();
			doLaunch = false;
		} else {
			loadAd();
		}
	}

	private void loadAd() {
		progressBar = new ProgressBar(this);
		progressBar.setIndeterminate(true);
		setContentView(progressBar, new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-2652482030334356/5468611825");
		AdRequest adRequest = AdUtils.addTestDevices(new AdRequest.Builder())
			.build();
		interstitial.setAdListener(new PostExitAdListener());
		interstitial.loadAd(adRequest);
	}

	private void launch() {
		startActivityForResult(new Intent(this, MainActivity.class), 1234);
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
