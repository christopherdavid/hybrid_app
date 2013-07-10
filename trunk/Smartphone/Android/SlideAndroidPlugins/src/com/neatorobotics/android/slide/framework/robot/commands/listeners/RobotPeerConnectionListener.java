package com.neatorobotics.android.slide.framework.robot.commands.listeners;

public interface RobotPeerConnectionListener {
	public void onRobotConnected(String robotId);
	public void onRobotDisconnected(String robotId);
	public void errorInConnecting(String robotId);
}
