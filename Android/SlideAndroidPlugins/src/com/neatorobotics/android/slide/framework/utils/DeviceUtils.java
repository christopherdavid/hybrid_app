package com.neatorobotics.android.slide.framework.utils;

import android.content.Context;

public class DeviceUtils {

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
}
