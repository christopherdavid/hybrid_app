package com.neatorobotics.android.slide.framework.robot.commands.listeners;

import com.neatorobotics.android.slide.framework.robot.commands.request.ResponsePacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotRequests;

public interface RobotPacketListener {
	public void onResponseReceived(String robotId, ResponsePacket response);
	public void onRequestReceived(String robotId, RobotRequests requests);
}
