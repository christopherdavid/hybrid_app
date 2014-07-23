package com.neatorobotics.android.slide.framework.plugins.requests.robot.manual;

import org.json.JSONArray;

import android.content.Context;

import com.neatorobotics.android.slide.framework.R;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.drive.RobotDriveHelper;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;

public class RobotDriveRequest extends RobotManagerRequest {

    @Override
    public void execute(String action, JSONArray data, String callbackId) {
        RobotJsonData jsonData = new RobotJsonData(data);
        driveRobot(mContext, jsonData, callbackId);
    }

    private void driveRobot(Context context, RobotJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "driveRobot action initiated in Robot plugin");
        String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
        String navigationControlId = jsonData.getString(JsonMapKeys.KEY_NAVIGATION_CONTROL_ID);
        LogHelper.logD(TAG, "Params\n\tRobotId=" + robotId);
        LogHelper.logD(TAG, "\n\tNavigation Control Id = " + navigationControlId);

        if (!RobotCommandServiceManager.isRobotDirectConnected(context, robotId)) {
            LogHelper.logD(TAG, "Drive Robot action cannot complete as robot connection does not exist");
            String errMessage = context.getString(R.string.error_robot_not_directly_connected);
            sendError(callbackId, ErrorTypes.ROBOT_NOT_CONNECTED, errMessage);
            return;
        }
        RobotDriveHelper.driveRobot(context, robotId, navigationControlId);
    }
}
