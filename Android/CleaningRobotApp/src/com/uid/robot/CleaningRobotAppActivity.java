package com.uid.robot;

import org.apache.cordova.DroidGap;
import android.os.Bundle;
import android.view.Menu;

public class CleaningRobotAppActivity extends DroidGap {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
