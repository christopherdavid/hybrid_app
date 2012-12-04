package com.neatorobotics.android.slide.framework.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.resultreceiver.NeatoRobotResultReceiverConstants;
import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;
import com.neatorobotics.android.slide.framework.tcp.TcpConnectionHelper;
import com.neatorobotics.android.slide.framework.tcp.TcpDataPacketListener;
import com.neatorobotics.android.slide.framework.udp.RobotDiscovery;
import com.neatorobotics.android.slide.framework.udp.UdpConnectionHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.AssociateNeatoRobotResult;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebservicesHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.xmpp.XMPPConnectionHelper;
import com.neatorobotics.android.slide.framework.xmpp.XMPPUtils;

public class NeatoSmartAppService extends Service {

	public static final String EXTRA_ROBOT_ASSOCIATION_START = "extra.robot.association.start";
	public static final String EXTRA_RESULT_RECEIVER = "extra.result_receiver";
	public static final String DISCOVERY_ROBOT_INFO = "robot.info";
	public static final String NEATO_RESULT_RECEIVER_ACTION = "com.neato.simulator.result_receiver.action";
	public static final String NEATO_UI_UPDATE_ACTION = "com.neato.simulator.ui.update.action";
	private static final String TAG = NeatoSmartAppService.class.getSimpleName();
	private UdpConnectionHelper mUdpConnectionHelper;
	private TcpConnectionHelper mTcpConnectionHelper;
	private XMPPConnectionHelper mXMPPConnectionHelper;

	private WifiManager mWifiManager;

	private Handler mHandler = new Handler();
	private ResultReceiver mResultReceiver;

	private RobotDiscovery mRobotDiscoveryListener = new RobotDiscovery() {

		public void onDiscoveryStarted() {
			if (mResultReceiver != null) {
				mResultReceiver.send(NeatoSmartAppsEventConstants.DISCOVERY_STARTED, null);
			}

		}

		public void onDiscoveryEnd() {
			if (mResultReceiver != null) {
				mResultReceiver.send(NeatoSmartAppsEventConstants.DISCOVERY_END, null);
			}
		}

		public void onRobotDiscovered(final RobotInfo robotInfo) {
			LogHelper.logD(TAG, "onRobotDiscovered called");
			if (mResultReceiver != null) {
				Bundle bundle = new Bundle();
				bundle.putParcelable(DISCOVERY_ROBOT_INFO, robotInfo);
				mResultReceiver.send(NeatoSmartAppsEventConstants.NEW_ROBOT_FOUND, bundle);
			}
		}
	};

	private TcpDataPacketListener mTcpDataPacketListener = new TcpDataPacketListener() {

		public void onConnect() {			
			if (mResultReceiver != null) {
				mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_CONNECTED, null);
			}
		}

		public void onDisconnect() {			
			if (mResultReceiver != null) {
				mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_DISCONNECTED, null);
			}

		}

		public void onDataReceived(RobotPacket robotPacket) {

			if (mResultReceiver != null) {
				mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_DATA_RECEIVED, null);
			}

		}
	};

	private void robotAssociationStarted()
	{
		Intent robotAssociationStartIntent = new Intent(NEATO_UI_UPDATE_ACTION);
		robotAssociationStartIntent.putExtra(EXTRA_ROBOT_ASSOCIATION_START, true);
		sendBroadcast(robotAssociationStartIntent);
	}

	private void robotAssociationFinished()
	{
		Intent robotAssociationFinishIntent = new Intent(NEATO_UI_UPDATE_ACTION);
		robotAssociationFinishIntent.putExtra(EXTRA_ROBOT_ASSOCIATION_START, false);
		sendBroadcast(robotAssociationFinishIntent);
	}


	// TODO: Used for sending messages to robot jabber account. 
	//WARNING: Duplicate code is there while sendning jabber account detaisl via TCP packet in tcp connection helper.

	private static String getJabberChatId(Context context)
	{
		RobotItem robotItem = NeatoPrefs.getRobotItem(context);
		if (robotItem == null) {
			String chatId = AppConstants.JABBER_ROBOT_ID + AppConstants.JABBER_ID_DOMAIN;
			LogHelper.log(TAG, "Hardcoded Robot Chat id = " + chatId);
			return chatId;
		}
		else {
			String chatId = robotItem.getChatId();
			LogHelper.log(TAG, "Robot Chat id = " + chatId);
			return chatId;
		}
	}


	private INeatoRobotService mNeatoRobotService = new INeatoRobotService.Stub() {

		public void startDiscovery() throws RemoteException {
			LogHelper.log(TAG, "startDiscovery Called");
			mUdpConnectionHelper.startDiscovery();
		}

		// TODO : Right now we are sending IP Address. To Decide whether to send whole robot-info.
		public void sendCommand( String ipAddress, int commandId, boolean useXmppServer) throws RemoteException {
			LogHelper.log(TAG, "sendCommand Called");
			RobotPacket robotPacket = new RobotPacket(commandId);
			if (!useXmppServer) {
				if(mTcpConnectionHelper.isConnected(ipAddress)) {
					LogHelper.log(TAG, "SendCommand Called using TCP connection as transport");
					mTcpConnectionHelper.sendRobotCommand(robotPacket, mTcpConnectionHelper.getTransport(ipAddress));

				} else {
					LogHelper.log(TAG, "Tcp peer connection does not exist.");
				}
			}
			else {
				if(mXMPPConnectionHelper.isJabberConnected()) {
					LogHelper.logD(TAG, "SendCommand Called using XMPP connection as transport. Command Id:-" + commandId);

					//TODO : Right now using hard-coded Jabber ID for robot
					String jabberId = XMPPUtils.getRobotJabberId(NeatoSmartAppService.this);
					mXMPPConnectionHelper.sendRobotCommand(robotPacket, jabberId);
				} else {
					LogHelper.logD(TAG, "Xmpp connection does not exist :-" + commandId);
				}
			}
		}

		public void connectToRobot(String ipAddress) throws RemoteException {
			LogHelper.log(TAG, "connectToRobot Called. IpAddress-" +ipAddress);
			// TODO: No need to connect through XMPP. Need to figure out whether there is a need to add to the roster of the smartapp
			// user and, thus, associated the robot to the user here.

			mTcpConnectionHelper.connectToRobot(ipAddress);
		}

		public void cancelDiscovery() throws RemoteException {
			LogHelper.log(TAG, "cancelDiscovery Called");
		}

		//TODO cleanup all the things here. TO be called when we wish to stop the service
		public void cleanup() {
			LogHelper.log(TAG, "cleanup service called");

			mUdpConnectionHelper.cancelDiscovery();
			mUdpConnectionHelper = null;

			//TODO: send the right IP address. As of now we are sending blank from the UI itself.
			if(mTcpConnectionHelper.isConnected(" ")) {
				mTcpConnectionHelper.closePeerConnection(" ");
			}
			//mTcpConnectionHelper.setTcpConnectionListener(null);
			mTcpDataPacketListener =null;
			mTcpConnectionHelper =null;
			mHandler = null;
			mResultReceiver = null;
			mRobotDiscoveryListener = null;
			unregisterReceiver(mWifiStateChange);
			mWifiStateChange = null;
			mXMPPConnectionHelper.disconnectXmppConnection();
			mXMPPConnectionHelper =null;
			stopSelf();
		}

		public void closePeerConnection(String ipAddress)
				throws RemoteException {
			if (mTcpConnectionHelper != null) {
				boolean isConnected = mTcpConnectionHelper.isConnected(ipAddress);
				if (isConnected) {
					mTcpConnectionHelper.closePeerConnection(ipAddress);
				}
			}

		}

		public void associateRobot(String serialId, String emailId)
				throws RemoteException {
		//	CreateRobotIfRequiredAndAssociateToUser associateRobotTask = new CreateRobotIfRequiredAndAssociateToUser(serialId, emailId);
		//	associateRobotTask.execute();
			CreateRobotIfRequiredAndAssociateToUserThread associateRobotTask = new CreateRobotIfRequiredAndAssociateToUserThread(serialId, emailId);
			Thread t = new Thread(associateRobotTask);
			t.start();
		}
	};

	private BroadcastReceiver mWifiStateChange = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if(networkInfo.isConnected()) {
					// Wifi is connected
					LogHelper.logD(TAG, "Connected to a network");
					mXMPPConnectionHelper.JabberUserLogin();

				}
			} 
			else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI && ! networkInfo.isConnected()) {
					// Wifi is disconnected
					LogHelper.logD(TAG, "Disconnecting from the network");
					// There is no API to logout. XMPP will automatically break the connection
				}
			}
		}
	};

	private BroadcastReceiver mResultReceiverBroadcast = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			mResultReceiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mUdpConnectionHelper = new UdpConnectionHelper(this);
		mUdpConnectionHelper.setHandler(mHandler);
		mUdpConnectionHelper.setUdpConnectionListener(mRobotDiscoveryListener);
		mTcpConnectionHelper = new TcpConnectionHelper(this);
		mTcpConnectionHelper.setHandler(mHandler);
		mTcpConnectionHelper.setTcpConnectionListener(mTcpDataPacketListener);
		mXMPPConnectionHelper =  XMPPConnectionHelper.getInstance(this);
		mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		if (isConnectedToWifiNetwork()) {
			mXMPPConnectionHelper.JabberUserLogin();
		}
		registerReceiver(mWifiStateChange, new IntentFilter( WifiManager.NETWORK_STATE_CHANGED_ACTION));
		registerReceiver(mWifiStateChange, new IntentFilter( ConnectivityManager.CONNECTIVITY_ACTION));
		registerReceiver(mResultReceiverBroadcast, new IntentFilter(NEATO_RESULT_RECEIVER_ACTION));
	}



	@Override
	public IBinder onBind(Intent intent) {
		ResultReceiver resultReceiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
		if (resultReceiver != null) {
			mResultReceiver = resultReceiver;
		}
		return mNeatoRobotService.asBinder();
	}



	@Override
	public boolean onUnbind(Intent intent) {
		mResultReceiver = null;
		return super.onUnbind(intent);
	}
	@Override
	public void onDestroy() {
		try {
			if (mWifiStateChange != null) {
				unregisterReceiver(mWifiStateChange);
			}
			unregisterReceiver(mResultReceiverBroadcast);
		}
		catch (Exception e) {
			LogHelper.log(TAG, "Exception in unregisterReceiver", e);
		}
		super.onDestroy();
	}


	//Check to see if the network connection to wifi is available
	public boolean isConnectedToWifiNetwork() {

		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			return true;
		}
		return false;
	}

	private class CreateRobotIfRequiredAndAssociateToUser extends AsyncTask<Void, Void, AssociateNeatoRobotResult>  {

		private String mEmailId;
		private String mRobotSerialId;
		public CreateRobotIfRequiredAndAssociateToUser(String robotSerialId, String emailId)
		{
			mRobotSerialId = robotSerialId;
			mEmailId = emailId;
		}



		@Override
		protected void onPreExecute() {
			robotAssociationStarted();
		}



		@Override
		protected AssociateNeatoRobotResult doInBackground(Void... params) {
			RobotManager robotManager = RobotManager.getInstance(getApplicationContext());
			RobotItem robotItem = robotManager.getRobotDetail(mRobotSerialId);
			LogHelper.log(TAG, "RobotItem = " + robotItem);
			if (robotItem != null) {
				NeatoPrefs.saveRobotInformation(getApplicationContext(), robotItem);
				AssociateNeatoRobotResult associationResult =  NeatoRobotWebservicesHelper.AssociateNeatoRobotRequest(NeatoSmartAppService.this, 
						mEmailId, mRobotSerialId);
				return associationResult;
			}


			return null;
		}

		@Override
		protected void onPostExecute(AssociateNeatoRobotResult result) {
			super.onPostExecute(result);
			if (result == null) {
				mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_FAILED, null);

			} 
			else if (!result.isCompleted()) {
				Bundle resultData = new Bundle();
				resultData.putString(NeatoRobotResultReceiverConstants.RESULT_ASSOCIATION_ERROR_MESSAGE, result.mMessage);
				mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_FAILED, resultData);
			}
			robotAssociationFinished();
		}

	}

	private class CreateRobotIfRequiredAndAssociateToUserThread implements Runnable {
		private String mEmailId;
		private String mRobotSerialId;
		public CreateRobotIfRequiredAndAssociateToUserThread(String robotSerialId, String emailId)
		{
			mRobotSerialId = robotSerialId;
			mEmailId = emailId;
		}


		@Override
		public void run() {
			// TODO Auto-generated method stub
			RobotManager robotManager = RobotManager.getInstance(getApplicationContext());
			RobotItem robotItem = robotManager.getRobotDetail(mRobotSerialId);
			LogHelper.log(TAG, "RobotItem = " + robotItem);
			if (robotItem != null) {
				NeatoPrefs.saveRobotInformation(getApplicationContext(), robotItem);
				AssociateNeatoRobotResult associationResult =  NeatoRobotWebservicesHelper.AssociateNeatoRobotRequest(NeatoSmartAppService.this, 
						mEmailId, mRobotSerialId);
				if(!associationResult.isCompleted()) {
					Bundle resultData = new Bundle();
					resultData.putString(NeatoRobotResultReceiverConstants.RESULT_ASSOCIATION_ERROR_MESSAGE, associationResult.mMessage);
					mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_FAILED, resultData);
					return;
				}
				mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_SUCCESS, null);


			} else {
				mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_FAILED, null);

			}

		}

	}
}
