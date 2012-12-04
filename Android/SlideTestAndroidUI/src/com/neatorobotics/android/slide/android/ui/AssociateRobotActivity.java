package com.neatorobotics.android.slide.android.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.service.INeatoRobotService;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppService;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;

public class AssociateRobotActivity extends Activity {
	
	private static final String TAG = AssociateRobotActivity.class.getSimpleName();
	
	private ProgressBar mAssociationProgress;
	private BroadcastReceiver mUiUpdateReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isStarted = intent.getBooleanExtra(NeatoSmartAppService.EXTRA_ROBOT_ASSOCIATION_START, false);
			if (isStarted) {
				mAssociationProgress.setVisibility(View.VISIBLE);
			}
			else {
				mAssociationProgress.setVisibility(View.INVISIBLE);
				// Toast.makeText(AssociateRobotActivity.this, "Robot Associated", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	};
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.associate_robot);
		//final TextView txt_page_title = (TextView) findViewById(R.id.txt_page_title);
		//txt_page_title.setText("Assocaite Robot");
		
		mAssociationProgress = (ProgressBar)findViewById(R.id.robot_associate_progress);
		final EditText etSerialId = (EditText)findViewById(R.id.edit_robot_serial_number);
		
		RobotItem robotItem = NeatoPrefs.getRobotItem(this);
		if (robotItem != null) {
			String serialId = robotItem.getSerialNumber();
			if (serialId != null) {
				etSerialId.setText(serialId);
				etSerialId.setSelection(serialId.length());
			}
		}
		/*
		Button btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		*/
		
		Button btnAssocaiteRobot = (Button)findViewById(R.id.btnAssociateRobot);
		btnAssocaiteRobot.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				INeatoRobotService service = ApplicationConfig.getInstance(getApplicationContext()).getRobotService();
				if (service != null) {
					String serialId = etSerialId.getText().toString();
					if (TextUtils.isEmpty(serialId)) {
						Toast.makeText(AssociateRobotActivity.this, "Serial Number is empty", Toast.LENGTH_LONG).show();
						return;
					}
					
					String emailId = NeatoPrefs.getUserEmailId(getApplicationContext());
					try {
						service.associateRobot(serialId, emailId);
					} 
					catch (RemoteException e) {
						
					}
					
				}
			}
		});
		
		registerReceiver(mUiUpdateReceiver, new IntentFilter(NeatoSmartAppService.NEATO_UI_UPDATE_ACTION));
		
	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(mUiUpdateReceiver);
		super.onDestroy();
	}
	
	
}
