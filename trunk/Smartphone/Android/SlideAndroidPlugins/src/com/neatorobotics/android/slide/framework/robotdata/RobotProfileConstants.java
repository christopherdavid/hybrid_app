package com.neatorobotics.android.slide.framework.robotdata;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails2.ProfileAttributeKeys;

public class RobotProfileConstants {
	
	private static final String TAG = RobotProfileConstants.class.getSimpleName();
	
	public static String getProfileKeyType(int commandId) {
		
		LogHelper.log(TAG, "getProfileKeyType for commandId:" + commandId);
		switch (commandId) {
			case RobotCommandPacketConstants.COMMAND_ROBOT_START:
			case RobotCommandPacketConstants.COMMAND_ROBOT_STOP:
			case RobotCommandPacketConstants.COMMAND_PAUSE_CLEANING:
			case RobotCommandPacketConstants.COMMAND_RESUME_CLEANING:
				return ProfileAttributeKeys.ROBOT_CLEANING_COMMAND;
			default:
				return null;
		}
	}

	//Key Codes for profile data changes.
	public static final int ROBOT_CURRENT_STATE_CHANGED = 4001;
	public static final int ROBOT_STATE_CHANGED = 4002;
	public static final int ROBOT_STATE_UPDATE = 4003;
}
