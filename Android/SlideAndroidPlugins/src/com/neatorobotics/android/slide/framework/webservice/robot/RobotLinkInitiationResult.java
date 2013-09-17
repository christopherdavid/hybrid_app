package com.neatorobotics.android.slide.framework.webservice.robot;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class RobotLinkInitiationResult extends NeatoWebserviceResult {

	
	public RobotLinkInitiationResult(NeatoHttpResponse response) {
		super(response);
	}
	
	public RobotLinkInitiationResult(int response, int responseStatusCode, String msg) {
		super(response, responseStatusCode);
		message = msg;
	}
	
	public Result result;

	public RobotLinkInitiationResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((status == RESPONSE_STATUS_SUCCESS) && (result != null) && (result.success));
	}
	
	public static class Result {
		public boolean success;
		public String message;
		public long expiry_time;
		public String serial_number;
	}

}
