package com.neatorobotics.android.slide.framework.robot.discovery;

public class RobotDiscoveryPacketHeader {
	private int version;
	private int signature;
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public int getSignature() {
		return signature;
	}
	
	public void setSignature(int signature) {
		this.signature = signature;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n*** RobotCommandPacketHeader ***\n");
		sb.append("---------------------------------\n");
		sb.append("Version = " + version);
		sb.append("\n");
		sb.append("signature = " + signature);
		return sb.toString();
	}

}
