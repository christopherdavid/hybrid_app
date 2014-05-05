package com.neatorobotics.android.slide.framework.robotdata;

import java.io.IOException;
import java.util.HashMap;

import android.content.Context;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacketUtils;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotPacketConstants;
import com.neatorobotics.android.slide.framework.robot.drive.RobotDriveHelper;
import com.neatorobotics.android.slide.framework.robot.settings.SettingsManager;
import com.neatorobotics.android.slide.framework.timedmode.RobotCommandTimerHelper;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.DeleteRobotProfileKeyResult;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeKeys;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebservicesHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.SetRobotProfileDetailsResult3;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;

public class RobotDataManager {

    private static final String TAG = RobotDataManager.class.getSimpleName();
    private static final String EMPTY_STRING = "";

    // Used to send command. This will send the standard command format as
    // value.
    public static void sendRobotCommand(Context context, String robotId, int commandId,
            HashMap<String, String> commandParams, WebServiceBaseRequestListener listener) {
        LogHelper.logD(TAG, "Send command action initiated sendRobotCommand - RobotSerialId = " + robotId);

        String robotPacketInXmlFormat = RobotCommandPacketUtils.getRobotCommandPacketXml(context, commandId,
                commandParams, RobotPacketConstants.DISTRIBUTION_MODE_TYPE_TIME_MODE_SERVER);

        setRobotProfileParam(context, robotId, commandId, robotPacketInXmlFormat, listener);

        // TODO: Find another place for this.
        // Ideally should be retrieved from the server.
        if (commandParams.get(JsonMapKeys.KEY_CLEANING_CATEGORY) != null) {
            int cleaningCategory = Integer.valueOf(commandParams.get(JsonMapKeys.KEY_CLEANING_CATEGORY));
            LogHelper.log(TAG, "Call before updateCleaningCategory, the category value is :" + cleaningCategory);
            SettingsManager.getInstance(context).updateCleaningCategory(robotId, cleaningCategory, null);
        }
    }

    // Public static helper method to set the schedule status to updated
    public static void sendRobotScheduleUpdated(Context context, String robotId, WebServiceBaseRequestListener listener) {
        LogHelper.logD(TAG, "Send command action initiated sendRobotScheduleUpdated - RobotSerialId = " + robotId);
        setRobotProfileParam(context, robotId, RobotCommandPacketConstants.COMMAND_SCHEDULE_UPDATED,
                String.valueOf(true), listener);
    }

    public static void setRobotProfileParam(final Context context, final String robotId, final int keyUniqueId,
            final String value, final WebServiceBaseRequestListener listener) {

        Runnable task = new Runnable() {
            public void run() {
                try {

                    String key = RobotProfileConstants.getProfileKeyTypeForCommand(keyUniqueId);

                    LogHelper.logD(TAG, "SetRobotProfileParam called for Robot Id = " + robotId + " Key: " + key
                            + "Value: " + value);
                    HashMap<String, String> profileParams = new HashMap<String, String>();
                    profileParams.put(key, value);
                    SetRobotProfileDetailsResult3 result = NeatoRobotDataWebservicesHelper
                            .setRobotProfileDetailsRequest3(context, robotId, profileParams);

                    long timestamp = result.extra_params.timestamp;
                    RobotHelper.saveProfileParam(context, robotId, key, timestamp);

                    // Do not start timer for every set profile data change.

                    if (RobotProfileConstants.isTimerExpirableForProfileKey(key)) {
                        RobotCommandTimerHelper.getInstance(context).startCommandExpiryTimer(robotId, keyUniqueId);
                    }

                    listener.onReceived(result);
                } catch (UserUnauthorizedException ex) {
                    listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, ex.getErrorMessage());
                } catch (NeatoServerException ex) {
                    listener.onServerError(ex.getStatusCode(), ex.getErrorMessage());
                } catch (IOException ex) {
                    listener.onNetworkError(ex.getMessage());
                }
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }

    public static void getServerData(final Context context, final String robotId) {

        Runnable task = new Runnable() {
            public void run() {
                try {
                    GetRobotProfileDetailsResult2 details = NeatoRobotDataWebservicesHelper
                            .getRobotProfileDetailsRequest2(context, robotId, EMPTY_STRING);
                    LogHelper.logD(TAG, "getServerData, retrieved profileDetails");
                    if (details.success()) {
                        RobotDataNotifyUtils.notifyProfileDataIfChanged(context, robotId, details);
                    }
                } catch (UserUnauthorizedException e) {
                    LogHelper.log(TAG, "UserUnauthorizedException in getServerData", e);
                } catch (NeatoServerException e) {
                    LogHelper.log(TAG, "NeatoServerException in getServerData", e);
                } catch (IOException e) {
                    LogHelper.log(TAG, "IOException in getServerData", e);
                }
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }

    private static void robotCommandExpiryResetData(final Context context, final String robotId,
            final WebServiceBaseRequestListener listener) {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    // Reset cleaningCommand so that robot does not fetch it.
                    // Also to update the UI of other smartapps.
                    // TODO: Reset other values too when support added.
                    // TODO: Remove
                    NeatoRobotDataWebservicesHelper.resetRobotProfileValue(context, robotId,
                            ProfileAttributeKeys.ROBOT_CLEANING_COMMAND, ProfileAttributeKeys.INTEND_TO_DRIVE);

                    // Get the current profile parameters to reflect in the UI
                    GetRobotProfileDetailsResult2 details = NeatoRobotDataWebservicesHelper
                            .getRobotProfileDetailsRequest2(context, robotId, EMPTY_STRING);

                    if (details.success()) {
                        RobotDataNotifyUtils.notifyProfileDataIfChanged(context, robotId, details);
                    }
                    listener.onReceived(details);
                } catch (UserUnauthorizedException e) {
                    LogHelper.log(TAG, "UserUnauthorizedException in getServerData", e);
                } catch (NeatoServerException e) {
                    LogHelper.log(TAG, "NeatoServerException in getServerData", e);
                } catch (IOException e) {
                    LogHelper.log(TAG, "IOException in getServerData", e);
                }
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }

    public static void onCommandExpired(final Context context, final String robotId,
            final WebServiceBaseRequestListener listener) {
        robotCommandExpiryResetData(context, robotId, listener);
    }

    public static void deleteProfileDetailKey(final Context context, final String robotId, final String key,
            boolean notify, final WebServiceBaseRequestListener listener) {
        try {
            DeleteRobotProfileKeyResult result = NeatoRobotDataWebservicesHelper.deleteRobotProfileKey(context,
                    robotId, key, notify);
            listener.onReceived(result);
        } catch (UserUnauthorizedException ex) {
            listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, ex.getErrorMessage());
        } catch (NeatoServerException ex) {
            listener.onServerError(ex.getStatusCode(), ex.getErrorMessage());
        } catch (IOException ex) {
            listener.onNetworkError(ex.getMessage());
        }
    }
}
