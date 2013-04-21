package com.neatorobotics.android.slide.framework.plugins;


import java.util.ArrayList;
import java.util.HashMap;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.EventTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;
import com.neatorobotics.android.slide.framework.utils.DeviceUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.AssociatedRobotDetailsListener;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.UserDetailsListener;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.UserForgetPasswordListener;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.UserRobotAssociateDisassociateListener;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.UserPasswordChangeListener;


public class UserManagerPlugin extends Plugin {

	private static final String TAG = UserManagerPlugin.class.getSimpleName();


	private static final HashMap<String, UserManagerPluginMethods> ACTION_MAP = new HashMap<String, UserManagerPlugin.UserManagerPluginMethods>();

	// If we add more action type, please ensure to add it into the ACTION_MAP
	private static enum UserManagerPluginMethods {CREATE_USER, LOGIN, LOGOUT, ISLOGGEDIN, GET_USER_DETAILS, 
										ASSOCIATE_ROBOT, DISASSOCIATE_ROBOT, GET_ASSOCIATED_ROBOTS, 
										DISASSOCAITE_ALL_ROBOTS, REGISTER_NETWORK_STATE_LISTENER, 
										FORGET_PASSWORD, CHANGE_PASSWORD, CREATE_USER2, RESEND_VALIDATION_MAIL,
										IS_USER_VALIDATED};
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
		ACTION_MAP.put(ActionTypes.REGISTER_NETWORK_STATE_LISTENER, UserManagerPluginMethods.REGISTER_NETWORK_STATE_LISTENER);
		ACTION_MAP.put(ActionTypes.FORGET_PASSWORD, UserManagerPluginMethods.FORGET_PASSWORD);
		ACTION_MAP.put(ActionTypes.CHANGE_PASSWORD, UserManagerPluginMethods.CHANGE_PASSWORD);
		ACTION_MAP.put(ActionTypes.IS_USER_VALIDATED, UserManagerPluginMethods.IS_USER_VALIDATED);
	}

	private RobotDetailsPluginListener mRobotDetailsPluginListener;
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
		case REGISTER_NETWORK_STATE_LISTENER:
			LogHelper.log(TAG, "REGISTER_NETWORK_STATE_LISTENER action initiated");
			registerNetworkStateListener(context, jsonData, callbackId);
			break;	
		case FORGET_PASSWORD:
			LogHelper.log(TAG, "FORGET_PASSWORD action initiated");
			forgetPassword(context, jsonData, callbackId);
			break;	
		case CHANGE_PASSWORD:
			LogHelper.log(TAG, "CHANGE_PASSWORD action initiated");
			changePassword(context, jsonData, callbackId);
			break;
 		}
	}

	private UserManagerPluginMethods convertToInternalAction(String action) {
		UserManagerPluginMethods userManagerPluginMethod = ACTION_MAP.get(action);
		return userManagerPluginMethod;
	}


	private void forgetPassword (Context context, UserJsonData jsonData, final String callbackId) {
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		UserManager.getInstance(context).forgetPassword(email, new UserForgetPasswordListener() {
			
			@Override
			public void onComplete() {
				PluginResult result = new PluginResult(PluginResult.Status.OK);
				result.setKeepCallback(false);
				success(result, callbackId);
			}
			
			@Override
			public void onServerError(int statusCode, String errorMessage) {
				LogHelper.logD(TAG, "forgetPassword unsuccessful. Server Error");
				sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errorMessage);
			}
			
			@Override
			public void onNetworkError(String errorMessage) {
				LogHelper.logD(TAG, "forgetPassword unsuccessful. Network Error");
				sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errorMessage);
			}
		});
	}
	
	private void changePassword(Context context, UserJsonData jsonData, final String callbackId) {
		String emailId = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		LogHelper.logD(TAG, "emailId = " + emailId);
		String authToken = NeatoPrefs.getNeatoUserAuthToken(context);
		String currentPassword = jsonData.getString(JsonMapKeys.KEY_CURRENT_PASSWORD);
		String newPassword = jsonData.getString(JsonMapKeys.KEY_NEW_PASSWORD);
		UserManager.getInstance(context).changePassword(authToken, currentPassword, newPassword, new UserPasswordChangeListener() {
			
			@Override
			public void onComplete() {
				PluginResult result = new PluginResult(PluginResult.Status.OK);
				result.setKeepCallback(false);
				success(result, callbackId);
			}
			
			@Override
			public void onServerError(int statusCode, String errorMessage) {
				LogHelper.logD(TAG, "changePassword unsuccessful. Server Error");
				sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errorMessage);
			}
			
			@Override
			public void onNetworkError(String errorMessage) {
				LogHelper.logD(TAG, "changePassword unsuccessful. Network Error");
				sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errorMessage);

			}
		});
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
//		LogHelper.logD(TAG, "Password = " + password);
		
		UserManager.getInstance(context).loginUser(email, password, new UserDetailsListener() {

			@Override
			public void onUserDetailsReceived(UserItem userItem) {
				JSONObject userDetails = new JSONObject();
				
				try {
					if (userItem != null) {
						LogHelper.logD(TAG, "User Item = " + userItem);
						
						userDetails.put(JsonMapKeys.KEY_EMAIL, userItem.getEmail());
						userDetails.put(JsonMapKeys.KEY_USER_NAME, userItem.getName());
						userDetails.put(JsonMapKeys.KEY_USER_ID, userItem.getId());
						userDetails.put(JsonMapKeys.KEY_IS_VALIDATED_USER, userItem.isValidated());
						RobotCommandServiceManager.loginToXmpp(context);
						PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.OK, userDetails);
						LogHelper.logD(TAG, "Login successful. Start service and send Success plugin to user with user details");
						success(loginUserPluginResult, callbackId);
						String authToken = NeatoPrefs.getNeatoUserAuthToken(context);
						UserManager.getInstance(context).setUserAttributesOnServer(authToken, DeviceUtils.getUserAttributes(context));
					}
					else {
						sendError(callbackId, ErrorTypes.ERROR_TYPE_UNKNOWN, "Unknown Error");
					}
				} 
				catch (JSONException e) {
					LogHelper.log(TAG, "Exception in login", e);
					sendError(callbackId, ErrorTypes.JSON_PARSING_ERROR, e.getMessage());
				}

				
			}

			@Override
			public void onNetworkError(String errMessage) {
				NeatoPrefs.saveUserEmailId(context, null);
				LogHelper.logD(TAG, "Login unsuccessful. Network Error");
				sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
			}

			@Override
			public void onServerError(String errMessage) {
				NeatoPrefs.saveUserEmailId(context, null);
				LogHelper.logD(TAG, "Login unsuccessful. Server Error");
				sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
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
			}
		};
		
		TaskUtils.scheduleTask(task, 0);
		
	}

	private void createUser(final Context context, final UserJsonData jsonData, final String callbackId) {
		
		LogHelper.logD(TAG, "createUser Called");
//		LogHelper.logD(TAG, "Password = " + password);
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		String password = jsonData.getString(JsonMapKeys.KEY_PASSWORD);
		String name = jsonData.getString(JsonMapKeys.KEY_USER_NAME);

		LogHelper.logD(TAG, "JSON String: " + jsonData);

		LogHelper.logD(TAG, "Email:" + email + " Name: " + name);

		
		UserManager.getInstance(context).createUser(name, email, password,  new UserDetailsListener() {

			@Override
			public void onUserDetailsReceived(UserItem userItem) {
				JSONObject userDetails = new JSONObject();
				
				try {
					if (userItem != null) {
						userDetails.put(JsonMapKeys.KEY_EMAIL, userItem.getEmail());
						userDetails.put(JsonMapKeys.KEY_USER_NAME, userItem.getName());
						userDetails.put(JsonMapKeys.KEY_USER_ID, userItem.getId());		
						userDetails.put(JsonMapKeys.KEY_IS_VALIDATED_USER, true);
						
						RobotCommandServiceManager.loginToXmpp(context);
						PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.OK, userDetails);
						LogHelper.logD(TAG, "User created");
						success(loginUserPluginResult, callbackId);
						String authToken = NeatoPrefs.getNeatoUserAuthToken(context);
						UserManager.getInstance(context).setUserAttributesOnServer(authToken, DeviceUtils.getUserAttributes(context));
					}
					else {
						sendError(callbackId, ErrorTypes.ERROR_TYPE_UNKNOWN, "Unknown Error");
					}
				} 
				catch (JSONException e) {
					LogHelper.log(TAG, "Exception in createUser", e);
					sendError(callbackId, ErrorTypes.JSON_PARSING_ERROR, e.getMessage());
				}

				
			}

			@Override
			public void onNetworkError(String errMessage) {
				NeatoPrefs.saveUserEmailId(context, null);
				LogHelper.logD(TAG, "Failed to create new user. Network Error");
				sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				
			}

			@Override
			public void onServerError(String errMessage) {
				NeatoPrefs.saveUserEmailId(context, null);
				LogHelper.logD(TAG, "Failed to create new user. Server Error");
				sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);

			}
		});
	}
	
	private void createUser2(final Context context, final UserJsonData jsonData, final String callbackId) {		
		LogHelper.logD(TAG, "createUser2 Called");
//		LogHelper.logD(TAG, "Password = " + password);
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		String password = jsonData.getString(JsonMapKeys.KEY_PASSWORD);
		String name = jsonData.getString(JsonMapKeys.KEY_USER_NAME);

		LogHelper.logD(TAG, "JSON String: " + jsonData);

		LogHelper.logD(TAG, "Email:" + email + " Name: " + name);
		
		UserManager.getInstance(context).createUser(name, email, password,  new UserDetailsListener() {

			@Override
			public void onUserDetailsReceived(UserItem userItem) {
				JSONObject userDetails = new JSONObject();
				
				try {
					if (userItem != null) {
						userDetails.put(JsonMapKeys.KEY_EMAIL, userItem.getEmail());
						userDetails.put(JsonMapKeys.KEY_USER_NAME, userItem.getName());
						userDetails.put(JsonMapKeys.KEY_USER_ID, userItem.getId());	
						userDetails.put(JsonMapKeys.KEY_IS_VALIDATED_USER, userItem.isValidated());
					
						RobotCommandServiceManager.loginToXmpp(context);
						PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.OK, userDetails);
						LogHelper.logD(TAG, "User created");
						success(loginUserPluginResult, callbackId);
						String authToken = NeatoPrefs.getNeatoUserAuthToken(context);
						UserManager.getInstance(context).setUserAttributesOnServer(authToken, DeviceUtils.getUserAttributes(context));
					}
					else {
						sendError(callbackId, ErrorTypes.ERROR_TYPE_UNKNOWN, "Unknown Error");
					}
				} 
				catch (JSONException e) {
					LogHelper.log(TAG, "Exception in createUser", e);
					sendError(callbackId, ErrorTypes.JSON_PARSING_ERROR, e.getMessage());
				}

				
			}

			@Override
			public void onNetworkError(String errMessage) {
				NeatoPrefs.saveUserEmailId(context, null);
				LogHelper.logD(TAG, "Failed to create new user. Network Error");
				sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				
			}

			@Override
			public void onServerError(String errMessage) {
				NeatoPrefs.saveUserEmailId(context, null);
				LogHelper.logD(TAG, "Failed to create new user. Server Error");
				sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);

			}
		});
	}	
	
	private void isUserValidated(final Context context, UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "checking if user is authenticated");
		LogHelper.logD(TAG, "JSON String: " + jsonData);
		
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		LogHelper.logD(TAG, "Email: " + email);
		
		boolean result = UserManager.getInstance(context).isUserValidated(email);
		
		JSONObject validUser = new JSONObject();
		try {
			validUser.put(JsonMapKeys.KEY_IS_VALIDATED_USER, result);
		} catch (JSONException e) {
			LogHelper.logD(TAG, "Exception in is user validated");
			sendError(callbackId, ErrorTypes.JSON_PARSING_ERROR, e.getMessage());
			return;
		}
		
		// TODO: Need to call the WebAPI to send error cases from server
		PluginResult validUserPluginResult = new PluginResult(PluginResult.Status.OK, validUser);
		success(validUserPluginResult, callbackId);					
	}
	
	private void resendValidationMail(final Context context, UserJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "resend Validation Mail called");
		LogHelper.logD(TAG, "JSON String: " + jsonData);
		
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		LogHelper.logD(TAG, "Email: " + email);
		// TODO: Need to call the WebAPI to send the actual result
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
		success(pluginResult, callbackId);
	}
	
	public void getUserDetails(final Context context, UserJsonData jsonData, final String callbackId) {

		LogHelper.logD(TAG, "getUserDetails Called");
		LogHelper.logD(TAG, "JSON String: " + jsonData);

		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		String authKey = NeatoPrefs.getNeatoUserAuthToken(context);
		LogHelper.logD(TAG, "Email:" + email + " authKey: " + authKey);		

		UserManager.getInstance(context).getUserDetails(email, authKey,  new UserDetailsListener() {
			@Override
			public void onUserDetailsReceived(UserItem userItem) {
				JSONObject userDetails = new JSONObject();
				if (userItem != null) {
					try {
						
						userDetails.put(JsonMapKeys.KEY_EMAIL, userItem.getEmail());
						userDetails.put(JsonMapKeys.KEY_USER_NAME, userItem.getName());
						userDetails.put(JsonMapKeys.KEY_USER_ID, userItem.getId());
						//Not quite sure whether we should save this at this time. As it is it is going to 
						//be saved at the time of login. Still doing it for safety.
						

						PluginResult getUserDetailsPluginResult = new  PluginResult(PluginResult.Status.OK, userDetails);
						LogHelper.logD(TAG, "Get User detail succeeded.");
						success(getUserDetailsPluginResult, callbackId);
						return;
					} 
					catch (JSONException e) { 
						LogHelper.log(TAG, "Exception in getUserDetails", e);
						sendError(callbackId, ErrorTypes.JSON_PARSING_ERROR, e.getMessage());
						return;
					}

				}
				else {
					sendError(callbackId, ErrorTypes.ERROR_TYPE_UNKNOWN, "Unknown Error");
				}	
			}

			@Override
			public void onNetworkError(String errMessage) {
				NeatoPrefs.saveUserEmailId(context, null);
				LogHelper.logD(TAG, "Failed to get user details. Network Error");
				sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);

			}

			@Override
			public void onServerError(String errMessage) {
				NeatoPrefs.saveUserEmailId(context, null);
				LogHelper.logD(TAG, "Failed to get user details. Server Error");
				sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);

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

		UserManager.getInstance(context).associateRobot(robotId, email, new UserRobotAssociateDisassociateListener() {
			
			@Override
			public void onServerError(String errorMessage) {
				LogHelper.logD(TAG, "associateRobot robot Error: " + errorMessage);
				sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, "Server Error");
			}
			
			@Override
			public void onNetworkError(String errorMessage) {
				LogHelper.logD(TAG, "associateRobot network Error: " + errorMessage);
				sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, "Network Error");
			}
			
			@Override
			public void onComplete() {
				PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK);
				LogHelper.logD(TAG, "associateRobot successful");
				success(pluginResult, callbackId);
			}
		});

	}

	private void disassociateAllRobots(Context context, UserJsonData jsonData, final String callbackId) {		
		LogHelper.logD(TAG, "disassociateAllRobots Called");
		
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		if (TextUtils.isEmpty(email)) {
			email = NeatoPrefs.getUserEmailId(context);
		}
		LogHelper.logD(TAG, "JSON String: " + jsonData);
		
		LogHelper.logD(TAG, "Disassociate all robots for email id: " + email);
		UserManager.getInstance(context).disassociateAllRobots(email, new UserRobotAssociateDisassociateListener() {
			
			@Override
			public void onServerError(String errorMessage) {
				LogHelper.logD(TAG, "disassociateAllRobots robot Error: " + errorMessage);
				sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errorMessage);
			}
			
			@Override
			public void onNetworkError(String errorMessage) {
				LogHelper.logD(TAG, "disassociateAllRobots network Error: " + errorMessage);
				sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errorMessage);
			}
			
			@Override
			public void onComplete() {
				PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK);
				LogHelper.logD(TAG, "disassociateAllRobots successful");
				success(pluginResult, callbackId);
			}
		});
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

		UserManager.getInstance(context).disassociateRobot(robotId, email, new UserRobotAssociateDisassociateListener() {
			
			@Override
			public void onServerError(String errorMessage) {
				LogHelper.logD(TAG, "disassociateRobot robot Error: " + errorMessage);
				sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errorMessage);
			}
			
			@Override
			public void onNetworkError(String errorMessage) {
				LogHelper.logD(TAG, "disassociateRobot network Error: " + errorMessage);
				sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errorMessage);
			}
			
			@Override
			public void onComplete() {
				PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK);
				LogHelper.logD(TAG, "disassociateRobot successful");
				success(pluginResult, callbackId);
			}
		});
	}

	private void getAssociatedRobots(Context context, UserJsonData jsonData, String callbackId) {
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);		
		
		if (TextUtils.isEmpty(email)) {
			email = NeatoPrefs.getUserEmailId(context);
		}
		
		String auth_token = NeatoPrefs.getNeatoUserAuthToken(context);
		
		LogHelper.logD(TAG, "getAssociatedRobots - JSON String: " + jsonData);
		
		mRobotDetailsPluginListener = new RobotDetailsPluginListener(callbackId);
		UserManager.getInstance(context).getAssociatedRobots(email, auth_token, mRobotDetailsPluginListener);
	}
	
	// TODO: for now this is in UserManager. Should be moved to appropriate plugin.
	private void registerNetworkStateListener(Context context, UserJsonData jsonData, String callbackId) {
		
		// TODO : Club all the Broadcast listeners for network state change together.
		BroadcastReceiver mWifiStateChange = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
					NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
					if(networkInfo.isConnected()) {
						// Network is connected
						LogHelper.logD(TAG, "Connected to a network");
						dispatchEvent(EventTypes.WIFI_CONNECTED);
					}
				} 
				else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
					if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI && ! networkInfo.isConnected()) {
						// Network is disconnected
						LogHelper.logD(TAG, "Disconnecting from the network");
						dispatchEvent(EventTypes.WIFI_DISCONNECTED);
					}
				}
			}
		};
		context.registerReceiver(mWifiStateChange, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
		context.registerReceiver(mWifiStateChange, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK);
		LogHelper.logD(TAG, "Network state listener registerred");
		success(pluginResult, callbackId);
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
		public static final String REGISTER_NETWORK_STATE_LISTENER = "registerNetworkStateListener";
		public static final String FORGET_PASSWORD	= "forgetPassword";
		public static final String CHANGE_PASSWORD	= "changePassword";
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
	
	private void dispatchEvent(String eventName) {
		LogHelper.log(TAG, "Event called");
		String javascriptTemplate = "var e = document.createEvent('Events');\n" +
				"e.initEvent('"+eventName+"');\n" +	                    
				"document.dispatchEvent(e);";
		this.sendJavascript(javascriptTemplate);		
	}
	
	private class RobotDetailsPluginListener implements AssociatedRobotDetailsListener {

		private String mCallBackId;

		RobotDetailsPluginListener(String callbackId) {
			mCallBackId = callbackId;
		}
		@Override
		public void onRobotDetailsReceived(ArrayList<RobotItem> robotList) {

			JSONArray robots = new JSONArray();
			PluginResult pluginResult;
			if (robotList != null) {
				for (RobotItem item: robotList) {
					try {

						JSONObject robot = new JSONObject();
						robot.put(JsonMapKeys.KEY_ROBOT_ID, item.getSerialNumber());
						robot.put(JsonMapKeys.KEY_ROBOT_NAME, item.getName());
						robots.put(robot);
					} catch (JSONException e) {
						LogHelper.log(TAG, "Exception in RobotDetailsPluginListener", e);
					}
				}
				LogHelper.logD(TAG, "Success");
			} 
			else {
				LogHelper.logD(TAG, "No robots associated");
			}
			pluginResult=  new  PluginResult(PluginResult.Status.OK, robots);
			success(pluginResult, mCallBackId);
		}

		@Override
		public void onNetworkError(String errMessage) {
			JSONObject error = getErrorJsonObject(ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
			PluginResult robotDetailsPluginResult = new  PluginResult(PluginResult.Status.ERROR, error);
			LogHelper.logD(TAG, "Error: " + errMessage);
			error(robotDetailsPluginResult, mCallBackId);
		}

		@Override
		public void onServerError(String errMessage) {
			JSONObject error = getErrorJsonObject(ErrorTypes.ERROR_SERVER_ERROR, errMessage);
			PluginResult associateRobotPluginResult = new  PluginResult(PluginResult.Status.ERROR, error);
			LogHelper.logD(TAG, "get robot details Error: " +errMessage);
			error(associateRobotPluginResult, mCallBackId);
		}
	}
}
