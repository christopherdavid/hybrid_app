package com.neatorobotics.android.slide.framework.robot.schedule2;


public class ScheduleTimeObject2 {

	private int mHrs;
	private int mMins;
	public ScheduleTimeObject2(int hrs, int min) {
		mMins = min;
		mHrs = hrs; 
	}	

	public ScheduleTimeObject2(String time) {
		int index = time.indexOf(':');
		if (index == -1 ) {
			mHrs = 0;
			mMins = 0;
		} else {
			String hrs = time.substring(0, index);
			String mins = time.substring(index+1);
			mHrs = convertStringToInt(hrs);
			mMins = convertStringToInt(mins);
		}
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
