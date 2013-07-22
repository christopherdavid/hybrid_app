package com.neatorobotics.android.slide.framework.udp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.DeviceUtils;

public class UdpUtils {

	private static final String TAG = UdpUtils.class.getSimpleName();
	
	// Public static helper method to return the 
	public static InetAddress  getBroadcastIp(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (!wifi.isWifiEnabled()) {
			return null;
		}
		
		DhcpInfo dhcp = wifi.getDhcpInfo();
		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++) {
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		}
		
		try {
			return InetAddress.getByAddress(quads);
		} 
		catch (UnknownHostException e) {
			LogHelper.log(TAG, "EXCEPTION in getBroadcastIp", e);
			return null;
		}
	}
	
	public static boolean isUdpBroadcastSupported() {
		return DeviceUtils.isUdpBroadcastSupported();
	}
}
