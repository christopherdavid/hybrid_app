package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;


public class GetNeatoRobotSchedulesResult extends NeatoWebserviceResult {
	
	public GetNeatoRobotSchedulesResult(NeatoHttpResponse response) {
		super(response);
	}
	public static final int RESPONSE_STATUS_SUCCESS = 0;
	
	@JsonProperty(value="status")
	public int mStatus = -1; 
	
	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public ArrayList<Result> mResult;

	public GetNeatoRobotSchedulesResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS)  );
	}
	public static class Result {
		
		@JsonProperty(value="id")
		public String mId;	
		
		@JsonProperty(value="schedule_type")
		public String mSchedule_Type;
		
		@JsonProperty(value="xml_data_version")
		public String mXml_Data_Version;	
		
		@JsonProperty(value="blob_data_version")
		public String mBlob_Data_Version;

	}

}