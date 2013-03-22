package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import java.util.ArrayList;

public interface GetScheduleEventsListener {
	public void onSuccess(String scheduleId, ArrayList<String> events);
	public void onServerError(int errorCode, String errMessage);
	public void onNetworkError(String errMessage);
}
