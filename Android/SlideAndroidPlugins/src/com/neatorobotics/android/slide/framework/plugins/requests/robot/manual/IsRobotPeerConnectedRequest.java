package com.neatorobotics.android.slide.framework.plugins.requests.robot.manual;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;

public class IsRobotPeerConnectedRequest extends RobotManagerRequest {
	
	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		isRobotPeerConnected(mContext, jsonData, callbackId);
	}
	
	private void isRobotPeerConnected(final Context context, RobotJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "isRobotPeerConnected is called");
		// If nothing exists, this API returns an empty robotId: ""
		final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
		boolean isPeerConnected = false;
		if (TextUtils.isEmpty(robotId)) {
			isPeerConnected = RobotCommandServiceManager.isDirectConnectionExists(context);
		}
		else {
			isPeerConnected = RobotCommandServiceManager.isRobotDirectConnected(context, robotId);
		}
		JSONObject jsonResult = new JSONObject();
		try {
			jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
			jsonResult.put(JsonMapKeys.KEY_IS_PEER_CONNECTED, isPeerConnected);
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonResult);
			pluginResult.setKeepCallback(false);
			sendSuccessPluginResult(pluginResult, callbackId);
		} catch (JSONException e) {
			LogHelper.logD(TAG, "JSON Error");
			sendError(callbackId, ErrorTypes.JSON_PARSING_ERROR, e.getMessage());
		}
	}
}
