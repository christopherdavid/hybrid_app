package com.neatorobotics.android.slide.framework.pluginhelper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedRobotSchedule;
import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedScheduleGroup;
import com.neatorobotics.android.slide.framework.robot.schedule.ScheduleTimeObject;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants.Day;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants.SchedularEvent;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants;


public class ScheduleJsonDataHelper {

	private static final String TAG = ScheduleJsonDataHelper.class.getSimpleName();
	public static AdvancedRobotSchedule jsonToSchedule(JSONObject jsonObject) {
		AdvancedRobotSchedule schedule = null;
		try {
			JSONArray dayArray = jsonObject.getJSONArray(JsonMapKeys.KEY_DAY);
			ArrayList<Day> days = new ArrayList<SchedulerConstants.Day>();
			
			for (int dayiterator =0 ; dayiterator<dayArray.length();dayiterator++) {
				int day = dayArray.getInt(dayiterator);
				Day dayMap = SchedulerConstants.detrmineDay(day);
				days.add(dayMap);
			}
			
			String startTimeStr = jsonObject.getString(JsonMapKeys.KEY_START_TIME);
			String endTImeStr = jsonObject.getString(JsonMapKeys.KEY_END_TIME);
			int eventType = jsonObject.getInt(JsonMapKeys.KEY_EVENT_TYPE);
			SchedularEvent event = SchedulerConstants.detrmineEvent(eventType);
			String area = jsonObject.getString(JsonMapKeys.KEY_AREA);
			ScheduleTimeObject startTime = new ScheduleTimeObject(startTimeStr);
			ScheduleTimeObject endTime = new ScheduleTimeObject(endTImeStr);
			schedule = new AdvancedRobotSchedule(days, startTime, endTime, area, event);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			LogHelper.log(TAG, "Exception in jsonToSchedule", e);
		}
		return schedule;
	}

	public static AdvancedScheduleGroup jsonToScheduleGroup(JSONArray scheduleArray) {
		AdvancedScheduleGroup scheduleGroup = new AdvancedScheduleGroup();
		for (int scheduleArrayIterator=0; scheduleArrayIterator<scheduleArray.length(); scheduleArrayIterator++) {
			try {
				JSONObject scheduleObject = scheduleArray.getJSONObject(scheduleArrayIterator);
				AdvancedRobotSchedule currentSchedule = jsonToSchedule(scheduleObject);
				scheduleGroup.addSchedule(currentSchedule);
			} catch (JSONException e) {
				LogHelper.logD(TAG, "Exception in jsonToScheduleGroup", e);
			}
		}
		return scheduleGroup;
	}
}
