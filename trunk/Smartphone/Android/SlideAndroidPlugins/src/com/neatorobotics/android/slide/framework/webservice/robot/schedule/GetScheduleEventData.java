package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import com.neatorobotics.android.slide.framework.robot.schedule2.Schedule2;

public interface GetScheduleEventData {
	public void onSuccess(String scheduleId, String eventId, Schedule2 schedule);
	public void onNetworkError(String errMessage);
	public void onServerError(int errorCode, String errMessage);
}
