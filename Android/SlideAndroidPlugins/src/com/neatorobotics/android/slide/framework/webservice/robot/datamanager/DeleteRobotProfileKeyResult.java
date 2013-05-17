package com.neatorobotics.android.slide.framework.webservice.robot.datamanager;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class DeleteRobotProfileKeyResult extends NeatoWebserviceResult {
	

	public DeleteRobotProfileKeyResult(NeatoHttpResponse response) {
		super(response);
	}
	
	public static final int RESPONSE_STATUS_SUCCESS = 0;
	public static final int RESULT_STATUS_SUCCESS = 1;

	public Result result;

	public DeleteRobotProfileKeyResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((status == RESPONSE_STATUS_SUCCESS) && (result != null) && (result.success));
	}
	
	public static class Result {
		boolean success;
	}
}