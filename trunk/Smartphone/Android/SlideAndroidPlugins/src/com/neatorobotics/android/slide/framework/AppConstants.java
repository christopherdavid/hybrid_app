package com.neatorobotics.android.slide.framework;

public class AppConstants {
	public static final int APP_SIGNATURE = 0xcafebabe;
	public static final int UDP_PACKET_VERSION = 1;
	public static final int TCP_PACKET_VERSION = 1;
	public static final int COMMAND_PACKET_VERSION = 1;
	
	public static final int UDP_ROBOT_BROADCAST_PORT = 12346;
	public static final int UDP_SMART_APPS_BROADCAST_PORT = 12346;
	
	public static final int TCP_ROBOT_SERVER_SOCKET_PORT = 4444;
	
	// TODO: As of now we are using IP address but eventually we need to switch to 
	// URL. 
	public static final int JABBER_SERVER_PORT =  5222;
	public static final String JABBER_WEB_SERVICE = "rajatogo" ;
	
	// TODO: As of now we are using the hardcoded jabber id but once we have
	// user management in place, we need to fetch the jabber id and password from server
//	public static final String JABBER_USER_ID = "neatosmartapptest";
//	public static final String JABBER_CHAT_PASSWORD = "neato123";
	public static final String JABBER_PACKET_PROPERTY_KEY = "robot_packet";
	
	
	//TODO : Right now hard-coded to be sent to robot. Later will be removed.
//	public static final String JABBER_ROBOT_ID = "neatorobotsimulator";
//	public static final String JABBER_ROBOT_PWD = "neato123";

}