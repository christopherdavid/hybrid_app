package com.neatorobotics.android.slide.framework.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class DeviceUtils {

    public static String getDeviceMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            return null;
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getMacAddress();
        }

        return null;
    }

    public static String getDeviceName(Context context) {
        return android.os.Build.MODEL;
    }

    public static String getDeviceOperatingSystemVersion(Context context) {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getDeviceOperatingSystem(Context context) {
        return "ANDROID";
    }

    public static UserAttributes getUserAttributes(Context context) {
        UserAttributes userAttributes = new UserAttributes();
        userAttributes.deviceName = getDeviceName(context);
        userAttributes.osName = getDeviceOperatingSystem(context);
        userAttributes.osVersion = getDeviceOperatingSystemVersion(context);

        return userAttributes;
    }

    public static boolean isUdpBroadcastSupported() {
        return true;
    }
}
