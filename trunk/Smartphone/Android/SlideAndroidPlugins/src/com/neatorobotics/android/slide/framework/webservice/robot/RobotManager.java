package com.neatorobotics.android.slide.framework.webservice.robot;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.content.Context;

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
	
	public void getRobotDetail(final String serialId, RobotDetailListener listener)
	{
		final WeakReference<RobotDetailListener> robotDetailListenerWeakRef = new WeakReference<RobotDetailListener>(listener);
		Runnable task = new Runnable() {
			
			public void run() {
				RobotDetailResult result = NeatoRobotWebservicesHelper.getRobotDetail(mContext, serialId);
				RobotDetailListener robotListener = robotDetailListenerWeakRef.get();
				if (robotListener == null) {
					LogHelper.logD(TAG, "Callback interface is null in getRobotDetail");
					return;
				}
				if (result != null && result.success()) {
					RobotItem robotItem = convertRobotDetailResultToRobotItem(result);
					if (robotListener != null) {
						robotListener.onRobotDetailReceived(robotItem);
					}
				}
				else {
					if (robotListener != null) {
						robotListener.onServerError();
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
		Runnable task = new Runnable() {
			public void run() {
				HashMap<String, String> profileParams = new HashMap<String, String>();
				profileParams.put(SetRobotProfileDetails.Attribute.ROBOT_NAME, robotName);
				SetRobotProfileDetailsResult result = NeatoRobotWebservicesHelper.setRobotProfileDetailsRequest(mContext, robotId, profileParams);
				if (result != null) {
					if(result.success()) {
						listener.onSetProfileSuccess();
					} else {
						listener.onSetProfileServerError(result.mMessage);
					}
				} else {
					listener.onSetProfileNetworkError("Network Error");
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
