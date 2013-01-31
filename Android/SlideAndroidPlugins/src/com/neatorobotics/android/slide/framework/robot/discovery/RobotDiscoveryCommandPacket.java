package com.neatorobotics.android.slide.framework.robot.discovery;

public class RobotDiscoveryCommandPacket {
	
	private RobotDiscoveryPacketHeader header;
	private RobotDiscoveryRequestPacket robotDiscoveryCommand;
	private RobotDiscoveryResponsePacket discoveryResponse;
	
	private RobotDiscoveryCommandPacket(RobotDiscoveryPacketHeader header, RobotDiscoveryRequestPacket robotDiscoveryCommand)
	{
		this.header = header;
		this.robotDiscoveryCommand = robotDiscoveryCommand;
	}
	
	private RobotDiscoveryCommandPacket(RobotDiscoveryPacketHeader header, RobotDiscoveryResponsePacket discoveryResponse)
	{
		this.header = header;
		this.discoveryResponse = discoveryResponse;
	}
	
	public static RobotDiscoveryCommandPacket createRobotCommandPacket(RobotDiscoveryPacketHeader header, RobotDiscoveryRequestPacket robotDiscoveryCommand)
	{
		RobotDiscoveryCommandPacket robotCommandPacket = new RobotDiscoveryCommandPacket(header, robotDiscoveryCommand);
		return robotCommandPacket;
	}
	
	public static RobotDiscoveryCommandPacket createRobotPacketWithResponseData(RobotDiscoveryPacketHeader header, RobotDiscoveryResponsePacket discoveryResponse)
	{
		RobotDiscoveryCommandPacket robotCommandPacket = new RobotDiscoveryCommandPacket(header, discoveryResponse);
		return robotCommandPacket;
	}
	
	public RobotDiscoveryPacketHeader getHeader()
	{
		return header;
	}
	
	public RobotDiscoveryRequestPacket getRobotDiscoveryCommand()
	{
		return robotDiscoveryCommand;
	}
	
	public RobotDiscoveryResponsePacket getDiscoveryResponse()
	{
		return discoveryResponse;
	}
	
	public boolean isRequest()
	{
		return (robotDiscoveryCommand != null);
	}
	
	public boolean isResponse()
	{
		return (discoveryResponse != null);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n*** Robot Discovery Command Packet ***\n");
		sb.append("-------------------------------\n");
		sb.append("Header = " + header);
		sb.append("\nisRequest =" + isRequest());
		if (isRequest()) {
			sb.append("\nRobot Discovery Commands = " + robotDiscoveryCommand);
		}
		else {
			sb.append("\nRobot Discovery Response = " + discoveryResponse);
		}
		return sb.toString();
	}

}
