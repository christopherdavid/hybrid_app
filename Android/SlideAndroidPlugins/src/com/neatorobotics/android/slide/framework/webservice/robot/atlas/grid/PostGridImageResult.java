package com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class PostGridImageResult extends NeatoWebserviceResult {

	public PostGridImageResult(NeatoHttpResponse response) {
		super(response);
	}
	public PostGridImageResult(int response, int responseStatusCode, String message) {
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

	public PostGridImageResult() {
		super();
	}

	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS) && mResult.mSuccess);
	}

	public static class Result {
		@JsonProperty(value="success")
		public boolean mSuccess;	
		
		@JsonProperty(value="id_grid_image")
		public String mId_Grid_Image;	
		
		@JsonProperty(value="id_atlas")
		public String mId_Atlas;	
		
		@JsonProperty(value="id_grid")
		public String mId_Grid;
		
		@JsonProperty(value="version")
		public String mVersion;
	
		@JsonProperty(value="blob_data_file_name")
		public String mGrid_Data_File_Name;
	}
}