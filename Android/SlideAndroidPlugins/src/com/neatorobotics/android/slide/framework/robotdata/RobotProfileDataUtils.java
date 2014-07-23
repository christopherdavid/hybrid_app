package com.neatorobotics.android.slide.framework.robotdata;

import java.util.HashMap;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants;
import com.neatorobotics.android.slide.framework.robotdata.RobotProfileConstants.RobotProfileValueChangedStatus;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2.ProfileKeyDetails;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeKeys;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeValueKeys;

public class RobotProfileDataUtils {

    private static final String TAG = RobotProfileDataUtils.class.getSimpleName();

    public static String getRobotName(GetRobotProfileDetailsResult2 details) {
        String robotName = details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_NAME);
        return robotName;
    }

    public static int getRobotIsOnlineStatus(GetRobotProfileDetailsResult2 details) {
        String robotIsOnlineStatus = details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_ONLINE_STATUS);
        return Integer.parseInt(robotIsOnlineStatus);
    }

    /**
     * Get state from the current state key and if that is empty get from the
     * current state details.
     */
    public static String getRobotCurrentState(Context context, GetRobotProfileDetailsResult2 details) {
        String currentState = details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_CURRENT_STATE);
        if (TextUtils.isEmpty(currentState)) {
            LogHelper.logD(TAG, "ROBOT_CURRENT_STATE is empty, get state from state details");
            currentState = getRobotCurrentStateFromStateDetails(context, details);
        }
        LogHelper.logD(TAG, "getServerData, retrieved ROBOT_CURRENT_STATE");
        return currentState;
    }

    /**
     * Get current state from the current state details
     */
    private static String getRobotCurrentStateFromStateDetails(Context context, GetRobotProfileDetailsResult2 details) {
        String currentStateDetails = details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_CURRENT_STATE_DETAILS);
        LogHelper.logD(TAG, "getServerData, retrieved ROBOT_CURRENT_STATE_DETAILS ======>>>> " + currentStateDetails);
        if (TextUtils.isEmpty(currentStateDetails)) {
            return "";
        }
        try {
            JSONObject currentStateDetailsJson = new JSONObject(currentStateDetails);
            return currentStateDetailsJson.getString(ProfileAttributeKeys.ROBOT_CURRENT_STATE);
        } catch (JSONException e) {
            LogHelper
                    .logD(TAG, "Exception in parsing ROBOT_CURRENT_STATE_DETAILS ======>>>> " + currentStateDetails, e);
        }
        return "";
    }

    public static String getScheduleState(Context context, int scheduleType, GetRobotProfileDetailsResult2 details) {
        String scheduleState = null;
        if (scheduleType == SchedulerConstants.SCHEDULE_TYPE_BASIC) {
            scheduleState = details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_ENABLE_BASIC_SCHEDULE);
            LogHelper.logD(TAG, "getBasicScheduleState, retrived ROBOT_SCHEDULE");
        }
        return scheduleState;
    }

    public static boolean isScheduleUpdated(Context context, GetRobotProfileDetailsResult2 details) {
        String isScheduleUpdated = details.getProfileParameterValue(ProfileAttributeKeys.ROBOT_SCHEDULE_UPDATED);
        LogHelper.logD(TAG, "isScheduleUpdated, retrived ROBOT_SCHEDULE");
        return Boolean.valueOf(isScheduleUpdated);
    }

    public static String getState(Context context, GetRobotProfileDetailsResult2 details) {
        return getRobotCurrentState(context, details);
    }

    private static boolean isDataChanged(Context context, GetRobotProfileDetailsResult2 details, String robotId,
            String key) {
        long timeStamp = details.getProfileParameterTimeStamp(key);
        return RobotHelper.isProfileDataChanged(context, robotId, key, timeStamp);
    }

    // Returns if data is changed or existing data was deleted.
    // There are 3 cases which will return ROBOT_VALUE_NOT_CHANGED,
    // ROBOT_VALUE_DELETED, ROBOT_VALUE_CHANGED
    // from enum RobotProfileKeyChangedStatus
    // It returns ROBOT_VALUE_CHANGED if the data is changed/added newly
    // It returns ROBOT_VALUE_NOT_CHANGED if the data is not changed
    // It returns ROBOT_VALUE_DELETED if the data is deleted
    // It also updates the database accordingly.
    public static RobotProfileValueChangedStatus updateDataTimestampIfChanged(Context context,
            GetRobotProfileDetailsResult2 details, String robotId, String key) {
        
        // Check if the key is available in the details.
        if (details.contains(key)) {
            // Check to see if it is changed
            if (isDataChanged(context, details, robotId, key)) {
                LogHelper.log(TAG, "Data is changed. Saving data for key" + key);
                long timestamp = details.getProfileParameterTimeStamp(key);
                RobotHelper.saveProfileParam(context, robotId, key, timestamp);
                return RobotProfileValueChangedStatus.ROBOT_VALUE_CHANGED;
            }
            LogHelper.log(TAG, "Data is not changed, for key" + key);
            return RobotProfileValueChangedStatus.ROBOT_VALUE_NOT_CHANGED;
        } else {
            // The data is not available. Delete the key from database.
            // Return true only if the delete was successful. if the key wasn't
            // present then it will return false.
            boolean deleteSuccessful = RobotHelper.deleteProfileParamIfExists(context, robotId, key);
            // return the status according to the deletion is successful or not.
            return (deleteSuccessful ? RobotProfileValueChangedStatus.ROBOT_VALUE_DELETED
                    : RobotProfileValueChangedStatus.ROBOT_VALUE_NOT_CHANGED);
        }
    }

    // Get the Profile keys whose timestamp values have been changed and are
    // different from what are in the database.
    // This will return an HashMap<ProfileAttributeKeysEnum,
    // RobotProfileKeyChangedStatus> with the profile keys.
    public static HashMap<String, RobotProfileValueChangedStatus> getChangedProfileKeysMap(Context context,
            GetRobotProfileDetailsResult2 details, String robotId) {
        HashMap<String, RobotProfileValueChangedStatus> changedProfileKeys = new HashMap<String, RobotProfileValueChangedStatus>();

        if ((details != null) && (details.result != null) && ((details.result.profile_details != null))) {
            // We need to know what values have been deleted from the profile
            // parameters also.
            // This is because we want to delete those from the database as well
            // and in some cases notify the user too.
            HashMap<String, ProfileKeyDetails> profileDetails = details.result.profile_details;
            Set<String> robotProfileKeys = profileDetails.keySet();
            for (String key : robotProfileKeys) {
                if (key != null) {
                    RobotProfileValueChangedStatus profileKeyValueUpdated = RobotProfileDataUtils
                            .updateDataTimestampIfChanged(context, details, robotId, key);
                    if (profileKeyValueUpdated != RobotProfileValueChangedStatus.ROBOT_VALUE_NOT_CHANGED) {
                        changedProfileKeys.put(key, profileKeyValueUpdated);
                    }
                } else {
                    LogHelper.logD(TAG, "Profile-value is not present for the ProfileAttributeKeysEnum key");
                }
            }
        }
        return removeDuplicateKeysFromMapBeforeNotification(changedProfileKeys);
    }

    // Some profile keys are mutually dependent. So send only 1 of them to the
    // notification layer if multiple of them exist.
    private static HashMap<String, RobotProfileValueChangedStatus> removeDuplicateKeysFromMapBeforeNotification(
            HashMap<String, RobotProfileValueChangedStatus> changedProfileKeys) {

        boolean isRobotCurrentStateChanged = changedProfileKeys.containsKey(ProfileAttributeKeys.ROBOT_CURRENT_STATE);
        boolean isRobotCurrentStateDetailsChanged = changedProfileKeys
                .containsKey(ProfileAttributeKeys.ROBOT_CURRENT_STATE_DETAILS);

        if (isRobotCurrentStateChanged && isRobotCurrentStateDetailsChanged) {
            changedProfileKeys.remove(ProfileAttributeKeys.ROBOT_CURRENT_STATE);
        }

        return changedProfileKeys;
    }
    
}
