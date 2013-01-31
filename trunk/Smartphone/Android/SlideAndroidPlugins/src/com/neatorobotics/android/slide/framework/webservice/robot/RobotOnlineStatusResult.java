package com.neatorobotics.android.slide.framework.webservice.robot;

import org.codehaus.jackson.annotate.JsonProperty;

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
	public static final int RESPONSE_STATUS_SUCCESS = 0;
	
	public RobotOnlineStatusResult(NeatoHttpResponse response) {
		super(response);
	}
	
	public RobotOnlineStatusResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS) && (mResult != null));
	}
	
	@JsonProperty(value="status")
	public int mStatus = -1; 
	
	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public Result mResult;

	public static class Result {
		@JsonProperty(value="online")
		public boolean mOnline;
		
		@JsonProperty(value="message")
		public String mMessage;	
	}
}
