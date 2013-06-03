package com.neatorobotics.android.slide.framework.robot.schedule2;


import org.json.JSONException;
import org.json.JSONObject;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2.Day;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2.SchedularEvent;

public class AdvancedScheduleEvent2 implements ScheduleEvent {

	private static final String TAG = AdvancedScheduleEvent2.class.getSimpleName();
	private String mEventId;
	private Day mDay;
	private ScheduleTimeObject2 mStartTime;
	private ScheduleTimeObject2 mEndTime;
	private String mArea;
	private SchedularEvent mEvent;

	public AdvancedScheduleEvent2(String eventId) {
		mEventId = eventId;
	}
	
	public AdvancedScheduleEvent2(String eventId, Day day, ScheduleTimeObject2 startTime, ScheduleTimeObject2 endTime, String area, SchedularEvent event) {
		mEventId = eventId;
		mDay = day;
		mStartTime = startTime;
		mEndTime = endTime;
		mArea = area;
		mEvent = event;
	}

	public JSONObject toJsonObject() {
		JSONObject schedule = new JSONObject();		
		try {
			//Put day 
			schedule.put(JsonMapKeys.KEY_DAY, mDay);

			//Put start-time and end-time HH:MM
			schedule.put(JsonMapKeys.KEY_START_TIME, mStartTime.toString());
			schedule.put(JsonMapKeys.KEY_END_TIME,mEndTime.toString());

			//Put event type. 0 for Quiet and 1 for Clean
			schedule.put(JsonMapKeys.KEY_EVENT_TYPE, mEvent.ordinal());

			//Put Area String
			schedule.put(JsonMapKeys.KEY_AREA, mArea);
		} 
		catch (JSONException e) {
			LogHelper.log(TAG, "Exception in toJsonObject", e);
		}
		return schedule;
	}

	public void setDay(Day day) {
		mDay = day;
	}
	
	public void setEventType(SchedularEvent event) {
		mEvent = event;
	}

	public void setStartScheduleTime(ScheduleTimeObject2 time) {
		mStartTime = time;
	}

	public void setEndScheduleTime(ScheduleTimeObject2 time) {
		mEndTime = time;
	}
	public void setArea(String area) {
		mArea = area;		
	}

	public String getStartScheduleTimeStr() {
		return mStartTime.toString();
	}

	public String getEndScheduleTimeStr() {
		return mEndTime.toString();
	}
	
	public String getScheduleAreaStr() {
		return mArea;
	}

	public String getEventId() {
		return mEventId;
	}
}
