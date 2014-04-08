package com.neatorobotics.android.slide.framework.plugins.requests.robot.command;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeKeys;



public class GetRobotCleaningDetailsRequest extends RobotManagerRequest {
	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		getRobotCleaningDetails(mContext, jsonData, callbackId);
	}

	private void getRobotCleaningDetails(final Context context, RobotJsonData jsonData,
			final String callbackId) {
		LogHelper.logD(TAG, "getRobotCleaningDetails action initiated in Robot plugin");	
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
					jsonResult = getCleaningDetailsJsonObject(result);
				}
				return jsonResult;
			}
		});
	}

	private JSONObject getCleaningDetailsJsonObject(GetRobotProfileDetailsResult2 result) {
		JSONObject cleaningDetailsJsonObj = null;
		try {
			cleaningDetailsJsonObj = new JSONObject();
			String cleaningStateDetails = result.getProfileParameterValue(ProfileAttributeKeys.ROBOT_CURRENT_STATE_DETAILS);
			LogHelper.logD(TAG, "getRobotCleaningDetails " + cleaningStateDetails);
			cleaningDetailsJsonObj.put(ProfileAttributeKeys.ROBOT_CURRENT_STATE_DETAILS, cleaningStateDetails);
			
		} catch (JSONException e) {
			LogHelper
					.logD(TAG, "Exception in getCleaningDetailsJsonObject", e);
		}

		return cleaningDetailsJsonObj;
	}
}
