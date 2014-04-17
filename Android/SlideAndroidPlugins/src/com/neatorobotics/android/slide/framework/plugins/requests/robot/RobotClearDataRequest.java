package com.neatorobotics.android.slide.framework.plugins.requests.robot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotClearDataResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotClearDataResult.Result;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;

public class RobotClearDataRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        clearRobotData(mContext, jsonData, callbackId);
    }

    private void clearRobotData(Context context, RobotJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "clearRobotData Called");

        String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
        String robotID = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);

        if (TextUtils.isEmpty(email)) {
            email = NeatoPrefs.getUserEmailId(context);
        }
        LogHelper.logD(TAG, "JSON String: " + jsonData);

        LogHelper.logD(TAG, "Clear the data on robot: " + robotID + " for email id: " + email);
        RobotManager.getInstance(context).clearRobotData(email, robotID, new RobotRequestListenerWrapper(callbackId) {

            @Override
            public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
                JSONObject robotDetail = getRobotClearDataResultJsonObject(responseResult);
                return robotDetail;
            }
        });
    }

    private JSONObject getRobotClearDataResultJsonObject(NeatoWebserviceResult responseResult) throws JSONException {
        JSONObject robotJsonObj = null;
        if ((responseResult != null) && (responseResult instanceof RobotClearDataResult)) {
            Result result = ((RobotClearDataResult) responseResult).result;
            if (result != null) {
                robotJsonObj = new JSONObject();
                robotJsonObj.put(JsonMapKeys.KEY_SUCCESS, result.success);
                robotJsonObj.put(JsonMapKeys.KEY_MESSAGE, result.message);
            }
        }

        return robotJsonObj;
    }

}
