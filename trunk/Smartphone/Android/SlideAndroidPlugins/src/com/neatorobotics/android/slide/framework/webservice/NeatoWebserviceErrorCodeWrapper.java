package com.neatorobotics.android.slide.framework.webservice;

import android.util.SparseIntArray;

import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;

public class NeatoWebserviceErrorCodeWrapper {
	
	// Add the server error codes in case we have to return some other error to the user.
	private static SparseIntArray ERROR_CODE_MAP = new SparseIntArray();
	
	static {
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_AUTHENTICATION_FAILED, ErrorTypes.ERROR_AUTHENTICATION_FAILED);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_INVALID_EMAIL_ID, ErrorTypes.ERROR_INVALID_EMAIL_ID);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_EMAIL_ALREADY_REGISTERED, ErrorTypes.ERROR_EMAIL_ALREADY_REGISTERED);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_CREATE_USER_FAILED_TRY_AGAIN, ErrorTypes.ERROR_CREATE_USER_FAILED_TRY_AGAIN);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_OLD_PASSWORD_MISMATCH, ErrorTypes.ERROR_OLD_PASSWORD_MISMATCH);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_INVALID_ROBOT_ACCOUNT_DETAIL, ErrorTypes.ERROR_INVALID_ROBOT_ACCOUNT_DETAIL);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_EMAIL_NOT_REGISTERED, ErrorTypes.ERROR_EMAIL_NOT_REGISTERED);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_ROBOT_NOT_REGISTERED, ErrorTypes.ERROR_ROBOT_NOT_REGISTERED);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_INVALID_ALTERNATE_EMAIL_ID, ErrorTypes.ERROR_INVALID_ALTERNATE_EMAIL_ID);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_RESEND_VALIDATION_EMAIL_LIMIT_REACHED, ErrorTypes.ERROR_RESEND_VALIDATION_EMAIL_LIMIT_REACHED);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_EMAIL_ALREADY_VALIDATED, ErrorTypes.ERROR_EMAIL_ALREADY_VALIDATED);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_SCHEDULE_VERSION_MISMATCH, ErrorTypes.ERROR_SCHEDULE_VERSION_MISMATCH);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_INVALID_SCHEDULE_TYPE, ErrorTypes.ERROR_INVALID_SCHEDULE_TYPE);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_INVALID_LINKING_CODE, ErrorTypes.ERROR_INVALID_LINKING_CODE);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_LINKING_CODE_EXPIRED, ErrorTypes.ERROR_LINKING_CODE_EXPIRED);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_NO_SCHEDULE_FOR_GIVEN_ROBOT, ErrorTypes.ERROR_NO_SCHEDULE_FOR_GIVEN_ROBOT);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_AUTHENTICATION_FAILED, ErrorTypes.ERROR_AUTHENTICATION_FAILED);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_LINKING_CODE_IN_USE, ErrorTypes.ERROR_LINKING_CODE_IN_USE);
		ERROR_CODE_MAP.put(NeatoWebServiceErrorCodes.ERROR_ROBOT_USER_ASSOCIATION_ALREADY_EXISTS, ErrorTypes.ERROR_ROBOT_USER_ASSOCIATION_ALREADY_EXISTS);
		
	}
	
	
	public static int convertServerErrorToUIErrorCodes(int serverErrorCode) {
		int error = ERROR_CODE_MAP.get(serverErrorCode, ErrorTypes.ERROR_TYPE_UNKNOWN);
		return error;
	}
}
