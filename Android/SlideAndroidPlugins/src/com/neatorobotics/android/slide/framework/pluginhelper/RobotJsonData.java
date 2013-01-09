package com.neatorobotics.android.slide.framework.pluginhelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;

// Pretty much similar to User data. But this can change with time when complex commands are included.
public class RobotJsonData {


	private static final String TAG = RobotJsonData.class.getSimpleName();

	private JSONArray mData;
	private JSONObject mDataObject;

	public RobotJsonData(JSONArray data) {
		mData = data;
		extractJsonObject();
	}

	public JSONArray getJsonArray(String key) {
		JSONArray jsonArray = null;
		try {
			jsonArray = mDataObject.getJSONArray(key);
		} catch (JSONException e) {
			LogHelper.log(TAG, "JSON value doesn't exist for key :"+key);
		}
		return jsonArray;

	}

	public JSONObject getJsonObject(String key) {

		JSONObject jsonobj = null;
		try {
			jsonobj = mDataObject.getJSONObject(key);
		} catch (JSONException e) {
			LogHelper.log(TAG, "JSON value doesn't exist for key :"+key);
		}
		return jsonobj;
	}

	private void extractJsonObject() {
		try {
			mDataObject = mData.getJSONObject(0);
		} catch (JSONException e) {
			LogHelper.log(TAG, "JSON object is not present");
		}
	}


	public String getString(String key) {
		return getString(key, "");
	}
	public String getString(String key, String DefaultStr) {
		String value = DefaultStr;
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

	@Override
	public String toString()
	{
		if (mDataObject != null) {
			return mDataObject.toString();
		}
		return "";
	}
}
