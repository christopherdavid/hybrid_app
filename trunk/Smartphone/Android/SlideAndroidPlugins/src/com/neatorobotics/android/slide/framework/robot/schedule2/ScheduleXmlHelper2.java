package com.neatorobotics.android.slide.framework.robot.schedule2;

import java.io.ByteArrayInputStream;
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
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2.Day;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2.SchedularEvent;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes;

public class ScheduleXmlHelper2 {
	
	public static final String TAG = ScheduleXmlHelper2.class.getSimpleName();
	
	private static class AdvancedScheduleXmlDataHandler extends DefaultHandler
	{
		private HashMap<String, String> mElementValueMap = new HashMap<String, String>();
		private AdvancedScheduleGroup2 mScheduleGroup = new AdvancedScheduleGroup2();
		private AdvancedScheduleEvent2 mCurrentSchedule;
		private ScheduleTimeObject2 mStartTime;
		private ScheduleTimeObject2 mEndTime;
		
		private LinkedList<String> mXmlNodes = new LinkedList<String>();
		@Override 
		public void startDocument() throws SAXException {
			LogHelper.logD(TAG, "Document Xml started Event");
		} 

		@Override 
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException { 
			LogHelper.logD(TAG, "StartElement: " + qName);
			mXmlNodes.addFirst(qName);
		} 


		@Override 
		public void endElement(String namespaceURI, String localName, String qName) throws SAXException { 
			mXmlNodes.remove(qName);
			LogHelper.log(TAG, "EndElement: " + qName);
			if(qName.equals(SchedulerConstants2.XML_TAG_SCHEDULE)) {
				if (mCurrentSchedule != null) {
					mScheduleGroup.addSchedule(mCurrentSchedule);
					mCurrentSchedule = null;
				}
			}
		}

		@Override 
		public void characters(char ch[], int start, int length) { 
			String chars = new String(ch, start, length); 
			chars = chars.trim(); 
			LogHelper.log(TAG, "Chars: " + chars);
			String nodeName = mXmlNodes.getFirst();
			if (nodeName.equals(SchedulerConstants2.XML_TAG_SCHEDULE_EVENT_ID)) {
				mCurrentSchedule = new AdvancedScheduleEvent2(chars);
			}
			if (nodeName.equals(SchedulerConstants2.XML_TAG_SCHEDULE_UUID)) {
				mScheduleGroup.setUUID(chars);
			}
			if (mCurrentSchedule != null) {
				//Details related to a specific command are here.
				if (nodeName.equals(SchedulerConstants2.XML_TAG_DAY)) {
					int day = DataConversionUtils.convertStringToInt(chars);
					Day weekDay = SchedulerConstants2.detrmineDay(day);
					mCurrentSchedule.setDay(weekDay);
				} else if (nodeName.equals(SchedulerConstants2.XML_TAG_STARTTIME)) {
					mStartTime = new ScheduleTimeObject2(chars);
					mCurrentSchedule.setStartScheduleTime(mStartTime);
				} else if (nodeName.equals(SchedulerConstants2.XML_TAG_ENDTIME)) {
					mEndTime = new ScheduleTimeObject2(chars);
					mCurrentSchedule.setEndScheduleTime(mEndTime);
				} else if (nodeName.equals(SchedulerConstants2.XML_TAG_AREA)) {
					String area = chars;
					mCurrentSchedule.setArea(area);
				} else if (nodeName.equals(SchedulerConstants2.XML_TAG_EVENTTYPE)) {
					int event = DataConversionUtils.convertStringToInt(chars);
					SchedularEvent eventType = SchedulerConstants2.detrmineEvent(event);
					mCurrentSchedule.setEventType(eventType);
				}	
			}
			mElementValueMap.put(nodeName, chars);
		} 
		@Override
		public void endDocument() {
			LogHelper.log(TAG, "Document End:");
		}
		public AdvancedScheduleGroup2 getScheduledGroup() {
			return mScheduleGroup;
		}
	}
	
	private static class BasicScheduleXmlDataHandler extends DefaultHandler
	{

		private HashMap<String, String> mElementValueMap = new HashMap<String, String>();
		private BasicScheduleGroup2 mScheduleGroup = new BasicScheduleGroup2();
		private BasicScheduleEvent2 mCurrentSchedule;
		private ScheduleTimeObject2 mStartTime;
		
		private LinkedList<String> mXmlNodes = new LinkedList<String>();
		@Override 
		public void startDocument() throws SAXException {
			LogHelper.logD(TAG, "Document Xml started Event");
		} 

		@Override 
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException { 
			LogHelper.logD(TAG, "StartElement: "+qName);
			mXmlNodes.addFirst(qName);
		} 

		@Override 
		public void endElement(String namespaceURI, String localName, String qName) throws SAXException { 
			mXmlNodes.remove(qName);
			LogHelper.log(TAG, "EndElement: "+qName);
			if(qName.equals(SchedulerConstants2.XML_TAG_SCHEDULE)) {
				if (mCurrentSchedule != null) {
					mScheduleGroup.addSchedule(mCurrentSchedule);
					mCurrentSchedule = null;
				}
			}
		}

		@Override 
		public void characters(char ch[], int start, int length) { 
			String chars = new String(ch, start, length); 
			chars = chars.trim(); 
			LogHelper.log(TAG, "Chars: "+chars);
			String nodeName = mXmlNodes.getFirst();
			if (nodeName.equals(SchedulerConstants2.XML_TAG_SCHEDULE_EVENT_ID)) {
				mCurrentSchedule = new BasicScheduleEvent2(chars);
			}
			if (nodeName.equals(SchedulerConstants2.XML_TAG_SCHEDULE_UUID)) {
				mScheduleGroup.setUUID(chars);
			}
			if (mCurrentSchedule != null) {
				//Details related to a specific command are here.
				if (nodeName.equals(SchedulerConstants2.XML_TAG_DAY)) {
					int day = DataConversionUtils.convertStringToInt(chars);
					Day weekDay = SchedulerConstants2.detrmineDay(day);
					mCurrentSchedule.setDay(weekDay);
				} else if (nodeName.equals(SchedulerConstants2.XML_TAG_STARTTIME)) {
					mStartTime = new ScheduleTimeObject2(chars);
					mCurrentSchedule.setStartScheduleTime(mStartTime);
				}
				else if (nodeName.equals(SchedulerConstants2.XML_TAG_CLEANING_MODE)) {
					mCurrentSchedule.setCleaningMode(chars);
				}
			}
			mElementValueMap.put(nodeName, chars);
		} 
		@Override
		public void endDocument() {
			LogHelper.log(TAG, "Document End:");
		}
		public BasicScheduleGroup2 getScheduledGroup() {
			return mScheduleGroup;
		}

	}

	private static AdvancedScheduleGroup2 readInputStreamXmlToAdvancedSchedule(InputStream in) {
		AdvancedScheduleGroup2 scheduleGroup = null;
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
	
	private static BasicScheduleGroup2 readInputStreamXmlToBasicSchedule(InputStream in) {
		BasicScheduleGroup2 scheduleGroup = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			LogHelper.logD(TAG, "Exception in readInputStreamXml ", e);

		} catch (SAXException e) {
			LogHelper.logD(TAG, "Exception in readInputStreamXml ", e);

		}
		BasicScheduleXmlDataHandler handler = new BasicScheduleXmlDataHandler();

		try {
			InputSource is = new InputSource(in);
			is.setEncoding("UTF-8");
			saxParser.parse(is, handler);
			scheduleGroup = handler.getScheduledGroup();
		} catch (SAXException e) {
			LogHelper.log(TAG, "Exception in readInputStreamXml ", e);
		} catch (IOException e) {
			LogHelper.log(TAG, "Exception in readInputStreamXml ", e);
		}
		return scheduleGroup;

	}
	
	private static AdvancedScheduleGroup2 readFileXmlToAdvancedSchedule(String filePath) {
		AdvancedScheduleGroup2 scheduleGroup = null;
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
			LogHelper.log(TAG, "Exception in readFileXml ", e);
		} catch (IOException e) {
			LogHelper.log(TAG, "Exception in readFileXml ", e);
		}
		return scheduleGroup;
	}
	
	private static BasicScheduleGroup2 readFileXmlToBasicSchedule(String filePath) {
		BasicScheduleGroup2 scheduleGroup = null;
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
			LogHelper.log(TAG, "Exception in readFileXml ", e);

		} catch (SAXException e) {
			LogHelper.log(TAG, "Exception in readFileXml ", e);

		}
		BasicScheduleXmlDataHandler handler = new BasicScheduleXmlDataHandler();

		try {
			InputSource is = new InputSource(in);
			is.setEncoding("UTF-8");
			saxParser.parse(is, handler);
			scheduleGroup = handler.getScheduledGroup();
		} catch (SAXException e) {
			LogHelper.log(TAG, "Exception in readFileXml ", e);
		} catch (IOException e) {
			LogHelper.log(TAG, "Exception in readFileXml ", e);
		}
		return scheduleGroup;
	}


	public static ScheduleGroup2 readFileXml(String filePath, String scheduleType) {
		if (scheduleType.equals(NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_ADVANCED)) {
			return readFileXmlToAdvancedSchedule(filePath);
		} else if (scheduleType.equals(NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_BASIC)) {
			return readFileXmlToBasicSchedule(filePath);
		} 
		return null;
	}

	public static ScheduleGroup2 readXmlString(String scheduleType, String xmlData) {
		
		InputStream is = new ByteArrayInputStream(xmlData.getBytes());
		if (scheduleType.equals(NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_ADVANCED)) {
			return readInputStreamXmlToAdvancedSchedule(is);
		} else if (scheduleType.equals(NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_BASIC)) {
			return readInputStreamXmlToBasicSchedule(is);
		} 
		
		return null;
	}
}
