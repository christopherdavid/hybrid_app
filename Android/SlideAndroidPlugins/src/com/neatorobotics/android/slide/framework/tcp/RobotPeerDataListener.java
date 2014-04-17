package com.neatorobotics.android.slide.framework.tcp;

import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacket;

public interface RobotPeerDataListener {
    public void onConnect(String robotId);

    public void onDisconnect(String robotId);

    public void onDataReceived(String robotId, RobotCommandPacket robotPacket);

    public void errorInConnecting(String robotId);
}
