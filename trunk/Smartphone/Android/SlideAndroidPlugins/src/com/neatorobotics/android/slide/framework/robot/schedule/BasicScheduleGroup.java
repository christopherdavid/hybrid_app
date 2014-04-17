package com.neatorobotics.android.slide.framework.robot.schedule;

import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;

public class BasicScheduleGroup implements Schedules {
    private static final String TAG = BasicScheduleGroup.class.getSimpleName();

    private String mUuid;
    private ArrayList<BasicScheduleEvent> mScheduleList = new ArrayList<BasicScheduleEvent>();

    public BasicScheduleGroup(String uuid) {
        mUuid = uuid;
    }

    public BasicScheduleGroup() {

    }

    public boolean addSchedule(ScheduleEvent schedule) {
        if (schedule instanceof BasicScheduleEvent) {
            return mScheduleList.add((BasicScheduleEvent) schedule);
        }
        return false;
    }

    public BasicScheduleEvent getSchedule(int index) {
        return mScheduleList.get(index);
    }

    public int getSize() {
        return mScheduleList.size();
    }

    public boolean removeScheduleEvent(String eventId) {
        Iterator<BasicScheduleEvent> iterator = mScheduleList.iterator();
        while (iterator.hasNext()) {
            BasicScheduleEvent element = iterator.next();
            if (element.getEventId().equals(eventId)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean updateScheduleEvent(ScheduleEvent event) {
        if (event instanceof BasicScheduleEvent) {
            Iterator<BasicScheduleEvent> iterator = mScheduleList.iterator();
            while (iterator.hasNext()) {
                BasicScheduleEvent element = iterator.next();
                if (element.getEventId().equals(event.getEventId())) {
                    iterator.remove();
                    mScheduleList.add((BasicScheduleEvent) event);
                    return true;
                }
            }
        }
        return false;
    }

    public JSONArray toJsonArray() {
        JSONArray scheduleItems = new JSONArray();
        for (BasicScheduleEvent schedule : mScheduleList) {
            scheduleItems.put(schedule.toJsonObject());
        }
        return scheduleItems;
    }

    @Override
    public ArrayList<String> getEventIds() {
        ArrayList<String> eventIds = new ArrayList<String>();
        for (BasicScheduleEvent schedule : mScheduleList) {
            eventIds.add(schedule.getEventId());
        }
        return eventIds;
    }

    @Override
    public ScheduleEvent getEvent(String eventId) {
        ScheduleEvent event = null;
        for (BasicScheduleEvent schedule : mScheduleList) {
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
            for (BasicScheduleEvent scheduleEvent : mScheduleList) {
                eventArray.put(scheduleEvent.toJson());
            }
            scheduleGroupObj.put(JsonMapKeys.KEY_EVENTS, eventArray);

            JSONObject scheduleInfo = new JSONObject();
            scheduleInfo.put(JsonMapKeys.KEY_SCHEDULE_GROUP, scheduleGroupObj);

            jsonData = scheduleInfo.toString();

        } catch (JSONException ex) {
            LogHelper.logD(TAG, "JSONException in getJsonData");
        }

        return jsonData;
    }

    @Override
    public int getScheduleType() {
        return SchedulerConstants.SCHEDULE_TYPE_BASIC;
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
