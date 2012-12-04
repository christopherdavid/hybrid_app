package com.neatorobotics.android.slide.framework.robot.commands;

public class RobotDiscoveryCommand extends RobotPacket {

	private static final String KEY_DEVICE_NAME = "key_robot_discovery_device_name";
	private static final String KEY_DEVICE_ID = "key_robot_discovery_device_id";
	
	public RobotDiscoveryCommand()
	{
		super(RobotCommandPacketConstants.PACKET_TYPE_ROBOT_DISCOVERY);
	}
	
	public String getDeviceName() {
		String deviceName = mNetworkPacketBundle.getString(KEY_DEVICE_ID);
		return deviceName;
		
	}
	public void setDeviceName(String deviceName) {
		mNetworkPacketBundle.putString(KEY_DEVICE_NAME, deviceName);
	}
	
	public String getDeviceId() {
		String deviceId = mNetworkPacketBundle.getString(KEY_DEVICE_ID);
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		mNetworkPacketBundle.putString(KEY_DEVICE_ID, deviceId);
	}
	
}
