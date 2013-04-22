package com.neatorobotics.android.slide.framework.plugins.base;

import org.apache.cordova.DroidGap;

import android.os.Bundle;
import com.neatorobotics.android.slide.framework.NeatoServiceManager;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationUtils;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.utils.DeviceUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebConstants;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class SlidePluginBaseActivity extends DroidGap {

	private static final String TAG = SlidePluginBaseActivity.class.getSimpleName();
	private NeatoServiceManager mServiceManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		LogHelper.logD(TAG, "onCreate called");
		super.onCreate(savedInstanceState);
		NeatoWebConstants.setServerEnvironment(NeatoWebConstants.STAGING_SERVER_ID);
		
		mServiceManager = new NeatoServiceManager(getApplicationContext());
		
		mServiceManager.initialize();
		
		
		if(UserHelper.isUserLoggedIn(this)) {
			String authToken = NeatoPrefs.getNeatoUserAuthToken(this);
			UserManager.getInstance(this).setUserAttributesOnServer(authToken, DeviceUtils.getUserAttributes(this));
			PushNotificationUtils.registerForPushNotification(this);
		}
	}
	
	@Override
	public void onDestroy() {

		mServiceManager.uninitialize();
		
		super.onDestroy();
	}
	// Prevent back button from the activity and implement the behaviour in the UI.
	@Override
	public void onBackPressed() {
		LogHelper.logD(TAG, "onBackPressed called");
	}
}
