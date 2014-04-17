package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.gcm.PushNotificationMessageHandler;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationUtils;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.drive.RobotDriveHelper;
import com.neatorobotics.android.slide.framework.timedmode.RobotCommandTimerHelper;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class LogoutUserRequest extends UserManagerRequest {
    @Override
    public void execute(JSONArray data, String callbackId) {
        UserJsonData jsonData = new UserJsonData(data);
        logoutUser(mContext, jsonData, callbackId);
    }

    private void logoutUser(final Context context, final UserJsonData jsonData, final String callbackId) {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                String authToken = NeatoPrefs.getNeatoUserAuthToken(context);
                if (TextUtils.isEmpty(authToken)) {
                    PluginResult logoutPluginResult = new PluginResult(PluginResult.Status.ERROR);
                    LogHelper.logD(TAG, "No user logged in.");
                    sendErrorPluginResult(logoutPluginResult, callbackId);
                    return;
                }

                UserManager.getInstance(context).logoutUser();
                PluginResult logoutPluginResult = new PluginResult(PluginResult.Status.OK);
                sendSuccessPluginResult(logoutPluginResult, callbackId);
                LogHelper.logD(TAG, "Logout successful.");
                // Clean-up
                PushNotificationMessageHandler.getInstance(context).removePushNotificationListener();
                PushNotificationUtils.unregisterPushNotification(context);
                RobotCommandTimerHelper.getInstance(context).stopAllCommandTimers();
                RobotDriveHelper.getInstance(context).untrackAllRobotDriveRequest();
                AppUtils.clearNeatoUserDeviceId(context);
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }
}
