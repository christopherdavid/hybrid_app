package com.neatorobotics.android.slide.framework.plugins.requests.robot.command;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robotdata.RobotDataNotifyUtils;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;

public class GetRobotDataRequest extends RobotManagerRequest {
    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        getRobotKeyDetails(mContext, jsonData, callbackId);
    }

    private void getRobotKeyDetails(final Context context, RobotJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "GetRobotDataRequest action initiated in Robot plugin");
        final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
        JSONArray profileKeyArray = jsonData.getJsonArray(JsonMapKeys.KEY_ROBOT_PROFILE_KEYS);
        final ArrayList<String> keys = DataConversionUtils.toStringArray(profileKeyArray);
        LogHelper.logD(TAG, "Params\nRobotId=" + robotId);
        LogHelper.logD(TAG, "Params\nRobotProfileKeys=" + profileKeyArray);
        NeatoPrefs.saveLastConnectedNeatoRobotId(context, robotId);
        RobotManager.getInstance(context).getRobotProfileDetails(robotId, keys,
                new RobotRequestListenerWrapper(callbackId) {
                    @Override
                    public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
                        JSONObject jsonResult = new JSONObject();
                        if ((responseResult != null) && (responseResult instanceof GetRobotProfileDetailsResult2)) {
                            GetRobotProfileDetailsResult2 result = (GetRobotProfileDetailsResult2) responseResult;
                            jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
                            JSONObject profileDetails = null;
                            if (keys.isEmpty()) {
                                LogHelper.log(TAG, "Empty Set, sending every profile value");
                                ArrayList<String> allKeys = result.getProfileKeys();
                                allKeys = RobotDataNotifyUtils.removeInternalKeys(allKeys);
                                profileDetails = result.extractProfileDetails(allKeys);
                            } else {
                                profileDetails = result.extractProfileDetails(keys);
                            }
                            jsonResult.put(JsonMapKeys.KEY_ROBOT_PROFILE_DATA, profileDetails);
                        }
                        return jsonResult;
                    }
                });
    }

}
