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
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotVirtualOnlineStatusResult;

public class RobotGetVirtualOnlineStatusRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        getRobotVirtualOnlineStatus(mContext, jsonData, callbackId);
    }

    private void getRobotVirtualOnlineStatus(Context context, RobotJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "getRobotVirtualOnlineStatus action initiated in Robot plugin");
        final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);

        RobotManager.getInstance(context).getRobotVirtualOnlineStatus(robotId,
                new RobotRequestListenerWrapper(callbackId) {

                    @Override
                    public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
                        JSONObject resultJsonObj = null;
                        if (responseResult instanceof RobotVirtualOnlineStatusResult) {
                            resultJsonObj = getRobotOnlineStatusJsonObject(robotId,
                                    ((RobotVirtualOnlineStatusResult) responseResult).result.online);

                        }
                        return resultJsonObj;
                    }
                });
    }

    protected JSONObject getRobotOnlineStatusJsonObject(String robotId, boolean online) throws JSONException {
        JSONObject robotJsonObj = new JSONObject();
        robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
        robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ONLINE_STATUS, online);
        return robotJsonObj;
    }
}
