package com.neatorobotics.android.slide.framework.gcm;

import com.google.android.gcm.GCMRegistrar;
import com.neatorobotics.android.slide.framework.logger.LogHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class PushNotificationUtils {
	private static final String TAG = PushNotificationUtils.class.getSimpleName();
	
	private static int ANDROID_DEVICE = 1;

	private static Bundle mPendingPushNotificationBundle;
	public static int getDeviceType() {
		return ANDROID_DEVICE;
	}
	
	public static void registerForPushNotificationOnNeatoServer(Context context, String regId) {
		Intent intent = new Intent(context, PushNotificationTaskIntentService.class);
		intent.putExtra(PushNotificationTaskIntentService.REGISTRATION_ID, regId);
		intent.putExtra(PushNotificationTaskIntentService.KEY_IS_REGISTER, true);
		context.startService(intent);
	}
	
	public static void unregisterPushNotification(Context context, String regId) {
		Intent intent = new Intent(context, PushNotificationTaskIntentService.class);
		intent.putExtra(PushNotificationTaskIntentService.REGISTRATION_ID, regId);
		intent.putExtra(PushNotificationTaskIntentService.KEY_IS_REGISTER, false);
		context.startService(intent);
	}
	
	// Public helper method to register with Google Push notification service GCM
	public static void registerForPushNotification(Context context) {
		LogHelper.logD(TAG, "registerforPushNotification called");
		// checkDevice - checks if this device supports GCM and throws
		// UnsupportedOperationException if it doesn't		
		GCMRegistrar.checkDevice(context);
		LogHelper.logD(TAG, "checkDevice succeeded");
		
		// checkManifest - checks if the application manifest meets all requirements
		// NOTE: only required while developing, can be removed when app is ready to
		// be published		
		GCMRegistrar.checkManifest(context);
		LogHelper.logD(TAG, "checkManifest succeeded");
		
		// GCMRegistrar keeps track of registration IDs. Before registering we should 
		// check if the device is already registered by getting the registration id
		// from GCMRegistrar. If id is null or empty register the device
		final String registrationId = GCMRegistrar.getRegistrationId(context);
		if(TextUtils.isEmpty(registrationId)) {
			LogHelper.logD(TAG, "Registration ID is empty. Registering for the push notification");
			GCMRegistrar.register(context, PushNotificationConstants.SENDER_ID);
		} else {
			// This device is already registered. Just send the registration id
			// to neato server
			LogHelper.logD(TAG, "Registration ID is " + registrationId);
			registerForPushNotificationOnNeatoServer(context, registrationId);
		}
	}
	
	public static void unregisterPushNotification(Context context) {
		// unregistering from push notifications at logout
		GCMRegistrar.unregister(context);
	}
	
	public static Bundle getPendingPushNotification() {
		return mPendingPushNotificationBundle;
	}
	
	public static void setPendingPushNotification(Bundle pushNotificationBundle) {
		mPendingPushNotificationBundle = pushNotificationBundle;
	}
	
	public static void clearPendingPushNotification() {
		mPendingPushNotificationBundle = null;
	}
	
}
