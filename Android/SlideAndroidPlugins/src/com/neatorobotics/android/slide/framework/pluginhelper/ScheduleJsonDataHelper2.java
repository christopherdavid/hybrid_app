package com.neatorobotics.android.slide.framework.pluginhelper;

import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.schedule2.AdvancedScheduleEvent2;
import com.neatorobotics.android.slide.framework.robot.schedule2.BasicScheduleEvent2;
import com.neatorobotics.android.slide.framework.robot.schedule2.Schedule2;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleTimeObject2;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2.Day;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2.SchedularEvent;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2;


public class ScheduleJsonDataHelper2 {

	private static final String TAG = ScheduleJsonDataHelper.class.getSimpleName();
	
	private static AdvancedScheduleEvent2 jsonToAdvancedSchedule(JSONObject jsonObject, String eventId) 
	{
		AdvancedScheduleEvent2 schedule = null;
		try {
			int day = jsonObject.getInt(JsonMapKeys.KEY_DAY);
			Day eventDay = SchedulerConstants2.detrmineDay(day);			
			String startTimeStr = jsonObject.getString(JsonMapKeys.KEY_START_TIME);
			String endTImeStr = jsonObject.getString(JsonMapKeys.KEY_END_TIME);
			int eventType = jsonObject.getInt(JsonMapKeys.KEY_EVENT_TYPE);
			SchedularEvent event = SchedulerConstants2.detrmineEvent(eventType);
			String area = jsonObject.getString(JsonMapKeys.KEY_AREA);
			ScheduleTimeObject2 startTime = new ScheduleTimeObject2(startTimeStr);
			ScheduleTimeObject2 endTime = new ScheduleTimeObject2(endTImeStr);
			schedule = new AdvancedScheduleEvent2(eventId, eventDay, startTime, endTime, area, event);
		} catch (JSONException e) {
			LogHelper.log(TAG, "Exception in jsonToSchedule", e);
		}
		return schedule;
	}
	
	private static BasicScheduleEvent2 jsonToBasicSchedule(JSONObject jsonObject, String eventId) {
		BasicScheduleEvent2 schedule = null;
		try {
			int dayInt = jsonObject.getInt(JsonMapKeys.KEY_DAY);
			Day day = SchedulerConstants2.detrmineDay(dayInt);
			
			String startTimeStr = jsonObject.getString(JsonMapKeys.KEY_START_TIME);
			ScheduleTimeObject2 startTime = new ScheduleTimeObject2(startTimeStr);
			schedule = new BasicScheduleEvent2(eventId, day, startTime);
		} catch (JSONException e) {
			LogHelper.log(TAG, "Exception in jsonToSchedule", e);
		}
		return schedule;
	}
	
	
	public static Schedule2 jsonToSchedule(JSONObject jsonObject, String eventId, int scheduleType) {
		if (scheduleType == SchedulerConstants2.SCHEDULE_TYPE_ADVANCED) {
			return jsonToAdvancedSchedule(jsonObject, eventId);
		} else if (scheduleType == SchedulerConstants2.SCHEDULE_TYPE_BASIC) {
			return jsonToBasicSchedule(jsonObject, eventId);
		}
		return null;
	}
}
