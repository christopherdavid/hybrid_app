package com.neatorobotics.android.slide.framework.database;

/*
 * Class contains constants referring to column names & table names to communicate with 
 * Neato database 
 */
public class DBCommon {
	public static final String TABLE_NAME_USER_INFO  = "user_info";
	public static final String TABLE_NAME_ROBOT_INFO = "robot_info";
	public static final String TABLE_NAME_CLEANING_SETTINGS = "cleaning_settings";
	public static final String TABLE_NAME_ATLAS_INFO = "atlas_info";
	public static final String TABLE_NAME_GRID_INFO  = "grid_info";
	public static final String TABLE_NAME_ROBOT_SCHEDULE_IDS = "robot_schedule_ids";
	public static final String TABLE_NAME_SCHEDULE_INFO = "schedule_info";

	// user_info table column names 
	public static final String COL_NAME_USER_DB_ID 		= "_id";
	public static final String COL_NAME_USER_ID 		= "userId";
	public static final String COL_NAME_USER_NAME 		= "name";
	public static final String COL_NAME_USER_EMAIL 		= "email";
	public static final String COL_NAME_USER_CHAT_ID 	= "chatId";
	public static final String COL_NAME_USER_CHAT_PWD 	= "chatPwd";
	
	// robot_info table column names 
	public static final String COL_NAME_ROBOT_DB_ID 		= "_id";
	public static final String COL_NAME_ROBOT_ID 			= "robotId";
	public static final String COL_NAME_ROBOT_SERIAL_ID 	= "serialId";
	public static final String COL_NAME_ROBOT_NAME 			= "name";	
	public static final String COL_NAME_ROBOT_CHAT_ID 		= "chatId";
	// public static final String COL_NAME_ROBOT_CHAT_PWD 		= "chatPwd";

	// cleaning_settings table column names
	public static final String COL_NAME_SPOT_AREA_LENGTH 	= "spotAreaLength";
	public static final String COL_NAME_SPOT_AREA_HEIGHT 	= "spotAreaHeight";

	// atlas_info table column names
	public static final String COL_NAME_ATLAS_ID 			= "atlasId";
	public static final String COL_NAME_ATLAS_XML_VERSION 	= "xmlVersion";	
	public static final String COL_NAME_ATLAS_XML_FILE_PATH = "xmlFilePath";
	
	// grid_info table column names
	public static final String COL_NAME_GRID_ID 			= "gridId";
	public static final String COL_NAME_GRID_DATA_VERSION 	= "dataVersion";	
	public static final String COL_NAME_GRID_DATA_FILE_PATH = "dataFilePath";
	
	// robot schedule table
	public static final String COL_NAME_ADVANCED_SCHEDULE_ID  = "advancedUUID";
	public static final String COL_NAME_BASIC_SCHEDULE_ID 	= "basicUUID"	;
	// schedule info table column names
	public static final String COL_NAME_SCHEDULE_SERVER_ID 		= "scheduleId";
	public static final String COL_NAME_SCHEDULE_ID 		= "scheduleUuid";
	public static final String COL_NAME_SCHEDULE_VERSION 	= "scheduleVersion";	
	public static final String COL_NAME_SCHEDULE_TYPE 		= "scheduleType";
	public static final String COL_NAME_SCHEDULE_DATA 		= "scheduleData";
}
