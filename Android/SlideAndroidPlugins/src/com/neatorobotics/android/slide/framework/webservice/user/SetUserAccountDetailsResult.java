package com.neatorobotics.android.slide.framework.webservice.user;

import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class SetUserAccountDetailsResult extends NeatoWebserviceResult {

    public Result result;

    @Override
    public boolean success() {
        return ((status == RESPONSE_STATUS_SUCCESS) && (result != null));
    }

    public static class Result {
        public boolean success;
    }

}
