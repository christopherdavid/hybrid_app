package com.neatorobotics.android.slide.framework.plugins.base;

import java.util.Observable;
import java.util.Observer;

import org.apache.cordova.DroidGap;

import android.os.Bundle;

import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.NeatoServiceManager;
import com.neatorobotics.android.slide.framework.crittercism.CrittercismHelper;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationMessageHandler;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationUtils;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.DeviceUtils;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class SlidePluginBaseActivity extends DroidGap implements Observer {

    private static final String TAG = SlidePluginBaseActivity.class.getSimpleName();

    /**
     * This boolean is used on the onResume call to know whether the activity is
     * resumed from sleep or resumed from new creation.
     */
    // TODO: We can work without this boolean flag. But there are prospective
    // issues which
    // which can happen. Need to look into it. For now we will keep this boolean
    // flag as-is.
    private boolean mIsActivityResumedFromSleep = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogHelper.logD(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        AppUtils.logLibraryVersion();
        CrittercismHelper.initializeCrittercism(this);
        NeatoServiceManager serviceManager = NeatoServiceManager.getInstance(getApplicationContext());
        serviceManager.bindNeatoService();

        if (UserHelper.isUserLoggedIn(this)) {
            String authToken = NeatoPrefs.getNeatoUserAuthToken(this);
            UserManager.getInstance(this).setUserAttributesOnServer(authToken, DeviceUtils.getUserAttributes(this));
            AppUtils.createNeatoUserDeviceIdIfNotExists(this);
            PushNotificationUtils.registerForPushNotification(this);
        }

        mIsActivityResumedFromSleep = false;
    }

    @Override
    public void onDestroy() {
        NeatoServiceManager serviceManager = NeatoServiceManager.getInstance(getApplicationContext());
        if (serviceManager != null) {
            serviceManager.unBindNeatoService();
        }
        super.onDestroy();
    }

    // Prevent back button from the activity and implement the behaviour in the
    // UI.
    @Override
    public void onBackPressed() {
        LogHelper.logD(TAG, "onBackPressed called");
    }

    @Override
    public void onResume() {
        super.onResume();
        UserManager.getInstance(this).addObserver(this);
        ApplicationConfig.getInstance(getApplicationContext()).activityResumed();
        if (UserHelper.isUserLoggedIn(this)) {
            showIfPendingPushNotification();
            syncWithServer();
        }
        mIsActivityResumedFromSleep = true;
    }

    private void syncWithServer() {
        // Make sure the xmpp is logged in
        if (!mIsActivityResumedFromSleep) {
            LogHelper
                    .log(TAG, "Activity is not resuming from sleep. So service initialization will handle xmpp login.");
            return;
        }
        RobotCommandServiceManager.loginXmppIfRequired(getApplicationContext());
    }

    /**
     * If the application is in foreground but the device goes to sleep, we send
     * the notification to the notification bar as the application is in the
     * paused state. <br>
     * But that is not what the requirement is, and so this fix is necessary. <br>
     * Once the application comes back to resume state from sleep, we check if
     * any pending notifications are there and send the notification if listener
     * is attached.
     */
    private void showIfPendingPushNotification() {
        if (mIsActivityResumedFromSleep) {
            PushNotificationMessageHandler.getInstance(this).showPendingPushNotification();
            PushNotificationMessageHandler.getInstance(this).clearPushNotificationFromBar();
        }
    }

    @Override
    public void onPause() {
        ApplicationConfig.getInstance(getApplicationContext()).activityPaused();
        UserManager.getInstance(this).deleteObserver(this);
        super.onPause();
    }

    public void onStop() {
        RobotCommandServiceManager.logoutXmpp(this);
        super.onStop();
    }

    // onUserLeaveHint
    @Override
    public void update(Observable observable, Object data) {
        NeatoServiceManager serviceManager = NeatoServiceManager.getInstance(getApplicationContext());
        LogHelper.logD(TAG, "onUpdate Called");
        if (serviceManager != null) {
            if (UserHelper.isUserLoggedIn(this)) {
                RobotCommandServiceManager.initiateXmppConnection(this);
                PushNotificationUtils.registerForPushNotification(this);
                AppUtils.createNeatoUserDeviceIdIfNotExists(this);
            } else {
                RobotCommandServiceManager.cleanUpServiceConnections(this);
            }
        }
    }
}
