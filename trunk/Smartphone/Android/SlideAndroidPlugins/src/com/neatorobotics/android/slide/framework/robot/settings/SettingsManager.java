package com.neatorobotics.android.slide.framework.robot.settings;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;
import com.neatorobotics.android.slide.framework.webservice.user.settings.NeatoRobotWebservicesSettingsHelper;
import com.neatorobotics.android.slide.framework.webservice.user.settings.RobotNotificationSettingsResult;

public class SettingsManager {
    private static final String TAG = SettingsManager.class.getSimpleName();
    private Context mContext;

    private static SettingsManager sSettingsManager;
    private static final Object INSTANCE_LOCK = new Object();

    private SettingsManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static SettingsManager getInstance(Context context) {
        synchronized (INSTANCE_LOCK) {
            if (sSettingsManager == null) {
                sSettingsManager = new SettingsManager(context);
            }
        }

        return sSettingsManager;
    }

    public void updateSpotDefinition(final String robotId, final int spotAreaLength, final int spotAreaHeight,
            final CleaningSettingsListener listener) {
        LogHelper.logD(TAG, "updateSpotDefinition called");
        LogHelper.logD(TAG, "Robot Id = " + robotId + ", Length = " + spotAreaLength + ", Height = " + spotAreaHeight);
        Runnable task = new Runnable() {
            public void run() {
                CleaningSettings cleaningSettings = RobotHelper.getCleaningSettings(mContext, robotId);
                if (cleaningSettings == null) {
                    cleaningSettings = new CleaningSettings();
                }
                // update values
                cleaningSettings.setSpotAreaLength(spotAreaLength);
                cleaningSettings.setSpotAreaHeight(spotAreaHeight);

                boolean updated = RobotHelper.updateCleaningSettings(mContext, robotId, cleaningSettings);
                if (updated) {
                    listener.onSuccess(cleaningSettings);
                } else {
                    listener.onError();
                }

            }
        };
        TaskUtils.scheduleTask(task, 0);
    }

    public void updateCleaningCategory(final String robotId, final int cleaningCategory,
            final CleaningSettingsListener listener) {
        LogHelper.logD(TAG, "updateCleaningCategory called");
        LogHelper.logD(TAG, "Robot Id = " + robotId + ", CleaningCategory = " + cleaningCategory);
        Runnable task = new Runnable() {
            public void run() {
                CleaningSettings cleaningSettings = RobotHelper.getCleaningSettings(mContext, robotId);
                if (cleaningSettings == null) {
                    cleaningSettings = new CleaningSettings();
                }
                // update values
                cleaningSettings.setCleaningCategory(cleaningCategory);

                boolean updated = RobotHelper.updateCleaningSettings(mContext, robotId, cleaningSettings);
                if (listener != null) {
                    if (updated) {
                        listener.onSuccess(cleaningSettings);
                    } else {
                        listener.onError();
                    }
                }
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }

    public void getCleaningSettings(final String robotId, final CleaningSettingsListener listener) {
        LogHelper.logD(TAG, "getRobotSettings called");
        LogHelper.logD(TAG, "Robot Id = " + robotId);
        Runnable task = new Runnable() {
            public void run() {
                CleaningSettings cleaningSettings = RobotHelper.getCleaningSettings(mContext, robotId);
                if (cleaningSettings != null) {
                    listener.onSuccess(cleaningSettings);
                } else {
                    listener.onError();
                }
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }

    public void getNotificationSettings(final String email, final WebServiceBaseRequestListener listener) {
        LogHelper.logD(TAG, "getNotificationSettings called");
        Runnable task = new Runnable() {
            public void run() {
                try {
                    RobotNotificationSettingsResult settingsResult = NeatoRobotWebservicesSettingsHelper
                            .getRobotNotificationSettingsRequest(mContext, email);
                    RobotHelper.saveNotificationSettingsJson(mContext, email, settingsResult.getNotificationsJson()
                            .toString());
                    listener.onReceived(settingsResult);
                } catch (UserUnauthorizedException e) {
                    listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, e.getErrorMessage());
                } catch (NeatoServerException e) {
                    listener.onServerError(e.getStatusCode(), e.getErrorMessage());
                } catch (IOException e) {
                    listener.onNetworkError(e.getMessage());
                }
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }

    public void updateNotificationState(final String email, final String notificationId, final boolean enable,
            final WebServiceBaseRequestListener listener) {

        LogHelper.logD(TAG, "updateNotificationState called");
        LogHelper.logD(TAG, "Email = " + email + ", NotificationId = " + notificationId + ", Enable = " + enable);

        Runnable task = new Runnable() {
            public void run() {
                try {
                    JSONObject updatedJsonObj = getUpdatedNotificationSettingsJsonObj(email, notificationId, enable);
                    NeatoWebserviceResult result = NeatoRobotWebservicesSettingsHelper
                            .setRobotNotificationSettingsRequest(mContext, email, updatedJsonObj.toString());
                    RobotHelper.saveNotificationSettingsJson(mContext, email, updatedJsonObj.toString());
                    listener.onReceived(result);
                } catch (UserUnauthorizedException e) {
                    listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, e.getErrorMessage());
                } catch (NeatoServerException e) {
                    listener.onServerError(e.getStatusCode(), e.getErrorMessage());
                } catch (IOException e) {
                    listener.onNetworkError(e.getMessage());
                }
            }
        };
        TaskUtils.scheduleTask(task, 0);
    }

    private JSONObject getUpdatedNotificationSettingsJsonObj(String email, String notificationId, boolean enable) {
        JSONObject settingsJson = RobotHelper.getNotificationSettingsJson(mContext, email);
        try {
            if (settingsJson != null) {
                if (notificationId.equals(RobotCommandPacketConstants.NOTIFICATIONS_ID_GLOBAL)) {
                    settingsJson.put(JsonMapKeys.KEY_GLOBAL_NOTIFICATIONS, enable);
                } else {
                    JSONArray notifications = (JSONArray) settingsJson.opt(JsonMapKeys.KEY_NOTIFICATIONS);
                    int count = notifications.length();
                    for (int index = 0; index < count; index++) {
                        JSONObject notificationObj = notifications.getJSONObject(index);
                        String id = notificationObj.optString(JsonMapKeys.KEY_NOTIFICATION_KEY);
                        if ((!TextUtils.isEmpty(id)) && (id.equals(notificationId))) {
                            notificationObj.put(JsonMapKeys.KEY_NOTIFICATION_VALUE, enable);
                            break;
                        }

                    }
                }
            }
        } catch (JSONException ex) {
            LogHelper.logD(TAG, "JSONException in getUpdatedNotificationSettingsJsonObj");
        }

        return settingsJson;
    }
}
