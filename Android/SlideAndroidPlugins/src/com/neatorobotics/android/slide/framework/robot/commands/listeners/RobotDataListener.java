package com.neatorobotics.android.slide.framework.robot.commands.listeners;

import java.util.HashMap;

public interface RobotDataListener {
    public void onDataReceived(String robotId, int dataCode, HashMap<String, String> data);
}
