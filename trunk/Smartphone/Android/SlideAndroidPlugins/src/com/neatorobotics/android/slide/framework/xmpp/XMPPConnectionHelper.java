package com.neatorobotics.android.slide.framework.xmpp;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.content.Context;
import android.os.Handler;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandHeader;
import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;


public class XMPPConnectionHelper {

	private static final String TAG = XMPPConnectionHelper.class.getSimpleName();
	private Handler mHandler;
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

	
	// TODO: Right now hard-coded. later to be set when retrieved from server using set apis declared.



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
	
	// Unlike TCP IP where multiple transports are needed, Connection object is only one for all communication in XMPP. 
	public Connection getConnection() {
		return mConnection;
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
			// TODO : Don't connect if already connected. Need to see if this can anytime happen
			if (mConnection == null || !mConnection.isConnected()) {
				LogHelper.logD(TAG, "formJabberConnection");
				if(mConnectionConfig == null) {
					mConnectionConfig = new ConnectionConfiguration(JABBER_SERVER_IP_ADDRESS, JABBER_SERVER_PORT, JABBER_WEB_SERVICE);
				}
				mConnection = new XMPPConnection(mConnectionConfig);
				try {
					mConnection.connect();
					LogHelper.log(TAG, "Connected to the Jabber Server.");

				} catch (XMPPException e) {
					LogHelper.logD(TAG, "Exception in Jabber Connect", e);
				}
			}
			String user_id = getJabberUsername();
			String user_pwd = getJabberUserPassword();
			LogHelper.logD(TAG, "Login attempt- User id - " + user_id + " user_pwd - " + user_pwd);
			try {
				mConnection.login(user_id, user_pwd);
				LogHelper.log(TAG, "Jabber Login Successful");

			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				LogHelper.log(TAG, "Exception in JabberUserLogin: ",e);
			}
			
			finally {
				synchronized (mLoginLock) {
					mLoginInProgress = false;
				}
			}
		}
	}


	public void JabberUserLogin() {

		if(!isJabberConnected()) {
			mJabberLoginAndConnectHelper.formJabberConnectionAndLogin();
			//TODO: Need to put code for reconnecting after some fix interval of time if connection fails.
		}
	}
	
	public void disconnectXmppConnection() {
		
		if(isJabberConnected()) {
			LogHelper.logD(TAG, "Jabber is connected. Breaking connection");
			mConnection.disconnect();		
		}
		mConnection = null;
	}

	public String getJabberUsername() {
		String jabberUserId = NeatoPrefs.getJabberId(mContext);
		return jabberUserId;
	}
	public String getJabberUserPassword() {
		String jabberUserPwd = NeatoPrefs.getJabberPwd(mContext);
		return jabberUserPwd;
	}

	private  byte[] getRobotPacket(RobotPacket robotPacket)
	{

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.write(RobotCommandHeader.getHeader());
			byte [] discoveryPacket = robotPacket.getBytes();
			dos.writeInt(discoveryPacket.length);
			dos.write(discoveryPacket);
		}
		catch (Exception e) {
			LogHelper.log(TAG, "Exception in getBytes", e);
			return null;
		}
		return bos.toByteArray();
	}



	public void sendRobotCommand(RobotPacket robotPacket, String peerJabberId) {

		byte[] packet = getRobotPacket(robotPacket);
		sendRobotPacketAsync(packet, peerJabberId);
	}
	
	private  void sendRobotPacket(byte[] packet , String peerJabberId)
	{
		if (packet == null) {
			LogHelper.log(TAG, "Packet is null");
			return;
		}
		Connection connection = getConnection();
		Message message = new Message();
		message.setType(Message.Type.chat);
		message.setProperty(JABBER_PACKET_PROPERTY_KEY, packet);
		message.setTo(peerJabberId);
		connection.sendPacket(message);
		LogHelper.logD(TAG, "Command is sent to :" + peerJabberId);
	}
	
	private  void sendRobotPacketAsync(final byte[] packet, final String peerJabberId)
	{
		Runnable task = new Runnable() {
			public void run() {
				sendRobotPacket(packet, peerJabberId);
			}
		};
		Thread t = new Thread(task);
		t.start();
	}

	public boolean isJabberConnected() {
		if (mConnection == null) {
			return false;
		}
		return (mConnection.isConnected() && mConnection.isAuthenticated());
	}



}
