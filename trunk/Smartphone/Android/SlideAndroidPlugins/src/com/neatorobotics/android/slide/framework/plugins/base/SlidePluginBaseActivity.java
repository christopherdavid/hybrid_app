package com.neatorobotics.android.slide.framework.plugins.base;

import org.apache.cordova.DroidGap;

import android.os.Bundle;

import com.neatorobotics.android.slide.framework.NeatoServiceManager;
import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class SlidePluginBaseActivity extends DroidGap {

	private static final String TAG = SlidePluginBaseActivity.class.getSimpleName();
	private NeatoServiceManager mServiceManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		LogHelper.logD(TAG, "onCreate called");
		super.onCreate(savedInstanceState);
		
		mServiceManager = new NeatoServiceManager(getApplicationContext());
		
		mServiceManager.initialize();
	}
	
	@Override
	public void onDestroy() {

		mServiceManager.uninitialize();
		
		super.onDestroy();
	}
}
