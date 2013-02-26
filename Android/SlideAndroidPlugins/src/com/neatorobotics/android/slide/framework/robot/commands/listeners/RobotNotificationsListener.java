package com.neatorobotics.android.slide.framework.robot.commands.listeners;

import android.os.Bundle;

/*
* Interface to receive robot status change callbacks if user 
* registered for these notifications
*/
public interface RobotNotificationsListener {
	public void onStatusChanged(String robotId, Bundle bundle);
	public void onRegister(String robotId, Bundle bundle);
	public void onUnregister(String robotId, Bundle bundle);
	public void onRobotCommandReceived(String robotId, String commandId, Bundle bundle);
}
