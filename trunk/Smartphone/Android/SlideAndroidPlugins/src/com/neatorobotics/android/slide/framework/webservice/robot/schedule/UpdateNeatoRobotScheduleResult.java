package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class UpdateNeatoRobotScheduleResult  extends NeatoWebserviceResult{

	public UpdateNeatoRobotScheduleResult(NeatoHttpResponse response) {
		super(response);
	}
	public UpdateNeatoRobotScheduleResult(int response, int responseStatusCode, String msg) {
		super(response, responseStatusCode);
		message = msg;
	}	
	
	public Result result;

	public UpdateNeatoRobotScheduleResult() {
		super();
	}

	@Override
	public boolean success() {
		return ((status == RESPONSE_STATUS_SUCCESS) && (result != null) && (result.success));
	}

	public class Result {		
		public boolean success;		
		public String message;	
		public String schedule_version;
	}
}

