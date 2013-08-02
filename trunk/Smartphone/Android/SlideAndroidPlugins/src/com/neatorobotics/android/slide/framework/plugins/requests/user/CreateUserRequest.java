package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.gcm.PushNotificationUtils;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;
import com.neatorobotics.android.slide.framework.webservice.user.UserValidationHelper;

import android.content.Context;

public class CreateUserRequest extends UserManagerRequest {

	boolean mUseValidEmail = true;
	
	public CreateUserRequest() {
	}
	
	// Temp constructor for CreateUser request without validation. This is useful for testing purposes.
	public CreateUserRequest(boolean useValidEmail) {
		mUseValidEmail = useValidEmail;
	}
	
	@Override
	public void execute(JSONArray data, String callbackId) {
		UserJsonData jsonData = new UserJsonData(data);
		if (mUseValidEmail) {
			createUser2(mContext, jsonData, callbackId);
		}
		else {
			createUser(mContext, jsonData, callbackId);
		}
	}

	private void createUser2(final Context context, final UserJsonData jsonData, final String callbackId) {		
		LogHelper.logD(TAG, "createUser2 Called");
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		String alternateEmail = jsonData.getString(JsonMapKeys.KEY_ALTERNATE_EMAIL);
		String password = jsonData.getString(JsonMapKeys.KEY_PASSWORD);
		String name = jsonData.getString(JsonMapKeys.KEY_USER_NAME);

		LogHelper.logD(TAG, "JSON String: " + jsonData);
		LogHelper.logD(TAG, "Email:" + email + " Name: " + name + " Alternate Email: " + alternateEmail);
		
		UserManager.getInstance(context).createUser2(name, email, alternateEmail, password, new UserRequestListenerWrapper(callbackId) {

			@Override
			public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {	
				UserItem userItem = getUserItemFromResponse(responseResult);
				
				JSONObject userDetails = null;
				if (userItem != null) {
					userDetails = new JSONObject();
					userDetails.put(JsonMapKeys.KEY_EMAIL, userItem.email);
					userDetails.put(JsonMapKeys.KEY_ALTERNATE_EMAIL, userItem.alternate_email);
					userDetails.put(JsonMapKeys.KEY_USER_NAME, userItem.name);
					userDetails.put(JsonMapKeys.KEY_USER_ID, userItem.id);
					int validationCode = UserValidationHelper.getUserValidationStatus(userItem.validation_status);
					userDetails.put(JsonMapKeys.KEY_VALIDATION_STATUS, validationCode);
					
					
					PushNotificationUtils.registerForPushNotification(context);
					AppUtils.createNeatoUserDeviceIdIfNotExists(context);
				}		
				
				return userDetails;
			}
		});
	}	
	
	private void createUser(final Context context, final UserJsonData jsonData, final String callbackId) {		
		LogHelper.logD(TAG, "createUser Called");
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		String password = jsonData.getString(JsonMapKeys.KEY_PASSWORD);
		String name = jsonData.getString(JsonMapKeys.KEY_USER_NAME);

		LogHelper.logD(TAG, "JSON String: " + jsonData);
		LogHelper.logD(TAG, "Email:" + email + " Name: " + name);
		
		UserManager.getInstance(context).createUser(name, email, password,  new UserRequestListenerWrapper(callbackId) {
			
			@Override
			public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {	
				UserItem userItem = getUserItemFromResponse(responseResult);
					
				JSONObject userDetails = null;				
				if (userItem != null) {					
					userDetails = new JSONObject();
					userDetails.put(JsonMapKeys.KEY_EMAIL, userItem.email);
					userDetails.put(JsonMapKeys.KEY_USER_NAME, userItem.name);
					userDetails.put(JsonMapKeys.KEY_USER_ID, userItem.id);
					int validationCode = UserValidationHelper.getUserValidationStatus(userItem.validation_status);
					userDetails.put(JsonMapKeys.KEY_VALIDATION_STATUS, validationCode);
					
					PushNotificationUtils.registerForPushNotification(context);
					AppUtils.createNeatoUserDeviceIdIfNotExists(context);
				}				
				
				return userDetails;
			}
		});
	}
	
}
