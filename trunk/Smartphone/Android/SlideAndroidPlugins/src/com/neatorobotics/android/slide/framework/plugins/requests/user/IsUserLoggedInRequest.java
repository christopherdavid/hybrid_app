package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;

import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;

public class IsUserLoggedInRequest extends UserManagerRequest {

    @Override
    public void execute(JSONArray data, String callbackId) {
        UserJsonData jsonData = new UserJsonData(data);
        isUserLoggedIn(mContext, jsonData, callbackId);
    }

    private void isUserLoggedIn(Context context, UserJsonData jsonData, String callbackId) {
        boolean isUserLoggedIn = UserHelper.isUserLoggedIn(mContext);
        PluginResult pluginLogoutResult = new PluginResult(PluginResult.Status.OK, isUserLoggedIn);
        pluginLogoutResult.setKeepCallback(false);
        sendSuccessPluginResult(pluginLogoutResult, callbackId);
    }
}
