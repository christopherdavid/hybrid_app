package com.neatorobotics.android.slide.framework.robot.schedule;


public class ScheduleTimeObject {

	int mHrs;
	int mMins;
	public ScheduleTimeObject(int hrs, int min) {
		mMins = min;
		mHrs = hrs; 
	}	

	ScheduleTimeObject(String time) {
		int index = time.indexOf(':');
		String hrs = time.substring(0, index);
		String mins = time.substring(index+1);
		mHrs = convertStringToInt(hrs);
		mMins = convertStringToInt(mins);
	}
	@Override
	public String toString() {
		StringBuilder time = new StringBuilder();
		String mins = convertIntToString(mMins);
		String hrs = convertIntToString(mHrs);
		time.append(hrs);
		time.append(":");
		if (mins.length() == 1) {
			time.append('0');		
		} 
		time.append(mins);
		return time.toString();

	}

	private String convertIntToString(int value) {
		String valueStr = String.valueOf(value);
		return valueStr;
	}
	
	private static int convertStringToInt(String value) {

		int valueInt = Integer.parseInt(value);
		return valueInt;
	}

}
