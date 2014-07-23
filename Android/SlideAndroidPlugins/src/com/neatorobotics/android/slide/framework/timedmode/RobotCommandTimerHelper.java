package com.neatorobotics.android.slide.framework.timedmode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationConstants;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationUtil;
import com.neatorobotics.android.slide.framework.robotdata.RobotDataManager;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;

import android.content.Context;

public class RobotCommandTimerHelper {

    private static final String TAG = RobotCommandTimerHelper.class.getSimpleName();
    private static final long DEFAULT_COMMAND_EXPIRY_TIMEOUT = 1 * 60 * 1000;
    private static final Object INSTANCE_LOCK = new Object();
    private TreeMap<String, CommandTimer> mTimerMap;
    private Context mContext;

    private static RobotCommandTimerHelper sRobotCommandTimerHelper;

    private RobotCommandTimerHelper(Context context) {
        mContext = context.getApplicationContext();
        mTimerMap = new TreeMap<String, RobotCommandTimerHelper.CommandTimer>(String.CASE_INSENSITIVE_ORDER);
    }

    public static RobotCommandTimerHelper getInstance(Context context) {
        synchronized (INSTANCE_LOCK) {
            if (sRobotCommandTimerHelper == null) {
                sRobotCommandTimerHelper = new RobotCommandTimerHelper(context);
            }
        }
        return sRobotCommandTimerHelper;
    }

    public void startCommandExpiryTimer(String robotId, int commandId) {
        setCommandExpiryTimer(robotId, commandId, DEFAULT_COMMAND_EXPIRY_TIMEOUT, 0);
    }

    private synchronized void setCommandExpiryTimer(String robotId, int commandId, long timeout, int offset) {
        if (!isTimerRunning(mContext, robotId)) {
            LogHelper.logD(TAG, "setCommandTimer called for robotId: " + robotId);
            CommandTimer robotTimer = new CommandTimer(mContext, robotId, timeout, offset);
            robotTimer.startTimer();
            mTimerMap.put(robotId, robotTimer);
            LogHelper.logD(TAG, "Command Timer started for robotId: " + robotId);
        } else {
            LogHelper.logD(TAG, "Timer is already running for the robotId: " + robotId);
        }
        trackCommand(robotId, commandId);
    }

    private synchronized void trackCommand(String robotId, int commandId) {
        CommandTimer timer = getCommandTimer(robotId);
        if (timer != null) {
            timer.addCommand(commandId);
        }
    }

    public synchronized void stopCommandTimerIfRunning(String robotId) {
        CommandTimer timer = getCommandTimer(robotId);
        if (timer != null) {
            LogHelper.logD(TAG, "stopCommandTimerIfRunning called for robotId: " + robotId);
            timer.stopTimer();
            timer.clearTrackedCommands();
            removeTimer(robotId);
        } else {
            LogHelper.logD(TAG, "Cannot stop timer as timer not running for robotId: " + robotId);
        }
    }

    private synchronized CommandTimer getCommandTimer(String robotId) {
        return mTimerMap.get(robotId);
    }

    public synchronized void stopAllCommandTimers() {
        if (mTimerMap != null) {
            for (String robotId : mTimerMap.keySet()) {
                CommandTimer timer = getCommandTimer(robotId);
                timer.stopTimer();
            }
            mTimerMap.clear();
        }
    }

    private synchronized void removeTimer(String robotId) {
        mTimerMap.remove(robotId);
    }

    private synchronized boolean isTimerRunning(Context context, String robotId) {
        return mTimerMap.containsKey(robotId);
    }

    class CommandTimer {
        long mTime;
        int mOffset;
        String mRobotId;
        Context mContext;
        Timer mTimer;

        ArrayList<Integer> trackCommandList = new ArrayList<Integer>();

        public CommandTimer(Context context, String robotId, long time, int offset) {
            mContext = context;
            mTime = time;
            mOffset = offset;
            mRobotId = robotId;
            mTimer = new Timer();
        }

        public void startTimer() {
            LogHelper.logD(TAG, "startTimer called for robotId: " + mRobotId);
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    onTimerExpired();
                }
            }, mTime);
        }

        private void onTimerExpired() {
            LogHelper.logD(TAG, "onTimerExpired called for robotId: " + mRobotId);
            removeTimer(mRobotId);
            RobotDataManager.onCommandExpired(mContext, mRobotId, new WebServiceBaseRequestListener() {

                @Override
                public void onServerError(int errorType, String errMessage) {
                    // TODO: Retry clearing?
                }

                @Override
                public void onReceived(NeatoWebserviceResult responseResult) {
                    for (Integer commandId : trackCommandList) {
                        notifyFailure(commandId);
                    }
                }

                public void notifyFailure(int commandId) {
                    LogHelper.log(TAG, "Notifying that the command has failed" + commandId);
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put(JsonMapKeys.FAILED_COMMAND_ID, String.valueOf(commandId));
                    RobotNotificationUtil.notifyDataChanged(mContext, mRobotId,
                            RobotNotificationConstants.ROBOT_COMMAND_FAILED, data);
                }

                @Override
                public void onNetworkError(String errMessage) {
                    // TODO: Retry clearing?
                }
            });
        }

        public void stopTimer() {
            LogHelper.logD(TAG, "stopTimer called for robotId: " + mRobotId);
            mTimer.cancel();
        }

        public void addCommand(int commandId) {
            LogHelper.log(TAG, "Adding the command to track " + commandId);
            trackCommandList.add(commandId);
        }

        public void clearTrackedCommands() {
            LogHelper.log(TAG, "Clearing the tracked commands for robotId " + mRobotId);
            trackCommandList.clear();
        }
    }
}
