package com.neatorobotics.android.slide.framework.plugins.requests.robot.manual;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotNetworkInfo;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.drive.RobotDriveHelper;
import com.neatorobotics.android.slide.framework.utils.NetworkConnectionUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3.ProfileAttributeKeys;

public class ConnectRobotRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        connectRobot(mContext, jsonData, callbackId);
    }

    private void connectRobot(final Context context, RobotJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "connectRobot is called");
        final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);

        if (!NetworkConnectionUtils.isConnectedOverWiFi(context)) {
            LogHelper.logD(TAG, "Wifi connection isn't being used. Cannot drive");
            sendError(callbackId, ErrorTypes.ERROR_TYPE_WIFI_NOT_CONNECTED, "Drive Robot needs wifi connection");
            return;
        }

        RobotDriveHelper.getInstance(context).getRobotNetworkInfo(robotId,
                new RobotSetProfileDataRequestListener(callbackId) {
                    @Override
                    public void onReceived(NeatoWebserviceResult responseResult) {
                        if ((responseResult != null) && (responseResult instanceof GetRobotProfileDetailsResult2)) {
                            GetRobotProfileDetailsResult2 result = (GetRobotProfileDetailsResult2) responseResult;
                            RobotNetworkInfo info = result.getProfileParameterValue(RobotNetworkInfo.class,
                                    ProfileAttributeKeys.ROBOT_NETWORK_INFO);

                            if ((info != null) && (info.isValid())) {
                                LogHelper.log(TAG, "Network Information is valid, trying connection");
                                NeatoPrefs.saveDriveSecureKey(mContext, info.robotDirectConnectSecret);

                                // TODO: This can be removed once we remove the
                                // support for intend to drive
                                // all together.
                                RobotDriveHelper.getInstance(mContext).trackRobotDriveRequest(robotId);
                                RobotDriveHelper.getInstance(mContext).robotReadyToDrive(robotId, info.robotIpAddress);
                                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
                                sendSuccessPluginResult(pluginResult, callbackId);
                            } else {
                                LogHelper.log(TAG, "Network Information is not valid, returning error");
                                sendError(callbackId, ErrorTypes.ERROR_TYPE_NETWORK_INFO_NOT_SET,
                                        "Network information is not set ny the robot");
                            }
                        }
                    }
                });
    }
}
