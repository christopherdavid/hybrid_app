package com.neatorobotics.android.slide.framework.robotdata;


import java.util.HashMap;
import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacketUtils;
import com.neatorobotics.android.slide.framework.robotdata.RobotProfileConstants.RobotProfileValueChangedStatus;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeKeys;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeKeysEnum;

public class RobotProfileDataUtils {

	private static final String TAG = RobotProfileDataUtils.class.getSimpleName();
	
	public static String getRobotVirtualState(Context context, GetRobotProfileDetailsResult2 details) {
		String virtualState = null;
		if (details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_CLEANING_COMMAND) != null) {
			LogHelper.logD(TAG, "getRobotVirtualState, retrieved ROBOT_CLEANING_COMMAND changed");
			String cleaningCommand = details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_CLEANING_COMMAND);
			if (!TextUtils.isEmpty(cleaningCommand)) {
				LogHelper.logD(TAG, "getRobotVirtualState, retrieved ROBOT_CLEANING_COMMAND changed: " +cleaningCommand);
				int commandId = RobotCommandPacketUtils.getRobotIdFromCommand(cleaningCommand);
				int state = RobotCommandPacketConstants.getRobotStateFromId(commandId);
				if (state != RobotCommandPacketConstants.ROBOT_STATE_INVALID) {
					virtualState = String.valueOf(state);
				}
			}
		}
		return virtualState;
	}
	
	public static boolean contains(GetRobotProfileDetailsResult2 details, String key) {
		return details.contains(key);
	}
	
	public static String getRobotName(GetRobotProfileDetailsResult2 details) {
		String robotName = details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_NAME);
		return robotName;
	}
	
	public static String getRobotCurrentState(Context context, GetRobotProfileDetailsResult2 details) {
		String currentState = details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_CURRENT_STATE);
		LogHelper.logD(TAG, "getServerData, retrieved ROBOT_CURRENT_STATE");
		return currentState;
	}
	
	public static String getBasicScheduleState(Context context, GetRobotProfileDetailsResult2 details) {
		String scheduleState = details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_ENABLE_BASIC_SCHEDULE);
		LogHelper.logD(TAG, "getBasicScheduleState, retrived ROBOT_SCHEDULE");
		return scheduleState;
	}
	
	public static boolean isScheduleUpdated(Context context, GetRobotProfileDetailsResult2 details) {
		String isScheduleUpdated = details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_SCHEDULE_UPDATED);
		LogHelper.logD(TAG, "isScheduleUpdated, retrived ROBOT_SCHEDULE");
		return Boolean.valueOf(isScheduleUpdated);
	}
	
	public static String getState(String virtualState, String currentState) {
		String actualState = null;
		if (!TextUtils.isEmpty(virtualState)) {
			actualState = virtualState;
		} else if (!TextUtils.isEmpty(currentState)) {
			actualState = currentState;
		}
		return actualState;
	}
	
	public static String getState(Context context, GetRobotProfileDetailsResult2 details) {
		String virtualState = getRobotVirtualState(context, details);
		String currentState = getRobotCurrentState(context, details);
		return getState(virtualState, currentState);
	}
	
	private static boolean isDataChanged(Context context, GetRobotProfileDetailsResult2 details, String robotId, String key) {
		long timeStamp = details.getProfileParameterTimeStamp(key);
		return RobotHelper.isProfileDataChanged(context, robotId, key, timeStamp);
	}
	
	// Returns if data is changed or existing data was deleted.
	// There are 3 cases which will return ROBOT_VALUE_NOT_CHANGED, ROBOT_VALUE_DELETED, ROBOT_VALUE_CHANGED
	// from enum RobotProfileKeyChangedStatus
	// It returns ROBOT_VALUE_CHANGED if the data is changed/added newly
	// It returns ROBOT_VALUE_NOT_CHANGED if the data is not changed
	// It returns ROBOT_VALUE_DELETED if the data is deleted
	// It also updates the database accordingly.
	public static RobotProfileValueChangedStatus updateDataTimestampIfChanged(Context context, GetRobotProfileDetailsResult2 details, String robotId, String key) {
		
		//Check if the key is available in the details.
		if (details.contains(key)) {
			// Check to see if it is changed
			if (isDataChanged(context, details, robotId, key)) {
				LogHelper.log(TAG, "Data is changed. Saving data for key" + key);
				long timestamp = details.getProfileParameterTimeStamp(key);
				RobotHelper.saveProfileParam(context, robotId, key, timestamp);
				return RobotProfileValueChangedStatus.ROBOT_VALUE_CHANGED;
			}
			LogHelper.log(TAG, "Data is not changed, for key" + key);
			return RobotProfileValueChangedStatus.ROBOT_VALUE_NOT_CHANGED;
		} 
		else {
			//The data is not available. Delete the key from database.
			//Return true only if the delete was successful. if the key wasn't present then it will return false.
			boolean deleteSuccessful = RobotHelper.deleteProfileParamIfExists(context, robotId, key);
			// return the status according to the deletion is successful or not.
			return (deleteSuccessful ? RobotProfileValueChangedStatus.ROBOT_VALUE_DELETED : RobotProfileValueChangedStatus.ROBOT_VALUE_NOT_CHANGED);
		}
	}
	
	// Get the Profile keys whose timestamp values have been changed and are different from what are in the database.
	// This will return an HashMap<ProfileAttributeKeysEnum, RobotProfileKeyChangedStatus> with the profile keys.
	public static HashMap<ProfileAttributeKeysEnum, RobotProfileValueChangedStatus> getChangedProfileKeysMap(Context context, GetRobotProfileDetailsResult2 details, String robotId) {
		HashMap<ProfileAttributeKeysEnum, RobotProfileValueChangedStatus> profileKeys = new HashMap<ProfileAttributeKeysEnum, RobotProfileValueChangedStatus>();
		if ((details != null) && (details.result != null) && ((details.result.profile_details != null))) {
			// We need to know what values have been deleted from the profile parameters also. 
			// This is because we want to delete those from the database as well and in some cases notify the user too.
			for (ProfileAttributeKeysEnum key : ProfileAttributeKeysEnum.values()) {
				String profileKey = SetRobotProfileDetails3.getProfileKey(key);
				if (profileKey != null) {
					RobotProfileValueChangedStatus profileKeyValueUpdated = RobotProfileDataUtils.updateDataTimestampIfChanged(context, details, robotId, profileKey);
					if (profileKeyValueUpdated != RobotProfileValueChangedStatus.ROBOT_VALUE_NOT_CHANGED) {
						profileKeys.put(key, profileKeyValueUpdated);
					}
				}
				else {
					LogHelper.logD(TAG, "Profile-value is not present for the ProfileAttributeKeysEnum key");
				}
			}
		}
		return removeDuplicateKeysFromMapBeforeNotification(profileKeys);
	}
	
	// Some profile keys are mutually dependent. So send only 1 of them to the notification layer if multiple of them exist.
	private static HashMap<ProfileAttributeKeysEnum, RobotProfileValueChangedStatus> removeDuplicateKeysFromMapBeforeNotification(HashMap<ProfileAttributeKeysEnum, RobotProfileValueChangedStatus> changedProfileKeys) {
		boolean isRobotCleaningCommandChanged = changedProfileKeys.containsKey(ProfileAttributeKeysEnum.ROBOT_CLEANING_COMMAND);
		boolean isRobotCurrentStateChanged = changedProfileKeys.containsKey(ProfileAttributeKeysEnum.ROBOT_CURRENT_STATE);
		if (isRobotCleaningCommandChanged && isRobotCurrentStateChanged) {
			changedProfileKeys.remove(ProfileAttributeKeysEnum.ROBOT_CURRENT_STATE);
		}
		return changedProfileKeys;
	}
	
}