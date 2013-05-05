package com.neatorobotics.android.slide.framework.webservice.robot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class SetRobotProfileDetailsResult extends NeatoWebserviceResult {
	
	public SetRobotProfileDetailsResult(NeatoHttpResponse response) {
		super(response);
	}
	public SetRobotProfileDetailsResult(int response, int responseStatusCode, String msg) {
		super(response, responseStatusCode);
		message = msg;
	}	
	
	public static final int RESULT_STATUS_SUCCESS = 1;

	@JsonProperty(value="result")
	public int result;

	public SetRobotProfileDetailsResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((status == RESPONSE_STATUS_SUCCESS) && (result == RESULT_STATUS_SUCCESS));
	}
}
