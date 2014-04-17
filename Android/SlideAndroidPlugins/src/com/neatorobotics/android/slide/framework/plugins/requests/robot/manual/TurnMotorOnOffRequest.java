package com.neatorobotics.android.slide.framework.plugins.requests.robot.manual;

import java.util.HashMap;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;

import com.neatorobotics.android.slide.framework.R;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;

public class TurnMotorOnOffRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        turnMotorOnOff(mContext, jsonData, callbackId);
    }

    private void turnMotorOnOff(Context context, RobotJsonData jsonData, String callbackId) {
        LogHelper.logD(TAG, "turn on/off motor called");
        String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
        String flagOn = jsonData.getString(JsonMapKeys.KEY_FLAG_ON);
        String motorType = String.valueOf(jsonData.getInt(JsonMapKeys.KEY_MOTOR_TYPE,
                RobotCommandPacketConstants.MOTOR_TYPE_VACUUM));

        if (!RobotCommandServiceManager.isRobotDirectConnected(context, robotId)) {
            LogHelper.logD(TAG, "turnMotorOnOff action cannot complete as robot connection does not exist");
            String errMessage = context.getString(R.string.error_robot_not_directly_connected);
            sendError(callbackId, ErrorTypes.ROBOT_NOT_CONNECTED, errMessage);
            return;
        }
        HashMap<String, String> commandParamsMap = new HashMap<String, String>();
        commandParamsMap.put(JsonMapKeys.KEY_FLAG_ON_OFF, flagOn);
        commandParamsMap.put(JsonMapKeys.KEY_MOTOR_TYPE, motorType);

        LogHelper.logD(TAG, "Direct connection exists. Send motor command.");
        RobotCommandServiceManager.sendCommandToPeer(context, robotId,
                RobotCommandPacketConstants.COMMAND_TURN_MOTOR_ONOFF, commandParamsMap);
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
        pluginResult.setKeepCallback(false);
        sendSuccessPluginResult(pluginResult, callbackId);
    }
}