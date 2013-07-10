package com.neatorobotics.android.slide.framework.pluginhelper;

import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.schedule2.AdvancedScheduleEvent2;
import com.neatorobotics.android.slide.framework.robot.schedule2.BasicScheduleEvent2;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleEvent;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleTimeObject2;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2.Day;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2.SchedularEvent;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2;


public class ScheduleJsonDataHelper2 {

	private static final String TAG = ScheduleJsonDataHelper2.class.getSimpleName();
	
	private static AdvancedScheduleEvent2 jsonToAdvancedSchedule(JSONObject jsonObject, String eventId) 
	{
		AdvancedScheduleEvent2 schedule = null;
		try {
			int day = jsonObject.getInt(JsonMapKeys.KEY_DAY);
			Day eventDay = SchedulerConstants2.determineDay(day);			
			String startTimeStr = jsonObject.getString(JsonMapKeys.KEY_START_TIME);
			String endTImeStr = jsonObject.getString(JsonMapKeys.KEY_END_TIME);
			int eventType = jsonObject.getInt(JsonMapKeys.KEY_EVENT_TYPE);
			SchedularEvent event = SchedulerConstants2.determineEvent(eventType);
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
			Day day = SchedulerConstants2.determineDay(dayInt);
			// If "cleaning mode" is not provided in the JSON then we use default cleaning mode
			String cleaningMode = String.valueOf(SchedulerConstants2.CLEANING_MODE_NORMAL); // default to normal mode
			
			String startTimeStr = jsonObject.getString(JsonMapKeys.KEY_START_TIME);
			ScheduleTimeObject2 startTime = new ScheduleTimeObject2(startTimeStr);

			if (!jsonObject.isNull(JsonMapKeys.KEY_CLEANING_MODE)) {
				cleaningMode = jsonObject.getString(JsonMapKeys.KEY_CLEANING_MODE);
				LogHelper.log(TAG, "Cleaning Mode : " + cleaningMode);
			}
			else {
				LogHelper.log(TAG, "Cleaning Mode NOT FOUND");
			}

			schedule = new BasicScheduleEvent2(eventId, day, startTime, cleaningMode);
		} catch (JSONException e) {
			LogHelper.log(TAG, "Exception in jsonToSchedule", e);
		}
		return schedule;
	}
	
	
	public static ScheduleEvent jsonToSchedule(JSONObject jsonObject, String eventId, int scheduleType) {
		if (scheduleType == SchedulerConstants2.SCHEDULE_TYPE_ADVANCED) {
			return jsonToAdvancedSchedule(jsonObject, eventId);
		} else if (scheduleType == SchedulerConstants2.SCHEDULE_TYPE_BASIC) {
			return jsonToBasicSchedule(jsonObject, eventId);
		}
		return null;
	}
}
