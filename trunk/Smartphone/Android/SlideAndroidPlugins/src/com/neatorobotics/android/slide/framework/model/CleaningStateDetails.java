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
public class CleaningStateDetails extends JSONObject {

    private static final String TAG = CleaningStateDetails.class.getSimpleName();

    public CleaningStateDetails(String cleaningStateDetails) throws JSONException {
        super(cleaningStateDetails);
    }

    public int getCleaningCategory() {
        String category = getParam(ProfileAttributeValueKeys.ROBOT_CLEANING_CATEGORY);
        if (!TextUtils.isEmpty(category)) {
            return Integer.parseInt(category);
        }
        // TODO: Expose enum for cleaning modifiers. Currently returns 2 i.e.
        // All
        return RobotCommandPacketConstants.CLEANING_CATEGORY_INVALID;
    }

    private JSONObject getStateParams() {
        JSONObject stateParams = new JSONObject();
        try {
            stateParams = getJSONObject(ProfileAttributeValueKeys.ROBOT_STATE_PARAMS);
        } catch (JSONException e) {
			LogHelper.log(TAG, "Exception in getStateParams", e);
        }
        return stateParams;
    }

    private String getParam(String paramKey) {
        try {
			JSONObject stateParams = getStateParams();
            if (stateParams != null) {
                String category = stateParams.getString(paramKey);
                return category;
            }
        } catch (JSONException e) {
            LogHelper.log(TAG, "Exception in getParam", e);
        }
        return null;
    }
}
