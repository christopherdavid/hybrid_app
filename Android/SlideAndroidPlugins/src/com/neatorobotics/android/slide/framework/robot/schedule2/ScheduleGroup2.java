package com.neatorobotics.android.slide.framework.robot.schedule2;

import java.util.ArrayList;

import org.json.JSONArray;

public interface ScheduleGroup2 {
	public void setUUID(String uuid);
	public String getId();
	public String getXml();
	public String getBlobData();
	public JSONArray toJsonArray();
	public ArrayList<String> getEventIds();
	public Schedule2 getEvent(String eventId);
	public boolean addSchedule(Schedule2 schedule);
	public boolean removeScheduleEvent(String eventId);
	public boolean updateScheduleEvent(Schedule2 schedule);
}
