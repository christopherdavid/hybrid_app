package com.neatorobotics.android.slide.framework.robot.commands;

public class RobotCommandPacketConstants {
	
	//TODO: Base 1000 for replies. change commandIds.
	public static final int PACKET_TYPE_ROBOT_DISCOVERY = 1;
	public static final int PACKET_TYPE_ROBOT_DISCOVERY_RESPONSE = 2;
	public static final int PACKET_TYPE_ASSOCIATED_ROBOT_DISCOVERY = 3;
	public static final int PACKET_TYPE_ASSOCIATED_ROBOT_DISCOVERY_RESPONSE = 4;
	public static final int COMMAND_ROBOT_START = 101;
	public static final int COMMAND_ROBOT_STOP = 102;
	public static final int COMMAND_ROBOT_JABBER_DETAILS = 103;
	public static final int COMMAND_SEND_BASE = 104;
	public static final int COMMAND_GET_ROBOT_STATE = 105;
	public static final int COMMAND_SEND_ROBOT_STATE = 106;
	public static final int COMMAND_PAUSE_CLEANING = 107;
	public static final int COMMAND_ENABLE_SCHEDULE = 108;
	public static final int COMMAND_DATA_CHANGED_ON_SERVER = 109;
	public static final int COMMAND_SET_ROBOT_TIME = 110;
	public static final int COMMAND_REGISTER_STATUS_NOTIFICATIONS = 111;
	public static final int COMMAND_UNREGISTER_STATUS_NOTIFICATIONS = 112;
	public static final int COMMAND_STATUS_NOTIFICATION = 113;
	public static final int COMMAND_RESUME_CLEANING = 114;
	
	//Special notifications have base 20000;
	public static final int COMMAND_ROBOT_STUCK = 20001;
	public static final int COMMAND_DIRT_BAG_FULL = 20002;

	// Cleaning Category
	public static final int CLEANING_CATEGORY_MANUAL = 1;
	public static final int CLEANING_CATEGORY_ALL = 2;
	public static final int CLEANING_CATEGORY_SPOT = 3;

	public static final String KEY_ROBOT_ID 								= "robotId";
	public static final String KEY_ROBOT_SERIAL_ID 							= "serialId";
	public static final String KEY_ROBOT_IP_ADDRESS 						= "robotIpAddress";
	public static final String KEY_ROBOT_PORT 								= "robotPort";
	public static final String KEY_ROBOT_JABBER_ID 							= "robotChatId";
	public static final String KEY_COMMAND_PARAMS_TAG 						= "params";
	public static final String KEY_DATA_CODE_CHANGED_ON_SERVER 				= "dataCodeChangedOnServer";
	
	public static final String KEY_ROBOT_NAME 								= "robotName";
	public static final String KEY_ROBOT_STATE 								= "robotState";
	public static final String KEY_REQUEST_COMMAND 							= "requestCommand";
	
	public static final String KEY_USER_ID 									= "userId";
	public static final String KEY_CHAT_ID 									= "chatId";

	//Robot states
	//TODO: Need to put these constants in relevant class.
	public static final int ROBOT_STATE_CLEANING 							= 10001;
	public static final int ROBOT_STATE_IDLE 								= 10002;
	public static final int ROBOT_STATE_CHARGING 							= 10003;
	//Codes for data changed on server
	public static final int KEY_ROBOT_SCHEDULE_CHANGED 						= 20001;
	public static final int KEY_ROBOT_ATLAS_CHANGED 						= 20002;
	public static final int KEY_ROBOT_MAP_CHANGED 							= 20003;
	public static final int KEY_ROBOT_DETAILS_CHANGED 						= 20004;
}
