package com.neatorobotics.android.slide.framework.tcp;

import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;


public interface TcpDataPacketListener {
	public void onConnect();
	public void onDisconnect();
	public void onDataReceived(RobotPacket robotPacket);
}
