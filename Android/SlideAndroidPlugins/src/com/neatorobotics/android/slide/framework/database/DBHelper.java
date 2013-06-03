package com.neatorobotics.android.slide.framework.database;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.neatorobotics.android.slide.framework.database.NeatoDatabase.CleaningSettingsColumns;
import com.neatorobotics.android.slide.framework.database.NeatoDatabase.NotificationSettingsColumns;
import com.neatorobotics.android.slide.framework.database.NeatoDatabase.RobotInfoColumns;
import com.neatorobotics.android.slide.framework.database.NeatoDatabase.RobotProfileParameters;
import com.neatorobotics.android.slide.framework.database.NeatoDatabase.ScheduleIdsColumns;
import com.neatorobotics.android.slide.framework.database.NeatoDatabase.ScheduleInfoColumns;
import com.neatorobotics.android.slide.framework.database.NeatoDatabase.Tables;
import com.neatorobotics.android.slide.framework.database.NeatoDatabase.UserInfoColumns;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleInfo2;
import com.neatorobotics.android.slide.framework.robot.settings.CleaningSettings;
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
	
	private static DBHelper singleInstanceObject;
	
	private SQLiteDatabase mNeatoDB;
	private Context mContext;
	
	// Select query statements
	private static final String SELECTION_USER_BY_USER_ID = Tables.TABLE_NAME_USER_INFO + "." + UserInfoColumns.COL_NAME_USER_ID + " = ?";
	private static final String SELECTION_USER_BY_USER_EMAIL = Tables.TABLE_NAME_USER_INFO + "." + UserInfoColumns.COL_NAME_USER_EMAIL + " = ?";
	
	private static final String SELECTION_ROBOT_BY_SERIAL_ID = Tables.TABLE_NAME_ROBOT_INFO + "." + RobotInfoColumns.COL_NAME_ROBOT_SERIAL_ID + " = ?";

	private static final String SELECTION_SCHEDULE_INFO_BY_ID = Tables.TABLE_NAME_SCHEDULE_INFO + "." + ScheduleInfoColumns.COL_NAME_SCHEDULE_ID + " = ?";
	private static final String SELECTION_ROBOT_SCHEDULE_BY_ID = Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS + "." + ScheduleIdsColumns.COL_NAME_ROBOT_ID + " = ?";
	private static final String SELECTION_ROBOT_BY_BASIC_SCHEDULE_ID = Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS + "." + ScheduleIdsColumns.COL_NAME_BASIC_SCHEDULE_ID + " = ?";
	private static final String SELECTION_ROBOT_BY_ADVANCED_SCHEDULE_ID = Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS + "." + ScheduleIdsColumns.COL_NAME_ADVANCED_SCHEDULE_ID + " = ?";
	private static final String SELECTION_CLEANING_SETTINGS_BY_ROBOTID = Tables.TABLE_NAME_CLEANING_SETTINGS + "." + CleaningSettingsColumns.COL_NAME_ROBOT_ID + " = ?";
	private static final String SELECTION_NOTIFICATION_SETTINGS_BY_EMAIL = Tables.TABLE_NAME_NOTIFICATION_SETTINGS + "." + NotificationSettingsColumns.COL_NAME_EMAIL + " = ?";
	
	private static final String SELECTION_ROBOT_PROFILE_PARAM_BY_ROBOTID_AND_KEY = Tables.TABLE_NAME_ROBOT_PROFILE_PARAMS + "." + RobotProfileParameters.COL_NAME_ROBOT_ID + " = ? AND " + Tables.TABLE_NAME_ROBOT_PROFILE_PARAMS + "." + RobotProfileParameters.COL_NAME_ROBOT_PARAM_KEY + " = ?";
	private DBHelper (Context context) {
		mContext = context;
	}
	
	private synchronized SQLiteDatabase getDatabase() {
		if (mNeatoDB != null) {
			return mNeatoDB;
		}
		
		NeatoDatabase dbOpenHelper = new NeatoDatabase(mContext);
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

	public void clearAllData() {
		clearAllAssociatedRobots();
		clearAllScheduleInfo();
		clearAllScheduleIds();
		clearAllCleaningSettings();
		clearAllNotificationSettings();
		clearAllRobotProfileParams();
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
		
		if (isUserExist(userItem.id)) {
			deleteUserById(userItem.id);
		}
		
		ContentValues values = getContentValues(userItem);
		
		SQLiteDatabase db = getDatabase();
		long rowId = db.insert(Tables.TABLE_NAME_USER_INFO, null, values);
		
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
		Cursor cursor = db.query(Tables.TABLE_NAME_USER_INFO, null, SELECTION_USER_BY_USER_ID, selectionArgs, null, null, null);
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
		Cursor cursor = db.query(Tables.TABLE_NAME_USER_INFO, null, SELECTION_USER_BY_USER_EMAIL, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			userInfo = convertToUserItem(cursor);
		}
		
		cursor.close();
		
		return userInfo;
	}
	
	public boolean deleteUserById(String userId) {
		String[] selectionArgs = new String[] {userId};
		
		SQLiteDatabase db = getDatabase();		
		int count = db.delete(Tables.TABLE_NAME_USER_INFO, SELECTION_USER_BY_USER_ID, selectionArgs);
		
		return (count > 0) ? true : false;
	}
	
	public boolean deleteUserByEmail(String userEmail) {
		String[] selectionArgs = new String[] {userEmail};
		
		SQLiteDatabase db = getDatabase();		
		int count = db.delete(Tables.TABLE_NAME_USER_INFO, SELECTION_USER_BY_USER_EMAIL, selectionArgs);
		
		return (count > 0) ? true : false;
	}
	
	private UserItem convertToUserItem(Cursor cursor) {
		UserItem userItem = new UserItem();
		
		userItem.id = cursor.getString(cursor.getColumnIndex(UserInfoColumns.COL_NAME_USER_ID));
		userItem.name = cursor.getString(cursor.getColumnIndex(UserInfoColumns.COL_NAME_USER_NAME));
		userItem.email = cursor.getString(cursor.getColumnIndex(UserInfoColumns.COL_NAME_USER_EMAIL));
		userItem.chat_id = cursor.getString(cursor.getColumnIndex(UserInfoColumns.COL_NAME_USER_CHAT_ID));		
		
		try {			
			String decryptedPwd = CryptoUtils.decrypt(cursor.getString(cursor.getColumnIndex(UserInfoColumns.COL_NAME_USER_CHAT_PWD)));
			userItem.chat_pwd = decryptedPwd;			
		}
		catch (Exception ex) {
			LogHelper.log(TAG, "Exception in password decryption");			
		}
		
		return userItem;
	}
	
	private ContentValues getContentValues(UserItem userItem) {
		ContentValues values  = new ContentValues();
		values.put(UserInfoColumns.COL_NAME_USER_ID, userItem.id);
		values.put(UserInfoColumns.COL_NAME_USER_NAME, userItem.name);
		values.put(UserInfoColumns.COL_NAME_USER_EMAIL, userItem.email);
		values.put(UserInfoColumns.COL_NAME_USER_CHAT_ID, userItem.chat_id);
		
		try {			
			String encryptedPwd = CryptoUtils.encrypt(userItem.chat_pwd);			
			values.put(UserInfoColumns.COL_NAME_USER_CHAT_PWD, encryptedPwd);
		}
		catch (Exception ex) {
			LogHelper.log(TAG, "Exception in password encryption");			
		}
		
		return values;
	}
	
	// Robot related functions ---------------------------------------------------------------------------
	
	public boolean saveRobot(RobotItem robotItem) {
		if (isRobotExist(robotItem.serial_number)) {
			deleteRobotBySerialId(robotItem.serial_number);
		}
		
		ContentValues values = getContentValues(robotItem);
		
		SQLiteDatabase db = getDatabase();
		long rowId = db.insert(Tables.TABLE_NAME_ROBOT_INFO, null, values);
		
		LogHelper.log(TAG, String.format("saveRobot SerialId [%s] - %d", robotItem.serial_number, rowId));
		
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
		Cursor cursor = db.query(Tables.TABLE_NAME_ROBOT_INFO, null, SELECTION_ROBOT_BY_SERIAL_ID, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			robotItem = convertToRobotItem(cursor);
		}
		
		cursor.close();
		
		return robotItem;
	}
	
	public RobotItem getDefaultRobot() {
		RobotItem robotItem = null;		
		
		SQLiteDatabase db = getDatabase();		
		Cursor cursor = db.query(Tables.TABLE_NAME_ROBOT_INFO, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			robotItem = convertToRobotItem(cursor);
		}
		
		cursor.close();
		
		return robotItem;
	}
	
	public List<RobotItem> getAllAssociatedRobots() {
		List<RobotItem> robotList = new ArrayList<RobotItem>();
		
		SQLiteDatabase db = getDatabase();		
		Cursor cursor = db.query(Tables.TABLE_NAME_ROBOT_INFO, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				RobotItem robotItem = convertToRobotItem(cursor);
				robotList.add(robotItem);
			}while(cursor.moveToNext());
		}
		
		cursor.close();
		
		return robotList;
	}
	
	public RobotItem updateRobotNameBySerialId(String serialId, String name) {		
		String[] selectionArgs = new String[] {serialId};
		
		ContentValues values = new ContentValues();
		values.put(RobotInfoColumns.COL_NAME_ROBOT_NAME, name);
		
		SQLiteDatabase db = getDatabase();		
		db.update(Tables.TABLE_NAME_ROBOT_INFO, values, SELECTION_ROBOT_BY_SERIAL_ID, selectionArgs);
		RobotItem robotItem = getRobotBySerialId(serialId);		
		
		return robotItem;
	}
	
	public boolean clearAllAssociatedRobots() {
		SQLiteDatabase db = getDatabase();		
		int count = db.delete(Tables.TABLE_NAME_ROBOT_INFO, null, null);
		
		LogHelper.log(TAG, String.format("clearAllAssociatedRobots - %d", count));
		
		return (count > 0) ? true : false;
	}
	
	public boolean deleteRobotBySerialId(String serialId) {
		String[] selectionArgs = new String[] {serialId};
		
		SQLiteDatabase db = getDatabase();		
		int count = db.delete(Tables.TABLE_NAME_ROBOT_INFO, SELECTION_ROBOT_BY_SERIAL_ID, selectionArgs);
		
		LogHelper.log(TAG, String.format("deleteRobotBySerialId [%s] - %d", serialId, count));
		
		return (count > 0) ? true : false;
	}
	
	private RobotItem convertToRobotItem(Cursor cursor) {
		RobotItem robotItem = new RobotItem();
		
		robotItem.id = cursor.getString(cursor.getColumnIndex(RobotInfoColumns.COL_NAME_ROBOT_ID));
		robotItem.serial_number = cursor.getString(cursor.getColumnIndex(RobotInfoColumns.COL_NAME_ROBOT_SERIAL_ID));
		robotItem.name = cursor.getString(cursor.getColumnIndex(RobotInfoColumns.COL_NAME_ROBOT_NAME));
		robotItem.chat_id = cursor.getString(cursor.getColumnIndex(RobotInfoColumns.COL_NAME_ROBOT_CHAT_ID));		
		
		return robotItem;
	}
	
	private ContentValues getContentValues(RobotItem robotItem) {
		ContentValues values  = new ContentValues();
		values.put(RobotInfoColumns.COL_NAME_ROBOT_ID, robotItem.id);
		values.put(RobotInfoColumns.COL_NAME_ROBOT_SERIAL_ID, robotItem.serial_number);
		values.put(RobotInfoColumns.COL_NAME_ROBOT_NAME, robotItem.name);
		values.put(RobotInfoColumns.COL_NAME_ROBOT_CHAT_ID, robotItem.chat_id);		
		
		return values;
	}
	
	// Public helper method to return the ScheduleInfo based on the id
	// from the database
	public ScheduleInfo2 getScheduleInfoById(String id) {
		ScheduleInfo2 scheduleInfo = null;
		String[] selectionArgs = new String[] {id};
		SQLiteDatabase db = getDatabase();
		Cursor cursor = db.query(Tables.TABLE_NAME_SCHEDULE_INFO, null, SELECTION_SCHEDULE_INFO_BY_ID, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			scheduleInfo = convertToScheduleInfo(cursor);
		}
		cursor.close();
		return scheduleInfo;
	}

	private ScheduleInfo2 convertToScheduleInfo(Cursor cursor) {
		ScheduleInfo2 scheduleInfo = new ScheduleInfo2();
		scheduleInfo.setScheduleId(cursor.getString(cursor.getColumnIndex(ScheduleInfoColumns.COL_NAME_SCHEDULE_ID)));
		scheduleInfo.setServerId(cursor.getString(cursor.getColumnIndex(ScheduleInfoColumns.COL_NAME_SCHEDULE_SERVER_ID)));
		scheduleInfo.setDataVersion(cursor.getString(cursor.getColumnIndex(ScheduleInfoColumns.COL_NAME_SCHEDULE_VERSION)));		
		scheduleInfo.setScheduleType(cursor.getString(cursor.getColumnIndex(ScheduleInfoColumns.COL_NAME_SCHEDULE_TYPE)));
		scheduleInfo.setScheduleData(cursor.getString(cursor.getColumnIndex(ScheduleInfoColumns.COL_NAME_SCHEDULE_DATA)));
		return scheduleInfo;
	}

	// Public helper method to save the schedule information into the database
	public ScheduleInfo2 saveScheduleInfo(String id, String serverId, String scheduleVersion, String scheduleType, String data) {	
		LogHelper.logD(TAG, String.format("saveScheduleInfo - Input ScheduleId = %s ScheduleVersion = %s", serverId, scheduleVersion));
		ScheduleInfo2 scheduleInfo = getScheduleInfoById(id);
		if (scheduleInfo != null) {			
			LogHelper.logD(TAG, String.format("saveScheduleInfo - Database ScheduleId = %s ScheduleVersion = %s ", 
					scheduleInfo.getServerId(), scheduleInfo.getDataVersion()));		
			scheduleInfo.setScheduleId(id);
			scheduleInfo.setServerId(serverId);
			scheduleInfo.setScheduleType(scheduleType);
			scheduleInfo.setDataVersion(scheduleVersion);
			scheduleInfo.setScheduleData(data);
			updateScheduleInfo(scheduleInfo);
		}
		else {
			LogHelper.logD(TAG, String.format("Insert schedule info -  ScheduleId = %s ScheduleVersion = %s ", 
					serverId, scheduleVersion));
			ContentValues values = getScheduleInfoContentValues(id, serverId, scheduleVersion, scheduleType, data);
			SQLiteDatabase db = getDatabase();
			long rowId = db.insert(Tables.TABLE_NAME_SCHEDULE_INFO, null, values);
			if (rowId > 0) {
				scheduleInfo = new ScheduleInfo2();
				scheduleInfo.setScheduleId(id);
				scheduleInfo.setServerId(serverId);
				scheduleInfo.setScheduleType(scheduleType);  
				scheduleInfo.setDataVersion(scheduleVersion);
			}
		}
		return scheduleInfo;
	}
		
	public boolean updateScheduleInfo(ScheduleInfo2 scheduleInfo) {
		ContentValues values = getScheduleInfoContentValues(scheduleInfo);		
		String[] selectionArgs = new String[] {scheduleInfo.getScheduleId()};
		SQLiteDatabase db = getDatabase();
		int count = db.update(Tables.TABLE_NAME_SCHEDULE_INFO, values, SELECTION_SCHEDULE_INFO_BY_ID, selectionArgs);
		return (count > 0) ? true : false;
	}

	private ContentValues getScheduleInfoContentValues(String id, String serverId, String scheduleVersion, String scheduleType, String scheduleData) 
	{
		ContentValues values  = new ContentValues();
		values.put(ScheduleInfoColumns.COL_NAME_SCHEDULE_ID, id);
		values.put(ScheduleInfoColumns.COL_NAME_SCHEDULE_SERVER_ID, serverId);
		values.put(ScheduleInfoColumns.COL_NAME_SCHEDULE_VERSION, scheduleVersion);
		values.put(ScheduleInfoColumns.COL_NAME_SCHEDULE_TYPE, scheduleType);
		values.put(ScheduleInfoColumns.COL_NAME_SCHEDULE_DATA, scheduleData);
		return values;
	}

	private ContentValues getScheduleInfoContentValues(ScheduleInfo2 scheduleInfo) {
		ContentValues values  = new ContentValues();
		values.put(ScheduleInfoColumns.COL_NAME_SCHEDULE_ID, scheduleInfo.getScheduleId());
		values.put(ScheduleInfoColumns.COL_NAME_SCHEDULE_SERVER_ID, scheduleInfo.getServerId());
		values.put(ScheduleInfoColumns.COL_NAME_SCHEDULE_VERSION, scheduleInfo.getDataVersion());
		values.put(ScheduleInfoColumns.COL_NAME_SCHEDULE_TYPE, scheduleInfo.getScheduleType());
		values.put(ScheduleInfoColumns.COL_NAME_SCHEDULE_DATA, scheduleInfo.getScheduleData());
		return values;
	}

	public void clearAllScheduleInfo() {
		SQLiteDatabase db = getDatabase();
		int count = db.delete(Tables.TABLE_NAME_SCHEDULE_INFO, null, null);
		LogHelper.logD(TAG, "clearAllScheduleInfo - " + count);
	}
	
	public boolean isScheduleInfoExistsForRobot(String robotId) {
		String[] selectionArgs = new String[] {robotId};
		SQLiteDatabase db = getDatabase();
		Cursor cursor = db.query(Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS, null, SELECTION_ROBOT_SCHEDULE_BY_ID, selectionArgs, null, null, null);
		return cursor.moveToFirst();
	} 

	// Robot schedule IDs helper function
	public String getAdvancedScheduleIdForRobot(String robotId) {
		String scheduleId = null;
		String[] selectionArgs = new String[] {robotId};

		SQLiteDatabase db = getDatabase();
		Cursor cursor = db.query(Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS, null, SELECTION_ROBOT_SCHEDULE_BY_ID, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			scheduleId = getAdvancedScheduleId(cursor);
		}
		cursor.close();
		return scheduleId;
	}


	public String getBasicScheduleIdForRobot(String robotId) {
		String scheduleId = null;
		String[] selectionArgs = new String[] {robotId};

		SQLiteDatabase db = getDatabase();
		Cursor cursor = db.query(Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS, null, SELECTION_ROBOT_SCHEDULE_BY_ID, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			scheduleId = getBasicScheduleId(cursor);
		}
		cursor.close();
		return scheduleId;
	}

	private String getAdvancedScheduleId(Cursor cursor) {
		return (cursor.getString(cursor.getColumnIndex(ScheduleIdsColumns.COL_NAME_ADVANCED_SCHEDULE_ID)));
	}

	private String getBasicScheduleId(Cursor cursor) {
		return (cursor.getString(cursor.getColumnIndex(ScheduleIdsColumns.COL_NAME_BASIC_SCHEDULE_ID)));
	}

	private String getRobotId(Cursor cursor) {
		return (cursor.getString(cursor.getColumnIndex(ScheduleIdsColumns.COL_NAME_ROBOT_ID)));
	}

	public boolean updateBasicScheduleId(String robotId, String basicScheduleId) {
		ContentValues values = getBasicContentValues(robotId, basicScheduleId);		
		String[] selectionArgs = new String[] {robotId};
		SQLiteDatabase db = getDatabase();
		int count = db.update(Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS, values, SELECTION_ROBOT_SCHEDULE_BY_ID, selectionArgs);
		return (count > 0) ? true : false;
	}

	public boolean updateAdvancedScheduleId(String robotId, String advacnedScheduleId) {
		ContentValues values = getAdvancedContentValues(robotId, advacnedScheduleId);		
		String[] selectionArgs = new String[] {robotId};
		SQLiteDatabase db = getDatabase();
		int count = db.update(Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS, values, SELECTION_ROBOT_SCHEDULE_BY_ID, selectionArgs);
		return (count > 0) ? true : false;
	}

	public void saveAdvancedScheduleId(String robotId, String id) {	
		if (isScheduleInfoExistsForRobot(robotId)) {			
			updateAdvancedScheduleId(robotId, id);
		}
		else {
			ContentValues values = getAdvancedContentValues(robotId, id);
			SQLiteDatabase db = getDatabase();
			db.insert(Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS, null, values);
		}
	}

	public void saveBasicScheduleId(String robotId, String id) {	
		if (isScheduleInfoExistsForRobot(robotId)) {			
			updateBasicScheduleId(robotId, id);
		}
		else {
			ContentValues values = getBasicContentValues(robotId, id);
			SQLiteDatabase db = getDatabase();
			db.insert(Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS, null, values);
			LogHelper.logD(TAG, "Added Basic Schedule Id for the robot successfully");
		}
	}

	public String getRobotIdForBasicSchedule(String id) {
		String[] selectionArgs = new String[] {id};
		String robotId = null;
		SQLiteDatabase db = getDatabase();
		Cursor cursor = db.query(Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS, null, SELECTION_ROBOT_BY_BASIC_SCHEDULE_ID, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			robotId = getRobotId(cursor);
		}
		cursor.close();
		return robotId;
	}

	public String getRobotIdForAdvancedSchedule(String id) {
		String[] selectionArgs = new String[] {id};
		String robotId = null;
		SQLiteDatabase db = getDatabase();
		Cursor cursor = db.query(Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS, null, SELECTION_ROBOT_BY_ADVANCED_SCHEDULE_ID, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			robotId = getRobotId(cursor);
		}
		cursor.close();
		return robotId;

	}
	private ContentValues getBasicContentValues(String robotId,  String id) {
		ContentValues values  = new ContentValues();
		values.put(ScheduleIdsColumns.COL_NAME_ROBOT_ID, robotId);
		values.put(ScheduleIdsColumns.COL_NAME_BASIC_SCHEDULE_ID, id);
		return values;
	}

	private ContentValues getAdvancedContentValues(String robotId, String id) {
		ContentValues values  = new ContentValues();
		values.put(ScheduleIdsColumns.COL_NAME_ROBOT_ID, robotId);
		values.put(ScheduleIdsColumns.COL_NAME_ADVANCED_SCHEDULE_ID, id);
		return values;
	}
	
	public void clearAllScheduleIds() {
		SQLiteDatabase db = getDatabase();
		int count = db.delete(Tables.TABLE_NAME_ROBOT_SCHEDULE_IDS, null, null);
		LogHelper.logD(TAG, "clearAllScheduleIds - " + count);
	}
	
	private CleaningSettings convertToCleaningSettingsObject(Cursor cursor) {
		CleaningSettings cleaningSettings = new CleaningSettings();

		cleaningSettings.setSpotAreaLength(cursor.getInt(cursor.getColumnIndex(CleaningSettingsColumns.COL_NAME_SPOT_AREA_LENGTH)));
		cleaningSettings.setSpotAreaHeight(cursor.getInt(cursor.getColumnIndex(CleaningSettingsColumns.COL_NAME_SPOT_AREA_HEIGHT)));

		return cleaningSettings;
	}

	private ContentValues getContentValues(CleaningSettings cleaningSettings) {
		ContentValues values  = new ContentValues();

		values.put(CleaningSettingsColumns.COL_NAME_SPOT_AREA_LENGTH, cleaningSettings.getSpotAreaLength());
		values.put(CleaningSettingsColumns.COL_NAME_SPOT_AREA_HEIGHT, cleaningSettings.getSpotAreaHeight());

		return values;
	}

	// Public helper method to return robot cleaning settings
	public CleaningSettings getCleaningSettings(String robotId) {
		CleaningSettings robotSettings = null;

		String[] selectionArgs = new String[] {robotId};

		SQLiteDatabase db = getDatabase();
		Cursor cursor = db.query(Tables.TABLE_NAME_CLEANING_SETTINGS, null, SELECTION_CLEANING_SETTINGS_BY_ROBOTID,
				selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			robotSettings = convertToCleaningSettingsObject(cursor);
		}
		cursor.close();

		return robotSettings;
	}

	// Public helper method to update cleaning settings
	public boolean updateCleaningSettings(String robotId, CleaningSettings cleaningSettings) {
		int count = 0;

		String[] selectionArgs = new String[] {robotId};

		ContentValues values = getContentValues(cleaningSettings);

		SQLiteDatabase db = getDatabase();

		Cursor cursor = db.query(Tables.TABLE_NAME_CLEANING_SETTINGS, null, SELECTION_CLEANING_SETTINGS_BY_ROBOTID, 
				selectionArgs, null, null, null);
		if (!cursor.moveToFirst()) {
			// Entry does not exist add one
			values.put(CleaningSettingsColumns.COL_NAME_ROBOT_ID, robotId);
			count = (int) db.insert(Tables.TABLE_NAME_CLEANING_SETTINGS, null, values);
		}
		else {
			count = db.update(Tables.TABLE_NAME_CLEANING_SETTINGS, values, SELECTION_CLEANING_SETTINGS_BY_ROBOTID, selectionArgs);
		}

		return (count > 0) ? true : false;
	}
	
	public void clearAllCleaningSettings() {
		SQLiteDatabase db = getDatabase();
		int count = db.delete(Tables.TABLE_NAME_CLEANING_SETTINGS, null, null);
		LogHelper.logD(TAG, "clearAllCleaningSettings - " + count);
	}	
	
	// Public helper method to update notification settings JSON
	public boolean saveNotificationSettingsJson(String email, String notificationSettingsJson) {
		long count = 0;

		ContentValues values = getContentValues(email, notificationSettingsJson);
		
		String[] selectionArgs = new String[] {email};
		SQLiteDatabase db = getDatabase();

		Cursor cursor = db.query(Tables.TABLE_NAME_NOTIFICATION_SETTINGS, null, 
				SELECTION_NOTIFICATION_SETTINGS_BY_EMAIL, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {			
			// Entry exist update it
			count = db.update(Tables.TABLE_NAME_NOTIFICATION_SETTINGS, values, 
					SELECTION_NOTIFICATION_SETTINGS_BY_EMAIL, selectionArgs);
		}
		else {
			// Entry does not exist add one
			values.put(NotificationSettingsColumns.COL_NAME_EMAIL, email);
			count = db.insert(Tables.TABLE_NAME_NOTIFICATION_SETTINGS, null, values);
		}
		
		cursor.close();
		
		return (count > 0) ? true : false;
	}	
	
	// Public helper method to return robot notification settings JSON string
	public String getNotificationSettingsJson(String email) {
		String notificationsJson = "";
		
		SQLiteDatabase db = getDatabase();
		String[] selectionArgs = new String[] {email};
		Cursor cursor = db.query(Tables.TABLE_NAME_NOTIFICATION_SETTINGS, null, 
				SELECTION_NOTIFICATION_SETTINGS_BY_EMAIL, selectionArgs, null, null, null);
		
		if (cursor.moveToFirst()) {
			notificationsJson = cursor.getString(cursor.getColumnIndex(NotificationSettingsColumns.COL_NAME_NOTIFICATION_JSON));
		}
		cursor.close();
		
		return notificationsJson;
	}
	
	public void clearAllNotificationSettings() {
		SQLiteDatabase db = getDatabase();
		int count = db.delete(Tables.TABLE_NAME_NOTIFICATION_SETTINGS, null, null);
		LogHelper.logD(TAG, "clearAllNotificationSettings - " + count);
	}	
	
	private ContentValues getContentValues(String email, String settingsJson) {
		ContentValues values  = new ContentValues();

		LogHelper.logD(TAG, "Saving notificationSettings JSON- " + settingsJson);		
		values.put(NotificationSettingsColumns.COL_NAME_NOTIFICATION_JSON, settingsJson);

		return values;
	}
	
	
	// Public method to handle profile params : Table :TABLE_NAME_ROBOT_PROFILE_PARAMS
	private void clearAllRobotProfileParams() {
		SQLiteDatabase db = getDatabase();
		int count = db.delete(Tables.TABLE_NAME_ROBOT_PROFILE_PARAMS, null, null);
		LogHelper.logD(TAG, "clearAllProfileParams - " + count);
	}
	
	private ContentValues getProfileParamsContentValues(String robotId, String key, long timestamp) {
		ContentValues values  = new ContentValues();
		values.put(RobotProfileParameters.COL_NAME_ROBOT_ID, robotId);
		values.put(RobotProfileParameters.COL_NAME_ROBOT_PARAM_KEY, key);
		values.put(RobotProfileParameters.COL_NAME_ROBOT_PARAM_TIMESTAMP, timestamp);
		return values;
	}
	
	private boolean addProfileParam(String robotId, String key, long timestamp) {
		LogHelper.logD(TAG, "AddprofileParam for the robot");
		ContentValues values = getProfileParamsContentValues(robotId, key, timestamp);
		SQLiteDatabase db = getDatabase();
		long rowId = db.insert(Tables.TABLE_NAME_ROBOT_PROFILE_PARAMS, null, values);
		return (rowId > 0) ? true : false;
	}
	
	private boolean updateProfileParam(String robotId, String key, long timestamp) {
		LogHelper.logD(TAG, "updateProfileParam for the robot");
		ContentValues values = getProfileParamsContentValues(robotId, key, timestamp);
		String[] selectionArgs = new String[] {robotId, key};
		SQLiteDatabase db = getDatabase();		
		int count = db.update(Tables.TABLE_NAME_ROBOT_PROFILE_PARAMS, values, SELECTION_ROBOT_PROFILE_PARAM_BY_ROBOTID_AND_KEY, selectionArgs);
		
		return (count > 0) ? true : false;
	}
	
	private boolean deleteProfileParam(String robotId, String key) {
		LogHelper.logD(TAG, "deleteProfileParam for the robot "+ robotId + " Key: " +key);
		String[] selectionArgs = new String[] {robotId, key};
		SQLiteDatabase db = getDatabase();		
		int count = db.delete(Tables.TABLE_NAME_ROBOT_PROFILE_PARAMS, SELECTION_ROBOT_PROFILE_PARAM_BY_ROBOTID_AND_KEY, selectionArgs);
		return (count > 0) ? true : false;
	}
	
	public long getProfileParamTimestamp(String robotId, String id) {
		String[] selectionArgs = new String[] {robotId, id};
		SQLiteDatabase db = getDatabase();
		Cursor cursor = db.query(Tables.TABLE_NAME_ROBOT_PROFILE_PARAMS, null, SELECTION_ROBOT_PROFILE_PARAM_BY_ROBOTID_AND_KEY, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			return cursor.getLong(cursor.getColumnIndex(RobotProfileParameters.COL_NAME_ROBOT_PARAM_TIMESTAMP));
		}
		return -1;
	}
	
	public boolean saveProfileParam(String robotId, String key, long timestamp) {
		if (getProfileParamTimestamp(robotId, key) != -1) {
			return updateProfileParam(robotId, key, timestamp);
		}
		else {
			return addProfileParam(robotId, key, timestamp);
		}
	}
	
	public boolean deleteProfileParamIfExists(String robotId, String key) {
			return deleteProfileParam(robotId, key);
	}
}
