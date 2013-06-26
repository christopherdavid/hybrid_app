package com.neatorobotics.android.slide.framework.plugins;


import java.util.ArrayList;
import java.util.HashMap;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationMessageHandler;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationUtils;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.settings.SettingsManager;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.user.GetNeatoUserDetailsResult;
import com.neatorobotics.android.slide.framework.webservice.user.GetUserAssociatedRobotsResult;
import com.neatorobotics.android.slide.framework.webservice.user.IsUserValidatedResult;
import com.neatorobotics.android.slide.framework.webservice.user.ResendValidationMailResult;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;
import com.neatorobotics.android.slide.framework.webservice.user.UserValidationHelper;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;
import com.neatorobotics.android.slide.framework.webservice.user.settings.RobotNotificationSettingsResult;


public class UserManagerPlugin extends Plugin {

	private static final String TAG = UserManagerPlugin.class.getSimpleName();


	private static final HashMap<String, UserManagerPluginMethods> ACTION_MAP = new HashMap<String, UserManagerPlugin.UserManagerPluginMethods>();

	// If we add more action type, please ensure to add it into the ACTION_MAP
	private static enum UserManagerPluginMethods {CREATE_USER, LOGIN, LOGOUT, ISLOGGEDIN, GET_USER_DETAILS, 
										ASSOCIATE_ROBOT, DISASSOCIATE_ROBOT, GET_ASSOCIATED_ROBOTS, 
										DISASSOCAITE_ALL_ROBOTS, 
										FORGET_PASSWORD, CHANGE_PASSWORD, CREATE_USER2, RESEND_VALIDATION_MAIL,
										IS_USER_VALIDATED, TURN_NOTIFICATION_ON_OFF, 
										IS_NOTIFICATION_ENABLED, GET_NOTIFICATION_SETTINGS};
	static {
		ACTION_MAP.put(ActionTypes.LOGIN, UserManagerPluginMethods.LOGIN);
		ACTION_MAP.put(ActionTypes.IS_USER_LOGGEDIN, UserManagerPluginMethods.ISLOGGEDIN);
		ACTION_MAP.put(ActionTypes.CREATE_USER, UserManagerPluginMethods.CREATE_USER);
		ACTION_MAP.put(ActionTypes.CREATE_USER2, UserManagerPluginMethods.CREATE_USER2);
		ACTION_MAP.put(ActionTypes.RESEND_VALIDATION_MAIL, UserManagerPluginMethods.RESEND_VALIDATION_MAIL);
		ACTION_MAP.put(ActionTypes.LOGOUT, UserManagerPluginMethods.LOGOUT);
		ACTION_MAP.put(ActionTypes.GET_USER_DETAILS, UserManagerPluginMethods.GET_USER_DETAILS);
		ACTION_MAP.put(ActionTypes.ASSOCIATE_ROBOT, UserManagerPluginMethods.ASSOCIATE_ROBOT);
		ACTION_MAP.put(ActionTypes.DISASSOCIATE_ROBOT, UserManagerPluginMethods.DISASSOCIATE_ROBOT);
		ACTION_MAP.put(ActionTypes.GET_ASSOCIATED_ROBOTS, UserManagerPluginMethods.GET_ASSOCIATED_ROBOTS);
		ACTION_MAP.put(ActionTypes.DISASSOCAITE_ALL_ROBOTS, UserManagerPluginMethods.DISASSOCAITE_ALL_ROBOTS);
		ACTION_MAP.put(ActionTypes.FORGET_PASSWORD, UserManagerPluginMethods.FORGET_PASSWORD);
		ACTION_MAP.put(ActionTypes.CHANGE_PASSWORD, UserManagerPluginMethods.CHANGE_PASSWORD);
		ACTION_MAP.put(ActionTypes.IS_USER_VALIDATED, UserManagerPluginMethods.IS_USER_VALIDATED);
		ACTION_MAP.put(ActionTypes.TURN_NOTIFICATION_ON_OFF, UserManagerPluginMethods.TURN_NOTIFICATION_ON_OFF);
		ACTION_MAP.put(ActionTypes.IS_NOTIFICATION_ENABLED, UserManagerPluginMethods.IS_NOTIFICATION_ENABLED);
		ACTION_MAP.put(ActionTypes.GET_NOTIFICATION_SETTINGS, UserManagerPluginMethods.GET_NOTIFICATION_SETTINGS);
	}
	
	@Override       
	public PluginResult execute(final String action, final JSONArray data, final String callbackId) {

		LogHelper.logD(TAG, "UserManagerPlugin execute with action :" + action);

		if (!isValidAction(action)) {
			LogHelper.logD(TAG, "Action is not a valid action. Action = " + action);
			PluginResult pluginResult = new PluginResult(PluginResult.Status.INVALID_ACTION);
			return pluginResult;
		}
		
		handlePluginExecute(action, data, callbackId);

		PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
		pluginResult.setKeepCallback(true);
		return pluginResult;
	}   
	
	// Private helper method to check, if we support the action
	// returns true, if we support the action, false otherwise
	private boolean isValidAction(String action)
	{
		return ACTION_MAP.containsKey(action);
	}

	// Private helper method to handle the action
	private void handlePluginExecute(String action, JSONArray data, String callbackId) {

		UserJsonData jsonData = new UserJsonData(data);
		Context context = cordova.getActivity();

		switch(convertToInternalAction(action)) {
		case LOGIN:
			LogHelper.log(TAG, "Login action initiated");
			loginUser(context, jsonData , callbackId);
			break;

		case CREATE_USER:
			LogHelper.log(TAG, "Register action initiated");
			createUser(context, jsonData, callbackId);
			break;
			
		case CREATE_USER2:
			LogHelper.log(TAG, "Register with email validation action initiated");
			createUser2(context, jsonData, callbackId);
			break;
			
		case RESEND_VALIDATION_MAIL:
			LogHelper.log(TAG, "Resending validation mail to your account");
			resendValidationMail(context, jsonData, callbackId);
			break;
		case IS_USER_VALIDATED:
			LogHelper.log(TAG, "Checking if User is validated or not");
			isUserValidated(context, jsonData, callbackId);
			break;
		
		case LOGOUT:
			LogHelper.log(TAG, "LOGOUT action initiated");
			logoutUser(context, jsonData, callbackId);
			break;

		case ISLOGGEDIN:
			LogHelper.log(TAG, "Is Logged-in action initiated");
			isUserLoggedIn(context, jsonData, callbackId);
			break;

		case ASSOCIATE_ROBOT:
			LogHelper.log(TAG, "ASSOCIATE_ROBOT action initiated");
			associateRobot(context, jsonData, callbackId);
			break;

		case GET_USER_DETAILS:
			LogHelper.log(TAG, "GET_USER_DETAILS action initiated");
			getUserDetails(context, jsonData, callbackId);
			break;

		case GET_ASSOCIATED_ROBOTS:
			LogHelper.log(TAG, "GET_ASSOCIATED_ROBOTS action initiated");
			getAssociatedRobots(context, jsonData, callbackId);
			break;
		case DISASSOCIATE_ROBOT:
			LogHelper.log(TAG, "DISASSOCIATE_ROBOT action initiated");
			disassociateRobot(context, jsonData, callbackId);
			break;
		case DISASSOCAITE_ALL_ROBOTS:
			LogHelper.log(TAG, "DISASSOCAITE_ALL_ROBOTS action initiated");
			disassociateAllRobots(context, jsonData, callbackId);
			break;
		case FORGET_PASSWORD:
			LogHelper.log(TAG, "FORGET_PASSWORD action initiated");
			forgetPassword(context, jsonData, callbackId);
			break;	
		case CHANGE_PASSWORD:
			LogHelper.log(TAG, "CHANGE_PASSWORD action initiated");
			changePassword(context, jsonData, callbackId);
			break;
		case TURN_NOTIFICATION_ON_OFF:
			LogHelper.log(TAG, "TURN_NOTIFICATION_ON_OFF action initiated");
			turnNotificationOnOff(context, jsonData, callbackId);
			break;
		case IS_NOTIFICATION_ENABLED:
			LogHelper.log(TAG, "IS_NOTIFICATION_ENABLED action initiated");
			isNotificationEnabled(context, jsonData, callbackId);
			break;				
		case GET_NOTIFICATION_SETTINGS:
			LogHelper.log(TAG, "GET_NOTIFICATION_SETTINGS action initiated");
			getNotificationSettings(context, jsonData, callbackId);
			break;
 		}
	}

	private UserManagerPluginMethods convertToInternalAction(String action) {
		UserManagerPluginMethods userManagerPluginMethod = ACTION_MAP.get(action);
		return userManagerPluginMethod;
	}


	// Helper method to get the notifications settings
	public void getNotificationSettings(final Context context, final UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "getNotificationSettings action initiated in Robot plugin");
		
		final String emailId = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		LogHelper.logD(TAG, String.format("Email = %s", emailId));			
	
		SettingsManager.getInstance(context).getNotificationSettings(emailId, new UserRequestListenerWrapper(callbackId) {				
				
			@Override
			public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
				JSONObject notificationSettings = null;
				if (responseResult instanceof RobotNotificationSettingsResult) {
					RobotNotificationSettingsResult result = (RobotNotificationSettingsResult)responseResult;
					notificationSettings = result.getNotificationsJson();
				}
				
				return notificationSettings;
			}
			
			// Send default notification settings.
			@Override
			public void onServerError(int errorCode, String errorMessage) {
				LogHelper.logD(TAG, String.format("Server Message = %s ", errorMessage));
				JSONObject notificationSettings = RobotHelper.getDefaultSettings();
				PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, notificationSettings);		
				success(pluginResult, mCallbackId);
			}
		});		
	}
	
	// Helper method to turn on/off the notifications
	// Enabling and disabling the notification settings on the server
	private void turnNotificationOnOff(final Context context, final UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "turnNotificationOnOff action initiated in Robot plugin");
		final String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		

		final String notificationId = jsonData.getString(JsonMapKeys.KEY_NOTIFICATION_ID);
		// TODO: Add notificationID validation check 
		
		final boolean onOffFlag = jsonData.getBoolean(JsonMapKeys.KEY_FLAG_ON);

		LogHelper.logD(TAG, String.format("Email = %s NotificationId = %s Enable = %s", email, notificationId, onOffFlag));

		SettingsManager.getInstance(context).updateNotificationState(email, notificationId, onOffFlag, new UserRequestListenerWrapper(callbackId) {
			@Override
			public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
				JSONObject notificationSetting = new JSONObject();
				notificationSetting.put(JsonMapKeys.KEY_NOTIFICATION_KEY, notificationId);
				notificationSetting.put(JsonMapKeys.KEY_NOTIFICATION_VALUE, onOffFlag);
				return notificationSetting;
			}
		});
	}
	
	private void isNotificationEnabled(Context context, UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "isNotificationEnabled action initiated in Robot plugin");	
		
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		final String notificationId = jsonData.getString(JsonMapKeys.KEY_NOTIFICATION_ID);
		// TODO: Add notificationID validation check
		LogHelper.logD(TAG, String.format("Email = %s NotificationId = %s", email, notificationId));			
		
		SettingsManager.getInstance(context).getNotificationSettings(email, new UserRequestListenerWrapper(callbackId) {				
			@Override
			public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
				JSONObject notificationSetting = new JSONObject();
				if (responseResult instanceof RobotNotificationSettingsResult) {
					RobotNotificationSettingsResult result = (RobotNotificationSettingsResult)responseResult;
					notificationSetting.put(JsonMapKeys.KEY_NOTIFICATION_KEY, notificationId);
					notificationSetting.put(JsonMapKeys.KEY_NOTIFICATION_VALUE, result.isNotificationEnable(notificationId));
				}
				
				return notificationSetting;
			}
		});
	}
	
	private void forgetPassword (Context context, UserJsonData jsonData, final String callbackId) {
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		UserManager.getInstance(context).forgetPassword(email, new UserRequestListenerWrapper(callbackId));		
	}
	
	private void changePassword(Context context, UserJsonData jsonData, final String callbackId) {
		String emailId = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		LogHelper.logD(TAG, "emailId = " + emailId);
		String authToken = NeatoPrefs.getNeatoUserAuthToken(context);
		String currentPassword = jsonData.getString(JsonMapKeys.KEY_CURRENT_PASSWORD);
		String newPassword = jsonData.getString(JsonMapKeys.KEY_NEW_PASSWORD);
		UserManager.getInstance(context).changePassword(authToken, currentPassword, newPassword, new UserRequestListenerWrapper(callbackId));
	}
		
	private void isUserLoggedIn(Context context, UserJsonData jsonData, String callbackId) {
		String authToken = NeatoPrefs.getNeatoUserAuthToken(context);
		boolean isUserLoggedIn = !TextUtils.isEmpty(authToken);
		PluginResult pluginLogoutResult = new PluginResult(PluginResult.Status.OK, isUserLoggedIn);
		pluginLogoutResult.setKeepCallback(false);
		success(pluginLogoutResult, callbackId);
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
					
					RobotCommandServiceManager.loginToXmpp(context);
					PushNotificationUtils.registerForPushNotification(context);
					AppUtils.createNeatoUserDeviceIdIfNotExists(context);
				}					
				
				return userDetails;
			}
		});
	}
	
	private void sendError(String callbackId, int errorCode, String message)
	{
		JSONObject errorInfo = getErrorJsonObject(errorCode, message);
		PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.ERROR, errorInfo);
		error(loginUserPluginResult, callbackId);
	}
	
	@SuppressWarnings("unused")
	private void sendError(String callbackId, int errorCode, int errorResId)
	{
		String message = cordova.getActivity().getString(errorResId);
		sendError(callbackId, errorCode, message);
	}

	private void logoutUser(final Context context, final UserJsonData jsonData, final String callbackId) {	
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				String authToken = NeatoPrefs.getNeatoUserAuthToken(context);
				if (TextUtils.isEmpty(authToken)) {
					PluginResult logoutPluginResult = new  PluginResult(PluginResult.Status.ERROR);
					LogHelper.logD(TAG, "No user logged in.");
					error(logoutPluginResult, callbackId);
					return;
				}
				
				UserHelper.logout(context);
				PluginResult logoutPluginResult = new  PluginResult(PluginResult.Status.OK);
				LogHelper.logD(TAG, "Logout successful.");
				success(logoutPluginResult, callbackId);
				PushNotificationMessageHandler.getInstance(context).removePushNotificationListener();
				PushNotificationUtils.unregisterPushNotification(context);
				AppUtils.clearNeatoUserDeviceId(context);
			}
		};
		
		TaskUtils.scheduleTask(task, 0);
		
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
					RobotCommandServiceManager.loginToXmpp(context);
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
					RobotCommandServiceManager.loginToXmpp(context);
					PushNotificationUtils.registerForPushNotification(context);
					AppUtils.createNeatoUserDeviceIdIfNotExists(context);
				}		
				
				return userDetails;
			}
		});
	}	
	
	private void isUserValidated(final Context context, UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "checking if user is authenticated");
		LogHelper.logD(TAG, "JSON String: " + jsonData);
		
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		LogHelper.logD(TAG, "Email: " + email);
		
		UserManager.getInstance(context).isUserValidated(email, new UserRequestListenerWrapper(callbackId) {
			@Override
			public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
				JSONObject resultObj = new JSONObject(); 
				if ((responseResult != null) && (responseResult instanceof IsUserValidatedResult)) {
					IsUserValidatedResult validationResult = (IsUserValidatedResult)responseResult;
					int userValidationCode = UserValidationHelper.getUserValidationStatus(validationResult.result.validation_status);
					resultObj.put(JsonMapKeys.KEY_VALIDATION_STATUS, userValidationCode);
					resultObj.put(JsonMapKeys.KEY_MESSAGE, validationResult.result.message);
				}
				
				return resultObj;
			}
		});			
	}
	
	private void resendValidationMail(final Context context, UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "resend Validation Mail called");
		LogHelper.logD(TAG, "JSON String: " + jsonData);
		
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		LogHelper.logD(TAG, "Email: " + email);
		
		UserManager.getInstance(context).resendValidationMail(email, new UserRequestListenerWrapper(callbackId) {
			@Override
			public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
				JSONObject resultObj = new JSONObject(); 
				if ((responseResult != null) && (responseResult instanceof ResendValidationMailResult)) {
					ResendValidationMailResult resendResult = (ResendValidationMailResult)responseResult;				
					resultObj.put(JsonMapKeys.KEY_MESSAGE, resendResult.result.message);
				}
				
				return resultObj;
			}
		});		
	}
	
	public void getUserDetails(final Context context, UserJsonData jsonData, final String callbackId) {
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

	private void associateRobot(Context context, UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "associateRobot Called");
		
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		
		if (TextUtils.isEmpty(email)) {
			email = NeatoPrefs.getUserEmailId(context);
		}
		
		String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);	
		
		LogHelper.logD(TAG, "JSON String: " + jsonData);		
		LogHelper.logD(TAG, "Associate action initiated in Robot plugin for robot Id: " + robotId + " and email id: " + email);

		UserManager.getInstance(context).associateRobot(robotId, email, new UserRequestListenerWrapper(callbackId));
	}

	private void disassociateAllRobots(Context context, UserJsonData jsonData, final String callbackId) {		
		LogHelper.logD(TAG, "disassociateAllRobots Called");
		
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		if (TextUtils.isEmpty(email)) {
			email = NeatoPrefs.getUserEmailId(context);
		}
		LogHelper.logD(TAG, "JSON String: " + jsonData);
		
		LogHelper.logD(TAG, "Disassociate all robots for email id: " + email);
		UserManager.getInstance(context).disassociateAllRobots(email, new UserRequestListenerWrapper(callbackId));
	}

	private void disassociateRobot(Context context, UserJsonData jsonData, final String callbackId) {		
		LogHelper.logD(TAG, "disassociateRobot Called");
		
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		if (TextUtils.isEmpty(email)) {
			email = NeatoPrefs.getUserEmailId(context);
		}
		String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
		
		LogHelper.logD(TAG, "JSON String: " + jsonData);
		LogHelper.logD(TAG, "Disassociate action initiated in Robot plugin for robot Id: " + robotId + " and email id: " + email);

		UserManager.getInstance(context).disassociateRobot(robotId, email, new UserRequestListenerWrapper(callbackId));
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
				success(pluginResult, callbackId);
			}
		});
	}
	
	
	private static class ActionTypes {
		public static final String LOGIN = "login";
		public static final String CREATE_USER = "createUser";
		public static final String CREATE_USER2 = "createUser2";
		public static final String RESEND_VALIDATION_MAIL = "resendValidationMail";
		public static final String IS_USER_VALIDATED = "isUserValidated";
		public static final String LOGOUT = "logout";
		public static final String IS_USER_LOGGEDIN = "isLoggedIn";
		public static final String GET_USER_DETAILS = "getUserDetails";
		public static final String ASSOCIATE_ROBOT = "associateRobot";
		public static final String GET_ASSOCIATED_ROBOTS = "getAssociatedRobots";
		public static final String DISASSOCIATE_ROBOT = "disassociateRobot";
		public static final String DISASSOCAITE_ALL_ROBOTS = "disassociateAllRobots";
		public static final String FORGET_PASSWORD	= "forgetPassword";
		public static final String CHANGE_PASSWORD	= "changePassword";
		public static final String TURN_NOTIFICATION_ON_OFF	= "turnNotificationOnOff";
		public static final String IS_NOTIFICATION_ENABLED	= "isNotificationEnabled";
		public static final String GET_NOTIFICATION_SETTINGS = "getNotificationSettings";
	}

	private JSONObject getErrorJsonObject(int errorCode, String errMessage) {
		JSONObject error = new JSONObject();
		try {
			error.put(JsonMapKeys.KEY_ERROR_CODE, errorCode);
			error.put(JsonMapKeys.KEY_ERROR_MESSAGE, errMessage);
		} catch (JSONException e) {
			LogHelper.logD(TAG, "Exception in getErrorJsonObject", e);
		}
		return error;
	}
	

	private UserItem getUserItemFromResponse(NeatoWebserviceResult responseResult) {
		UserItem userItem = null;		
		if ((responseResult != null) && (responseResult instanceof GetNeatoUserDetailsResult)) {
			userItem = ((GetNeatoUserDetailsResult)responseResult).result;
		}
		
		return userItem;
	}
	
	private JSONArray convertRobotItemsToJSONArray(ArrayList<RobotItem> robotList) {
		JSONArray robots = new JSONArray();
		if ((robotList != null) && (robotList.size() > 0)) {
			for (RobotItem item: robotList) {
				try {

					JSONObject robot = new JSONObject();
					robot.put(JsonMapKeys.KEY_ROBOT_ID, item.serial_number);
					robot.put(JsonMapKeys.KEY_ROBOT_NAME, item.name);
					robots.put(robot);
				} catch (JSONException e) {
					LogHelper.log(TAG, "Exception in RobotDetailsPluginListener", e);
				}
			}					
		} 
		else {
			LogHelper.logD(TAG, "No robots associated");
		}
		return robots;
	}	

	private class UserRequestListenerWrapper implements WebServiceBaseRequestListener {
		protected String mCallbackId;
		
		public UserRequestListenerWrapper(String callbackId) {
			mCallbackId = callbackId;
		}
		
		@Override
		public void onServerError(int errorType, String errMessage) {
			LogHelper.logD(TAG, String.format("Server ErrorType = [%d] Message = %s", errorType, errMessage));
			sendError(mCallbackId, errorType, errMessage);
		}
		
		@Override
		public void onNetworkError(String errorMessage) {
			LogHelper.logD(TAG, "Network Error: " + errorMessage);
			sendError(mCallbackId, ErrorTypes.ERROR_NETWORK_ERROR, errorMessage);
		}
		
		@Override
		public void onReceived(NeatoWebserviceResult responseResult) {
			LogHelper.logD(TAG, "Request processed successfully");
			try {
				JSONObject resultObj = getResultObject(responseResult);
				if (resultObj != null) {
					PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, resultObj);		
					success(pluginResult, mCallbackId);
				}
				else {
					LogHelper.logD(TAG, "Unknown Error");
					sendError(mCallbackId, ErrorTypes.ERROR_TYPE_UNKNOWN, "Unknown Error");
				}
			}
			catch (JSONException ex) {
				LogHelper.logD(TAG, "JSON Error");
				sendError(mCallbackId, ErrorTypes.JSON_PARSING_ERROR, ex.getMessage());
			}
		}	
		
		public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
			return new JSONObject(); 
		}
	}
}
