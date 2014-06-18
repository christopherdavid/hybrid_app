package com.neatorobotics.android.slide.framework.robotdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotNetworkInfo;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationConstants;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationUtil;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.drive.RobotAvailabilityToDriveStatus;
import com.neatorobotics.android.slide.framework.robot.drive.RobotDriveHelper;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants;
import com.neatorobotics.android.slide.framework.robotdata.RobotProfileConstants.RobotProfileValueChangedStatus;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeKeys;

public class RobotDataNotifyUtils {

    private static final String TAG = RobotDataNotifyUtils.class.getSimpleName();

    public static void notifyProfileDataIfChanged(Context context, String robotId, GetRobotProfileDetailsResult2 details) {
        HashMap<String, RobotProfileValueChangedStatus> changedProfileKeys = RobotProfileDataUtils
                .getChangedProfileKeysMap(context, details, robotId);
        if (changedProfileKeys != null) {
            Set<String> keySet = changedProfileKeys.keySet();
            for (String key : keySet) {
                RobotProfileValueChangedStatus changedStatus = changedProfileKeys.get(key);
                notifyProfileKeyDataChanged(context, robotId, details, key, changedStatus);
            }
            // generic notification to be fired with all the changed profile
            // details.
            notifyRobotDataChanged(context, robotId, details, changedProfileKeys);
        }
    }

    private static RobotItem fetchRobotInformationIfRequired(Context context, String robotId) {

        RobotItem robotItem = RobotHelper.getRobotItem(context, robotId);
        if (robotItem == null) {
            robotItem = RobotManager.getInstance(context).getRobotDetailAndSave(robotId);
        }

        return robotItem;

    }

    private static void notifyProfileKeyDataChanged(Context context, String robotId,
            GetRobotProfileDetailsResult2 details, String key, RobotProfileValueChangedStatus changedStatus) {

        // Ensure we have the robot information in our database for which change
        // event is fired
        fetchRobotInformationIfRequired(context, robotId);
        if (key.equals(ProfileAttributeKeys.ROBOT_CURRENT_STATE)
                || key.equals(ProfileAttributeKeys.ROBOT_CURRENT_STATE_DETAILS)) {
            notifyStateChange(context, robotId, details);
        } else if (key.equals(ProfileAttributeKeys.ROBOT_NAME)) {
            notifyRobotNameChange(context, robotId, details);
        } else if (key.equals(ProfileAttributeKeys.ROBOT_ENABLE_BASIC_SCHEDULE)) {
            notifyBasicScheduleStateChange(context, robotId, details);
        } else if (key.equals(ProfileAttributeKeys.ROBOT_SCHEDULE_UPDATED)) {
            notifyScheduleUpdated(context, robotId, details);
        } else if (key.equals(ProfileAttributeKeys.AVAILABLE_TO_DRIVE)) {
            // Notify if the available for drive robot is changed.
            if (changedStatus == RobotProfileValueChangedStatus.ROBOT_VALUE_CHANGED) {
                RobotAvailabilityToDriveStatus availabiltyReponse = RobotProfileDataUtils.getRobotAvailableResponse(
                        context, details);
                if (availabiltyReponse != null) {
                    if (availabiltyReponse.isRobotAvailableToDrive()) {
                        String robotDriveIp = availabiltyReponse.getRobotDriveIp();
                        RobotDriveHelper.getInstance(context).robotReadyToDrive(robotId, robotDriveIp);
                        // Currently sending secret pass key in this flow also
                        // to enable testing of robot with secure key.
                        RobotNetworkInfo info = details.getProfileParameterValue(RobotNetworkInfo.class,
                                ProfileAttributeKeys.ROBOT_NETWORK_INFO);
                        if ((info != null) && (info.isValid())) {
                            NeatoPrefs.saveDriveSecureKey(context, info.robotDirectConnectSecret);
                        }
                    } else {
                        int responseCode = availabiltyReponse.getDriveErrorCode();
                        RobotDriveHelper.getInstance(context).notifyRobotNotAvailableForDrive(robotId, responseCode);
                    }
                }
            }
        } else if (key.equals(ProfileAttributeKeys.INTEND_TO_DRIVE)) {
            // Notify if intend to drive is set for the robot
            // TODO: What if the request is empty. Would it notify?
            if (changedStatus == RobotProfileValueChangedStatus.ROBOT_VALUE_CHANGED) {
                String intendToDrive = RobotProfileDataUtils.getRobotDriveRequest(context, details);
                RobotDriveHelper.getInstance(context).robotDriveRequestInitiated(robotId, intendToDrive);
            }
        } else if (key.equals(ProfileAttributeKeys.ROBOT_NOTIFICATION)) {
            if (changedStatus == RobotProfileValueChangedStatus.ROBOT_VALUE_CHANGED) {
                String notification = RobotProfileDataUtils.getRobotNotification(context, details);
                notifyRobotNotification(context, robotId, notification);
            }
        } else if (key.equals(ProfileAttributeKeys.ROBOT_ERROR)) {
            if (changedStatus == RobotProfileValueChangedStatus.ROBOT_VALUE_CHANGED) {
                String error = RobotProfileDataUtils.getRobotNotificationError(context, details);
                notifyRobotError(context, robotId, error);
            }
        } else if (key.equals(ProfileAttributeKeys.ROBOT_ONLINE_STATUS)) {
            if (changedStatus == RobotProfileValueChangedStatus.ROBOT_VALUE_CHANGED) {
                notifyRobotIsOnlineStatusChanged(context, robotId, details);
            }
        }
    }

    // Private helper method to consume the profile data parameters.
    private static void notifyBasicScheduleStateChange(Context context, String robotId,
            GetRobotProfileDetailsResult2 details) {
        String basicScheduleState = RobotProfileDataUtils.getScheduleState(context,
                SchedulerConstants.SCHEDULE_TYPE_BASIC, details);
        LogHelper.logD(TAG, "Robot Schedule State :" + basicScheduleState);
        if (!TextUtils.isEmpty(basicScheduleState)) {
            HashMap<String, String> stateData = new HashMap<String, String>();
            stateData.put(JsonMapKeys.KEY_SCHEDULE_STATE, basicScheduleState);
            stateData.put(JsonMapKeys.KEY_SCHEDULE_TYPE, String.valueOf(SchedulerConstants.SCHEDULE_TYPE_BASIC));
            RobotNotificationUtil.notifyDataChanged(context, robotId,
                    RobotNotificationConstants.ROBOT_SCHEDULE_STATE_CHANGED, stateData);
        }
    }

    private static void notifyRobotNotification(Context context, String robotId, String notification) {
        if (TextUtils.isEmpty(notification)) {
            return;
        }
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(JsonMapKeys.KEY_ROBOT_NOTIFICATION, notification);
        RobotNotificationUtil.notifyDataChanged(context, robotId, RobotNotificationConstants.ROBOT_NOTIFICATION, data);
    }

    private static void notifyRobotError(Context context, String robotId, String error) {
        if (TextUtils.isEmpty(error)) {
            return;
        }
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(JsonMapKeys.KEY_ROBOT_ERROR, error);
        RobotNotificationUtil.notifyDataChanged(context, robotId, RobotNotificationConstants.ROBOT_ERROR, data);
    }

    private static void notifyScheduleUpdated(Context context, String robotId, GetRobotProfileDetailsResult2 details) {
        boolean isScheduleUpdated = RobotProfileDataUtils.isScheduleUpdated(context, details);
        if (isScheduleUpdated) {
            RobotNotificationUtil.notifyDataChanged(context, robotId,
                    RobotNotificationConstants.ROBOT_IS_SCHEDULE_UPDATED, new HashMap<String, String>());
        }
    }

    private static void notifyRobotNameChange(Context context, String robotId, GetRobotProfileDetailsResult2 details) {

        String robotName = RobotProfileDataUtils.getRobotName(details);
        if (TextUtils.isEmpty(robotName)) {
            LogHelper.logD(TAG, "robotName is empty");
            return;
        }

        RobotItem robotItem = fetchRobotInformationIfRequired(context, robotId);
        if (robotItem == null) {
            return;
        }
        String currentRobotName = "";
        currentRobotName = robotItem.name;
        if (robotName.equalsIgnoreCase(currentRobotName)) {
            LogHelper.logD(TAG, "Robot name is not changed");
            return;
        }
        robotItem.name = robotName;
        RobotHelper.saveRobotDetails(context, robotItem);

        LogHelper.logD(TAG, "Robot name is changed");
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(JsonMapKeys.KEY_ROBOT_NAME, robotName);
        RobotNotificationUtil.notifyDataChanged(context, robotId, RobotNotificationConstants.ROBOT_NAME_UPDATE, data);
    }

    private static void notifyStateChange(Context context, String robotId, GetRobotProfileDetailsResult2 details) {

        String currentState = RobotProfileDataUtils.getRobotCurrentState(context, details);
        String currentStateDetails = details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_CURRENT_STATE_DETAILS);

        HashMap<String, String> stateData = new HashMap<String, String>();
        stateData.put(ProfileAttributeKeys.ROBOT_CURRENT_STATE, currentState);
        stateData.put(ProfileAttributeKeys.ROBOT_CURRENT_STATE_DETAILS, currentStateDetails);

        RobotNotificationUtil.notifyDataChanged(context, robotId,
                RobotNotificationConstants.ROBOT_CURRENT_STATE_CHANGED, stateData);

        String state = RobotProfileDataUtils.getState(context, details);
        if (!TextUtils.isEmpty(state)) {
            HashMap<String, String> virtualStateData = new HashMap<String, String>();
            stateData.put(JsonMapKeys.KEY_ROBOT_STATE_UPDATE, String.valueOf(state));
            RobotNotificationUtil.notifyDataChanged(context, robotId, RobotNotificationConstants.ROBOT_STATE_UPDATE,
                    virtualStateData);
        } else {
            LogHelper.logD(TAG, "No State for robotId: " + robotId);
        }

        LogHelper.log(TAG, "Current state Received from Web server: " + currentStateDetails);

    }

    private static void notifyRobotDataChanged(Context context, String robotId, GetRobotProfileDetailsResult2 details,
            HashMap<String, RobotProfileValueChangedStatus> changedKeyStatusMap) {

        Set<String> keySet = changedKeyStatusMap.keySet();
        ArrayList<String> updatedKeys = new ArrayList<String>();

        for (String key : keySet) {
            LogHelper.log(TAG, "Updated Key is " + key);
            updatedKeys.add(key);
        }
        updatedKeys = RobotDataNotifyUtils.removeInternalKeys(updatedKeys);
        if (updatedKeys.isEmpty()) {
            LogHelper.log(TAG, "No external keys updated. return");
            return;
        }
        JSONObject profileData = details.extractProfileDetails(updatedKeys);
        RobotNotificationUtil.notifyDataChanged(context, robotId, RobotNotificationConstants.ROBOT_DATA_CHANGED,
                profileData);
    }

    private static void notifyRobotIsOnlineStatusChanged(Context context, String robotId,
            GetRobotProfileDetailsResult2 details) {
        int robotIsOnlineStatus = RobotProfileDataUtils.getRobotIsOnlineStatus(details);
        LogHelper.logD(TAG, "Robot online status is changed");
        LogHelper.logD(TAG, "Current Robot online status : " + robotIsOnlineStatus);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(JsonMapKeys.KEY_ROBOT_ONLINE_STATUS, String.valueOf(robotIsOnlineStatus));
        RobotNotificationUtil.notifyDataChanged(context, robotId, RobotNotificationConstants.ROBOT_ONLINE_STATUS, data);
    }

    public static ArrayList<String> removeInternalKeys(ArrayList<String> keys) {
        for (String key : ProfileAttributeKeys.sInternalUsedKeys) {
            if (keys.contains(key)) {
                LogHelper.log(TAG, "Removing Key " + key);
                keys.remove(key);
            }
        }
        return keys;
    }

}
