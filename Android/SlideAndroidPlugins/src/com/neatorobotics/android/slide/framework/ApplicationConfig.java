package com.neatorobotics.android.slide.framework;

import android.content.Context;

import com.neatorobotics.android.slide.framework.resultreceiver.NeatoRobotResultReceiver;
import com.neatorobotics.android.slide.framework.service.INeatoRobotService;

public class ApplicationConfig {

	private static final String TAG = ApplicationConfig.class.getSimpleName();
	private Context mContext;
	private static ApplicationConfig sApplicationConfig;
	private static final Object INSTANCE_LOCK = new Object();

	private INeatoRobotService mNeatoRobotService;
	private NeatoRobotResultReceiver mNeatoRobotResultReceiver;
//	private InitHelper mInitHelper;

	private ApplicationConfig(Context context)
	{
		mContext = context.getApplicationContext();
	}

	public static ApplicationConfig getInstance(Context context)
	{
		synchronized (INSTANCE_LOCK) {
			if (sApplicationConfig == null) {
				sApplicationConfig = new ApplicationConfig(context);
			}
		}

		return sApplicationConfig;
	}

	public INeatoRobotService getRobotService()
	{
		return mNeatoRobotService;
	}

	public void setRobotService(INeatoRobotService service)
	{
		mNeatoRobotService = service;
	}

	public void setRobotResultReceiver(NeatoRobotResultReceiver receiver) {
		mNeatoRobotResultReceiver = receiver;
	}

	public NeatoRobotResultReceiver getRobotResultReceiver() {
		return mNeatoRobotResultReceiver;
	}
	
//	public InitHelper getinitHelper() {
//		return mInitHelper;
//	}
//	
//	public void setInitHelper(InitHelper initHelper) {
//		mInitHelper = initHelper;
//	}
}
