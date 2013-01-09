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


	
	
	public static final String KEY_ROBOT_ID = "robot_id";
	public static final String KEY_ROBOT_SERIAL_ID = "serial_id";
	public static final String KEY_ROBOT_IP_ADDRESS = "robot_ip_address";
	public static final String KEY_ROBOT_PORT = "robot_port";
	public static final String KEY_ROBOT_JABBER_ID = "robot_jabber_id";
	public static final String KEY_ROBOT_JABBER_PWD = "robot_jabber_pwd";
	
	public static final String KEY_ROBOT_NAME = "robot_name";
	public static final String KEY_ROBOT_STATE = "robot_state";

	//Robot states
	//TODO: Need to put these constants in relevant class.
	public static final int ROBOT_STATE_CLEANING = 10001;
	public static final int ROBOT_STATE_IDLE = 10002;
	public static final int ROBOT_STATE_CHARGING = 10003;
}
