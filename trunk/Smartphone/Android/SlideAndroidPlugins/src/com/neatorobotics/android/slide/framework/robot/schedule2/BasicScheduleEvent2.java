package com.neatorobotics.android.slide.framework.robot.schedule2;


import org.json.JSONException;
import org.json.JSONObject;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2.Day;

public class BasicScheduleEvent2 implements ScheduleEvent {
	private static final String TAG = AdvancedScheduleEvent2.class.getSimpleName();
	private String mEventId;
	private Day mDay;
	private ScheduleTimeObject2 mStartTime;
	private String mCleaningMode;

	public BasicScheduleEvent2(String eventId) {	
		mEventId = eventId;
		mCleaningMode = String.valueOf(SchedulerConstants2.CLEANING_MODE_NORMAL);
	}
	
	public BasicScheduleEvent2(String eventId, Day day, ScheduleTimeObject2 startTime, 
			String cleaningMode) {
		mEventId = eventId;
		mDay = day;
		mStartTime = startTime;
		mCleaningMode = cleaningMode;
	}


	public JSONObject toJsonObject() {
		JSONObject schedule = new JSONObject();		
		try {
			//Put day array
			schedule.put(JsonMapKeys.KEY_DAY, mDay.ordinal());
			//Put start-time HH:MM
			schedule.put(JsonMapKeys.KEY_START_TIME, mStartTime.toString());
			// Put cleaning mode			
			schedule.put(JsonMapKeys.KEY_CLEANING_MODE, mCleaningMode);
		} 
		catch (JSONException e) {
			LogHelper.log(TAG, "Exception in toJsonObject", e);
		}
		return schedule;
	}
	
	public JSONObject toJson() {
		JSONObject eventObj = new JSONObject();		
		try {
			//Put day array
			eventObj.put(JsonMapKeys.KEY_DAY, mDay.ordinal());
			//Put start-time HH:MM
			eventObj.put(JsonMapKeys.KEY_START_TIME, mStartTime.toString());
			// Put cleaning mode			
			eventObj.put(JsonMapKeys.KEY_CLEANING_MODE, mCleaningMode);
			// Put event id			
			eventObj.put(JsonMapKeys.KEY_SCHEDULE_EVENT_ID, mEventId);
		} 
		catch (JSONException e) {
			LogHelper.log(TAG, "Exception in toJsonData", e);
		}
		return eventObj;
	}

	public void setDay(Day day) {
		mDay = day;
	}
	public void setStartScheduleTime(ScheduleTimeObject2 time) {
		mStartTime = time;
	}


	public String getStartScheduleTimeStr() {
		return mStartTime.toString();
	}

	public String getEventId() {
		return mEventId;		
	}

	public String getCleaningMode() {
		return mCleaningMode;
	}

	public void setCleaningMode(String cleaningMode) {
		mCleaningMode = cleaningMode;
	}
}
