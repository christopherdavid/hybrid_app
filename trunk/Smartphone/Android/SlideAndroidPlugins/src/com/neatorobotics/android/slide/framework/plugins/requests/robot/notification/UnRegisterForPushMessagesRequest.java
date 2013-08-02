package com.neatorobotics.android.slide.framework.plugins.requests.robot.notification;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;

import com.neatorobotics.android.slide.framework.gcm.PushNotificationMessageHandler;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;

public class UnRegisterForPushMessagesRequest extends RobotManagerRequest {

	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		unregisterForRobotMessages(mContext, jsonData, callbackId);
	}
	
	private void unregisterForRobotMessages(Context context, RobotJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "unregisterForRobotMessages called");
		PushNotificationMessageHandler.getInstance(context).removePushNotificationListener();
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
		pluginResult.setKeepCallback(false);
		sendSuccessPluginResult(pluginResult, callbackId);
	}
}
