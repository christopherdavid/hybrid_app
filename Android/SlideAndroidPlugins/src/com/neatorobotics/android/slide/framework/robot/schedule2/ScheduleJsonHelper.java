package com.neatorobotics.android.slide.framework.robot.schedule2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;

public class ScheduleJsonHelper {
	
	private static final String TAG = ScheduleJsonHelper.class.getSimpleName();
	
	public static BasicScheduleGroup2 readJsonToBasicSchedule(String jsonData) {
		BasicScheduleGroup2 scheduleGroup = null;
		
		try {
			// Process events of a Schedule
			JSONObject scheduleJsonObj = new JSONObject(jsonData);
			JSONObject scheduleGroupObj = scheduleJsonObj.optJSONObject(JsonMapKeys.KEY_SCHEDULE_GROUP);
			if (scheduleGroupObj != null) {
				String scheduleUID = scheduleGroupObj.optString(JsonMapKeys.KEY_SCHEDULE_UUID);
				if (!TextUtils.isEmpty(scheduleUID)) {
					scheduleGroup = new BasicScheduleGroup2(scheduleUID);				
				
					JSONArray events = scheduleGroupObj.optJSONArray(JsonMapKeys.KEY_EVENTS);
					if (events != null) {					
						int count = events.length();								
						for (int index = 0; index < count; index++) {
							JSONObject event = events.getJSONObject(index);								
							
							
							BasicScheduleEvent2 scheduleEvent = new BasicScheduleEvent2(event.optString(JsonMapKeys.KEY_SCHEDULE_EVENT_ID),
										SchedulerConstants2.detrmineDay(event.optInt(JsonMapKeys.KEY_DAY)),
										new ScheduleTimeObject2(event.optString(JsonMapKeys.KEY_START_TIME)),
										event.optString(JsonMapKeys.KEY_CLEANING_MODE));
							
							scheduleGroup.addSchedule(scheduleEvent);
						}
					}
				}
			}			
		}
		catch (JSONException ex) {
			LogHelper.logD(TAG, "JSONException in readJsonToBasicSchedule");			
		}
		
		return scheduleGroup;
	}
	
}
