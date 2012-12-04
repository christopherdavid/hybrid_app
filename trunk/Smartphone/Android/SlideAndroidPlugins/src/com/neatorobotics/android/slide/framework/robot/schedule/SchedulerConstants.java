package com.neatorobotics.android.slide.framework.robot.schedule;

import android.os.Environment;




public class SchedulerConstants {
	//XML tags

	public static final String XML_TAG_SCHEDULES = "ScheduleGroup";
	public static final String XML_TAG_SCHEDULE = "Schedule";
	public static final String XML_TAG_DAY = "Day";
	public static final String XML_TAG_AREA = "Area";
	public static final String XML_TAG_EVENTTYPE= "EventType";

	public static final String XML_TAG_STARTTIME = "StartTime";
	public static final String XML_TAG_ENDTIME = "EndTime";

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
	
	public static String dayToString(Day day) {
		switch(day) {
		case SUNDAY: 
			return "Sunday";
		case MONDAY: 
			return "Monday";
		case TUESDAY: 
			return "Tuesday";
		case WEDNESDAY: 
			return "Wednesday";
		case THURSDAY:
			return "Thursday";
		case FRIDAY: 
			return "Friday";
		case SATURDAY: 
			return "Saturday";
		}
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
	
	public static String eventToString(SchedularEvent mEvent) {
		// TODO Auto-generated method stub
		switch(mEvent) {
		case QUIET: 
			return "Quiet";
		case CLEAN: 
			return "Clean";
		case NONE:
			return "-";
		}
		return null;
	}
}
