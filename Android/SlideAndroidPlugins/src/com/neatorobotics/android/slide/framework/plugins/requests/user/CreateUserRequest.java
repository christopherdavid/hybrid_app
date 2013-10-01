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

	public static final int CREATE_USER_WITHOUT_AUTH  = 0;
	public static final int CREATE_USER_WITH_AUTH  = 1;
	public static final int CREATE_USER_WITH_AUTH_EXTRA_PARAM  = 2;
	
	private int mRequestType;

	public CreateUserRequest() {
		mRequestType = CREATE_USER_WITH_AUTH;
	}
	
	
	public CreateUserRequest(int requestType) {
		mRequestType = requestType;
	}
	
	
	@Override
	public void execute(JSONArray data, String callbackId) {
		UserJsonData jsonData = new UserJsonData(data);
		if(mRequestType == CREATE_USER_WITH_AUTH_EXTRA_PARAM) {
			createUser3(mContext, jsonData, callbackId);
		} else if (mRequestType == CREATE_USER_WITH_AUTH) {
			createUser2(mContext, jsonData, callbackId);			
		} else {
			createUser(mContext, jsonData, callbackId);
		}
	}
	
	private void createUser3(final Context context, final UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "createUser3 called");
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		String alternateEmail = jsonData.getString(JsonMapKeys.KEY_ALTERNATE_EMAIL);
		String password = jsonData.getString(JsonMapKeys.KEY_PASSWORD);
		String name = jsonData.getString(JsonMapKeys.KEY_USER_NAME);
		String extraParams = jsonData.getString(JsonMapKeys.KEY_EXTRA_PARAMS);
		
		UserManager.getInstance(context).createUser3(name, email, alternateEmail, password, extraParams, new UserRequestListenerWrapper(callbackId) {

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
					String extraParam = "{\"countryCode\":\""+userItem.extra_param.countryCode+"\", \"optIn\":\""+userItem.extra_param.optIn+"\"}";
					JSONObject jsonParam = new JSONObject(extraParam);
					userDetails.put(JsonMapKeys.KEY_EXTRA_PARAMS, jsonParam);	
					
					PushNotificationUtils.registerForPushNotification(context);
					AppUtils.createNeatoUserDeviceIdIfNotExists(context);
				}		
				
				return userDetails;
			}			
		});
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
