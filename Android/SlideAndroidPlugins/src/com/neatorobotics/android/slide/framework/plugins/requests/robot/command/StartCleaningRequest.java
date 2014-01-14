package com.neatorobotics.android.slide.framework.plugins.requests.robot.command;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.settings.CleaningSettings;
import com.neatorobotics.android.slide.framework.robotdata.RobotDataManager;

public class StartCleaningRequest extends RobotManagerRequest {

	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		sendStartCleaningCommand(mContext, jsonData, callbackId);
	}

	public void sendStartCleaningCommand(final Context context, RobotJsonData jsonData, final String callbackId) {
		
		String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
		JSONObject commandParams = jsonData.getJsonObject(JsonMapKeys.KEY_COMMAND_PARAMETERS);
		HashMap<String, String> commadParamsMap = CommandRequestUtils.getCommandParams(commandParams);
		LogHelper.logD(TAG, "sendCommand2 - COMMAND_ROBOT_START");
		int cleaningCategory = 0;
		
		if (!TextUtils.isEmpty(commadParamsMap.get(JsonMapKeys.KEY_CLEANING_CATEGORY))) {
			cleaningCategory = Integer.valueOf(commadParamsMap.get(JsonMapKeys.KEY_CLEANING_CATEGORY));
		}
		
		// TODO: Remove this once the phonegap test UI removes the code to send manual category in this command.
		if (cleaningCategory == RobotCommandPacketConstants.CLEANING_CATEGORY_MANUAL) {
			LogHelper.log(TAG, "Cleaning called with manual category. Send error result.");
			sendError(callbackId, ErrorTypes.ERROR_NOT_SUPPORTED, "This API does not supports Manual Cleaning");
			return;
		}
		
		if (cleaningCategory == RobotCommandPacketConstants.CLEANING_CATEGORY_SPOT) {
			CleaningSettings cleaningSettings = RobotHelper.getCleaningSettings(context, robotId);
			if (cleaningSettings == null) {
				LogHelper.log(TAG, "Spot definition not set. Callback Id = " + callbackId);
				sendError(callbackId, ErrorTypes.INVALID_PARAMETER, "Spot Definition Not Set");
				return;
			}
			String spotAreaLength = String.valueOf(cleaningSettings.getSpotAreaLength());
			String spotAreaHeight = String.valueOf(cleaningSettings.getSpotAreaHeight());
			commadParamsMap.put(JsonMapKeys.KEY_SPOT_CLEANING_AREA_LENGTH, spotAreaLength);
			commadParamsMap.put(JsonMapKeys.KEY_SPOT_CLEANING_AREA_HEIGHT, spotAreaHeight);
		}
		RobotDataManager.sendRobotCommand(context, robotId, RobotCommandPacketConstants.COMMAND_ROBOT_START, commadParamsMap  , new RobotSetProfileDataRequestListener(callbackId));
	}
}
