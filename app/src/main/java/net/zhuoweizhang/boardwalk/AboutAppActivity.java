package net.zhuoweizhang.boardwalk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public final class AboutAppActivity extends Activity implements View.OnLongClickListener/*, View.OnClickListener*/ {

	public TextView appNameText;

	public TextView appVersionText;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		appNameText = (TextView) findViewById(R.id.about_appnametext);
		appNameText.setOnLongClickListener(this);
		appVersionText = (TextView) findViewById(R.id.about_appversiontext);
		String appVersion = "Top secret alpha pre-prerelease";
		try {
			appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		appVersionText.setText(appVersion);
	}

	public boolean onLongClick(View v) {
		if (v == appNameText) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("http://www.youtube.com/watch?v=2HQaBWziYvY"));
			startActivity(intent);
			return true;
		}
		return false;
	}
}
