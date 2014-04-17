package com.neatorobotics.android.slide.framework.tcp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandBuilder;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacketUtils;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandParser;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotPacketConstants;
import com.neatorobotics.android.slide.framework.transport.Transport;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;

// TODO: Change name
public class RobotPeerConnectionUtils {

    private static final String TAG = RobotPeerConnectionUtils.class.getSimpleName();
    private static final int PACKET_READ_CHUNK_SIZE = (4 * 1024);

    protected static void sendRobotPacket(final Transport transport, final byte[] packet) {
        if (transport == null) {
            LogHelper.log(TAG, "Transport is null. Cannot send packet");
            return;
        }
        if (packet == null) {
            LogHelper.log(TAG, "Packet is null");
            return;
        }

        try {
            transport.send(packet);
            LogHelper.log(TAG, "Packet is sent");
        } catch (IOException e) {
            LogHelper.log(TAG, "Exception in sendRobotPacket", e);
        }

    }

    protected static void sendRobotPacketAsync(final Transport transport, final RobotCommandPacket robotPacket) {
        Runnable task = new Runnable() {

            public void run() {
                byte[] packet = getRobotPacketBytes(robotPacket);
                if (transport == null) {
                    LogHelper.log(TAG, "Transport is null. Cannot send packet");
                    return;
                }
                if (packet == null) {
                    LogHelper.log(TAG, "Packet is null");
                    return;
                }

                try {
                    transport.send(packet);
                    LogHelper.log(TAG, "Packet is sent");
                } catch (IOException e) {
                    LogHelper.log(TAG, "Exception in sendRobotPacket", e);
                }
            }
        };
        Thread t = new Thread(task);
        t.start();
    }

    private static byte[] getRobotPacketBytes(RobotCommandPacket robotPacket) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        RobotCommandBuilder builder = new RobotCommandBuilder();
        byte[] packet = builder.convertRobotCommandsToBytes(robotPacket);
        int signature = robotPacket.getHeader().getSignature();
        int version = robotPacket.getHeader().getVersion();
        try {
            dos.writeInt(signature);
            dos.writeInt(version);
            dos.writeInt(packet.length);
            dos.write(packet);
        } catch (Exception e) {
            LogHelper.log(TAG, "Exception in getBytes", e);
            return null;
        }
        return bos.toByteArray();
    }

    protected static void logAsString(byte[] data) {
        String dataAsStr;
        try {
            dataAsStr = new String(data, "UTF-8");
            LogHelper.logD(TAG, "Message received");
            LogHelper.logD(TAG, "Message = " + dataAsStr);
        } catch (UnsupportedEncodingException e) {

        }

    }

    protected static RobotCommandPacket readPacket(DataInputStream din) throws EOFException, IOException {
        RobotCommandPacket commandPacket = null;

        int length = din.readInt();
        LogHelper.log(TAG, "length = " + length);
        byte[] commandData = new byte[length];
        readByteArrayHelper(din, commandData, length);
        RobotPeerConnectionUtils.logAsString(commandData);
        LogHelper.log(TAG, "Header Version and Signature match");
        RobotCommandParser commandParser = new RobotCommandParser();
        commandPacket = commandParser.convertBytesToRobotCommands(commandData);

        return commandPacket;
    }

    private static void readByteArrayHelper(DataInputStream din, byte[] byData, int length) throws EOFException,
            IOException {
        int chunkLength = (length > PACKET_READ_CHUNK_SIZE) ? PACKET_READ_CHUNK_SIZE : length;
        byte[] buffer = new byte[chunkLength];
        int offSet = 0;
        while (length > 0) {
            int dataReadSize = (length > PACKET_READ_CHUNK_SIZE) ? PACKET_READ_CHUNK_SIZE : length;
            int dataRead = din.read(buffer, 0, dataReadSize);
            length -= dataRead;
            System.arraycopy(buffer, 0, byData, offSet, dataRead);
            offSet += dataRead;
            if (length > 0) {
                TaskUtils.sleep(100);
            }
        }
    }

    protected static RobotCommandPacket getConnectionBreakPacket(Context context) {
        int commandId = RobotCommandPacketConstants.COMMAND_ROBOT_CONNECTION_BREAK;
        HashMap<String, String> commandParams = new HashMap<String, String>();
        int distributionMode = RobotPacketConstants.DISTRIBUTION_MODE_TYPE_PEER;
        return RobotCommandPacketUtils.getRobotCommandPacket(context, commandId, commandParams, distributionMode);
    }

    protected static RobotCommandPacket getConnectionPingPacket(Context context) {
        int commandId = RobotCommandPacketConstants.COMMAND_ROBOT_CONNECTION_PING;
        HashMap<String, String> commandParams = new HashMap<String, String>();
        int distributionMode = RobotPacketConstants.DISTRIBUTION_MODE_TYPE_PEER;
        return RobotCommandPacketUtils.getRobotCommandPacket(context, commandId, commandParams, distributionMode);
    }
}
