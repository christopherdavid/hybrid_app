package com.neatorobotics.android.slide.framework.plugins.base;

import java.util.Observable;
import java.util.Observer;

import org.apache.cordova.DroidGap;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.NeatoServiceManager;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationConstants;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationMessageHandler;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationUtils;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.resultreceiver.NeatoRobotResultReceiverConstants;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.DeviceUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebConstants;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class SlidePluginBaseActivity extends DroidGap implements Observer {

	private static final String TAG = SlidePluginBaseActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		LogHelper.logD(TAG, "onCreate called");
		super.onCreate(savedInstanceState);
		AppUtils.logLibraryVersion();

		NeatoWebConstants.setServerEnvironment(NeatoWebConstants.STAGING_SERVER_ID);
		NeatoServiceManager serviceManager = NeatoServiceManager.getInstance(getApplicationContext());
		if(UserHelper.isUserLoggedIn(this)) {
			serviceManager.initialize();
			String authToken = NeatoPrefs.getNeatoUserAuthToken(this);
			AppUtils.createNeatoUserDeviceIdIfNotExists(this);
			UserManager.getInstance(this).setUserAttributesOnServer(authToken, DeviceUtils.getUserAttributes(this));
			PushNotificationUtils.registerForPushNotification(this);
		}
	}
	
	@Override
	public void onDestroy() {
		NeatoServiceManager serviceManager = NeatoServiceManager.getInstance(getApplicationContext());
		if (serviceManager != null) {
			serviceManager.uninitialize();
		}
		super.onDestroy();
	}
	// Prevent back button from the activity and implement the behaviour in the UI.
	@Override
	public void onBackPressed() {
		LogHelper.logD(TAG, "onBackPressed called");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UserManager.getInstance(this).addObserver(this);
		ApplicationConfig.getInstance(getApplicationContext()).activityResumed();
		LogHelper.logD(TAG, "onResume Called");
		if(UserHelper.isUserLoggedIn(this)) {
			Bundle pendingMessage = PushNotificationUtils.getPendingPushNotification();
			//LogHelper.logD(TAG, "Pending Message"+ pendingMessage.getString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID));
			if(pendingMessage != null){
				LogHelper.logD(TAG, "Yes some Pending message");
				//LogHelper.logD(TAG, "Pending Message Send"+ pendingMessage.getString(PushNotificationConstants.NOTIFICATION_ID_KEY));
				PushNotificationMessageHandler.getInstance(this).sendForegroundNotification(getApplicationContext(), pendingMessage);
				PushNotificationUtils.clearPendingPushNotification();
				NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
				if(notificationManager != null)
				{
					// No message stackup 
					//LogHelper.logD(TAG, "Canceled the Pending message :"+ getIntent().getExtras().getString("time"));
					//LogHelper.logD(TAG, "Notification id :"+ pendingMessage.getString("time"));
					//notificationManager.cancel(Integer.parseInt(pendingMessage.getString("time")));
					notificationManager.cancel(0);
				}
			}
			else
				LogHelper.logD(TAG, "No Pending message");
		}	
	}
	
	@Override
	public void onPause() {
		ApplicationConfig.getInstance(getApplicationContext()).activityPaused();
		UserManager.getInstance(this).deleteObserver(this);
		super.onPause();
	}

	@Override
	public void update(Observable observable, Object data) {
		NeatoServiceManager serviceManager = NeatoServiceManager.getInstance(getApplicationContext());
		if (serviceManager != null) {
			if (UserHelper.isUserLoggedIn(this)) {
				serviceManager.initialize();
			}
			else {
				serviceManager.uninitialize();
			}
		}
	}
}
