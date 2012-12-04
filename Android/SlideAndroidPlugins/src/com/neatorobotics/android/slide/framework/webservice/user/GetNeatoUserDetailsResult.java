package com.neatorobotics.android.slide.framework.webservice.user;

import org.codehaus.jackson.annotate.JsonProperty;

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
	
	@JsonProperty(value="status")
	public int mStatus = -1; 
	
	@JsonProperty(value="message")
	public String mMessage;	
	
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
		public String[] mRobots;	
		
	}

}
