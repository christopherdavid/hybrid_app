package com.neatorobotics.android.slide.framework.robot.schedule;

import java.util.ArrayList;

import org.json.JSONArray;

public interface Schedules {
    public void setUUID(String uuid);

    public String getId();

    public String getJSON();

    public JSONArray toJsonArray();

    public ArrayList<String> getEventIds();

    public ScheduleEvent getEvent(String eventId);

    public boolean addSchedule(ScheduleEvent schedule);

    public boolean removeScheduleEvent(String eventId);

    public boolean updateScheduleEvent(ScheduleEvent schedule);

    public int getScheduleType();

    public int eventCount();

    public ScheduleEvent getEvent(int index);
}
