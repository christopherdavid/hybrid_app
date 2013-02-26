package com.neatorobotics.android.slide.framework.webservice.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;


public class LoginNeatoUserTokenResult extends NeatoWebserviceResult{

	public static final int RESPONSE_STATUS_SUCCESS = 0;
	
	
	@JsonProperty(value="result")
	public String mUserAuthToken;
	
	public LoginNeatoUserTokenResult(NeatoHttpResponse response) {
		super(response);
	}
	public LoginNeatoUserTokenResult() {
		super();
	}
	public LoginNeatoUserTokenResult(int response, int responseStatusCode, String message) {
		super(response, responseStatusCode);
		mMessage = message;
	}
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS) && (mUserAuthToken != null));
	}

}
