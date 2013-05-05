package com.neatorobotics.android.slide.framework.webservice.robot;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

/*
 * Result object returned from "robot.is_online" method. 
 * It extends from NeatoWebserviceResult. When the HTTP call successful, 
 * this object is created from the json returned from the webservice call.
 * If the call was successful, it will contain a result object.
 * else it will be instantiated from the NeatoHttpResponse object so that only the
 * NeatoWebserviceResult attributes are filled with the error status and reason 
 */
public class RobotOnlineStatusResult extends NeatoWebserviceResult {	
	
	public RobotOnlineStatusResult(NeatoHttpResponse response) {
		super(response);
	}
	
	public RobotOnlineStatusResult(int response, int responseStatusCode, String msg) {
		super(response, responseStatusCode);
		message = msg;
	}
	
	public RobotOnlineStatusResult() {
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
