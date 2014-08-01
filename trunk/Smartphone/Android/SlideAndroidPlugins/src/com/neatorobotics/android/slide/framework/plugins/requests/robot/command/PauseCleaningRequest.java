package com.neatorobotics.android.slide.framework.plugins.requests.robot.command;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;

public class PauseCleaningRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        sendPauseCleaningCommand(mContext, jsonData, callbackId);
    }

    private void sendPauseCleaningCommand(final Context context, RobotJsonData jsonData, final String callbackId) {
        String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
        JSONObject commandParams = jsonData.getJsonObject(JsonMapKeys.KEY_COMMAND_PARAMETERS);
        HashMap<String, String> commadParamsMap = CommandRequestUtils.getCommandParams(commandParams);
        LogHelper.logD(TAG, "CommandTrip: sendCommand2 - COMMAND_PAUSE");
        int commandId = RobotCommandPacketConstants.COMMAND_PAUSE_CLEANING;
        sendCommand(context, callbackId, robotId, commadParamsMap, commandId);
    }
}
