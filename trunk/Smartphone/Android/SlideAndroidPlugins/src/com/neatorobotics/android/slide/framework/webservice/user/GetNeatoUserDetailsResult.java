package com.neatorobotics.android.slide.framework.webservice.user;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;


public class GetNeatoUserDetailsResult extends NeatoWebserviceResult {


	public static final int STATUS_SUCCESS = 0;
	
	public GetNeatoUserDetailsResult() {
		super();
	}
	
	public GetNeatoUserDetailsResult(NeatoHttpResponse response) {
		super(response);
	}	
	public GetNeatoUserDetailsResult(int response, int responseStatusCode, String message) {
		super(response, responseStatusCode);
		mMessage = message;
	}
	
	
	@JsonProperty(value="result")
	public Result mResult;
	
	@Override
	public boolean success() {
		return ((mStatus == STATUS_SUCCESS));
	}
	public static class Result {
		
		@JsonProperty(value="id")
		public String mId;
		
		@JsonProperty(value="name")
		public String mName;	

		@JsonProperty(value="email")
		public String mEmail;
		
		@JsonProperty(value="social_networks")
		public String[] mSocial_networks;	
		
		@JsonProperty(value="chat_id")
		public String mChat_id;
		
		@JsonProperty(value="chat_pwd")
		public String mChat_pwd;
		
		@JsonProperty(value="robots")
		public ArrayList<UserAssociatedRobot> mRobots;
	}

}
