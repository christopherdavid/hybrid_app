package com.neatorobotics.android.slide.framework.robot.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import com.neatorobotics.android.slide.framework.NetworkPacketBundle;
import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class RobotPacket {
	
	private  final String TAG = getClass().getSimpleName();
	private int mCommandId;
	protected NetworkPacketBundle mNetworkPacketBundle;
	
	
	public RobotPacket(int commandId)
	{
		mCommandId = commandId;
		mNetworkPacketBundle = new NetworkPacketBundle();
	}
	
	public RobotPacket(int commandId, NetworkPacketBundle networkPacketBundle)
	{
		mCommandId = commandId;
		mNetworkPacketBundle = new NetworkPacketBundle(networkPacketBundle);
	}

	public int getCommandId() {
		return mCommandId;
	}

	public void setCommandId(int commandId) {
		this.mCommandId = commandId;
	}

	public int getPacketLength() {
		return 0;
	}

	
	public NetworkPacketBundle getBundle()
	{
		return mNetworkPacketBundle;
	}
	
	public byte [] getBytes()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(mCommandId);
			dos.write(mNetworkPacketBundle.toByteArray());
		}
		catch (Exception e) {
			LogHelper.log(TAG, "Exception in getBytes", e);
		}

		return bos.toByteArray();
	}
	

}
