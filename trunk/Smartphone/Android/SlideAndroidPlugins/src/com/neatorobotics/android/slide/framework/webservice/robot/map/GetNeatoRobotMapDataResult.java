package com.neatorobotics.android.slide.framework.webservice.robot.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class GetNeatoRobotMapDataResult extends NeatoWebserviceResult{
	public GetNeatoRobotMapDataResult(NeatoHttpResponse response) {
		super(response);
	}
	public GetNeatoRobotMapDataResult(int response, int responseStatusCode, String message) {
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

	public GetNeatoRobotMapDataResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS) && (mResult != null));
	}
	public static class Result {
				
		@JsonProperty(value="xml_data_url")
		public String mXml_Data_Url;	
		
		@JsonProperty(value="blob_data_url")
		public String mBlob_Data_Url;

	}


}
