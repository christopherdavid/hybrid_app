package com.neatorobotics.android.slide.framework.service;

import com.neatorobotics.android.slide.framework.robot.commands.request.RobotRequests;
import  com.neatorobotics.android.slide.framework.robot.commands.request.RequestPacket;

interface INeatoRobotService
{
	void startDiscovery();
	void cancelDiscovery();
	void sendCommand2(in String robotId, in RobotRequests requests);
	void connectToRobot2(in String robot_id);
	void closePeerConnection(in String robotId);
	void cleanup();
	void loginToXmpp();
	void registerRobotNotifications(String robotId);
	void unregisterRobotNotifications(String robotId);
}