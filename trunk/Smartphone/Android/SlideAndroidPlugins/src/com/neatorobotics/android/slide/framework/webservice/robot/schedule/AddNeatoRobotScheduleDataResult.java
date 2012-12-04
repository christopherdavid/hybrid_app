package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import org.codehaus.jackson.annotate.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class AddNeatoRobotScheduleDataResult extends NeatoWebserviceResult{

	public AddNeatoRobotScheduleDataResult(NeatoHttpResponse response) {
		super(response);
	}
	public static final int RESPONSE_STATUS_SUCCESS = 0;

	@JsonProperty(value="status")
	public int mStatus = -1; 

	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public Result mResult;

	public AddNeatoRobotScheduleDataResult() {
		super();
	}

	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS) && (mResult.mSuccess));
	}


	public class Result {
		@JsonProperty(value="success")
		public boolean mSuccess;	

		@JsonProperty(value="schedule_type")
		public String mSchedule_Type;	

		@JsonProperty(value="robot_schedule_id")
		public String mRobot_Schedule_Id;	

		@JsonProperty(value="xml_data_version")
		public String mXml_Data_Version;	

		@JsonProperty(value="blob_data_version")
		public String mBlob_Data_Version;

	}
}

