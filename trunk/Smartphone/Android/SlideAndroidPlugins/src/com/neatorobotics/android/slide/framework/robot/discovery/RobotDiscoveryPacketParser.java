package com.neatorobotics.android.slide.framework.robot.discovery;


/*
 * Sample Request XML 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <packet>
 * 		<header>
 *  		<version>1</version>
 *   		<signature>-889275714</signature>
 *    	</header>
 *     	<payload>
 *      	<request>
 *       		<command>101</command>
 *        		<requestId>ff345</requestId>
 *         		<timeStamp></timeStamp>
 *          	<retryCount>0</retryCount>
 *           	<responseNeeded>true</responseNeeded>
 *            	<replyTo>userId</replyTo>
 *             	<distributionMode>0</distributionMode> <!--  0 for XMPP and 1 for TCP/IP -->
 *              <params>
 *              	<param1>value1</param1>
 *              </params>
 *           </request>
 *       </payload>
 * </packet>
 * 
 * 
 * Sample response XML
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <packet>
 *  		<header>
 *   			<version>1</version>
 *    			<signature>-889275714</signature>
 *     		</header>
 *     		<payload>
 *      			<response>
 *       				<requestId>ff345</requestId>
 *        				<status>0</status>
 *         				<params>
 *          				<param1>value1</param1>
 *          			</params>
 *          		</response>
 *          </payload>
 * </packet>
 */


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;
import android.util.Xml;

public class RobotDiscoveryPacketParser {
	
	private static final String TAG = RobotDiscoveryPacketParser.class.getSimpleName();
	
	
	
	public RobotDiscoveryCommandPacket convertStringToRobotDiscoveryCommands(String robotPacket)
	{
		RobotCommandHandler handler = new RobotCommandHandler();
		try {
			Xml.parse(robotPacket, handler);
			RobotDiscoveryCommandPacket robotCommandPacket = handler.getRobotPacket();
			return robotCommandPacket;
		} 
		catch (SAXException e) {
			Log.i(TAG, "EXCEPTION in parsing", e);
		}
		catch (Exception e) {
			Log.e(TAG, "EXCEPTION in convertStringToRobotDiscoveryCommands", e);
		}
		return null;
	}
	

	
	public RobotDiscoveryCommandPacket convertBytesToRobotDiscoveryCommands(byte[] robotPacket)
	{
		try {
			String robotPacketInStr = new String(robotPacket, "UTF-8");
			return convertStringToRobotDiscoveryCommands(robotPacketInStr);
		} 
		catch (UnsupportedEncodingException e) {
			
		}
		return null;
	}
	
	
	
	
	private static class RobotCommandHandler extends DefaultHandler
	{
		
		private RobotDiscoveryPacketHeader header;
		private RobotDiscoveryRequestPacket command;
		private RobotDiscoveryResponsePacket response;
		private HeaderParser headerParser;
		private CommandParser commandParser;
		private ResponseParser responseParser;
		
		@Override
		public void startElement(String uri, String localName, String qName, 
				Attributes attributes) throws SAXException {
			Log.i(TAG, "RobotCommandHandler - Local Name = " + localName);
			
			if (localName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_HEADER)) {
				headerParser = new HeaderParser();
			}
			else if (localName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_COMMAND)) {
				commandParser = new CommandParser();
			}
			else if (localName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_RESPONSE)) {
				responseParser = new ResponseParser();
			}
			
			if (headerParser != null) {
				headerParser.startElement(uri, localName, qName, attributes);
			}
			else if (commandParser != null) {
				commandParser.startElement(uri, localName, qName, attributes);
			}
			else if (responseParser != null) {
				responseParser.startElement(uri, localName, qName, attributes);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (headerParser != null) {
				headerParser.characters(ch, start, length);
			}
			else if (commandParser != null) {
				commandParser.characters(ch, start, length);
			}
			else if (responseParser != null) {
				responseParser.characters(ch, start, length);
			}

		}

		@Override
		public void endDocument() throws SAXException {
			Log.i(TAG, "Header = " + header);
			Log.i(TAG, "Commands = " + command);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			Log.i(TAG, "RobotCommandHandler - endElement Local Name = " + localName);
			Log.i(TAG, "RobotCommandHandler - endElement qName = " + qName);
			
			if (headerParser != null) {
				headerParser.endElement(uri, localName, qName);
			}
			else if (commandParser != null) {
				commandParser.endElement(uri, localName, qName);
			}
			else if (responseParser != null) {
				responseParser.endElement(uri, localName, qName);
			}
			if (localName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_HEADER)) {
				header = headerParser.getRobotCommandHeader();
				headerParser = null;
			}
			else if (localName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_COMMAND)) {
				command = commandParser.getRobotCommand();
				commandParser = null;
			}
			else if (localName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_RESPONSE)) {
				RobotDiscoveryResponsePacket commandResponse = responseParser.getResponse();
				response = commandResponse;
				responseParser = null;
			}
		}

		@Override
		public void startDocument() throws SAXException {
		}
		
		
		public RobotDiscoveryCommandPacket getRobotPacket()
		{
			RobotDiscoveryCommandPacket packet = null;
			if (command != null) {
				packet = RobotDiscoveryCommandPacket.createRobotCommandPacket(header, command);
			}
			else if (response != null) {
				packet = RobotDiscoveryCommandPacket.createRobotPacketWithResponseData(header, response);
			}
			
			return packet;
		}

		
	}
	
	private static class HeaderParser 
	{
		
		private HashMap<String, String> headerValues = new HashMap<String, String>();
		private String key;
		private String value;
		
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			Log.i(TAG, "HeaderParser - Local Name = " + localName);
			key = localName;
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			value = new String(ch, start, length);
			Log.i(TAG, "HeaderParser - characters = " + value);
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			
			headerValues.put(convertToLowerCase(key), value);
			Log.i(TAG, "HeaderParser - endElement Local Name = " + localName);
		}
		
		public RobotDiscoveryPacketHeader getRobotCommandHeader()
		{
			String version = headerValues.get(convertToLowerCase(RobotDiscoveryPacketConstants.XML_TAG_HEADER_VERSION));
			String signature = headerValues.get(convertToLowerCase(RobotDiscoveryPacketConstants.XML_TAG_HEADER_SIGNATURE));
			
			Log.i(TAG, "HeaderParser - version = " + version);
			Log.i(TAG, "HeaderParser - signature = " + signature);
			RobotDiscoveryPacketHeader header = new RobotDiscoveryPacketHeader();
			int signatureInNumber = 0;
			if (signature.startsWith("0x")) {
				String signatureWithoutHexPrefix = removeHexPrefix(signature);
				signatureInNumber = (int)Long.parseLong(signatureWithoutHexPrefix, 16);
			}
			else {
				signatureInNumber = Integer.parseInt(signature);
			}
			header.setSignature(signatureInNumber);
			header.setVersion(Integer.parseInt(version));
			return header;
		}

	}
	
	private static String removeHexPrefix(String numberInHex)
	{
		String numberWithoutHexPrefix = numberInHex.substring("0x".length());
		Log.i(TAG, "numberWithoutHexPrefix = " + numberWithoutHexPrefix);
		return numberWithoutHexPrefix;
				
	}
	
	private static class CommandParser 
	{
		private int commandId;
		private String requestId;
		private String userId;
		private String robotId;
		
		private HashMap<String, String> commandParams = new HashMap<String, String>();
		private boolean isCommandParam = false;
		private String key;
		private String value;
		private String nodeName;
		
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			
			if (isCommandParam) {
				key = localName;
			}
			
			nodeName = localName;
			
			if (localName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_PARAMS_DATA)) {
				isCommandParam = true;
			}
			
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			
			if (isCommandParam) {
				value = new String(ch, start, length);
				return;
			}
			
			if (nodeName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_COMMAND_ID)) {
				String s = new String(ch, start, length);
				if (s.length() > 0) {
					commandId = Integer.parseInt(s);
				}
			}
			
			else if (nodeName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_USER_ID)) {
				String s = new String(ch, start, length);
				userId = s;
			}
			else if (nodeName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_REQUEST_ID)) {
				String s = new String(ch, start, length);
				requestId = s;
				
			}
			else if (nodeName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_ROBOT_ID)) {
				String s = new String(ch, start, length);
				robotId = s;
			}
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			
			if (localName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_PARAMS_DATA)) {
				isCommandParam = false;
			}
		
			if (isCommandParam) {
				commandParams.put(key, value);
			}
		}
		
		public RobotDiscoveryRequestPacket getRobotCommand()
		{
			RobotDiscoveryRequestPacket robotCommand = RobotDiscoveryRequestPacket.createRobotCommandWithParams(commandId, userId, commandParams);
			robotCommand.setRequestId(requestId);
			robotCommand.setRobotId(robotId);
			
			return robotCommand;
		}


	}
	
	private static class ResponseParser 
	{
		private String requestId;
		private String robotId;
		private String name;
		private String chatId;
		private HashMap<String, String> responseParams = new HashMap<String, String>();
		private boolean isResponseParam = false;
		private String key;
		private String value;
		
		private String node;
		
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			
			if (isResponseParam) {
				key = localName;
			}
			
			if (localName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_PARAMS_DATA)) {
				isResponseParam = true;
			}
			
			node = localName;
			
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			
			if (isResponseParam) {
				value = new String(ch, start, length);
				return;
			}
			
			if (node.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_ROBOT_NAME)) {
				String s = new String(ch, start, length);
				name = s;
			}
			else if (node.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_REQUEST_ID)) {
				String s = new String(ch, start, length);
				requestId = s;
			}
			else if (node.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_ROBOT_ID)) {
				String s = new String(ch, start, length);
				robotId = s;
			}
			else if (node.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_ROBOT_CHAT_ID)) {
				String s = new String(ch, start, length);
				chatId = s;
			}
			
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			
			if (localName.equalsIgnoreCase(RobotDiscoveryPacketConstants.XML_TAG_PARAMS_DATA)) {
				isResponseParam = false;
			}
		
			if (isResponseParam) {
				responseParams.put(key, value);
			}
		}
		
		public RobotDiscoveryResponsePacket getResponse()
		{
			RobotDiscoveryResponsePacket response = RobotDiscoveryResponsePacket.createResponseWithParams(requestId, responseParams);
			response.setChatId(chatId);
			response.setName(name);
			response.setRobotId(robotId);
			
			return response;
		}

	}
	
	private static String convertToLowerCase(String s)
	{
		String lowerCaseStr = s.toLowerCase(Locale.getDefault());
		
		return lowerCaseStr;
	}

}
