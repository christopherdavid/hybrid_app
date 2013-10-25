package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationUtils;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;
import com.neatorobotics.android.slide.framework.webservice.user.UserValidationHelper;

public class LoginUserRequest extends UserManagerRequest {

	@Override
	public void execute(JSONArray data, String callbackId) {
		UserJsonData jsonData = new UserJsonData(data);
		loginUser(mContext, jsonData, callbackId);
	}
	
	private void loginUser(final Context context, final UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "loginUser Called");
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		String password = jsonData.getString(JsonMapKeys.KEY_PASSWORD);
		
		LogHelper.logD(TAG, "JSON String: " + jsonData);		
		LogHelper.logD(TAG, "Email = " + email);
		
		UserManager.getInstance(context).loginUser(email, password, new UserRequestListenerWrapper(callbackId) {
			@Override
			public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
				UserItem userItem = getUserItemFromResponse(responseResult);
				
				JSONObject userDetails = null;
				if (userItem != null) {
					LogHelper.logD(TAG, "Login successful. Start service and send Success plugin to user with user details");						
					userDetails = new JSONObject();
					userDetails.put(JsonMapKeys.KEY_EMAIL, userItem.email);
					userDetails.put(JsonMapKeys.KEY_USER_NAME, userItem.name);
					userDetails.put(JsonMapKeys.KEY_USER_ID, userItem.id);
					int validationCode = UserValidationHelper.getUserValidationStatus(userItem.validation_status);
					userDetails.put(JsonMapKeys.KEY_VALIDATION_STATUS, validationCode);
					String extraParam = "{\"countryCode\":\""+userItem.extra_param.country_code+"\", \"optIn\":\""+userItem.extra_param.opt_in+"\"}";
					JSONObject jsonParam = new JSONObject(extraParam);
					userDetails.put(JsonMapKeys.KEY_EXTRA_PARAMS, jsonParam);
					PushNotificationUtils.registerForPushNotification(context);
					AppUtils.createNeatoUserDeviceIdIfNotExists(context);
				}					
				
				return userDetails;
			}
		});
	}

}
