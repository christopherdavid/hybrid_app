package com.neatorobotics.android.slide.framework.plugins.requests.robot.profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robotdata.RobotProfileDataUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;

public class GetRobotCleaningStateRequest extends RobotManagerRequest {

	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		getRobotCleaningState(mContext, jsonData, callbackId);
	}
	private void getRobotCleaningState(final Context context, RobotJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "getRobotCleaningState is called");
		final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
		RobotManager.getInstance(context).getRobotCleaningState(context, robotId, new RobotRequestListenerWrapper(callbackId) {
			@Override
			public JSONObject getResultObject(
					NeatoWebserviceResult responseResult)
					throws JSONException {
				JSONObject jsonResult = new JSONObject();
				if((responseResult != null) && (responseResult instanceof GetRobotProfileDetailsResult2)) {
					GetRobotProfileDetailsResult2 result = (GetRobotProfileDetailsResult2) responseResult;
					String currentState = RobotProfileDataUtils.getRobotCurrentState(context, result);
					String state = RobotProfileDataUtils.getState(context, result);
					if (!TextUtils.isEmpty(currentState)) {
						jsonResult.put(JsonMapKeys.KEY_ROBOT_CURRENT_STATE, currentState);
					}
					if (!TextUtils.isEmpty(state)) {
						jsonResult.put(JsonMapKeys.KEY_ROBOT_NEW_VIRTUAL_STATE, state);
					}
					jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
				}
				return jsonResult;
			}
		});
	}
	
}
