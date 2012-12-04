package com.neatorobotics.android.slide.framework.robot.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.transport.Transport;

public class RobotPacketHeader {
	
	private static final String TAG = RobotPacketHeader.class.getSimpleName();
	
	public static byte[] getHeader(Transport transport)
	{	
		int packetVersion = transport.getVersion();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(AppConstants.APP_SIGNATURE);
			dos.writeInt(packetVersion);
		}
		catch (Exception e) {
			LogHelper.log(TAG, "Exception in getBytes", e);
		}
	
		return bos.toByteArray();
		
	}

}
