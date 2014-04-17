package com.neatorobotics.android.slide.framework.robot.drive;

import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeValueKeys;

public class RobotAvailabilityToDriveStatus {

    private static final String TAG = RobotAvailabilityToDriveStatus.class.getSimpleName();

    private boolean mAvailableStatus;
    private String mRobotDriveIp;
    private int mDriveErrorCode;

    private RobotAvailabilityToDriveStatus(boolean driveStatus, String robotIp) {
        mAvailableStatus = driveStatus;
        mRobotDriveIp = robotIp;
    }

    private RobotAvailabilityToDriveStatus(boolean driveStatus, int driveErrorCode) {
        mAvailableStatus = driveStatus;
        mDriveErrorCode = driveErrorCode;
    }

    public static RobotAvailabilityToDriveStatus getAvailabilityStatus(String availableToDriveResponse) {
        JSONObject object;
        try {
            object = new JSONObject(availableToDriveResponse);
            boolean driveStatus = object.getBoolean(ProfileAttributeValueKeys.DRIVE_AVAILABLE_STATUS);
            if (driveStatus) {
                String robotIp = object.getString(ProfileAttributeValueKeys.ROBOT_IP_ADDRESS);
                return new RobotAvailabilityToDriveStatus(true, robotIp);
            } else {
                int reasonCode = object.getInt(ProfileAttributeValueKeys.ERROR_DRIVE_REASON_CODE);
                return new RobotAvailabilityToDriveStatus(false, reasonCode);
            }
        } catch (JSONException e) {
            LogHelper.log(TAG, "Exception in getAvailableToDriveResponse", e);
        }
        return null;
    }

    public boolean isRobotAvailableToDrive() {
        return mAvailableStatus;
    }

    public String getRobotDriveIp() {
        return mRobotDriveIp;
    }

    public int getDriveErrorCode() {
        return mDriveErrorCode;
    }
}
