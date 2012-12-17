package com.neatorobotics.android.slide.phonegap.ui;


import android.os.Bundle;

import com.neatorobotics.android.slide.framework.plugins.base.SlidePluginBaseActivity;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebConstants;


public class NeatoSmartappTestActivity extends SlidePluginBaseActivity {
	
	private static final String PHONEGAP_URL = "file:///android_asset/www/neatosmartapp.html";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NeatoWebConstants.setServerEnvironment(NeatoWebConstants.PROD_SERVER_ID);
        
		super.loadUrl(PHONEGAP_URL);
    }
}