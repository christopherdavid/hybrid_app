package com.neatorobotics.android.slide.framework.webservice.robot;

public interface SetRobotProfileDetailsListener {
	public void onComplete(RobotItem robotItem);
	public void onServerError(String errMessage);
	public void onNetworkError(String errMessage);
}
