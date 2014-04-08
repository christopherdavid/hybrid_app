package com.neatorobotics.android.slide.framework.gcm;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebservicesHelper;
import com.neatorobotics.android.slide.framework.webservice.user.RegisterPushNotificationResult;
import com.neatorobotics.android.slide.framework.webservice.user.UnregisterPushNotificationResult;

import android.app.IntentService;
import android.content.Intent;

public class PushNotificationTaskIntentService extends IntentService{
	private static final String TAG = PushNotificationTaskIntentService.class.getSimpleName();
	
	private static final String SERVICE_NAME = "GCMTaskIntentService";
	static final String REGISTRATION_ID = "registrationId";
	static final String KEY_IS_REGISTER = "isRegister";
	
	public PushNotificationTaskIntentService() {
		super(SERVICE_NAME);
	}	

	@Override
	protected void onHandleIntent(Intent intent) {
		LogHelper.logD(TAG, "PushNotificationTaskIntentService called");
		boolean isRegister = intent.getBooleanExtra(KEY_IS_REGISTER, false);
		String registrationId = intent.getStringExtra(REGISTRATION_ID);
		if (isRegister) {
			registerWithNeatoServer(registrationId);
		} else {
			unregisterWithNeatoServer(registrationId);
		}
	}
	
	private void registerWithNeatoServer(String registrationId) {
		String email = NeatoPrefs.getUserEmailId(getApplicationContext());
		RegisterPushNotificationResult result = null;
		try {
			result = NeatoUserWebservicesHelper.registerPushNotification(this, email, PushNotificationUtils.getDeviceType(), registrationId);
		} catch (Exception e) {
			LogHelper.logD(TAG, "Exception message " + e.getMessage());
		}
		
		if (result == null ) {
			LogHelper.logD(TAG, "Unable to send reg key. Result is null");
			return;
		}
		if(result.success()) {
			LogHelper.logD(TAG, "Registered on server");
		} else {
			LogHelper.logD(TAG, "Unable to send reg key " + result.message);
		}
		
	}
	
	private void unregisterWithNeatoServer(String registrationId) {
		UnregisterPushNotificationResult result = null;
		try {
			result = NeatoUserWebservicesHelper.unregisterPushNotification(this, registrationId);
		} catch (Exception e) {
			LogHelper.logD(TAG, "Exception message " + e.getMessage());
		}
		if (result == null ) {
			LogHelper.logD(TAG, "Unable to unregister from server Result is null");
			return;
		}
		if(result.success()) {
			LogHelper.logD(TAG, "Push notification unregistered from server");
		} else {
			LogHelper.logD(TAG, "Unable to unregister from server " + result.message);
		}
	}

}
