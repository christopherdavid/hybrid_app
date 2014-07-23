package com.neatorobotics.android.slide.framework.robot.drive;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationConstants;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationUtil;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeKeys;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;

public class RobotDriveHelper {

    private static final String TAG = RobotDriveHelper.class.getSimpleName();

    public static ConnectionAllowedStatus getConnectionAllowedStatus(Context context, String robotId) {
        ConnectionAllowedStatus driveStatus = isDriveRobotAllowed(context, robotId);
        return driveStatus;
    }

    public static void getRobotNetworkInfo(Context context, String robotId, WebServiceBaseRequestListener listener) {
        LogHelper.logD(TAG, "getNetInfoAndConnect - RobotSerialId = " + robotId);
        ArrayList<String> keys = new ArrayList<String>();
        keys.add(ProfileAttributeKeys.ROBOT_NETWORK_INFO);
        RobotManager.getInstance(context).getRobotProfileDetails(robotId, keys, listener);
    }

    public static void driveRobot(Context context, String robotId, String navigationControlId) {
        HashMap<String, String> commandParams = new HashMap<String, String>();
        commandParams.put(RobotCommandPacketConstants.KEY_NAVIGATION_CONTROL_ID, navigationControlId);
        LogHelper.logD(TAG, "Direct connection exists. Send drive command.");
        RobotCommandServiceManager.sendCommandToPeer(context, robotId, RobotCommandPacketConstants.COMMAND_DRIVE_ROBOT,
                commandParams);
    }

    // Disconnect the connection.
    public static void stopRobotDrive(Context context, String robotId) {
        LogHelper.logD(TAG, "stopRobotDrive - RobotSerialId = " + robotId);
        RobotCommandServiceManager.disconnectDirectConnection(context, robotId);
    }

    // driveRobotIp can be null
    public static void robotReadyToDrive(final Context context, final String robotId, String driveRobotIp) {
        LogHelper.logD(TAG, "robotReadyToDrive robotId: " + robotId);
        RobotCommandServiceManager.tryDirectConnectionWithIp(context, robotId, driveRobotIp,
                new RobotPeerConnectionListener() {

                    @Override
                    public void onRobotDisconnected(String robotId) {
                        HashMap<String, String> data = new HashMap<String, String>();
                        RobotNotificationUtil.notifyDataChanged(context, robotId,
                                RobotNotificationConstants.ROBOT_IS_DISCONNECTED, data);
                    }

                    @Override
                    public void onRobotConnected(String robotId) {
                        HashMap<String, String> data = new HashMap<String, String>();
                        RobotNotificationUtil.notifyDataChanged(context, robotId,
                                RobotNotificationConstants.ROBOT_IS_CONNECTED, data);
                    }

                    @Override
                    public void errorInConnecting(String robotId) {
                        // Multiple times try?
                        notifyCannotDriveRobot(context, robotId,
                                RobotDriveStatusCodes.RESPONSE_CODE_ERROR_IN_CONNECTION);
                    }
                });
    }

    // Robot Status Codes to be sent to the Plugin Layer.
    // These includes status codes which can be sent by Robot
    // These status codes will be sent VIA notifyCannotDriveRobot
    private static class RobotDriveStatusCodes {
        // Received when robot is already connected to some user
        @SuppressWarnings("unused")
        public static final int RESPONSE_CODE_ROBOT_CONNECTED_TO_OTHER_USER = 10001;
        // Received when robot new connection is established.
        public static final int RESPONSE_CODE_ERROR_IN_CONNECTION = 10005;
    }

    // Currently checks if any direct connection exists with any robot from
    // smartapp
    // Add other cases whenever necessary
    private static ConnectionAllowedStatus isDriveRobotAllowed(Context context, String robotId) {
        if (RobotCommandServiceManager.isDirectConnectionExists(context)) {
            int errorType = ErrorTypes.DIFFERENT_ROBOT_ALREADY_CONNECTED;
            // These are just debug messages, No need to localize them
            String errorMsg = "Different robot is currrently being driven";
            if (RobotCommandServiceManager.isRobotDirectConnected(context, robotId)) {
                errorType = ErrorTypes.ROBOT_ALREADY_CONNECTED;
                errorMsg = "Robot is already connected";
            }
            return new ConnectionAllowedStatus(false, errorType, errorMsg);
        }
        return new ConnectionAllowedStatus(true);
    }

    // Helper method to send plugin notification that robot drive request
    // failed.
    private static void notifyCannotDriveRobot(Context context, String robotId, int errorResponseCode) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(JsonMapKeys.ERROR_DRIVE_RESPONSE_CODE, String.valueOf(errorResponseCode));
        RobotNotificationUtil.notifyDataChanged(context, robotId, RobotNotificationConstants.ROBOT_ERROR_IN_CONNECTING,
                data);
    }

    public static class ConnectionAllowedStatus {

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

        public boolean isConnectionAllowed() {
            return mStatus;
        }

        public int getErrorCode() {
            return mErrorCode;
        }

        public String getErrorMessage() {
            return mErrorMessage;
        }
    }

}
