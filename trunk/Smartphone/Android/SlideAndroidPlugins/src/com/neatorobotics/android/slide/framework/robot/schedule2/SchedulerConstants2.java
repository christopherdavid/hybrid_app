package com.neatorobotics.android.slide.framework.robot.schedule2;

import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes;

import android.os.Environment;




public class SchedulerConstants2 {
	//XML tags

	public static final String XML_TAG_SCHEDULES = "ScheduleGroup";
	public static final String XML_TAG_SCHEDULE = "Schedule";
	public static final String XML_TAG_SCHEDULE_EVENT_ID = "ScheduleEventId";
	public static final String XML_TAG_SCHEDULE_UUID = "ScheduleUUID";
	public static final String XML_TAG_DAY = "Day";
	public static final String XML_TAG_AREA = "Area";
	public static final String XML_TAG_EVENTTYPE= "EventType";

	public static final String XML_TAG_STARTTIME = "StartTime";
	public static final String XML_TAG_ENDTIME = "EndTime";
	public static final String XML_TAG_CLEANING_MODE = "CleaningMode";
	public static final int SCHEDULE_TYPE_BASIC = 0;
	public static final int SCHEDULE_TYPE_ADVANCED = 1;
	public static final int CLEANING_MODE_ECO = 1;
	public static final int CLEANING_MODE_NORMAL = 2;	

	public static enum Day {SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};
	public static enum SchedularEvent {QUIET, CLEAN, NONE};	

	public static final String DEFAULT_XML_STORE_FILE = Environment.getExternalStorageDirectory().getAbsolutePath()  +  "/neato/schedule_data/schedule/data.xml";
	
	public static Day detrmineDay(int day) {

		if(day == Day.SUNDAY.ordinal())
			return Day.SUNDAY;
		if(day == Day.MONDAY.ordinal())
			return Day.MONDAY;
		if(day == Day.TUESDAY.ordinal())
			return Day.TUESDAY;
		if(day == Day.WEDNESDAY.ordinal())
			return Day.WEDNESDAY;
		if(day == Day.THURSDAY.ordinal())
			return Day.THURSDAY;
		if(day == Day.FRIDAY.ordinal())
			return Day.FRIDAY;
		if(day == Day.SATURDAY.ordinal())
			return Day.SATURDAY;
		
		return null;
	}
	
	public static SchedularEvent detrmineEvent(int eventType) {
		// TODO Auto-generated method stub
		if(eventType == SchedularEvent.QUIET.ordinal())
			return SchedularEvent.QUIET;
		if(eventType == SchedularEvent.CLEAN.ordinal())
			return SchedularEvent.CLEAN;
		return null;

	}
	
	public static String getScheduleType(int scheduleVal) {
		String scheduleType = null;
		if (scheduleVal == SchedulerConstants2.SCHEDULE_TYPE_BASIC) {
			scheduleType = NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_BASIC;
		} else if (scheduleVal == SchedulerConstants2.SCHEDULE_TYPE_ADVANCED) {
			scheduleType = NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_ADVANCED;
		} 
		return scheduleType;
	}	
	
	public static int getScheduleIntType(String scheduleType) {
		int scheduleVal = -1;
		if (scheduleType.equals(NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_BASIC)) {
			scheduleVal = SchedulerConstants2.SCHEDULE_TYPE_BASIC;
		} else if (scheduleType.equals(NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_ADVANCED)) {
			scheduleVal = SchedulerConstants2.SCHEDULE_TYPE_ADVANCED;
		} 
		return scheduleVal;
	}
	
	public static ScheduleGroup2 getEmptySchedule(int type) {
		ScheduleGroup2 group = null;
		String uuid = AppUtils.generateScheduleUUId();
		if (type == SCHEDULE_TYPE_BASIC) {
			group = new BasicScheduleGroup2(uuid);
		} else if (type == SCHEDULE_TYPE_ADVANCED) {
			group = new AdvancedScheduleGroup2(uuid);
		} 
		return group;
	}
	
	public static ScheduleGroup2 getEmptySchedule(String type) {
		ScheduleGroup2 group = null;
		String uuid = AppUtils.generateScheduleUUId();
		if (type.equals(NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_BASIC)) {
			group = new BasicScheduleGroup2(uuid);
		} else if (type.equals(NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_ADVANCED)) {
			group = new AdvancedScheduleGroup2(uuid);
		} 
		return group;
	}
}
