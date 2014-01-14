package com.neatorobotics.android.slide.framework.plugins.requests.robot.command;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.settings.CleaningSettings;
import com.neatorobotics.android.slide.framework.robot.settings.CleaningSettingsListener;
import com.neatorobotics.android.slide.framework.robot.settings.SettingsManager;

public class GetRobotCleaningCategoryRequest  extends RobotManagerRequest {
	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		getRobotCleaningCategory(mContext, jsonData, callbackId);
	}
	
	private void getRobotCleaningCategory(Context context, RobotJsonData jsonData,
			final String callbackId) {
		LogHelper.logD(TAG, "getRobotCleaningCategory action initiated in Robot plugin");	
		final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
		LogHelper.logD(TAG, "Params\nRobotId=" + robotId);
		
		// TODO: Should be retrieved from the server.
		SettingsManager.getInstance(context).getCleaningSettings(robotId, new CleaningSettingsListener() {				
			@Override
			public void onSuccess(CleaningSettings cleaningSettings) {
				LogHelper.log(TAG, "Successfully got the cleaningSettings"+ cleaningSettings.getCleaningCategory() + "Spot size :" + cleaningSettings.getSpotAreaHeight());
				JSONObject cleaningCategoryJsonObj = getCleaningCategoryJsonObject(cleaningSettings, robotId);
				if (cleaningCategoryJsonObj != null) {
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, cleaningCategoryJsonObj);
					pluginResult.setKeepCallback(false);
					sendSuccessPluginResult(pluginResult, callbackId);
				}
				else {
					LogHelper.log(TAG, "Unable to get cleaning category JSON object");
					sendError(callbackId, ErrorTypes.JSON_CREATION_ERROR, "Unable to get cleaning category JSON object");
				}
			}

			@Override
			public void onError() {
				LogHelper.log(TAG, "Unable to get robot cleaning category");
				sendError(callbackId, ErrorTypes.ERROR_DB_ERROR, "Unable to get robot cleaning category");
			}
		});
	}
	

	private JSONObject getCleaningCategoryJsonObject(CleaningSettings cleaningSettings, String robotId) {
		JSONObject cleaningCategoryJsonObj = null;
		try {
			LogHelper.log(TAG, "Cleaning Category from Settings "+ cleaningSettings.getCleaningCategory());
			cleaningCategoryJsonObj = new JSONObject();
			cleaningCategoryJsonObj.put(JsonMapKeys.KEY_CLEANING_CATEGORY, cleaningSettings.getCleaningCategory());
			cleaningCategoryJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, robotId);				
		}
		catch (JSONException e) {
			LogHelper.logD(TAG, "Exception in getCleaningCategoryJsonObject", e);
		}
		
		return cleaningCategoryJsonObj;	
	}
}
