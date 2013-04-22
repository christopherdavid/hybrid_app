package com.neatorobotics.android.slide.framework.webservice.robot;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class RobotAssociationDisassociationResult extends NeatoWebserviceResult{	
	public RobotAssociationDisassociationResult(NeatoHttpResponse response) {
		super(response);
	}
	public RobotAssociationDisassociationResult(int response, int responseStatusCode, String msg) {
		super(response, responseStatusCode);
		message = msg;
	}	
	
	public Result result;

	public RobotAssociationDisassociationResult() {
		super();
	}
	
	@Override
	public boolean success() {
		return ((status == RESPONSE_STATUS_SUCCESS) && ((result != null) && result.success));
	}
	public class Result {		
		public boolean success;
		public String message;	
	}
}
