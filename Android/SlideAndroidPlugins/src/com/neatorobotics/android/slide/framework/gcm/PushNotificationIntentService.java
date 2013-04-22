package com.neatorobotics.android.slide.framework.gcm;

import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.google.android.gcm.GCMBaseIntentService;
import com.neatorobotics.android.slide.framework.R;
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
		sendNotification(context, pushNotificationBundle);
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		LogHelper.logD(TAG, "Registration Id: " + regId);
		PushNotificationUtils.registerForPushNotificationOnNeatoServer(context, regId);
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		PushNotificationUtils.unregisterPushNotification(context, regId);
	}
	
	private void sendNotification(Context context, Bundle bundle) {		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// TODO: Later we need to start the service but for now, we are just showing
		// the main launcher activity of the application
		//Intent notifyIntent = new Intent(context, NeatoSmartAppService.class);
		Intent notifyIntent = getLauncherIntent(context);
		notifyIntent.putExtra(PushNotificationConstants.EXTRA_NOTIFICATION_BUNDLE, bundle);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);
		String message = bundle.getString(PushNotificationConstants.NOTIFICATION_TICKER_KEY);
		String notificationTitle = getString(R.string.notification_text_title);
		
		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.notification_icon)
		.setContentTitle(notificationTitle)
		.setContentText(message)
		.setTicker(message)
		.setContentIntent(pendingIntent)
		.setAutoCancel(true);
		Notification notification = builder.getNotification();
		notificationManager.notify(0, notification);
	}
	
	private Intent getLauncherIntent(Context context) {
		PackageManager pm = context.getPackageManager();
		String packageName = context.getPackageName();
		LogHelper.logD(TAG, "packageName " + packageName);
		Intent intent = pm.getLaunchIntentForPackage(packageName);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		LogHelper.logD(TAG, "intent " + intent);
		return intent;
	}
}
