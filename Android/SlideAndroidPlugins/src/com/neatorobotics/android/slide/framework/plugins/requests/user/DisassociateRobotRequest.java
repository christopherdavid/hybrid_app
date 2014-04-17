package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class DisassociateRobotRequest extends UserManagerRequest {

    @Override
    public void execute(JSONArray data, String callbackId) {
        UserJsonData jsonData = new UserJsonData(data);
        disassociateRobot(mContext, jsonData, callbackId);
    }

    private void disassociateRobot(Context context, UserJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "disassociateRobot Called");

        String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
        if (TextUtils.isEmpty(email)) {
            email = NeatoPrefs.getUserEmailId(context);
        }
        String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);

        LogHelper.logD(TAG, "JSON String: " + jsonData);
        LogHelper.logD(TAG, "Disassociate action initiated in Robot plugin for robot Id: " + robotId
                + " and email id: " + email);

        UserManager.getInstance(context).disassociateRobot(robotId, email, new UserRequestListenerWrapper(callbackId));
    }

}
