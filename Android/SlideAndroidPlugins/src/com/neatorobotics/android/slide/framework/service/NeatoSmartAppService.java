package com.neatorobotics.android.slide.framework.service;

import org.jivesoftware.smack.XMPPException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;
import com.neatorobotics.android.slide.framework.tcp.TcpConnectionHelper;
import com.neatorobotics.android.slide.framework.tcp.TcpDataPacketListener;
import com.neatorobotics.android.slide.framework.udp.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.udp.UdpConnectionHelper;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebConstants;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.UserRobotAssociateDisassociateListener;
import com.neatorobotics.android.slide.framework.xmpp.XMPPConnectionHelper;
import com.neatorobotics.android.slide.framework.xmpp.XMPPNotificationListener;
import com.neatorobotics.android.slide.framework.xmpp.XMPPUtils;

public class NeatoSmartAppService extends Service {

	public static final String EXTRA_ROBOT_ASSOCIATION_START = "extra.robot.association.start";
	public static final String EXTRA_ROBOT_ASSOCIATION_STATUS = "extra.robot.association.status";
	public static final String EXTRA_RESULT_RECEIVER = "extra.result_receiver";
	public static final String DISCOVERY_ROBOT_INFO = "robot.info";
	public static final String NEATO_RESULT_RECEIVER_ACTION = "com.neato.simulator.result_receiver.action";
	public static final String NEATO_UI_UPDATE_ACTION = "com.neato.simulator.ui.update.action";
	private static final String TAG = NeatoSmartAppService.class.getSimpleName();
	private UdpConnectionHelper mUdpConnectionHelper;
	private TcpConnectionHelper mTcpConnectionHelper;
	private XMPPConnectionHelper mXMPPConnectionHelper;

	private Handler mHandler = new Handler();
	private ResultReceiver mResultReceiver;
	
	private XMPPNotificationListener xmppNotificationListener = new XMPPNotificationListener() {

		@Override
		public void onConnectFailed() {
			LogHelper.log(TAG, "XMPP Connection failed");
			
		}

		@Override
		public void onConnectSucceeded() {
			LogHelper.log(TAG, "XMPP Connection Succeeded");
		}

		@Override
		public void onLoginFailed() {
			LogHelper.log(TAG, "XMPP Login failed");
		}

		@Override
		public void onLoginSucceeded() {
			LogHelper.log(TAG, "XMPP Login succeeded");
		}

		@Override
		public void onConnectionReset() {
			LogHelper.log(TAG, "XMPP Connection reset");
		}

		@Override
		public void onDisconnect() {
			LogHelper.log(TAG, "XMPP disconnected");
		}

		@Override
		public void onDataReceived(String from, RobotPacket robotCommand) {
			LogHelper.log(TAG, "XMPP onDataReceived. Data = " + robotCommand);
		}
		
	};

	private RobotDiscoveryListener mRobotDiscoveryListener = new RobotDiscoveryListener() {

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

		public void onConnect(String robotId) {			
			if (mResultReceiver != null) {
				mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_CONNECTED, null);
			}
		}

		public void onDisconnect(String robotId) {			
			if (mResultReceiver != null) {
				mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_DISCONNECTED, null);
			}
		}

		public void onDataReceived(String robotId, RobotPacket robotPacket) {
			LogHelper.log(TAG, "Command Received from Remote Control with Command Id - " + robotPacket.getCommandId());
			if (mResultReceiver != null) {
				mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_DATA_RECEIVED, null);
			}
		}

		@Override
		public void errorInConnecting(String robotId) {
			if (mResultReceiver != null) {
				mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_CONNECTION_ERROR, null);
			}
		}
	};

	private void robotAssociationFinished(int statuscode)
	{
		Intent robotAssociationFinishIntent = new Intent(NEATO_UI_UPDATE_ACTION);
		robotAssociationFinishIntent.putExtra(EXTRA_ROBOT_ASSOCIATION_START, false);
		robotAssociationFinishIntent.putExtra(EXTRA_ROBOT_ASSOCIATION_STATUS, statuscode);
		sendBroadcast(robotAssociationFinishIntent);
	}


	private INeatoRobotService mNeatoRobotService = new INeatoRobotService.Stub() {

		public void startDiscovery() throws RemoteException {
			LogHelper.log(TAG, "startDiscovery Called");
			mUdpConnectionHelper.startDiscovery();
		}

		public void sendCommand(String robotId, int commandId) throws RemoteException {
			LogHelper.log(TAG, "sendCommand2 Called");
			// TODO: Send with command params which we get. Right now we assume there are no commandParams.
			// Once we get Command parameters , we will add those to RobotPacketBundle.
			RobotPacket robotPacket = new RobotPacket(commandId);
			
			if(mTcpConnectionHelper.isConnected(robotId)) {
				LogHelper.log(TAG, "SendCommand Called using TCP connection as transport");
				mTcpConnectionHelper.sendRobotCommand(robotId, robotPacket);
			} 
			else {
				LogHelper.log(TAG, "Tcp peer connection does not exist.");
				if(mXMPPConnectionHelper.isConnected()) {
					LogHelper.logD(TAG, "SendCommand Called using XMPP connection as transport. Command Id:-" + commandId);

					String chatId = XMPPUtils.getRobotChatId(NeatoSmartAppService.this, robotId);
					mXMPPConnectionHelper.sendRobotCommand(chatId, robotPacket);
				} else {
					LogHelper.logD(TAG, "Xmpp connection does not exist: Command Id = " + commandId);
				}
			}
		}
		
		public void connectToRobot(String robot_id) throws RemoteException {
			LogHelper.log(TAG, "connectToRobot Called. robot_id-" + robot_id);			
			RobotDiscoveryListener associatedRobotDiscoveryListener = new RobotDiscoveryListener() {
				RobotInfo mRobotInfo =null;
				public void onDiscoveryStarted() {

				}

				public void onDiscoveryEnd() {
					LogHelper.logD(TAG, "onDiscoveryEnd called");
					Bundle bundle = new Bundle();
					bundle.putParcelable(DISCOVERY_ROBOT_INFO, mRobotInfo);
					if (mRobotInfo != null) 
					{
						mTcpConnectionHelper.connectToRobot(mRobotInfo.getSerialId(), mRobotInfo.getRobotIpAddress());
					} 
					else {
						LogHelper.logD(TAG, "Associated robot not found in the network. Could not form Direct connection.");
						if (mResultReceiver != null) {
							mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_CONNECTION_ERROR, null);
						}
					}
				}

				public void onRobotDiscovered(final RobotInfo robotInfo) {
					LogHelper.logD(TAG, "onAssociatedRobotDiscovered called");
					mRobotInfo = robotInfo;
				}
			};
			mUdpConnectionHelper.discoverAssociatedRobot(robot_id, associatedRobotDiscoveryListener);
		}

		public void cancelDiscovery() throws RemoteException {
			LogHelper.log(TAG, "cancelDiscovery Called");
		}

		//TODO cleanup all the things here. TO be called when we wish to stop the service
		public void cleanup() {
			LogHelper.log(TAG, "cleanup service called");

			mUdpConnectionHelper.cancelDiscovery();
			mUdpConnectionHelper = null;

			if(mTcpConnectionHelper.isConnected("")) {
				mTcpConnectionHelper.closePeerConnection("");
			}
			//mTcpConnectionHelper.setTcpConnectionListener(null);
			mTcpDataPacketListener =null;
			mTcpConnectionHelper =null;
			mHandler = null;
			mResultReceiver = null;
			mRobotDiscoveryListener = null;
			unregisterReceiver(mWifiStateChange);
			mWifiStateChange = null;
			mXMPPConnectionHelper.close();
			mXMPPConnectionHelper =null;
			
			// Delete logged-in user related info from the DB & shared preference
			UserHelper.logout(getApplicationContext());
			
			stopSelf();
		}

		public void closePeerConnection(String robotId)
				throws RemoteException {
			if (mTcpConnectionHelper != null) {
				boolean isConnected = mTcpConnectionHelper.isConnected(robotId);
				if (isConnected) {
					mTcpConnectionHelper.closePeerConnection(robotId);
				} else {
					// This should not be hit., Still catching the if at all.
					LogHelper.logD(TAG, "Close peer connection when peer already closed.");
					NeatoPrefs.setPeerConnectionStatus(getApplicationContext(), false);
					mTcpDataPacketListener.onDisconnect(robotId);
				}
			}

		}

		public void associateRobot(String serialId, String emailId)
				throws RemoteException {
			
			UserManager.getInstance(getApplicationContext()).associateRobot(serialId, emailId, new UserRobotAssociateDisassociateListener() {
				
				@Override
				public void onServerError(String errorMessage) {
					LogHelper.log(TAG, "associateRobot onServerError");
					mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_FAILED, null);
					robotAssociationFinished(NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_FAILED);
				}
				
				@Override
				public void onNetworkError(String errorMessage) {
					LogHelper.log(TAG, "associateRobot onNetworkError");
					mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_FAILED, null);
					robotAssociationFinished(NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_FAILED);
				}
				
				@Override
				public void onComplete() {
					LogHelper.log(TAG, "associateRobot onComplete");
					mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_SUCCESS, null);
					robotAssociationFinished(NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_SUCCESS);

				}
			});
		}

		@Override
		public void loginToXmpp() throws RemoteException {
			LogHelper.log(TAG, "loginToXmpp called");
			if(mXMPPConnectionHelper != null) {
				LogHelper.log(TAG, "mXMPPConnectionHelper is not null");
				Runnable task = new Runnable() {

					@Override
					public void run() {
						loginToXmppServer();
					}
				};
				TaskUtils.scheduleTask(task, 0);
			}

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
					Runnable task = new Runnable() {

						@Override
						public void run() {
							loginToXmppServer();
						}
					};
					TaskUtils.scheduleTask(task, 0);
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
		
		NeatoWebConstants.setServerEnvironment(NeatoWebConstants.STAGING_SERVER_ID);
		
		mUdpConnectionHelper = new UdpConnectionHelper(this);
		mUdpConnectionHelper.setHandler(mHandler);
		mUdpConnectionHelper.setUdpConnectionListener(mRobotDiscoveryListener);
		mTcpConnectionHelper = new TcpConnectionHelper(this);
		mTcpConnectionHelper.setHandler(mHandler);
		mTcpConnectionHelper.setTcpConnectionListener(mTcpDataPacketListener);
		mXMPPConnectionHelper =  XMPPConnectionHelper.getInstance(this);
		String xmppDomain = NeatoWebConstants.getXmppServerDomain();
		mXMPPConnectionHelper.setServerInformation(xmppDomain, AppConstants.JABBER_SERVER_PORT, AppConstants.JABBER_WEB_SERVICE);
		mXMPPConnectionHelper.setXmppNotificationListener(xmppNotificationListener, mHandler);
		
		if (isConnectedToWifiNetwork()) {
			Runnable task = new Runnable() {

				@Override
				public void run() {
					loginToXmppServer();
				}
			};
			TaskUtils.scheduleTask(task, 0);
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
	
	private Object mXmppConnectionLock = new Object();
	
	private void loginToXmppServer() {
		try {
			synchronized (mXmppConnectionLock) {
				LogHelper.log(TAG, "loginToXmppServer called");
				mXMPPConnectionHelper.close();
				
				LogHelper.logD(TAG, "closed existing connection. Now retring to connect");
				mXMPPConnectionHelper.connect();
				LogHelper.logD(TAG, "connected. Now loging in");
				String userId = getUserChatId();
				String password = getUserChatPassword();
				LogHelper.log(TAG, "userId = " + userId);
				mXMPPConnectionHelper.login(userId, password);
			}
		}
		catch (XMPPException e) {
			LogHelper.log(TAG, "Exception in connecting to XMPP server", e);
		}
	}

	private String getUserChatId() {
		String jabberUserId = UserHelper.getChatId(this);		
		jabberUserId = XMPPUtils.removeJabberDomain(jabberUserId);
		return jabberUserId;
	}
	private String getUserChatPassword() {
		String jabberUserPwd = UserHelper.getChatPwd(this);
		return jabberUserPwd;
	}
}
