package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class GetUserDetailsRequest extends UserManagerRequest {

	@Override
	public void execute(JSONArray data, String callbackId) {
		UserJsonData jsonData = new UserJsonData(data);
		getUserDetails(mContext, jsonData, callbackId);
	}
	
	private void getUserDetails(final Context context, UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "getUserDetails Called");
		LogHelper.logD(TAG, "JSON String: " + jsonData);

		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		String authKey = NeatoPrefs.getNeatoUserAuthToken(context);
		LogHelper.logD(TAG, "Email:" + email + " authKey: " + authKey);		

		UserManager.getInstance(context).getUserDetails(email, authKey,  new UserRequestListenerWrapper(callbackId) {
			@Override
			public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
				UserItem userItem = getUserItemFromResponse(responseResult);
					
				JSONObject userDetails = null;
				if (userItem != null) {
					userDetails = new JSONObject();
					userDetails.put(JsonMapKeys.KEY_EMAIL, userItem.email);
					userDetails.put(JsonMapKeys.KEY_USER_NAME, userItem.name);
					userDetails.put(JsonMapKeys.KEY_USER_ID, userItem.id);												
				}	
				
				return userDetails;
			}
		});
	}

}
