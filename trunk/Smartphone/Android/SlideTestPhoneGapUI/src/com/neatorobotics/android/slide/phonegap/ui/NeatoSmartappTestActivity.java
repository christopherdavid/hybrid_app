package com.neatorobotics.android.slide.phonegap.ui;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.neatorobotics.android.slide.framework.plugins.base.SlidePluginBaseActivity;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebConstants;


public class NeatoSmartappTestActivity extends SlidePluginBaseActivity {
	
	private static final String PHONEGAP_URL = "file:///android_asset/www/neatosmartapp.html";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
    	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
    		super.setBooleanProperty("showTitle", true);
    	}
        super.onCreate(savedInstanceState);
        NeatoWebConstants.setServerEnvironment(NeatoWebConstants.PROD_SERVER_ID);
                
		super.loadUrl(PHONEGAP_URL);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.menuitem_about:
				Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
				startActivity(aboutIntent);
				return true;
	
			default:
				return super.onOptionsItemSelected(item);
			}
	}
}