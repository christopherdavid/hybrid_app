package com.neatorobotics.android.slide.framework.robot.commands;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
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

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.CommandXmlConstants;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;
import com.neatorobotics.android.slide.framework.xml.TerminateSaxException;


public class CommandPacketValidator {

	private static final String TAG = CommandPacketValidator.class.getSimpleName();

	public static Node getHeaderXml() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;

		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

		}

		Document doc = docBuilder.newDocument();
		Node header = doc.createElement(CommandXmlConstants.XML_TAG_HEADER);
		Node version = doc.createElement(CommandXmlConstants.XML_TAG_VERSION);

		String transportVersion = DataConversionUtils.convertIntToString(AppConstants.COMMAND_PACKET_VERSION);
		version.appendChild(doc.createTextNode(transportVersion));
		header.appendChild(version);
		Element signature = doc.createElement(CommandXmlConstants.XML_TAG_SIGNATURE);
		String appSignature = DataConversionUtils.convertHexToString(AppConstants.APP_SIGNATURE);
		signature.appendChild(doc.createTextNode(appSignature));
		header.appendChild(signature);
		return header;
	}
	
	private static class PacketHeaderDataHandler extends DefaultHandler
	{
		private LinkedList<String> mXmlNodes = new LinkedList<String>();
		private int mVersion;
		private int mSignature;
		private boolean terminate = false;
		@Override 
		public void startDocument() throws SAXException { 

		} 

		@Override 
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException { 
			mXmlNodes.addFirst(qName);
			if (terminate) {
				throw  (new TerminateSaxException());
			}
		} 

		@Override 
		public void endElement(String namespaceURI, String localName, String qName) throws SAXException { 
			mXmlNodes.remove(qName);
			if(qName.equals(CommandXmlConstants.XML_TAG_HEADER)) {
				terminate = true;
			}
		}

		@Override 
		public void characters(char ch[], int start, int length) { 
			String chars = new String(ch, start, length); 
			chars = chars.trim(); 
			String nodeName = mXmlNodes.getFirst();
			if (nodeName.equals(CommandXmlConstants.XML_TAG_VERSION)) {
				mVersion = DataConversionUtils.convertStringToInt(chars);	
			}
			else if (nodeName.equals(CommandXmlConstants.XML_TAG_SIGNATURE)) {
				mSignature = DataConversionUtils.convertStringToInt(chars);
			}
		} 
		
		@Override
		public void endDocument() {
			
		}

		public boolean isHeaderValid() {
			LogHelper.log(TAG, "Version:" + mVersion + " Signature:" + mSignature);
			boolean versionMatch = (mVersion == AppConstants.COMMAND_PACKET_VERSION);
			boolean signatureMatch = (mSignature == AppConstants.APP_SIGNATURE);
			return (versionMatch && signatureMatch);
		}
	}
	
	
	public static boolean validateHeaderAndSignature(byte[] data) {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);
		boolean headerAuthenticated = false;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			LogHelper.log(TAG, "Exception readInputStreamXml ", e);
		} catch (SAXException e) {
			LogHelper.log(TAG, "Exception readInputStreamXml", e);
		}
		PacketHeaderDataHandler handler = new PacketHeaderDataHandler();
		try {
			saxParser.parse(dis, handler);
			headerAuthenticated = handler.isHeaderValid();	
		}
		//Used to terminate the SAX parsing deliberately.
		catch(TerminateSaxException e) {
			headerAuthenticated = handler.isHeaderValid();	
		} catch (SAXException e) {
			LogHelper.log(TAG, "Exception while Read byte data into coomand", e);
		} catch (IOException e) {
			LogHelper.log(TAG, "Exception while Read byte data into coomand", e);
		}
		return headerAuthenticated;
	}	
}