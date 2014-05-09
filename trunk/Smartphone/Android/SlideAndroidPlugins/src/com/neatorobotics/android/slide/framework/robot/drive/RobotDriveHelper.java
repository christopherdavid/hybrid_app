package com.neatorobotics.android.slide.framework.robot.drive;

import java.util.HashMap;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationConstants;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationUtil;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.robotdata.RobotDataManager;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeKeys;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeValueKeys;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;

public class RobotDriveHelper {

    private static final String TAG = RobotDriveHelper.class.getSimpleName();

    private static final int DEFAULT_ROBOT_DRIVE_WIFI_ON_TIME = 2 * 60 * 1000; // 2
                                                                               // minutes

    private TreeMap<String, Boolean> mPendingIntendDriveRequests;

    private Context mContext;

    private static RobotDriveHelper sRobotDriveHelper;
    private static final Object INSTANCE_LOCK = new Object();

    private RobotDriveHelper(Context context) {
        mContext = context.getApplicationContext();
        mPendingIntendDriveRequests = new TreeMap<String, Boolean>(String.CASE_INSENSITIVE_ORDER);
    }

    public static RobotDriveHelper getInstance(Context context) {
        synchronized (INSTANCE_LOCK) {
            if (sRobotDriveHelper == null) {
                sRobotDriveHelper = new RobotDriveHelper(context);
            }
        }
        return sRobotDriveHelper;
    }

    public void setRobotDriveRequest(String robotId, WebServiceBaseRequestListener listener) {
        LogHelper.logD(TAG, "setRobotDriveRequest - RobotSerialId = " + robotId);

        ConnectionAllowedStatus driveStatus = isDriveRobotAllowed(robotId);
        if (driveStatus.isConnectionAllowed()) {
            // Send intend to drive in the profile parameter of the robot along
            // with
            // the wifi_on time and cause agent id.
            String driveDetail = getDriveRequestParams(DEFAULT_ROBOT_DRIVE_WIFI_ON_TIME);
            RobotDataManager.setRobotProfileParam(mContext, robotId, RobotCommandPacketConstants.COMMAND_INTEND_TO_DRIVE, driveDetail,
                    listener);
        } else {
            // Drive denied. Send error result
            LogHelper.logD(TAG, "setRobotDriveRequest - denied" + driveStatus.getErrorMessage());
            // Server error?
            listener.onServerError(driveStatus.getErrorCode(), driveStatus.getErrorMessage());
            return;
        }
    }

    public void cancelRobotDriveRequest(String robotId, WebServiceBaseRequestListener listener) {
        LogHelper.logD(TAG, "cancelRobotDriveRequest - RobotSerialId = " + robotId);

        // If the user is already connected to the robot VIA direct connection,
        // return with a error.
        if (RobotCommandServiceManager.isRobotDirectConnected(mContext, robotId)) {
            listener.onServerError(ErrorTypes.ROBOT_ALREADY_CONNECTED,
                    "Robot already connected cannot cancel intend to drive");
            return;
        }

        if (isRobotDriveRequested(robotId)) {
            LogHelper.logD(TAG, "Robot request exists. Cancelling = " + robotId);
            RobotDataManager.deleteProfileDetailKey(mContext, robotId, ProfileAttributeKeys.INTEND_TO_DRIVE, true,
                    listener);
        } else {
            listener.onServerError(ErrorTypes.ROBOT_NO_DRIVE_REQUEST_FOUND, "No robot drive request found");
            return;
        }
    }

    public void driveRobot(String robotId, String navigationControlId) {
        HashMap<String, String> commandParams = new HashMap<String, String>();
        commandParams.put(JsonMapKeys.KEY_NAVIGATION_CONTROL_ID, navigationControlId);
        LogHelper.logD(TAG, "Direct connection exists. Send drive command.");
        RobotCommandServiceManager.sendCommandToPeer(mContext, robotId,
                RobotCommandPacketConstants.COMMAND_DRIVE_ROBOT, commandParams);
    }

    // Disconnect the connection.
    public void stopRobotDrive(String robotId) {
        LogHelper.logD(TAG, "stopRobotDrive - RobotSerialId = " + robotId);
        RobotCommandServiceManager.disconnectDirectConnection(mContext, robotId);
    }

    public boolean isRobotDriving(String robotId) {
        return RobotCommandServiceManager.isRobotDirectConnected(mContext, robotId);
    }

    // Function to add to the map that we have requested intend to drive
    public void trackRobotDriveRequest(String robotId) {
        mPendingIntendDriveRequests.put(robotId, true);
    }

    // Function to remove the request from the map
    public void untrackRobotDriveRequest(String robotId) {
        mPendingIntendDriveRequests.remove(robotId);
    }

    public void untrackAllRobotDriveRequest() {
        mPendingIntendDriveRequests.clear();
    }

    // Called when our command timer expires.
    // The intend to drive request will also get reset from robotdatamanager.
    public void notifyDriveFailedAsRobotOffline(String robotId) {
        if (isRobotDriveRequested(robotId)) {
            mPendingIntendDriveRequests.remove(robotId);
            notifyCannotDriveRobot(robotId, RobotDriveStatusCodes.RESPONSE_CODE_ROBOT_OFFLINE);
        }
    }

    // Called when robot signals not available to drive.
    // It also sends error codes.
    // See RobotDriveStatusCodes for various codes.
    public void notifyRobotNotAvailableForDrive(String robotId, int errorResponseCode) {
        if (isRobotDriveRequested(robotId)) {
            mPendingIntendDriveRequests.remove(robotId);
            notifyCannotDriveRobot(robotId, errorResponseCode);
        }
    }

    // driveRobotIp can be null
    public void robotReadyToDrive(final String robotId, String driveRobotIp) {
        if (isRobotDriveRequested(robotId)) {
            mPendingIntendDriveRequests.remove(robotId);
            LogHelper.logD(TAG, "robotReadyToDrive robotId: " + robotId);
            RobotCommandServiceManager.tryDirectConnectionWithIp(mContext, robotId, driveRobotIp,
                    new RobotPeerConnectionListener() {

                        @Override
                        public void onRobotDisconnected(String robotId) {
                            HashMap<String, String> data = new HashMap<String, String>();
                            RobotNotificationUtil.notifyDataChanged(mContext, robotId,
                                    RobotNotificationConstants.ROBOT_IS_DISCONNECTED, data);
                        }

                        @Override
                        public void onRobotConnected(String robotId) {
                            HashMap<String, String> data = new HashMap<String, String>();
                            RobotNotificationUtil.notifyDataChanged(mContext, robotId,
                                    RobotNotificationConstants.ROBOT_IS_CONNECTED, data);
                        }

                        @Override
                        public void errorInConnecting(String robotId) {
                            // Multiple times try?
                            notifyCannotDriveRobot(robotId, RobotDriveStatusCodes.RESPONSE_CODE_ERROR_IN_CONNECTION);
                        }
                    });
        }
    }

    // Robot drive request on server is initiated by some other user
    public void robotDriveRequestInitiated(String robotId, String intendToDrive) {
        // Currently not used.
    }

    // Robot drive request on server is deleted from the server.
    // This would mean that either the intend to drive is successful or
    // cancelled.
    public void robotDriveRequestRemoved(String robotId) {
        // Currently not used.
    }

    // Method to know whether the user has requested robot drive.
    private boolean isRobotDriveRequested(String robotId) {
        boolean isRequested = false;
        if (mPendingIntendDriveRequests.containsKey(robotId)) {
            isRequested = mPendingIntendDriveRequests.get(robotId);
        }
        return isRequested;
    }

    // Robot Status Codes to be sent to the Plugin Layer.
    // These includes status codes which can be sent by Robot
    // These status codes will be sent VIA notifyCannotDriveRobot
    private static class RobotDriveStatusCodes {
        // Received when robot is already connected to some user
        @SuppressWarnings("unused")
        public static final int RESPONSE_CODE_ROBOT_CONNECTED_TO_OTHER_USER = 10001;
        // Received when robot wait for connection is timed-out.
        public static final int RESPONSE_CODE_ROBOT_OFFLINE = 10003;
        // Received when robot new connection is established.
        public static final int RESPONSE_CODE_ERROR_IN_CONNECTION = 10005;
    }

    // Method gives the drive request json value to be set in the profile
    // parameter for intendToDrive.
    private String getDriveRequestParams(int wifi_on_time) {
        JSONObject intendToDrive = new JSONObject();
        try {
            intendToDrive.put(ProfileAttributeValueKeys.ROBOT_WIFI_ON_TIME_IN_MS, wifi_on_time);
            intendToDrive.put(ProfileAttributeValueKeys.DEVICE_ID, NeatoPrefs.getNeatoUserDeviceId(mContext));
        } catch (JSONException e) {
            LogHelper.log(TAG, "Unable to create Drive Details.");
        }
        return intendToDrive.toString();
    }

    // Called before sending intendToDrive to see if the robot drive is allowed
    // or not
    // Currently checks if any direct connection exists with any robot from
    // smartapp
    // Add other cases whenever necessary
    private ConnectionAllowedStatus isDriveRobotAllowed(String robotId) {
        if (RobotCommandServiceManager.isDirectConnectionExists(mContext)) {
            int errorType = ErrorTypes.DIFFERENT_ROBOT_ALREADY_CONNECTED;
            // These are just debug messages, No need to localize them
            String errorMsg = "Different robot is currrently being driven";
            if (RobotCommandServiceManager.isRobotDirectConnected(mContext, robotId)) {
                errorType = ErrorTypes.ROBOT_ALREADY_CONNECTED;
                errorMsg = "Robot is already connected";
            }
            return new ConnectionAllowedStatus(false, errorType, errorMsg);
        }
        return new ConnectionAllowedStatus(true);
    }

    // Helper method to send plugin notification that robot drive request
    // failed.
    private void notifyCannotDriveRobot(String robotId, int errorResponseCode) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(JsonMapKeys.ERROR_DRIVE_RESPONSE_CODE, String.valueOf(errorResponseCode));
        RobotNotificationUtil.notifyDataChanged(mContext, robotId,
                RobotNotificationConstants.ROBOT_ERROR_IN_CONNECTING, data);
    }

    private static class ConnectionAllowedStatus {

        private boolean mStatus;
        private int mErrorCode;
        private String mErrorMessage;

        ConnectionAllowedStatus(boolean status, int errorCode, String message) {
            mStatus = status;
            mErrorCode = errorCode;
            mErrorMessage = message;
        }

        ConnectionAllowedStatus(boolean status) {
            mStatus = status;
        }

        boolean isConnectionAllowed() {
            return mStatus;
        }

        int getErrorCode() {
            return mErrorCode;
        }

        String getErrorMessage() {
            return mErrorMessage;
        }
    }

}
