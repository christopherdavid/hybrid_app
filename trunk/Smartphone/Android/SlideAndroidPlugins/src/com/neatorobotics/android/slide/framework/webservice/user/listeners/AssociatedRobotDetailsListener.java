package com.neatorobotics.android.slide.framework.webservice.user.listeners;

import java.util.ArrayList;

import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;

public interface AssociatedRobotDetailsListener {
	public void onRobotDetailsReceived(ArrayList<RobotItem> robotList);
	public void onNetworkError(String err);
	public void onServerError(String err);
}
