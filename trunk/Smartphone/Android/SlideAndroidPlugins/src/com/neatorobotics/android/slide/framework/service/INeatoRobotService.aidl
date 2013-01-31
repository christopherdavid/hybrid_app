package com.neatorobotics.android.slide.framework.service;

import com.neatorobotics.android.slide.framework.robot.commands.request.RobotRequests;
import  com.neatorobotics.android.slide.framework.robot.commands.request.RequestPacket;

interface INeatoRobotService
{
	void startDiscovery();
	void cancelDiscovery();
	void sendCommand(in String robotId, in int commandId);
	void sendCommand2(in String robotId, in RobotRequests requests);
	void connectToRobot(in String robot_id);
	void connectToRobot2(in String robot_id);
	void closePeerConnection(in String robotId);
	void cleanup();
	void loginToXmpp();
}