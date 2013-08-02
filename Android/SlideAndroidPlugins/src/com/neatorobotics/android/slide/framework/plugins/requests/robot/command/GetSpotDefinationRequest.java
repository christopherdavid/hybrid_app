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

public class GetSpotDefinationRequest extends RobotManagerRequest {

	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		getSpotDefination(mContext, jsonData, callbackId);
	}

	private void getSpotDefination(Context context, RobotJsonData jsonData,
			final String callbackId) {
		LogHelper.logD(TAG, "getSpotDefination action initiated in Robot plugin");	
		String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
		LogHelper.logD(TAG, "Params\nRobotId=" + robotId);
		
		SettingsManager.getInstance(context).getCleaningSettings(robotId, new CleaningSettingsListener() {				
			@Override
			public void onSuccess(CleaningSettings cleaningSettings) {
				JSONObject spotDefinitionJsonObj = getSpotDefinationJsonObject(cleaningSettings);
				if (spotDefinitionJsonObj != null) {
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, spotDefinitionJsonObj);
					pluginResult.setKeepCallback(false);
					sendSuccessPluginResult(pluginResult, callbackId);
				}
				else {
					LogHelper.log(TAG, "Unable to get spot defination JSON object");
					sendError(callbackId, ErrorTypes.JSON_CREATION_ERROR, "Unable to get spot definition JSON object");
				}
			}

			@Override
			public void onError() {
				LogHelper.log(TAG, "Unable to get robot spot definition");
				sendError(callbackId, ErrorTypes.ERROR_DB_ERROR, "Unable to get robot spot definition");
			}
		});
	}
	

	private JSONObject getSpotDefinationJsonObject(CleaningSettings cleaningSettings) {
		JSONObject spotDefinitionJsonObj = null;
		try {
			spotDefinitionJsonObj = new JSONObject();
			spotDefinitionJsonObj.put(JsonMapKeys.KEY_SPOT_CLEANING_AREA_LENGTH, cleaningSettings.getSpotAreaLength());
			spotDefinitionJsonObj.put(JsonMapKeys.KEY_SPOT_CLEANING_AREA_HEIGHT, cleaningSettings.getSpotAreaHeight());				
		}
		catch (JSONException e) {
			LogHelper.logD(TAG, "Exception in getSpotDefinitionJsonObject", e);
		}
		
		return spotDefinitionJsonObj;	
	}
}
