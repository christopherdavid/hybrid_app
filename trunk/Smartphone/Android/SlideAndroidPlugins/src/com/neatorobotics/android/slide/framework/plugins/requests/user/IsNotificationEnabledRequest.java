package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.robot.settings.SettingsManager;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.user.settings.RobotNotificationSettingsResult;

public class IsNotificationEnabledRequest extends UserManagerRequest {

    @Override
    public void execute(JSONArray data, String callbackId) {
        UserJsonData jsonData = new UserJsonData(data);
        isNotificationEnabled(mContext, jsonData, callbackId);
    }

    private void isNotificationEnabled(Context context, UserJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "isNotificationEnabled action initiated in Robot plugin");

        String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
        final String notificationId = jsonData.getString(JsonMapKeys.KEY_NOTIFICATION_ID);
        // TODO: Add notificationID validation check
        LogHelper.logD(TAG, String.format("Email = %s NotificationId = %s", email, notificationId));

        SettingsManager.getInstance(context).getNotificationSettings(email, new UserRequestListenerWrapper(callbackId) {
            @Override
            public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
                JSONObject notificationSetting = new JSONObject();
                if (responseResult instanceof RobotNotificationSettingsResult) {
                    RobotNotificationSettingsResult result = (RobotNotificationSettingsResult) responseResult;
                    notificationSetting.put(JsonMapKeys.KEY_NOTIFICATION_KEY, notificationId);
                    notificationSetting.put(JsonMapKeys.KEY_NOTIFICATION_VALUE,
                            result.isNotificationEnable(notificationId));
                }

                return notificationSetting;
            }
        });
    }

}
