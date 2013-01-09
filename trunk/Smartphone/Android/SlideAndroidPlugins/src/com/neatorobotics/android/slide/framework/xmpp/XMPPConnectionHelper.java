package com.neatorobotics.android.slide.framework.xmpp;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.w3c.dom.Node;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.CommandFactory;
import com.neatorobotics.android.slide.framework.robot.commands.CommandPacketValidator;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandsGroup;
import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;



public class XMPPConnectionHelper {

	private static final String TAG = XMPPConnectionHelper.class.getSimpleName();
	public static final String STRING_ENCODING = "UTF-8";
	@SuppressWarnings("unused")
	private Context mContext;
	private Connection mConnection;
	private ConnectionConfiguration mConnectionConfig;
	private String serverIpAddress;
	private String webServiceName;
	private int serverPort;
	private static final String JABBER_PACKET_PROPERTY_KEY = AppConstants.JABBER_PACKET_PROPERTY_KEY;
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


	private XMPPConnectionHelper(Context context)
	{
		mContext = context.getApplicationContext();
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

	public void setServerInformation(String ipAddress, int port, String webServiceName)
	{
		serverIpAddress = ipAddress;
		serverPort = port;
		this.webServiceName = webServiceName;

	}

	public void setXmppNotificationListener(XMPPNotificationListener listener, Handler handler)
	{
		mListener = listener;
		mHandler = handler;
	}

	public void connect() throws XMPPException
	{
		LogHelper.log(TAG, "connect called");
		synchronized (mConnectionObjectLock) {
			Connection connection = getConnection();
			connection.connect();
		}
	}

	public void connectAsync()
	{
		Runnable task = new Runnable() {

			@Override
			public void run() {
				try {
					connect();
					notifyConnectionSuccessful();
				} 
				catch (XMPPException e) {
					LogHelper.log(TAG, "Exception in connect", e);
					notifyConnectionFailed(e);
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
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
				connection.login(userId, password);
				startReceivingPackets();
				LogHelper.log(TAG, "XMPP Login Successful");
			} else {
				LogHelper.log(TAG, "UserId or Password is empty. Could not login.");
			}
		}
	}

	private boolean isValidUserIdAndPassword(String userId, String password)
	{
		return (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(password));
	}

	public void loginAsync(final String userId, final String password) {

		Runnable task = new Runnable() {

			@Override
			public void run() {
				synchronized (mConnectionObjectLock) {
					try {
						login(userId, password);
						notifyLoginSuccessful();
					} catch (XMPPException e) {
						LogHelper.log(TAG, "Exception in loginAsync: ",e);
						notifyLoginFailed(e);
					} 
				}

			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	private void startReceivingPackets()
	{
		PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
		mConnection.addPacketListener(mPacketListener, filter);
	}

	public void logout()
	{
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
		if(mConnectionConfig == null) {
			mConnectionConfig = new ConnectionConfiguration(serverIpAddress, serverPort, webServiceName);
		}
		return mConnectionConfig;
	}


	private Connection getConnection()
	{
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

	public void sendRobotCommand(String to, RobotPacket robotPacket) {

		LogHelper.logD(TAG, "sendRobotCommand called");
		LogHelper.logD(TAG, "sending to = " + to);
		byte[] packet = getRobotPacket(robotPacket);
		String packetXml = getRobotPacketString(robotPacket);
		sendRobotPacketAsync(to, packetXml, packet);
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


	private  byte[] getRobotPacket(RobotPacket robotPacket)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		Node header = CommandPacketValidator.getHeaderXml();
		Node command = robotPacket.robotCommandToXmlNode();
		byte[] packet = CommandFactory.getPacketData(header, command);
		try {
			dos.write(packet);
		}
		catch (Exception e) {
			LogHelper.log(TAG, "Exception in getBytes", e);
			return null;
		}
		return bos.toByteArray();

	}

	private String getRobotPacketString(RobotPacket robotPacket)
	{		
		Node header = CommandPacketValidator.getHeaderXml();
		Node command = robotPacket.robotCommandToXmlNode();
		String packet = CommandFactory.getPacketXml(header, command);

		return packet;

	}

	//TODO: later the byte[] parameter will be removed. This is temporary.
	private  void sendRobotPacket(String to, String packetXml, byte[] packet)
	{
		if (packet == null) {
			LogHelper.log(TAG, "Packet is null");
			return;
		}
		Connection connection = getConnection();
		Message message = new Message();
		message.setType(Message.Type.chat);
		message.setProperty(JABBER_PACKET_PROPERTY_KEY, packet);
		message.setBody(packetXml);
		message.setTo(to);
		connection.sendPacket(message);
		LogHelper.logD(TAG, "Command is sent to :" + to);
	}

	//TODO: later the byte[] parameter will be removed. This is temporary.
	private  void sendRobotPacketAsync(final String to, final String packetData, final byte[] packet)
	{
		Runnable task = new Runnable() {
			public void run() {
				sendRobotPacket(to, packetData, packet);
			}
		};
		Thread t = new Thread(task);
		t.start();
	}

	private void processMessageFromRobot(Message message) {
		String packetXml = message.getBody();
		RobotCommandsGroup robotCommandsGroup = null;
		byte[] packetData = null;
		String messageSender = message.getFrom();
		LogHelper.log(TAG, "Message recevied from: "+ messageSender);
		//TODO: Remove the check for command in property. This is just temporary. Later commands will be sent 
		// in body itself.
		if (packetXml == null) {
			packetData = (byte[]) message.getProperty(JABBER_PACKET_PROPERTY_KEY);
			if (packetData == null) {
				LogHelper.logD(TAG, "Empty jabber message");
				return;
			}
			LogHelper.logD(TAG, "jabber message size: "+packetData.length);
		} else {
			LogHelper.log(TAG, "Incoming message: " + packetXml);
			try {
				packetData = packetXml.getBytes(STRING_ENCODING);
			} catch (UnsupportedEncodingException e) {
				LogHelper.log(TAG, "Exception in processMessageFromRobot", e);
				return;
			}
		}
		boolean isPacketValid = CommandPacketValidator.validateHeaderAndSignature(packetData);
		if (isPacketValid) {
			LogHelper.log(TAG, "Header Version and Signature match");
			robotCommandsGroup = CommandFactory.createCommandGroup(packetData);
		} 
		else {
			LogHelper.log(TAG, "Header Version or Signature mis-match");
		}
		if (robotCommandsGroup == null) {
			LogHelper.log(TAG, "robotCommandsGroup is null");
			return;
		}
		notifyPacketReceived(messageSender, robotCommandsGroup);
	}

	private void notifyPacketReceived(final String from, final RobotCommandsGroup robotCommandGroup)
	{
		for (int i=0; i<robotCommandGroup.size(); i++) {
			final RobotPacket robotPacket = robotCommandGroup.getRobotPacket(i);
			if (mListener == null) {
				LogHelper.logD(TAG, "XMPPDataPacketListener is null");
				return;
			}

			if (mHandler != null) {
				mHandler.post(new Runnable() {
					public void run() {
						mListener.onDataReceived(from, robotPacket);
					}
				});
			}
			else {
				mListener.onDataReceived(from, robotPacket);
			}
		}
	}



	private void notifyConnectionSuccessful()
	{
		if (mHandler != null) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mListener.onConnectSucceeded();
				}
			});
		}
	}

	private void notifyConnectionFailed(XMPPException e)
	{
		if (mHandler != null) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mListener.onConnectFailed();
				}
			});
		}
	}

	private void notifyLoginSuccessful()
	{
		if (mHandler != null) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mListener.onLoginSucceeded();
				}
			});
		}
	}

	private void notifyLoginFailed(XMPPException e)
	{
		if (mHandler != null) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mListener.onLoginFailed();
				}
			});
		}
	}

}
