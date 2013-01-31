package com.neatorobotics.android.slide.framework.tcp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;


import android.content.Context;
import android.os.Handler;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandBuilder;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandParser;
import com.neatorobotics.android.slide.framework.transport.Transport;
import com.neatorobotics.android.slide.framework.transport.TransportFactory;
import com.neatorobotics.android.slide.framework.udp.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.udp.RobotDiscoveryService;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;

public class RobotPeerConnection {
	private static final String TAG = RobotPeerConnection.class.getSimpleName();
	private Context mContext;
	private Handler mHandler;
	private static final int TCP_ROBOT_SERVER_PORT = AppConstants.TCP_ROBOT_SERVER_SOCKET_PORT2;
	private RobotPeerDataListener mRobotPeerDataListener;
	private static final int PACKET_READ_CHUNK_SIZE = (4 * 1024);
	private RobotConnectionInfo mRobotConnectionInfo;
	private Object mRobotConnectionInfoLock = new Object();
	
	public RobotPeerConnection(Context context)
	{
		mContext = context.getApplicationContext();
	}
	public void setHandler(Handler handler)
	{
		mHandler = handler;
	}
	public void setPeerDataListener(RobotPeerDataListener robotPeerDataListener)
	{
		mRobotPeerDataListener = robotPeerDataListener;
	}

	private void notifyRobotConnected(final String robotId)
	{
		NeatoPrefs.setPeerConnectionStatus(mContext, true);
		if (mRobotPeerDataListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {
					public void run() {
						mRobotPeerDataListener.onConnect(robotId);
					}
				});
			}
			else {
				mRobotPeerDataListener.onConnect(robotId);
			}
		}
	}

	private void notifyRobotDisconnected(final String robotId)
	{
		NeatoPrefs.setPeerConnectionStatus(mContext, false);
		if (mRobotPeerDataListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {
					public void run() {
						mRobotPeerDataListener.onDisconnect(robotId);
					}
				});
			}
			else {
				mRobotPeerDataListener.onDisconnect(robotId);
			}
		}
	}

	private void notifyPacketReceived(final String robotId, final RobotCommandPacket robotCommandPacket)
	{
		if (mRobotPeerDataListener == null) {
			return;
		}

		if (mHandler != null) {
			mHandler.post(new Runnable() {
				public void run() {
					mRobotPeerDataListener.onDataReceived(robotId, robotCommandPacket);
				}
			});
		}
		else {
			mRobotPeerDataListener.onDataReceived(robotId, robotCommandPacket);
		}

	}


	public void connectToRobot(final String robotId) {

		LogHelper.log(TAG, "connectToRobot called");
		final int peerPort = TCP_ROBOT_SERVER_PORT;
		Runnable task = new Runnable() {
			public void run() {
				String userId = AppUtils.getLoggedInUserId(mContext);
				RobotDiscoveryService.startRobotsDiscovery(mContext, userId, robotId, new RobotDiscoveryListener() {

					private boolean receivedRobotIp = false;
					@Override
					public void onRobotDiscovered(RobotInfo robotInfo) {
						// We send the discovery request 3 times and we get the response 3 times. So
						// whenever we get the request we ignore subsequent request
						if (receivedRobotIp) {
							return;
						}
						LogHelper.logD(TAG, "Robot Discovered. Now will try to connect");
						if (robotInfo != null) {
							receivedRobotIp = true;
							//As we are supporting only one TCP connection as of now, break the exisiting conneciton if any
							closeExistingPeerConnection();
							connectToRobotInternal(robotId, robotInfo.getRobotIpAddress(), peerPort);
						}
					}

					@Override
					public void onDiscoveryStarted() {
					}

					@Override
					public void onDiscoveryEnd() {
					}
				});
			}
		};

		TaskUtils.scheduleTask(task, 0);
	}

	private void connectToRobotInternal(String robotId, String ipAddress, int port) {
		LogHelper.log(TAG, "connectToRobot internal called");
		InetAddress peerAddress = TcpUtils.getInetAddressFromIp(ipAddress);
		LogHelper.logD(TAG, "Robot TCP IP Address = " + peerAddress);
		Transport transport = TransportFactory.createTransport(peerAddress, port);
		if (transport != null) {
			LogHelper.logD(TAG, "transport = " + transport);
			RobotConnectionInfo robotConnectionInfo = createRobotConnectionInfo(robotId, ipAddress, transport);
			setConnectionRobotInfo(robotConnectionInfo);
			startReadDataThreadForRobot(robotConnectionInfo);
		} else {
			LogHelper.log(TAG, "Could not connect to peer. Try again.");
			if (mRobotPeerDataListener != null) {
				mRobotPeerDataListener.errorInConnecting(robotId);
			}
		}
	}

	private void startReadDataThreadForRobot(RobotConnectionInfo robotConnectionInfo) {
		if ((robotConnectionInfo != null) && (robotConnectionInfo.getTransport() != null)) {
			InputStream transportInputStream = robotConnectionInfo.getTransport().getInputStream();
			if (transportInputStream != null) {
				ConnectThread readRobotMessages = new ConnectThread(robotConnectionInfo);
				Thread t = new Thread(readRobotMessages);
				t.start();
			} else {
				LogHelper.log(TAG, "Could not get input stream for the socket. Try again.");
			}
		}
	}

	public void closeExistingPeerConnection() {
		if (getConnectedRobotInfo() != null) {
			String peerRobotId = getConnectedRobotInfo().getRobotId();
			LogHelper.log(TAG, "Closing peer connection of robot: "+peerRobotId);
			closePeerRobotConnectionInternal(peerRobotId);
		} else {
			LogHelper.log(TAG, "No robot peer connected");
		}
	}

	private class ConnectThread implements Runnable {

		private InputStream mIs;
		private RobotConnectionInfo connectionInfo;
		private Transport mTransport;
		private String mRobotId;
		//No need to send input stream. It can be very well retrieved from the transport. This is just to make sure input streaM
		// exists for ther transport before making an attempt to connect.
		public ConnectThread(RobotConnectionInfo robotConnectionInfo) {
			connectionInfo = robotConnectionInfo;
		}
		public void run() {
			try {
				mTransport = connectionInfo.getTransport();
				mIs = mTransport.getInputStream();
				mRobotId = connectionInfo.getRobotId();
				notifyRobotConnected(mRobotId);
				while(true) {
					DataInputStream din = new DataInputStream(mIs);
					RobotCommandPacket commandPacket = null;
					try {
						if (mTransport != null && mTransport.isConnected()) {
							int signature = readInt(din);
							LogHelper.log(TAG, "received signature:" + signature + " expected signature : " + AppConstants.APP_SIGNATURE);
							
							if (signature  != AppConstants.APP_SIGNATURE) {
								LogHelper.log(TAG, "****ERROR***** - Signature mismatch");
								mTransport.close();
								break;
							}
							
							int version = readInt(din);
							LogHelper.log(TAG, "received version:" + version + " expected version : " + AppConstants.COMMAND_PACKET_VERSION);
							
							if (version  != AppConstants.COMMAND_PACKET_VERSION) {
								LogHelper.log(TAG, "****ERROR***** - version mismatch");
								mTransport.close();
								break;
							}
							commandPacket = readPacket(din);
						}
						else {
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
					
					if (commandPacket != null) {	
						notifyPacketReceived(mRobotId, commandPacket);
					} 
					else {
						LogHelper.log(TAG, "Null packet received");
					}
				}
			}
			finally {
				LogHelper.logD(TAG, "Connected Thread end");
				notifyRobotDisconnected(mRobotId);
				deleteConnectionInfo(mRobotId);
			}
		}
	}
	
	
	public void closePeerRobotConnection(final String robotId)
	{
		Runnable task = new Runnable() {
			public void run() {
				closePeerRobotConnectionInternal(robotId);
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	private void closePeerRobotConnectionInternal(String robotId)
	{
		LogHelper.log(TAG, "closePeerConnection called");
		if(isPeerRobotConnected(robotId)) {	
			NeatoPrefs.setPeerConnectionStatus(mContext, false);
			RobotConnectionInfo robotConnection = getRobotConnectionInfo(robotId);
			Transport transport = robotConnection.getTransport();
			if (transport != null) {
				transport.close();
				transport = null;
			}
			deleteConnectionInfo(robotId);
		} 
	}

	public boolean isPeerRobotConnected(String robotId) {
		LogHelper.logD(TAG, "isPeerRobotConnected transport: " + robotId);
		RobotConnectionInfo robotConnectionInfo = getRobotConnectionInfo(robotId);
		if (robotConnectionInfo != null) {
			Transport transport = robotConnectionInfo.getTransport();
			if (transport == null) {
				LogHelper.log(TAG, "trasport for robotId is null");
				return false;
			}
			return transport.isConnected();
		}
		else {
			LogHelper.log(TAG, "RobotConnectionInfo for robotId does not exist");
			return false;
		}		
	}

	public  void sendRobotCommand(String robotId, RobotCommandPacket robotPacket) {
		byte[] packet = getRobotPacket(robotPacket);
		RobotConnectionInfo robotConnectionInfo = getRobotConnectionInfo(robotId);
		if (robotConnectionInfo != null) {
			Transport transport = robotConnectionInfo.getTransport();
			LogHelper.log(TAG, "Connection exist. Sending command.");
			sendRobotPacketAsync(transport, packet);
		} else {
			LogHelper.log(TAG, "Connection does not exist.");
		}
	}

	private  byte[] getRobotPacket(RobotCommandPacket robotPacket)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		RobotCommandBuilder builder = new RobotCommandBuilder();
		byte[] packet =  builder.convertRobotCommandsToBytes(robotPacket);
		int signature = robotPacket.getHeader().getSignature();
		int version = robotPacket.getHeader().getVersion();
		try {
			dos.writeInt(signature);
			dos.writeInt(version);
			dos.writeInt(packet.length);
			dos.write(packet);
		}
		catch (Exception e) {
			LogHelper.log(TAG, "Exception in getBytes", e);
			return null;
		}
		return bos.toByteArray();
	}

	private  void sendRobotPacketAsync(final Transport transport, final  byte[] packet )
	{
		if (transport == null) {
			LogHelper.log(TAG, "Transport is null. Cannot send packet");
			return;
		}
		if (packet == null) {
			LogHelper.log(TAG, "Packet is null");
			return;
		}

		Runnable task = new Runnable() {

			public void run() {
				try {
					transport.send(packet);
					LogHelper.log(TAG, "Packet is sent");
				}
				catch (IOException e) {
					LogHelper.log(TAG, "Exception in sendRobotPacket", e);
				}
			}
		};
		Thread t = new Thread(task);
		t.start();
	}

	private RobotCommandPacket readPacket(DataInputStream din) throws EOFException, IOException 
	{
		RobotCommandPacket commandPacket = null;

		int length = din.readInt();
		LogHelper.log(TAG, "length = " + length);
		byte [] commandData = new byte[length];
		readByteArrayHelper(din, commandData, length);
		logAsString(commandData);
		LogHelper.log(TAG, "Header Version and Signature match");
		RobotCommandParser commandParser = new RobotCommandParser();
		commandPacket = commandParser.convertBytesToRobotCommands(commandData);

		return commandPacket;
	}
	
	private int readInt(DataInputStream din) throws IOException
	{
		int data = din.readInt();
		return data;
	}

	private void logAsString(byte [] data)
	{
		String dataAsStr;
		try {
			dataAsStr = new String(data, "UTF-8");
			LogHelper.logD(TAG, "Message received");
			LogHelper.logD(TAG, "Message = " + dataAsStr);
		} catch (UnsupportedEncodingException e) {

		}

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
	
	private RobotConnectionInfo getConnectedRobotInfo() {
		synchronized (mRobotConnectionInfoLock) {
			LogHelper.log(TAG, "getConnectedRobotInfo called. mRobotConnectionInfo = " + mRobotConnectionInfo);
			return mRobotConnectionInfo;
		}

	}
	
	private void setConnectionRobotInfo(RobotConnectionInfo robotConnectionInfo)
	{
		LogHelper.log(TAG, "setConnectionRobotInfo called. robotConnectionInfo = " + robotConnectionInfo);
		synchronized (mRobotConnectionInfoLock) {
			mRobotConnectionInfo = robotConnectionInfo;
		}
	}

	private RobotConnectionInfo createRobotConnectionInfo(String robotId, String ipAddress, Transport transport) {
		RobotConnectionInfo robotConnectionInfo = new RobotConnectionInfo(robotId, ipAddress, transport);
		return robotConnectionInfo;
	}

	private RobotConnectionInfo getRobotConnectionInfo(String robotId) {
		LogHelper.log(TAG, "getRobotConnectionInfo called");
		LogHelper.log(TAG, "mRobotConnectionInfo = " + mRobotConnectionInfo);
		if ((mRobotConnectionInfo != null) && (mRobotConnectionInfo.getRobotId().equals(robotId))) {
			return mRobotConnectionInfo;
		} else {
			LogHelper.log(TAG, "RobotConnectionInfo does not exist for robotId:" + robotId);
			return null;
		}
	}

	private void deleteConnectionInfo(String robotId) {
		LogHelper.log(TAG, "Deleting Peer robot connection info");
		if (mRobotConnectionInfo != null) {
			mRobotConnectionInfo.setIpAddress(null);
			mRobotConnectionInfo.setRobotId(null);
			mRobotConnectionInfo.setTransport(null);
		}
		setConnectionRobotInfo(null);
	}

	private class RobotConnectionInfo {
		private String mIpAddress;
		private String mRobotId;
		private Transport mTransport;

		public RobotConnectionInfo(String robotId, String ipAddress, Transport transport) {
			mRobotId = robotId;
			mIpAddress = ipAddress;
			mTransport = transport;
		}

		public String getRobotId() {
			return mRobotId;
		}

		@SuppressWarnings("unused")
		public String getIpAddress() {
			return mIpAddress;
		}
		public Transport getTransport() {
			return mTransport;
		}

		public void setRobotId(String robotId) {
			mRobotId = robotId;
		}
		public void setIpAddress(String ipAddress) {
			mIpAddress = ipAddress;
		}
		public void setTransport(Transport transport) {
			mTransport = transport;
		}
	}
}
