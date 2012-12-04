package com.neatorobotics.android.slide.framework.plugins;


import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.user.UserDetailsListener;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class UserManagerPlugin extends Plugin{

	private static final String TAG = UserManagerPlugin.class.getSimpleName();
	private Context mContext;
	
	enum Methods { LOGIN, REGISTER, LOGOUT};
	private UserPluginListener userPluginSuccess;

	@Override       
	public PluginResult execute(String action, JSONArray data, String callBackid) {
		
		String mCallbackId;
		LogHelper.logD(TAG, "UserManagerPlugin execute with action :" + action);
		UserJsonData jsonData = new UserJsonData(data);

		mContext = cordova.getActivity();
		mCallbackId = callBackid;

		switch(determineAction(action)) {
		case LOGIN:
			LogHelper.log(TAG, "Login action initiated");
			PluginResult mpluginLoginResult = new PluginResult(PluginResult.Status.NO_RESULT);
			mpluginLoginResult.setKeepCallback(true);
			
			loginUser(mContext, jsonData , mCallbackId);
			
			return mpluginLoginResult;
		case REGISTER:
			LogHelper.log(TAG, "Register action initiated");
			PluginResult mpluginRegisterResult = new PluginResult(PluginResult.Status.NO_RESULT);
			mpluginRegisterResult.setKeepCallback(true);
			createUser(mContext, jsonData, mCallbackId);
			return mpluginRegisterResult;
		case LOGOUT:
			LogHelper.log(TAG, "LOGOUT action initiated");
			PluginResult mpluginLogoutResult = new PluginResult(PluginResult.Status.OK);
			mpluginLogoutResult.setKeepCallback(true);
			logoutUser(mContext, jsonData, mCallbackId);
			return mpluginLogoutResult;

		default:
			return new PluginResult(PluginResult.Status.INVALID_ACTION);
		}


	}   


	private Methods determineAction(String action){
		if(action.equals(ActionTypes.LOGIN))
			return Methods.LOGIN;
		if(action.equals(ActionTypes.REGISTER))
			return Methods.REGISTER;
		if(action.equals(ActionTypes.LOGOUT))
			return Methods.LOGOUT;
		return null;
	}

	
	private void loginUser(Context context, UserJsonData jsonData, String callbackId) {

		String mEmail = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		String mPassword = jsonData.getString(JsonMapKeys.KEY_PASSWORD);
		userPluginSuccess = new UserPluginListener(callbackId);
		UserManager.getInstance(context).loginUser(mEmail, mPassword, userPluginSuccess);
	}

	private void logoutUser(Context context, UserJsonData jsonData,
			String callbackId) {	
		//mNeatoRobotService.cleanup();
		NeatoPrefs.saveUserEmailId(context, null);
	}

	private void createUser(Context context, UserJsonData jsonData, String callbackId) {

		String mEmail = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		String mPassword = jsonData.getString(JsonMapKeys.KEY_PASSWORD);
		String mName = jsonData.getString(JsonMapKeys.KEY_USER_NAME);
		userPluginSuccess = new UserPluginListener(callbackId);
		UserManager.getInstance(context).createUser(mName, mEmail, mPassword, userPluginSuccess);
		
	}


	public void GetUserDetails(Context context, JSONObject argsMap, int callbackId) {


	}


	class ActionTypes {

		public static final String LOGIN = "login";
		public static final String REGISTER = "register";
		public static final String GETUSERDETAILS = "userdetails";
		public static final String LOGOUT = "logout";
	}

	class ERROR_TYPES {
		public static final int ERROR_INVALID_ARGUMENT=1;
		public static final int ERROR_INVALID_ACTION=2;

	}

	//Both Login and Register will get the user details related to the user.
	class UserPluginListener implements UserDetailsListener {

		String mCallBackId;
		UserPluginListener(String callBackId) {
			mCallBackId = callBackId;
		}

		@Override
		public void onUserDetailsReceived(UserItem userItem) {
			//TODO convert this UserItem to JSON Object and pass to the UI.
			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.OK);
			LogHelper.logD(TAG, "Login successful. Start service and send Success plugin to user with user details");
			success(loginUserPluginResult, mCallBackId);
			
		}

		@Override
		public void onNetworkError() {
			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.ERROR);
			error(loginUserPluginResult, mCallBackId);
		}

		@Override
		public void onServerError() {
			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.ERROR);
			error(loginUserPluginResult, mCallBackId);

		}


	}

}
