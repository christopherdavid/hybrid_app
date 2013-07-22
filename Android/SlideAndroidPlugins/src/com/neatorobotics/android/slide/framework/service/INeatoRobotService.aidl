package com.neatorobotics.android.slide.framework.service;

import com.neatorobotics.android.slide.framework.robot.commands.request.RobotRequests;
import  com.neatorobotics.android.slide.framework.robot.commands.request.RequestPacket;

interface INeatoRobotService
{
	void startDiscovery();
	void cancelDiscovery();
	void sendCommand(in String robotId, in RobotRequests requests, in int transportMode);
	void connectToRobot2(in String robot_id);
	void connectToRobot3(in String robot_id, in String robot_ip_address);
	void closePeerConnection(in String robotId);
	void cleanup();
	void loginToXmpp();
	void registerRobotNotifications(String robotId);
	void unregisterRobotNotifications(String robotId);
	boolean isRobotDirectConnected(String robotId);
	boolean isAnyPeerConnectionExists();
}