package com.neatorobotics.android.slide.android.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadListener;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.service.INeatoRobotService;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppService;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppsEventConstants;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebservicesHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotAssociationDisassociationResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;

public class NeatoSmartAppTestActivity extends Activity {
	private static final String TAG = NeatoSmartAppTestActivity.class.getSimpleName();
	private INeatoRobotService mNeatoRobotService;
	private boolean mServiceBound = false;
	private Button btnPeerConnection;
	private boolean mRobotStarted = false;

	private ServiceConnection mNeatoRobotServiceConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			LogHelper.logD(TAG, "onServiceDisconnected called");
			LogHelper.logD(TAG, "component Name = " + name);
			mNeatoRobotService = null;
			ApplicationConfig.getInstance(NeatoSmartAppTestActivity.this).setRobotService(null);
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			LogHelper.log(TAG, "onServiceConnected called");
			mNeatoRobotService = INeatoRobotService.Stub.asInterface(service);
			ApplicationConfig.getInstance(NeatoSmartAppTestActivity.this).setRobotService(mNeatoRobotService);

			Intent resultReceiverIntent = new Intent(NeatoSmartAppService.NEATO_RESULT_RECEIVER_ACTION);
			resultReceiverIntent.putExtra(NeatoSmartAppService.EXTRA_RESULT_RECEIVER, mResultReciever);
			sendBroadcast(resultReceiverIntent);

			if (mNeatoRobotService != null) {
				try {
					mNeatoRobotService.loginToXmpp();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	private RobotListAdapter mRobotListAdapter;
	private Handler mHandler = new Handler();
	private AlertDialog mRobotListUi;
	// private LinearLayout mProgressBar;
	private ProgressBar mProgressBar;

	private DialogInterface.OnClickListener mRobotSelectListener = new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int position) {
			closeRobotListDialog();
			RobotInfo robotInfo = (RobotInfo)mRobotListAdapter.getItem(position);
			String robotIpAddress = robotInfo.getRobotIpAddress();
			LogHelper.log(TAG, "Connecting to Robot. IP Address:" + robotIpAddress);
			// 	try {
			String emailId = NeatoPrefs.getUserEmailId(NeatoSmartAppTestActivity.this);
			LogHelper.log(TAG, "Associating user with Robot. User Email = " + emailId);
			AssociateToUser robotAssociate = new AssociateToUser(robotInfo.getRobotName(), 
					robotInfo.getSerialId(), emailId, robotIpAddress);
			robotAssociate.execute();
			/*NeatoPrefs.setPeerIpAddress(NeatoSmartAppTestActivity.this, robotIpAddress);*/
		}
	};

	private class NeatoRobotResultReceiver extends ResultReceiver
	{

		public NeatoRobotResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			LogHelper.log(TAG, "Result Code = " + resultCode);
			LogHelper.log(TAG, "Bundle = " + resultData);
			
			if (NeatoSmartAppsEventConstants.NEW_ROBOT_FOUND == resultCode) {
				LogHelper.logD(TAG, "NEW_ROBOT_FOUND");
				RobotInfo robotInfo = resultData.getParcelable(NeatoSmartAppService.DISCOVERY_ROBOT_INFO);
				mRobotListAdapter.addRobot(robotInfo);
				if ((mRobotListUi == null) || !mRobotListUi.isShowing()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(NeatoSmartAppTestActivity.this);
					builder.setTitle("Nearby Robots");
					builder.setAdapter(mRobotListAdapter, mRobotSelectListener);					
					mRobotListUi = builder.create();
					mRobotListUi.show();
				}
			}
			else if (NeatoSmartAppsEventConstants.DISCOVERY_STARTED == resultCode) {
				LogHelper.logD(TAG, "DISCOVERY_STARTED");
				mProgressBar.setVisibility(View.VISIBLE);
				mBtnFindRobots.setEnabled(false);
			}
			else if (NeatoSmartAppsEventConstants.DISCOVERY_END == resultCode) {
				LogHelper.logD(TAG, "DISCOVERY_END");
				mProgressBar.setVisibility(View.GONE);
				mBtnFindRobots.setEnabled(true);
			}
			else if (NeatoSmartAppsEventConstants.ROBOT_CONNECTED == resultCode) {
				//mConnectionStatusText.setText("Robot Connected");
				LogHelper.logD(TAG, "ROBOT_CONNECTED");
				btnPeerConnection.setText("Close Peer connection");
				enableDisableStartStopUI(true);				
			} else if (NeatoSmartAppsEventConstants.ROBOT_DATA_RECEIVED == resultCode) {
			
			} else if (NeatoSmartAppsEventConstants.ROBOT_CONNECTION_ERROR == resultCode) {
				btnPeerConnection.setText("Open Peer connection");
				LogHelper.logD(TAG, "ROBOT_CONNECTION_ERROR");
				Toast.makeText(NeatoSmartAppTestActivity.this, "Could not connect to the peer", Toast.LENGTH_LONG).show();

			} 
			else if (NeatoSmartAppsEventConstants.ROBOT_DISCONNECTED == resultCode) {
				//mConnectionStatusText.setText("No Robot Connected");
				btnPeerConnection.setText("Open Peer connection");
				LogHelper.logD(TAG, "ROBOT_DISCONNECTED");
				enableDisableStartStopUI(false);
			}
			else if (NeatoSmartAppsEventConstants.ROBOT_DATA_RECEIVED == resultCode) {

			}
		}
	}
	
	private ResultReceiver mResultReciever;
	private Button mBtnFindRobots; 
	private TextView mRobotAssociationStatusText;
	private Button mStartStopPeerConn;
	private Button mStartStopServerConn;
	
	private static final int ROBOT_ASSOCIATED_REQUEST_CODE = 1;
	
	private static String getTitle(Context context)
	{
		String version = AppUtils.getVersionWithBuildNumber(context);
		String title = "Neato SmartApps (Build " + version + ")";
		return title;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		String title = getTitle(this);
		setTitle(title);
		
		String version = AppUtils.getVersionWithBuildNumber(this);
		LogHelper.log(TAG, "Build Number = " + version);
		
		mResultReciever = new NeatoRobotResultReceiver(mHandler);
		
		// mProgressBar = (LinearLayout)findViewById(R.id.find_robot_progress);
		mProgressBar = (ProgressBar)findViewById(R.id.progress_view);
		mRobotAssociationStatusText = (TextView)findViewById(R.id.txtConnectionStatus);
		Intent serviceIntent = new Intent(this, NeatoSmartAppService.class);
		startService(serviceIntent);
		Intent bindServiceIntent = new Intent(this, NeatoSmartAppService.class);
		bindServiceIntent.putExtra(NeatoSmartAppService.EXTRA_RESULT_RECEIVER, mResultReciever);
		mServiceBound = bindService(bindServiceIntent, mNeatoRobotServiceConnection, BIND_AUTO_CREATE);
		
		mRobotListAdapter = new RobotListAdapter(this);
		
		mBtnFindRobots = (Button)findViewById(R.id.find_robots);
		mBtnFindRobots.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mRobotListAdapter.clear();
				if (mNeatoRobotService != null) {
					try {
						mNeatoRobotService.startDiscovery();
					} 
					catch (RemoteException e) {
					}
				}
			}
		});
	

		btnPeerConnection = (Button)findViewById(R.id.btn_peer_connection);
		btnPeerConnection.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					if(!NeatoPrefs.getPeerConnectionStatus(NeatoSmartAppTestActivity.this)) {
						
						String ipaddress = NeatoPrefs.getPeerIpAddress(NeatoSmartAppTestActivity.this);
						if(ipaddress != null) {
							mNeatoRobotService.connectToRobot(ipaddress);
							LogHelper.log(TAG, "Forming connection "+ipaddress);
						} else {
							Toast.makeText(NeatoSmartAppTestActivity.this, "Ip address not available", Toast.LENGTH_LONG).show();
						}

					} else {
						LogHelper.logD(TAG, "Connection exists. Disconnecting..");
						mNeatoRobotService.closePeerConnection("");
					}
				} catch (RemoteException e) {
					LogHelper.log(TAG, "Exception in closePeerConnection", e);
				}

			}
		});

		mStartStopPeerConn = (Button)findViewById(R.id.startStopPeerConn);
		mStartStopPeerConn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {				
				toggleRobot(false);
			}
		});

		mStartStopServerConn = (Button)findViewById(R.id.startStopServer);
		mStartStopServerConn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				toggleRobot(true);
			}
		});
		
		
		Button btnAssociateRobot = (Button)findViewById(R.id.btn_associaterobot);
		btnAssociateRobot.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(NeatoSmartAppTestActivity.this, AssociateRobotActivity.class);
				startActivityForResult(intent, ROBOT_ASSOCIATED_REQUEST_CODE);
			}
		});		
		

		Button btnGetMapData = (Button)findViewById(R.id.btn_getMapData);
		btnGetMapData.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				Intent getMapActivity = new Intent(NeatoSmartAppTestActivity.this, GetRobotMapActivity.class);
				startActivity(getMapActivity);
			}
		});		
		
		Button btnScheduleRobot = (Button)findViewById(R.id.btn_schedulerobot);
		btnScheduleRobot.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(NeatoSmartAppTestActivity.this, ScheduleRobotActivity.class);
				startActivity(intent);
			}
		});		

		
//		NeatoPrefs.saveJabberId(getApplicationContext(), AppConstants.JABBER_USER_ID);
//		NeatoPrefs.saveJabberPwd(getApplicationContext(), AppConstants.JABBER_CHAT_PASSWORD);
		
		RobotItem robotItem = NeatoPrefs.getRobotItem(this);
		if (robotItem != null) {
			setAssociationStatus("Associated with " + robotItem.getName());
		}

		boolean isPeerConnected = NeatoPrefs.getPeerConnectionStatus(this);
		if (isPeerConnected) {
			//mConnectionStatusText.setText("Robot Connected");

			enableDisableStartStopUI(true);
			btnPeerConnection.setText("Close Peer connection");
		}
		else {
			enableDisableStartStopUI(false);
			btnPeerConnection.setText("Open Peer connection");
		}

		updateStartStopUI();
	}

	private void setAssociationStatus(String status) {
		mRobotAssociationStatusText.setText(status);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == ROBOT_ASSOCIATED_REQUEST_CODE) {
				RobotItem robotItem = NeatoPrefs.getRobotItem(this);
				if (robotItem != null) {
					setAssociationStatus("Associated with " + robotItem.getSerialNumber());
				}
				else {
					setAssociationStatus(getString(R.string.text_robot_connection_status_default));
				}
			}
		}
	}


	private void updateStartStopUI() {
		if (mRobotStarted) {
			mStartStopPeerConn.setText(getString(R.string.text_btn_stop_peer_conn));
			mStartStopServerConn.setText(getString(R.string.text_btn_stop_server_conn));
		}
		else {
			mStartStopPeerConn.setText(getString(R.string.text_btn_start_peer_conn));
			mStartStopServerConn.setText(getString(R.string.text_btn_start_server_conn));
		}
	}
	
	private void enableDisableStartStopUI(boolean enable) {
		if (enable) {
			mStartStopPeerConn.setEnabled(true);
		}
		else {
			mStartStopPeerConn.setEnabled(false);			
		}
	}
	
	private void toggleRobot(boolean useXmppServer) {
		if(mRobotStarted) {
			stopRobot(useXmppServer);
			mRobotStarted = false;
		}
		else {
			startRobot(useXmppServer);
			mRobotStarted = true;
		}
		
		updateStartStopUI();
	}
	
	private void startRobot(boolean useXmppServer) {
		if (mNeatoRobotService != null) {
			try {
				mRobotListAdapter.clear();
				//TODO Send right IP Address. As we supporting only one Robot now, this won't create a problem.
				mNeatoRobotService.sendCommand("", RobotCommandPacketConstants.COMMAND_ROBOT_START ,useXmppServer);
				}
			
			catch (RemoteException e) {				
				e.printStackTrace();
			}
		}
	}
	
	private void stopRobot(boolean useXmppServer) {
		if (mNeatoRobotService != null) {
			try {
				mRobotListAdapter.clear();
				//TODO  Send right IP Address. As we supporting only one Robot now, this won't create a problem.
				mNeatoRobotService.sendCommand("", RobotCommandPacketConstants.COMMAND_ROBOT_STOP ,useXmppServer);
			} 
			catch (RemoteException e) {				
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {

		closeRobotListDialog();
		if (mServiceBound) {
			unbindService(mNeatoRobotServiceConnection);
			//Intent serviceIntent = new Intent(this, NeatoSmartAppService.class);
			//stopService(serviceIntent);   
			mServiceBound = false;
		}
		super.onDestroy();
	}

	private void closeRobotListDialog() {
		if (mRobotListUi != null) {
			if (mRobotListUi.isShowing()) {
				mRobotListUi.dismiss();
			}
			mRobotListUi = null;
		}
	}

	private class RobotListAdapter extends BaseAdapter
	{
		private ArrayList<RobotInfo> mRobotInfo = new ArrayList<RobotInfo>();
		private LayoutInflater mInflater;
		public RobotListAdapter(Context context)
		{
			mInflater = LayoutInflater.from(context);
		}
		public void addRobot(RobotInfo robotInfo)
		{
			mRobotInfo.add(robotInfo);
			notifyDataSetChanged();
		}
		
		public void clear()
		{
			mRobotInfo.clear();
			notifyDataSetChanged();
		}
		
		public int getCount() {
			return mRobotInfo.size();
		}

		public Object getItem(int position) {
			return mRobotInfo.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = mInflater.inflate(R.layout.robot_item_view, null);
			RobotInfo robotInfo = mRobotInfo.get(position);
			if (robotInfo != null) {
				TextView txtName = (TextView)view.findViewById(R.id.robotName);
				TextView txtSerialNumber = (TextView)view.findViewById(R.id.robotSerialId);
				txtName.setText(robotInfo.getRobotName());
				txtSerialNumber.setText(robotInfo.getSerialId());
			}
			return view;
		}

	}

	private class AssociateToUser extends AsyncTask<Void, Void, RobotAssociationDisassociationResult>  {

		private String mEmailId;
		private String mRobotSerialId;
		private String mRobotName;
		private RobotItem mRobotItem;
		private String mRobotIpAddress;
		//NeatoPrefs.saveRobotInformation(getApplicationContext(), robotItem);
		public AssociateToUser(String robotName, String robotSerialId, String emailId, String robotIpAddress)
		{
			mRobotName = robotName;
			mRobotSerialId = robotSerialId;
			mEmailId = emailId;
			mRobotIpAddress = robotIpAddress;
			LogHelper.log(TAG, "AssociateToUser - mRobotName = " + mRobotName);
			LogHelper.log(TAG, "AssociateToUser - mRobotSerialId = " + mRobotSerialId);
		}

		@Override
		protected RobotAssociationDisassociationResult doInBackground(Void... params) {

			RobotManager robotManager = RobotManager.getInstance(getApplicationContext());
			RobotItem robotItem = robotManager.getRobotDetail(mRobotSerialId);
			LogHelper.log(TAG, "Got remote detail from server");
			LogHelper.log(TAG, "RobotItem = " + robotItem);

			if (robotItem != null) {
				//NeatoPrefs.saveRobotInformation(getApplicationContext(), robotItem);
				mRobotItem = robotItem;
				RobotAssociationDisassociationResult associationResult =  NeatoRobotWebservicesHelper.associateNeatoRobotRequest(NeatoSmartAppTestActivity.this, 
						mEmailId, mRobotSerialId);
				LogHelper.log(TAG, "AssociateNeatoRobotResult = " + associationResult);
				return associationResult;
			}
			return null;
		}

		@Override
		protected void onPostExecute(RobotAssociationDisassociationResult result) {
			super.onPostExecute(result);
			if (result == null) {
				Toast.makeText(NeatoSmartAppTestActivity.this, "Please register the robot before associating it.", Toast.LENGTH_LONG).show();
				return;
			}
			if (result.success()) {
				//Toast.makeText(NeatoSmartAppTestActivity.this, "Robot associated", Toast.LENGTH_LONG).show();
				NeatoPrefs.saveRobotInformation(getApplicationContext(), mRobotItem);
				NeatoPrefs.setPeerIpAddress(NeatoSmartAppTestActivity.this, mRobotIpAddress);
				setAssociationStatus("Associated with " + mRobotSerialId);
			} 
			else {
				Toast.makeText(NeatoSmartAppTestActivity.this, result.mMessage, Toast.LENGTH_LONG).show();
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.welcome_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menuitem_logout:
			logout();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void logout() {
		NeatoPrefs.saveUserEmailId(NeatoSmartAppTestActivity.this, null);
		try {
			mNeatoRobotService.cleanup();
		} catch (RemoteException e) {		
			Log.e(TAG, "RemoteException in logout" + e);
		}

		Intent createUserIntent = new Intent(NeatoSmartAppTestActivity.this , NeatoSmartAppWelcomeScreen.class);
		startActivity(createUserIntent);
		finish();
	}
}
