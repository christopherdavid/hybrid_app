package com.neatorobotics.android.slide.framework.timemode;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robotdata.RobotDataManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RobotCommandTimerHelper {
	
	private static final String TAG = RobotCommandTimerHelper.class.getSimpleName();
	
	private static final long DEFAULT_COMMAND_EXPIRY_TIMEOUT = 3 * 60 * 1000;
	
	private static final Object INSTANCE_LOCK = new Object();
	private HashMap<String, CommandTimer> mTimerMap;
	Context mContext;
	
	BroadcastReceiver mCommandExpiryAlarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LogHelper.log(TAG, "NetworkConnectionMgr:onReceive called");
			
			LogHelper.log(TAG, "No Network");
		}
	};
	
	private static RobotCommandTimerHelper sRobotCommandTimerHelper;
	
	private RobotCommandTimerHelper(Context context)
	{
		mContext = context.getApplicationContext();
		mTimerMap = new HashMap<String, RobotCommandTimerHelper.CommandTimer>();
	}

	public static RobotCommandTimerHelper getInstance(Context context)
	{
		synchronized (INSTANCE_LOCK) {
			if (sRobotCommandTimerHelper == null) {
				sRobotCommandTimerHelper = new RobotCommandTimerHelper(context);
			}
		}
		return sRobotCommandTimerHelper;
	}
	
	
	public void startCommandExpiryTimer(String robotId) {
		setCommandExpiryTimer(robotId, DEFAULT_COMMAND_EXPIRY_TIMEOUT, 0);
	}
	
	private void setCommandExpiryTimer(String robotId, long timeout, int offset) {
		if (!isTimerRunning(mContext, robotId)) {
			LogHelper.logD(TAG, "setCommandTimer called for robotId: " + robotId);
			CommandTimer robotTimer = new CommandTimer(mContext, robotId, timeout, offset);
			robotTimer.startTimer();
			mTimerMap.put(robotId, robotTimer);
			LogHelper.logD(TAG, "Command Timer started for robotId: " + robotId);
		}
		else {
			LogHelper.logD(TAG, "Timer is already running for the robotId: " + robotId);
		}
	}
	

	public void stopCommandTimerIfRunning(String robotId) {
		CommandTimer timer = mTimerMap.get(robotId);
		if (timer != null) {
			LogHelper.logD(TAG, "stopCommandTimerIfRunning called for robotId: " + robotId);
			timer.stopTimer();
			removeTimer(robotId);
		} 
		else {
			LogHelper.logD(TAG, "Cannot stop timer as timer not running for robotId: " + robotId);
		}
	}
	
	private void removeTimer(String robotId) {
		mTimerMap.remove(robotId);
	}
	
	private boolean isTimerRunning(Context context, String robotId) {
		return mTimerMap.containsKey(robotId);
	}
	
	class CommandTimer {
		long mTime;
		int mOffset;
		String mRobotId;
		Context mContext;
		Timer mTimer;
		
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
			RobotDataManager.onCommandExpired(mContext, mRobotId);
		}
		
		public void stopTimer() {
			LogHelper.logD(TAG, "stopTimer called for robotId: " + mRobotId);
			mTimer.cancel();
		}
	}
}
