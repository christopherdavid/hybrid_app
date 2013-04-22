package com.neatorobotics.android.slide.framework.service;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.XMPPException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.text.TextUtils;
import android.util.SparseBooleanArray;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.R;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.plugins.RobotConstants;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.resultreceiver.NeatoRobotResultReceiverConstants;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RequestPacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.ResponsePacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacketHeader;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotRequests;
import com.neatorobotics.android.slide.framework.tcp.RobotPeerConnection;
import com.neatorobotics.android.slide.framework.tcp.RobotPeerDataListener;
import com.neatorobotics.android.slide.framework.tcp.TcpConnectionHelper;
import com.neatorobotics.android.slide.framework.tcp.TcpDataPacketListener;
import com.neatorobotics.android.slide.framework.udp.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.udp.RobotDiscoveryService;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebConstants;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.xmpp.XMPPConnectionHelper;
import com.neatorobotics.android.slide.framework.xmpp.XMPPNotificationListener;
import com.neatorobotics.android.slide.framework.xmpp.XMPPUtils;

public class NeatoSmartAppService extends Service {
	
	private static final String TAG = NeatoSmartAppService.class.getSimpleName();
	
	public static final String EXTRA_RESULT_RECEIVER = "extra.result_receiver";
	public static final String DISCOVERY_ROBOT_INFO = "robot.info";
	public static final String NEATO_RESULT_RECEIVER_ACTION = "com.neato.simulator.result_receiver.action";
	
	private TcpConnectionHelper mTcpConnectionHelper;
	private XMPPConnectionHelper mXMPPConnectionHelper;
	private RobotPeerConnection mRobotPeerConnection;
	
	private static final int COMMAND_PACKET_SIGNATURE = 0xCafeBabe;
	private static final int COMMAND_PACKET_VERSION = 1;
	private static final int ROBOT_NOTIFICATION_ID = 100; // arbitrary number
	
	private Handler mHandler = new Handler();
	private ResultReceiver mResultReceiver;
	private static SparseBooleanArray notificationCommands = new SparseBooleanArray();
	
	static {
		// Add more notification commands here
		// Notification commands are always sent to the plugin layers
		notificationCommands.append(RobotCommandPacketConstants.COMMAND_GET_ROBOT_STATE, true);
		notificationCommands.append(RobotCommandPacketConstants.COMMAND_STATUS_NOTIFICATION, true);
		notificationCommands.append(RobotCommandPacketConstants.COMMAND_REGISTER_STATUS_NOTIFICATIONS, true);
		notificationCommands.append(RobotCommandPacketConstants.COMMAND_UNREGISTER_STATUS_NOTIFICATIONS, true);
	}


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

		@Override
		public void onDataReceived(String from, RobotCommandPacket packet) {
			LogHelper.log(TAG, "TODO: XMPP onDataReceived. New Packet Data = " + packet);
			sendNotification(packet);
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
			
			LogHelper.log(TAG, "Command Received from Remote Control - " + robotPacket.toString());
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

	private RobotPeerDataListener mRobotPeerDataListener = new RobotPeerDataListener() {

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

		@Override
		public void errorInConnecting(String robotId) {
			if (mResultReceiver != null) {
				mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_CONNECTION_ERROR, null);
			}
		}

		@Override
		public void onDataReceived(String robotId, RobotCommandPacket robotPacket) {
			LogHelper.log(TAG, "onDataReceived. Packet = " + robotPacket);
			if (isNotificationCommand(robotPacket)) {
				sendNotification(robotPacket);
				return;
			}
			if (!shouldNotifyUi(robotId, robotPacket)) {
				return;
			}
			if (mResultReceiver == null) {
				return;
			}
				

			LogHelper.log(TAG, "Need to notify the UI");

			Bundle bundle = new Bundle();
			bundle.putString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID, robotId);
			bundle.putBoolean(NeatoRobotResultReceiverConstants.KEY_IS_REQUEST_PACKET, robotPacket.isRequest());
						
			if (robotPacket.isResponse()) {
				LogHelper.log(TAG, "Command Response from Remote Control = " + robotPacket);
				bundle.putParcelable(NeatoRobotResultReceiverConstants.KEY_REMOTE_RESPONSE_PACKET, robotPacket.getCommandResponse());
			}
			else {
				LogHelper.log(TAG, "Command Requests from Remote Control = "+ robotPacket);
				bundle.putParcelable(NeatoRobotResultReceiverConstants.KEY_REMOTE_REQUEST_PACKET, robotPacket.getRobotCommands());
			}
			mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_PACKET_RECEIVED_ON_PEER_CONNETION, bundle);
		}
	};	

	// Hook point to indicate, if the request/response should be sent to the UI
	// TODO: As of now we are returning true and also we are not handling
	// any response here. We may need to handle command/response here
	private boolean shouldNotifyUi(String robotId, RobotCommandPacket robotPacket) {
		return true;
	}
	

	private boolean isNotificationCommand(RobotCommandPacket robotPacket) {
		if (robotPacket.isResponse()) {
			ResponsePacket responsePacket =  robotPacket.getCommandResponse();
			
			String commandData = responsePacket.getParams().get(RobotCommandPacketConstants.KEY_REQUEST_COMMAND);
			LogHelper.log(TAG, "isNotificationCommandResponse - Command = " + commandData);
			if (!TextUtils.isEmpty(commandData)) {
				int commandId = Integer.valueOf(commandData);	
				return notificationCommands.get(commandId);
			}
		} 
		return false;
	}
	
	private void sendNotification(RobotCommandPacket robotPacket) {		
		if (robotPacket.isResponse()) {
			sendResponseNotification(robotPacket);
		} else if (robotPacket.isRequest()) {
			sendRequestNotification(robotPacket);
		}
	}


	private void sendRequestNotification(RobotCommandPacket robotPacket) {
		RobotRequests requests = robotPacket.getRobotCommands();
		int requestSize = requests.getNumberOfCommands();
		for (int i = 0; i < requestSize; i++) {
			int commandId = requests.getCommand(i).getCommand();
			Bundle requestDataParams = getParamsBundle(requests.getCommand(i).getCommandParams());
			switch (commandId) {			
			case RobotCommandPacketConstants.COMMAND_DIRT_BAG_FULL:	
				LogHelper.log(TAG, "COMMAND_DIRT_BAG_FULL");
				notifyRobotState(getApplicationContext(), getString(R.string.notification_text_dirt_bag_full));
				if (mResultReceiver != null) {
					mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_DIRT_BAG_FULL, requestDataParams);
				}
				break;
			case RobotCommandPacketConstants.COMMAND_ROBOT_STUCK:
				LogHelper.logD(TAG, "COMMAND_ROBOT_STUCK");
				notifyRobotState(getApplicationContext(), getString(R.string.notification_text_robot_stuck));
				if (mResultReceiver != null) {
					mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_STUCK, requestDataParams);
				}
				break;
			}
		}
	}


	private void sendResponseNotification(RobotCommandPacket robotPacket) {
		ResponsePacket responsePacket =  robotPacket.getCommandResponse();
		
		String commandData = responsePacket.getParams().get(RobotCommandPacketConstants.KEY_REQUEST_COMMAND);
		if (!TextUtils.isEmpty(commandData)) {
			int commandId = Integer.valueOf(commandData);				
			if (mResultReceiver != null) {
				
				LogHelper.log(TAG, "sendNotification for commandId = " + commandId);
				Bundle responseDataParams = getParamsBundle(responsePacket.getParams());		
				switch (commandId) {			
					case RobotCommandPacketConstants.COMMAND_GET_ROBOT_STATE:				
						mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_STATE, responseDataParams);
						break;
						
					case RobotCommandPacketConstants.COMMAND_STATUS_NOTIFICATION:
						mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_STATUS_NOTIFICATION, responseDataParams);
						break;
						
					case RobotCommandPacketConstants.COMMAND_REGISTER_STATUS_NOTIFICATIONS:				
						mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_REGISTER_STATUS_NOTIFICATIONS, responseDataParams);
						break;
						
					case RobotCommandPacketConstants.COMMAND_UNREGISTER_STATUS_NOTIFICATIONS:
						mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_UNREGISTER_STATUS_NOTIFICATIONS, responseDataParams);
						break;
				}
			}
		}
	}
	
	
	
	private void notifyRobotState(Context context, String notification) {
		LogHelper.logD(TAG, "generateNotification:" + notification);
		String robotState = getString(R.string.notification_text_robot_state);
		PendingIntent intent = getNotificationIntent(context, R.drawable.ic_launcher, notification, robotState);
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Notification n = new Notification();
		n.flags = Notification.FLAG_AUTO_CANCEL;
		n.icon = R.drawable.ic_launcher;
		n.tickerText = notification;
		n.setLatestEventInfo(context, notification, robotState, intent);

		nm.notify(ROBOT_NOTIFICATION_ID, n);
	}
	
	private PendingIntent getNotificationIntent(Context context, int icon, String title, String text)
	{
		Intent notifyIntent = new Intent();
		notifyIntent.setAction(RobotConstants.ROBOT_NOTIFICATION_ACTION);
		notifyIntent.addCategory(Intent.CATEGORY_DEFAULT);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notifyIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		return pendingIntent;
	}
	
	private Bundle getParamsBundle(Map<String, String> params) {
		Bundle dataParams = new Bundle();
		for (String key: params.keySet()) {
			if (!key.equalsIgnoreCase(RobotCommandPacketConstants.KEY_REQUEST_COMMAND)) {
				dataParams.putString(key, params.get(key));
			}
		}
		
		return dataParams;
	}
	

	private void initializeOldPeerHelperClassIfRequired()
	{
		if (mTcpConnectionHelper == null) {
			LogHelper.logD(TAG, "Using old Command structure");
			mTcpConnectionHelper = new TcpConnectionHelper(this);
			mTcpConnectionHelper.setHandler(mHandler);
			mTcpConnectionHelper.setTcpConnectionListener(mTcpDataPacketListener);
		}
	}
	
	private void initializeNewPeerHelperClassIfRequired()
	{
		if (mRobotPeerConnection == null) {
			LogHelper.logD(TAG, "Using new Command structure");
			mRobotPeerConnection = new RobotPeerConnection(this);
			mRobotPeerConnection.setHandler(mHandler);
			mRobotPeerConnection.setPeerDataListener(mRobotPeerDataListener);
		}
	}
	

	private INeatoRobotService mNeatoRobotService = new INeatoRobotService.Stub() {

		public void startDiscovery() throws RemoteException {
			LogHelper.log(TAG, "startDiscovery Called");
			String userId = AppUtils.getLoggedInUserId(NeatoSmartAppService.this);
			RobotDiscoveryService.startRobotsDiscovery(getApplicationContext(), 
					userId, mRobotDiscoveryListener);
		}

		public void sendCommand(String robotId, int commandId) throws RemoteException {
			LogHelper.log(TAG, "sendCommand Called");
			
			sendCommandUsingOldCommandStructure(robotId, commandId);
			
		}

		public void sendCommand2(String robotId, RobotRequests requests) throws RemoteException {
			LogHelper.logD(TAG, "sendCommand Called. Using new command structure");
			sendCommandUsingNewCommandStructure(robotId, requests);
		} 


		public void connectToRobot(String robotId) throws RemoteException {
			LogHelper.log(TAG, "connectToRobot Called. robotId -" + robotId);
			
			initializeOldPeerHelperClassIfRequired();
			if (mTcpConnectionHelper != null) {
				LogHelper.logD(TAG, "Using old command structure");
				mTcpConnectionHelper.connectToRobot(robotId);
			}
		}
		
		public void connectToRobot2(String robotId) throws RemoteException {
			LogHelper.log(TAG, "connectToRobot2 Called. robotId -" + robotId);
			
			initializeNewPeerHelperClassIfRequired();
			if (mRobotPeerConnection != null) {
				LogHelper.logD(TAG, "Using new command structure");
				mRobotPeerConnection.connectToRobot(robotId);
			}
		}

		public void cancelDiscovery() throws RemoteException {
			LogHelper.log(TAG, "cancelDiscovery Called");
		}

		//TODO cleanup all the things here. TO be called when we wish to stop the service
		public void cleanup() {
			LogHelper.log(TAG, "cleanup service called");

			if((mTcpConnectionHelper != null) && (mTcpConnectionHelper.isConnected(""))) {
				mTcpConnectionHelper.closePeerConnection("");

			}
			if (mRobotPeerConnection != null) {
				mRobotPeerConnection.closeExistingPeerConnection();
			}
			mTcpDataPacketListener =null;
			mTcpConnectionHelper =null;
			mRobotPeerConnection = null;
			mRobotPeerDataListener = null;
			mHandler = null;
			mResultReceiver = null;
			mRobotDiscoveryListener = null;
			unregisterReceiver(mWifiStateChange);
			mWifiStateChange = null;
			mXMPPConnectionHelper.close();
			mXMPPConnectionHelper =null;
			
			stopSelf();
		}

		public void closePeerConnection(String robotId)
				throws RemoteException {
			
			if (mRobotPeerConnection != null) {
				boolean isConnected = mRobotPeerConnection.isPeerRobotConnected(robotId);
				if (isConnected) {
					mRobotPeerConnection.closePeerRobotConnection(robotId);
				} else {
					// This should not be hit., Still catching the if at all.
					LogHelper.logD(TAG, "Close peer connection when peer already closed.");
					NeatoPrefs.setPeerConnectionStatus(getApplicationContext(), false);
					mRobotPeerDataListener.onDisconnect(robotId);
				}
			}
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
		
		public void registerRobotNotifications(String robotId) throws RemoteException {			
			LogHelper.logD(TAG, "registerRobotNotifications called for RobotId = " + robotId);
			UserItem userItem = UserHelper.getLoggedInUserDetails(NeatoSmartAppService.this);			
			Map<String, String> requestParams = new HashMap<String, String>();
			requestParams.put(RobotCommandPacketConstants.KEY_USER_ID, userItem.id);
			requestParams.put(RobotCommandPacketConstants.KEY_CHAT_ID, userItem.chat_id);
			
			RequestPacket request = RequestPacket.createRobotCommandWithParams(RobotCommandPacketConstants.COMMAND_REGISTER_STATUS_NOTIFICATIONS, requestParams);
			String requestId = AppUtils.generateNewRequestId(getApplicationContext()); 
			request.setRequestId(requestId);
			RobotRequests robotRequests = new RobotRequests();
			robotRequests.addCommand(request);
	
			sendCommandUsingNewCommandStructure(robotId, robotRequests);
		}
		
		public void unregisterRobotNotifications(String robotId) throws RemoteException {
			LogHelper.logD(TAG, "unregisterRobotNotifications called for RobotId = " + robotId);
			UserItem userItem = UserHelper.getLoggedInUserDetails(NeatoSmartAppService.this);
			Map<String, String> requestParams = new HashMap<String, String>();
			requestParams.put(RobotCommandPacketConstants.KEY_USER_ID, userItem.id);			
			
			RequestPacket request = RequestPacket.createRobotCommandWithParams(RobotCommandPacketConstants.COMMAND_UNREGISTER_STATUS_NOTIFICATIONS,
																				requestParams);
			String requestId = AppUtils.generateNewRequestId(getApplicationContext()); 
			request.setRequestId(requestId);
			
			RobotRequests robotRequests = new RobotRequests();
			robotRequests.addCommand(request);
			
			sendCommandUsingNewCommandStructure(robotId, robotRequests);
		};
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
	
	private String getFormattedServerInfo()
	{
		return String.format("%s (%s)", NeatoWebConstants.getServerUrl(), NeatoWebConstants.getServerName());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		NeatoWebConstants.setServerEnvironment(NeatoWebConstants.STAGING_SERVER_ID);
		
		LogHelper.log(TAG, "Server information = " + getFormattedServerInfo());
		
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
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogHelper.logD(TAG, "startId is " + startId);
		Bundle bundle = intent.getBundleExtra(PushNotificationConstants.EXTRA_NOTIFICATION_BUNDLE);
		if(bundle != null) {
			LogHelper.logD(TAG, "called from notification, bundle message is " + bundle.getString(PushNotificationConstants.NOTIFICATION_TICKER_KEY));
//			sendPushNotificationToUI(bundle);
		}
		return super.onStartCommand(intent, flags, startId);
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

	private void sendCommandUsingOldCommandStructure(String robotId, int commandId) {
		
		LogHelper.log(TAG, "sendCommandUsingOldCommandStructure called");
		RobotPacket robotPacket = new RobotPacket(commandId);
		
		if((mTcpConnectionHelper != null) && (mTcpConnectionHelper.isConnected(robotId))) {
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

	private void sendCommandUsingNewCommandStructure(String robotId, RobotRequests requests) {
		
		LogHelper.log(TAG, "sendCommandUsingNewCommandStructure called");
		RobotCommandPacketHeader header = getRobotCommandHeader();
		if(isPeerConnectionExists(robotId)) {
			LogHelper.log(TAG, "SendCommand Called using TCP connection as transport");
			requests.setDistributionMode(RobotPacketConstants.DISTRIBUTION_MODE_TYPE_PEER);
			//Form robot packet after setting all request parameters.
			RobotCommandPacket robotCommandPacket = RobotCommandPacket.createRobotCommandPacket(header, requests);
			mRobotPeerConnection.sendRobotCommand(robotId, robotCommandPacket);
		} 
		else {
			LogHelper.log(TAG, "Tcp peer connection does not exist.");
			
			if(isXmppConnectionExists(robotId)) {
				String chatId = XMPPUtils.getRobotChatId(NeatoSmartAppService.this, robotId);
				LogHelper.logD(TAG, "SendCommand Called using XMPP connection as transport. Request:-" + requests);
				requests.setDistributionMode(RobotPacketConstants.DISTRIBUTION_MODE_TYPE_XMPP);
				//Form robot packet after setting all request parameters.
				RobotCommandPacket robotCommandPacket = RobotCommandPacket.createRobotCommandPacket(header, requests);
				mXMPPConnectionHelper.sendRobotCommand(chatId, robotCommandPacket);
			} else {
				LogHelper.logD(TAG, "Xmpp connection does not exist: Request = " + requests);
			}
		}
	}
	
	private boolean isPeerConnectionExists(String robotId)
	{
		return ((mRobotPeerConnection!= null) && mRobotPeerConnection.isPeerRobotConnected(robotId));
	}
	
	private boolean isXmppConnectionExists(String robotId)
	{
		return ((mXMPPConnectionHelper!= null) && mXMPPConnectionHelper.isConnected());
	}
	
	private RobotCommandPacketHeader getRobotCommandHeader() {
		RobotCommandPacketHeader header = new RobotCommandPacketHeader();
		header.setSignature(COMMAND_PACKET_SIGNATURE);
		header.setVersion(COMMAND_PACKET_VERSION);
		return header;
	}
	
}
