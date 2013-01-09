package com.neatorobotics.android.slide.framework.service;


interface INeatoRobotService
{
	void startDiscovery();
	void cancelDiscovery();
	void sendCommand(in String robotId, in int commandId);
	void connectToRobot(in String robot_id);
	void closePeerConnection(in String robotId);
	void cleanup();
	void associateRobot(String serialId, String emailId);
	void loginToXmpp();
}