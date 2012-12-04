package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import org.codehaus.jackson.annotate.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class UpdateNeatoRobotScheduleResult  extends NeatoWebserviceResult{

	public UpdateNeatoRobotScheduleResult(NeatoHttpResponse response) {
		super(response);
	}
	public static final int RESPONSE_STATUS_SUCCESS = 0;

	@JsonProperty(value="status")
	public int mStatus = -1; 

	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public Result mResult;

	public UpdateNeatoRobotScheduleResult() {
		super();
	}

	@Override
	public boolean success() {
		return ((mStatus == RESPONSE_STATUS_SUCCESS) && (mResult.mSuccess));
	}


	public class Result {
		@JsonProperty(value="success")
		public boolean mSuccess;	

		@JsonProperty(value="message")
		public String mMessage;	

	}
}

