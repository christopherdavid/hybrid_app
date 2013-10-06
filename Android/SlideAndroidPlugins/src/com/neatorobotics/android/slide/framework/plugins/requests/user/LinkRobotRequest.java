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
import com.neatorobotics.android.slide.framework.webservice.user.RobotLinkResult;
import com.neatorobotics.android.slide.framework.webservice.user.RobotLinkResult.Result;


public class LinkRobotRequest extends UserManagerRequest {

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

		RobotLinkingManager.linkRobot(context, linkCode, email, new UserRequestListenerWrapper(callbackId) {
			@Override
			public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {	
				JSONObject robotDetail = getInitiateLinkRobotResultJsonObject(responseResult);
				return robotDetail;
			}
		});
	}

	private JSONObject getInitiateLinkRobotResultJsonObject(NeatoWebserviceResult responseResult) throws JSONException {
		JSONObject robotJsonObj = null;					
		if ((responseResult != null) && (responseResult instanceof RobotLinkResult)) {
			Result  result = ((RobotLinkResult)responseResult).result;
			if (result != null) {
				robotJsonObj = new JSONObject();
				robotJsonObj.put(JsonMapKeys.KEY_SUCCESS, result.success);
				robotJsonObj.put(JsonMapKeys.KEY_MESSAGE, result.message);	
				robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, result.serial_number);
			}
		}
		
		return robotJsonObj;
	}
}
