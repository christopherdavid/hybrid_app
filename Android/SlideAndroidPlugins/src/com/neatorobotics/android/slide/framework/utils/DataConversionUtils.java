package com.neatorobotics.android.slide.framework.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    // Public helper method to convert from JSONObject to HashMap
    // We are assuming both keys and values are in string format
    public static HashMap<String, String> jsonObjectToHashMap(JSONObject jObject) {

        HashMap<String, String> jMap = new HashMap<String, String>();
        if (jObject == null) {
            LogHelper.log(TAG, "Json object is empty");
            return jMap;
        }
        Iterator<?> keys = jObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            try {
                jMap.put(key, jObject.getString(key));
            } catch (JSONException e) {
                LogHelper.log(TAG, "exception in jsonObjectToHashMap", e);
            }
        }
        return jMap;
    }

    // Public helper method to convert from Map to JSONObject
    // We are assuming both keys and values are in string format
    public static JSONObject mapToJsonObject(Map<String, String> hash) {
        JSONObject jObject = null;
        if (hash == null) {
            jObject = new JSONObject();
            return jObject;
        }
        jObject = new JSONObject(hash);
        return jObject;
    }

    public static JSONArray toJsonArray(ArrayList<String> arrayList) {
        JSONArray array = new JSONArray();
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                array.put(arrayList.get(i));
            }
        }
        return array;
    }
}
