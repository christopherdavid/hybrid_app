package com.neatorobotics.android.slide.framework.robot.schedule2;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2.Day;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;
import com.neatorobotics.android.slide.framework.xml.XmlHelper;

public class BasicScheduleEvent2 implements Schedule2 {
	private static final String TAG = AdvancedScheduleEvent2.class.getSimpleName();
	private String mEventId;
	private Day mDay;
	private ScheduleTimeObject2 mStartTime;

	public BasicScheduleEvent2(String eventId) {	
		mEventId = eventId;
	}
	
	public BasicScheduleEvent2(String eventId, Day day, ScheduleTimeObject2 startTime) {
		mEventId = eventId;
		mDay = day;
		mStartTime = startTime;
	}


	public Node toXmlNode() {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

		}
		Document doc = docBuilder.newDocument();

		Node schedule = doc.createElement(SchedulerConstants2.XML_TAG_SCHEDULE);
		Node scheduleId = doc.createElement(SchedulerConstants2.XML_TAG_SCHEDULE_EVENT_ID);
		scheduleId.appendChild(doc.createTextNode(mEventId));
		schedule.appendChild(scheduleId);
		Node startDayNode = doc.createElement(SchedulerConstants2.XML_TAG_DAY);
		String day = DataConversionUtils.convertIntToString(mDay.ordinal());
		startDayNode.appendChild(doc.createTextNode(day));
		schedule.appendChild(startDayNode);
		Node startTimeNode = doc.createElement(SchedulerConstants2.XML_TAG_STARTTIME);
		String startTime = mStartTime.toString();
		startTimeNode.appendChild(doc.createTextNode(startTime));
		schedule.appendChild(startTimeNode);

		return schedule;
	}

	public JSONObject toJsonObject() {
		JSONObject schedule = new JSONObject();		
		try {
			//Put day array
			schedule.put(JsonMapKeys.KEY_DAY, mDay.ordinal());
			//Put start-time HH:MM
			schedule.put(JsonMapKeys.KEY_START_TIME, mStartTime.toString());
		} 
		catch (JSONException e) {
			LogHelper.log(TAG, "Exception in toJsonObject", e);
		}
		return schedule;
	}

	public String getXml() {
		return XmlHelper.NodeToXmlString(this.toXmlNode());
	}
	
	public String getBlobData() {
		return "";
	}
	public void setDay(Day day) {
		mDay = day;
	}
	public void setStartScheduleTime(ScheduleTimeObject2 time) {
		mStartTime = time;
	}


	public String getStartScheduleTimeStr() {
		return mStartTime.toString();
	}

	public String getEventId() {
		return mEventId;		
	}
}
