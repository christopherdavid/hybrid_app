package com.neatorobotics.android.slide.framework.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.CryptoUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;

/*
 * Class exposes helper methods that abstracts the caller from 
 * database details
 * 
 */
public class DBHelper {
	private static final String TAG = DBHelper.class.getSimpleName();
	
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "neato_plugin_smart_apps.db";
	
	private static DBHelper singleInstanceObject;
	
	private SQLiteDatabase mNeatoDB;
	private Context mContext;
	
	private static final String CREATE_USER_INFO_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " +
		DBCommon.TABLE_NAME_USER_INFO 
		+ "(" 
		+ DBCommon.COL_NAME_USER_DB_ID			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ DBCommon.COL_NAME_USER_ID				+ " TEXT, "
		+ DBCommon.COL_NAME_USER_NAME			+ " TEXT, "
		+ DBCommon.COL_NAME_USER_EMAIL			+ " TEXT, "
		+ DBCommon.COL_NAME_USER_CHAT_ID		+ " TEXT, "
		+ DBCommon.COL_NAME_USER_CHAT_PWD		+ " TEXT )";
	
	private static final String CREATE_ROBOT_INFO_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " +
		DBCommon.TABLE_NAME_ROBOT_INFO 
		+ "(" 
		+ DBCommon.COL_NAME_ROBOT_DB_ID			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ DBCommon.COL_NAME_ROBOT_ID			+ " TEXT, "
		+ DBCommon.COL_NAME_ROBOT_SERIAL_ID		+ " TEXT, "
		+ DBCommon.COL_NAME_ROBOT_NAME			+ " TEXT, "	
		+ DBCommon.COL_NAME_ROBOT_CHAT_ID		+ " TEXT ) ";
	
	private static final String DROP_USER_INFO_TABLE_QUERY = "DROP TABLE IF EXISTS " + DBCommon.TABLE_NAME_USER_INFO;
	private static final String DROP_ROBOT_INFO_TABLE_QUERY = "DROP TABLE IF EXISTS " + DBCommon.TABLE_NAME_ROBOT_INFO;
	
	private static final String SELECTION_USER_BY_USER_ID = DBCommon.TABLE_NAME_USER_INFO + "." + DBCommon.COL_NAME_USER_ID + " = ?";
	private static final String SELECTION_USER_BY_USER_EMAIL = DBCommon.TABLE_NAME_USER_INFO + "." + DBCommon.COL_NAME_USER_EMAIL + " = ?";
	
	private static final String SELECTION_ROBOT_BY_SERIAL_ID = DBCommon.TABLE_NAME_ROBOT_INFO + "." + DBCommon.COL_NAME_ROBOT_SERIAL_ID + " = ?";	
	
	private class DBOpenHelper extends SQLiteOpenHelper {
		// private boolean mDBCreated;
		
		public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
			LogHelper.log(TAG, "Neato DBOpenHelper - DB Version = " + version);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {			
			createTables(db);			
		}
		
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			LogHelper.log(TAG, "The Neato database code expects a newer version of the database. Old db version - " + oldVersion + ", New db version - " + 
			newVersion + ". The old database will be dropped and a new database will be created");
			
			dropTables(db);
			
			onCreate(db);
		}
		
		private void createTables(SQLiteDatabase db) {
			db.execSQL(CREATE_USER_INFO_TABLE_QUERY);
			db.execSQL(CREATE_ROBOT_INFO_TABLE_QUERY);
		}
		
		private void dropTables(SQLiteDatabase db) {
			db.execSQL(DROP_USER_INFO_TABLE_QUERY);
			db.execSQL(DROP_ROBOT_INFO_TABLE_QUERY);
		}
	}
	
	private DBHelper (Context context) {
		mContext = context;
	}
	
	private synchronized SQLiteDatabase getDatabase() {
		if (mNeatoDB != null) {
			return mNeatoDB;
		}
		
		DBOpenHelper dbOpenHelper = new DBOpenHelper(mContext, DB_NAME, null, DB_VERSION);
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
				
		mNeatoDB = db;
		
		return mNeatoDB;
	}
	
	public static synchronized DBHelper getInstance(Context context) {
		
		if (singleInstanceObject == null) {
			singleInstanceObject = new DBHelper(context);
		}

		return singleInstanceObject;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (mNeatoDB != null) {
			mNeatoDB.close();
			mNeatoDB = null;
		}
	}
	
	// User related functions ---------------------------------------------------------------------------
	
	public boolean saveUser(UserItem userItem) {
		
		if (isUserExist(userItem.getId())) {
			deleteUserById(userItem.getId());
		}
		
		ContentValues values = getContentValues(userItem);
		
		SQLiteDatabase db = getDatabase();
		long rowId = db.insert(DBCommon.TABLE_NAME_USER_INFO, null, values);
		
		boolean saved = (rowId > 0) ? true : false;		
		// Save associated robots info
		if (saved && (userItem.getAssociateRobotCount() > 0)) {			
			for(int index = 0; index < userItem.getAssociateRobotCount(); index++) {
				saveRobot(userItem.getAssociateRobot(index));
			}
		}
		
		return saved;
	}
	
	private boolean isUserExist(String userId) {
		UserItem userItem = getUserById(userId);
		
		if (userItem != null) {
			return true;
		}
		
		return false;
	}
	
	
	public UserItem getUserById(String userId) {
		UserItem userInfo = null;
		String[] selectionArgs = new String[] {userId};
		
		SQLiteDatabase db = getDatabase();		
		Cursor cursor = db.query(DBCommon.TABLE_NAME_USER_INFO, null, SELECTION_USER_BY_USER_ID, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			userInfo = convertToUserItem(cursor);
		}
		
		cursor.close();
		
		return userInfo;
	}
	
	public UserItem getUserByEmail(String email) {
		UserItem userInfo = null;
		String[] selectionArgs = new String[] {email};
		
		SQLiteDatabase db = getDatabase();		
		Cursor cursor = db.query(DBCommon.TABLE_NAME_USER_INFO, null, SELECTION_USER_BY_USER_EMAIL, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			userInfo = convertToUserItem(cursor);
		}
		
		cursor.close();
		
		return userInfo;
	}
	
	public boolean deleteUserById(String userId) {
		String[] selectionArgs = new String[] {userId};
		
		SQLiteDatabase db = getDatabase();		
		int count = db.delete(DBCommon.TABLE_NAME_USER_INFO, SELECTION_USER_BY_USER_ID, selectionArgs);
		
		return (count > 0) ? true : false;
	}
	
	public boolean deleteUserByEmail(String userEmail) {
		String[] selectionArgs = new String[] {userEmail};
		
		SQLiteDatabase db = getDatabase();		
		int count = db.delete(DBCommon.TABLE_NAME_USER_INFO, SELECTION_USER_BY_USER_EMAIL, selectionArgs);
		
		return (count > 0) ? true : false;
	}
	
	private UserItem convertToUserItem(Cursor cursor) {
		UserItem userItem = new UserItem();
		
		userItem.setId(cursor.getString(cursor.getColumnIndex(DBCommon.COL_NAME_USER_ID)));
		userItem.setName(cursor.getString(cursor.getColumnIndex(DBCommon.COL_NAME_USER_NAME)));
		userItem.setEmail(cursor.getString(cursor.getColumnIndex(DBCommon.COL_NAME_USER_EMAIL)));
		userItem.setChatId(cursor.getString(cursor.getColumnIndex(DBCommon.COL_NAME_USER_CHAT_ID)));		
		
		try {			
			String decryptedPwd = CryptoUtils.decrypt(cursor.getString(cursor.getColumnIndex(DBCommon.COL_NAME_USER_CHAT_PWD)));
			userItem.setChatPwd(decryptedPwd);			
		}
		catch (Exception ex) {
			LogHelper.log(TAG, "Exception in password decryption");			
		}
		
		return userItem;
	}
	
	private ContentValues getContentValues(UserItem userItem) {
		ContentValues values  = new ContentValues();
		values.put(DBCommon.COL_NAME_USER_ID, userItem.getId());
		values.put(DBCommon.COL_NAME_USER_NAME, userItem.getName());
		values.put(DBCommon.COL_NAME_USER_EMAIL, userItem.getEmail());
		values.put(DBCommon.COL_NAME_USER_CHAT_ID, userItem.getChatId());
		
		try {			
			String encryptedPwd = CryptoUtils.encrypt(userItem.getChatPwd());			
			values.put(DBCommon.COL_NAME_USER_CHAT_PWD, encryptedPwd);
		}
		catch (Exception ex) {
			LogHelper.log(TAG, "Exception in password encryption");			
		}
		
		return values;
	}
	
	// Robot related functions ---------------------------------------------------------------------------
	
	public boolean saveRobot(RobotItem robotItem) {
		if (isRobotExist(robotItem.getSerialNumber())) {
			deleteRobotBySerialId(robotItem.getSerialNumber());
		}
		
		ContentValues values = getContentValues(robotItem);
		
		SQLiteDatabase db = getDatabase();
		long rowId = db.insert(DBCommon.TABLE_NAME_ROBOT_INFO, null, values);
		
		LogHelper.log(TAG, String.format("saveRobot SerialId [%s] - %d", robotItem.getSerialNumber(), rowId));
		
		boolean saved = (rowId > 0) ? true : false; 
		
		return saved;
	}
	
	public void saveRobot(List<RobotItem> robotList) {
		for (RobotItem robotItem : robotList) {
			saveRobot(robotItem);
		}
	}
	
	private boolean isRobotExist(String serialId) {
		RobotItem robotItem = getRobotBySerialId(serialId);
		
		if (robotItem != null) {
			return true;
		}
		
		return false;
	}
	
	public RobotItem getRobotBySerialId(String serialId) {
		RobotItem robotItem = null;
		String[] selectionArgs = new String[] {serialId};
		
		SQLiteDatabase db = getDatabase();		
		Cursor cursor = db.query(DBCommon.TABLE_NAME_ROBOT_INFO, null, SELECTION_ROBOT_BY_SERIAL_ID, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			robotItem = convertToRobotItem(cursor);
		}
		
		cursor.close();
		
		return robotItem;
	}
	
	public RobotItem getDefaultRobot() {
		RobotItem robotItem = null;		
		
		SQLiteDatabase db = getDatabase();		
		Cursor cursor = db.query(DBCommon.TABLE_NAME_ROBOT_INFO, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			robotItem = convertToRobotItem(cursor);
		}
		
		cursor.close();
		
		return robotItem;
	}
	
	public RobotItem updateRobotNameBySerialId(String serialId, String name) {		
		String[] selectionArgs = new String[] {serialId};
		
		ContentValues values = new ContentValues();
		values.put(DBCommon.COL_NAME_ROBOT_NAME, name);
		
		SQLiteDatabase db = getDatabase();		
		db.update(DBCommon.TABLE_NAME_ROBOT_INFO, values, SELECTION_ROBOT_BY_SERIAL_ID, selectionArgs);
		RobotItem robotItem = getRobotBySerialId(serialId);		
		
		return robotItem;
	}
	
	public List<RobotItem> getAllAssociatedRobots() {
		List<RobotItem> robotList = new ArrayList<RobotItem>();
		
		SQLiteDatabase db = getDatabase();		
		Cursor cursor = db.query(DBCommon.TABLE_NAME_ROBOT_INFO, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				RobotItem robotItem = convertToRobotItem(cursor);
				robotList.add(robotItem);
			}while(cursor.moveToNext());
		}
		
		cursor.close();
		
		return robotList;
	}
	
	public boolean clearAllAssociatedRobots() {
		SQLiteDatabase db = getDatabase();		
		int count = db.delete(DBCommon.TABLE_NAME_ROBOT_INFO, null, null);
		
		LogHelper.log(TAG, String.format("clearAllAssociatedRobots - %d", count));
		
		return (count > 0) ? true : false;
	}
	
	public boolean deleteRobotBySerialId(String serialId) {
		String[] selectionArgs = new String[] {serialId};
		
		SQLiteDatabase db = getDatabase();		
		int count = db.delete(DBCommon.TABLE_NAME_ROBOT_INFO, SELECTION_ROBOT_BY_SERIAL_ID, selectionArgs);
		
		LogHelper.log(TAG, String.format("deleteRobotBySerialId [%s] - %d", serialId, count));
		
		return (count > 0) ? true : false;
	}
	
	private RobotItem convertToRobotItem(Cursor cursor) {
		RobotItem robotItem = new RobotItem();
		
		robotItem.setId(cursor.getString(cursor.getColumnIndex(DBCommon.COL_NAME_ROBOT_ID)));
		robotItem.setSerialNumber(cursor.getString(cursor.getColumnIndex(DBCommon.COL_NAME_ROBOT_SERIAL_ID)));
		robotItem.setName(cursor.getString(cursor.getColumnIndex(DBCommon.COL_NAME_ROBOT_NAME)));
		robotItem.setChatId(cursor.getString(cursor.getColumnIndex(DBCommon.COL_NAME_ROBOT_CHAT_ID)));		
		
		return robotItem;
	}
	
	private ContentValues getContentValues(RobotItem robotItem) {
		ContentValues values  = new ContentValues();
		values.put(DBCommon.COL_NAME_ROBOT_ID, robotItem.getId());
		values.put(DBCommon.COL_NAME_ROBOT_SERIAL_ID, robotItem.getSerialNumber());
		values.put(DBCommon.COL_NAME_ROBOT_NAME, robotItem.getName());
		values.put(DBCommon.COL_NAME_ROBOT_CHAT_ID, robotItem.getChatId());		
		
		return values;
	}
}
