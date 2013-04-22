package com.neatorobotics.android.slide.framework.webservice.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;


public class LoginNeatoUserTokenResult extends NeatoWebserviceResult{
	
	@JsonProperty(value="result")
	public String mUserAuthToken;
	
	public boolean success() {
		return ((status == RESPONSE_STATUS_SUCCESS) && (mUserAuthToken != null));
	}

}
