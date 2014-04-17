package com.neatorobotics.android.slide.framework.webservice.robot;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class RobotDetailResult extends NeatoWebserviceResult {

    public RobotDetailResult(NeatoHttpResponse response) {
        super(response);
    }

    public RobotDetailResult(int response, int responseStatusCode, String msg) {
        super(response, responseStatusCode);
        message = msg;
    }

    public RobotItem result;

    public RobotDetailResult() {
        super();
    }

    @Override
    public boolean success() {
        return (status == RESPONSE_STATUS_SUCCESS);
    }
}
