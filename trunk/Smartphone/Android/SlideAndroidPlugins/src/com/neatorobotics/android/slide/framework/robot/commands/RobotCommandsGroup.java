package com.neatorobotics.android.slide.framework.robot.commands;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;



public class RobotCommandsGroup {
	private List<RobotPacket> mCommands;

	public RobotCommandsGroup() {
		mCommands = new ArrayList<RobotPacket>();
	}

	public void addCommandToGroup(RobotPacket robotPacket) {
		mCommands.add(robotPacket);
	}

	public int size() {
		return mCommands.size();
	}

	public RobotPacket getRobotPacket(int index) {
		RobotPacket robotPacket = mCommands.get(index);
		return robotPacket;
	}

	public Node robotCommandGroupToXml() {

		int groupSize = size();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

		}
		Document doc = docBuilder.newDocument();
		Node payload = doc.createElement(CommandXmlConstants.XML_TAG_PAYLOAD);
		for (int robotPacketIterator =0; robotPacketIterator< groupSize; robotPacketIterator++){
			Node xmlNode = getRobotPacket(robotPacketIterator).robotCommandToXmlNode();
			Node robotCommandNode = doc.adoptNode(xmlNode);
			payload.appendChild(robotCommandNode);
		}
		return payload;
	}

}
