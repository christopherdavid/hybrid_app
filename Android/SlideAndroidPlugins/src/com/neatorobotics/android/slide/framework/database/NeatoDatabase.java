package com.neatorobotics.android.slide.framework.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class NeatoDatabase extends SQLiteOpenHelper {
    private static final String TAG = NeatoDatabase.class.getSimpleName();

    private static final int DB_VERSION = 7;
    private static final String DB_NAME = "neato_plugin_smart_apps.db";

    // Table names
    public interface Tables {
        public static final String TABLE_NAME_USER_INFO = "user_info";
        public static final String TABLE_NAME_ROBOT_INFO = "robot_info";
        public static final String TABLE_NAME_CLEANING_SETTINGS = "cleaning_settings";
        public static final String TABLE_NAME_NOTIFICATION_SETTINGS = "notification_settings";
        public static final String TABLE_NAME_ATLAS_INFO = "atlas_info";
        public static final String TABLE_NAME_GRID_INFO = "grid_info";
        public static final String TABLE_NAME_ROBOT_SCHEDULE_IDS = "robot_schedule_ids";
        public static final String TABLE_NAME_SCHEDULE_INFO = "schedule_info";
        public static final String TABLE_NAME_ROBOT_PROFILE_PARAMS = "robot_profile_params";
    }

    // user_info table column names
    public interface UserInfoColumns {
        public static final String COL_NAME_USER_DB_ID = "_id";
        public static final String COL_NAME_USER_ID = "userId";
        public static final String COL_NAME_USER_NAME = "name";
        public static final String COL_NAME_USER_EMAIL = "email";
        public static final String COL_NAME_USER_CHAT_ID = "chatId";
        public static final String COL_NAME_USER_CHAT_PWD = "chatPwd";
    }

    // robot_info table column names
    public interface RobotInfoColumns {
        public static final String COL_NAME_ROBOT_DB_ID = "_id";
        public static final String COL_NAME_ROBOT_ID = "robotId";
        public static final String COL_NAME_ROBOT_SERIAL_ID = "serialId";
        public static final String COL_NAME_ROBOT_NAME = "name";
        public static final String COL_NAME_ROBOT_CHAT_ID = "chatId";
    }

    // cleaning_settings table column names
    public interface CleaningSettingsColumns {
        public static final String COL_NAME_ROBOT_ID = "robotId";
        public static final String COL_NAME_SPOT_AREA_LENGTH = "spotAreaLength";
        public static final String COL_NAME_SPOT_AREA_HEIGHT = "spotAreaHeight";
        public static final String COL_NAME_CLEANING_CATEGORY = "cleaningCategory";
    }

    // notification_settings table column names
    public interface NotificationSettingsColumns {
        public static final String COL_NAME_EMAIL = "email";
        public static final String COL_NAME_NOTIFICATION_JSON = "notificationsJson";
    }

    // atlas_info table column names
    public interface AtlasInfoColumns {
        public static final String COL_NAME_ATLAS_ID = "atlasId";
        public static final String COL_NAME_ATLAS_XML_VERSION = "xmlVersion";
        public static final String COL_NAME_ATLAS_XML_FILE_PATH = "xmlFilePath";
    }

    // grid_info table column names
    public interface GridInfoColumns {
        public static final String COL_NAME_GRID_ID = "gridId";
        public static final String COL_NAME_GRID_DATA_VERSION = "dataVersion";
        public static final String COL_NAME_GRID_DATA_FILE_PATH = "dataFilePath";
    }

    // robot schedule table
    public interface ScheduleIdsColumns {
        public static final String COL_NAME_ROBOT_ID = "robotId";
        public static final String COL_NAME_BASIC_SCHEDULE_ID = "basicUUID";
    }

    // schedule info table column names
    public interface ScheduleInfoColumns {
        public static final String COL_NAME_SCHEDULE_SERVER_ID = "scheduleId";
        public static final String COL_NAME_SCHEDULE_ID = "scheduleUuid";
        public static final String COL_NAME_SCHEDULE_VERSION = "scheduleVersion";
        public static final String COL_NAME_SCHEDULE_TYPE = "scheduleType";
        public static final String COL_NAME_SCHEDULE_DATA = "scheduleData";
    }

    // Robot profile parameters
    public interface RobotProfileParameters {
        public static final String COL_NAME_ROBOT_PROFILE_DB_ID = "_id";
        public static final String COL_NAME_ROBOT_ID = "robotId";
        public static final String COL_NAME_ROBOT_PARAM_KEY = "robotParamKey";
        public static final String COL_NAME_ROBOT_PARAM_TIMESTAMP = "robotParamTimestamp";
    }

    public NeatoDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        LogHelper.log(TAG, "Neato DBOpenHelper - DB Version = " + DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogHelper.log(TAG, "The Neato database code expects a newer version of the database. Old db version - "
                + oldVersion + ", New db version - " + newVersion
                + ". The old database will be dropped and a new database will be created");

        dropTables(db);

        onCreate(db);
    }

	private void createTables(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " +
				Tables.TABLE_NAME_USER_INFO 
				+ "(" 
				+ UserInfoColumns.COL_NAME_USER_DB_ID		+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ UserInfoColumns.COL_NAME_USER_ID			+ " TEXT, "
				+ UserInfoColumns.COL_NAME_USER_NAME		+ " TEXT, "
				+ UserInfoColumns.COL_NAME_USER_EMAIL		+ " TEXT, "
				+ UserInfoColumns.COL_NAME_USER_CHAT_ID		+ " TEXT, "
				+ UserInfoColumns.COL_NAME_USER_CHAT_PWD	+ " TEXT )");
			
			db.execSQL("CREATE TABLE IF NOT EXISTS " +
				Tables.TABLE_NAME_ROBOT_INFO 
				+ "(" 
				+ RobotInfoColumns.COL_NAME_ROBOT_DB_ID			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ RobotInfoColumns.COL_NAME_ROBOT_ID			+ " TEXT, "
				+ RobotInfoColumns.COL_NAME_ROBOT_SERIAL_ID		+ " TEXT, "
				+ RobotInfoColumns.COL_NAME_ROBOT_NAME			+ " TEXT, "	
				+ RobotInfoColumns.COL_NAME_ROBOT_CHAT_ID		+ " TEXT )");
			
			
			db.execSQL("CREATE TABLE IF NOT EXISTS " +
				Tables.TABLE_NAME_ATLAS_INFO 
				+ "(" 
				+ AtlasInfoColumns.COL_NAME_ATLAS_ID			+ " TEXT PRIMARY KEY, "
				+ AtlasInfoColumns.COL_NAME_ATLAS_XML_VERSION	+ " TEXT, "		
				+ AtlasInfoColumns.COL_NAME_ATLAS_XML_FILE_PATH		+ " TEXT ) ");
			
			db.execSQL("CREATE TABLE IF NOT EXISTS " +
				Tables.TABLE_NAME_GRID_INFO 
				+ "(" 
				+ GridInfoColumns.COL_NAME_GRID_ID				+ " TEXT PRIMARY KEY, "
				+ GridInfoColumns.COL_NAME_GRID_DATA_VERSION	+ " TEXT, "		
				+ GridInfoColumns.COL_NAME_GRID_DATA_FILE_PATH	+ " TEXT ) ");
			
			db.execSQL("CREATE TABLE IF NOT EXISTS " +
				Tables.TABLE_NAME_SCHEDULE_INFO 
				+ "(" 
				+ ScheduleInfoColumns.COL_NAME_SCHEDULE_ID			+ " TEXT PRIMARY KEY, "
				+ ScheduleInfoColumns.COL_NAME_SCHEDULE_SERVER_ID	+ " TEXT, "
				+ ScheduleInfoColumns.COL_NAME_SCHEDULE_VERSION		+ " TEXT, "		
				+ ScheduleInfoColumns.COL_NAME_SCHEDULE_DATA		+ " TEXT, "
				+ ScheduleInfoColumns.COL_NAME_SCHEDULE_TYPE 		+ " TEXT )");
			
			db.execSQL("CREATE TABLE IF NOT EXISTS " +
				Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS
				+ "("
				+ ScheduleIdsColumns.COL_NAME_ROBOT_ID				+ " TEXT PRIMARY KEY, "
				+ ScheduleIdsColumns.COL_NAME_BASIC_SCHEDULE_ID 	+ " TEXT ) ");
			
			db.execSQL("CREATE TABLE IF NOT EXISTS " +
				Tables.TABLE_NAME_CLEANING_SETTINGS
				+ "("
				+ CleaningSettingsColumns.COL_NAME_ROBOT_ID			+ " TEXT PRIMARY KEY, "
				+ CleaningSettingsColumns.COL_NAME_SPOT_AREA_LENGTH	+ " INTEGER, "
				+ CleaningSettingsColumns.COL_NAME_SPOT_AREA_HEIGHT	+ " INTEGER, "
				+ CleaningSettingsColumns.COL_NAME_CLEANING_CATEGORY + " INTEGER )");			

			db.execSQL("CREATE TABLE IF NOT EXISTS " +
					Tables.TABLE_NAME_NOTIFICATION_SETTINGS
					+ "("
					+ NotificationSettingsColumns.COL_NAME_EMAIL			+ " TEXT PRIMARY KEY, "
					+ NotificationSettingsColumns.COL_NAME_NOTIFICATION_JSON	+ " TEXT )");
			
			db.execSQL("CREATE TABLE IF NOT EXISTS " +
					Tables.TABLE_NAME_ROBOT_PROFILE_PARAMS
					+ "("
					+ RobotProfileParameters.COL_NAME_ROBOT_PROFILE_DB_ID		+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ RobotProfileParameters.COL_NAME_ROBOT_ID					+ " TEXT KEY, "
					+ RobotProfileParameters.COL_NAME_ROBOT_PARAM_KEY			+ " TEXT KEY, "
					+ RobotProfileParameters.COL_NAME_ROBOT_PARAM_TIMESTAMP	+ " LONG )");
	
			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}

    private void dropTables(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_NAME_USER_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_NAME_ROBOT_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_NAME_ATLAS_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_NAME_GRID_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_NAME_SCHEDULE_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_NAME_CLEANING_SETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_NAME_NOTIFICATION_SETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_NAME_ROBOT_PROFILE_PARAMS);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
