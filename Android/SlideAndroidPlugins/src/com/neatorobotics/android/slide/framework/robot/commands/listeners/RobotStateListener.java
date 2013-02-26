package com.neatorobotics.android.slide.framework.robot.commands.listeners;

import android.os.Bundle;

/*
* Interface to receive robot state information callback when user 
* requesting a robot for a state
*/
public interface RobotStateListener {	
	public void onStateReceived(String robotId, Bundle bundle);	
}
