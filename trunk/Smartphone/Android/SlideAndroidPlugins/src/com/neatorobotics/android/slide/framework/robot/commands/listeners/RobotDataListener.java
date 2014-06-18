package com.neatorobotics.android.slide.framework.robot.commands.listeners;


import org.json.JSONObject;

public interface RobotDataListener {
    public void onDataReceived(String robotId, int dataCode, JSONObject data);
}
