package com.neatorobotics.android.slide.framework.webservice;

import com.fasterxml.jackson.annotation.JsonProperty;





public abstract class NeatoWebserviceResult {
	
	public int mResponseStatus;
	public int mResponseErrorReason;
	
	@JsonProperty(value="status")
	public int mStatus = -1; // 0 is returned as success from the webservice so we should not initialize to 0
	
	@JsonProperty(value="message")
	public String mMessage;
	
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
		mMessage = message;
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
