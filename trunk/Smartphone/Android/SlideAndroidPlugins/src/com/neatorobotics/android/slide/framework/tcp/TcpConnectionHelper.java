package com.neatorobotics.android.slide.framework.tcp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import android.content.Context;
import android.os.Handler;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.NetworkPacketBundle;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.CommandFactory;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandHeader;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.RobotDiscoveryPacketHeader;
import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;
import com.neatorobotics.android.slide.framework.transport.Transport;
import com.neatorobotics.android.slide.framework.transport.TransportFactory;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.xmpp.XMPPUtils;

public class TcpConnectionHelper {

	private static final String TAG = TcpConnectionHelper.class.getSimpleName();
	private Context mContext;
	private Handler mHandler;
	private TcpRobotConnectHelper mTcpRobotConnectHelper;
	private static final int TCP_ROBOT_SERVER_PORT = AppConstants.TCP_ROBOT_SERVER_SOCKET_PORT;
	private TcpDataPacketListener mTcpDataPacketListener;
	private Transport mTransport;
	
	public TcpConnectionHelper(Context context)
	{
		mContext = context.getApplicationContext();
		mTcpRobotConnectHelper = new TcpRobotConnectHelper();
	}
	public void setHandler(Handler handler)
	{
		mHandler = handler;
	}
	public void setTcpConnectionListener(TcpDataPacketListener tcpDataPacketListener)
	{
		mTcpDataPacketListener = tcpDataPacketListener;
	}
	
	private void notifyRobotConnected()
	{
		NeatoPrefs.setPeerConnectionStatus(mContext, true);
		if (mTcpDataPacketListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {
					
					public void run() {
						mTcpDataPacketListener.onConnect();
					}
				});
			}
			else {
				mTcpDataPacketListener.onConnect();
			}
		}
	}
	
	private void notifyRobotDisconnected()
	{
		NeatoPrefs.setPeerConnectionStatus(mContext, false);
		if (mTcpDataPacketListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {
					
					public void run() {
						mTcpDataPacketListener.onDisconnect();
					}
				});
				
			}
			else {
				mTcpDataPacketListener.onDisconnect();
			}
		}
	}

	private class TcpRobotConnectHelper
	{
		// TODO: Need to figure out whether to pass IP Address or Robot Id. Right now implemented for IpAddress
		public void connectToRobot(String IPAddress) {

			LogHelper.log(TAG, "connectToRobot internal called");
			/*InetAddress peerAddress = TcpUtils.getRobotInetAddressFromRobotId(RobotId);*/
			InetAddress peerAddress = TcpUtils.getInetAddressFromIp(IPAddress);
			int peerPort = TCP_ROBOT_SERVER_PORT;
			LogHelper.logD(TAG, "Robot TCP IP Address = " + peerAddress);
			mTransport = TransportFactory.createTransport(peerAddress, peerPort);
			InputStream transportInputStream = mTransport.getInputStream();
			ConnectThread readRobotMessages = new ConnectThread(transportInputStream, mTransport);
			Thread t = new Thread(readRobotMessages);
			t.start();

			//TODO : Need to send the jabber details in a different way later.
			sendRobotJabberDetails();
		}

		public void sendRobotJabberDetails() {
			LogHelper.log(TAG, "Send Jabber details to Robot");
			RobotPacket jabberDetailsPacket = new RobotPacket(RobotCommandPacketConstants.COMMAND_ROBOT_JABBER_DETAILS);
			NetworkPacketBundle jabberDetailsBundle = jabberDetailsPacket.getBundle();
			jabberDetailsBundle.putString(RobotCommandPacketConstants.KEY_ROBOT_JABBER_ID, XMPPUtils.getRobotJabberId(mContext));
			jabberDetailsBundle.putString(RobotCommandPacketConstants.KEY_ROBOT_JABBER_PWD, XMPPUtils.getRobotJabberPwd(mContext));
			sendRobotCommand(jabberDetailsPacket, mTransport);
		}
	}

	private class ConnectThread implements Runnable {

		boolean running = true;
		private InputStream mIs;

		public ConnectThread(InputStream is , Transport transport) {
			mIs = is;
			mTransport = transport;

		}
		public void run() {

			try {
				notifyRobotConnected();
				while(running) {
					DataInputStream din = new DataInputStream(mIs);
					int msgLen = din.readInt();
					LogHelper.logD(TAG, "TCP Message Received with length: " + msgLen);
					byte[] msg = new byte[msgLen];
					din.readFully(msg);
					processPacketAndReply(din , mTransport);
				}
			}
			catch (IOException e) {
				LogHelper.log(TAG, "Error in ReadThread:  ", e);
				mTransport = null;
			}
			finally {
				notifyRobotDisconnected();
			}
		}
	}

	private void processPacketAndReply(DataInputStream din, Transport mTransport) {

		// first check the signature and the version
		int signature;
		try {
			signature = din.readInt();

			LogHelper.logD(TAG, "signature = " + Integer.toHexString(signature));		

			if (signature != AppConstants.APP_SIGNATURE) {
				LogHelper.log(TAG, "***SIGNATURE MISMATCH*****" );	
				LogHelper.logD(TAG, "expected signature = " + Integer.toHexString(AppConstants.APP_SIGNATURE));
				return;
			}
			int version = din.readInt();
			LogHelper.logD(TAG, "version = " + version);		
			if (version != mTransport.getVersion()) {
				LogHelper.logD(TAG, "***VERSION MISMATCH" );	
				LogHelper.logD(TAG, "expected version = " + Integer.toHexString(RobotDiscoveryPacketHeader.DISCOVERY_PACKET_HEADER_VERSION));
				return;
			}


			int length = din.readInt();
			LogHelper.logD(TAG, "length = " + length);	
			RobotPacket robotPacket = CommandFactory.createCommand(din);
			if (robotPacket == null) {
				LogHelper.log(TAG, "robotPacket is null");
				return;
			}
			// Processes the robot packet and sends reply if necessary
			// TODO : Handle command replies
		}
		catch (IOException e) {
			LogHelper.log(TAG, "Unable to process incoming TCP Packet.");
		}

	}

	public void connectToRobot(final String ipAddress) {
		Runnable task = new Runnable() {
			
			public void run() {
				connectToRobotInternal(ipAddress);
			}
		};
		
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void connectToRobotInternal(String ipAddress) {
		LogHelper.log(TAG, "connectToRobot called");
		if(!isConnected(ipAddress)) {
			mTcpRobotConnectHelper.connectToRobot(ipAddress);
		} 
		else {
			LogHelper.log(TAG, "Already connected to robot. IpAddress: " + ipAddress);
		}
	}
	
	public void closePeerConnection(final String ipAddress)
	{
		Runnable task = new Runnable() {
			
			public void run() {
				closePeerConnectionInternal(ipAddress);
			}
		};
		
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void closePeerConnectionInternal(String ipAddress)
	{
		LogHelper.log(TAG, "closePeerConnection called");
		if(isConnected(ipAddress)) {
			if (mTransport != null) {
				mTransport.close();
				mTransport = null;
			}
		} 
	}
	
	public boolean isConnected(String ipAddress) {
		LogHelper.log(TAG, "isConnected transport = " + mTransport);
		if (mTransport == null) {
			return false;
		}
		return mTransport.isConnected();
	}


	//TODO : Can these two functions be included in RobotCommandHelper?
	public  void sendRobotCommand(RobotPacket robotPacket, Transport transport) {
		byte[] packet = getRobotPacket(robotPacket);
		sendRobotPacketAsync(transport,packet);
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
	
	private  void sendRobotPacket(Transport transport,  byte[] Packet )
	{
		
		if (Packet == null) {
			LogHelper.log(TAG, "Packet is null");
			return;
		}
		
		try {
			transport.send(Packet);
		}
		catch (IOException e) {
			LogHelper.log(TAG, "Exception in sendRobotPacket", e);
		}
	}

	private  void sendRobotPacketAsync(final Transport transport, final  byte[] Packet )
	{

		Runnable task = new Runnable() {

			public void run() {
				sendRobotPacket( transport, Packet );
			}
		};
		Thread t = new Thread(task);
		t.start();
	}

	//TODO : Later to be used in a MAP 
	public Transport getTransport(String ipAddress) {
		return mTransport;
	}




}
