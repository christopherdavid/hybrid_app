package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class ChangePasswordRequest extends UserManagerRequest {

    @Override
    public void execute(JSONArray data, String callbackId) {
        UserJsonData jsonData = new UserJsonData(data);
        changePassword(mContext, jsonData, callbackId);
    }

    private void changePassword(Context context, UserJsonData jsonData, final String callbackId) {
        String emailId = jsonData.getString(JsonMapKeys.KEY_EMAIL);
        LogHelper.logD(TAG, "emailId = " + emailId);
        String authToken = NeatoPrefs.getNeatoUserAuthToken(context);
        String currentPassword = jsonData.getString(JsonMapKeys.KEY_CURRENT_PASSWORD);
        String newPassword = jsonData.getString(JsonMapKeys.KEY_NEW_PASSWORD);
        UserManager.getInstance(context).changePassword(authToken, currentPassword, newPassword,
                new UserRequestListenerWrapper(callbackId));
    }
}
