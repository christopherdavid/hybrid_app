package com.neatorobotics.android.slide.framework.tcp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class TcpUtils {

	private static String TAG = TcpUtils.class.getSimpleName();

	public static InetAddress getRobotInetAddressFromRobotId(String RobotId) {
		// TODO: To be implemented
		return null;
	} 

	public static InetAddress getInetAddressFromIp(String IpAdress) {
		try {
			return InetAddress.getByName(IpAdress);
		} catch (UnknownHostException e) {
			LogHelper.log(TAG, "Error in getting Robot's Ip Address:" , e);
		}	
		return null;
	}
}
