package com.neatorobotics.android.slide.framework.robot.discovery;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.xmlpull.v1.XmlSerializer;
import android.text.TextUtils;
import android.util.Xml;

public class RobotDiscoveryPacketBuilder {
	
	public String convertRobotCommandsToString(RobotDiscoveryCommandPacket robotDiscoveryPacket)
	{
		RobotDiscoveryPacketHeader header = robotDiscoveryPacket.getHeader();
		
		String xml = null;
		if (robotDiscoveryPacket.isRequest()) {
			RobotDiscoveryRequestPacket command = robotDiscoveryPacket.getRobotDiscoveryCommand();
			xml = convertRequestToXml(header, command);
		}
		else {
			RobotDiscoveryResponsePacket response = robotDiscoveryPacket.getDiscoveryResponse();
			xml = convertResponseToXml(header, response);
		}
		return xml;
	}
	
	public byte[] convertRobotCommandsToBytes(RobotDiscoveryCommandPacket robotDiscoveryPacket)
	{
		RobotDiscoveryPacketHeader header = robotDiscoveryPacket.getHeader();
		String xml = null;
		if (robotDiscoveryPacket.isRequest()) {
			RobotDiscoveryRequestPacket command = robotDiscoveryPacket.getRobotDiscoveryCommand();
			xml = convertRequestToXml(header, command);
		}
		else {
			RobotDiscoveryResponsePacket response = robotDiscoveryPacket.getDiscoveryResponse();
			xml = convertResponseToXml(header, response);
		}
		
		if (xml != null) {
			try {
				return xml.getBytes("UTF-8");
			} 
			catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}
	
	private String convertRequestToXml(RobotDiscoveryPacketHeader header, RobotDiscoveryRequestPacket robotDiscoveryCommand)
	{
		StringWriter sw = new StringWriter();

		try {
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(sw);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", RobotDiscoveryPacketConstants.XML_TAG_ROOT_NODE);
			serializer.flush();
			String headerXml = getCommandHeaderInXml(header);
			sw.write(headerXml);
			
			serializer.startTag("", RobotDiscoveryPacketConstants.XML_TAG_PAYLOAD);
			serializer.flush();
			String commandXml = getCommandItemInXml(robotDiscoveryCommand);
			sw.write(commandXml);
			
			serializer.endTag("", RobotDiscoveryPacketConstants.XML_TAG_PAYLOAD);
			serializer.endTag("", RobotDiscoveryPacketConstants.XML_TAG_ROOT_NODE);
			serializer.endDocument();
			serializer.flush();
		}
		catch (Exception e) {
			
		}
		
		return sw.toString();
	}
	
	private String convertResponseToXml(RobotDiscoveryPacketHeader header, RobotDiscoveryResponsePacket robotDiscoveryResponse)
	{
		StringWriter sw = new StringWriter();

		try {
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(sw);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", RobotDiscoveryPacketConstants.XML_TAG_ROOT_NODE);
			serializer.flush();
			String headerXml = getCommandHeaderInXml(header);
			sw.write(headerXml);
			
			serializer.startTag("", RobotDiscoveryPacketConstants.XML_TAG_PAYLOAD);
			serializer.flush();
			
			String responseInXml = getResponseInXml(robotDiscoveryResponse);
			sw.write(responseInXml);
			
			serializer.endTag("", RobotDiscoveryPacketConstants.XML_TAG_PAYLOAD);
			serializer.endTag("", RobotDiscoveryPacketConstants.XML_TAG_ROOT_NODE);
			serializer.endDocument();
			serializer.flush();
		}
		catch (Exception e) {
			
		}
		
		return sw.toString();
	}
	
	private String getResponseInXml(RobotDiscoveryResponsePacket robotDiscoveryResponse)
	{
		StringWriter sw = new StringWriter();
		try {
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(sw);
			serializer.startTag("", RobotDiscoveryPacketConstants.XML_TAG_RESPONSE);
			addXmlNode(serializer, RobotDiscoveryPacketConstants.XML_TAG_REQUEST_ID, robotDiscoveryResponse.getRequestId());
			addXmlNode(serializer, RobotDiscoveryPacketConstants.XML_TAG_ROBOT_NAME, robotDiscoveryResponse.getName());
			addXmlNode(serializer, RobotDiscoveryPacketConstants.XML_TAG_ROBOT_ID, robotDiscoveryResponse.getRobotId());
			addXmlNode(serializer, RobotDiscoveryPacketConstants.XML_TAG_ROBOT_CHAT_ID, robotDiscoveryResponse.getChatId());
			
			serializer.startTag("", RobotDiscoveryPacketConstants.XML_TAG_PARAMS_DATA);
			
			// command params
			Map<String, String> commandParams = robotDiscoveryResponse.getParams();
			
			for (String key: commandParams.keySet()) {
				String value = commandParams.get(key);
				addXmlNode(serializer, key, value);
			}
			serializer.endTag("", RobotDiscoveryPacketConstants.XML_TAG_PARAMS_DATA);
			serializer.endTag("", RobotDiscoveryPacketConstants.XML_TAG_RESPONSE);
			serializer.flush();
		}
		catch (Exception e) {
		}
		return sw.toString();
	}
	
	
	private String getCommandHeaderInXml(RobotDiscoveryPacketHeader header)
	{
		StringWriter sw = new StringWriter();

		try {
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(sw);
			serializer.startTag("", RobotDiscoveryPacketConstants.XML_TAG_HEADER);
			addXmlNode(serializer, RobotDiscoveryPacketConstants.XML_TAG_HEADER_VERSION, header.getVersion());
			int signature = header.getSignature();
			String signatureStrInHex = String.format("0x%x", signature);
			addXmlNode(serializer, RobotDiscoveryPacketConstants.XML_TAG_HEADER_SIGNATURE, signatureStrInHex);

			serializer.endTag("", RobotDiscoveryPacketConstants.XML_TAG_HEADER);
			serializer.flush();
		}
		catch (Exception e) {
			
		}
		
		return sw.toString();
	}
	
	private String getCommandItemInXml(RobotDiscoveryRequestPacket robotDiscoveryCommand)
	{
		StringWriter sw = new StringWriter();
		try {
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(sw);
			serializer.startTag("", RobotDiscoveryPacketConstants.XML_TAG_COMMAND);
			addXmlNode(serializer, RobotDiscoveryPacketConstants.XML_TAG_COMMAND_ID, robotDiscoveryCommand.getCommand());
			addXmlNode(serializer, RobotDiscoveryPacketConstants.XML_TAG_REQUEST_ID, robotDiscoveryCommand.getRequestId());
			addXmlNode(serializer, RobotDiscoveryPacketConstants.XML_TAG_USER_ID, robotDiscoveryCommand.getUserId());
			addXmlNode(serializer, RobotDiscoveryPacketConstants.XML_TAG_ROBOT_ID, robotDiscoveryCommand.getRobotId());
			
			
			serializer.startTag("", RobotDiscoveryPacketConstants.XML_TAG_PARAMS_DATA);
			
			// command params
			Map<String, String> commandParams = robotDiscoveryCommand.getCommandParams();
			
			for (String key: commandParams.keySet()) {
				String value = commandParams.get(key);
				addXmlNode(serializer, key, value);
			}
			serializer.endTag("", RobotDiscoveryPacketConstants.XML_TAG_PARAMS_DATA);
			serializer.endTag("", RobotDiscoveryPacketConstants.XML_TAG_COMMAND);
			serializer.flush();
		}
		catch (Exception e) {
		}
		return sw.toString();
	}
	
	private void addXmlNode(XmlSerializer serializer, String nodeName, String value) throws IllegalArgumentException, IllegalStateException, IOException
	{
		if (!TextUtils.isEmpty(value)) {
			serializer.startTag("", nodeName);
			serializer.text(value);
			serializer.endTag("", nodeName);
		}
	}
	
	private void addXmlNode(XmlSerializer serializer, String nodeName, int value) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag("", nodeName);
		serializer.text(String.valueOf(value));
		serializer.endTag("", nodeName);
	}

}
