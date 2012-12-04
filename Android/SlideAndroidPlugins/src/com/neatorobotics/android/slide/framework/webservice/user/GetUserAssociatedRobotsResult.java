package com.neatorobotics.android.slide.framework.webservice.user;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class GetUserAssociatedRobotsResult extends NeatoWebserviceResult {


	public static final int STATUS_SUCCESS = 0;
	
	public GetUserAssociatedRobotsResult() {
		super();
	}
	
	public GetUserAssociatedRobotsResult(NeatoHttpResponse response) {
		super(response);
	}

	@JsonProperty(value="status")
	public int mStatus = -1; 
	
	@JsonProperty(value="message")
	public String mMessage;	
	
	@JsonProperty(value="result")
	public ArrayList<Result> mResult[];
	
	@Override
	public boolean success() {
		return ((mStatus == STATUS_SUCCESS));
	}
	
	public class Result {
		
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
