package com.neatorobotics.android.slide.framework.webservice.user;

import org.codehaus.jackson.annotate.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;


public class LoginNeatoUserTokenResult extends NeatoWebserviceResult{

	public static final int RESPONSE_STATUS_SUCCESS = 0;
	
	
	@JsonProperty(value="status")
	public int mStatus = -1; // 0 is returned as success from the webservice so we should not initialize to 0
	
	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public String mUserAuthToken;
	
	public LoginNeatoUserTokenResult(NeatoHttpResponse response) {
		super(response);
	}
	public LoginNeatoUserTokenResult() {
		super();
	}

	
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS) && (mUserAuthToken != null));
	}

}
