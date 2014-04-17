package com.neatorobotics.android.slide.framework.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeValueKeys;

/**
 * Model class with all the state details. Please add methods as and when new
 * details are added. <br>
 */
public class CleaningStateDetails {

    private static final String TAG = CleaningStateDetails.class.getSimpleName();

    public String robotCurrentState;
    public String robotStateParams;

    public int getCleaningCategory() {

        String category = getParam(ProfileAttributeValueKeys.ROBOT_CLEANING_CATEGORY);
        if (!TextUtils.isEmpty(category)) {
            return Integer.parseInt(category);
        }
        // TODO: Expose enum for cleaning modifiers. Currently returns 2 i.e.
        // All
        return RobotCommandPacketConstants.CLEANING_CATEGORY_ALL;

    }

    private JSONObject getParams() {
        if (!TextUtils.isEmpty(robotStateParams)) {
            try {
                return new JSONObject(robotStateParams);
            } catch (JSONException e) {
                LogHelper.log(TAG, "Exception in getParams", e);
            }
        }
        return new JSONObject();
    }

    private String getParam(String paramKey) {
        try {
            if (getParams() != null) {
                String category;
                category = getParams().getString(paramKey);
                return category;
            }
        } catch (JSONException e) {
            LogHelper.log(TAG, "Exception in getParam", e);
        }
        return null;
    }
}
