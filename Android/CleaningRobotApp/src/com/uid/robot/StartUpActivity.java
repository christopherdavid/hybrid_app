package com.uid.robot;


import java.util.Calendar;
import java.util.GregorianCalendar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * startup is the first activity which checks the expired date (1.9.2014)
 * if the app has been started before a specific date the cleaning robot activity starts
 * otherwise an expired text  is shown
 * @author Lars.Kuhs
 *
 */
public class StartUpActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar now = Calendar.getInstance();
        Calendar endDate = new GregorianCalendar();
        endDate.set(Calendar.YEAR, 2014);
        endDate.set(Calendar.MONTH, Calendar.SEPTEMBER);
        endDate.set(Calendar.DAY_OF_MONTH, 1);

        if(now.after(endDate)) {
        	// expired
        	setContentView(R.layout.activity_startup);
        	Button exitButton = (Button) findViewById( R.id.button1 );
    		
        	exitButton.setOnClickListener( new OnClickListener() { 
    			
    			@Override
    			public void onClick(View v) {
    				System.exit(0);
    			}
    		});
        	
        } else {
        	startActivity(new Intent(StartUpActivity.this, CleaningRobotAppActivity.class));
        	finish();
        }
    }
}
