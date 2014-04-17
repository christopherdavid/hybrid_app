package com.neatorobotics.android.slide.framework.webservice.robot;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class RobotVirtualOnlineStatusResult extends NeatoWebserviceResult {

    public RobotVirtualOnlineStatusResult(NeatoHttpResponse response) {
        super(response);
    }

    public RobotVirtualOnlineStatusResult(int response, int responseStatusCode, String msg) {
        super(response, responseStatusCode);
        message = msg;
    }

    public RobotVirtualOnlineStatusResult() {
        super();
    }

    @Override
    public boolean success() {
        return ((status == RESPONSE_STATUS_SUCCESS) && (result != null));
    }

    public Result result;

    public static class Result {
        public boolean online;
        public String message;
    }
}