package com.neatorobotics.android.slide.framework.tcp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import org.w3c.dom.Node;
import android.content.Context;
import android.os.Handler;
import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.CommandFactory;
import com.neatorobotics.android.slide.framework.robot.commands.CommandPacketValidator;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandsGroup;
import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;
import com.neatorobotics.android.slide.framework.transport.Transport;
import com.neatorobotics.android.slide.framework.transport.TransportFactory;
import com.neatorobotics.android.slide.framework.udp.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.udp.RobotDiscoveryService;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;

public class TcpConnectionHelper {

	private static final String TAG = TcpConnectionHelper.class.getSimpleName();
	private Context mContext;
	private Handler mHandler;
	private TcpRobotConnectHelper mTcpRobotConnectHelper;       
	private static final int TCP_ROBOT_SERVER_PORT = AppConstants.TCP_ROBOT_SERVER_SOCKET_PORT;
	private TcpDataPacketListener mTcpDataPacketListener;
	private static final int PACKET_READ_CHUNK_SIZE = (4 * 1024);

	// Can be array if multiple connections. Right now only one connection is going to be supported.
	// If request for a new robot comes, we disconnect the current connection.
	private RobotConnectionInfo mRobotConnectionInfo;

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

	private void notifyRobotConnected(final String robotId)
	{
		NeatoPrefs.setPeerConnectionStatus(mContext, true);
		if (mTcpDataPacketListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {

					public void run() {
						mTcpDataPacketListener.onConnect(robotId);
					}
				});
			}
			else {
				mTcpDataPacketListener.onConnect(robotId);
			}
		}
	}

	private void notifyRobotDisconnected(final String robotId)
	{
		NeatoPrefs.setPeerConnectionStatus(mContext, false);
		if (mTcpDataPacketListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {

					public void run() {
						mTcpDataPacketListener.onDisconnect(robotId);
					}
				});

			}
			else {
				mTcpDataPacketListener.onDisconnect(robotId);
			}
		}
	}

	private class TcpRobotConnectHelper
	{
		public void connectToRobot(String robotId, String ipAddress) {
			LogHelper.log(TAG, "connectToRobot internal called");
			InetAddress peerAddress = TcpUtils.getInetAddressFromIp(ipAddress);
			int peerPort = TCP_ROBOT_SERVER_PORT;
			LogHelper.logD(TAG, "Robot TCP IP Address = " + peerAddress);

			if (getConnectedRobotInfo() != null) {
				closePeerConnectionInternal(getConnectedRobotInfo().getRobotId());
			}

			Transport transport = TransportFactory.createTransport(peerAddress, peerPort);

			if (transport != null) {
				RobotConnectionInfo robotConnectionInfo = createRobotConnectionInfo(robotId, ipAddress, transport);
				InputStream transportInputStream = transport.getInputStream();
				if (transportInputStream != null) {
					ConnectThread readRobotMessages = new ConnectThread(transportInputStream, robotConnectionInfo);
					Thread t = new Thread(readRobotMessages);
					t.start();
				} else {
					LogHelper.log(TAG, "Could not get input stream for the socket. Try again.");
				}
			} else {
				LogHelper.log(TAG, "Could not connect to peer. Try again.");
				mTcpDataPacketListener.errorInConnecting(robotId);
			}
		}		
	}

	private class ConnectThread implements Runnable {

		boolean running = true;
		private InputStream mIs;
		private RobotConnectionInfo connectionInfo;
		private Transport transport;
		private String robotId;
		//No need to send input stream. It can be very well retrieved from the transport. This is just to make sure input streaM
		// exists for ther transport before making an attempt to connect.
		public ConnectThread(InputStream is , RobotConnectionInfo robotConnectionInfo) {
			mIs = is;
			connectionInfo = robotConnectionInfo;
		}
		public void run() {
			transport = connectionInfo.getTransport();
			robotId = connectionInfo.getRobotId();
			try {
				notifyRobotConnected(robotId);
				while(running) {
					DataInputStream din = new DataInputStream(mIs);
					RobotCommandsGroup robotCommandsGroup = null;
					try {
						if (transport != null && transport.isConnected()) {
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
						notifyPacketReceived(robotId, robotCommandsGroup);
					} else {
						LogHelper.log(TAG, "Null packet received");
					}
				}
			}
			finally {
				LogHelper.logD(TAG, "Connected Thread end");
				notifyRobotDisconnected(robotId);
				deleteConnectionInfo(robotId);
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
		
		logAsString(data);
		
		boolean isValidCommandPacket = CommandPacketValidator.validateHeaderAndSignature(data);
		if (isValidCommandPacket) {
			LogHelper.log(TAG, "Header Version and Signature match");
			robotCommandGroup = CommandFactory.createCommandGroup(data);
		} else {
			LogHelper.log(TAG, "Header Version and Signature mis-match");
		}
		return robotCommandGroup;
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

	// TODO: add a field for robotId. Once we have multiple robot support, we will need that field.
	private void notifyPacketReceived(final String robotId, final RobotCommandsGroup robotCommandGroup)
	{
		for (int i = 0; i < robotCommandGroup.size(); i++) {

			final RobotPacket robotPacket = robotCommandGroup.getRobotPacket(i);
			if (mTcpDataPacketListener == null) {
				return;
			}

			if (mHandler != null) {
				mHandler.post(new Runnable() {
					public void run() {
						mTcpDataPacketListener.onDataReceived(robotId, robotPacket);
					}
				});
			}
			else {
				mTcpDataPacketListener.onDataReceived(robotId, robotPacket);
			}
		}
	}

	public void connectToRobot(final String robotId, final String ipAddress) {
		Runnable task = new Runnable() {

			public void run() {
				connectToRobotInternal(robotId, ipAddress);
			}
		};

		TaskUtils.scheduleTask(task, 0);
	}
	
	public void connectToRobot(final String robotId) {
		LogHelper.log(TAG, "connectToRobot called. Robot Id = " + robotId);
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
							connectToRobotInternal(robotId, robotInfo.getRobotIpAddress());
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

	private void connectToRobotInternal(String robotId, String ipAddress) {
		LogHelper.log(TAG, "connectToRobot called");
		if(!isConnected(robotId)) {
			LogHelper.log(TAG, "Not connected to robot. Connecting now.");
		} 
		else {
			LogHelper.log(TAG, "Already connected to robot. Breaking Connection.");
		}
		mTcpRobotConnectHelper.connectToRobot(robotId, ipAddress);
	}

	public void closePeerConnection(final String robotId)
	{
		Runnable task = new Runnable() {
			public void run() {
				closePeerConnectionInternal(robotId);
			}
		};

		TaskUtils.scheduleTask(task, 0);
	}

	private void closePeerConnectionInternal(String robotId)
	{
		LogHelper.log(TAG, "closePeerConnection called");
		if(isConnected(robotId)) {	
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

	//TODO: Find if the robot is connected. Map the serial id with the existing connections.
	public boolean isConnected(String robotId) {
		LogHelper.log(TAG, "isConnected transport: "+robotId);
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

	public  void sendRobotCommand(RobotPacket robotPacket, Transport transport) {
		byte[] packet = getRobotPacket(robotPacket);
		sendRobotPacketAsync(transport,packet);
	}
	
	public  void sendRobotCommand(String robotId, RobotPacket robotPacket) {
		byte[] packet = getRobotPacket(robotPacket);
		RobotConnectionInfo robotConnectionInfo = getRobotConnectionInfo(robotId);
		if (robotConnectionInfo != null) {
			Transport transport = robotConnectionInfo.getTransport();
			LogHelper.log(TAG, "Connection exist. Sending command.");
			sendRobotPacketAsync(transport,packet);
		} else {
			LogHelper.log(TAG, "Connection does not exist.");
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
			dos.writeInt(packet.length);
			dos.write(packet);
		}
		catch (Exception e) {
			LogHelper.log(TAG, "Exception in getBytes", e);
			return null;
		}
		return bos.toByteArray();
	}

	private  void sendRobotPacket(Transport transport,  byte[] packet )
	{
		if (packet == null) {
			LogHelper.log(TAG, "Packet is null");
			return;
		}

		try {
			transport.send(packet);
			LogHelper.log(TAG, "Packet is sent");
		}
		catch (IOException e) {
			LogHelper.log(TAG, "Exception in sendRobotPacket", e);
		}
	}

	private  void sendRobotPacketAsync(final Transport transport, final  byte[] packet )
	{
		if (transport == null) {
			LogHelper.log(TAG, "Transport is null. Cannot send packet");
			return;
		}
		Runnable task = new Runnable() {

			public void run() {
				sendRobotPacket( transport, packet );
			}
		};
		Thread t = new Thread(task);
		t.start();
	}

	public Transport getTransport(String robotId) {
		RobotConnectionInfo robotConnectionInfo = getRobotConnectionInfo(robotId);
		if (robotConnectionInfo != null) {
			return robotConnectionInfo.getTransport();
		} else {
			LogHelper.log(TAG, "No transport exists for the robotId");
			return null;
		}
	}

	// TODO: If supporting multiple connections. Change this function to return a Arraylist<RobotConnectionInfo> 
	public RobotConnectionInfo getConnectedRobotInfo() {
		return mRobotConnectionInfo;
	}

	private RobotConnectionInfo createRobotConnectionInfo(String robotId, String ipAddress, Transport transport) {
		//TODO: Right now only one connectionInfoObject as supports only one connection.
		mRobotConnectionInfo = new RobotConnectionInfo(robotId, ipAddress, transport);
		return mRobotConnectionInfo;
	}

	private RobotConnectionInfo getRobotConnectionInfo(String robotId) {
		if ((mRobotConnectionInfo != null) && (mRobotConnectionInfo.getRobotId().equals(robotId))) {
			return mRobotConnectionInfo;
		} else {
			LogHelper.log(TAG, "RobotConnectionInfo does not exist for robotId:" + robotId);
			return null;
		}
	}

	private void deleteConnectionInfo(String robotId) {
		LogHelper.log(TAG, "Deleting Peer robot connection info");
		//TODO: Right now only one connectionInfoObject as supports only one connection.
		if (mRobotConnectionInfo != null) {
			mRobotConnectionInfo.setIpAddress(null);
			mRobotConnectionInfo.setRobotId(null);
			mRobotConnectionInfo.setTransport(null);
		}
		mRobotConnectionInfo = null;
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
