package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.robot.settings.SettingsManager;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

import android.content.Context;

public class TurnNotificationOnOffRequest extends UserManagerRequest {

	@Override
	public void execute(JSONArray data, String callbackId) {
		UserJsonData jsonData = new UserJsonData(data);
		turnNotificationOnOff(mContext, jsonData, callbackId);
	}

	// Enabling and disabling the notification settings on the server
	private void turnNotificationOnOff(final Context context,
			final UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG,
				"turnNotificationOnOff action initiated in Robot plugin");
		final String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);

		final String notificationId = jsonData
				.getString(JsonMapKeys.KEY_NOTIFICATION_ID);
		// TODO: Add notificationID validation check

		final boolean onOffFlag = jsonData.getBoolean(JsonMapKeys.KEY_FLAG_ON);

		LogHelper.logD(TAG, String.format(
				"Email = %s NotificationId = %s Enable = %s", email,
				notificationId, onOffFlag));

		SettingsManager.getInstance(context).updateNotificationState(email,
				notificationId, onOffFlag,
				new UserRequestListenerWrapper(callbackId) {
					@Override
					public JSONObject getResultObject(
							NeatoWebserviceResult responseResult)
							throws JSONException {
						JSONObject notificationSetting = new JSONObject();
						notificationSetting.put(
								JsonMapKeys.KEY_NOTIFICATION_KEY,
								notificationId);
						notificationSetting.put(
								JsonMapKeys.KEY_NOTIFICATION_VALUE, onOffFlag);
						return notificationSetting;
					}
				});
	}
}
