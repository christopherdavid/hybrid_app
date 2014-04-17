package com.neatorobotics.android.slide.framework.robot.schedule;

import org.json.JSONException;
import org.json.JSONObject;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants.Day;

public class BasicScheduleEvent implements ScheduleEvent {
    private static final String TAG = BasicScheduleEvent.class.getSimpleName();
    private String mEventId;
    private Day mDay;
    private ScheduleTimeObject mStartTime;
    private String mCleaningMode;

    public BasicScheduleEvent(String eventId) {
        mEventId = eventId;
        mCleaningMode = String.valueOf(SchedulerConstants.CLEANING_MODE_NORMAL);
    }

    public BasicScheduleEvent(String eventId, Day day, ScheduleTimeObject startTime, String cleaningMode) {
        mEventId = eventId;
        mDay = day;
        mStartTime = startTime;
        mCleaningMode = cleaningMode;
    }

    public JSONObject toJsonObject() {
        JSONObject schedule = new JSONObject();
        try {
            // Put day array
            schedule.put(JsonMapKeys.KEY_DAY, mDay.ordinal());
            // Put start-time HH:MM
            schedule.put(JsonMapKeys.KEY_START_TIME, mStartTime.toString());
            // Put cleaning mode
            schedule.put(JsonMapKeys.KEY_CLEANING_MODE, mCleaningMode);
        } catch (JSONException e) {
            LogHelper.log(TAG, "Exception in toJsonObject", e);
        }
        return schedule;
    }

    public JSONObject toJson() {
        JSONObject eventObj = new JSONObject();
        try {
            // Put day array
            eventObj.put(JsonMapKeys.KEY_DAY, mDay.ordinal());
            // Put start-time HH:MM
            eventObj.put(JsonMapKeys.KEY_START_TIME, mStartTime.toString());
            // Put cleaning mode
            eventObj.put(JsonMapKeys.KEY_CLEANING_MODE, mCleaningMode);
            // Put event id
            eventObj.put(JsonMapKeys.KEY_SCHEDULE_EVENT_ID, mEventId);
        } catch (JSONException e) {
            LogHelper.log(TAG, "Exception in toJsonData", e);
        }
        return eventObj;
    }

    public void setDay(Day day) {
        mDay = day;
    }

    public void setStartScheduleTime(ScheduleTimeObject time) {
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
