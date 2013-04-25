package com.neatorobotics.android.slide.framework.webservice.user;

public class UserValidationHelper {
	// User validation status code on server.
	private static final int USER_VALIDATION_STATUS_VALIDATED 		=  0;
	private static final int USER_VALIDATION_STATUS_VALIDATION_IN_GRACEPERIOD		= -1;
	private static final int USER_VALIDATION_STATUS_NOT_VALIDATED 	= -2;
	
	
	// We convert the user validation server status code to internal status code
	public static final int VALIDATION_STATUS_UNKNOWN		 	=  -99;
	public static final int VALIDATION_STATUS_VALIDATED 		=   0;
	public static final int VALIDATION_STATUS_PENDING 			=  -1;
	public static final int VALIDATION_STATUS_NOT_VALIDATED 	=  -2;
	
	
	
	// Public static helper method to get the internal user validation code
	public static int getUserValidationStatus(int serverStatusCode) {
		int validationStatusCode = VALIDATION_STATUS_UNKNOWN;
		
		switch (serverStatusCode) {
			case USER_VALIDATION_STATUS_VALIDATED:
				validationStatusCode = VALIDATION_STATUS_VALIDATED;
				break;
				
			case USER_VALIDATION_STATUS_VALIDATION_IN_GRACEPERIOD:
				validationStatusCode = VALIDATION_STATUS_PENDING;
				break;
				
			case USER_VALIDATION_STATUS_NOT_VALIDATED:
				validationStatusCode = VALIDATION_STATUS_NOT_VALIDATED;
				break;		
				
			default:
				break;
		}
		
		return validationStatusCode;
	}
}
