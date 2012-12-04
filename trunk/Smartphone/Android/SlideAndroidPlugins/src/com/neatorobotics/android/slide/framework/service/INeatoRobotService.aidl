package com.neatorobotics.android.slide.framework.service;


interface INeatoRobotService
{
	void startDiscovery();
	void cancelDiscovery();
	void sendCommand(in String ipAddress, in int commandId, in boolean useXmppServer );
	void connectToRobot(in String ipAddress);
	void closePeerConnection(in String ipAddress);
	void cleanup();
	void associateRobot(String serialId, String emailId);
	void loginToXmpp();
}