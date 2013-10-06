package com.neatorobotics.android.slide.framework.webservice.user;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class RobotLinkResult extends NeatoWebserviceResult {

	
	public RobotLinkResult(NeatoHttpResponse response) {
		super(response);
	}
	
	public RobotLinkResult(int response, int responseStatusCode, String msg) {
		super(response, responseStatusCode);
		message = msg;
	}
	
	public Result result;

	public RobotLinkResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((status == RESPONSE_STATUS_SUCCESS) && (result != null) && (result.success));
	}
	
	public static class Result {
		public boolean success;
		public String message;
		public String serial_number;
	}

}
