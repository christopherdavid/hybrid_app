package com.neatorobotics.android.slide.framework.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;

public class NeatoPrefs {
	public static final String PREFERANCE_NAME = "NeatoPref";
	public static final String JABBER_USER_ID = "user_id";
	public static final String JABBER_USER_PWD = "user_pwd";
	
	private static final String USER_EMAIL_ID_KEY = "user_email_id";
	
	
	private static final String KEY_ROBOT_ID = "robot_id";
	private static final String KEY_ROBOT_NAME = "robot_name";
	private static final String KEY_ROBOT_CHAT_ID = "robot_chat_id";
	private static final String KEY_ROBOT_SERIAL_ID = "robot_serial_id";
	private static final String KEY_ROBOT_CHAT_PWD = "robot_chat_pwd";
	private static final String KEY_NEATO_USER_AUTH_TOKEN = "neato_user_auth_token";
	private static final String PEER_CONNECTION_STATUS = "peer_conn_status";
	
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
	
	public static void saveUserEmailId(Context context, String emailId)
	{
		savePreference(context, USER_EMAIL_ID_KEY, emailId);
	}
	
	public static void saveRobotInformation(Context context, RobotItem robotItem)
	{
		savePreference(context, KEY_ROBOT_ID, robotItem.getId());
		savePreference(context, KEY_ROBOT_NAME, robotItem.getName());
		savePreference(context, KEY_ROBOT_SERIAL_ID, robotItem.getSerialNumber());
		savePreference(context, KEY_ROBOT_CHAT_ID, robotItem.getChatId());
		savePreference(context, KEY_ROBOT_CHAT_PWD, robotItem.getChatPwd());
	}
	
	public static RobotItem getRobotItem(Context context)
	{
		String robotId = getPreferenceStrValue(context, KEY_ROBOT_ID);
		if (TextUtils.isEmpty(robotId)) {
			return null;
		}
		RobotItem robotItem = new RobotItem();
		robotItem.setId(robotId);
		robotItem.setName(getPreferenceStrValue(context, KEY_ROBOT_NAME));
		robotItem.setSerialNumber(getPreferenceStrValue(context, KEY_ROBOT_SERIAL_ID));
		robotItem.setChatId(getPreferenceStrValue(context, KEY_ROBOT_CHAT_ID));
		robotItem.setChatPwd(getPreferenceStrValue(context, KEY_ROBOT_CHAT_PWD));
		
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
	

	public static boolean saveNeatoUserAuthToken(Context context, String authToken) {
		return savePreference(context, NeatoPrefs.KEY_NEATO_USER_AUTH_TOKEN , authToken);
	}
	
	public static String getNeatoUserAuthToken(Context context) {
		return getPreferenceStrValue(context, NeatoPrefs.KEY_NEATO_USER_AUTH_TOKEN);
	}
	
}