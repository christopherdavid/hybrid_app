package com.neatorobotics.android.slide.framework.plugins.requests.robot.notification;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationUtil;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;

public class UnRegisterDataChangeNotificationRequest extends
		RobotManagerRequest {

	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		unregisterRobotNotifications2(mContext, jsonData, callbackId);
	}
	
	private void unregisterRobotNotifications2(Context context, RobotJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "unregisterRobotNotifications2 action initiated in Robot plugin");
		RobotNotificationUtil.removeRobotDataChangedListener(context);
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
		pluginResult.setKeepCallback(false);
		sendSuccessPluginResult(pluginResult, callbackId);
	}
}
