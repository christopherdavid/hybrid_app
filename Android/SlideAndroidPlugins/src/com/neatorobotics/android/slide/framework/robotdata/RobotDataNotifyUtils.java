package com.neatorobotics.android.slide.framework.robotdata;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.resultreceiver.NeatoRobotResultReceiverConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDataListener;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppsEventConstants;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails2.ProfileAttributeKeys;

public class RobotDataNotifyUtils {
	
	public static final String TAG = RobotDataNotifyUtils.class.getSimpleName();
	
	public static void addRobotDataChangedListener(Context context, RobotDataListener listener) {
		if (ApplicationConfig.getInstance(context) != null) {
			ApplicationConfig.getInstance(context).getRobotResultReceiver().addRobotDataListener(listener);
		}
	}
	
	public static void removeRobotDataChangedListener(Context context) {
		if (ApplicationConfig.getInstance(context) != null) {
			ApplicationConfig.getInstance(context).getRobotResultReceiver().addRobotDataListener(null);
		}
	}
	
	protected static void notifyProfileDataIfChanged(Context context, String robotId, GetRobotProfileDetailsResult2 details) {
		
		boolean isRobotNameChanged = RobotProfileDataUtils.isDataChangedAndSave(context, details, robotId, ProfileAttributeKeys.ROBOT_NAME);
		// Notify if robot name is changed.
		if (isRobotNameChanged) {
			notifyRobotNameChange(context, robotId, details);
		}
		
		boolean isRobotCleaningCommandChanged = RobotProfileDataUtils.isDataChangedAndSave(context, details, robotId, ProfileAttributeKeys.ROBOT_CLEANING_COMMAND);
		boolean isRobotCurrentStateChanged = RobotProfileDataUtils.isDataChangedAndSave(context, details, robotId, ProfileAttributeKeys.ROBOT_CURRENT_STATE);
		// Notify only when one of the virtual or current has changed.
		if (isRobotCleaningCommandChanged || isRobotCurrentStateChanged) {
			notifyStateChange(context, robotId, details);
		}
		
		boolean isRobotScheduleChanged = RobotProfileDataUtils.isDataChangedAndSave(context, details, robotId, ProfileAttributeKeys.ROBOT_ENABLE_SCHEDULE);
		if (isRobotScheduleChanged) {
			notifyScheduleStateChange(context, robotId, details);
		}
		
		
	}
	
	// Private helper method to consume the profile data parameters.
	private static void notifyScheduleStateChange(Context context, String robotId, GetRobotProfileDetailsResult2 details) {
		String basicScheduleState = RobotProfileDataUtils.getBasicScheduleState(context, details);
		LogHelper.logD(TAG, "Robot Schedule State :" + basicScheduleState);
		if(!TextUtils.isEmpty(basicScheduleState)) {
			HashMap<String, String> stateData = new HashMap<String, String>();
			stateData.put(JsonMapKeys.KEY_SCHEDULE_STATE, basicScheduleState);
			stateData.put(JsonMapKeys.KEY_SCHEDULE_TYPE, String.valueOf(SchedulerConstants2.SCHEDULE_TYPE_BASIC));
			notifyDataChanged(context, robotId, RobotProfileConstants.ROBOT_SCHEDULE_STATE_CHANGED, stateData);
		}
	}
	
	private static void notifyRobotNameChange(Context context, String robotId, GetRobotProfileDetailsResult2 details) {
		String robotName = RobotProfileDataUtils.getRobotName(details);
		if (!TextUtils.isEmpty(robotName)) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put(JsonMapKeys.KEY_ROBOT_NAME, robotName);
			notifyDataChanged(context, robotId, RobotProfileConstants.ROBOT_NAME_UPDATE, data);
		}
	}
	
	private static void notifyStateChange (Context context, String robotId, GetRobotProfileDetailsResult2 details) {
		
		String virtualState = RobotProfileDataUtils.getRobotVirtualState(context, details);
		String currentState = RobotProfileDataUtils.getRobotCurrentState(context, details);
		String state = RobotProfileDataUtils.getState(virtualState, currentState);
		if (!TextUtils.isEmpty(currentState)) {
			HashMap<String, String> stateData = new HashMap<String, String>();
			stateData.put(JsonMapKeys.KEY_ROBOT_CURRENT_STATE, currentState);
			notifyDataChanged(context, robotId, RobotProfileConstants.ROBOT_CURRENT_STATE_CHANGED, stateData);
			LogHelper.log(TAG, "Current state Received from Web server: " + currentState);
		}
		
		if (!TextUtils.isEmpty(state)) {
			HashMap<String, String> stateData = new HashMap<String, String>();
			stateData.put(JsonMapKeys.KEY_ROBOT_STATE_UPDATE, String.valueOf(state));
			notifyDataChanged(context, robotId, RobotProfileConstants.ROBOT_STATE_UPDATE, stateData);
		}
		else {
			LogHelper.logD(TAG, "No State for robotId: "+robotId);
		}
	}
	
	
	// Helper method to send notification.
	private static void notifyDataChanged(Context context, String robotId, int keyCode, HashMap<String, String> data) {
		int resultCode = NeatoSmartAppsEventConstants.ROBOT_DATA;
		Bundle dataChanged = new Bundle();
		dataChanged.putString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID, robotId);
		dataChanged.putInt(NeatoRobotResultReceiverConstants.ROBOT_DATA_KEY_CODE, keyCode);
		dataChanged.putSerializable(NeatoRobotResultReceiverConstants.ROBOT_DATA_KEY, data);
		ApplicationConfig.getInstance(context).getRobotResultReceiver().send(resultCode, dataChanged);
	}
}
