package com.neatorobotics.android.slide.framework.plugins.requests.robot.command;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;

public class SendRobotCommandRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        sendCommand2(mContext, jsonData, callbackId);
    }

    private void sendCommand2(Context context, RobotJsonData jsonData, String callbackId) {
        LogHelper.logD(TAG, "CommandTrip: Send command action initiated in Robot plugin " + jsonData.toString());
        int commandId = jsonData.getInt(JsonMapKeys.KEY_COMMAND_ID);
        String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
        JSONObject commandParams = jsonData.getJsonObject(JsonMapKeys.KEY_COMMAND_PARAMETERS);
        HashMap<String, String> commadParamsMap = CommandRequestUtils.getCommandParams(commandParams);
        sendCommand(context, callbackId, robotId, commadParamsMap, commandId);
    }
}
