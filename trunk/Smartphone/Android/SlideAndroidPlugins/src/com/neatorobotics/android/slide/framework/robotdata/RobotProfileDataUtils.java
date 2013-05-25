package com.neatorobotics.android.slide.framework.robotdata;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacketUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails2.ProfileAttributeKeys;

public class RobotProfileDataUtils {

	public static final String TAG = RobotProfileDataUtils.class.getSimpleName();
	
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
	
	private static boolean isDataChanged(Context context, GetRobotProfileDetailsResult2 details, String robotId, String key) {
		long timeStamp = details.getProfileParameterTimeStamp(key);
		return RobotHelper.isProfileDataChanged(context, robotId, key, timeStamp);
	}
	
	//Returns true if data is changed or existing data was deleted.
	//It also updates the database accordingly.
	protected static boolean isDataChangedAndSave(Context context, GetRobotProfileDetailsResult2 details, String robotId, String key) {
		
		//Check if the key is available in the details.
		if (details.contains(key)) {
			//Check to see if it is changed.
			if (isDataChanged(context, details, robotId, key)) {
				LogHelper.log(TAG, "Data is changed. Saving data for key" + key);
				long timestamp = details.getProfileParameterTimeStamp(key);
				RobotHelper.saveProfileParam(context, robotId, key, timestamp);
				return true;
			}
			LogHelper.log(TAG, "Data is not changed, for key" + key);
			return false;
		} 
		else {
			//The data is not available. Delete the key from database.
			//Return true only if the delete was successful. if the key wasn't present then it will return false.
			return RobotHelper.deleteProfileParamIfExists(context, robotId, key);
		}
	}
}
