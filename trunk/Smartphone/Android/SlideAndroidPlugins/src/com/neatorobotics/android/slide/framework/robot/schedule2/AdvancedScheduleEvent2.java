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
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2.SchedularEvent;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;
import com.neatorobotics.android.slide.framework.xml.XmlHelper;

public class AdvancedScheduleEvent2 implements Schedule2 {

	private static final String TAG = AdvancedScheduleEvent2.class.getSimpleName();
	private String mEventId;
	private Day mDay;
	private ScheduleTimeObject2 mStartTime;
	private ScheduleTimeObject2 mEndTime;
	private String mArea;
	private SchedularEvent mEvent;

	public AdvancedScheduleEvent2(String eventId) {
		mEventId = eventId;
	}
	
	public AdvancedScheduleEvent2(String eventId, Day day, ScheduleTimeObject2 startTime, ScheduleTimeObject2 endTime, String area, SchedularEvent event) {
		mEventId = eventId;
		mDay = day;
		mStartTime = startTime;
		mEndTime = endTime;
		mArea = area;
		mEvent = event;
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

		Node endTimeNode = doc.createElement(SchedulerConstants2.XML_TAG_ENDTIME);
		String endTime = mEndTime.toString();
		endTimeNode.appendChild(doc.createTextNode(endTime));

		Node eventIdNode = doc.createElement(SchedulerConstants2.XML_TAG_EVENTTYPE);
		String event = DataConversionUtils.convertIntToString(mEvent.ordinal());
		eventIdNode.appendChild(doc.createTextNode(event));

		Node areaNode = doc.createElement(SchedulerConstants2.XML_TAG_AREA);
		areaNode.appendChild(doc.createTextNode(mArea));

		schedule.appendChild(startTimeNode);
		schedule.appendChild(endTimeNode);
		schedule.appendChild(eventIdNode);
		schedule.appendChild(areaNode);

		return schedule;
	}

	public JSONObject toJsonObject() {
		JSONObject schedule = new JSONObject();		
		try {
			//Put day 
			schedule.put(JsonMapKeys.KEY_DAY, mDay);

			//Put start-time and end-time HH:MM
			schedule.put(JsonMapKeys.KEY_START_TIME, mStartTime.toString());
			schedule.put(JsonMapKeys.KEY_END_TIME,mEndTime.toString());

			//Put event type. 0 for Quiet and 1 for Clean
			schedule.put(JsonMapKeys.KEY_EVENT_TYPE, mEvent.ordinal());

			//Put Area String
			schedule.put(JsonMapKeys.KEY_AREA, mArea);
		} 
		catch (JSONException e) {
			LogHelper.log(TAG, "Exception in toJsonObject", e);
		}
		return schedule;
	}

	public String getXml() {
		return XmlHelper.NodeToXmlString(this.toXmlNode());
	}
	
	// TODO: As of now there is no Blob data associated with the
	// schedule but server exposes the blob data file. So for now
	// we really don't need to the Blob data file path and most likely
	// we won't need it in future also. But keeping it around for sometime
	public String getBlobData() {
		return "";
	}
	
	public void setDay(Day day) {
		mDay = day;
	}
	
	public void setEventType(SchedularEvent event) {
		mEvent = event;
	}

	public void setStartScheduleTime(ScheduleTimeObject2 time) {
		mStartTime = time;
	}

	public void setEndScheduleTime(ScheduleTimeObject2 time) {
		mEndTime = time;
	}
	public void setArea(String area) {
		mArea = area;		
	}

	public String getStartScheduleTimeStr() {
		return mStartTime.toString();
	}

	public String getEndScheduleTimeStr() {
		return mEndTime.toString();
	}
	
	public String getScheduleAreaStr() {
		return mArea;
	}


	public String getEventId() {
		return mEventId;
	}
}
