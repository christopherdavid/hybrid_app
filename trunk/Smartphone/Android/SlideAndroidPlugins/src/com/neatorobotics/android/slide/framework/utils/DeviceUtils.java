package com.neatorobotics.android.slide.framework.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class DeviceUtils {
	
	public static String getDeviceMacAddress(Context context)
	{
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			return null;
		}
		
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo != null) {
			return wifiInfo.getMacAddress();
		}
		
		return null;
	}
	
}
