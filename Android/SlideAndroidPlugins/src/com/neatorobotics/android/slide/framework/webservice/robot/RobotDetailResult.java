package com.neatorobotics.android.slide.framework.webservice.robot;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class RobotDetailResult extends NeatoWebserviceResult{
	
	public RobotDetailResult(NeatoHttpResponse response) {
		super(response);
	}
	public RobotDetailResult(int response, int responseStatusCode, String message) {
		super(response, responseStatusCode);
		mMessage = message;
	}
	
	public static final int RESPONSE_STATUS_SUCCESS = 0;
	
	@JsonProperty(value="status")
	public int mStatus = -1; 
	
	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public Result mResult;

	public RobotDetailResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return (mStatus == RESPONSE_STATUS_SUCCESS);
	}
	
	public static class Result {
		
		@JsonProperty(value="id")
		public String mId;
		
		@JsonProperty(value="name")
		public String mName;	

		@JsonProperty(value="serial_number")
		public String mSerialNumber;
		
		@JsonProperty(value="chat_id")
		public String mChat_id;
		
		@JsonProperty(value="chat_pwd")
		public String mChat_pwd;
		
		@JsonProperty(value="users")
		public ArrayList<RobotAssociatedUser> mUsers;	
	}
}
