package com.neatorobotics.android.slide.framework.robot.commands;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import android.content.Context;

import com.neatorobotics.android.slide.framework.NetworkPacketBundle;
import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class CommandFactory {
	
	private static final String TAG = CommandFactory.class.getSimpleName();
	
	public static RobotDiscoveryCommand createRobotDicoveryCommand(Context context)
	{
		return new RobotDiscoveryCommand();
	}
	
	public static RobotPacket createCommand(byte [] data) 
	{
		RobotPacket robotPacket = null;
		
		if (data == null) {
			return null;
		}
		
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);
		try {
			int commandId = dis.readInt();
			NetworkPacketBundle bundle = NetworkPacketBundle.createBundleFromByteArray(dis);
			
			robotPacket = new RobotPacket(commandId, bundle);
		}
		catch (IOException e) {
			LogHelper.log(TAG, "Exception in createCommand (byte)", e);
		}
		return robotPacket;
	}
	
	public static RobotPacket createCommand(DataInputStream dis) 
	{
		RobotPacket robotPacket = null;
		
		if (dis == null) {
			return null;
		}
		try {
			int commandId = dis.readInt();
			NetworkPacketBundle bundle = NetworkPacketBundle.createBundleFromByteArray(dis);
			robotPacket = new RobotPacket(commandId, bundle);
		}
		catch (IOException e) {
			LogHelper.log(TAG, "Exception in createCommand (dis)", e);
		}
		
		return robotPacket;
	}
	
	

}
