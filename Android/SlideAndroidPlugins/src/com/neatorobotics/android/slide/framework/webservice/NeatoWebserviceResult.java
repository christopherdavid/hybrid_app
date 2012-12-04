package com.neatorobotics.android.slide.framework.webservice;




public abstract class NeatoWebserviceResult {
	
	public int mResponseStatus;
	public int mResponseServerErrorReason;
	
	public NeatoWebserviceResult () 
	{
		mResponseStatus = NeatoWebConstants.RESPONSE_SUCCESS;
	}

	
	public NeatoWebserviceResult(NeatoHttpResponse response) {
		mResponseStatus = response.mResponse;
		mResponseServerErrorReason = response.mServerErrorReason;
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

	public abstract boolean success();


}
