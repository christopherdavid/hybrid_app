package com.neatorobotics.android.slide.framework.udp;

import com.neatorobotics.android.slide.framework.model.RobotInfo;

public interface RobotDiscovery {
	public void onDiscoveryStarted();
	public void onDiscoveryEnd();
	public void onRobotDiscovered(RobotInfo robotInfo);
}
