package com.neatorobotics.android.slide.framework.webservice.robot;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class RobotClearDataResult extends NeatoWebserviceResult {

	public RobotClearDataResult(NeatoHttpResponse response) {
		super(response);
	}
	
	public RobotClearDataResult(int response, int responseStatusCode, String msg) {
		super(response, responseStatusCode);
		message = msg;
	}

	public Result result;

	public RobotClearDataResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((status == RESPONSE_STATUS_SUCCESS) && ((result != null) && result.success));
	}
	public static class Result {
		public boolean success;
		public String message;	
	}
}
