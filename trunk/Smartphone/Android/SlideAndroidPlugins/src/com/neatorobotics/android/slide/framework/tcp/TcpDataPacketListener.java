package com.neatorobotics.android.slide.framework.tcp;

import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;


public interface TcpDataPacketListener {
	public void onConnect(String robotId);
	public void onDisconnect(String robotId);
	public void onDataReceived(String robotId, RobotPacket robotPacket);
	public void errorInConnecting(String robotId);
}
