package com.neatorobotics.android.slide.framework.udp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import android.content.Context;
import android.os.Handler;
import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.robot.commands.CommandFactory;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandsGroup;
import com.neatorobotics.android.slide.framework.robot.commands.RobotDiscoveryCommand;
import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;
import com.neatorobotics.android.slide.framework.robot.commands.RobotPacketBundle;
import com.neatorobotics.android.slide.framework.transport.Transport;
import com.neatorobotics.android.slide.framework.transport.TransportFactory;
import com.neatorobotics.android.slide.framework.utils.DeviceUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.xml.NetworkXmlHelper;

public class UdpConnectionHelper {

	private static final String TAG = UdpConnectionHelper.class.getSimpleName();

	private RobotDiscoveryListener mRobotDiscoveryListener;
	private RobotDiscoveryListener mAssociatedRobotDiscoveryListener;
	private Handler mHandler;
	private Context mContext;
	private static final int MAX_DISCOVERY_TIME_OUT = (5 * 1000); // 5 seconds is the max discovery time for the robots

	private static final int UDP_SEND_DISCOVERY_BROADCAST_PORT = AppConstants.UDP_SMART_APPS_BROADCAST_PORT;
	private static final int UDP_ROBOT_DISCOVERY_BROADCAST_PORT = AppConstants.UDP_ROBOT_BROADCAST_PORT;

	private Transport mTrasport; 

	private UdpServiceDiscoveryHelper mUdpServiceDiscoveryHelper;
	private boolean isDiscoveryInProgress = false; 

	public UdpConnectionHelper(Context context)
	{
		mUdpServiceDiscoveryHelper = new UdpServiceDiscoveryHelper();
		mContext = context.getApplicationContext();
	}

	private static final int getUdpBroadcastPort()
	{
		return UDP_SEND_DISCOVERY_BROADCAST_PORT;
	}

	public void setUdpConnectionListener(RobotDiscoveryListener udpDataPacketListener)
	{
		mRobotDiscoveryListener = udpDataPacketListener;
	}

	public void setHandler(Handler handler)
	{
		mHandler = handler;
	}

	private class UdpServiceDiscoveryHelper
	{
		private boolean mExplicitCloseSocket = false;

		public UdpServiceDiscoveryHelper()
		{

		}

		public void startDiscovery()
		{
			LogHelper.log(TAG, "startDiscovery internal called");
			InetAddress peerAddress = UdpUtils.getBroadcastIp(mContext);
			int localBindPort = getUdpBroadcastPort();
			int peerPort = UDP_ROBOT_DISCOVERY_BROADCAST_PORT;
			LogHelper.logD(TAG, "UDP localBindPort = " + localBindPort);
			LogHelper.logD(TAG, "UDP discovery port = " + peerPort);

			if (mTrasport == null) {
				mTrasport = TransportFactory.createUdpTransport(peerAddress, peerPort, localBindPort);
				if (mTrasport == null) {
					LogHelper.log(TAG, "mTransport is null");
					return;
				}
			}

			notifyDiscoveryStarted();
			Runnable task = new Runnable() {

				public void run() {
					LogHelper.log(TAG, "Now listening on the port");
					listenOnPortAsync(mTrasport);
					// wait for 500 milliseconds before sending broadcast packet
					TaskUtils.sleep(2000);
					LogHelper.log(TAG, "sending discovery packet");
					sendDiscoveryPacket(mTrasport);


				}
			};

			TaskUtils.scheduleTask(task, 0);

			Runnable stopThreadTask = new Runnable() {

				public void run() {
					LogHelper.log(TAG, "stopThreadTask called");
					if (mTrasport != null) {
						LogHelper.log(TAG, "closing socket");
						mExplicitCloseSocket = true;
						mTrasport.close();
						mTrasport = null;

						notifyDiscoveryEnd();

					}
				}
			};

			TaskUtils.scheduleTask(stopThreadTask, MAX_DISCOVERY_TIME_OUT);
		}

		private byte[] getRobotDiscoveryRequestPacket()
		{
			RobotDiscoveryCommand discoveryCommand = CommandFactory.createRobotDicoveryCommand(mContext);
			String macAddress = DeviceUtils.getDeviceMacAddress(mContext);

			if (macAddress == null) {
				//TODO: add a toast notification.
				LogHelper.log(TAG, "Mac address is null. Please check network connection");
				notifyDiscoveryEnd();
				return null;
			}
			discoveryCommand.setDeviceId(macAddress);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);

			byte[] packet = NetworkXmlHelper.commandToXml(discoveryCommand);
			try {
				dos.write(packet);
			}
			catch (Exception e) {
				LogHelper.log(TAG, "Exception in getBytes", e);
				return null;
			}
			return bos.toByteArray();

		}

		private byte[] getAssociatedRobotDiscoveryRequestPacket(String robotId)
		{
			//RobotDiscoveryCommand discoveryCommand = CommandFactory.createRobotDicoveryCommand(mContext);
			RobotPacket discoverAssociatedRobotPacket = new RobotPacket(RobotCommandPacketConstants.PACKET_TYPE_ASSOCIATED_ROBOT_DISCOVERY);
			discoverAssociatedRobotPacket.getBundle().putString(RobotCommandPacketConstants.KEY_ROBOT_SERIAL_ID, robotId);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);

			byte[] packet = NetworkXmlHelper.commandToXml(discoverAssociatedRobotPacket);
			try {
				dos.write(packet);
			}
			catch (Exception e) {
				LogHelper.log(TAG, "Exception in getBytes", e);
				return null;
			}
			return bos.toByteArray();
		}



		private void sendDiscoveryPacket(Transport transport)
		{
			byte [] discoveryPacket = getRobotDiscoveryRequestPacket();
			if (discoveryPacket == null) {
				LogHelper.log(TAG, "sendDiscoveryPacket - discoveryPacket is null");
				return;
			}

			LogHelper.log(TAG, "Sent data: " + discoveryPacket.length);

			try {
				transport.send(discoveryPacket);
			}
			catch (IOException e) {
				LogHelper.log(TAG, "Exception in sendDiscoveryPacket", e);
			}
		}

		private void sendAssociatedRobotDiscoveryPacket(Transport transport, String robotId)
		{
			byte [] packet = getAssociatedRobotDiscoveryRequestPacket(robotId);
			if (packet == null) {
				LogHelper.log(TAG, "sendDiscoveryPacket - discoveryPacket is null");
				return;
			}

			LogHelper.log(TAG, "Sent data: " + packet.length);

			try {
				transport.send(packet);
			}
			catch (IOException e) {
				LogHelper.log(TAG, "Exception in sendDiscoveryPacket", e);
			}
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
					DatagramPacket receive_packet = null;
					String localIp = UdpUtils.getOwnIp(mContext);
					while(true) {
						try {
							LogHelper.log(TAG, "waiting for datagram packet");
							receive_packet = transport.readDatagram();
							LogHelper.log(TAG, "Recevied Message!");
							InetAddress remoteIpAddress = receive_packet.getAddress();
							int recv_port = receive_packet.getPort();
							LogHelper.logD(TAG, "recv_port = " + recv_port);
							if(remoteIpAddress.getHostAddress().toString().equals(localIp)) {
								LogHelper.log(TAG, "Received datagram from same machine!");
								continue;
							}
							String stringIpaddress = remoteIpAddress.getHostAddress().toString();
							LogHelper.log(TAG, "Robot found. Ip address = " + stringIpaddress);		

							// Parse the data
							byte [] data = receive_packet.getData();

							ByteArrayInputStream bias = new ByteArrayInputStream(data);

							LogHelper.log(TAG, "Received data: "+receive_packet.getLength());
							byte[] dataPacket = new byte[receive_packet.getLength()];
							bias.read(dataPacket, 0, receive_packet.getLength());

							ByteArrayInputStream bais = new ByteArrayInputStream(dataPacket);
							DataInputStream dis = new DataInputStream(bais);

							RobotCommandsGroup robotCommandsGroup = CommandFactory.createCommandGroup(dis);
							//Handle all commands
							RobotPacket robotPacket = robotCommandsGroup.getRobotPacket(0);
							if (robotPacket == null) {
								LogHelper.log(TAG, "robotPacket is null");
								continue;
							}
							parsePacket(robotPacket);
						} 
						catch (IOException e) {
							if (!mExplicitCloseSocket) {
								LogHelper.log(TAG, "EXCEPTION in listenOnPort", e);
							}
							break;
						}
					}

				}
			};

			Thread t = new Thread(readPacketTask);
			t.start();
		}


		//TODO Re-write this whole API.
		public void parsePacket(RobotPacket packet) {
			int commandId = packet.getCommandId();
			if (commandId == RobotCommandPacketConstants.PACKET_TYPE_ROBOT_DISCOVERY_RESPONSE) {
				RobotPacketBundle robotPacketBundle = packet.getBundle();
				
				if (robotPacketBundle != null) {
					parseResponseAndGetRobotInfo(robotPacketBundle, false);
				} 
				else {
					LogHelper.log(TAG, "networkPacketBundle is null");
					return;
				} 
			} 
			else if (commandId == RobotCommandPacketConstants.PACKET_TYPE_ASSOCIATED_ROBOT_DISCOVERY_RESPONSE) {
				RobotPacketBundle robotPacketBundle = packet.getBundle();
				
				if (robotPacketBundle != null) {
					parseResponseAndGetRobotInfo(robotPacketBundle, true);
				} 
				else {
					LogHelper.log(TAG, "networkPacketBundle is null");
					return;
				} 
			}
		}

		private void parseResponseAndGetRobotInfo(RobotPacketBundle robotPacketBundle, boolean bAssociated) {
			final RobotInfo robotInfo = new RobotInfo();
			String robotId = robotPacketBundle.getString(RobotCommandPacketConstants.KEY_ROBOT_ID);
			String robotIpAddress = robotPacketBundle.getString(RobotCommandPacketConstants.KEY_ROBOT_IP_ADDRESS);
			String robotSerialId = robotPacketBundle.getString(RobotCommandPacketConstants.KEY_ROBOT_SERIAL_ID);
			int robotPort = robotPacketBundle.getInt(RobotCommandPacketConstants.KEY_ROBOT_PORT);
			String robotName = robotPacketBundle.getString(RobotCommandPacketConstants.KEY_ROBOT_NAME);
			robotInfo.setRobotId(robotId);
			robotInfo.setRobotIpAddress(robotIpAddress);
			robotInfo.setSerialId(robotSerialId);
			robotInfo.setRobotPort(robotPort);
			robotInfo.setRobotName(robotName);
			notifyRobotDiscovery(robotInfo, bAssociated);
		}

		public void findRobotForRobotId(final String robotId, final RobotDiscoveryListener listener)
		{
			mAssociatedRobotDiscoveryListener = listener;
			LogHelper.log(TAG, "startAssociatedRobotDiscovery internal called");
			InetAddress peerAddress = UdpUtils.getBroadcastIp(mContext);
			int localBindPort = getUdpBroadcastPort();
			int peerPort = UDP_ROBOT_DISCOVERY_BROADCAST_PORT;
			LogHelper.logD(TAG, "UDP localBindPort = " + localBindPort);
			LogHelper.logD(TAG, "UDP discovery port = " + peerPort);

			if (mTrasport == null) {
				mTrasport = TransportFactory.createUdpTransport(peerAddress, peerPort, localBindPort);
				if (mTrasport == null) {
					LogHelper.log(TAG, "mTransport is null");
					return;
				}
			}
			Runnable task = new Runnable() {

				public void run() {
					LogHelper.log(TAG, "Now listening on the port");
					listenOnPortAsync(mTrasport);
					// wait for 500 milliseconds before sending broadcast packet
					TaskUtils.sleep(2000);
					LogHelper.log(TAG, "sending associated robot discovery packet");
					sendAssociatedRobotDiscoveryPacket(mTrasport, robotId);
				}
			};

			TaskUtils.scheduleTask(task, 0);

			Runnable stopThreadTask = new Runnable() {

				public void run() {
					LogHelper.log(TAG, "stopThreadTask called");
					if (mTrasport != null) {
						LogHelper.log(TAG, "closing socket");
						mExplicitCloseSocket = true;
						mTrasport.close();
						mTrasport = null;
						mAssociatedRobotDiscoveryListener.onDiscoveryEnd();
					}
				}
			};
			TaskUtils.scheduleTask(stopThreadTask, MAX_DISCOVERY_TIME_OUT);
		}
	}


	public void startDiscovery()
	{
		LogHelper.log(TAG, "startDiscovery called");
		if (isDiscoveryInProgress) {
			LogHelper.log(TAG, "discovery in progress. first cancel the existing discovery");
			cancelDiscovery();
		}

		if (mUdpServiceDiscoveryHelper != null) {
			mUdpServiceDiscoveryHelper.startDiscovery();
		}
	}

	public void discoverAssociatedRobot(String robotId, RobotDiscoveryListener listener) {
		LogHelper.log(TAG, "discoverAssociatedRobot called");
		if (mUdpServiceDiscoveryHelper != null) {
			mAssociatedRobotDiscoveryListener = listener;
			mUdpServiceDiscoveryHelper.findRobotForRobotId(robotId, listener);
		}
	}

	public void cancelDiscovery()
	{
		LogHelper.log(TAG, "cancelDiscovery called");
		// TODO: needs to implement
	}

	private void notifyRobotDiscovery(final RobotInfo robotInfo, boolean bAssociated) {
		
		final RobotDiscoveryListener discoveryListener = (bAssociated)?mAssociatedRobotDiscoveryListener:mRobotDiscoveryListener;

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
		if (mRobotDiscoveryListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {

					public void run() {
						mRobotDiscoveryListener.onDiscoveryStarted();

					}
				});
			}
			else {
				mRobotDiscoveryListener.onDiscoveryStarted();
			}
		}
	}

	private void notifyDiscoveryEnd() {
		// after parsing packet send this data to the callback
		if (mRobotDiscoveryListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {

					public void run() {
						mRobotDiscoveryListener.onDiscoveryEnd();
					}
				});
			}
			else {
				mRobotDiscoveryListener.onDiscoveryEnd();
			}
		}
	}
}
