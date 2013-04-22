package com.neatorobotics.android.slide.framework.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class NeatoPrefs {
	public static final String PREFERANCE_NAME = "NeatoPref";
	
	private static final String USER_EMAIL_ID_KEY = "user_email_id";
	private static final String KEY_NEATO_USER_AUTH_TOKEN = "neato_user_auth_token";
	private static final String PEER_CONNECTION_STATUS = "peer_conn_status";
	private static final String MANAGED_ROBOT_SERIAL_ID = "managed_robot_serial_id";

	// TODO: This is just a temporary arrangement till everybody switches to the new command structure
	// 
	private static final String USE_NEW_COMMAND_STRUCTURE = "useNewCommandStructure";
	private static final boolean DEFAULT_USE_NEW_COMMAND_PACKET_STRUCTURE = false;

	public static boolean savePreferenceBooleanValue(Context context, String preferenceName, boolean preferenceValue) {
		SharedPreferences preferences = context.getSharedPreferences(NeatoPrefs.PREFERANCE_NAME, 0);
		Editor preferencesEditor = preferences.edit();
		preferencesEditor.putBoolean(preferenceName, preferenceValue);
		boolean result = preferencesEditor.commit();

		return result;
	} 
	
	private static boolean getPreferenceBooleanValue(Context context, String preferenceName, boolean defaultValue) {
		SharedPreferences preferences = context.getSharedPreferences(NeatoPrefs.PREFERANCE_NAME, 0);
		boolean preferenceValue = preferences.getBoolean(preferenceName, defaultValue);
		return preferenceValue;
	}

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

	public static boolean shouldUseNewCommandPacketStruture(Context context)
	{
		return getPreferenceBooleanValue(context, USE_NEW_COMMAND_STRUCTURE, DEFAULT_USE_NEW_COMMAND_PACKET_STRUCTURE);
	}

	public static boolean saveUseNewCommandStruture(Context context, boolean useNewCommandStructure)
	{
		return savePreferenceBooleanValue(context, USE_NEW_COMMAND_STRUCTURE, useNewCommandStructure);
	}

	public static boolean clearManagedRobotSerialId(Context context) {
		return savePreference(context, NeatoPrefs.MANAGED_ROBOT_SERIAL_ID, "");
	}
}