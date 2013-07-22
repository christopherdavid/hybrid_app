package com.neatorobotics.android.slide.framework.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetworkConnectionUtils {

	public static boolean isConnectedOverWiFi(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiInfo == null) {
			return false;
		}
		State wifiState = wifiInfo.getState();
		return ((wifiState == NetworkInfo.State.CONNECTED) || (wifiState == NetworkInfo.State.CONNECTING));
	}

	public static boolean isUsingMobileData(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		if (mobileInfo == null) {
			return false;
		}
		
		State mobileState = mobileInfo.getState();
		return ((mobileState == NetworkInfo.State.CONNECTED) || (mobileState == NetworkInfo.State.CONNECTING));
	}

	public static boolean hasNetworkConnection(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
		if (networkInfo == null) {
			return false;
		}
		
		return (networkInfo.isAvailable()) && (networkInfo.isConnected());
	}

}
