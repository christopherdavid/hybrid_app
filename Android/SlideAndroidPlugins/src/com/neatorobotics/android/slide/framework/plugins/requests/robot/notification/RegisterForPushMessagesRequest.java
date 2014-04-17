package com.neatorobotics.android.slide.framework.plugins.requests.robot.notification;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;

import com.neatorobotics.android.slide.framework.gcm.PushNotificationConstants;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationListener;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationMessageHandler;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.utils.AppUtils;

public class RegisterForPushMessagesRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        registerForRobotsMessages(mContext, jsonData, callbackId);
    }

    private void registerForRobotsMessages(Context context, RobotJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "registerForRobotsMessages called");
        PushNotificationMessageHandler.getInstance(context).addPushNotificationListener(
                new RobotPushNotificationListener(callbackId));
    }

    private class RobotPushNotificationListener implements PushNotificationListener {
        private String mCallbackId;

        public RobotPushNotificationListener(String callbackId) {
            mCallbackId = callbackId;
        }

        @Override
        public void onShowPushNotification(Bundle bundle) {
            LogHelper.log(TAG, "onShowPushNotification: " + bundle);
            JSONObject jsonObject = convertBundleToJsonObject(bundle);
            PluginResult pluginPushNotificationResult = new PluginResult(PluginResult.Status.OK, jsonObject);
            pluginPushNotificationResult.setKeepCallback(true);
            sendSuccessPluginResult(pluginPushNotificationResult, mCallbackId);
        }
    }

    private static final JSONObject convertBundleToJsonObject(Bundle bundle) {
        JSONObject jsonObject = new JSONObject();
        // bundle.

        if (bundle != null) {
            String notificationId = bundle.getString(PushNotificationConstants.NOTIFICATION_ID_KEY);
            String message = bundle.getString(PushNotificationConstants.NOTIFICATION_MESSAGE_KEY);
            String robotId = bundle.getString(PushNotificationConstants.ROBOT_ID_KEY);
            AppUtils.addToJsonObjectIfNotEmpty(jsonObject, PushNotificationConstants.NOTIFICATION_ID_KEY,
                    notificationId);
            AppUtils.addToJsonObjectIfNotEmpty(jsonObject, PushNotificationConstants.NOTIFICATION_MESSAGE_KEY, message);
            AppUtils.addToJsonObjectIfNotEmpty(jsonObject, PushNotificationConstants.ROBOT_ID_KEY, robotId);
        }

        return jsonObject;
    }

}
