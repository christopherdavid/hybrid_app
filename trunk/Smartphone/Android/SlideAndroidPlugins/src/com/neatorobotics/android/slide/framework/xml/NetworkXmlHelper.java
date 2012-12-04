package com.neatorobotics.android.slide.framework.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandsGroup;
import com.neatorobotics.android.slide.framework.robot.commands.RobotPacket;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;

// This class will be used as a helper class to convert commands to XML and vice-versa.

// TODO: This class needs work. Redesign it in such a way that all other entities like web services can also use the XML
// properties. Right now they are using Xml Helper.
public class NetworkXmlHelper {
	
	public static final String TAG = NetworkXmlHelper.class.getSimpleName();
	
	private static byte[] commandsToXml(RobotCommandsGroup commandsGroup) {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(NetworkXmlConstants.XML_TAG_PACKET);
			doc.appendChild(rootElement);

			Node headerNode = getHeaderXml();
			Node importHeaderNode = doc.adoptNode(headerNode);
			rootElement.appendChild(importHeaderNode);

			Node xmlNode = commandsGroup.robotCommandGroupToXml();
			Node payload = doc.adoptNode(xmlNode);
			rootElement.appendChild(payload);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformerfile = transformerFactory.newTransformer();
			DOMSource sourcefile = new DOMSource(doc);
			StringWriter write = new StringWriter();
			StreamResult resultfile = new StreamResult(write);
			transformerfile.transform(sourcefile, resultfile);
			LogHelper.log(TAG, write.toString());
			
			 
			Source source = new DOMSource(doc);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Result result = new StreamResult(out);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			//Check this again.
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(source, result);
			return out.toByteArray();

		} catch (ParserConfigurationException pce) {
			LogHelper.logD(TAG, "Exception in convertToXml",pce);
		} catch (TransformerException tfe) {
			LogHelper.logD(TAG, "Exception in convertToXml",tfe);
		}
		return null;


	}

	public static byte[] commandToXml(RobotPacket robotPacket) {
		RobotCommandsGroup commandsGroup = new RobotCommandsGroup();
		commandsGroup.addCommandToGroup(robotPacket);
		return commandsToXml(commandsGroup);
	}


	private static Node getHeaderXml() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

		}

		Document doc = docBuilder.newDocument();
		Node header = doc.createElement(NetworkXmlConstants.XML_TAG_HEADER);
		Node version = doc.createElement(NetworkXmlConstants.XML_TAG_VERSION);
		// TODO: Form header correctly.
		String transportVersion = DataConversionUtils.convertIntToString(AppConstants.TCP_PACKET_VERSION);
		version.appendChild(doc.createTextNode(transportVersion));
		header.appendChild(version);
		Element signature = doc.createElement(NetworkXmlConstants.XML_TAG_SIGNATURE);
		String appSignature = DataConversionUtils.convertIntToString(AppConstants.APP_SIGNATURE);
		signature.appendChild(doc.createTextNode(appSignature));
		header.appendChild(signature);

		return header;

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
			if (qName.equals(NetworkXmlConstants.XML_TAG_COMMAND)) {
				currentRobotCommand = new RobotPacket();
			}

		} 


		@Override 
		public void endElement(String namespaceURI, String localName, String qName) throws SAXException { 
			mXmlNodes.remove(qName);
			if(qName.equals(NetworkXmlConstants.XML_TAG_COMMAND)) {
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
				if (nodeName.equals(NetworkXmlConstants.XML_TAG_COMMANDID)) {
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
