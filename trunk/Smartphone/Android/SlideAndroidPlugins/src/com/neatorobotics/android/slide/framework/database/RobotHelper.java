package com.neatorobotics.android.slide.framework.database;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.settings.CleaningSettings;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails2.ProfileAttributeKeys;

public class RobotHelper {	
	private static final String TAG = RobotHelper.class.getSimpleName();
	
	public static boolean saveRobotDetails(Context context, RobotItem robotItem) {
		boolean result = false;
		if (robotItem != null) {
			DBHelper dbHelper = DBHelper.getInstance(context);
			
			// NOTE: Presently we are supporting only one robot-user association
			// to do this clear the previous robot-user association and add the new one
			// In future we will add multiple robot-user association then remove 
			// this clear function call 
			// dbHelper.clearAllAssociatedRobots();
			result = dbHelper.saveRobot(robotItem);			
		}		
		
		return result;
	}
	
	public static RobotItem getManagedRobot(Context context) {
		RobotItem robotItem = null;
		String serialId = NeatoPrefs.getManagedRobotSerialId(context);
		if (!TextUtils.isEmpty(serialId)) {
			robotItem = DBHelper.getInstance(context).getRobotBySerialId(serialId);
		}
		return robotItem;
	}
	
	public static RobotItem getRobotItem(Context context, String robotId) {
		RobotItem robotItem = null;
		if (!TextUtils.isEmpty(robotId)) {
			robotItem = DBHelper.getInstance(context).getRobotBySerialId(robotId);
		}
		return robotItem;
	}
	
	public static void setRobotToManage(Context context, RobotItem robotItem) {
		if (robotItem != null) {
			NeatoPrefs.saveManagedRobotSerialId(context, robotItem.serial_number);
		}
	}
	
	public static boolean clearRobotDetails(Context context, String serialId) {
		boolean result = false;
		
		if (TextUtils.isEmpty(serialId)) {
			LogHelper.log(TAG, "clearRobotDetails - Invalid robot serial id = " + serialId);
			return result;
		}		
		
		result = DBHelper.getInstance(context).deleteRobotBySerialId(serialId);
		
		// If it is managed robot serialId then clear it 
		String managedSerialId = NeatoPrefs.getManagedRobotSerialId(context);
		if (managedSerialId.equals(serialId)) {
			NeatoPrefs.clearManagedRobotSerialId(context);
		}
		
		return result;
	}
	
	public static void clearAllUserAssociatedRobots(Context context) {
		DBHelper.getInstance(context).clearAllAssociatedRobots();
		NeatoPrefs.clearManagedRobotSerialId(context);
	}	
	
	public static void saveRobotDetails(Context context, List<RobotItem> robotList) {
		if (robotList != null) {
			DBHelper.getInstance(context).saveRobot(robotList);
		}
	}
	
	public static List<RobotItem> getAllAssociatedRobots(Context context) {
		return DBHelper.getInstance(context).getAllAssociatedRobots();
	}
	
	public static RobotItem updateRobotName(Context context, String robotSerialNo, String name) {
		RobotItem robotItem = null;
		if (!TextUtils.isEmpty(robotSerialNo) && (!TextUtils.isEmpty(name))) {
			robotItem = DBHelper.getInstance(context).updateRobotNameBySerialId(robotSerialNo, name);
		}
		return robotItem;
	}

	public static boolean updateCleaningSettings(Context context, String robotId, 
			CleaningSettings cleaningSettings) {
		boolean updated = false;
		if (!TextUtils.isEmpty(robotId) && (cleaningSettings != null)) {
			updated = DBHelper.getInstance(context).updateCleaningSettings(robotId, cleaningSettings);
		}
		return updated;
	}

	private static CleaningSettings getDefaultCleaningSettings() {
		CleaningSettings cleaningSettings = new CleaningSettings();
		cleaningSettings.setSpotAreaLength(RobotCommandPacketConstants.DEFAULT_SPOT_CLEANING_LENGTH);
		cleaningSettings.setSpotAreaHeight(RobotCommandPacketConstants.DEFAULT_SPOT_CLEANING_HEIGHT);
		return cleaningSettings;
	}
	public static CleaningSettings getCleaningSettings(Context context, String robotId) {
		CleaningSettings cleaningSettings = null;
		if (!TextUtils.isEmpty(robotId)) {
			cleaningSettings = DBHelper.getInstance(context).getCleaningSettings(robotId);
			if (cleaningSettings == null) {
				cleaningSettings = getDefaultCleaningSettings();
			}
		}
		return cleaningSettings;
	}	
	
	public static boolean saveNotificationSettingsJson(Context context, String email, String notificationsJson) {
		boolean updated = false;
		if (!TextUtils.isEmpty(email) && (!TextUtils.isEmpty(notificationsJson))) {
			updated = DBHelper.getInstance(context).saveNotificationSettingsJson(email, notificationsJson);
		}
		return updated;
	}
	
	public static JSONObject getNotificationSettingsJson(Context context, String email) {
		JSONObject settingsJsonObj = null;
		try {
			if (!TextUtils.isEmpty(email)) {
				String settingsJson = DBHelper.getInstance(context).getNotificationSettingsJson(email);
				if (!TextUtils.isEmpty(settingsJson)) {
					settingsJsonObj = new JSONObject(settingsJson);
				}
				else {
					// Return default settings Object
					settingsJsonObj = getDefaultSettings();
				}
			}		
		}
		catch (JSONException ex) {		
			LogHelper.logD(TAG, "JSONException in getNotificationSettingsJson", ex);
		}
		
		return settingsJsonObj;
	}
	
	// Private helper method to return the default push notification options
	// by default all push notifications are disabled
	public static JSONObject getDefaultSettings() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(JsonMapKeys.KEY_GLOBAL_NOTIFICATIONS, false);
			
			JSONArray notificationsArray = new JSONArray();
						
			JSONObject dirtBinFullObj = new JSONObject();
			dirtBinFullObj.put(JsonMapKeys.KEY_NOTIFICATION_KEY, RobotCommandPacketConstants.NOTIFICATION_ID_DIRT_BIN_FULL);
			dirtBinFullObj.put(JsonMapKeys.KEY_NOTIFICATION_VALUE, false);
			notificationsArray.put(dirtBinFullObj);
			
			JSONObject robotStuckObj = new JSONObject();
			robotStuckObj.put(JsonMapKeys.KEY_NOTIFICATION_KEY, RobotCommandPacketConstants.NOTIFICATION_ID_ROBOT_STUCK);
			robotStuckObj.put(JsonMapKeys.KEY_NOTIFICATION_VALUE, false);
			notificationsArray.put(robotStuckObj);
			
			JSONObject cleaningDoneObj = new JSONObject();
			cleaningDoneObj.put(JsonMapKeys.KEY_NOTIFICATION_KEY, RobotCommandPacketConstants.NOTIFICATION_ID_CLEANING_DONE);
			cleaningDoneObj.put(JsonMapKeys.KEY_NOTIFICATION_VALUE, false);
			notificationsArray.put(cleaningDoneObj);
			
			
			jsonObject.put(JsonMapKeys.KEY_NOTIFICATIONS, notificationsArray);
		}
		catch(JSONException ex) {	
			LogHelper.logD(TAG, "JSONException in getNotificationsJson", ex);
		}
		
		return jsonObject;
	}
	
	// Robot profile
	public static boolean isProfileKeyExists(Context context, String robotId, String key) {
		if (DBHelper.getInstance(context).getProfileParamTimestamp(robotId, key) != -1) {
			return true;
		}
		return false;
	}
	
	public static boolean isProfileDataChanged(Context context, String robotId, String key, long timeStamp) {
		long currentTimestamp = DBHelper.getInstance(context).getProfileParamTimestamp(robotId, key);
		if (currentTimestamp != -1) {
			// Hack for robotName. the timestamp is always 0. So check the robotName.
			if (key.equals(ProfileAttributeKeys.ROBOT_NAME)) {
				// From here we will always return true.
				return true;
			}
			LogHelper.logD(TAG, "Current Timestamp: " + currentTimestamp + " For key: "+ key);
			LogHelper.logD(TAG, "New Timestamp: " + timeStamp + " For key: " +key);
			return (Long.valueOf(timeStamp) > Long.valueOf(currentTimestamp));
		}
		return true;
	}
	
	public static boolean saveProfileParam(Context context, String robotId, String key, long timestamp) {
		return DBHelper.getInstance(context).saveProfileParam(robotId, key, timestamp);
	}
	
	public static boolean deleteProfileParamIfExists(Context context, String robotId, String key) {
		if (isProfileKeyExists(context, robotId, key)) {
			return DBHelper.getInstance(context).deleteProfileParamIfExists(robotId, key);
		}
		return false;
	}
}
