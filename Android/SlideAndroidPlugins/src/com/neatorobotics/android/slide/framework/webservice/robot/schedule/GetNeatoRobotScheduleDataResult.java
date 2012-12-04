package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import org.codehaus.jackson.annotate.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class GetNeatoRobotScheduleDataResult extends NeatoWebserviceResult{

	public GetNeatoRobotScheduleDataResult(NeatoHttpResponse response) {
		super(response);
	}
	public static final int RESPONSE_STATUS_SUCCESS = 0;

	@JsonProperty(value="status")
	public int mStatus = -1; 

	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public Result mResult;

	public GetNeatoRobotScheduleDataResult() {
		super();
	}

	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS) && (mResult != null));
	}


	public static class Result {

		@JsonProperty(value="schedule_type")
		public String mSchedule_Type;	

		@JsonProperty(value="xml_data_url")
		public String mXml_Data_Url;	

		@JsonProperty(value="blob_data_url")
		public String mBlob_Data_Url;

	}
}
