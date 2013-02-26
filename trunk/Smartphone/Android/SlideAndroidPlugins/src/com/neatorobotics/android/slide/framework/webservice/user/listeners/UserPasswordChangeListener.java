package com.neatorobotics.android.slide.framework.webservice.user.listeners;


public interface UserPasswordChangeListener {
	public void onComplete();
	public void onNetworkError(String errorMessage);
	public void onServerError(int statusCode, String errorMessage);
}
