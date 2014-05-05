package com.neatorobotics.android.slide.framework.service;

import org.jivesoftware.smack.XMPPException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.resultreceiver.NeatoRobotResultReceiverConstants;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RequestPacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacketHeader;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotRequests;
import com.neatorobotics.android.slide.framework.robotdata.RobotDataManager;
import com.neatorobotics.android.slide.framework.tcp.RobotPeerConnection;
import com.neatorobotics.android.slide.framework.tcp.RobotPeerDataListener;
import com.neatorobotics.android.slide.framework.timedmode.RobotCommandTimerHelper;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.NetworkConnectionUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebConstants;
import com.neatorobotics.android.slide.framework.xmpp.XMPPConnectionHelper;
import com.neatorobotics.android.slide.framework.xmpp.XMPPNotificationListener;
import com.neatorobotics.android.slide.framework.xmpp.XMPPUtils;

public class NeatoSmartAppService extends Service {

    private static final String TAG = NeatoSmartAppService.class.getSimpleName();

    public static final String EXTRA_RESULT_RECEIVER = "extra.result_receiver";
    public static final String NEATO_RESULT_RECEIVER_ACTION = "com.neato.simulator.result_receiver.action";

    private Handler mHandler = new Handler();
    private ResultReceiver mResultReceiver;

    private XMPPConnectionHelper mXMPPConnectionHelper;
    private RobotPeerConnection mRobotPeerConnection;

    private boolean isDataChangedCommand(RequestPacket request) {
        int commandId = request.getCommand();
        return (commandId == RobotCommandPacketConstants.COMMAND_ROBOT_PROFILE_DATA_CHANGED);
    }

    private void processDataChangedRequest(String from, RequestPacket request) {
        LogHelper.log(TAG, "Data changed on server for robot");
        String robotId = request.getCommandParam(RobotCommandPacketConstants.KEY_ROBOT_ID);

        String causeAgentId = request.getCommandParam(RobotCommandPacketConstants.KEY_CAUSE_AGENT_ID);
        if (!TextUtils.isEmpty(causeAgentId)) {
            if (causeAgentId.equals(NeatoPrefs.getNeatoUserDeviceId(getApplicationContext()))) {
                LogHelper.log(TAG, "Causing Agent Matched. Ignore Data changed notification");
                return;
            }
        }

        if (TextUtils.isEmpty(robotId)) {
            LogHelper.log(TAG, "Robot Id is empty in data changed command.");
            robotId = NeatoPrefs.getManagedRobotSerialId(getApplicationContext());
        }

        if (causeAgentId.equalsIgnoreCase(robotId)) {
            LogHelper.logD(TAG,
                    "packet is received from robot cause agent id. Stop the command expiry timer if running.");
            RobotCommandTimerHelper.getInstance(getApplicationContext()).stopCommandTimerIfRunning(robotId);
        } else {
            LogHelper.logD(TAG, "packet is not received from robot chat id.");
        }

        RobotDataManager.getServerData(getApplicationContext(), robotId);
    }

    private void initializePeerHelperIfRequired() {
        if (mRobotPeerConnection == null) {
            LogHelper.logD(TAG, "Using new Command structure");
            mRobotPeerConnection = new RobotPeerConnection(this);
            mRobotPeerConnection.setHandler(mHandler);
            mRobotPeerConnection.setPeerDataListener(mRobotPeerDataListener);
        }
    }

    private INeatoRobotService mNeatoRobotService = new INeatoRobotService.Stub() {

        public void sendCommand(String robotId, RobotRequests requests, int mode) throws RemoteException {
            LogHelper.logD(TAG, "sendCommand Called. Using new command structure with mode - " + mode);
            RobotCommandPacketHeader header = RobotCommandPacketHeader.getRobotCommandHeader(
                    RobotCommandPacketConstants.COMMAND_PACKET_SIGNATURE,
                    RobotCommandPacketConstants.COMMAND_PACKET_VERSION);
            if (mode == RobotPacketConstants.DISTRIBUTION_MODE_TYPE_PEER) {
                if (isPeerConnectionExists(robotId)) {
                    LogHelper.logD(TAG, "SendCommand Called using TCP connection as transport");
                    requests.setDistributionMode(RobotPacketConstants.DISTRIBUTION_MODE_TYPE_PEER);
                    RobotCommandPacket robotCommandPacket = RobotCommandPacket.createRobotCommandPacket(header,
                            requests);
                    mRobotPeerConnection.sendRobotCommand(robotId, robotCommandPacket);
                } else {
                    LogHelper.logD(TAG, "peer connection does not exist: Request = " + requests);
                }
            } else if (mode == RobotPacketConstants.DISTRIBUTION_MODE_TYPE_XMPP) {
                if (isXmppConnectionExists(robotId)) {
                    String chatId = XMPPUtils.getRobotChatId(NeatoSmartAppService.this, robotId);
                    LogHelper.logD(TAG, "SendCommand Called using XMPP connection as transport. Request:-" + requests);
                    requests.setDistributionMode(RobotPacketConstants.DISTRIBUTION_MODE_TYPE_XMPP);
                    RobotCommandPacket robotCommandPacket = RobotCommandPacket.createRobotCommandPacket(header,
                            requests);
                    mXMPPConnectionHelper.sendRobotCommand(chatId, robotCommandPacket);
                } else {
                    LogHelper.logD(TAG, "Xmpp connection does not exist: Request = " + requests);
                }
            }
        }

        public void connectToRobot3(String robotId, String robotIpAddress) throws RemoteException {
            LogHelper.log(TAG, "connectToRobot3 Called. robotId -" + robotId);

            initializePeerHelperIfRequired();
            if (mRobotPeerConnection != null) {
                LogHelper.logD(TAG, "Using new command structure");
                mRobotPeerConnection.connectToRobot(robotId, robotIpAddress);
            }
        }

        // cleanup all the things here. TO be called when we wish to stop the
        // service
        public void cleanup() {
            LogHelper.log(TAG, "cleanup service called");

            if (mRobotPeerConnection != null) {
                mRobotPeerConnection.closeExistingPeerConnection();
            }

            mRobotPeerConnection = null;
            mRobotPeerDataListener = null;
            mHandler = null;
            mResultReceiver = null;
            unregisterReceiver(mWifiStateChange);
            mWifiStateChange = null;
            mXMPPConnectionHelper.close();
            mXMPPConnectionHelper = null;

            stopSelf();
        }

        public void closePeerConnection(String robotId) throws RemoteException {

            if (mRobotPeerConnection != null) {
                boolean isConnected = mRobotPeerConnection.isPeerRobotConnected(robotId);
                if (isConnected) {
                    mRobotPeerConnection.closePeerRobotConnection(robotId);
                } else {
                    // This should not be hit., Still catching the if at all.
                    LogHelper.logD(TAG, "Close peer connection when peer already closed.");
                    mRobotPeerDataListener.onDisconnect(robotId);
                }
            }
        }

        @Override
        public void loginToXmpp() throws RemoteException {
            LogHelper.log(TAG, "loginToXmpp called");
            if (mXMPPConnectionHelper != null) {
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

        @Override
        public boolean isRobotDirectConnected(String robotId) throws RemoteException {
            if (mRobotPeerConnection != null) {
                return mRobotPeerConnection.isConnectedAndPingRobot(robotId);
            }
            return false;
        };

        @Override
        public boolean isAnyPeerConnectionExists() throws RemoteException {
            if (mRobotPeerConnection != null) {
                return mRobotPeerConnection.isConnectedAndSendPingPacket();
            }
            return false;
        }

        @Override
        public void loginToXmppIfRequired() throws RemoteException {
            LogHelper.log(TAG, "loginToXmppIfRequired called");
            if (mXMPPConnectionHelper != null) {
                Runnable task = new Runnable() {

                    @Override
                    public void run() {
                        loginToXmppServerIfNotConnected();
                    }
                };
                TaskUtils.scheduleTask(task, 0);
            }
        }

    };

    private BroadcastReceiver mWifiStateChange = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (NetworkConnectionUtils.hasNetworkConnection(getApplicationContext())) {
                    // network is connected
                    LogHelper.logD(TAG, "Connected to a network");
                    Runnable task = new Runnable() {

                        @Override
                        public void run() {
                            loginToXmppServer();
                        }
                    };
                    TaskUtils.scheduleTask(task, 0);
                } else {
                    // Wifi is disconnected
                    LogHelper.logD(TAG, "Disconnecting from the network");
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

    private String getFormattedServerInfo() {
        return String.format("%s (%s)", NeatoWebConstants.getServerUrl(this), NeatoWebConstants.getServerName(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppUtils.logLibraryVersion();
        LogHelper.log(TAG, "Server information = " + getFormattedServerInfo());

        // Get XMPP started.
        mXMPPConnectionHelper = XMPPConnectionHelper.getInstance(this);
        String xmppDomain = NeatoWebConstants.getXmppServerDomain(this);
        mXMPPConnectionHelper.setServerInformation(xmppDomain, AppConstants.JABBER_SERVER_PORT,
                NeatoWebConstants.getXmppWebServer(this));
        mXMPPConnectionHelper.setXmppNotificationListener(mXmppNotificationListener, mHandler);

        if (NetworkConnectionUtils.hasNetworkConnection(getApplicationContext())) {
            Runnable task = new Runnable() {

                @Override
                public void run() {
                    loginToXmppServer();
                }
            };
            TaskUtils.scheduleTask(task, 0);
        }
        registerReceiver(mWifiStateChange, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        registerReceiver(mWifiStateChange, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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
        } catch (Exception e) {
            LogHelper.log(TAG, "Exception in unregisterReceiver", e);
        }
        super.onDestroy();
    }

    private Object mXmppConnectionLock = new Object();

    private void loginToXmppServer() {
        try {
            synchronized (mXmppConnectionLock) {
                if (UserHelper.isUserLoggedIn(getApplicationContext())) {
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
        } catch (XMPPException e) {
            LogHelper.log(TAG, "Exception in connecting to XMPP server", e);
        }
    }

    private void loginToXmppServerIfNotConnected() {
        try {
            synchronized (mXmppConnectionLock) {
                String userId = getUserChatId();
                String password = getUserChatPassword();
                if (!mXMPPConnectionHelper.isConnected()) {
                    LogHelper.log(TAG, "XMPP is not connected, trying now");
                    mXMPPConnectionHelper.login(userId, password);
                }
            }
        } catch (XMPPException e) {
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

    private void sendCommandUsingNewCommandStructure(String robotId, RobotRequests requests) {

        LogHelper.logD(TAG, "sendCommandUsingNewCommandStructure called");
        RobotCommandPacketHeader header = RobotCommandPacketHeader.getRobotCommandHeader(
                RobotCommandPacketConstants.COMMAND_PACKET_SIGNATURE,
                RobotCommandPacketConstants.COMMAND_PACKET_VERSION);
        if (isXmppConnectionExists(robotId)) {
            String chatId = XMPPUtils.getRobotChatId(NeatoSmartAppService.this, robotId);
            LogHelper.logD(TAG, "SendCommand Called using XMPP connection as transport. Request:-" + requests);
            requests.setDistributionMode(RobotPacketConstants.DISTRIBUTION_MODE_TYPE_XMPP);
            RobotCommandPacket robotCommandPacket = RobotCommandPacket.createRobotCommandPacket(header, requests);
            mXMPPConnectionHelper.sendRobotCommand(chatId, robotCommandPacket);
        } else {
            LogHelper.logD(TAG, "Xmpp connection does not exist: Request = " + requests);
        }
    }

    private boolean isPeerConnectionExists(String robotId) {
        return ((mRobotPeerConnection != null) && mRobotPeerConnection.isPeerRobotConnected(robotId));
    }

    private boolean isXmppConnectionExists(String robotId) {
        return ((mXMPPConnectionHelper != null) && mXMPPConnectionHelper.isConnected());
    }

    private XMPPNotificationListener mXmppNotificationListener = new XMPPNotificationListener() {

        @Override
        public void onDataReceived(String from, RobotCommandPacket packet) {
            LogHelper.log(TAG, "XMPP onDataReceived. New Packet Data = " + packet);
            // If data changed command recevied, retrieve the changed data from
            // server.
            if (packet.isRequest()) {
                RequestPacket request = packet.getRobotCommands().getCommand(0);
                if (isDataChangedCommand(request)) {
                    processDataChangedRequest(from, request);
                    return;
                }
            }
        }
    };

    private RobotPeerDataListener mRobotPeerDataListener = new RobotPeerDataListener() {

        public void onConnect(String robotId) {
            if (mResultReceiver != null) {
                Bundle bundle = new Bundle();
                bundle.putString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID, robotId);
                mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_CONNECTED, bundle);
            }
        }

        public void onDisconnect(String robotId) {
            if (mResultReceiver != null) {
                Bundle bundle = new Bundle();
                bundle.putString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID, robotId);
                mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_DISCONNECTED, bundle);
            }
        }

        @Override
        public void errorInConnecting(String robotId) {
            if (mResultReceiver != null) {
                Bundle bundle = new Bundle();
                bundle.putString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID, robotId);
                mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_CONNECTION_ERROR, bundle);
            }
        }

        @Override
        public void onDataReceived(String robotId, RobotCommandPacket robotPacket) {
            LogHelper.log(TAG, "onDataReceived. Packet = " + robotPacket);

            if (mResultReceiver == null) {
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID, robotId);
            bundle.putBoolean(NeatoRobotResultReceiverConstants.KEY_IS_REQUEST_PACKET, robotPacket.isRequest());

            if (robotPacket.isResponse()) {
                LogHelper.log(TAG, "Command Response from Remote Control = " + robotPacket);
                bundle.putParcelable(NeatoRobotResultReceiverConstants.KEY_REMOTE_RESPONSE_PACKET,
                        robotPacket.getCommandResponse());
            } else {
                LogHelper.log(TAG, "Command Requests from Remote Control = " + robotPacket);
                bundle.putParcelable(NeatoRobotResultReceiverConstants.KEY_REMOTE_REQUEST_PACKET,
                        robotPacket.getRobotCommands());
            }
            mResultReceiver.send(NeatoSmartAppsEventConstants.ROBOT_PACKET_RECEIVED_ON_PEER_CONNETION, bundle);
        }
    };

}
