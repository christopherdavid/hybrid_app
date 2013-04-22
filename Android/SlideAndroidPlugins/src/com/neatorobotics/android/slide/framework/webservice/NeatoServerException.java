package com.neatorobotics.android.slide.framework.webservice;

import java.io.IOException;

public class NeatoServerException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int mStatusCode;
	private String mErrorMessage;
	
	
	public NeatoServerException(int statusCode, String message) {
		super(message);
		mStatusCode = statusCode;
		mErrorMessage = message;
	}
	
	public int getStatusCode() {
		return mStatusCode;
	}
	
	public String getErrorMessage() {
		return mErrorMessage;
	}

}
