package com.neatorobotics.android.slide.framework.webservice.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class ChangePasswordResult extends NeatoWebserviceResult {
	
	public static final int STATUS_SUCCESS = 0;
	
	@JsonProperty(value="result")
	public Result mResult;
	
	public ChangePasswordResult() {
		super();
	}
	
	public ChangePasswordResult(NeatoHttpResponse response) {
		super(response);
	}
	
	public ChangePasswordResult(int response, int responseStatusCode, String message) {
		super(response, responseStatusCode);
		mMessage = message;
	}
	
	@Override
	public boolean success() {
		return ((mStatus == STATUS_SUCCESS) && ((mResult != null) && mResult.mSuccess));
	}
	
	public class Result {
		@JsonProperty(value="success")
		public boolean mSuccess;
		
		@JsonProperty(value="message")
		public String mMessage;	
	}
}