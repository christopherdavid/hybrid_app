package com.neatorobotics.android.slide.framework.robot.schedule;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.neatorobotics.android.slide.framework.xml.XmlHelper;

public class AdvancedScheduleGroup {

	ArrayList<AdvancedRobotSchedule> mScheduleList;
	public AdvancedScheduleGroup() {
		mScheduleList = new ArrayList<AdvancedRobotSchedule>();
	}
	
	public void addSchedule(AdvancedRobotSchedule schedule) {
		mScheduleList.add(schedule);
	}
	
	public AdvancedRobotSchedule getSchedule(int index) {
		return mScheduleList.get(index);
	}
	public int getSize() {
		return mScheduleList.size();
	}
	public void removeSchedule(AdvancedRobotSchedule schedule) {
		//TODO
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
		Node schedules = doc.createElement(SchedulerConstants.XML_TAG_SCHEDULES);
		for (int scheduleIterator =0; scheduleIterator< groupSize; scheduleIterator++){
			Node scheduleNode = getSchedule(scheduleIterator).toXmlNode();
			Node addScheduleNode = doc.adoptNode(scheduleNode);
			schedules.appendChild(addScheduleNode);
		}
		return schedules;
	
	}
	
	public JSONArray toJsonArray() {
		JSONArray scheduleItems = new JSONArray();
		for (AdvancedRobotSchedule schedule: mScheduleList) {
			scheduleItems.put(schedule.toJsonObject());
		}
		return scheduleItems;	
	}
	
	public String getXml() {
		return XmlHelper.NodeToXmlString(this.toXmlNode());
	}

	public String getBlobData() {
		// TODO Auto-generated method stub
		return "";
	}
	
}
