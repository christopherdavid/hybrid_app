package com.neatorobotics.android.slide.framework.robot.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class RobotCommandHeader {
	
	public static final int ROBOT_COMMUNICATION_PACKET_HEADER_VERSION = 1;
	private static final String TAG = RobotCommandHeader.class.getSimpleName();
	public static byte[] getHeader()
	{	
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(AppConstants.APP_SIGNATURE);
			dos.writeInt(ROBOT_COMMUNICATION_PACKET_HEADER_VERSION);
		}
		catch (Exception e) {
			LogHelper.log(TAG, "Exception in getBytes", e);
		}
	
		return bos.toByteArray();
		
	}
}
