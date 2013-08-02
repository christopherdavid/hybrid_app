package com.neatorobotics.android.slide.framework.plugins.requests.user;

import java.util.ArrayList;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.user.GetUserAssociatedRobotsResult;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class GetAssociatedRobotsRequest extends UserManagerRequest {

	@Override
	public void execute(JSONArray data, String callbackId) {
		UserJsonData jsonData = new UserJsonData(data);
		getAssociatedRobots(mContext, jsonData, callbackId);
	}
	
	private void getAssociatedRobots(Context context, UserJsonData jsonData, final String callbackId) {
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);		
		
		if (TextUtils.isEmpty(email)) {
			email = NeatoPrefs.getUserEmailId(context);
		}
		
		String auth_token = NeatoPrefs.getNeatoUserAuthToken(context);		
		LogHelper.logD(TAG, "getAssociatedRobots - JSON String: " + jsonData);		
		
		UserManager.getInstance(context).getAssociatedRobots(email, auth_token, new UserRequestListenerWrapper(callbackId) {
			
			@Override
			public void onReceived(NeatoWebserviceResult responseResult) {
				ArrayList<RobotItem> robotList = null; 
				
				if ((responseResult != null) && (responseResult instanceof GetUserAssociatedRobotsResult)) {
					robotList = ((GetUserAssociatedRobotsResult)responseResult).result;
				}
				
				JSONArray robots = convertRobotItemsToJSONArray(robotList);
				PluginResult pluginResult =  new  PluginResult(PluginResult.Status.OK, robots);
				sendSuccessPluginResult(pluginResult, callbackId);
			}
		});
	}
}
