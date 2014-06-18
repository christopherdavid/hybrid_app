package com.neatorobotics.android.slide.framework.webservice.robot.datamanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.json.JsonHelper;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class GetRobotProfileDetailsResult2 extends NeatoWebserviceResult {

    public GetRobotProfileDetailsResult2(NeatoHttpResponse response) {
        super(response);
    }

    public static final int RESPONSE_STATUS_SUCCESS = 0;
    public Result result;

    public GetRobotProfileDetailsResult2() {
        super();
    }

    @Override
    public boolean success() {
        return ((status == RESPONSE_STATUS_SUCCESS) && ((result != null)));
    }

    public static class Result {
        public boolean success;
        public HashMap<String, ProfileKeyDetails> profile_details;

    }

    public static class ProfileKeyDetails {
        public String value;
        public long timestamp;
    }

    public long getProfileParameterTimeStamp(String key) {
        if (isProfileDetailsNotEmpty()) {
            HashMap<String, ProfileKeyDetails> profileDetails = result.profile_details;
            if (profileDetails.containsKey(key)) {
                return profileDetails.get(key).timestamp;
            }
        }
        return 0;
    }

    private boolean isProfileDetailsNotEmpty() {
        return (result != null && result.profile_details != null);
    }

    public boolean contains(String profileKey) {
        if (isProfileDetailsNotEmpty()) {
            HashMap<String, ProfileKeyDetails> profileDetails = result.profile_details;
            if (profileDetails.containsKey(profileKey)) {
                return true;
            }
        }
        return false;
    }

    public String getProfileParameterValue(String key) {
        if (isProfileDetailsNotEmpty()) {
            HashMap<String, ProfileKeyDetails> profileDetails = result.profile_details;
            if (profileDetails.containsKey(key)) {
                return profileDetails.get(key).value;
            }
        }
        return null;
    }
    
    public <T> T getProfileParameterValue(Class<T> type, String key) {
        return JsonHelper.objectFromJson(getProfileParameterValue(key), type);
    }

    public JSONObject extractProfileDetails(ArrayList<String> keys) {
        JSONObject data = new JSONObject();
        try {
            if (!keys.isEmpty()) {
                for (String profileKey : keys) {
                    if (contains(profileKey)) {
                        String value = getProfileParameterValue(profileKey);
                        addKeyVal(data, profileKey, value);
                    } else {
                        data.put(profileKey, "");
                    }
                }
            }
        } catch (JSONException e) {
            
        }
        return data;
    }

    public ArrayList<String> getProfileKeys() {
        ArrayList<String> profileKeysList = new ArrayList<String>();
        if (isProfileDetailsNotEmpty()) {
            profileKeysList = DataConversionUtils.listFromSet(result.profile_details.keySet());
            
        }
        return profileKeysList;
    }

    private void addKeyVal(JSONObject parent, String key, String value) {
        try {
            JSONObject valObj = DataConversionUtils.getJsonObject(value);
            if (valObj != null) {
                parent.put(key, valObj);
                return;
            }

            JSONArray valArray = DataConversionUtils.getJsonArray(value);
            if (valArray != null) {
                parent.put(key, valArray);
                return;
            }

            parent.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
