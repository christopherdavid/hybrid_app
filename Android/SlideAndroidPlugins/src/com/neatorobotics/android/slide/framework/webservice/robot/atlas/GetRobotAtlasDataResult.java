package com.neatorobotics.android.slide.framework.webservice.robot.atlas;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class GetRobotAtlasDataResult extends NeatoWebserviceResult {
	public GetRobotAtlasDataResult(NeatoHttpResponse response) {
		super(response);
	}
	public GetRobotAtlasDataResult(int response, int responseStatusCode, String message) {
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

	public GetRobotAtlasDataResult() {
		super();
	}

	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS));
	}

	public class Result {
		
		@JsonProperty(value="atlas_id")
		public String mAtlas_Id;
		
		@JsonProperty(value="xml_data_url")
		public String mXml_Data_Url;	
		
		@JsonProperty(value="version")
		public String mXml_Data_Version;	
	}
}
