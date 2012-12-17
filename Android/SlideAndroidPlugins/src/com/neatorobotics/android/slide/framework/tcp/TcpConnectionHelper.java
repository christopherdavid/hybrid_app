package com.neatorobotics.android.slide.framework.tcp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import android.content.Context;
import android.os.Handler;
import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.CommandFactory;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandsGroup;
import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;
import com.neatorobotics.android.slide.framework.transport.Transport;
import com.neatorobotics.android.slide.framework.transport.TransportFactory;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.xml.NetworkXmlHelper;

public class TcpConnectionHelper {

	private static final String TAG = TcpConnectionHelper.class.getSimpleName();
	private Context mContext;
	private Handler mHandler;
	private TcpRobotConnectHelper mTcpRobotConnectHelper;
	private static final int TCP_ROBOT_SERVER_PORT = AppConstants.TCP_ROBOT_SERVER_SOCKET_PORT;
	private TcpDataPacketListener mTcpDataPacketListener;
	private Transport mTransport;
	private static final int PACKET_READ_CHUNK_SIZE = (4 * 1024);

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
			if (mTransport != null) {
				InputStream transportInputStream = mTransport.getInputStream();
				if (transportInputStream != null) {
					ConnectThread readRobotMessages = new ConnectThread(transportInputStream, mTransport);
					Thread t = new Thread(readRobotMessages);
					t.start();
					//TODO : Need to send the jabber details in a different way later.
					sendRobotJabberDetails();
				} else {
					LogHelper.log(TAG, "Could not get input stream for the socket. Try again.");
				}
			} else {
				LogHelper.log(TAG, "Could not connect to peer. Try again.");
				mTcpDataPacketListener.errorInConnecting();
			}
		}

		public void sendRobotJabberDetails() {
			LogHelper.log(TAG, "Send Jabber details to Robot");
//			RobotPacket jabberDetailsPacket = new RobotPacket(RobotCommandPacketConstants.COMMAND_ROBOT_JABBER_DETAILS);
			/*NetworkPacketBundle jabberDetailsBundle = jabberDetailsPacket.getBundle();*/
	//		RobotPacketBundle jabberDetailsBundle = jabberDetailsPacket.getBundle();
	//		jabberDetailsBundle.putString(RobotCommandPacketConstants.KEY_ROBOT_JABBER_ID, XMPPUtils.getRobotJabberId(mContext));
	//		jabberDetailsBundle.putString(RobotCommandPacketConstants.KEY_ROBOT_JABBER_PWD, XMPPUtils.getRobotJabberPwd(mContext));
	//		sendRobotCommand(jabberDetailsPacket, mTransport);
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
					RobotCommandsGroup robotCommandsGroup = null;
					try {
						if (mTransport != null && mTransport.isConnected()) {
							robotCommandsGroup = readPacket(din);
						} else {
							LogHelper.log(TAG, "Peer is not connected");
							break;
						}
					} catch (EOFException e) {
						LogHelper.log(TAG, "Exception in ConnectedThread" ,e);
						break;
					} catch (IOException e) {
						LogHelper.log(TAG, "Exception in ConnectedThread" ,e);
						break;
					}
					if (robotCommandsGroup != null) {	
						//TODO : Get all commands. Right now only processing one.
						RobotPacket robotPacket = robotCommandsGroup.getRobotPacket(0);
						notifyPacketReceived(robotPacket);
					} else {
						LogHelper.log(TAG, "Null packet received");
					}
				}
			}
			finally {
				LogHelper.logD(TAG, "Connected Thread end");
				mTransport = null;
				notifyRobotDisconnected();
			}
		}
	}

	private RobotCommandsGroup readPacket(DataInputStream din) throws EOFException, IOException 
	{
		RobotCommandsGroup robotCommandGroup = null;
		int length = din.readInt();
		LogHelper.log(TAG, "length = " + length);

		byte [] data = new byte[length];

		readByteArrayHelper(din, data, length);

		robotCommandGroup = CommandFactory.createCommandGroup(data);
		//LogHelper.log(TAG, "Robot Packet = " + robotPacket);
		//TODO : Process all robot packets.

		return robotCommandGroup;
	}
	private void readByteArrayHelper(DataInputStream din, byte [] byData, int length) throws IOException
	{
		int chunkLength = (length > PACKET_READ_CHUNK_SIZE)? PACKET_READ_CHUNK_SIZE:length;
		byte [] buffer = new byte[chunkLength];
		int offSet = 0;
		while (length > 0) {
			int dataReadSize = (length > PACKET_READ_CHUNK_SIZE)? PACKET_READ_CHUNK_SIZE:length;
			int dataRead = din.read(buffer, 0, dataReadSize);
			length -= dataRead;
			System.arraycopy(buffer, 0, byData, offSet, dataRead);
			offSet += dataRead;
			if (length > 0) {
				TaskUtils.sleep(100);
			}
		}
	}

	private void notifyPacketReceived(final RobotPacket robotPacket)
	{
		if (mTcpDataPacketListener == null) {
			return;
		}

		if (mHandler != null) {
			mHandler.post(new Runnable() {

				public void run() {
					mTcpDataPacketListener.onDataReceived(robotPacket);
				}
			});
		}
		else {
			mTcpDataPacketListener.onDataReceived(robotPacket);
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
			NeatoPrefs.setPeerConnectionStatus(mContext, false);
			if (mTransport != null) {
				mTransport.close();
				mTransport = null;
			}
		} 
	}

	//TODO: Find if the robot is connected. Map the serial id with the existing connections.
	public boolean isConnected(String robotId) {
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
		byte[] packet = NetworkXmlHelper.commandToXml(robotPacket);
		try {
			dos.writeInt(packet.length);
			dos.write(packet);
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
