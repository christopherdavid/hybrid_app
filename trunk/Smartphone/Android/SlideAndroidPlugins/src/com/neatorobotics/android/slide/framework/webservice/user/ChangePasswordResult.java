package com.neatorobotics.android.slide.framework.webservice.user;

import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class ChangePasswordResult extends NeatoWebserviceResult {
    public Result result;

    public ChangePasswordResult() {
        super();
    }

    @Override
    public boolean success() {
        return ((status == RESPONSE_STATUS_SUCCESS) && ((result != null) && result.success));
    }

    public class Result {
        public boolean success;
        public String message;
    }
}