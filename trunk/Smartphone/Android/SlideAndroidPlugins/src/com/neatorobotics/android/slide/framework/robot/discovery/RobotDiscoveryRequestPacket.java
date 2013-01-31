package com.neatorobotics.android.slide.framework.robot.discovery;

import java.util.HashMap;
import java.util.Map;

public class RobotDiscoveryRequestPacket {
	
	private int command;
	private String requestId;
	private String userId;
	private String robotId;
	private HashMap<String, String> commandParams;
	
	private RobotDiscoveryRequestPacket(int command, String userId)
	{
		this.command = command;
		this.userId = userId;
		commandParams = new HashMap<String, String>();
	}
	
	private RobotDiscoveryRequestPacket(int command, String userId, Map<String, String> commandParams)
	{
		this.command = command;
		this.userId = userId;
		this.commandParams = new HashMap<String, String>(commandParams);
	}
	
	
	public static RobotDiscoveryRequestPacket createRobotCommand(int commandId, String userId)
	{
		RobotDiscoveryRequestPacket robotCommand = new RobotDiscoveryRequestPacket(commandId, userId);
		return robotCommand;
	}
	
	public static RobotDiscoveryRequestPacket createRobotCommandWithParams(int commandId, String userId, Map<String, String> commandParams)
	{
		RobotDiscoveryRequestPacket robotCommand = new RobotDiscoveryRequestPacket(commandId, userId, commandParams);
		return robotCommand;
	}

	public int getCommand() {
		return command;
	}
	
	public String getCommandParam(String paramKey)
	{
		if (commandParams.containsKey(paramKey)) {
			return commandParams.get(paramKey);
		}
		
		return "";
	}
	
	public Map<String, String> getCommandParams()
	{
		return commandParams;
	}
	
	
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRobotId() {
		return robotId;
	}

	public void setRobotId(String robotId) {
		this.robotId = robotId;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n*** Discovery Request Packet ***\n");
		sb.append("----------------------\n");
		sb.append("Command = " + command);
		sb.append("Request Id = " + requestId);
		sb.append("\nUser Id = " + userId);
		sb.append("\nRobot Id = " + robotId);
		sb.append("Request Id = " + requestId);
		sb.append("\nParams...\n");
		
		for (String key: commandParams.keySet()) {
			sb.append("\t");
			sb.append(key);
			sb.append("\t");
			sb.append(commandParams.get(key));
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
