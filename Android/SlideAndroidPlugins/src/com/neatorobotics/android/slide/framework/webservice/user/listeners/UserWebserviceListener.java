package com.neatorobotics.android.slide.framework.webservice.user.listeners;


//To be used for listener to only success and error callbacks. No information will be passed.
public interface UserWebserviceListener {
	public void onSuccess();
	public void onNetworkError(String errorMessage);
	public void onServerError(String errorMessage);
}
