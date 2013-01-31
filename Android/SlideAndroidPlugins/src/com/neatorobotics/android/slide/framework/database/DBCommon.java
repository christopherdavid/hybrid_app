package com.neatorobotics.android.slide.framework.database;

/*
 * Class contains constants referring to column names & table names to communicate with 
 * Neato database 
 */
public class DBCommon {
	public static final String TABLE_NAME_USER_INFO  = "user_info";
	public static final String TABLE_NAME_ROBOT_INFO = "robot_info";
	public static final String TABLE_NAME_ATLAS_INFO = "atlas_info";
	public static final String TABLE_NAME_GRID_INFO  = "grid_info";
	
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
		
	// atlas_info table column names
	public static final String COL_NAME_ATLAS_ID 			= "atlasId";
	public static final String COL_NAME_ATLAS_XML_VERSION 	= "xmlVersion";	
	public static final String COL_NAME_ATLAS_XML_FILE_PATH = "xmlFilePath";
	
	// grid_info table column names
	public static final String COL_NAME_GRID_ID 			= "gridId";
	public static final String COL_NAME_GRID_DATA_VERSION 	= "dataVersion";	
	public static final String COL_NAME_GRID_DATA_FILE_PATH = "dataFilePath";
}
