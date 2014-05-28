package com.neatorobotics.android.slide.framework.robot.commands;

public class RobotCommandPacketConstants {

    public static final int COMMAND_PACKET_SIGNATURE = 0xCafeBabe;
    public static final int COMMAND_PACKET_VERSION = 1;

    public static final int COMMAND_ROBOT_START = 101;
    public static final int COMMAND_ROBOT_STOP = 102;
    public static final int COMMAND_SEND_BASE = 104;
    public static final int COMMAND_PAUSE_CLEANING = 107;
    public static final int COMMAND_RESUME_CLEANING = 114;
    public static final int COMMAND_DRIVE_ROBOT = 115;
    public static final int COMMAND_TURN_MOTOR_ONOFF = 116;
    public static final int COMMAND_TURN_WIFI_ONOFF = 117;
    
    public static final int COMMAND_SCHEDULE_UPDATED = 118;
    public static final int COMMAND_INTEND_TO_DRIVE = 119;
    
    public static final int COMMAND_ROBOT_PROFILE_DATA_CHANGED = 5001;
    public static final int COMMAND_ROBOT_CONNECTION_PING = 5003;
    public static final int COMMAND_ROBOT_CONNECTION_BREAK = 9999;

    // Notification Ids
    public static final String NOTIFICATIONS_ID_GLOBAL = "global";
    public static final String NOTIFICATION_ID_ROBOT_STUCK = "101";
    public static final String NOTIFICATION_ID_DIRT_BIN_FULL = "102";
    public static final String NOTIFICATION_ID_CLEANING_DONE = "103";
    public static final String NOTIFICATION_ID_GENERIC = "999";

    // Cleaning Category
    public static final int CLEANING_CATEGORY_INVALID = -1;
    public static final int CLEANING_CATEGORY_MANUAL = 1;
    public static final int CLEANING_CATEGORY_ALL = 2;
    public static final int CLEANING_CATEGORY_SPOT = 3;

    // Codes for Motor Types
    public static final int MOTOR_TYPE_VACUUM = 101;
    public static final int MOTOR_TYPE_BRUSH = 102;

    // Default Spot cleaning values
    public static final int DEFAULT_SPOT_CLEANING_LENGTH = 100;
    public static final int DEFAULT_SPOT_CLEANING_HEIGHT = 100;

    public static final String KEY_ROBOT_ID = "robotId";
    public static final String KEY_COMMAND_PARAMS_TAG = "params";
    public static final String KEY_REQUEST_COMMAND = "requestCommand";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_CHAT_ID = "chatId";
    public static final String KEY_CAUSE_AGENT_ID = "causeAgentId";
    public static final String KEY_EMAIL_ID = "emailId";
    public static final String KEY_LINK_CODE = "linkCode";

    // Robot states
    public static final int ROBOT_STATE_CLEANING = 3;
    public static final int ROBOT_STATE_IDLE = 1;
    public static final int ROBOT_STATE_PAUSED = 5;
    public static final int ROBOT_STATE_RETURNING = 7;
    public static final int ROBOT_STATE_MANUAL_CLEANING = 6;
    public static final int ROBOT_STATE_INVALID = 19999;

    // Codes for data changed on server
    public static final int KEY_ROBOT_SCHEDULE_CHANGED = 20001;
    public static final int KEY_ROBOT_DETAILS_CHANGED = 20004;

    // Helper method to get the state of the robot depending on the commandId.
    public static int getRobotStateFromId(int commandId) {
        if (commandId == COMMAND_ROBOT_START) {
            return ROBOT_STATE_CLEANING;
        } else if (commandId == COMMAND_ROBOT_STOP) {
            return ROBOT_STATE_IDLE;
        } else if (commandId == COMMAND_PAUSE_CLEANING) {
            return ROBOT_STATE_PAUSED;
        } else if (commandId == COMMAND_RESUME_CLEANING) {
            return ROBOT_STATE_CLEANING;
        } else if (commandId == COMMAND_SEND_BASE) {
            return ROBOT_STATE_RETURNING;
        }
        return ROBOT_STATE_INVALID;
    }
}
