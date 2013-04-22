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
	private String mCleaningMode;

	public BasicScheduleEvent2(String eventId) {	
		mEventId = eventId;
		mCleaningMode = String.valueOf(SchedulerConstants2.CLEANING_MODE_NORMAL);
	}
	
	public BasicScheduleEvent2(String eventId, Day day, ScheduleTimeObject2 startTime, 
			String cleaningMode) {
		mEventId = eventId;
		mDay = day;
		mStartTime = startTime;
		mCleaningMode = cleaningMode;
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
		// Add cleaning mode
		Node cleaningModeNode = doc.createElement(SchedulerConstants2.XML_TAG_CLEANING_MODE);
		cleaningModeNode.appendChild(doc.createTextNode(mCleaningMode));
		schedule.appendChild(cleaningModeNode);

		return schedule;
	}

	public JSONObject toJsonObject() {
		JSONObject schedule = new JSONObject();		
		try {
			//Put day array
			schedule.put(JsonMapKeys.KEY_DAY, mDay.ordinal());
			//Put start-time HH:MM
			schedule.put(JsonMapKeys.KEY_START_TIME, mStartTime.toString());
			// Put cleaning mode			
			schedule.put(JsonMapKeys.KEY_CLEANING_MODE, mCleaningMode);
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

	public String getCleaningMode() {
		return mCleaningMode;
	}

	public void setCleaningMode(String cleaningMode) {
		mCleaningMode = cleaningMode;
	}
}
