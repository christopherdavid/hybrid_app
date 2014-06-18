package com.neatorobotics.android.slide.framework.model;

import android.text.TextUtils;

public class RobotNetworkInfo {
    public String robotSsid;
    public String robotIpAddress;
    public String robotDirectConnectSecret;

    public boolean isValid() {
        return (!(TextUtils.isEmpty(robotIpAddress) || TextUtils.isEmpty(robotDirectConnectSecret)));
    }

}
