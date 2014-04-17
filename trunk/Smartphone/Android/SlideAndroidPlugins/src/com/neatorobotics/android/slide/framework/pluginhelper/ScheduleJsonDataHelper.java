package com.neatorobotics.android.slide.framework.pluginhelper;

import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.schedule.BasicScheduleEvent;
import com.neatorobotics.android.slide.framework.robot.schedule.ScheduleEvent;
import com.neatorobotics.android.slide.framework.robot.schedule.ScheduleTimeObject;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants.Day;

public class ScheduleJsonDataHelper {

    private static final String TAG = ScheduleJsonDataHelper.class.getSimpleName();

    private static BasicScheduleEvent jsonToBasicSchedule(JSONObject jsonObject, String eventId) {
        BasicScheduleEvent schedule = null;
        try {
            int dayInt = jsonObject.getInt(JsonMapKeys.KEY_DAY);
            Day day = SchedulerConstants.determineDay(dayInt);
            // If "cleaning mode" is not provided in the JSON then we use
            // default cleaning mode
            String cleaningMode = String.valueOf(SchedulerConstants.CLEANING_MODE_NORMAL); // default
                                                                                           // to
                                                                                           // normal
                                                                                           // mode

            String startTimeStr = jsonObject.getString(JsonMapKeys.KEY_START_TIME);
            ScheduleTimeObject startTime = new ScheduleTimeObject(startTimeStr);

            if (!jsonObject.isNull(JsonMapKeys.KEY_CLEANING_MODE)) {
                cleaningMode = jsonObject.getString(JsonMapKeys.KEY_CLEANING_MODE);
                LogHelper.log(TAG, "Cleaning Mode : " + cleaningMode);
            } else {
                LogHelper.log(TAG, "Cleaning Mode NOT FOUND");
            }

            schedule = new BasicScheduleEvent(eventId, day, startTime, cleaningMode);
        } catch (JSONException e) {
            LogHelper.log(TAG, "Exception in jsonToSchedule", e);
        }
        return schedule;
    }

    public static ScheduleEvent jsonToSchedule(JSONObject jsonObject, String eventId, int scheduleType) {
        if (scheduleType == SchedulerConstants.SCHEDULE_TYPE_BASIC) {
            return jsonToBasicSchedule(jsonObject, eventId);
        }
        return null;
    }
}
