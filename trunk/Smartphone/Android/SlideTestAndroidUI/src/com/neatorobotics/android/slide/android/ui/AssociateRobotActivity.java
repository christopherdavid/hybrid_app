package com.neatorobotics.android.slide.android.ui;

import org.apache.cordova.api.LOG;

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
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.service.INeatoRobotService;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppService;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppsEventConstants;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;

public class AssociateRobotActivity extends Activity {

	private static final String TAG = AssociateRobotActivity.class.getSimpleName();

	private ProgressBar mAssociationProgress;
	private BroadcastReceiver mUiUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isStarted = intent.getBooleanExtra(NeatoSmartAppService.EXTRA_ROBOT_ASSOCIATION_START, false);
			int status = intent.getIntExtra(NeatoSmartAppService.EXTRA_ROBOT_ASSOCIATION_STATUS, NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_FAILED);
			if (isStarted) {
				mAssociationProgress.setVisibility(View.VISIBLE);
			}
			else {
				mAssociationProgress.setVisibility(View.INVISIBLE);
				//Association successful. Close existing peer connection.
				if (status == NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_SUCCESS) {
					LogHelper.logD(TAG, "Association successful. Disconnect peer connection");
					try {
						INeatoRobotService service = ApplicationConfig.getInstance(getApplicationContext()).getRobotService();
						service.closePeerConnection("");
					} catch (RemoteException e) {
						LogHelper.log(TAG, "Exception in closePeerConnection");
					}
					NeatoPrefs.setPeerIpAddress(getApplicationContext(), null);
					Toast.makeText(AssociateRobotActivity.this, "Robot Associated", Toast.LENGTH_SHORT).show();
					AssociateRobotActivity.this.setResult(RESULT_OK);
					finish();
				} else {
					LogHelper.log(TAG, "Association unsuccessful.");
					Toast.makeText(AssociateRobotActivity.this, "Association not successful.", Toast.LENGTH_LONG).show();
				}
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
		

		Button btnAssociateRobot = (Button)findViewById(R.id.btnAssociateRobot);
		btnAssociateRobot.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				INeatoRobotService service = ApplicationConfig.getInstance(getApplicationContext()).getRobotService();
				if (service != null) {
					String serialId = etSerialId.getText().toString();
					if (TextUtils.isEmpty(serialId)) {
						Toast.makeText(AssociateRobotActivity.this, "Serial Number is empty", Toast.LENGTH_LONG).show();
						return;
					}

					RobotItem robotItem = NeatoPrefs.getRobotItem(getApplicationContext());					
					if (robotItem != null) {
						String oldSerialNo = robotItem.getSerialNumber();
						if (!TextUtils.isEmpty(oldSerialNo)) {
							if(serialId.equals(oldSerialNo)) {
								Toast.makeText(AssociateRobotActivity.this, "Robot is already associated", Toast.LENGTH_LONG).show();
								return;
							} 
						}
					}

					String emailId = NeatoPrefs.getUserEmailId(getApplicationContext());
					try {
						service.associateRobot(serialId, emailId);
					} 
					catch (RemoteException e) {
						LOG.e(TAG, "Remote Exception:", e);
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
