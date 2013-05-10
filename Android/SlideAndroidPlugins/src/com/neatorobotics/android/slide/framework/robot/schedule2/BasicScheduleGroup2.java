package com.neatorobotics.android.slide.framework.robot.schedule2;

import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;

public class BasicScheduleGroup2 implements Schedules {
	private static final String TAG = BasicScheduleGroup2.class.getSimpleName();
	
	private String mUuid;
	private ArrayList<BasicScheduleEvent2> mScheduleList = new ArrayList<BasicScheduleEvent2>();

	public BasicScheduleGroup2(String uuid) {
		mUuid = uuid;
	}

	public BasicScheduleGroup2() {

	}
	
	public boolean addSchedule(ScheduleEvent schedule) {
		if (schedule instanceof BasicScheduleEvent2) {
			return mScheduleList.add((BasicScheduleEvent2) schedule);
		}
		return false;
	}

	public BasicScheduleEvent2 getSchedule(int index) {
		return mScheduleList.get(index);
	}
	public int getSize() {
		return mScheduleList.size();
	}
	public boolean removeScheduleEvent(String eventId) {
		Iterator<BasicScheduleEvent2> iterator = mScheduleList.iterator();
		while (iterator.hasNext()) {
			BasicScheduleEvent2 element = iterator.next();
			if (element.getEventId().equals(eventId)) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}
	
	public boolean updateScheduleEvent(ScheduleEvent event) {
		if (event instanceof BasicScheduleEvent2) {
			Iterator<BasicScheduleEvent2> iterator = mScheduleList.iterator();
			while (iterator.hasNext()) {
				BasicScheduleEvent2 element = iterator.next();
				if (element.getEventId().equals(event.getEventId())) {
					iterator.remove();
					mScheduleList.add((BasicScheduleEvent2) event);
					return true;
				}
			}
		}
		return false;
	}
	
	public JSONArray toJsonArray() {
		JSONArray scheduleItems = new JSONArray();
		for (BasicScheduleEvent2 schedule: mScheduleList) {
			scheduleItems.put(schedule.toJsonObject());
		}
		return scheduleItems;	
	}


	@Override
	public ArrayList<String> getEventIds() {
		ArrayList<String> eventIds = new ArrayList<String>();
		for (BasicScheduleEvent2 schedule: mScheduleList) {
			eventIds.add(schedule.getEventId());
		}
		return eventIds;
	}

	@Override
	public ScheduleEvent getEvent(String eventId) {
		ScheduleEvent event = null;
		for (BasicScheduleEvent2 schedule: mScheduleList) {
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
		String jsonData = "";
		try {
			JSONObject scheduleGroupObj = new JSONObject();
			scheduleGroupObj.put(JsonMapKeys.KEY_SCHEDULE_UUID, mUuid);
			
			JSONArray eventArray = new JSONArray();
			for (BasicScheduleEvent2 scheduleEvent : mScheduleList) {
				eventArray.put(scheduleEvent.toJson());
			}
			scheduleGroupObj.put(JsonMapKeys.KEY_EVENTS, eventArray);
			
			JSONObject scheduleInfo = new JSONObject();
			scheduleInfo.put(JsonMapKeys.KEY_SCHEDULE_GROUP, scheduleGroupObj);
			
			jsonData = scheduleInfo.toString(); 
			
		}
		catch (JSONException ex) {
			LogHelper.logD(TAG, "JSONException in getJsonData");
		}
		
		return jsonData;
	}
	
	@Override
	public int getScheduleType() {		
		return SchedulerConstants2.SCHEDULE_TYPE_BASIC;
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
