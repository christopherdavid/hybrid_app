package com.neatorobotics.android.slide.framework.transport;

import java.net.InetAddress;

public class TransportFactory {
	
	public static Transport createUdpTransport(InetAddress peerAddress, int peerPort, int localBindPort)
	{
		return UdpTransport.createUdpTransport(peerAddress, peerPort, localBindPort);
	}
	
	public static Transport createTransport(InetAddress peerAddress, int peerPort)
	{
		return TcpIpTransport.createTransport(peerAddress, peerPort);
	}

}
