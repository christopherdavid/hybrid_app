package com.neatorobotics.android.slide.framework.plugins.requests.robot.manual;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.drive.RobotDriveHelper;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;

public class StopRobotDriveRequest extends RobotManagerRequest {
	
	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		stopRobotDrive(mContext, jsonData, callbackId);
	}
	
	private void stopRobotDrive(final Context context, RobotJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "stopRobotDrive is called");
		final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
		
		// If no connection exits, send error result.
		if (!RobotCommandServiceManager.isRobotDirectConnected(context, robotId)) {
			sendError(callbackId, ErrorTypes.ROBOT_NOT_CONNECTED, "Robot is not connected");
			return;
		}
		// Disconnect the robot connection and send success plugin result.
		RobotDriveHelper.getInstance(context).stopRobotDrive(robotId);
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
		pluginResult.setKeepCallback(false);
		sendSuccessPluginResult(pluginResult, callbackId);
	}
}
