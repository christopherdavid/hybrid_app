package com.neatorobotics.android.slide.framework.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;

public class NeatoPrefs {
	public static final String PREFERANCE_NAME = "NeatoPref";
	
	private static final String JABBER_USER_ID = "user_chat_id";
	private static final String JABBER_USER_PWD = "user__chat_pwd";
	private static final String USER_EMAIL_ID_KEY = "user_email_id";
	private static final String KEY_NEATO_USER_AUTH_TOKEN = "neato_user_auth_token";
	private static final String KEY_USER_ID		= "userId";
	private static final String KEY_USER_NAME		= "userName";
	
	
	private static final String KEY_ROBOT_ID = "robot_id";
	private static final String KEY_ROBOT_NAME = "robot_name";
	private static final String KEY_ROBOT_CHAT_ID = "robot_chat_id";
	private static final String KEY_ROBOT_SERIAL_ID = "robot_serial_id";
	private static final String KEY_ROBOT_CHAT_PWD = "robot_chat_pwd";
	private static final String PEER_CONNECTION_STATUS = "peer_conn_status";
	private static final String PEER_IP_ADDRESS = "peer_ip_address";
	
	private static final String TAG = NeatoPrefs.class.getSimpleName();
	
	private static boolean savePreference(Context context, String preferenceName, String preferenceValue) {
		SharedPreferences preferences = context.getSharedPreferences(NeatoPrefs.PREFERANCE_NAME, 0);
		Editor preferencesEditor = preferences.edit();
		preferencesEditor.putString(preferenceName, preferenceValue);
		boolean result = preferencesEditor.commit();
		
		return result;
	}
		
	private static String getPreferenceStrValue(Context context, String preferenceName) {
		SharedPreferences preferences = context.getSharedPreferences(NeatoPrefs.PREFERANCE_NAME, 0);
		String preferenceValue = preferences.getString(preferenceName, null);
		
		return preferenceValue;
	}
	
	@SuppressWarnings("unused")
	private static String getPreferenceStrValue(Context context, String preferenceName, String defaultValue) {
		SharedPreferences preferences = context.getSharedPreferences(NeatoPrefs.PREFERANCE_NAME, 0);
		String preferenceValue = preferences.getString(preferenceName, defaultValue);
		
		return preferenceValue;
	}
	
	public static boolean clearPreferences(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(PREFERANCE_NAME, 0);
		Editor prefsEditor = preferences.edit();
		prefsEditor.clear();
		boolean result = prefsEditor.commit();
		return result;
	}
	

	public static boolean saveJabberId(Context context, String jabberId) {
		return savePreference(context, NeatoPrefs.JABBER_USER_ID , jabberId);
	}
	
	public static boolean saveJabberPwd(Context context, String jabberPwd) {
		return savePreference(context, NeatoPrefs.JABBER_USER_PWD , jabberPwd);
	}
	
	public static String getJabberId(Context context) {
		return getPreferenceStrValue(context, NeatoPrefs.JABBER_USER_ID);
	}
	
	public static String getJabberPwd(Context context) {
		return getPreferenceStrValue(context, NeatoPrefs.JABBER_USER_PWD);
	}
	
	public static String getUserEmailId(Context context)
	{
		return getPreferenceStrValue(context, USER_EMAIL_ID_KEY);
	}
	
	public static boolean isUserLoggedIn(Context context)
	{
		String emailId = NeatoPrefs.getUserEmailId(context);
		return ((emailId != null) && (emailId.length() > 0)) ? true:false;
	}
	
	public static UserItem getUserDetail(Context context)
	{
		if (isUserLoggedIn(context)) {
			UserItem userItem = new UserItem();
			userItem.setEmail(getUserEmailId(context));
			userItem.setName(getPreferenceStrValue(context, KEY_USER_NAME));
			userItem.setChatId(getPreferenceStrValue(context, JABBER_USER_ID));
			userItem.setId(getPreferenceStrValue(context, KEY_USER_ID));
			userItem.setChatPwd(getPreferenceStrValue(context, JABBER_USER_PWD));
			
			return userItem;
		}
		else {
			LogHelper.log(TAG, "User is not logged in");
		}
		
		return null;
	}
	
	
	public static void saveUserDetail(Context context, UserItem userItem)
	{
		savePreference(context, KEY_USER_ID, userItem.getId());
		savePreference(context, KEY_USER_NAME, userItem.getName());
		savePreference(context, USER_EMAIL_ID_KEY, userItem.getEmail());
		savePreference(context, JABBER_USER_ID, userItem.getChatId());
		savePreference(context, JABBER_USER_PWD, userItem.getChatPwd());
	}
	
	public static void saveUserEmailId(Context context, String emailId)
	{
		savePreference(context, USER_EMAIL_ID_KEY, emailId);
	}
	
	public static void logoutUser(Context context)
	{
		savePreference(context, USER_EMAIL_ID_KEY, "");
		savePreference(context, KEY_NEATO_USER_AUTH_TOKEN, "");
		savePreference(context, KEY_USER_ID, "");
		savePreference(context, KEY_ROBOT_CHAT_ID, "");
		savePreference(context, KEY_ROBOT_CHAT_PWD, "");
		savePreference(context, KEY_USER_NAME, "");
		
	}
	
	public static void saveRobotInformation(Context context, RobotItem robotItem)
	{
		LogHelper.log(TAG, "saveRobotInformation called");
		LogHelper.log(TAG, "Robot Items = " + robotItem);
		savePreference(context, KEY_ROBOT_ID, robotItem.getId());
		savePreference(context, KEY_ROBOT_NAME, robotItem.getName());
		savePreference(context, KEY_ROBOT_SERIAL_ID, robotItem.getSerialNumber());
		savePreference(context, KEY_ROBOT_CHAT_ID, robotItem.getChatId());
		savePreference(context, KEY_ROBOT_CHAT_PWD, robotItem.getChatPwd());
	}
	
	public static RobotItem getRobotItem(Context context)
	{
		LogHelper.log(TAG, "getRobotItem called");
		String robotId = getPreferenceStrValue(context, KEY_ROBOT_ID);
		LogHelper.log(TAG, "robotId = " + robotId);
		if (TextUtils.isEmpty(robotId)) {
			return null;
		}
		RobotItem robotItem = new RobotItem();
		robotItem.setId(robotId);
		robotItem.setName(getPreferenceStrValue(context, KEY_ROBOT_NAME));
		robotItem.setSerialNumber(getPreferenceStrValue(context, KEY_ROBOT_SERIAL_ID));
		robotItem.setChatId(getPreferenceStrValue(context, KEY_ROBOT_CHAT_ID));
		robotItem.setChatPwd(getPreferenceStrValue(context, KEY_ROBOT_CHAT_PWD));
		
		LogHelper.log(TAG, "Robot Items = " + robotItem);
		
		return robotItem;
	}
	
	public static void clearRobotInformation(Context context)
	{
		savePreference(context, KEY_ROBOT_ID, null);
		savePreference(context, KEY_ROBOT_NAME, null);
		savePreference(context, KEY_ROBOT_SERIAL_ID, null);
		savePreference(context, KEY_ROBOT_CHAT_ID, null);
		savePreference(context, KEY_ROBOT_CHAT_PWD, null);
	}
	
	public static void setPeerConnectionStatus(Context context, boolean enable) {
		SharedPreferences preferences = context.getSharedPreferences(NeatoPrefs.PREFERANCE_NAME, 0);
		Editor preferencesEditor = preferences.edit();
		preferencesEditor.putBoolean(PEER_CONNECTION_STATUS, enable);
		preferencesEditor.commit();
	}

	public static boolean getPeerConnectionStatus(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(NeatoPrefs.PREFERANCE_NAME, 0);
		boolean preferenceValue = preferences.getBoolean(PEER_CONNECTION_STATUS, false);
		
		return preferenceValue;
	}

	public static boolean setPeerIpAddress(Context context, String peer_ip_address) {
		return savePreference(context, NeatoPrefs.PEER_IP_ADDRESS , peer_ip_address);
	}

	public static String getPeerIpAddress(Context context) {
		return getPreferenceStrValue(context, NeatoPrefs.PEER_IP_ADDRESS);
	}

	public static boolean saveNeatoUserAuthToken(Context context, String authToken) {
		return savePreference(context, NeatoPrefs.KEY_NEATO_USER_AUTH_TOKEN , authToken);
	}
	
	public static String getNeatoUserAuthToken(Context context) {
		return getPreferenceStrValue(context, NeatoPrefs.KEY_NEATO_USER_AUTH_TOKEN);
	}
	
}