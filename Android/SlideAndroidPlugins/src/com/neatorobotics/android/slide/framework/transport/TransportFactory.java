package com.neatorobotics.android.slide.framework.transport;

import java.net.InetAddress;

public class TransportFactory {
	
	public static Transport createTransport(InetAddress peerAddress, int peerPort)
	{
		return TcpIpTransport.createTransport(peerAddress, peerPort);
	}

}
