package com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid;

import org.codehaus.jackson.annotate.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class GetAtlasGridDataResult extends NeatoWebserviceResult{

	public GetAtlasGridDataResult(NeatoHttpResponse response) {
		super(response);
	}
	public static final int RESPONSE_STATUS_SUCCESS = 0;

	@JsonProperty(value="status")
	public int mStatus = -1; 

	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public Result[] mResult;

	public GetAtlasGridDataResult() {
		super();
	}

	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS));
	}

	public static class Result {
		@JsonProperty(value="id_grid")
		public String mId_Grid;	
		
		@JsonProperty(value="blob_data_file_name")
		public String mGrid_Data_Url;	
		
		@JsonProperty(value="version")
		public String mGrid_Data_Version;	
	}
}
