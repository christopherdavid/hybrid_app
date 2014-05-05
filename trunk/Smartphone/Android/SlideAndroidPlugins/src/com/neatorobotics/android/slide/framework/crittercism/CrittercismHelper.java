package com.neatorobotics.android.slide.framework.crittercism;

import com.crittercism.app.Crittercism;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebConstants;

import android.content.Context;

public class CrittercismHelper {

    private static final boolean ENABLE_CRITTERCISM_SUPPORT = true;
    private static boolean mInitialized = false;

    /**
     * Initializes the Crittercism Framework. It will be initialized only if
     * ENABLE_CRITTERCISM_SUPPORT is true.
     * 
     * @param context
     *            The context
     */
    public static void initializeCrittercism(Context context) {

        if (context == null) {
            return;
        }

        if (ENABLE_CRITTERCISM_SUPPORT && !mInitialized) {
            Crittercism.initialize(context.getApplicationContext(), NeatoWebConstants.getCrittercismAppId(context));
            mInitialized = true;
        }
    }
}
