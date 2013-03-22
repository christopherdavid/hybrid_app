package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleGroup2;

public interface GetScheduleListener2 {
	public void onSuccess(ScheduleGroup2 group, String scheduleId, int scheduleType, String scheduleVersion);
	public void onServerError(int errorCode, String errMessage);
	public void onNetworkError(String errMessage);
}
