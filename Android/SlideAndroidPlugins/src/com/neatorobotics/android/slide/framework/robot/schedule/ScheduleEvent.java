package com.neatorobotics.android.slide.framework.robot.schedule;

import org.json.JSONObject;

public interface ScheduleEvent {
    public JSONObject toJsonObject();

    public String getEventId();
}
