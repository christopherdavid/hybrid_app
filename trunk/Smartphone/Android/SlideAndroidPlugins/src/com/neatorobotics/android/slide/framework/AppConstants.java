package com.neatorobotics.android.slide.framework;

public class AppConstants {
	public static final int APP_SIGNATURE = 0xCafeBabe;
	public static final int UDP_PACKET_VERSION = 1;
	public static final int TCP_PACKET_VERSION = 1;
	public static final int COMMAND_PACKET_VERSION = 1;
	
	public static final int TCP_ROBOT_SERVER_SOCKET_PORT = 4444;
	public static final int TCP_ROBOT_SERVER_SOCKET_PORT2 = 49001;
	
	public static final int JABBER_SERVER_PORT =  5222;
	public static final String JABBER_WEB_SERVICE = "rajatogo" ;
	
	public static final String JABBER_PACKET_PROPERTY_KEY = "robot_packet";
	
	public static final String NETWORK_ERROR_STRING = "Network Error";
	
	/*
	 * The support for saving Robot Commands on server instead of sending it VIA XMPP/TCP
	 * is added. To use this functionality, the value of DEFAULT_IS_SERVER_DATA_MODE_ENABLED constant
	 * should be set to true.
     * If set to TRUE the robot commands will be saved on the server and will be accessed by robot from the
	 * the server.
	 * If set to false, the commands will be sent VIA XMPP/TCP connection and server won't be involved.
	 */
	private static final boolean IS_SERVER_DATA_MODE_ENABLED = false;
	
	public static boolean isServerDataModeEnabled() {
		return IS_SERVER_DATA_MODE_ENABLED;
	}
}