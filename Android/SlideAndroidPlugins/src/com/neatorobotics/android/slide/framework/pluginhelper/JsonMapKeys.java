package com.neatorobotics.android.slide.framework.pluginhelper;


//JSON data keys which will be used by javasacript to send the data in json array object.
public class JsonMapKeys {
	
	// Used by user plugin
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_USER_NAME = "username";
	
	// Used by robot plugin
	public static final String KEY_COMMAND = "command";
	public static final String KEY_ROBOT_SERIAL_ID = "serialid";
	public static final String KEY_USE_XMPP = "useXMPP";
	public static final String KEY_ROBOT_NAME = "robot_name";
	public static final String KEY_ROBOT_IP_ADDRESS = "robot_ipaddress";
	
	//Used by scheduler
	public static final String KEY_DAY = "day";
	public static final String KEY_START_TIME_HRS = "startTimeHrs";
	public static final String KEY_END_TIME_HRS = "endTimeHrs";
	public static final String KEY_START_TIME_MINS = "startTimeMins";
	public static final String KEY_END_TIME_MINS = "endTimeMins";
	public static final String KEY_EVENT_TYPE = "eventType";
	public static final String KEY_AREA = "area";

}
