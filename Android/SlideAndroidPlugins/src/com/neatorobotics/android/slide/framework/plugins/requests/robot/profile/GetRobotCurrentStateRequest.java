package com.neatorobotics.android.slide.framework.plugins.requests.robot.profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robotdata.RobotProfileDataUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;

public class GetRobotCurrentStateRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        getRobotCurrentState(mContext, jsonData, callbackId);
    }

    private void getRobotCurrentState(final Context context, RobotJsonData jsonData, final String callbackId) {

        LogHelper.logD(TAG, "getRobotCurrentState is called");
        final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
        NeatoPrefs.saveLastConnectedNeatoRobotId(context, robotId);
        RobotManager.getInstance(context).getRobotCleaningState(context, robotId,
                new RobotRequestListenerWrapper(callbackId) {
                    @Override
                    public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
                        JSONObject jsonResult = new JSONObject();
                        if ((responseResult != null) && (responseResult instanceof GetRobotProfileDetailsResult2)) {
                            GetRobotProfileDetailsResult2 result = (GetRobotProfileDetailsResult2) responseResult;
                            String virtualState = RobotProfileDataUtils.getState(context, result);
                            jsonResult.put(JsonMapKeys.KEY_ROBOT_NEW_VIRTUAL_STATE, virtualState);
                            String currentState = RobotProfileDataUtils.getRobotCurrentState(context, result);

                            jsonResult.put(JsonMapKeys.KEY_ROBOT_CURRENT_STATE, currentState);
                            jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
                        }
                        return jsonResult;
                    }
                });
    }

}
