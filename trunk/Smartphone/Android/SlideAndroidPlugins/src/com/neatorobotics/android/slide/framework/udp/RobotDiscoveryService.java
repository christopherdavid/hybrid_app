package com.neatorobotics.android.slide.framework.udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Set;
import android.content.Context;
import android.os.Handler;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.robot.discovery.RobotDiscoveryCommandPacket;
import com.neatorobotics.android.slide.framework.robot.discovery.RobotDiscoveryPacketBuilder;
import com.neatorobotics.android.slide.framework.robot.discovery.RobotDiscoveryPacketHeader;
import com.neatorobotics.android.slide.framework.robot.discovery.RobotDiscoveryPacketParser;
import com.neatorobotics.android.slide.framework.robot.discovery.RobotDiscoveryRequestPacket;
import com.neatorobotics.android.slide.framework.robot.discovery.RobotDiscoveryResponsePacket;
import com.neatorobotics.android.slide.framework.transport.Transport;
import com.neatorobotics.android.slide.framework.transport.TransportFactory;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;

public class RobotDiscoveryService {

	private static final String TAG = RobotDiscoveryService.class.getSimpleName();

	private Handler mHandler;
	private Context mContext;
	
	private static final int MAX_DISCOVERY_TIME_OUT = (5 * 1000); // 5 seconds is the max discovery time for the robots

	private static final int UDP_SEND_GLOBAL_ROBOT_DISCOVERY_BROADCAST_PORT = 48001;
	private static final int UDP_SEND_ROBOT_DISCOVERY_BROADCAST_PORT = 48002;
	private static final int UDP_ROBOT_DISCOVERY_BROADCAST_PORT = 48003;
	
	public static final int ROBOT_DISCOVERY_REQUEST = 5001;
	
	private static final int DISCOVERY_PACKET_SIGNATURE = 0xCafeBabe;
	private static final int DISCOVERY_PACKET_VERSION = 1;

	private Transport mTrasport; 

	private RobotDiscoveryListener mListener;
	private int mLocalBindPort;
	private String mRobotId;
	private String mUserId;
	
	private Object mRobotMapLock = new Object();
	private static final int MAX_SEND_PACKET_COUNT = 3;
	
	private static class RobotDiscoveryInternal
	{
		String fromIpAddress;
		byte [] data;
	}
	
	private HashMap<String, RobotInfo> robotMap = new HashMap<String, RobotInfo>();
	
	public RobotDiscoveryService(Context context)
	{
		mContext = context.getApplicationContext();
	}
	
	private RobotDiscoveryService(Context context, int localBindPort, String userId, RobotDiscoveryListener listener)
	{
		mContext = context.getApplicationContext();
		mLocalBindPort = localBindPort;
		mUserId = userId;
		mListener = listener;
	}
	
	private RobotDiscoveryService(Context context, int localBindPort, String userId, String robotId, RobotDiscoveryListener listener)
	{
		mContext = context.getApplicationContext();
		mLocalBindPort = localBindPort;
		mRobotId = robotId;
		mUserId = userId;
		mListener = listener;
	}
	
	public static void startRobotsDiscovery(Context context, String userId, RobotDiscoveryListener listener)
	{
		LogHelper.log(TAG, "startRobotsDiscovery called.");
		int localBindPort = UDP_SEND_GLOBAL_ROBOT_DISCOVERY_BROADCAST_PORT;
		LogHelper.log(TAG, "Local port to bind = " + UDP_SEND_GLOBAL_ROBOT_DISCOVERY_BROADCAST_PORT);
		RobotDiscoveryService robotDiscoverService = new RobotDiscoveryService(context, localBindPort, userId, listener);
		robotDiscoverService.startDiscovery();
	}
	
	public static void startRobotsDiscovery(Context context, String userId, String robotId, RobotDiscoveryListener listener)
	{
		LogHelper.log(TAG, "startRobotsDiscovery called.");
		int localBindPort = UDP_SEND_ROBOT_DISCOVERY_BROADCAST_PORT;
		LogHelper.log(TAG, "Local port to bind = " + UDP_SEND_ROBOT_DISCOVERY_BROADCAST_PORT);
		RobotDiscoveryService robotDiscoverService = new RobotDiscoveryService(context, localBindPort, userId, robotId, listener);
		robotDiscoverService.startDiscovery();
	}
	
	private void startDiscovery()
	{
		LogHelper.log(TAG, "startDiscovery internal called");
		InetAddress peerAddress = UdpUtils.getBroadcastIp(mContext);
		int localBindPort = mLocalBindPort;
		int peerPort = UDP_ROBOT_DISCOVERY_BROADCAST_PORT;
		LogHelper.logD(TAG, "UDP localBindPort = " + localBindPort);
		LogHelper.logD(TAG, "UDP discovery port = " + peerPort);

		final Transport transport = createUdpTransport(peerAddress, localBindPort, peerPort);
		
		if (transport == null) {
			LogHelper.log(TAG, "Transport is null. Would not be able to send the discovery command");
			return;
		}

		notifyDiscoveryStarted();
		listenOnPortAsync(transport);
		
		TaskUtils.sleep(1000);
		
		sendDiscoveryCommandAsync();

		sendStopDiscoveryCommandAfterTimeOut(MAX_DISCOVERY_TIME_OUT);
	}

	private void sendStopDiscoveryCommandAfterTimeOut(int timeout) {
		Runnable stopThreadTask = new Runnable() {

			public void run() {
				LogHelper.log(TAG, "stopThreadTask called");
				if (mTrasport != null) {
					LogHelper.log(TAG, "closing socket");
					mTrasport.close();
					mTrasport = null;
					notifyDiscoveryEnd();

				}
			}
		};

		TaskUtils.scheduleTask(stopThreadTask, timeout);
	}

	private void sendDiscoveryCommandAsync() {
		Runnable task = new Runnable() {

			public void run() {
				LogHelper.log(TAG, "Now listening on the port");
				LogHelper.log(TAG, "sending discovery packet");
				sendDiscoveryPacket(mTrasport);
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}


	private Transport createUdpTransport(InetAddress peerAddress, int localBindPort,
			int peerPort) {
		
		if (mTrasport == null) {
			mTrasport = TransportFactory.createUdpTransport(peerAddress, peerPort, localBindPort);
		}
		return mTrasport;
	}

	private RobotDiscoveryCommandPacket getRobotDiscoveryPacket()
	{
		
		RobotDiscoveryPacketHeader header = new RobotDiscoveryPacketHeader();
		header.setSignature(DISCOVERY_PACKET_SIGNATURE);
		header.setVersion(DISCOVERY_PACKET_VERSION);
		 
		HashMap<String, String> emptyMap = new HashMap<String, String>();
		RobotDiscoveryRequestPacket robotDiscoveryRequestPacket = 
				RobotDiscoveryRequestPacket.createRobotCommandWithParams(ROBOT_DISCOVERY_REQUEST, mUserId, emptyMap);
		
		String requestId = AppUtils.generateNewRequestId(mContext);
		robotDiscoveryRequestPacket.setRequestId(requestId);
		robotDiscoveryRequestPacket.setRobotId(mRobotId);
		RobotDiscoveryCommandPacket packet = RobotDiscoveryCommandPacket.createRobotCommandPacket(header, robotDiscoveryRequestPacket);
		
		return packet;
	}

	private void sendDiscoveryPacket(Transport transport)
	{
		RobotDiscoveryCommandPacket robotDiscoveryRequestPacket = getRobotDiscoveryPacket();
		
		RobotDiscoveryPacketBuilder builder = new RobotDiscoveryPacketBuilder();
		byte [] discoveryPacket = builder.convertRobotCommandsToBytes(robotDiscoveryRequestPacket);
		
		if (discoveryPacket == null) {
			LogHelper.log(TAG, "sendDiscoveryPacket - discoveryPacket is null");
			return;
		}

		LogHelper.log(TAG, "Sent data: " + discoveryPacket.length);
		logPacket(discoveryPacket);
		try {
			for (int i = 0; i < MAX_SEND_PACKET_COUNT; i++) {
				transport.send(discoveryPacket);
				TaskUtils.sleep(500);
			}
		}
		catch (IOException e) {
			LogHelper.log(TAG, "Exception in sendDiscoveryPacket", e);
		}
	}

	private RobotDiscoveryInternal readData(Transport transport) throws IOException
	{
		
		DatagramPacket receive_packet = transport.readDatagram();
		LogHelper.log(TAG, "Recevied Message!");
		InetAddress remoteIpAddress = receive_packet.getAddress();
		int recv_port = receive_packet.getPort();
		LogHelper.logD(TAG, "recv_port = " + recv_port);
		
		String stringIpaddress = remoteIpAddress.getHostAddress().toString();
		LogHelper.log(TAG, "Robot found. Ip address = " + stringIpaddress);		

		// Parse the data
		byte [] data = receive_packet.getData();
		ByteArrayInputStream bias = new ByteArrayInputStream(data);
		LogHelper.log(TAG, "Received data: "+receive_packet.getLength());
		byte[] dataPacket = new byte[receive_packet.getLength()];
		bias.read(dataPacket, 0, receive_packet.getLength());
		
		RobotDiscoveryInternal fromRobot = new RobotDiscoveryInternal();
		fromRobot.data = dataPacket;
		fromRobot.fromIpAddress = stringIpaddress;
		return fromRobot;
	}
	
	// We do not process the packets from the same IPADDRESS as ours.
	private void listenOnPortAsync(final Transport transport) {

		// Now we have the DatagramSocket, we will wait for the other robots to send packets
		if(transport == null) {
			LogHelper.log(TAG, "ERROR - why transport will be null here");
			return;
		}

		Runnable readPacketTask = new Runnable() {

			public void run() {
				while(true) {
					try {
						LogHelper.log(TAG, "waiting for datagram packet");
						RobotDiscoveryInternal fromRobot = readData(transport);
						handleRobotDiscoveryResponse(fromRobot);
					} 
					catch (IOException e) {
						break;
					}
				}

			}
		};

		Thread t = new Thread(readPacketTask);
		t.start();
	}

	private RobotDiscoveryCommandPacket getDiscoveryResponse(byte[] dataPacket) {
		RobotDiscoveryPacketParser parser = new RobotDiscoveryPacketParser();
		try {
			logPacket(dataPacket);
			RobotDiscoveryCommandPacket packet = parser.convertBytesToRobotDiscoveryCommands(dataPacket);
			return packet;
		}
		catch (Exception e) {
			LogHelper.log(TAG, "Exception in parsing data", e);
		}
		
		return null;
		
	}
	
	private void logPacket(byte [] data)
	{
		try {
			String request = new String(data, "UTF-8");
			LogHelper.logD(TAG, "Request = " + request);
		}
		catch (Exception e) {
			LogHelper.logD(TAG, "Exception in forming a string in logPacket", e);
		}
	}

	private void notifyRobotDiscovery(final RobotInfo robotInfo) {

		final RobotDiscoveryListener discoveryListener = mListener;

		// after parsing packet send this data to the callback
		if (discoveryListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {

					public void run() {
						discoveryListener.onRobotDiscovered(robotInfo);

					}
				});
			}
			else {
				discoveryListener.onRobotDiscovered(robotInfo);
			}
		}
	}

	private void notifyDiscoveryStarted() {
		// after parsing packet send this data to the callback
		if (mListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {

					public void run() {
						mListener.onDiscoveryStarted();

					}
				});
			}
			else {
				mListener.onDiscoveryStarted();
			}
		}
	}

	private void notifyDiscoveryEnd() {
		
		synchronized (mRobotMapLock) {
			Set<String> keys = robotMap.keySet();
			if ((keys != null) && (keys.size() > 0)) {
				for (String robotId : keys) {
					notifyRobotDiscovery(robotMap.get(robotId));
				}
				robotMap.clear();
			}
		}
		
		// after parsing packet send this data to the callback
		if (mListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {

					public void run() {
						mListener.onDiscoveryEnd();
					}
				});
			}
			else {
				mListener.onDiscoveryEnd();
			}
		}
	}

	private void handleRobotDiscoveryResponse(final RobotDiscoveryInternal fromRobotResponse) {
		
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				if (fromRobotResponse.data == null) {
					LogHelper.log(TAG, "Data Packet is null");
					return;
				}
				RobotDiscoveryCommandPacket responsePacket = getDiscoveryResponse(fromRobotResponse.data);
				if (responsePacket == null) {
					LogHelper.log(TAG, "failed to parse");
					return;
				}
				
				RobotDiscoveryResponsePacket response = responsePacket.getDiscoveryResponse();
				RobotInfo robotInfo = convertResponseToRobotInfo(fromRobotResponse.fromIpAddress, response);
				addRobotIntoMap(robotInfo);
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	private void addRobotIntoMap(RobotInfo robotInfo)
	{
		synchronized (mRobotMapLock) {
			robotMap.put(robotInfo.getSerialId(), robotInfo);	
		}
	}
	
	private RobotInfo convertResponseToRobotInfo(String fromIpAddress, RobotDiscoveryResponsePacket response)
	{
		RobotInfo robotInfo = new RobotInfo();
		robotInfo.setRobotId(response.getRobotId());
		robotInfo.setRobotIpAddress(fromIpAddress);
		robotInfo.setRobotName(response.getName());
		robotInfo.setSerialId(response.getRobotId());
		
		return robotInfo;
	}
}
