package com.neatorobotics.android.slide.framework.robot.commands.request;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.xmlpull.v1.XmlSerializer;

import android.text.TextUtils;
import android.util.Xml;

public class RobotCommandBuilder {

    public String convertRobotCommandsToString(RobotCommandPacket robotPacket) {
        RobotCommandPacketHeader header = robotPacket.getHeader();

        String xml = null;
        if (robotPacket.isRequest()) {
            RobotRequests commands = robotPacket.getRobotCommands();
            xml = convertRequestToXml(header, commands);
        } else {
            ResponsePacket response = robotPacket.getCommandResponse();
            xml = convertResponseToXml(header, response);
        }
        return xml;
    }

    public byte[] convertRobotCommandsToBytes(RobotCommandPacket robotPacket) {
        RobotCommandPacketHeader header = robotPacket.getHeader();
        RobotRequests commands = robotPacket.getRobotCommands();

        String xml = convertRequestToXml(header, commands);

        if (xml != null) {
            try {
                return xml.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
        }
        return null;
    }

    private String convertRequestToXml(RobotCommandPacketHeader header, RobotRequests commands) {
        StringWriter sw = new StringWriter();

        try {
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(sw);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", RobotPacketConstants.XML_TAG_ROOT_NODE);
            serializer.flush();
            String headerXml = getCommandHeaderInXml(header);
            sw.write(headerXml);

            serializer.startTag("", RobotPacketConstants.XML_TAG_PAYLOAD);
            serializer.flush();

            int size = commands.getNumberOfCommands();
            for (int i = 0; i < size; i++) {
                RequestPacket command = commands.getCommand(i);
                String commandXml = getCommandItemInXml(command);
                sw.write(commandXml);
            }

            serializer.endTag("", RobotPacketConstants.XML_TAG_PAYLOAD);
            serializer.endTag("", RobotPacketConstants.XML_TAG_ROOT_NODE);
            serializer.endDocument();
            serializer.flush();
        } catch (Exception e) {

        }

        return sw.toString();
    }

    private String convertResponseToXml(RobotCommandPacketHeader header, ResponsePacket response) {
        StringWriter sw = new StringWriter();

        try {
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(sw);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", RobotPacketConstants.XML_TAG_ROOT_NODE);
            serializer.flush();
            String headerXml = getCommandHeaderInXml(header);
            sw.write(headerXml);

            serializer.startTag("", RobotPacketConstants.XML_TAG_PAYLOAD);
            serializer.flush();

            String responseInXml = getResponseInXml(response);
            sw.write(responseInXml);

            serializer.endTag("", RobotPacketConstants.XML_TAG_PAYLOAD);
            serializer.endTag("", RobotPacketConstants.XML_TAG_ROOT_NODE);
            serializer.endDocument();
            serializer.flush();
        } catch (Exception e) {

        }

        return sw.toString();
    }

    private String getResponseInXml(ResponsePacket response) {
        StringWriter sw = new StringWriter();
        try {
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(sw);
            serializer.startTag("", RobotPacketConstants.XML_TAG_RESPONSE);
            addXmlNode(serializer, RobotPacketConstants.XML_TAG_STATUS, response.getStatus());

            serializer.startTag("", RobotPacketConstants.XML_TAG_PARAMS_DATA);

            // command params
            Map<String, String> commandParams = response.getParams();

            for (String key : commandParams.keySet()) {
                String value = commandParams.get(key);
                addXmlNode(serializer, key, value);
            }
            serializer.endTag("", RobotPacketConstants.XML_TAG_PARAMS_DATA);
            serializer.endTag("", RobotPacketConstants.XML_TAG_RESPONSE);
            serializer.flush();
        } catch (Exception e) {
        }
        return sw.toString();
    }

    private String getCommandHeaderInXml(RobotCommandPacketHeader header) {
        StringWriter sw = new StringWriter();

        try {
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(sw);
            serializer.startTag("", RobotPacketConstants.XML_TAG_HEADER);
            addXmlNode(serializer, RobotPacketConstants.XML_TAG_HEADER_VERSION, header.getVersion());
            int signature = header.getSignature();
            String signatureStrInHex = String.format("0x%x", signature);
            addXmlNode(serializer, RobotPacketConstants.XML_TAG_HEADER_SIGNATURE, signatureStrInHex);

            serializer.endTag("", RobotPacketConstants.XML_TAG_HEADER);
            serializer.flush();
        } catch (Exception e) {

        }

        return sw.toString();
    }

    private String getCommandItemInXml(RequestPacket robotCommand) {
        StringWriter sw = new StringWriter();
        try {
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(sw);
            serializer.startTag("", RobotPacketConstants.XML_TAG_COMMAND);
            addXmlNode(serializer, RobotPacketConstants.XML_TAG_COMMAND_ID, robotCommand.getCommand());
            addXmlNode(serializer, RobotPacketConstants.XML_TAG_COMMAND_TIMESTAMP, robotCommand.getTimestamp());

            serializer.startTag("", RobotPacketConstants.XML_TAG_PARAMS_DATA);

            // command params
            Map<String, String> commandParams = robotCommand.getCommandParams();

            for (String key : commandParams.keySet()) {
                String value = commandParams.get(key);
                addXmlNode(serializer, key, value);
            }
            serializer.endTag("", RobotPacketConstants.XML_TAG_PARAMS_DATA);
            serializer.endTag("", RobotPacketConstants.XML_TAG_COMMAND);
            serializer.flush();
        } catch (Exception e) {
        }
        return sw.toString();
    }

    private void addXmlNode(XmlSerializer serializer, String nodeName, String value) throws IllegalArgumentException,
            IllegalStateException, IOException {
        if (!TextUtils.isEmpty(value)) {
            serializer.startTag("", nodeName);
            serializer.text(value);
            serializer.endTag("", nodeName);
        }
    }

    private void addXmlNode(XmlSerializer serializer, String nodeName, int value) throws IllegalArgumentException,
            IllegalStateException, IOException {
        serializer.startTag("", nodeName);
        serializer.text(String.valueOf(value));
        serializer.endTag("", nodeName);
    }

}
