package net.zhuoweizhang.boardwalk;

import com.google.android.gms.ads.*;

public final class AdUtils {
	public static AdRequest.Builder addTestDevices(AdRequest.Builder builder) {
		return builder
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
			.addTestDevice(AdvertConstants.DEVICE_ID_TESTER)
			.addTestDevice(AdvertConstants.DEVICE_ID_TESTER_L)
			.addTestDevice(AdvertConstants.DEVICE_ID_TESTER_PHONE)
			.addTestDevice("D8F3383EC7C7875FADC84B5CA9C48CA4")
			.addTestDevice(AdvertConstants.DEVICE_ID_TESTER_N7)
			.addTestDevice("A5E5FAB2F157373440A9E226EA2BEA94");
	}
}
