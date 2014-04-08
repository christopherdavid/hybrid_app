package com.neatorobotics.android.slide.framework.json;

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
