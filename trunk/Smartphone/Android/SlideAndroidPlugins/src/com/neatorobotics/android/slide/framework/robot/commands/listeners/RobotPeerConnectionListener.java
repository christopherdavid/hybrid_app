package com.neatorobotics.android.slide.framework.robot.commands.listeners;

// TODO: Add robotId as argument.
public interface RobotPeerConnectionListener {
	public void onRobotConnected();
	public void onRobotDisconnected();
	public void errorInConnecting();
}
