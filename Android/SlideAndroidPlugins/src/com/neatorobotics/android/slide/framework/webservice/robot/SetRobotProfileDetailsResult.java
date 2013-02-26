package com.neatorobotics.android.slide.framework.webservice.robot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class SetRobotProfileDetailsResult extends NeatoWebserviceResult {
	
	public SetRobotProfileDetailsResult(NeatoHttpResponse response) {
		super(response);
	}
	public SetRobotProfileDetailsResult(int response, int responseStatusCode, String message) {
		super(response, responseStatusCode);
		mMessage = message;
	}
	
	public static final int RESPONSE_STATUS_SUCCESS = 0;
	public static final int RESULT_STATUS_SUCCESS = 1;
	
	@JsonProperty(value="status")
	public int mStatus = -1; 
	
	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public int mResult;

	public SetRobotProfileDetailsResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS) && (mResult == RESULT_STATUS_SUCCESS));
	}
}
