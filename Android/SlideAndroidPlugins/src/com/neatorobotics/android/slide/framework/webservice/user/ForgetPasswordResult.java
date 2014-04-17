package com.neatorobotics.android.slide.framework.webservice.user;

import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class ForgetPasswordResult extends NeatoWebserviceResult {
    public Result result;

    @Override
    public boolean success() {
        return ((status == RESPONSE_STATUS_SUCCESS) && ((result != null) && result.success));
    }

    public static class Result {
        public boolean success;
        public String message;
    }
}
