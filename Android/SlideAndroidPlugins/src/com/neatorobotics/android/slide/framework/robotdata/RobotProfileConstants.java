package com.neatorobotics.android.slide.framework.robotdata;

import java.util.HashMap;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeKeys;

public class RobotProfileConstants {

    private static final String TAG = RobotProfileConstants.class.getSimpleName();
    private static final HashMap<String, Boolean> PROFILE_KEY_TIMER_EXPIRY_MAP = new HashMap<String, Boolean>();

    static {
        PROFILE_KEY_TIMER_EXPIRY_MAP.put(ProfileAttributeKeys.ROBOT_CLEANING_COMMAND, true);
    }

    public static String getProfileKeyTypeForCommand(int commandId) {

        LogHelper.log(TAG, "getProfileKeyType for commandId:" + commandId);
        switch (commandId) {
            case RobotCommandPacketConstants.COMMAND_ROBOT_START:
            case RobotCommandPacketConstants.COMMAND_ROBOT_STOP:
            case RobotCommandPacketConstants.COMMAND_PAUSE_CLEANING:
            case RobotCommandPacketConstants.COMMAND_RESUME_CLEANING:
            case RobotCommandPacketConstants.COMMAND_SEND_BASE:
                return ProfileAttributeKeys.ROBOT_CLEANING_COMMAND;
            case RobotCommandPacketConstants.COMMAND_SCHEDULE_UPDATED:
                return ProfileAttributeKeys.ROBOT_SCHEDULE_UPDATED;
            default:
                return null;
        }
    }

    public static boolean isTimerExpirableForProfileKey(String key) {
        if (PROFILE_KEY_TIMER_EXPIRY_MAP.containsKey(key)) {
            return PROFILE_KEY_TIMER_EXPIRY_MAP.get(key);
        }
        return false;
    }

    // Used to see if the command should be sent VIA timedmode or XMPP.
    // Add profileattribute key whenever a support for command is added here.
    public static boolean isCommandSendViaServer(int commandId) {
        LogHelper.logD(TAG, "isTimedModeSupportedForCommand called for commandId:" + commandId);
        switch (commandId) {
            case RobotCommandPacketConstants.COMMAND_ROBOT_START:
            case RobotCommandPacketConstants.COMMAND_ROBOT_STOP:
            case RobotCommandPacketConstants.COMMAND_PAUSE_CLEANING:
            case RobotCommandPacketConstants.COMMAND_RESUME_CLEANING:
            case RobotCommandPacketConstants.COMMAND_SEND_BASE:
                return true;
            default:
                return false;
        }
    }

    public static enum RobotProfileValueChangedStatus {
        ROBOT_VALUE_NOT_CHANGED, ROBOT_VALUE_DELETED, ROBOT_VALUE_CHANGED
    }

    public static String getScheduleKey(int scheduleType) {
        String scheduleTypeInStr = null;
        if (scheduleType == SchedulerConstants.SERVER_SCHEDULE_TYPE_BASIC) {
            scheduleTypeInStr = ProfileAttributeKeys.ROBOT_ENABLE_BASIC_SCHEDULE;
        }
        return scheduleTypeInStr;
    }

}
