package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class DisassociateAllRobotsRequest extends UserManagerRequest {

    @Override
    public void execute(JSONArray data, String callbackId) {
        UserJsonData jsonData = new UserJsonData(data);
        disassociateAllRobots(mContext, jsonData, callbackId);
    }

    private void disassociateAllRobots(Context context, UserJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "disassociateAllRobots Called");

        String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
        if (TextUtils.isEmpty(email)) {
            email = NeatoPrefs.getUserEmailId(context);
        }
        LogHelper.logD(TAG, "JSON String: " + jsonData);

        LogHelper.logD(TAG, "Disassociate all robots for email id: " + email);
        UserManager.getInstance(context).disassociateAllRobots(email, new UserRequestListenerWrapper(callbackId));
    }
}
