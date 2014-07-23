package com.neatorobotics.android.slide.framework.plugins.requests.robot.command;

import java.util.HashMap;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.settings.CleaningSettings;
import com.neatorobotics.android.slide.framework.robotdata.RobotDataManager;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;

public class StartCleaningRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        sendStartCleaningCommand(mContext, jsonData, callbackId);
    }

    public void sendStartCleaningCommand(final Context context, RobotJsonData jsonData, final String callbackId) {

        String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
        JSONObject commandParams = jsonData.getJsonObject(JsonMapKeys.KEY_COMMAND_PARAMETERS);
        HashMap<String, String> commadParamsMap = CommandRequestUtils.getCommandParams(commandParams);
        LogHelper.logD(TAG, "sendCommand2 - COMMAND_ROBOT_START");
        int cleaningCategory = 0;

        if (!TextUtils.isEmpty(commadParamsMap.get(JsonMapKeys.KEY_CLEANING_CATEGORY))) {
            cleaningCategory = Integer.valueOf(commadParamsMap.get(JsonMapKeys.KEY_CLEANING_CATEGORY));
        }

        if (cleaningCategory == RobotCommandPacketConstants.CLEANING_CATEGORY_MANUAL) {
            boolean isDirectConnected = RobotCommandServiceManager.isRobotDirectConnected(context, robotId);
            if (!isDirectConnected) {
                LogHelper.log(TAG, "Manual cleaning cannot be started as direct connection does not exist");
                sendError(callbackId, ErrorTypes.ROBOT_NOT_CONNECTED, "Robot is not connected");
                return;
            }
            else {
				LogHelper.log(TAG, "Sending Peer to peer manual start cleaning");
                RobotCommandServiceManager.sendCommandToPeer(mContext, robotId, RobotCommandPacketConstants.COMMAND_ROBOT_START, commadParamsMap);
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
                pluginResult.setKeepCallback(false);
                sendSuccessPluginResult(pluginResult, callbackId);
                return;
            }
        }

        if (cleaningCategory == RobotCommandPacketConstants.CLEANING_CATEGORY_SPOT) {
            CleaningSettings cleaningSettings = RobotHelper.getCleaningSettings(context, robotId);
            if (cleaningSettings == null) {
                LogHelper.log(TAG, "Spot definition not set. Callback Id = " + callbackId);
                sendError(callbackId, ErrorTypes.INVALID_PARAMETER, "Spot Definition Not Set");
                return;
            }
            String spotAreaLength = String.valueOf(cleaningSettings.getSpotAreaLength());
            String spotAreaHeight = String.valueOf(cleaningSettings.getSpotAreaHeight());
            commadParamsMap.put(JsonMapKeys.KEY_SPOT_CLEANING_AREA_LENGTH, spotAreaLength);
            commadParamsMap.put(JsonMapKeys.KEY_SPOT_CLEANING_AREA_HEIGHT, spotAreaHeight);
        }
        RobotDataManager.sendRobotCommand(context, robotId, RobotCommandPacketConstants.COMMAND_ROBOT_START,
                commadParamsMap, new RobotSetProfileDataRequestListener(callbackId));
    }
}
