package com.neatorobotics.android.slide.framework.robot.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class RobotDiscoveryPacketHeader {
	public static final int DISCOVERY_PACKET_HEADER_VERSION = 1;
	private static final String TAG = RobotDiscoveryPacketHeader.class.getSimpleName();
	
	public static byte[] getHeader()
	{	
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(AppConstants.APP_SIGNATURE);
			dos.writeInt(AppConstants.UDP_PACKET_VERSION);
		}
		catch (Exception e) {
			LogHelper.log(TAG, "Exception in getBytes", e);
		}
	
		return bos.toByteArray();
		
	}
	
}
