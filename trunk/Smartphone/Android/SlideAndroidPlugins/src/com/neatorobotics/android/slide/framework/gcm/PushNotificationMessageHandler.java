package com.neatorobotics.android.slide.framework.gcm;

import java.util.HashMap;

import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.R;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

public class PushNotificationMessageHandler {
	
	private static final int PUSH_NOTIFICATION_UNIQUE_ID = 0;

	private static final String TAG = PushNotificationMessageHandler.class.getSimpleName();
	
	private static PushNotificationMessageHandler sPushNotificationMessageHandler;
	private static Object mObjectCreateLock = new Object();	
	
	private Context mContext;
	private PushNotificationListener mPushNotificationListener;
	
	private static HashMap<String, Integer> sNotificationResourceIdMap = new HashMap<String, Integer>();
	
	static {
		sNotificationResourceIdMap.put(RobotCommandPacketConstants.NOTIFICATION_ID_ROBOT_STUCK, R.string.notification_text_robot_stuck);
		sNotificationResourceIdMap.put(RobotCommandPacketConstants.NOTIFICATION_ID_DIRT_BIN_FULL, R.string.notification_text_dirt_bag_full);
		sNotificationResourceIdMap.put(RobotCommandPacketConstants.NOTIFICATION_ID_CLEANING_DONE, R.string.notification_text_cleaning_done);
		sNotificationResourceIdMap.put(RobotCommandPacketConstants.NOTIFICATION_ID_DUST_BIN_MISSING, R.string.notification_text_dust_bin_missing);
		sNotificationResourceIdMap.put(RobotCommandPacketConstants.NOTIFICATION_ID_ERR_CANCEL, R.string.notification_text_cancel_error);
		sNotificationResourceIdMap.put(RobotCommandPacketConstants.NOTIFICATION_ID_PLUG_CABLE, R.string.notification_text_plug_cable);
		sNotificationResourceIdMap.put(RobotCommandPacketConstants.NOTIFICATION_ID_GENERIC, R.string.notification_text_generic_message);
	}
	private PushNotificationMessageHandler(Context context)
	{
		mContext = context.getApplicationContext();
	}

	public static PushNotificationMessageHandler getInstance(Context context)
	{
		synchronized (mObjectCreateLock) {
			if (sPushNotificationMessageHandler == null) {
				sPushNotificationMessageHandler = new PushNotificationMessageHandler(context);
			}
		}
		return sPushNotificationMessageHandler;
	}
	
	public void processPushMessageAndNotify(Context context, Bundle bundle) {
		sendNotification(context, bundle);
	}
	

	// Send the push notification to the user if registered.
	private void sendNotification(Context context, Bundle bundle) {
		boolean isAppInForeground = ApplicationConfig.getInstance(mContext).isApplicationForeground();
		if (isAppInForeground) {
			LogHelper.log(TAG, "Application is in foreground");
			sendForegroundNotification(context, bundle);
		} else {
			LogHelper.log(TAG, "Application is in background");
			sendBackgroundNotification(context, bundle);
		}
	}
	
	private void sendBackgroundNotification(Context context, Bundle bundle) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notifyIntent = getLauncherIntent(context);
		notifyIntent.putExtra(PushNotificationConstants.EXTRA_NOTIFICATION_BUNDLE, bundle);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);
		String notificationId = bundle.getString(PushNotificationConstants.NOTIFICATION_ID_KEY);
		LogHelper.logD(TAG, "notificationId = " + notificationId);
		String message = getResourceStringForNotification(context, notificationId);
		String notificationTitle = context.getString(R.string.notification_text_title);
		
		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.notification_icon)
				.setContentTitle(notificationTitle).setContentText(message)
				.setTicker(message).setContentIntent(pendingIntent)
				.setAutoCancel(true);
		
		Notification notification = builder.getNotification();
		notificationManager.notify(PUSH_NOTIFICATION_UNIQUE_ID, notification);
		PushNotificationUtils.setPendingPushNotification(bundle);
	}

	private void sendForegroundNotification(Context context, Bundle bundle) {
		String message = bundle.getString(PushNotificationConstants.NOTIFICATION_MESSAGE_KEY);
		LogHelper.logD(TAG, "sendForegroundNotification: " + message);
		if (mPushNotificationListener != null) {
			mPushNotificationListener.onShowPushNotification(bundle);
		} else {
			LogHelper.logD(TAG, "cannot send push notification to UI");
		}
	}
	
	private String getResourceStringForNotification(Context context, String notificationId) {
		
		int notificationResId = 0;
		if (TextUtils.isEmpty(notificationId)) {
			LogHelper.logD(TAG, "notificationId is empty");
			notificationResId = R.string.notification_text_generic_message;
		} else {
			LogHelper.logD(TAG, "getting it from hashmap");
			if (sNotificationResourceIdMap.containsKey(notificationId)) {
				notificationResId = sNotificationResourceIdMap.get(notificationId);
			}
			else {
				notificationResId = R.string.notification_text_generic_message;
			}
			LogHelper.logD(TAG, "Resource id = " + notificationResId);
		}
	
		String message = context.getString(notificationResId);
		LogHelper.logD(TAG, "message = " + message);
		if (TextUtils.isEmpty(message)) {
			message = context.getString(R.string.notification_text_generic_message);
		}
		
		return message;
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
	
	public void addPushNotificationListener(PushNotificationListener listener) {
		mPushNotificationListener = listener;
		// Send any pending notifications saved
		showPendingPushNotification();
	}

	public void showPendingPushNotification() {
		
		if (mPushNotificationListener == null) {
			return;
		}
		Bundle pendingNotification = PushNotificationUtils.getPendingPushNotification();
		if (pendingNotification != null) {
			mPushNotificationListener.onShowPushNotification(pendingNotification);
			PushNotificationUtils.clearPendingPushNotification();
		}
	}
	
	public void removePushNotificationListener() {
		mPushNotificationListener = null;
	}
	
	public void clearPushNotificationFromBar() {
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		if(notificationManager != null)
		{
			notificationManager.cancel(PUSH_NOTIFICATION_UNIQUE_ID);
		}
	}
	
}
