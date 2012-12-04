package com.neatorobotics.android.slide.framework.robot.schedule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants.Day;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants.SchedularEvent;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;

public class ScheduleXmlHelper {
	public static final String TAG = ScheduleXmlHelper.class.getSimpleName();
	
	private static class AdvancedScheduleXmlDataHandler extends DefaultHandler
	{

		private HashMap<String, String> mElementValueMap = new HashMap<String, String>();
		private AdvancedScheduleGroup scheduleGroup = new AdvancedScheduleGroup();
		private AdvancedRobotSchedule currentSchedule;
		private ScheduleTimeObject startTime;
		private ScheduleTimeObject endTime;
		
		private LinkedList<String> mXmlNodes = new LinkedList<String>();
		@Override 
		public void startDocument() throws SAXException {
			LogHelper.logD(TAG, "Document Xml started Event");
			currentSchedule = new AdvancedRobotSchedule();
		} 

		@Override 
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException { 
			LogHelper.logD(TAG, "StartElement: "+qName);
			mXmlNodes.addFirst(qName);
			if (qName.equals(SchedulerConstants.XML_TAG_SCHEDULE)) {
				// New schedule data is being followed
				currentSchedule = new AdvancedRobotSchedule();
			}
		} 


		@Override 
		public void endElement(String namespaceURI, String localName, String qName) throws SAXException { 
			mXmlNodes.remove(qName);
			LogHelper.log(TAG, "EndElement: "+qName);
			if(qName.equals(SchedulerConstants.XML_TAG_SCHEDULE)) {
				if (currentSchedule != null) {
					scheduleGroup.addSchedule(currentSchedule);
					currentSchedule = null;
				}
			}
		}

		@Override 
		public void characters(char ch[], int start, int length) { 
			String chars = new String(ch, start, length); 
			chars = chars.trim(); 
			LogHelper.log(TAG, "Chars: "+chars);
			String nodeName = mXmlNodes.getFirst();

			if (currentSchedule != null) {
				//Details related to a specific command are here.
				if (nodeName.equals(SchedulerConstants.XML_TAG_DAY)) {
					int day = DataConversionUtils.convertStringToInt(chars);
					Day weekDay = SchedulerConstants.detrmineDay(day);
					currentSchedule.addDay(weekDay);
				} else if (nodeName.equals(SchedulerConstants.XML_TAG_STARTTIME)) {
					startTime = new ScheduleTimeObject(chars);
					currentSchedule.setStartScheduleTime(startTime);
				} else if (nodeName.equals(SchedulerConstants.XML_TAG_ENDTIME)) {
					endTime = new ScheduleTimeObject(chars);
					currentSchedule.setEndScheduleTime(endTime);
				} else if (nodeName.equals(SchedulerConstants.XML_TAG_AREA)) {
					String area = chars;
					currentSchedule.setArea(area);
				} else if (nodeName.equals(SchedulerConstants.XML_TAG_EVENTTYPE)) {
					int event = DataConversionUtils.convertStringToInt(chars);
					SchedularEvent eventType = SchedulerConstants.detrmineEvent(event);
					currentSchedule.setEventType(eventType);
				}	
			}
			mElementValueMap.put(nodeName, chars);
		} 
		@Override
		public void endDocument() {
			LogHelper.log(TAG, "Document End:");
		}
		public AdvancedScheduleGroup getScheduledGroup() {
			return scheduleGroup;
		}

	}
	public static AdvancedScheduleGroup readInputStreamXml(InputStream in) {
		AdvancedScheduleGroup scheduleGroup = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			LogHelper.logD(TAG, "Exception in readInputStreamXml ", e);

		} catch (SAXException e) {
			LogHelper.logD(TAG, "Exception in readInputStreamXml ", e);

		}
		AdvancedScheduleXmlDataHandler handler = new AdvancedScheduleXmlDataHandler();

		try {
			InputSource is = new InputSource(in);
			is.setEncoding("UTF-8");
			saxParser.parse(is, handler);
			scheduleGroup = handler.getScheduledGroup();
		} catch (SAXException e) {
			LogHelper.logD(TAG, "Exception in readInputStreamXml ", e);
		} catch (IOException e) {
			LogHelper.logD(TAG, "Exception in readInputStreamXml ", e);
		}
		return scheduleGroup;

	}
	public static AdvancedScheduleGroup readFileXml(String filePath) {
		AdvancedScheduleGroup scheduleGroup = null;
		InputStream in = null;
		try {
			in = new FileInputStream(filePath);
		} catch (FileNotFoundException e1) {
			LogHelper.log(TAG, "Exception readFileXml", e1);
		}  

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			LogHelper.logD(TAG, "Exception in readFileXml ", e);

		} catch (SAXException e) {
			LogHelper.logD(TAG, "Exception in readFileXml ", e);

		}
		AdvancedScheduleXmlDataHandler handler = new AdvancedScheduleXmlDataHandler();

		try {
			InputSource is = new InputSource(in);
			is.setEncoding("UTF-8");
			saxParser.parse(is, handler);
			scheduleGroup = handler.getScheduledGroup();
		} catch (SAXException e) {
			LogHelper.logD(TAG, "Exception in readFileXml ", e);
		} catch (IOException e) {
			LogHelper.logD(TAG, "Exception in readFileXml ", e);
		}
		return scheduleGroup;
	}

	

}
