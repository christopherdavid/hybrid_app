package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedScheduleGroup;

public interface GetScheduleListener {
	public void onSuccess(AdvancedScheduleGroup group, String scheduleId);
	public void onError(String errMessage);
}
