package com.neatorobotics.android.slide.framework.transport;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class UdpTransport implements Transport {
	
	private static final String TAG = UdpTransport.class.getSimpleName();
	
	private InetAddress mPeerAddress;
	private int mPeerPort;
	private DatagramSocket mSocket;
	
	private static final int MAX_BUFFER_SIZE = 1 * 1024;

	private UdpTransport(InetAddress peerAddress , int peerPort, DatagramSocket socket)
	{
		mPeerAddress = peerAddress;
		mPeerPort = peerPort;
		mSocket = socket;
	}
	
	public static UdpTransport createUdpTransport(InetAddress peerAddress, int peerPort, int localBindPort)
	{
		DatagramSocket socket;
		try {
			socket = new DatagramSocket(localBindPort);
			
			UdpTransport udpTransport = new UdpTransport(peerAddress, peerPort, socket);
			return udpTransport;
		} 
		catch (SocketException e) {
			LogHelper.log(TAG, "Exception in createUdpTransport", e);
		}
		
		return null;
	}

	public void send(byte[] data) throws IOException {
		if (mSocket == null) {
			LogHelper.log(TAG, "mSocket is null. Could not send data");
			return;
		}
		DatagramPacket sendPacket = new DatagramPacket(data,
				data.length, 
				mPeerAddress, mPeerPort);
		try {
			mSocket.send(sendPacket);
		} 
		catch (IOException e) {
			LogHelper.log(TAG, "Exception in send", e);
		}
		
	}
	
	public void read(byte [] data) throws IOException {
		if (mSocket == null) {
			LogHelper.logD(TAG, "Socket is null");
			return;
		}
		DatagramPacket receive_packet = new DatagramPacket(data, data.length);
		mSocket.receive(receive_packet);
	}

	public void close() {
		if (mSocket != null) {
			mSocket.close();
			mSocket = null;
		}
	}

	public DatagramPacket readDatagram() throws IOException {
		if (mSocket == null) {
			LogHelper.logD(TAG, "Socket is null");
			return null;
		}
		byte [] buffer = new byte[MAX_BUFFER_SIZE];
		DatagramPacket receive_packet = new DatagramPacket(buffer, buffer.length );
		mSocket.receive(receive_packet);
		return receive_packet;
	}

	// UDP does not support the input stream. So returning null from here
	public InputStream getInputStream() {
		return null;
	}

	public int getVersion() {
		// TODO Auto-generated method stub
		return AppConstants.UDP_PACKET_VERSION;
	}

	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

}
