package com.neatorobotics.android.slide.framework.plugins.requests.robot.command;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.settings.CleaningSettings;
import com.neatorobotics.android.slide.framework.robot.settings.CleaningSettingsListener;
import com.neatorobotics.android.slide.framework.robot.settings.SettingsManager;

public class SetSpotDefinationRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        setSpotDefinition(mContext, jsonData, callbackId);
    }

    private void setSpotDefinition(Context context, RobotJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "setSpotDefinition action initiated in Robot plugin");
        String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
        int spotCleaningAreaLength = 0;
        int spotCleaningAreaHeight = 0;

        try {
            if (!TextUtils.isEmpty(jsonData.getString(JsonMapKeys.KEY_SPOT_CLEANING_AREA_LENGTH))) {
                spotCleaningAreaLength = Integer.valueOf(jsonData.getString(JsonMapKeys.KEY_SPOT_CLEANING_AREA_LENGTH));
            }
            if (!TextUtils.isEmpty(jsonData.getString(JsonMapKeys.KEY_SPOT_CLEANING_AREA_HEIGHT))) {
                spotCleaningAreaHeight = Integer.valueOf(jsonData.getString(JsonMapKeys.KEY_SPOT_CLEANING_AREA_HEIGHT));
            }

            LogHelper.logD(TAG, "Params\n\tRobotId= " + robotId + "\n\tSpot Area Length = " + spotCleaningAreaLength
                    + "\n\tSpot Area Height = " + spotCleaningAreaHeight);

            SettingsManager.getInstance(context).updateSpotDefinition(robotId, spotCleaningAreaLength,
                    spotCleaningAreaHeight, new CleaningSettingsListener() {
                        @Override
                        public void onSuccess(CleaningSettings cleaningSettings) {
                            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
                            pluginResult.setKeepCallback(false);
                            sendSuccessPluginResult(pluginResult, callbackId);
                        }

                        @Override
                        public void onError() {
                            LogHelper.log(TAG, "Unable to update robot spot definition");
                            sendError(callbackId, ErrorTypes.ERROR_DB_ERROR, "Unable to update robot spot definition");
                        }
                    });
        } catch (NumberFormatException e) {
            LogHelper.logD(TAG, "Exception in setSpotDefinition", e);
            sendError(callbackId, ErrorTypes.INVALID_PARAMETER, "Invalid spot definition parameters specified");
        }
    }

}
