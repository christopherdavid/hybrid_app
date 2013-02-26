package com.neatorobotics.android.slide.framework.webservice.robot.map;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class GetNeatoRobotMapsResult extends NeatoWebserviceResult {
	
	public GetNeatoRobotMapsResult(NeatoHttpResponse response) {
		super(response);
	}
	public GetNeatoRobotMapsResult(int response, int responseStatusCode, String message) {
		super(response, responseStatusCode);
		mMessage = message;
	}
	public static final int RESPONSE_STATUS_SUCCESS = 0;
	
	@JsonProperty(value="status")
	public int mStatus = -1; 
	
	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public ArrayList<Result> mResult;

	public GetNeatoRobotMapsResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS));
	}
	public static class Result {
		
		@JsonProperty(value="id")
		public String mId;	
		
		@JsonProperty(value="xml_data_version")
		public String mXml_Data_Version;	
		
		@JsonProperty(value="blob_data_version")
		public String mBlob_Data_Version;

	}

}
