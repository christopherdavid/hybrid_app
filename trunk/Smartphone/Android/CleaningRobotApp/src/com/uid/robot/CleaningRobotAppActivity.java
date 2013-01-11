package com.uid.robot;

import android.os.Bundle;
import android.view.Menu;

import com.neatorobotics.android.slide.framework.plugins.base.SlidePluginBaseActivity;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebConstants;

public class CleaningRobotAppActivity extends SlidePluginBaseActivity {
	/**
	 */
	@Override
	public void init() {

		super.init();

		appView.setHorizontalScrollBarEnabled(false);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NeatoWebConstants.setServerEnvironment(NeatoWebConstants.PROD_SERVER_ID);



		super.loadUrl("file:///android_asset/www/index.html");
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
