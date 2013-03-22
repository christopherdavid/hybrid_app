package com.neatorobotics.android.slide.framework.robot.schedule2;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.neatorobotics.android.slide.framework.xml.XmlHelper;

public class BasicScheduleGroup2 implements ScheduleGroup2 {
	private String mUuid;
	private ArrayList<BasicScheduleEvent2> mScheduleList = new ArrayList<BasicScheduleEvent2>();

	public BasicScheduleGroup2(String uuid) {
		mUuid = uuid;
	}

	public BasicScheduleGroup2() {

	}
	
	public boolean addSchedule(Schedule2 schedule) {
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
	
	public boolean updateScheduleEvent(Schedule2 event) {
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
	
	public Node toXmlNode() {

		int groupSize = getSize();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

		}
		Document doc = docBuilder.newDocument();
		Node schedules = doc.createElement(SchedulerConstants2.XML_TAG_SCHEDULES);
		Node scheduleUUID = doc.createElement(SchedulerConstants2.XML_TAG_SCHEDULE_UUID);
		scheduleUUID.appendChild(doc.createTextNode(mUuid));
		schedules.appendChild(scheduleUUID);
		for (int scheduleIterator =0; scheduleIterator< groupSize; scheduleIterator++){
			Node scheduleNode = getSchedule(scheduleIterator).toXmlNode();
			Node addScheduleNode = doc.adoptNode(scheduleNode);
			schedules.appendChild(addScheduleNode);
		}
		return schedules;

	}

	public JSONArray toJsonArray() {
		JSONArray scheduleItems = new JSONArray();
		for (BasicScheduleEvent2 schedule: mScheduleList) {
			scheduleItems.put(schedule.toJsonObject());
		}
		return scheduleItems;	
	}

	public String getXml() {
		return XmlHelper.NodeToXmlString(this.toXmlNode());
	}

	public String getBlobData() {
		return "";
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
	public Schedule2 getEvent(String eventId) {
		Schedule2 event = null;
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
}
