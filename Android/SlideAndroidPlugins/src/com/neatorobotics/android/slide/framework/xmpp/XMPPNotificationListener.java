package com.neatorobotics.android.slide.framework.xmpp;

import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacket;

public interface XMPPNotificationListener {
	public void onConnectFailed();
	public void onConnectSucceeded();
	public void onLoginFailed();
	public void onLoginSucceeded();
	public void onConnectionReset();
	public void onDisconnect();
	public void onDataReceived(String from, RobotCommandPacket packet);
}
