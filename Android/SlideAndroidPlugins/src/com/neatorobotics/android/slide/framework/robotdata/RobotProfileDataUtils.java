package com.neatorobotics.android.slide.framework.robotdata;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacketUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;

public class RobotProfileDataUtils {

	public static final String TAG = RobotProfileDataUtils.class.getSimpleName();
	
	public static String getRobotVirtualState(Context context, GetRobotProfileDetailsResult2 details) {
		String virtualState = null;
		if (details.getCleaningCommand() != null) {
			LogHelper.logD(TAG, "getServerData, retrieved ROBOT_CLEANING_COMMAND changed");
			String cleaningCommand = details.getCleaningCommand();
			if (!TextUtils.isEmpty(cleaningCommand)) {
				LogHelper.logD(TAG, "getServerData, retrieved ROBOT_CLEANING_COMMAND changed: " +cleaningCommand);
				int commandId = RobotCommandPacketUtils.getRobotIdFromCommand(context, cleaningCommand);
				int state = RobotCommandPacketConstants.getRobotStateFromId(commandId);
				if (state != RobotCommandPacketConstants.ROBOT_STATE_INVALID) {
					virtualState = String.valueOf(state);
				}
			}
		}
		return virtualState;
	}
	
	public static String getRobotCurrentState(Context context, GetRobotProfileDetailsResult2 details) {
		String currentState = details.getRobotCurrentState();
		LogHelper.logD(TAG, "getServerData, retrieved ROBOT_CURRENT_STATE");
		return currentState;
	}
	
	public static String getActualState(String virtualState, String currentState) {
		String actualState = null;
		if (!TextUtils.isEmpty(virtualState)) {
			actualState = virtualState;
		} else if (!TextUtils.isEmpty(currentState)) {
			actualState = currentState;
		}
		return actualState;
	}
}
