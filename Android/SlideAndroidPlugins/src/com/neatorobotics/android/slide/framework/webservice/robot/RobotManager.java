package com.neatorobotics.android.slide.framework.webservice.robot;

import java.util.HashMap;

import android.content.Context;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.SetRobotProfileDetails;

public class RobotManager {
	private static final String TAG = RobotManager.class.getSimpleName();
	private Context mContext;
	
	private static RobotManager sRobotManager;
	private static final Object INSTANCE_LOCK = new Object();
	
	private RobotManager(Context context)
	{
		mContext = context.getApplicationContext();
	}
	
	public static RobotManager getInstance(Context context)
	{
		synchronized (INSTANCE_LOCK) {
			if (sRobotManager == null) {
				sRobotManager = new RobotManager(context);
			}
		}
		
		return sRobotManager;
	}
	
	public void getRobotDetail(final String serialId, final RobotDetailListener listener)	{
		Runnable task = new Runnable() {
			
			public void run() {
				RobotDetailResult result = NeatoRobotWebservicesHelper.getRobotDetail(mContext, serialId);				
				if (result != null) {
					if (result.success()) {
						RobotItem robotItem = convertRobotDetailResultToRobotItem(result);
						// Update the robot data if anything changed
						RobotHelper.saveRobotDetails(mContext, robotItem);
						if (listener != null) {
							listener.onRobotDetailReceived(robotItem);
						}
					}
					else {
						if (listener != null) {
							listener.onServerError(result.mMessage);
						}
					}
				}
				else {
					if (listener != null) {
						listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public RobotItem getRobotDetail(final String serialId)
	{
		RobotItem robotItem = null;
		RobotDetailResult result = NeatoRobotWebservicesHelper.getRobotDetail(mContext, serialId);
		if (result != null && result.success()) {
			robotItem = convertRobotDetailResultToRobotItem(result);
		}
		return robotItem;
	}

	public void setRobotName(final String robotId, final String robotName, final SetRobotProfileDetailsListener listener) 
	{
		LogHelper.logD(TAG, "setRobotName called");
		LogHelper.logD(TAG, "Robot Id = " + robotId + " New Name = " + robotName);
		Runnable task = new Runnable() {
			public void run() {
				HashMap<String, String> profileParams = new HashMap<String, String>();
				profileParams.put(SetRobotProfileDetails.Attribute.ROBOT_NAME, robotName);
				SetRobotProfileDetailsResult result = NeatoRobotWebservicesHelper.setRobotProfileDetailsRequest(mContext, robotId, profileParams);
				if (result == null) {
					listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);
					return;
				}
				
				if(result.success()) {
					// Robot Name updated on the server, we now update the name in the database
					RobotItem robotItem = RobotHelper.updateRobotName(mContext, robotId, robotName);	
					listener.onComplete(robotItem);
				}
				else {
					listener.onServerError(result.mMessage);
				}
				
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void getRobotOnlineStatus(final String robotId, final RobotOnlineStatusListener listener) {
		LogHelper.logD(TAG, "getRobotOnlineStatus called for RobotID = " + robotId);
		
		Runnable task = new Runnable() {			
			@Override
			public void run() {
				RobotOnlineStatusResult result = NeatoRobotWebservicesHelper.getRobotOnlineStatus(mContext, robotId);
				if (listener == null) {					
					return;
				}
				
				if (result != null) {
					if (result.success()) {
						listener.onComplete(result.mResult.mOnline);
					}
					else {
						listener.onServerError(result.mMessage);
					}
				}
				else {
					listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);
				}
			}
		};
		
		TaskUtils.scheduleTask(task, 0);
	}
	
	private RobotItem convertRobotDetailResultToRobotItem(RobotDetailResult result)
	{
		RobotItem robotItem = new RobotItem();
		robotItem.setId(result.mResult.mId);
		robotItem.setName(result.mResult.mName);
		robotItem.setSerialNumber(result.mResult.mSerialNumber);
		robotItem.setChatId(result.mResult.mChat_id);
		robotItem.setChatPwd(result.mResult.mChat_pwd);
		return robotItem;
	}

}
