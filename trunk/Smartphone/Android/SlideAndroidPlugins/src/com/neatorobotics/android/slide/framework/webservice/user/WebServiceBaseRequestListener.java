package com.neatorobotics.android.slide.framework.webservice.user;

import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

/**
 * This interface should be implemented by Plugin manager to keep
 * track of the called request result.
 */
public interface WebServiceBaseRequestListener {
	public void onReceived(NeatoWebserviceResult responseResult);
	public void onNetworkError(String errMessage);
	public void onServerError(int errorType, String errMessage);
}
