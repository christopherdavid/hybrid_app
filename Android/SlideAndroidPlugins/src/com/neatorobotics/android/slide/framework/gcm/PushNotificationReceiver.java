package com.neatorobotics.android.slide.framework.gcm;

import android.content.Context;
import com.google.android.gcm.GCMBroadcastReceiver;

public class PushNotificationReceiver extends GCMBroadcastReceiver {

    @Override
    protected String getGCMIntentServiceClassName(Context context) {
        return PushNotificationIntentService.class.getName();
    }
}
