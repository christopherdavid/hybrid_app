package com.neatorobotics.android.slide.framework.webservice.robot.atlas;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class AddUpdateRobotAtlasMetadataResult  extends NeatoWebserviceResult{
	public AddUpdateRobotAtlasMetadataResult(NeatoHttpResponse response) {
		super(response);
	}
	public AddUpdateRobotAtlasMetadataResult(int response, int responseStatusCode, String message) {
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

	public AddUpdateRobotAtlasMetadataResult() {
		super();
	}

	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS) && (mResult.mSuccess));
	}
	
	public class Result {
		@JsonProperty(value="success")
		public boolean mSuccess;	

		@JsonProperty(value="robot_atlas_id")
		public String mRobot_Atlas_Id;	

		@JsonProperty(value="xml_data_version")
		public String mXml_Data_Version;	
		
		@JsonProperty(value="message")
		public String mMessage;
	}


}
