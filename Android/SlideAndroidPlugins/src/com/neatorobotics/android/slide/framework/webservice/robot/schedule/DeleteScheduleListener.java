package com.neatorobotics.android.slide.framework.webservice.robot.schedule;
/*
 * Interface to receive callbacks of deleting schedule data of the robot
 * Success callback will get called when schedule data deleted successfully
 * And the error callback will get called when any type of error occurred
 */
public interface DeleteScheduleListener {
	public void onSuccess(String robotId);
	public void onServerError(String errMessage);
	public void onNetworkError(String errMessage);
}
