package com.neatorobotics.android.slide.framework.robot.commands.request;

public class RobotCommandPacket {

    private RobotCommandPacketHeader header;
    private RobotRequests robotCommands;
    private ResponsePacket commandResponse;

    private RobotCommandPacket(RobotCommandPacketHeader header, RobotRequests robotCommands) {
        this.header = header;
        this.robotCommands = robotCommands;
    }

    private RobotCommandPacket(RobotCommandPacketHeader header, ResponsePacket commandResonse) {
        this.header = header;
        this.commandResponse = commandResonse;
    }

    public static RobotCommandPacket createRobotCommandPacket(RobotCommandPacketHeader header,
            RobotRequests robotCommands) {
        RobotCommandPacket robotCommandPacket = new RobotCommandPacket(header, robotCommands);
        return robotCommandPacket;
    }

    public static RobotCommandPacket createRobotCommandPacket(RobotCommandPacketHeader header,
            RequestPacket robotCommand) {
        RobotRequests robotCommands = new RobotRequests();
        robotCommands.addCommand(robotCommand);
        RobotCommandPacket robotCommandPacket = new RobotCommandPacket(header, robotCommands);
        return robotCommandPacket;
    }

    public static RobotCommandPacket createRobotPacketWithResponseData(RobotCommandPacketHeader header,
            ResponsePacket commandResonse) {
        RobotCommandPacket robotCommandPacket = new RobotCommandPacket(header, commandResonse);
        return robotCommandPacket;
    }

    public RobotCommandPacketHeader getHeader() {
        return header;
    }

    public RobotRequests getRobotCommands() {
        return robotCommands;
    }

    public ResponsePacket getCommandResponse() {
        return commandResponse;
    }

    public boolean isRequest() {
        return (robotCommands != null);
    }

    public boolean isResponse() {
        return (commandResponse != null);
    }

    public boolean isValidPacket() {
        return ((commandResponse != null) || (robotCommands != null));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n*** Robot Command Packet ***\n");
        sb.append("-------------------------------\n");
        sb.append("Header = " + header);
        sb.append("\nisRequest =" + isRequest());
        if (isRequest()) {
            sb.append("\nRobotCommands = " + robotCommands);
        } else {
            sb.append("\nRobot Response = " + commandResponse);
        }
        return sb.toString();
    }

}
