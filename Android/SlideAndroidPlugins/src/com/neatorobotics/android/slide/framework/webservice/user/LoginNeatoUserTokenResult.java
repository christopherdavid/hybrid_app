package com.neatorobotics.android.slide.framework.webservice.user;

import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;


public class LoginNeatoUserTokenResult extends NeatoWebserviceResult{	
	public ExtraParams extra_params;	
	
	public String result;
	
	public String getAuthToken() {
		return result;
	}
	
	public boolean success() {
		return ((status == RESPONSE_STATUS_SUCCESS) && (!TextUtils.isEmpty(result)));
	}
	
	public static class ExtraParams {
		public int validation_status;
		public String message;
	}
}
