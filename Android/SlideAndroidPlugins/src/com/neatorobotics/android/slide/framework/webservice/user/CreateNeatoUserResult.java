package com.neatorobotics.android.slide.framework.webservice.user;

import org.codehaus.jackson.annotate.JsonProperty;

import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;


public class CreateNeatoUserResult extends NeatoWebserviceResult {
	
	public static final int STATUS_SUCCESS = 0;
	
	@JsonProperty(value="status")
	public int mStatus = -1; // 0 is returned as success from the webservice so we should not initialize to 0
	
	@JsonProperty(value="message")
	public String mMessage;	

	@JsonProperty(value="result")
	public Result mResult;
	
	public CreateNeatoUserResult() {
		super();
	}
	
	public CreateNeatoUserResult(NeatoHttpResponse response) {
		super(response);
	}
	
	@Override
	public boolean success() {
		return ((mStatus == STATUS_SUCCESS) && ((mResult != null) && mResult.mSuccess));
	}
	
	public class Result {
		@JsonProperty(value="success")
		public boolean mSuccess;
		
		@JsonProperty(value="message")
		public String mMessage;	

		@JsonProperty(value="guid")
		public String mUserId;
		
		@JsonProperty(value="user_handle")
		public String mUserHandle;	
	}

}
