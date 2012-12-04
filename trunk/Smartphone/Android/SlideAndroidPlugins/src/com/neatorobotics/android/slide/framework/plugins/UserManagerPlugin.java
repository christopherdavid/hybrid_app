package com.neatorobotics.android.slide.framework.plugins;


import java.util.HashMap;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.user.UserDetailsListener;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class UserManagerPlugin extends Plugin {

	private static final String TAG = UserManagerPlugin.class.getSimpleName();

	
	private static final HashMap<String, UserManagerPluginMethods> ACTION_MAP = new HashMap<String, UserManagerPlugin.UserManagerPluginMethods>();

	// If we add more action type, please ensure to add it into the ACTION_MAP
	private static enum UserManagerPluginMethods { LOGIN, REGISTER, LOGOUT, ISLOGGEDIN};
	
	static {
		ACTION_MAP.put(ActionTypes.LOGIN, UserManagerPluginMethods.LOGIN);
		ACTION_MAP.put(ActionTypes.ISLOGGEDIN, UserManagerPluginMethods.ISLOGGEDIN);
		ACTION_MAP.put(ActionTypes.REGISTER, UserManagerPluginMethods.REGISTER);
		ACTION_MAP.put(ActionTypes.LOGOUT, UserManagerPluginMethods.LOGOUT);
	}

	private UserDetailsPluginListener mUserDetailsPluginListener;
	@Override       
	public PluginResult execute(final String action, final JSONArray data, final String callbackId) {

		LogHelper.logD(TAG, "UserManagerPlugin execute with action :" + action);

		Activity currentActivity = cordova.getActivity();
		currentActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				handlePluginExecute(action, data, callbackId);
			}
		});

		PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
		pluginResult.setKeepCallback(true);
		return pluginResult;
	}   

	private void handlePluginExecute(String action, JSONArray data, String callbackId) {

		UserJsonData jsonData = new UserJsonData(data);
		Context context = cordova.getActivity();

		switch(convertToInternalAction(action)) {
		case LOGIN:
			LogHelper.log(TAG, "Login action initiated");
			loginUser(context, jsonData , callbackId);
			PluginResult pluginLoginResult = new PluginResult(PluginResult.Status.NO_RESULT);
			pluginLoginResult.setKeepCallback(true);
			success(pluginLoginResult, callbackId);
			break;
		case REGISTER:
			LogHelper.log(TAG, "Register action initiated");
			createUser(context, jsonData, callbackId);
			break;
		case LOGOUT:
			LogHelper.log(TAG, "LOGOUT action initiated");
			logoutUser(context, jsonData, callbackId);
			PluginResult pluginLogoutResult = new PluginResult(PluginResult.Status.OK);
			pluginLogoutResult.setKeepCallback(false);
			success(pluginLogoutResult, callbackId);
		case ISLOGGEDIN:
			LogHelper.log(TAG, "Is Logged-in action initiated");
			isUserLoggedIn(context, jsonData, callbackId);

		}
	}

	private UserManagerPluginMethods convertToInternalAction(String action) {
		UserManagerPluginMethods userManagerPluginMethod = ACTION_MAP.get(action);
		return userManagerPluginMethod;
	}


	private void isUserLoggedIn(Context context, UserJsonData jsonData, String callbackId) {
		
		String authToken = NeatoPrefs.getNeatoUserAuthToken(context);
		if (authToken == null) {
			PluginResult pluginLogoutResult = new PluginResult(PluginResult.Status.OK, false);
			pluginLogoutResult.setKeepCallback(false);
			success(pluginLogoutResult, callbackId);
		} else {
			PluginResult pluginLogoutResult = new PluginResult(PluginResult.Status.OK, true);
			pluginLogoutResult.setKeepCallback(false);
			success(pluginLogoutResult, callbackId);

		}
	}
	
	private void loginUser(Context context, UserJsonData jsonData, String callbackId) {

		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		String password = jsonData.getString(JsonMapKeys.KEY_PASSWORD);
		mUserDetailsPluginListener =  new UserDetailsPluginListener(callbackId);
		UserManager.getInstance(context).loginUser(email, password, mUserDetailsPluginListener);
	}

	private void logoutUser(Context context, UserJsonData jsonData, String callbackId) {	
		//mNeatoRobotService.cleanup();
		NeatoPrefs.saveUserEmailId(context, null);
	}

	private void createUser(Context context, UserJsonData jsonData, String callbackId) {
		String mEmail = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		String mPassword = jsonData.getString(JsonMapKeys.KEY_PASSWORD);
		String mName = jsonData.getString(JsonMapKeys.KEY_USER_NAME);
		mUserDetailsPluginListener =  new UserDetailsPluginListener(callbackId);
		UserManager.getInstance(context).createUser(mName, mEmail, mPassword, mUserDetailsPluginListener);
	}

	public void GetUserDetails(Context context, JSONObject argsMap, int callbackId) {
	}


	private static class ActionTypes {
		public static final String LOGIN = "login";
		public static final String REGISTER = "register";
		public static final String GETUSERDETAILS = "userdetails";
		public static final String LOGOUT = "logout";
		public static final String ISLOGGEDIN = "isloggedin";
	}

	private static class ERROR_TYPES {
		public static final int ERROR_INVALID_ARGUMENT=1;
		public static final int ERROR_INVALID_ACTION=2;
	}

	//Both Login and Register will get the user details related to the user.
	private class UserDetailsPluginListener implements UserDetailsListener {

		String mCallBackId;
		Context context = cordova.getActivity();
		UserDetailsPluginListener(String callBackId) {
			mCallBackId = callBackId;
		}

		@Override
		public void onUserDetailsReceived(UserItem userItem) {
			if (userItem != null) {
				NeatoPrefs.saveUserEmailId(context, userItem.getEmail());
			}
			//TODO convert this UserItem to JSON Object and pass to the UI.
			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.OK);
			LogHelper.logD(TAG, "Login successful. Start service and send Success plugin to user with user details");
			success(loginUserPluginResult, mCallBackId);

		}

		@Override
		public void onNetworkError() {
			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.ERROR);
			NeatoPrefs.saveUserEmailId(context, null);
			LogHelper.logD(TAG, "Login unsuccessful. Network Error");
			error(loginUserPluginResult, mCallBackId);
		}

		@Override
		public void onServerError() {
			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.ERROR);
			NeatoPrefs.saveUserEmailId(context, null);
			LogHelper.logD(TAG, "Login unsuccessful. Server Error");
			error(loginUserPluginResult, mCallBackId);

		}


	}

}
