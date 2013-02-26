package com.neatorobotics.android.slide.framework.webservice.robot.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class UpdateNeatoRobotMapResult extends NeatoWebserviceResult{

	public UpdateNeatoRobotMapResult(NeatoHttpResponse response) {
		super(response);
	}
	public UpdateNeatoRobotMapResult(int response, int responseStatusCode, String message) {
		super(response, responseStatusCode);
		mMessage = message;
	}
	public static final int RESPONSE_STATUS_SUCCESS = 0;

	@JsonProperty(value="status")
	public int mStatus = -1; 

	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public Result mResult;

	public UpdateNeatoRobotMapResult() {
		super();
	}

	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS) && (mResult.mSuccess));
	}


	public class Result {
		@JsonProperty(value="success")
		public boolean mSuccess;	

		@JsonProperty(value="message")
		public String mMessage;	

	}
}

