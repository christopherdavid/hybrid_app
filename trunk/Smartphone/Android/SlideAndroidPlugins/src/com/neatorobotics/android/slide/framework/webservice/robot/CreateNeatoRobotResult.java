package com.neatorobotics.android.slide.framework.webservice.robot;

import org.codehaus.jackson.annotate.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class CreateNeatoRobotResult extends NeatoWebserviceResult {

	
	public CreateNeatoRobotResult(NeatoHttpResponse response) {
		super(response);
	}
	public static final int RESPONSE_STATUS_SUCCESS = 0;
	
	@JsonProperty(value="status")
	public int mStatus = -1; 
	
	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public Result mResult;

	public CreateNeatoRobotResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS) && ((mResult != null) && mResult.mSuccess));
	}
	public class Result {
		@JsonProperty(value="success")
		public boolean mSuccess;
		
		@JsonProperty(value="message")
		public String mMessage;	
	}



}
