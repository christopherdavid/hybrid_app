package com.neatorobotics.android.slide.framework.plugins.requests.robot.profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotDetailResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;

public class GetRobotDetailRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        getRobotDetail(mContext, jsonData, callbackId);
    }

    private void getRobotDetail(Context context, RobotJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "getRobotDetail action initiated in Robot plugin");
        String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);

        RobotManager.getInstance(context).getRobotDetail(robotId, new RobotRequestListenerWrapper(callbackId) {

            @Override
            public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
                JSONObject robotDetail = getRobotDetailJsonObject(responseResult);
                return robotDetail;
            }
        });
    }

    private JSONObject getRobotDetailJsonObject(NeatoWebserviceResult responseResult) throws JSONException {
        JSONObject robotJsonObj = null;
        if ((responseResult != null) && (responseResult instanceof RobotDetailResult)) {
            RobotItem robotItem = ((RobotDetailResult) responseResult).result;
            if (robotItem != null) {
                robotJsonObj = new JSONObject();
                robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, robotItem.serial_number);
                robotJsonObj.put(JsonMapKeys.KEY_ROBOT_NAME, robotItem.name);
            }
        }

        return robotJsonObj;
    }
}
