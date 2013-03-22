package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

public interface ScheduleEventListener {
	public void onSuccess();
	public void onNetworkError(String errMessage);
	public void onServerError(int errorCode, String errMessage);
}
