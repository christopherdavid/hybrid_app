package com.neatorobotics.android.slide.framework.robot.discovery;

import java.util.HashMap;
import java.util.Map;

public class RobotDiscoveryResponsePacket {

	private String requestId;
	private String robotId;
	private String name;
	private String chatId;
	private HashMap<String, String> paramData;
	
	private RobotDiscoveryResponsePacket(String requestId, Map<String, String> commandParams)
	{
		this.requestId = requestId;
		paramData = new HashMap<String, String>(commandParams);
	}
	
	public static RobotDiscoveryResponsePacket createResponseWithParams(String requestId, Map<String, String> params)
	{
		RobotDiscoveryResponsePacket response = new RobotDiscoveryResponsePacket(requestId, params);
		return response;
	}
	
	public String getRequestId()
	{
		return requestId;
	}
	

	
	public Map<String, String> getParams()
	{
		return paramData;
	}
	
	public String getRobotId() {
		return robotId;
	}

	public void setRobotId(String robotId) {
		this.robotId = robotId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n*** Response Packet ***\n");
		sb.append("----------------------\n");
		sb.append("Request Id = " + requestId);
		sb.append("\n Name = " + name);
		sb.append("\n Robot Id = " + robotId);
		sb.append("\n Chat Id = " + chatId);
		sb.append("\n Params...\n");
		
		for (String key: paramData.keySet()) {
			sb.append("\t");
			sb.append(key);
			sb.append("\t");
			sb.append(paramData.get(key));
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
