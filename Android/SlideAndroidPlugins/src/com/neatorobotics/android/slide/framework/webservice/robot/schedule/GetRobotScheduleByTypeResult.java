package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class GetRobotScheduleByTypeResult extends NeatoWebserviceResult {
    public static final String TAG = GetRobotScheduleByTypeResult.class.getSimpleName();

    @Override
    public boolean success() {
        return ((status == RESPONSE_STATUS_SUCCESS) && (result != null));
    }

    public List<Result> result;

    public static class Result {
        public String schedule_id;
        public String schedule_type;
        public String schedule_version;
        public String schedule_data;

        public String getScheduleUID() {
            String scheduleUID = "";
            try {
                JSONObject scheduleDataObj = new JSONObject(schedule_data);
                JSONObject scheduleGroup = scheduleDataObj.optJSONObject(JsonMapKeys.KEY_SCHEDULE_GROUP);
                scheduleUID = scheduleGroup.optString(JsonMapKeys.KEY_SCHEDULE_UUID);
            } catch (JSONException ex) {
                LogHelper.logD(TAG, "JSONException in getScheduleUID");
            }

            return scheduleUID;
        }

        public JSONArray toEventIDJsonArray() {
            JSONArray array = new JSONArray();
            try {
                JSONObject scheduleDataObj = new JSONObject(schedule_data);
                JSONObject scheduleGroup = scheduleDataObj.optJSONObject(JsonMapKeys.KEY_SCHEDULE_GROUP);
                JSONArray events = scheduleGroup.optJSONArray(JsonMapKeys.KEY_EVENTS);
                int length = events.length();
                for (int index = 0; index < length; index++) {
                    String scheduleEventId = events.getJSONObject(index).getString(JsonMapKeys.KEY_SCHEDULE_EVENT_ID);
                    array.put(scheduleEventId);
                }
            } catch (JSONException ex) {
                LogHelper.logD(TAG, "JSONException in toEventsJsonArray");
            }

            return array;
        }
    }

    public boolean isRobotScheduleAvailable() {
        if ((result != null) && (result.size() > 0)) {
            return true;
        }

        return false;
    }

    public Result getScheduleData() {
        if (isRobotScheduleAvailable()) {
            return result.get(0);
        }

        return null;
    }
}
