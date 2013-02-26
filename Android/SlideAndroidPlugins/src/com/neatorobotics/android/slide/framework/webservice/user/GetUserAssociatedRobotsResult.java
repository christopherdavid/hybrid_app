package com.neatorobotics.android.slide.framework.webservice.user;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class GetUserAssociatedRobotsResult extends NeatoWebserviceResult {


	public static final int STATUS_SUCCESS = 0;
	
	public GetUserAssociatedRobotsResult() {
		super();
	}
	public GetUserAssociatedRobotsResult(int response, int responseStatusCode, String message) {
		super(response, responseStatusCode);
		mMessage = message;
	}
	public GetUserAssociatedRobotsResult(NeatoHttpResponse response) {
		super(response);
	}
	
	@JsonProperty(value="result")
	public ArrayList<Result> mResults;
	
	@Override
	public boolean success() {
		return ((mStatus == STATUS_SUCCESS));
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
		
	}
	
}
