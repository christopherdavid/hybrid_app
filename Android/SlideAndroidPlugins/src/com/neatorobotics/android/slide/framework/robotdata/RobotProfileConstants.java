package com.neatorobotics.android.slide.framework.robotdata;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails2.ProfileAttributeKeys;

public class RobotProfileConstants {
	
	private static final String TAG = RobotProfileConstants.class.getSimpleName();
	
	public static String getProfileKeyTypeForCommand(int commandId) {
		
		LogHelper.log(TAG, "getProfileKeyType for commandId:" + commandId);
		switch (commandId) {
			case RobotCommandPacketConstants.COMMAND_ROBOT_START:
			case RobotCommandPacketConstants.COMMAND_ROBOT_STOP:
			case RobotCommandPacketConstants.COMMAND_PAUSE_CLEANING:
			case RobotCommandPacketConstants.COMMAND_RESUME_CLEANING:
			case RobotCommandPacketConstants.COMMAND_SEND_BASE:
				return ProfileAttributeKeys.ROBOT_CLEANING_COMMAND;
			case RobotCommandPacketConstants.COMMAND_TURN_VACUUM_ONOFF:
				return ProfileAttributeKeys.ROBOT_TURN_VACUUM_ONOFF;
			case RobotCommandPacketConstants.COMMAND_TURN_WIFI_ONOFF:
				return ProfileAttributeKeys.ROBOT_TURN_WIFI_ONOFF;
			default:
				return null;
		}
	}
	
	// Used to see if the command should be sent VIA timedmode or XMPP.
	// Add profileattribute key whenever a support for command is added here.
	public static boolean isTimedModeSupportedForCommand(int commandId) {
		LogHelper.logD(TAG, "isTimedModeSupportedForCommand clalled for commandId:" + commandId);
		switch (commandId) {
			case RobotCommandPacketConstants.COMMAND_ROBOT_START:
			case RobotCommandPacketConstants.COMMAND_ROBOT_STOP:
			case RobotCommandPacketConstants.COMMAND_PAUSE_CLEANING:
			case RobotCommandPacketConstants.COMMAND_RESUME_CLEANING:
			case RobotCommandPacketConstants.COMMAND_SEND_BASE:
			case RobotCommandPacketConstants.COMMAND_TURN_VACUUM_ONOFF:
			case RobotCommandPacketConstants.COMMAND_TURN_WIFI_ONOFF:
				return true;
			default:
				return false;
		}
	}
	
	public static String getProfileKeyType(String key) {
		return key;
	}
	
	//Key Codes for profile data changes.
	
	public static final int ROBOT_CURRENT_STATE_CHANGED 	= 4001;
	public static final int ROBOT_STATE_CHANGED 			= 4002;
	public static final int ROBOT_STATE_UPDATE 				= 4003;
	public static final int ROBOT_NAME_UPDATE  				= 4004;
	public static final int ROBOT_SCHEDULE_STATE_CHANGED 	= 4005;
}
