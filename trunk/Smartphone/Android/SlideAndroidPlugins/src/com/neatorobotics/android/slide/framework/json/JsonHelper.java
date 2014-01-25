package com.neatorobotics.android.slide.framework.json;

import java.io.FileInputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class JsonHelper {
	private static final String TAG = JsonHelper.class.getSimpleName();
	
	private static ObjectMapper sResultMapper = new ObjectMapper();

    static {
    	sResultMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    	sResultMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	sResultMapper.setSerializationInclusion(Include.NON_NULL);
    }
	
	public static JSONObject createJsonFromFile(String filePath)
	{
		JSONObject obj = null;
		try {
			FileInputStream is  = new FileInputStream(filePath);
			StringBuilder sb = new StringBuilder();
			if (is != null) {
				byte [] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) > 0) {
					String s = new String(buffer, 0, len);
					sb.append(s);
				}
				is.close();

				try {
					LogHelper.logD(TAG, "File contents:" + sb.toString());
					obj = new JSONObject(sb.toString());
					LogHelper.log(TAG, "JSON Object parsed. JSON value = " + obj.toString());
					return obj;
				} catch (JSONException e) {
					LogHelper.log(TAG, "Exception in parsing JSON file", e);
				}
			}
		}
		catch (IOException e) {
			LogHelper.log(TAG, "Exception opening file from asset", e);
		}
		return obj;

	}
	
	public static ObjectMapper getObjectMapper() {
		return sResultMapper;
	}
	
	public static <T> T objectFromJson(String response, Class<T> tClass) {
		try {
			T object = sResultMapper.readValue(response, tClass);
			
			return object;
		} catch (Exception e) {
			LogHelper.log(TAG, "Unable to parse the response into the given object" + e.getMessage());
		}
		return null;
	}
}
