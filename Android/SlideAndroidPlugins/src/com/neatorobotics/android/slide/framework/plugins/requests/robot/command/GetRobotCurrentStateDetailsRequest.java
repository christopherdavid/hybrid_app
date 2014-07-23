package com.neatorobotics.android.slide.framework.plugins.requests.robot.command;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeKeys;

public class GetRobotCurrentStateDetailsRequest extends RobotManagerRequest {
    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        getRobotCurrentStateDetails(mContext, jsonData, callbackId);
    }

    private void getRobotCurrentStateDetails(final Context context, RobotJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "getRobotCurrentStateDetails action initiated in Robot plugin");
        final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
        LogHelper.logD(TAG, "Params\nRobotId=" + robotId);
        NeatoPrefs.saveLastConnectedNeatoRobotId(context, robotId);
        RobotManager.getInstance(context).getRobotCleaningStateDetails(context, robotId,
                new RobotRequestListenerWrapper(callbackId) {
                    @Override
                    public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
                        JSONObject jsonResult = new JSONObject();
                        if ((responseResult != null) && (responseResult instanceof GetRobotProfileDetailsResult2)) {
                            GetRobotProfileDetailsResult2 result = (GetRobotProfileDetailsResult2) responseResult;
                            jsonResult = getCurrentStateDetailsJsonObject(robotId, result);
                        }
                        return jsonResult;
                    }
                });
    }

    private JSONObject getCurrentStateDetailsJsonObject(String robotId, GetRobotProfileDetailsResult2 result) {
        JSONObject cleaningDetailsJsonObj = null;
        try {
            cleaningDetailsJsonObj = new JSONObject();
			JSONObject jsonCleaningStateDetails = new JSONObject();
            String currentStateDetails = result
                    .getProfileParameterValue(ProfileAttributeKeys.ROBOT_CURRENT_STATE_DETAILS);
            LogHelper.logD(TAG, "getRobotCurrentStateDetails " + currentStateDetails);
            try {
            	if (!TextUtils.isEmpty(currentStateDetails)) {
	            	jsonCleaningStateDetails = new JSONObject(currentStateDetails);
            	}
            }
            catch (Exception e) {
            	LogHelper.logD(TAG, "Exception in JSON parsing", e);
            }
			cleaningDetailsJsonObj.put(ProfileAttributeKeys.ROBOT_CURRENT_STATE_DETAILS, jsonCleaningStateDetails);
            cleaningDetailsJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, robotId);

        } catch (JSONException e) {
            LogHelper.logD(TAG, "Exception in getCurrentStateDetailsJsonObject", e);
        }

        return cleaningDetailsJsonObj;
    }
}
