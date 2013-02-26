package com.neatorobotics.android.slide.framework.webservice.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class SetUserAttributesResult extends NeatoWebserviceResult {
	
	public static final int STATUS_SUCCESS = 0;
	
	@JsonProperty(value="result")
	public Result mResult;
	
	public SetUserAttributesResult() {
		super();
	}
	
	public SetUserAttributesResult(NeatoHttpResponse response) {
		super(response);
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

