package com.neatorobotics.android.slide.framework.webservice.robot.datamanager;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class GetRobotPresenceStatusResult extends NeatoWebserviceResult {

    public GetRobotPresenceStatusResult(NeatoHttpResponse response) {
        super(response);
    }

    public GetRobotPresenceStatusResult(int response, int responseStatusCode, String msg) {
        super(response, responseStatusCode);
        message = msg;
    }

    public Result result;

    public GetRobotPresenceStatusResult() {
        super();
    }

    @Override
    public boolean success() {
        return ((status == RESPONSE_STATUS_SUCCESS) && (result != null));
    }

    public class Result {
        public boolean online;
        public String message;
    }
}
