package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

public interface ScheduleWebserviceListener {
	public void onSuccess();
	public void onNetworkError();
	public void onServerError();
}
