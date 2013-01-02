package com.neatorobotics.android.slide.framework.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class NeatoPrefs {
	public static final String PREFERANCE_NAME = "NeatoPref";
	

	private static final String USER_EMAIL_ID_KEY = "user_email_id";
	private static final String KEY_NEATO_USER_AUTH_TOKEN = "neato_user_auth_token";
		
	
	private static final String PEER_CONNECTION_STATUS = "peer_conn_status";
	private static final String PEER_IP_ADDRESS = "peer_ip_address";
	private static final String MANAGED_ROBOT_SERIAL_ID = "managed_robot_serial_id";
	
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
	

	
	
	public static String getUserEmailId(Context context)
	{
		return getPreferenceStrValue(context, USER_EMAIL_ID_KEY);
	}
	
	
	
	public static void saveUserEmailId(Context context, String emailId)
	{
		savePreference(context, USER_EMAIL_ID_KEY, emailId);
	}
	
	public static void clearUserEmailAndAuthToken(Context context)	{
		savePreference(context, USER_EMAIL_ID_KEY, "");
		savePreference(context, KEY_NEATO_USER_AUTH_TOKEN, "");
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
	
	public static boolean saveManagedRobotSerialId(Context context, String serialId) {
		return savePreference(context, NeatoPrefs.MANAGED_ROBOT_SERIAL_ID , serialId);
	}
	
	public static String getManagedRobotSerialId(Context context) {
		return getPreferenceStrValue(context, NeatoPrefs.MANAGED_ROBOT_SERIAL_ID);
	}
	
	public static boolean clearManagedRobotSerialId(Context context) {
		return savePreference(context, NeatoPrefs.MANAGED_ROBOT_SERIAL_ID, "");
	}
}