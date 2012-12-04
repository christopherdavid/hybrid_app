package com.neatorobotics.android.slide.framework.robot.commands;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import android.content.Context;
import com.neatorobotics.android.slide.framework.xml.NetworkXmlHelper;

public class CommandFactory {
	
	private static final String TAG = CommandFactory.class.getSimpleName();
	
	public static RobotDiscoveryCommand createRobotDicoveryCommand(Context context)
	{
		return new RobotDiscoveryCommand();
	}

	public static RobotCommandsGroup createCommandGroup(DataInputStream dis) {
		
		RobotCommandsGroup commandsGroup = NetworkXmlHelper.readInputStreamXml(dis);
		return commandsGroup;
	}

	
	public static RobotCommandsGroup createCommandGroup(byte [] data) {
		
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);
		RobotCommandsGroup commandsGroup = NetworkXmlHelper.readInputStreamXml(dis);
		return commandsGroup;
	}
}
