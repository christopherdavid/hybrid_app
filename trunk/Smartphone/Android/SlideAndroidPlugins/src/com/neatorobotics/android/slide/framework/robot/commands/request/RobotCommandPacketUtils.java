package com.neatorobotics.android.slide.framework.robot.commands.request;

import java.util.HashMap;

import android.content.Context;

import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;

public class RobotCommandPacketUtils {
	
	//One step method to create RobotCommandPacket. This method can be used in other places too.
	public static String getRobotCommandPacket(Context context, int commandId, HashMap<String, String> commandParams, int distributionMode) {
		RequestPacket request = RequestPacket.createRequestPacket(context, commandId, commandParams);
		RobotRequests requests = new RobotRequests();
		requests.addCommand(request);
		request.setDistributionMode(distributionMode);
		RobotCommandPacketHeader header = RobotCommandPacketHeader.getRobotCommandHeader(RobotCommandPacketConstants.COMMAND_PACKET_SIGNATURE, RobotCommandPacketConstants.COMMAND_PACKET_VERSION);
		
		RobotCommandPacket robotCommandPacket = RobotCommandPacket.createRobotCommandPacket(header, requests);
		RobotCommandBuilder builder = new RobotCommandBuilder();
		String robotPacketInXmlFormat =  builder.convertRobotCommandsToString(robotCommandPacket);
		return robotPacketInXmlFormat;
	}
	
	//Method to get commandId from command.
	public static int getRobotIdFromCommand(String cleaningCommand) {
		RobotCommandParser parser = new RobotCommandParser();
		RobotCommandPacket packet = parser.convertStringToRobotCommands(cleaningCommand);
		if (packet != null && packet.isRequest()) {
			int commandId = packet.getRobotCommands().getCommand(0).getCommand();
			return commandId;
		}
		return -1;
	}
}
