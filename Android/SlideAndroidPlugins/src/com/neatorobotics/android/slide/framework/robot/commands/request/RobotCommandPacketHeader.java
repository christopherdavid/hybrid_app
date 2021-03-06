package com.neatorobotics.android.slide.framework.robot.commands.request;

public class RobotCommandPacketHeader {
    private int version;
    private int signature;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getSignature() {
        return signature;
    }

    public void setSignature(int signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n*** RobotCommandPacketHeader ***\n");
        sb.append("---------------------------------\n");
        sb.append("Version = " + version);
        sb.append("\n");
        sb.append("signature = " + signature);
        return sb.toString();
    }

    // Method to create header depending on signature and version.
    public static RobotCommandPacketHeader getRobotCommandHeader(int signature, int version) {
        RobotCommandPacketHeader header = new RobotCommandPacketHeader();
        header.setSignature(signature);
        header.setVersion(version);
        return header;
    }
}
