package com.uid.robot;

import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import com.vorwerkrobot.vr200beta.R;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;

import com.neatorobotics.android.slide.framework.plugins.base.SlidePluginBaseActivity;
//import com.neatorobotics.android.slide.framework.webservice.NeatoWebConstants;

public class CleaningRobotAppActivity extends SlidePluginBaseActivity {
	/**
	 */
	@Override
	public void init() {

		super.init();

		appView.setHorizontalScrollBarEnabled(false);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
	        if(0 != (getApplicationInfo().flags = ApplicationInfo.FLAG_DEBUGGABLE)){
		        // Enabling web debugging
		        WebView.setWebContentsDebuggingEnabled(true);
		    }
	    }
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//NeatoWebConstants.setServerEnvironment(NeatoWebConstants.PROD_SERVER_ID);

		super.loadUrl("file:///android_asset/www/index.html");
		
		// Disable text selection on android to enable taphold within the web view.
		super.appView.setLongClickable(false);
		super.appView.setOnLongClickListener(new View.OnLongClickListener() {
		    
		    @Override
		    public boolean onLongClick(View v) {
			return true;
		    }
		});
	}

}
