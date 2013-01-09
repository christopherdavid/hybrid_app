package com.neatorobotics.android.slide.framework.robot.commands;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;

public class RobotPacket {
		
	@SuppressWarnings("unused")
	private  final String TAG = getClass().getSimpleName();
	private int mCommandId;
	protected RobotPacketBundle mRobotPacketBundle;
	
	public RobotPacket() {
		mCommandId = 0; // Default value
		mRobotPacketBundle = new RobotPacketBundle();
	}
	
	public RobotPacket(int commandId)
	{
		mCommandId = commandId;
		mRobotPacketBundle = new RobotPacketBundle();
	}
	
	public RobotPacket(int commandId, RobotPacketBundle robotPacketBundle)
	{
		mCommandId = commandId;
		mRobotPacketBundle = new RobotPacketBundle(robotPacketBundle);
	}
	
	public int getCommandId() {
		return mCommandId;
	}

	public void setCommandId(int commandId) {
		this.mCommandId = commandId;
	}

	public int getPacketLength() {
		return 0;
	}

	
	public RobotPacketBundle getBundle()
	{
		return mRobotPacketBundle;
	}

	//Document where it is added and the parent element of the command xml.
	public Node robotCommandToXmlNode() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

		}
		Document doc = docBuilder.newDocument();
		Node command = doc.createElement(CommandXmlConstants.XML_TAG_COMMAND);
		Node commadid = doc.createElement(CommandXmlConstants.XML_TAG_COMMANDID);
		commadid.appendChild(doc.createTextNode(DataConversionUtils.convertIntToString(getCommandId())));
		
		Node commandDataNode = getBundle().bundleToXml();
		Node impCommandData = doc.adoptNode(commandDataNode);
		
		
		command.appendChild(commadid);
		command.appendChild(impCommandData);
		return command;
	}
}
