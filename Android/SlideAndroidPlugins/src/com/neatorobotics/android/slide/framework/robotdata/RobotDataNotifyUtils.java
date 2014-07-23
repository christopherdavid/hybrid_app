package com.neatorobotics.android.slide.framework.robotdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationConstants;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationUtil;
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
        if (key.equals(ProfileAttributeKeys.ROBOT_NAME)) {
            notifyRobotNameChange(context, robotId, details);
        } else if (key.equals(ProfileAttributeKeys.ROBOT_ENABLE_BASIC_SCHEDULE)) {
            notifyBasicScheduleStateChange(context, robotId, details);
        } else if (key.equals(ProfileAttributeKeys.ROBOT_SCHEDULE_UPDATED)) {
            notifyScheduleUpdated(context, robotId, details);
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
