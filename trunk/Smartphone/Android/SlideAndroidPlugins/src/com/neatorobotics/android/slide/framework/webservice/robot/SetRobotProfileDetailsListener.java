package com.neatorobotics.android.slide.framework.webservice.robot;

public interface SetRobotProfileDetailsListener {
	public void onSetProfileSuccess();
	public void onSetProfileServerError(String errMessage);
	public void onSetProfileNetworkError(String errMessage);
}
