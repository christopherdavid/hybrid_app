package com.neatorobotics.android.slide.framework.webservice.robot.datamanager;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class SetRobotProfileDetailsResult3 extends NeatoWebserviceResult {

    public SetRobotProfileDetailsResult3(NeatoHttpResponse response) {
        super(response);
    }

    public SetRobotProfileDetailsResult3(int response, int responseStatusCode, String msg) {
        super(response, responseStatusCode);
        message = msg;
    }

    public static final int RESULT_STATUS_SUCCESS = 1;

    public int result;

    public ExtraParams extra_params;

    public SetRobotProfileDetailsResult3() {
        super();
    }

    @Override
    public boolean success() {
        return ((status == RESPONSE_STATUS_SUCCESS));
    }

    public static class ExtraParams {
        public int expected_time;
        public long timestamp;
    }

}
