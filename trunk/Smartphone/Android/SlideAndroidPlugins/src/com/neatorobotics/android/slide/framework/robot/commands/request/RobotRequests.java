package com.neatorobotics.android.slide.framework.robot.commands.request;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class RobotRequests implements Parcelable {
    private ArrayList<RequestPacket> commands = new ArrayList<RequestPacket>();

    public int getNumberOfCommands() {
        return commands.size();
    }

    public RequestPacket getCommand(int index) {
        if (isValidIndex(index)) {
            return commands.get(index);
        }
        return null;
    }

    public void addCommand(RequestPacket command) {
        commands.add(command);
    }

    private boolean isValidIndex(int index) {
        int size = commands.size();
        if (index >= 0 && index < size) {
            return true;
        }

        return false;
    }

    public void setDistributionMode(int distributionMode) {
        if (commands == null || commands.size() == 0) {
            return;
        }
        int size = commands.size();
        for (int i = 0; i < size; i++) {
            RequestPacket packet = commands.get(i);
            if (packet != null) {
                packet.setDistributionMode(distributionMode);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n*** Robot Command Collection ***\n");
        sb.append("---------------------------------\n");
        sb.append("Number of Commands = " + commands.size());
        sb.append("\nCommands\n");

        for (RequestPacket robot : commands) {
            sb.append(robot);
        }

        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int commandSize = getNumberOfCommands();
        // Number of requests.
        dest.writeInt(commandSize);
        for (int i = 0; i < commandSize; i++) {
            getCommand(i).writeToParcel(dest, flags);
        }
    }

    public static RobotRequests readFromParcel(Parcel in) {
        RobotRequests requests = new RobotRequests();
        // Number of requests.
        int requestSize = in.readInt();
        for (int i = 0; i < requestSize; i++) {
            requests.addCommand(RequestPacket.readFromParcel(in));
        }
        return requests;
    }

    public static final Parcelable.Creator<RobotRequests> CREATOR = new Parcelable.Creator<RobotRequests>() {
        public RobotRequests createFromParcel(Parcel in) {
            return readFromParcel(in);
        }

        public RobotRequests[] newArray(int size) {
            return new RobotRequests[size];
        }
    };
}
