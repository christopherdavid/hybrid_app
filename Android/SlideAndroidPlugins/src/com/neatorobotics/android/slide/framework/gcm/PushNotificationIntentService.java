package com.neatorobotics.android.slide.framework.gcm;

import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gcm.GCMBaseIntentService;
import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class PushNotificationIntentService extends GCMBaseIntentService {
	private static final String TAG = PushNotificationIntentService.class.getSimpleName();
	
	public PushNotificationIntentService() {
		super(PushNotificationConstants.SENDER_ID);
	}

	@Override
	protected void onError(Context context, String error) {
		LogHelper.log(TAG, "Register Push Notification failed with error " + error);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		LogHelper.logD(TAG, "onMessage called");
		final Bundle pushNotificationBundle = intent.getExtras();
		if(pushNotificationBundle != null) {
			Set<String> keys = pushNotificationBundle.keySet();
			for(String key: keys) {
				Object keyValue = pushNotificationBundle.get(key);
				LogHelper.logD(TAG, " key = " + key + " \t value = " + keyValue);
			}
		}		
		PushNotificationMessageHandler.getInstance(context).processPushMessageAndNotify(context, pushNotificationBundle);
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		LogHelper.logD(TAG, "Registration Id: " + regId);
		PushNotificationUtils.registerForPushNotificationOnNeatoServer(context, regId);
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		LogHelper.logD(TAG, "onUnregistered Id: " + regId);
		PushNotificationUtils.unregisterPushNotification(context, regId);
	}
	
}
