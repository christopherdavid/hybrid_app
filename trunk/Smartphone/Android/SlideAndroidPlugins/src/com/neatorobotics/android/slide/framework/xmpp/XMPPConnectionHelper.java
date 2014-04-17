package com.neatorobotics.android.slide.framework.xmpp;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandBuilder;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandParser;

public class XMPPConnectionHelper {

    private static final String TAG = XMPPConnectionHelper.class.getSimpleName();

    private static final int MAX_RETRY_CONNECT_COUNT = 3;

    private String serverIpAddress;
    private String webServiceName;
    private int serverPort;

    private Context mContext;
    private Connection mConnection;
    private ConnectionConfiguration mConnectionConfig;
    private static XMPPConnectionHelper sXMPPConnectionHelper;
    private static Object mObjectCreateLock = new Object();
    private Object mConnectionObjectLock = new Object();
    private Handler mHandler;
    private XMPPNotificationListener mListener;

    private PacketListener mPacketListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            Message message = (Message) packet;
            LogHelper.log(TAG, "XMPP message received from robot");
            processMessageFromRobot(message);
        }
    };

    private XMPPConnectionHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public static XMPPConnectionHelper getInstance(Context context) {
        synchronized (mObjectCreateLock) {
            if (sXMPPConnectionHelper == null) {
                sXMPPConnectionHelper = new XMPPConnectionHelper(context);
            }
        }
        return sXMPPConnectionHelper;
    }

    public void setServerInformation(String ipAddress, int port, String webServiceName) {
        serverIpAddress = ipAddress;
        serverPort = port;
        this.webServiceName = webServiceName;

    }

    public void setXmppNotificationListener(XMPPNotificationListener listener, Handler handler) {
        mListener = listener;
        mHandler = handler;
    }

    public void connect() throws XMPPException {
        LogHelper.log(TAG, "connect called");
        synchronized (mConnectionObjectLock) {
            Connection connection = getConnection();
            connection.connect();
            notifyConnectionSuccessful();
        }
    }

    public void login(String userId, String password) throws XMPPException {
        LogHelper.log(TAG, "login called");
        synchronized (mConnectionObjectLock) {
            Connection connection = getConnection();
            if (!connection.isConnected()) {
                throw new XMPPException("Not connected to XMPP server");
            }
            LogHelper.logD(TAG, "Login attempt- User id - " + userId);
            if (isValidUserIdAndPassword(userId, password)) {
                String resourceId = getResourceId();
                connection.login(userId, password, resourceId);
                notifyLoginSuccessful();
                startReceivingPackets();
                LogHelper.log(TAG, "XMPP Login Successful");
            } else {
                LogHelper.log(TAG, "UserId or Password is empty. Could not login.");
            }
        }
    }

    private boolean isValidUserIdAndPassword(String userId, String password) {
        return (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(password));
    }

    private void startReceivingPackets() {
        PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
        mConnection.addPacketListener(mPacketListener, filter);
    }

    private void logout() {
        LogHelper.logD(TAG, "logout called");
        synchronized (mConnectionObjectLock) {
            if (mConnection != null) {
                mConnection.removePacketListener(mPacketListener);
            }
        }
    }

    private ConnectionConfiguration getConnectionConfig() {
        LogHelper.logD(TAG, "Server IP address = " + serverIpAddress);
        LogHelper.logD(TAG, "Server port = " + serverPort);
        LogHelper.logD(TAG, "webServiceName = " + webServiceName);
        if (mConnectionConfig == null) {
            mConnectionConfig = new ConnectionConfiguration(serverIpAddress, serverPort, webServiceName);
        }
        return mConnectionConfig;
    }

    private Connection getConnection() {
        synchronized (mConnectionObjectLock) {
            if (mConnection == null) {
                ConnectionConfiguration connectionConfig = getConnectionConfig();
                mConnection = new XMPPConnection(connectionConfig);
            }
            return mConnection;
        }
    }

    public void close() {
        LogHelper.logD(TAG, "close called");
        synchronized (mConnectionObjectLock) {
            if (mConnection != null) {
                logout();
                mConnection.disconnect();
                mConnection = null;
            }
        }
    }

    public void sendRobotCommand(String peerJabberId, RobotCommandPacket robotPacket) {

        LogHelper.logD(TAG, "sendRobotCommand called");
        LogHelper.logD(TAG, "sending to = " + peerJabberId);

        RobotCommandBuilder builder = new RobotCommandBuilder();
        String robotPacketInXmlFormat = builder.convertRobotCommandsToString(robotPacket);
        LogHelper.logD(TAG, "robotPacketInXmlFormat = " + robotPacketInXmlFormat);
        sendRobotPacketAsync(peerJabberId, robotPacketInXmlFormat);
    }

    public boolean isConnected() {
        synchronized (mConnectionObjectLock) {
            Connection connection = getConnection();
            if (connection == null) {
                return false;
            }
            boolean isConnected = connection.isConnected();
            boolean isAuthenticated = connection.isAuthenticated();

            LogHelper.logD(TAG, "isConnected = " + isConnected + " isAuthenticated = " + isAuthenticated);
            return (isConnected && isAuthenticated);
        }
    }

    private void sendRobotPacket(String to, String packetXml) {
        Connection connection = getConnection();
        Message message = new Message();
        message.setType(Message.Type.chat);
        message.setBody(packetXml);
        message.setTo(to);
        connection.sendPacket(message);
        LogHelper.logD(TAG, "Command is sent to :" + to);
    }

    private void sendRobotPacketAsync(final String to, final String packetData) {
        Runnable task = new Runnable() {
            public void run() {
                boolean isConnected = tryXmppConnection();
                if (isConnected) {
                    sendRobotPacket(to, packetData);
                } else {
                    LogHelper.log(TAG, "Not connected over XMPP. Will not be sending packet");
                }
            }
        };
        Thread t = new Thread(task);
        t.start();
    }

    private boolean tryXmppConnection() {
        Connection connection = getConnection();
        boolean isConnected = connection.isConnected();
        boolean isAuthenticated = connection.isAuthenticated();
        if (isConnected && isAuthenticated) {
            // already connected return true from here
            return true;
        }
        for (int i = 0; i < MAX_RETRY_CONNECT_COUNT; i++) {
            try {
                close();
                connection = getConnection();
                isConnected = connection.isConnected();
                if (!isConnected) {
                    connect();
                }
                isAuthenticated = connection.isAuthenticated();
                if (!isAuthenticated) {
                    String userId = getUserChatId();
                    String password = getUserChatPassword();
                    if (isValidUserIdAndPassword(userId, password)) {
                        try {
                            String resourceId = getResourceId();
                            connection.login(userId, password, resourceId);
                        } catch (Exception e) {

                        }

                    }
                } else {
                    LogHelper.log(TAG, "XMPP login");
                    break;
                }
            } catch (XMPPException e) {

            }
        }
        return isConnected;
    }

    private String getResourceId() {
        return NeatoPrefs.getNeatoUserDeviceId(mContext);
    }

    private void processMessageFromRobot(Message message) {
        String packetXml = message.getBody();

        LogHelper.log(TAG, "Message body : " + packetXml);
        String messageSender = message.getFrom();
        LogHelper.log(TAG, "Message recevied from: " + messageSender);

        RobotCommandParser parser = new RobotCommandParser();
        RobotCommandPacket packet = parser.convertStringToRobotCommands(packetXml);
        if ((packet != null) && (packet.isValidPacket())) {
            LogHelper.log(TAG, "New packet structure and packet is valid ");
            notifyPacketReceived(messageSender, packet);
            return;
        } else {
            LogHelper.log(TAG, "Invalid packet srtucture received");
        }
    }

    private void notifyPacketReceived(final String from, final RobotCommandPacket packet) {
        if (mListener == null) {
            return;
        }

        if (mHandler != null) {
            mHandler.post(new Runnable() {

                public void run() {
                    mListener.onDataReceived(from, packet);
                }
            });
        } else {
            mListener.onDataReceived(from, packet);
        }
    }

    private void notifyConnectionSuccessful() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    mListener.onConnectSucceeded();
                }
            });
        }
    }

    private void notifyLoginSuccessful() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    mListener.onLoginSucceeded();
                }
            });
        }
    }

    private String getUserChatId() {
        String jabberUserId = UserHelper.getChatId(mContext);
        jabberUserId = XMPPUtils.removeJabberDomain(jabberUserId);
        return jabberUserId;
    }

    private String getUserChatPassword() {
        String jabberUserPwd = UserHelper.getChatPwd(mContext);
        return jabberUserPwd;
    }

}
