package com.neatorobotics.android.slide.framework.webservice.user.settings;

import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class SetRobotNotificationSettingsResult extends NeatoWebserviceResult {
    public Result result;

    public static class Result {
        public boolean success;
        public String message;
    }
}
