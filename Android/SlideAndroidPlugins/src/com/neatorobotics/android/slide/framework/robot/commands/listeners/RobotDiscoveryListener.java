package com.neatorobotics.android.slide.framework.robot.commands.listeners;

import com.neatorobotics.android.slide.framework.model.RobotInfo;

public interface RobotDiscoveryListener {
	public void onNewRobotFound(RobotInfo robotinfo);
	public void onDiscoveryStarted();
	public void onDiscoveryFinished();
	public void discoveryError();
}
