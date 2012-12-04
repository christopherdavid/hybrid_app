package com.neatorobotics.android.slide.framework.robot.commands;

public class RobotDiscoveryCommand extends RobotPacket {

	private static final String KEY_DEVICE_NAME = "key_robot_discovery_device_name";
	private static final String KEY_DEVICE_ID = "key_robot_discovery_device_id";
	
	public RobotDiscoveryCommand()
	{
		super(RobotCommandPacketConstants.PACKET_TYPE_ROBOT_DISCOVERY);
	}
	
	public String getDeviceName() {
		String deviceName = mRobotPacketBundle.getString(KEY_DEVICE_ID);
		return deviceName;
		
	}
	public void setDeviceName(String deviceName) {
		mRobotPacketBundle.putString(KEY_DEVICE_NAME, deviceName);
	}
	
	public String getDeviceId() {
		String deviceId = mRobotPacketBundle.getString(KEY_DEVICE_ID);
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		mRobotPacketBundle.putString(KEY_DEVICE_ID, deviceId);
	}
	
}
