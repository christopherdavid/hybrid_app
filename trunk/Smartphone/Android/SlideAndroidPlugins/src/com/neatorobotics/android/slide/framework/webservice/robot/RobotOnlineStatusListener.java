package com.neatorobotics.android.slide.framework.webservice.robot;

/*
 * Callback listener to be invoked when the robot online status 
 * request completed
 */	
public interface RobotOnlineStatusListener {	
	public void onComplete(boolean isOnline);
	public void onServerError(String errMessage);
	public void onNetworkError(String errMessage);
}
