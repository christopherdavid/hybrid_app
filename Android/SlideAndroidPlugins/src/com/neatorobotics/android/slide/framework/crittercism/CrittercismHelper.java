package com.neatorobotics.android.slide.framework.crittercism;

import com.crittercism.app.Crittercism;

import android.content.Context;

public class CrittercismHelper {

    private static final String CRITTERCISM_DEBUG_APPLICATION_ID = "5332917e40ec923484000006";
    private static final String CRITTERCISM_RELEASE_APPLICATION_ID = "5310ec6e7c37643414000003";

    private static final boolean ENABLE_CRITTERCISM_SUPPORT = true;
    private static final boolean IS_IN_DEBUG_MODE = true;

    private static final String CRITTERCISM_APPLICATION_ID = (IS_IN_DEBUG_MODE ? CRITTERCISM_DEBUG_APPLICATION_ID
            : CRITTERCISM_RELEASE_APPLICATION_ID);

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
            Crittercism.initialize(context.getApplicationContext(), CRITTERCISM_APPLICATION_ID);
            mInitialized = true;
        }
    }
}
