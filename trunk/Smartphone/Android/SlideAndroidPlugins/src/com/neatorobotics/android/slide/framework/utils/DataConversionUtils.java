package com.neatorobotics.android.slide.framework.utils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class DataConversionUtils {
	
	private static final String TAG = DataConversionUtils.class.getSimpleName();
	public static String convertIntToString(int value) {
		String valueStr = String.valueOf(value);
		return valueStr;
	}
	public static String convertHexToString(int value) {
		String valueStr = String.format("0x%x", value);
		return valueStr;
	}
	public static int convertStringToInt(String value) {
		// If received string is hex.
		if (value.startsWith("0x")) {
			String hexString = value.substring(2);
			try {
				long valueLong = Long.valueOf(hexString, 16);
				return (int) valueLong;
			} catch (NumberFormatException e) {
				LogHelper.log(TAG, "Exception in convertStringToInt", e);
				return 0;
			}
		} else {
			int valueInt = Integer.parseInt(value);
			return valueInt;
		}
	}
	
	public static String convertHexIntToString(int value) {
		String valueStr = String.valueOf(value);
		return valueStr;
	}
}
