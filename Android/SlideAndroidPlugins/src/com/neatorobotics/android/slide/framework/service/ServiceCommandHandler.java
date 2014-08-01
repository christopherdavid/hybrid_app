package com.neatorobotics.android.slide.framework.service;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RequestPacket;
import com.neatorobotics.android.slide.framework.robotdata.RobotDataManager;
import com.neatorobotics.android.slide.framework.timedmode.RobotCommandTimerHelper;

public class ServiceCommandHandler {

    private static final String TAG = ServiceCommandHandler.class.getSimpleName();

    static boolean isDataChangedCommand(RequestPacket request) {
        int commandId = request.getCommand();
        return (commandId == RobotCommandPacketConstants.COMMAND_ROBOT_PROFILE_DATA_CHANGED);
    }

    static void processDataChangedRequest(Context context, String from, RequestPacket request) {
        LogHelper.log(TAG, "CommandTrip: Data changed on server for robot");
        String robotId = request.getCommandParam(RobotCommandPacketConstants.KEY_ROBOT_ID);

        String causeAgentId = request.getCommandParam(RobotCommandPacketConstants.KEY_CAUSE_AGENT_ID);
        if (!TextUtils.isEmpty(causeAgentId)) {
            if (causeAgentId.equals(NeatoPrefs.getNeatoUserDeviceId(context))) {
                LogHelper.log(TAG, "CommandTrip: Causing Agent Matched. Ignore Data changed notification");
                return;
            }
        }

        if (causeAgentId.equalsIgnoreCase(robotId)) {
            LogHelper.logD(TAG,
                    "CommandTrip: packet is received from robot cause agent id. Stop the command expiry timer if running.");
            RobotCommandTimerHelper.getInstance(context).stopCommandTimerIfRunning(robotId);
        } else {
            LogHelper.logD(TAG, "CommandTrip: packet is not received from robot chat id.");
        }

        RobotDataManager.getServerData(context, robotId);
    }
}
