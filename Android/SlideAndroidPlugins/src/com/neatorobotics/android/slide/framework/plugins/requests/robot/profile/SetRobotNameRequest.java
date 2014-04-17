package com.neatorobotics.android.slide.framework.plugins.requests.robot.profile;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;

public class SetRobotNameRequest extends RobotManagerRequest {
    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        setRobotName(mContext, jsonData, callbackId);
    }

    private void setRobotName(final Context context, RobotJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "setRobotName2 action initiated in Robot plugin");
        final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
        final String robotName = jsonData.getString(JsonMapKeys.KEY_ROBOT_NAME);

        RobotManager.getInstance(context).setRobotName(robotId, robotName, new RobotRequestListenerWrapper(callbackId) {

            @Override
            public void onReceived(NeatoWebserviceResult responseResult) {
                JSONObject robotJsonObj = getRobotDetailJsonObject(robotId, robotName);
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, robotJsonObj);
                pluginResult.setKeepCallback(false);
                sendSuccessPluginResult(pluginResult, callbackId);
            }
        });
    }

    protected JSONObject getRobotDetailJsonObject(String robotId, String robotName) {
        JSONObject robotJsonObj = new JSONObject();
        try {
            robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
            robotJsonObj.put(JsonMapKeys.KEY_ROBOT_NAME, robotName);
        } catch (JSONException e) {
            LogHelper.logD(TAG, "Exception in getRobotDetailJsonObject", e);
        }
        return robotJsonObj;
    }
}
