package com.neatorobotics.android.slide.framework.utils;

public class DataConversionUtils {
	public static String convertIntToString(int value) {
		String valueStr = String.valueOf(value);
		return valueStr;
	}
	public static int convertStringToInt(String value) {

		int valueInt = Integer.parseInt(value);
		return valueInt;
	}

}
