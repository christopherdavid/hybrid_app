package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.robot.settings.SettingsManager;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.user.settings.RobotNotificationSettingsResult;

public class GetNotificationSettingsRequest extends UserManagerRequest {

    @Override
    public void execute(JSONArray data, String callbackId) {
        UserJsonData jsonData = new UserJsonData(data);
        getNotificationSettings(mContext, jsonData, callbackId);
    }

    public void getNotificationSettings(final Context context, final UserJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "getNotificationSettings action initiated in Robot plugin");

        final String emailId = jsonData.getString(JsonMapKeys.KEY_EMAIL);
        LogHelper.logD(TAG, String.format("Email = %s", emailId));

        SettingsManager.getInstance(context).getNotificationSettings(emailId,
                new UserRequestListenerWrapper(callbackId) {

                    @Override
                    public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
                        JSONObject notificationSettings = null;
                        if (responseResult instanceof RobotNotificationSettingsResult) {
                            RobotNotificationSettingsResult result = (RobotNotificationSettingsResult) responseResult;
                            notificationSettings = result.getNotificationsJson();
                        }

                        return notificationSettings;
                    }

                    // Send default notification settings.
                    @Override
                    public void onServerError(int errorCode, String errorMessage) {
                        LogHelper.logD(TAG, String.format("Server Message = %s ", errorMessage));
                        JSONObject notificationSettings = RobotHelper.getDefaultSettings();
                        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, notificationSettings);
                        sendSuccessPluginResult(pluginResult, mCallbackId);
                    }
                });
    }
}
