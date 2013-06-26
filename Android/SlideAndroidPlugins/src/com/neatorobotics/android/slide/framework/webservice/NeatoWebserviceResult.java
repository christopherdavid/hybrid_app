package com.neatorobotics.android.slide.framework.webservice;


public abstract class NeatoWebserviceResult {
	
	public static final int RESPONSE_STATUS_SUCCESS = 0;
	
	public int mResponseStatus;
	public int mResponseErrorReason;
	
	public int status = -1; // 0 is returned as success from the webservice so we should not initialize to 0
	public String message;
	public ServerError error;
	
	public NeatoWebserviceResult () 
	{
		mResponseStatus = NeatoWebConstants.RESPONSE_SUCCESS;
	}

	public NeatoWebserviceResult(int responseStatus, int responseErrorReason) {
		mResponseStatus = responseStatus;
		mResponseErrorReason = responseErrorReason;
	}
	public NeatoWebserviceResult(NeatoHttpResponse response) {
		mResponseStatus = response.mResponse;
		mResponseErrorReason = response.mServerErrorReason;
	}
	
	public void setResult (int status, int reason, String message)
	{
		mResponseStatus = status;
		mResponseErrorReason = reason;
		this.message = message;
	}
	
	
	public final boolean isCompleted() {
		return (mResponseStatus == NeatoWebConstants.RESPONSE_SUCCESS);
	}
	
	public final boolean isNetworkError() {
		return (mResponseStatus == NeatoWebConstants.RESPONSE_NETWORK_ERROR);
	}
	
	public final boolean isServerError() {
		return (mResponseStatus == NeatoWebConstants.RESPONSE_SERVER_ERROR) || (mResponseStatus == NeatoWebConstants.RESPONSE_SERVER_ERROR_REASON_UNKNOWN) ;
	}
	
	public boolean success() {
		return (status == RESPONSE_STATUS_SUCCESS);
	}
	
	public static class ServerError {
		public int code;
		public String message;
	}

	public int getErrorCode() {
		int code;
		if (error != null) {
			code = error.code;
		}
		else {
			code = mResponseStatus;
		}
		return code;
	}
	
	public String getErrorMessage() {
		String errMessage;
		if (error != null) {
			errMessage = error.message;
		}
		else {
			errMessage = message;
		}
		return errMessage;
	}
}
