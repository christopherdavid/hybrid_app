package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robotlinking.RobotLinkingManager;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotLinkInitiationResult;

public class InitiateLinkRobotRequest extends UserManagerRequest {

	@Override
	public void execute(JSONArray data, String callbackId) {
		UserJsonData jsonData = new UserJsonData(data);
		tryLinkingToRobot(mContext, jsonData, callbackId);
	}
	
	private void tryLinkingToRobot(Context context, UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "tryLinkingToRobot Called");
		
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		// Check for empty
		String linkCode = jsonData.getString(JsonMapKeys.KEY_LINKING_CODE);
		
		if (TextUtils.isEmpty(email)) {
			email = NeatoPrefs.getUserEmailId(context);
		}
		
		String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);	
		
		LogHelper.logD(TAG, "JSON String: " + jsonData);		
		LogHelper.logD(TAG, "tryLinkingToRobot initiated in Robot plugin for robot Id: " + robotId + " and email id: " + email);

		RobotLinkingManager.initiateLinkRobot(context, linkCode, email, new UserRequestListenerWrapper(callbackId) {
			@Override
			public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {	
				JSONObject initiateResult = new JSONObject();
				if (responseResult instanceof RobotLinkInitiationResult) {
					RobotLinkInitiationResult initiationResponse = (RobotLinkInitiationResult) responseResult;
					long expiryTime = initiationResponse.result.expiry_time * 1000;
					initiateResult.put(JsonMapKeys.KEY_LINK_CODE_EXPIRY_TIME, expiryTime);
				}
				return initiateResult;
			}
		});
	}

}
