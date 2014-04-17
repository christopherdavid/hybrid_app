package com.neatorobotics.android.slide.framework.plugins.requests.robot.command;

import java.util.HashMap;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robotdata.RobotDataManager;
import com.neatorobotics.android.slide.framework.robotdata.RobotProfileConstants;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;

public class SendRobotCommandRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        sendCommand2(mContext, jsonData, callbackId);
    }

    private void sendCommand2(Context context, RobotJsonData jsonData, String callbackId) {
        LogHelper.logD(TAG, "Send command action initiated in Robot plugin " + jsonData.toString());
        int commandId = jsonData.getInt(JsonMapKeys.KEY_COMMAND_ID);
        String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
        JSONObject commandParams = jsonData.getJsonObject(JsonMapKeys.KEY_COMMAND_PARAMETERS);
        HashMap<String, String> commadParamsMap = CommandRequestUtils.getCommandParams(commandParams);
        sendCommandHelper(context, robotId, commandId, commadParamsMap, callbackId);
    }

    // Private helper method to create the Robot JSON
    private void sendCommandHelper(Context context, String robotId, int commandId, HashMap<String, String> params,
            String callbackId) {
        // Create Robot state notification listener to notify when we get a
        // robot state info
        // from the robot
        if (RobotProfileConstants.isCommandSendViaServer(commandId)) {
            LogHelper.logD(TAG, "Sending command VIA webservice");
            RobotDataManager.sendRobotCommand(context, robotId, commandId, params,
                    new RobotSetProfileDataRequestListener(callbackId));
            return;
        }
        // TODO: This is NOT applicable now. Remove this call.
        RobotCommandServiceManager.sendCommandThroughXmpp(context, robotId, commandId, params);
        PluginResult pluginStartResult = new PluginResult(PluginResult.Status.OK);
        pluginStartResult.setKeepCallback(false);
        sendSuccessPluginResult(pluginStartResult, callbackId);
    }

}
