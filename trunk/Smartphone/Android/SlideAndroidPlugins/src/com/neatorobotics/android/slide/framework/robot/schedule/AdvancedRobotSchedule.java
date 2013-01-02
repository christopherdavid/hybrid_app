package com.neatorobotics.android.slide.framework.robot.schedule;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.sax.StartElementListener;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants.Day;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants.SchedularEvent;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;
import com.neatorobotics.android.slide.framework.xml.XmlHelper;

public class AdvancedRobotSchedule implements Schedule{

	private static final String TAG = AdvancedRobotSchedule.class.getSimpleName();
	ArrayList<Day> mDay;
	ScheduleTimeObject mStartTime;
	ScheduleTimeObject mEndTime;
	String mArea;
	SchedularEvent mEvent;

	public AdvancedRobotSchedule() {
		mDay = new ArrayList<SchedulerConstants.Day>();
	}
	
	public AdvancedRobotSchedule(ArrayList<Day> day, ScheduleTimeObject startTime, ScheduleTimeObject endTime, String area, SchedularEvent event) {
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

		Node schedule = doc.createElement(SchedulerConstants.XML_TAG_SCHEDULE);
		
		for (int dayIterator = 0; dayIterator < mDay.size(); dayIterator++) {
			Node startDayNode = doc.createElement(SchedulerConstants.XML_TAG_DAY);
			//	String day = getScheduleDay(mDay.get(dayIterator));
			//TODO: add this in Scheduler UTILS
			String day = DataConversionUtils.convertIntToString(mDay.get(dayIterator).ordinal());
			startDayNode.appendChild(doc.createTextNode(day));
			schedule.appendChild(startDayNode);
		}
		Node startTimeNode = doc.createElement(SchedulerConstants.XML_TAG_STARTTIME);
		String startTime = mStartTime.toString();
		startTimeNode.appendChild(doc.createTextNode(startTime));

		Node endTimeNode = doc.createElement(SchedulerConstants.XML_TAG_ENDTIME);
		String endTime = mEndTime.toString();
		endTimeNode.appendChild(doc.createTextNode(endTime));

		Node eventIdNode = doc.createElement(SchedulerConstants.XML_TAG_EVENTTYPE);
		String event = DataConversionUtils.convertIntToString(mEvent.ordinal());
		eventIdNode.appendChild(doc.createTextNode(event));

		Node areaNode = doc.createElement(SchedulerConstants.XML_TAG_AREA);
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
			//Put day array
			JSONArray dayArray = new JSONArray();
			for(Day day: mDay) {
				dayArray.put(day.ordinal());
			}
			schedule.put(JsonMapKeys.KEY_DAY, dayArray);

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
	
	//TODO: To be implemented
	public String getBlobData() {
		return "";
	}
	public void setDays(ArrayList<Day> day) {
		mDay = day;
	}
	public void addDay(Day day) {
		mDay.add(day);
	}
	public void setEventType(SchedularEvent event) {
		mEvent = event;
	}

	public void setStartScheduleTime(ScheduleTimeObject time) {
		mStartTime = time;
	}

	public void setEndScheduleTime(ScheduleTimeObject time) {
		mEndTime = time;
	}
	public void setArea(String area) {
		mArea = area;		
	}


	// Get String methods
	public String getScheduleDayStr() {
		StringBuilder builder = new StringBuilder();
		for (int dayIterator=0; dayIterator<mDay.size(); dayIterator++ ) {
			builder.append(SchedulerConstants.dayToString(mDay.get(dayIterator))+ " ");
		}
		return builder.toString();
	}


	public String getScheduleEventTypeStr() {
		return SchedulerConstants.eventToString(mEvent);
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


	@Override
	public String scheduleToString() {

		StringBuilder builder = new StringBuilder();
		builder.append(" Schedule: ");
		for (int dayIterator=0; dayIterator<mDay.size(); dayIterator++ ) {
			builder.append(" Day: "+SchedulerConstants.dayToString(mDay.get(dayIterator)));
		}
		builder.append(" Area:" + mArea);
		builder.append(" Event:" + SchedulerConstants.eventToString(mEvent));
		builder.append(" Start time: "+mStartTime.toString());
		builder.append(" End time: "+mEndTime.toString());
		return builder.toString();
	}

}
