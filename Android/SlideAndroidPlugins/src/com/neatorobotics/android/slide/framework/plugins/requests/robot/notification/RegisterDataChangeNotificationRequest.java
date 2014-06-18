package com.neatorobotics.android.slide.framework.plugins.requests.robot.notification;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationUtil;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDataListener;

public class RegisterDataChangeNotificationRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        registerRobotNotifications2(mContext, jsonData, callbackId);
    }

    private void registerRobotNotifications2(Context context, RobotJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "registerRobotNotifications2 action initiated in Robot plugin");

        RobotNotificationUtil.addRobotDataChangedListener(context, new RobotDataListener() {
            @Override
            public void onDataReceived(String robotId, int dataCode, JSONObject data) {
                JSONObject robotData = RobotNotificationUtil.getNotificationObject(robotId, dataCode, data);
                if (robotData != null) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, robotData);
                    pluginResult.setKeepCallback(true);
                    sendSuccessPluginResult(pluginResult, callbackId);
                }

            }
        });
    }
}
