package com.neatorobotics.android.slide.framework.robot.commands.request;

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

public class RobotCommandParser {

    private static final String TAG = RobotCommandParser.class.getSimpleName();

    public RobotCommandPacket convertStringToRobotCommands(String robotPacket) {
        RobotCommandHandler handler = new RobotCommandHandler();
        try {
            Xml.parse(robotPacket, handler);
            RobotCommandPacket robotCommandPacket = handler.getRobotPacket();
            return robotCommandPacket;
        } catch (SAXException e) {
            Log.i(TAG, "EXCEPTION in parsing", e);
        } catch (Exception e) {
            Log.e(TAG, "EXCEPTION in convertStringToRobotCommands", e);
        }

        return null;
    }

    public RobotCommandPacket convertBytesToRobotCommands(byte[] robotPacket) {
        try {
            String robotPacketInStr = new String(robotPacket, "UTF-8");
            return convertStringToRobotCommands(robotPacketInStr);
        } catch (UnsupportedEncodingException e) {

        }
        return null;
    }

    private static class RobotCommandHandler extends DefaultHandler {

        private RobotCommandPacketHeader header;
        private RobotRequests commands;
        private ResponsePacket response;
        private HeaderParser headerParser;
        private CommandParser commandParser;
        private ResponseParser responseParser;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            // Log.i(TAG, "RobotCommandHandler - Local Name = " + localName);

            if (localName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_HEADER)) {
                headerParser = new HeaderParser();
            } else if (localName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_COMMAND)) {
                commands = new RobotRequests();
                commandParser = new CommandParser();
            } else if (localName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_RESPONSE)) {
                responseParser = new ResponseParser();
            }

            if (headerParser != null) {
                headerParser.startElement(uri, localName, qName, attributes);
            } else if (commandParser != null) {
                commandParser.startElement(uri, localName, qName, attributes);
            } else if (responseParser != null) {
                responseParser.startElement(uri, localName, qName, attributes);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (headerParser != null) {
                headerParser.characters(ch, start, length);
            } else if (commandParser != null) {
                commandParser.characters(ch, start, length);
            } else if (responseParser != null) {
                responseParser.characters(ch, start, length);
            }

        }

        @Override
        public void endDocument() throws SAXException {
            // Log.i(TAG, "Header = " + header);
            // Log.i(TAG, "Commands = " + commands);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            // Log.i(TAG, "RobotCommandHandler - endElement Local Name = " +
            // localName);
            // Log.i(TAG, "RobotCommandHandler - endElement qName = " + qName);

            if (headerParser != null) {
                headerParser.endElement(uri, localName, qName);
            } else if (commandParser != null) {
                commandParser.endElement(uri, localName, qName);
            } else if (responseParser != null) {
                responseParser.endElement(uri, localName, qName);
            }
            if (localName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_HEADER)) {
                header = headerParser.getRobotCommandHeader();
                headerParser = null;
            } else if (localName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_COMMAND)) {
                commands.addCommand(commandParser.getRobotCommand());
                commandParser = null;
            } else if (localName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_RESPONSE)) {
                ResponsePacket commandResponse = responseParser.getResponse();
                response = commandResponse;
                responseParser = null;
            }
        }

        @Override
        public void startDocument() throws SAXException {
        }

        public RobotCommandPacket getRobotPacket() {
            RobotCommandPacket packet = null;
            if (commands != null) {
                packet = RobotCommandPacket.createRobotCommandPacket(header, commands);
            } else if (response != null) {
                packet = RobotCommandPacket.createRobotPacketWithResponseData(header, response);
            }

            return packet;
        }

    }

    private static class HeaderParser {

        private HashMap<String, String> headerValues = new HashMap<String, String>();
        private String key;
        private String value;

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            // Log.i(TAG, "HeaderParser - Local Name = " + localName);
            key = localName;
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            value = new String(ch, start, length);
            // Log.i(TAG, "HeaderParser - characters = " + value);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {

            headerValues.put(convertToLowerCase(key), value);
            // Log.i(TAG, "HeaderParser - endElement Local Name = " +
            // localName);
        }

        public RobotCommandPacketHeader getRobotCommandHeader() {
            String version = headerValues.get(convertToLowerCase(RobotPacketConstants.XML_TAG_HEADER_VERSION));
            String signature = headerValues.get(convertToLowerCase(RobotPacketConstants.XML_TAG_HEADER_SIGNATURE));

            // Log.i(TAG, "HeaderParser - version = " + version);
            // Log.i(TAG, "HeaderParser - signature = " + signature);
            RobotCommandPacketHeader header = new RobotCommandPacketHeader();
            int signatureInNumber = 0;
            if (signature.startsWith("0x")) {
                String signatureWithoutHexPrefix = removeHexPrefix(signature);
                signatureInNumber = (int) Long.parseLong(signatureWithoutHexPrefix, 16);
            } else {
                signatureInNumber = Integer.parseInt(signature);
            }
            header.setSignature(signatureInNumber);
            header.setVersion(Integer.parseInt(version));
            return header;
        }

    }

    private static String removeHexPrefix(String numberInHex) {
        String numberWithoutHexPrefix = numberInHex.substring("0x".length());
        // Log.i(TAG, "numberWithoutHexPrefix = " + numberWithoutHexPrefix);
        return numberWithoutHexPrefix;

    }

    private static class CommandParser {
        private int commandId;
        private String requestId;
        private String timeStamp;
        private boolean replyRequired;
        private int retryCount;
        private int distributionMode;
        private String replyTo;

        private HashMap<String, String> commandParams = new HashMap<String, String>();
        private boolean isCommandParam = false;
        private String key;
        private String value;
        private String nodeName;

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            if (isCommandParam) {
                key = localName;
            }

            nodeName = localName;

            if (localName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_PARAMS_DATA)) {
                isCommandParam = true;
            }

        }

        public void characters(char[] ch, int start, int length) throws SAXException {

            if (isCommandParam) {
                value = new String(ch, start, length);
                return;
            }

            if (nodeName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_COMMAND_ID)) {
                String s = new String(ch, start, length);
                if (s.length() > 0) {
                    commandId = Integer.parseInt(s);
                }
            } else if (nodeName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_COMMAND_TIMESTAMP)) {
                String s = new String(ch, start, length);
                timeStamp = s;
            } else if (nodeName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_REQUEST_ID)) {
                String s = new String(ch, start, length);
                requestId = s;

            } else if (nodeName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_DISTRIBUTION_MODE)) {
                String s = new String(ch, start, length);
                if (s.length() > 0) {
                    distributionMode = Integer.parseInt(s);
                }
            } else if (nodeName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_RETRY_COUNT)) {
                String s = new String(ch, start, length);
                if (s.length() > 0) {
                    retryCount = Integer.parseInt(s);
                }

            } else if (nodeName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_REPLY_REQUIRED)) {
                String s = new String(ch, start, length);
                if (s.length() > 0) {
                    replyRequired = s.equalsIgnoreCase("true") ? true : false;
                }
            } else if (nodeName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_REPLY_TO)) {
                String s = new String(ch, start, length);
                replyTo = s;
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {

            if (localName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_PARAMS_DATA)) {
                isCommandParam = false;
            }

            if (isCommandParam) {
                commandParams.put(key, value);
            }
        }

        public RequestPacket getRobotCommand() {
            RequestPacket robotCommand = RequestPacket.createRobotCommandWithParams(commandId, commandParams);
            robotCommand.setDistributionMode(distributionMode);
            robotCommand.setReplyToAddress(replyTo);
            robotCommand.setRequestId(requestId);
            robotCommand.setResponseNeeded(replyRequired);
            robotCommand.setRetryCount(retryCount);
            robotCommand.setTimestamp(timeStamp);

            return robotCommand;
        }

    }

    private static class ResponseParser {
        private String requestId;
        private int status;
        private HashMap<String, String> responseParams = new HashMap<String, String>();
        private boolean isResponseParam = false;
        private String key;
        private String value;

        private String node;

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            if (isResponseParam) {
                key = localName;
            }

            if (localName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_PARAMS_DATA)) {
                isResponseParam = true;
            }

            node = localName;

        }

        public void characters(char[] ch, int start, int length) throws SAXException {

            if (isResponseParam) {
                value = new String(ch, start, length);
                return;
            }

            if (node.equalsIgnoreCase(RobotPacketConstants.XML_TAG_STATUS)) {
                String s = new String(ch, start, length);
                if (s.length() > 0) {
                    status = Integer.parseInt(s);
                }
            } else if (node.equalsIgnoreCase(RobotPacketConstants.XML_TAG_REQUEST_ID)) {
                String s = new String(ch, start, length);
                if (s.length() > 0) {
                    requestId = s;
                }
            }

        }

        public void endElement(String uri, String localName, String qName) throws SAXException {

            if (localName.equalsIgnoreCase(RobotPacketConstants.XML_TAG_PARAMS_DATA)) {
                isResponseParam = false;
            }

            if (isResponseParam) {
                responseParams.put(key, value);
            }
        }

        public ResponsePacket getResponse() {
            ResponsePacket response = ResponsePacket.createResponseWithParams(requestId, status, responseParams);

            return response;
        }

    }

    private static String convertToLowerCase(String s) {
        String lowerCaseStr = s.toLowerCase(Locale.getDefault());

        return lowerCaseStr;
    }

}
