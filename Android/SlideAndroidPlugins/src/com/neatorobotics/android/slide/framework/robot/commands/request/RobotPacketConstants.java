package com.neatorobotics.android.slide.framework.robot.commands.request;

public class RobotPacketConstants {
    public static final String XML_TAG_ROOT_NODE = "packet";
    public static final String XML_TAG_HEADER = "header";
    public static final String XML_TAG_HEADER_VERSION = "version";
    public static final String XML_TAG_HEADER_SIGNATURE = "signature";

    public static final String XML_TAG_PAYLOAD = "payload";
    public static final String XML_TAG_COMMAND = "request";
    public static final String XML_TAG_COMMAND_ID = "command";
    public static final String XML_TAG_COMMAND_TIMESTAMP = "timeStamp";
    public static final String XML_TAG_PARAMS_DATA = "params";

    public static final String XML_TAG_RESPONSE = "response";
    public static final String XML_TAG_STATUS = "status";

    // Distribution modes for command packets.
    public static final int DISTRIBUTION_MODE_TYPE_XMPP = 0;
    public static final int DISTRIBUTION_MODE_TYPE_PEER = 1;
    public static final int DISTRIBUTION_MODE_TYPE_TIME_MODE_SERVER = 2;

}
