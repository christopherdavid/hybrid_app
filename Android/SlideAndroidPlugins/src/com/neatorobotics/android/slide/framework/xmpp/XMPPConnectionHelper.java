package com.neatorobotics.android.slide.framework.xmpp;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import android.content.Context;
// import android.os.Handler;
import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;
import com.neatorobotics.android.slide.framework.xml.NetworkXmlHelper;


public class XMPPConnectionHelper {

	private static final String TAG = XMPPConnectionHelper.class.getSimpleName();
//	private Handler mHandler;
	private Context mContext;
	private Connection mConnection;
	private ConnectionConfiguration mConnectionConfig;
	private static final String JABBER_SERVER_IP_ADDRESS = AppConstants.JABBER_SERVER_IP_ADDRESS;
	private static final String JABBER_WEB_SERVICE = AppConstants.JABBER_WEB_SERVICE;
	private static final int JABBER_SERVER_PORT = AppConstants.JABBER_SERVER_PORT;
	private JabberLoginAndConnectHelper mJabberLoginAndConnectHelper;
	private static final String JABBER_PACKET_PROPERTY_KEY = AppConstants.JABBER_PACKET_PROPERTY_KEY;

	private boolean mLoginInProgress = false;
	private Object mLoginLock = new Object();
	private static XMPPConnectionHelper sXMPPConnectionHelper;
	private static Object mObjectCreateLock = new Object();
	private Object mConnectionObjectLock = new Object();
	

	private XMPPConnectionHelper(Context context)
	{
		mContext = context.getApplicationContext();
		mJabberLoginAndConnectHelper = new JabberLoginAndConnectHelper();
	}
	
	public static XMPPConnectionHelper getInstance(Context context)
	{
		synchronized (mObjectCreateLock) {
			if (sXMPPConnectionHelper == null) {
				sXMPPConnectionHelper = new XMPPConnectionHelper(context);
			}
		}
		return sXMPPConnectionHelper;
	}
	

	//TODO : DO we need to have Jabber in different thread? Its a push mechanism. Need to analyse.
	private class JabberLoginAndConnectHelper {

		public void formJabberConnectionAndLogin() {
			LogHelper.logD(TAG, "formJabberConnectionAndLogin");
			ConnectAndLoginThread connectJabberServerThread = new ConnectAndLoginThread();
			Thread t = new Thread(connectJabberServerThread);
			t.start();
		}
	}

		private class ConnectAndLoginThread implements Runnable {

		public ConnectAndLoginThread() {
		}

		public void run() {
			synchronized (mLoginLock) {
				if (mLoginInProgress) {
					return;
				}
				mLoginInProgress = true;
			}
			try {
				boolean connectSuccess = jabberConnect();
				if (connectSuccess) {
					String user_id = getJabberUsername();
					String user_pwd = getJabberUserPassword();
					login(user_id, user_pwd);
				} 
				else {
					LogHelper.log(TAG, "Jabber not connected. Could not login.");
				}
			}
			finally {
					synchronized (mLoginLock) {
					mLoginInProgress = false;
				}
			}
		};
	}

	// To be called in a thread.
	private boolean jabberConnect() {
		Connection connection = null;
		
		synchronized (mConnectionObjectLock) {
			connection = getConnection();
		}
		// TODO: If connection already exists, then should we close the existing connection
		// and create a new connection
		if ((connection == null) || !connection.isConnected()) {
			LogHelper.logD(TAG, "jabberConnect called");
			ConnectionConfiguration connectionConfig = getConnectionConfig();
			connection = new XMPPConnection(connectionConfig);
			try {
				connection.connect();
				LogHelper.log(TAG, "Connected to the Jabber Server.");
				setConnection(connection);
				return true;
			} 
			catch (XMPPException e) {
				LogHelper.logD(TAG, "Exception in Jabber Connect", e);
				return false;
			}
		}
		//Already connected.
		LogHelper.log(TAG, "Already connected to jabber server");
		return true;
	}

	private ConnectionConfiguration getConnectionConfig() {
		if(mConnectionConfig == null) {
			mConnectionConfig = new ConnectionConfiguration(JABBER_SERVER_IP_ADDRESS, JABBER_SERVER_PORT, JABBER_WEB_SERVICE);
		}
		return mConnectionConfig;
	}

	//To be called in a thread. Needs to be called after jabber server is connected.
	private boolean login(String userId, String userPassword) {
		
		if (userId == null || userPassword == null) {
			LogHelper.log(TAG, "Jabber username or password empty");
			return false;
		}
		synchronized (mConnectionObjectLock) {
			try {
				Connection connection = getConnection();
				if (connection != null && connection.isConnected()) {
					LogHelper.logD(TAG, "Login attempt- User id - " + userId + " user_pwd - " + userPassword);
					connection.login(userId, userPassword);
					LogHelper.log(TAG, "Jabber Login Successful");
					return true;
				}
				else {
					LogHelper.log(TAG, "Could not Login in Jabber server as Jabber server is not connected");
					return false;
				}
			} catch (XMPPException e) {
				LogHelper.log(TAG, "Exception in JabberUserLogin: ",e);
				return false;
			} 
		}
	}
	
	private Connection getConnection()
	{
		synchronized (mConnectionObjectLock) {
			return mConnection;
		}
	}
	
	private void setConnection(Connection connection)
	{
		synchronized (mConnectionObjectLock) {
			mConnection = connection;
		}
	}

	public void login() {

		if(!isConnected()) {
			mJabberLoginAndConnectHelper.formJabberConnectionAndLogin();
		}
	}

	public void disconnectXmppConnection() {
		synchronized (mConnectionObjectLock) {
			if(isConnected()) {
				Connection connection = getConnection();
				LogHelper.logD(TAG, "Jabber is connected. Breaking connection");
				if (connection != null) {
					connection.disconnect();
				}
			}
			setConnection(null);
		}
	}

	private String getJabberUsername() {
		String jabberUserId = NeatoPrefs.getJabberId(mContext);
		jabberUserId = XMPPUtils.removeJabberDomain(jabberUserId);
		return jabberUserId;
	}
	private String getJabberUserPassword() {
		String jabberUserPwd = NeatoPrefs.getJabberPwd(mContext);
		return jabberUserPwd;
	}

	private  byte[] getRobotPacket(RobotPacket robotPacket)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		byte[] packet = NetworkXmlHelper.commandToXml(robotPacket);
		try {
		//	dos.writeInt(packet.length);
			dos.write(packet);
		}
		catch (Exception e) {
			LogHelper.log(TAG, "Exception in getBytes", e);
			return null;
		}
		return bos.toByteArray();
	}

	public void sendRobotCommand(String peerJabberId, RobotPacket robotPacket) {

		byte[] packet = getRobotPacket(robotPacket);
		sendRobotPacketAsync(peerJabberId, packet);
	}

	private  void sendRobotPacket(String to, byte[] packet)
	{
		if (packet == null) {
			LogHelper.log(TAG, "Packet is null");
			return;
		}
		Connection connection = getConnection();
		Message message = new Message();
		message.setType(Message.Type.chat);
		message.setProperty(JABBER_PACKET_PROPERTY_KEY, packet);
		message.setTo(to);
		connection.sendPacket(message);
		LogHelper.logD(TAG, "Command is sent to :" + to);
	}

	private  void sendRobotPacketAsync(final String to, final byte[] packet)
	{
		Runnable task = new Runnable() {
			public void run() {
				sendRobotPacket(to, packet);
			}
		};
		Thread t = new Thread(task);
		t.start();
	}

	public boolean isConnected() {
		synchronized (mConnectionObjectLock) {
			Connection connection = getConnection();
			if (connection == null) {
				return false;
			}
			return (connection.isConnected() && connection.isAuthenticated());
		}
	}

}
