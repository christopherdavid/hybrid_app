package com.neatorobotics.android.slide.framework.robot.schedule2;

import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;

public class AdvancedScheduleGroup2 implements Schedules {
	private String mUuid;
	private ArrayList<AdvancedScheduleEvent2> mScheduleList = new ArrayList<AdvancedScheduleEvent2>();
	
	public AdvancedScheduleGroup2(String uuid) {
		mUuid = uuid;
	}
	
	public AdvancedScheduleGroup2() {
		
	} 
	
	public boolean addSchedule(ScheduleEvent schedule) {
		if (schedule instanceof AdvancedScheduleEvent2) {
			return mScheduleList.add((AdvancedScheduleEvent2) schedule);
		}
		return false;
	}
	
	public AdvancedScheduleEvent2 getSchedule(int index) {
		return mScheduleList.get(index);
	}
	public int getSize() {
		return mScheduleList.size();
	}
	
	public boolean removeScheduleEvent(String eventId) {
		Iterator<AdvancedScheduleEvent2> iterator = mScheduleList.iterator();
		while (iterator.hasNext()) {
			AdvancedScheduleEvent2 element = iterator.next();
			if (element.getEventId().equals(eventId)) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}
	
	public boolean updateScheduleEvent(ScheduleEvent event) {
		if (event instanceof AdvancedScheduleEvent2) {
			Iterator<AdvancedScheduleEvent2> iterator = mScheduleList.iterator();
			while (iterator.hasNext()) {
				AdvancedScheduleEvent2 element = iterator.next();
				if (element.getEventId().equals(event.getEventId())) {
					iterator.remove();
					mScheduleList.add((AdvancedScheduleEvent2) event);
					return true;
				}
			}
		}
		return false;
	}
	
	public JSONArray toJsonArray() {
		JSONArray scheduleItems = new JSONArray();
		for (AdvancedScheduleEvent2 schedule: mScheduleList) {
			scheduleItems.put(schedule.toJsonObject());
		}
		return scheduleItems;	
	}
	
	@Override
	public ArrayList<String> getEventIds() {
		ArrayList<String> eventIds = new ArrayList<String>();
		for (AdvancedScheduleEvent2 schedule: mScheduleList) {
			eventIds.add(schedule.getEventId());
		}
		return eventIds;
	}

	@Override
	public ScheduleEvent getEvent(String eventId) {
		ScheduleEvent event = null;
		for (AdvancedScheduleEvent2 schedule: mScheduleList) {
			if (eventId.equals(schedule.getEventId())) {
				event = schedule;
			}
		}
		return event;
	}


	@Override
	public String getId() {
		return mUuid;
	}


	@Override
	public void setUUID(String uuid) {
		mUuid = uuid;
	}
	
	@Override
	public String getJSON() {		
		return toJsonArray().toString();
	}
	
	@Override
	public int getScheduleType() {		
		return SchedulerConstants2.SCHEDULE_TYPE_ADVANCED;
	}
	
	@Override
	public int eventCount() {		
		return mScheduleList.size();
	}
	
	@Override
	public ScheduleEvent getEvent(int index) {
		return mScheduleList.get(index);
	}
}
