package com.neatorobotics.android.slide.framework.pluginhelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;


//TODO: Get the design for incoming data for user manager json array which will be sent by javascript.
//		Right now Assumed that the data will be sent in a (key,value) pair in a JSONObject which will be the 1st element of the 
//  	received JSON Array. 

public class UserJsonData {

	private static final String TAG = UserJsonData.class.getSimpleName();

	private JSONArray mdata;
	private JSONObject mDataObject;

	public UserJsonData(JSONArray data) {
		mdata = data;
		extractJsonObject();
	}


	private void extractJsonObject() {
		try {
			mDataObject = mdata.getJSONObject(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			LogHelper.log(TAG, "JSON object is not present");
		}
	}


	public String getString(String key) {
		String value = "";

		try {
			value = mDataObject.getString(key);
		} catch (JSONException e) {
			LogHelper.log(TAG, "JSON value doesn't exist for key :"+key);
		}
		return value;
	}

	//assume default false
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	public boolean getBoolean(String key, boolean defaultValue) {
		boolean value = defaultValue;
		try {
			value = mDataObject.getBoolean(key);
		} catch(JSONException e) {
			LogHelper.log(TAG, "JSON value doesn't exist for key :"+key);
		}

		return value;
	}
	// Assume default value as 0
	public int getInt(String key) {
		return getInt(key ,0);
	}

	public int getInt(String key, int defaultValue) {
		int i = defaultValue;

		try {
			i = mDataObject.getInt(key);
		} catch (JSONException e) {
			LogHelper.log(TAG, "JSON value doesn't exist for key :"+key);
		}
		return i;
	}


	//TODO : write other functions for get like getJsonArray etc
}
