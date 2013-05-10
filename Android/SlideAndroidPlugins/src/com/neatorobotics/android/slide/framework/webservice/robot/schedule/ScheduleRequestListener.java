package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;

public interface ScheduleRequestListener extends WebServiceBaseRequestListener{
	public void onScheduleData(JSONObject scheduleJson);
}
