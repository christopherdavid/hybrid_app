package com.neatorobotics.android.slide.framework.webservice.robot;

public interface RobotDetailListener {
	public void onRobotDetailReceived(RobotItem robotItem);
	public void onNetworkError(String errMessage);
	public void onServerError(String errMessage);
}
