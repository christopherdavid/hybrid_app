package com.neatorobotics.android.slide.framework.plugins.requests.robot.profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.SetRobotProfileDetailsResult3;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.RobotSchedulerManager;

public class EnableScheduleRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        enableSchedule(mContext, jsonData, callbackId);
    }

    // Private helper method to return the schedule enable/disable state.
    private void enableSchedule(Context context, RobotJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "enableSchedule called");
        final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
        final int scheduleType = jsonData.getInt(JsonMapKeys.KEY_SCHEDULE_TYPE);
        final boolean enableSchedule = jsonData.getBoolean(JsonMapKeys.KEY_ENABLE_SCHEDULE);

        final int scheduleTypeOnServer = SchedulerConstants.convertToServerConstants(scheduleType);
        RobotSchedulerManager.getInstance(context).setEnableSchedule(robotId, scheduleTypeOnServer, enableSchedule,
                new RobotRequestListenerWrapper(callbackId) {

                    @Override
                    public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
                        JSONObject jsonResult;
                        if ((responseResult != null) && (responseResult instanceof SetRobotProfileDetailsResult3)) {
                            jsonResult = new JSONObject();
                            jsonResult.put(JsonMapKeys.KEY_IS_SCHEDULE_ENABLED, enableSchedule);
                            jsonResult.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleType);
                            jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
                        } else {
                            jsonResult = getErrorJsonObject(ErrorTypes.ERROR_TYPE_UNKNOWN,
                                    "response result is not of type set profile details result");
                        }
                        return jsonResult;
                    }
                });
    }

}
