package com.neatorobotics.android.slide.framework.robot.commands;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;

public class CommandXmlHelper {

	public static final String TAG = CommandXmlHelper.class.getSimpleName();

	private static Document commandsToXml(Node headerNode, Node commands) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(CommandXmlConstants.XML_TAG_PACKET);
			doc.appendChild(rootElement);

			Node importHeaderNode = doc.adoptNode(headerNode);
			rootElement.appendChild(importHeaderNode);

			Node payload = doc.adoptNode(commands);
			rootElement.appendChild(payload);

			return doc;
		} catch (ParserConfigurationException pce) {
			LogHelper.logD(TAG, "Exception in convertToXml",pce);
		} 
		return null;
	}
	

	public static Document commandToXml(Node header, Node commands) {
		return commandsToXml(header, commands);
	}

	private static class XmlDataHandler extends DefaultHandler
	{
		private HashMap<String, String> mElementValueMap = new HashMap<String, String>();
		private RobotCommandsGroup robotCommands;
		private RobotPacket currentRobotCommand;

		private LinkedList<String> mXmlNodes = new LinkedList<String>();
		@Override 
		public void startDocument() throws SAXException { 
			robotCommands = new RobotCommandsGroup();
		} 

		@Override 
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException { 
			mXmlNodes.addFirst(qName);
			if (qName.equals(CommandXmlConstants.XML_TAG_COMMAND)) {
				currentRobotCommand = new RobotPacket();
			}
		} 

		@Override 
		public void endElement(String namespaceURI, String localName, String qName) throws SAXException { 
			mXmlNodes.remove(qName);
			if(qName.equals(CommandXmlConstants.XML_TAG_COMMAND)) {
				if (currentRobotCommand != null) {
					robotCommands.addCommandToGroup(currentRobotCommand);
					currentRobotCommand = null;
				}
			}
		}

		@Override 
		public void characters(char ch[], int start, int length) { 
			String chars = new String(ch, start, length); 
			chars = chars.trim(); 
			String nodeName = mXmlNodes.getFirst();

			if (currentRobotCommand != null) {
				//Details related to a specific command are here.
				if (nodeName.equals(CommandXmlConstants.XML_TAG_COMMANDID)) {
					int commandId = DataConversionUtils.convertStringToInt(chars);
					currentRobotCommand.setCommandId(commandId);
				}
				else {
					currentRobotCommand.getBundle().putString(nodeName, chars);
				}
			}
			mElementValueMap.put(nodeName, chars);
		} 
		@Override
		public void endDocument() {

		}

		public RobotCommandsGroup getRobotCommandList() {
			return robotCommands;
		}
	}
	
	public static RobotCommandsGroup readInputStreamXml(InputStream is) {
		RobotCommandsGroup commandGroup = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			LogHelper.log(TAG, "Exception readInputStreamXml ", e);
		} catch (SAXException e) {
			LogHelper.log(TAG, "Exception readInputStreamXml", e);
		}
		XmlDataHandler handler = new XmlDataHandler();

		try {
			saxParser.parse(is, handler);
			commandGroup = handler.getRobotCommandList();
		} catch (SAXException e) {
			LogHelper.log(TAG, "Exception while Read byte data into coomand", e);
		} catch (IOException e) {
			LogHelper.log(TAG, "Exception while Read byte data into coomand", e);
		}
		return commandGroup;

	}
}
