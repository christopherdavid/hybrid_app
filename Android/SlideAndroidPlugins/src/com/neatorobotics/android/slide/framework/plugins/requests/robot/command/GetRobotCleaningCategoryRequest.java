package com.neatorobotics.android.slide.framework.plugins.requests.robot.command;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robotdata.RobotProfileDataUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;

public class GetRobotCleaningCategoryRequest extends RobotManagerRequest {
	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		getRobotCleaningCategory(mContext, jsonData, callbackId);
	}

	private void getRobotCleaningCategory(final Context context, RobotJsonData jsonData,
			final String callbackId) {
		LogHelper.logD(TAG, "getRobotCleaningCategory action initiated in Robot plugin");	
		final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
		LogHelper.logD(TAG, "Params\nRobotId=" + robotId);
		
		
		RobotManager.getInstance(context).getRobotCleaningStateDetails(context, robotId, new RobotRequestListenerWrapper(callbackId) {
			@Override
			public JSONObject getResultObject(
					NeatoWebserviceResult responseResult)
					throws JSONException {
				JSONObject jsonResult = new JSONObject();
				if((responseResult != null) && (responseResult instanceof GetRobotProfileDetailsResult2)) {
					GetRobotProfileDetailsResult2 result = (GetRobotProfileDetailsResult2) responseResult;
					int cleaningCategory = RobotProfileDataUtils.getRobotCleaningCategory(context, result);
					jsonResult = getCleaningCategoryJsonObject(cleaningCategory, robotId);
				}
				return jsonResult;
			}
		});
	}

	private JSONObject getCleaningCategoryJsonObject(int cleaningCategory,
			String robotId) {
		JSONObject cleaningCategoryJsonObj = null;
		try {
			cleaningCategoryJsonObj = new JSONObject();
			cleaningCategoryJsonObj.put(JsonMapKeys.KEY_CLEANING_CATEGORY,
					cleaningCategory);
			cleaningCategoryJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
		} catch (JSONException e) {
			LogHelper
					.logD(TAG, "Exception in getCleaningCategoryJsonObject", e);
		}

		return cleaningCategoryJsonObj;
	}
}
