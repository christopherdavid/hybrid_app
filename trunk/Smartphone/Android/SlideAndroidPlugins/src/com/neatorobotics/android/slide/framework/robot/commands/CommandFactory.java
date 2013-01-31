package com.neatorobotics.android.slide.framework.robot.commands;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.neatorobotics.android.slide.framework.xml.XmlHelper;

public class CommandFactory {
	
	@SuppressWarnings("unused")
	private static final String TAG = CommandFactory.class.getSimpleName();
	

	public static RobotCommandsGroup createCommandGroup(DataInputStream dis) {
		
		RobotCommandsGroup commandsGroup = CommandXmlHelper.readInputStreamXml(dis);
		return commandsGroup;
	}
	
	public static RobotCommandsGroup createCommandGroup(byte [] data) {
		
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);
		RobotCommandsGroup commandsGroup = CommandXmlHelper.readInputStreamXml(dis);
		return commandsGroup;
	}
	public static byte[] getPacketData(Node header, Node command) {
		Document packet = CommandXmlHelper.commandToXml(header, command);
		return XmlHelper.documentToBytes(packet);
	}
	public static String getPacketXml(Node header, Node command) {
		Document packet = CommandXmlHelper.commandToXml(header, command);
		return XmlHelper.documentToString(packet);
	}
}
