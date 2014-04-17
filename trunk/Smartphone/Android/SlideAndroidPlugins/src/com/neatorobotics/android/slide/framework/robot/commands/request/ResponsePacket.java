package com.neatorobotics.android.slide.framework.robot.commands.request;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class ResponsePacket implements Parcelable {

    private String requestId;
    private int status;
    private HashMap<String, String> paramData;

    private ResponsePacket(String requestId, int status, Map<String, String> commandParams) {
        this.requestId = requestId;
        this.status = status;
        paramData = new HashMap<String, String>(commandParams);
    }

    public static ResponsePacket createResponseWithParams(String requestId, int status, Map<String, String> params) {
        ResponsePacket response = new ResponsePacket(requestId, status, params);
        return response;
    }

    public String getRequestId() {
        return requestId;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getParams() {
        return paramData;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n*** Response Packet ***\n");
        sb.append("----------------------\n");
        sb.append("Request Id = " + requestId);
        sb.append("\nParams...\n");

        for (String key : paramData.keySet()) {
            sb.append("\t");
            sb.append(key);
            sb.append("\t");
            sb.append(paramData.get(key));
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeToParcel(dest);
    }

    public void writeToParcel(Parcel out) {
        out.writeString(requestId);
        out.writeInt(status);
        out.writeInt(paramData.size());
        for (String key : paramData.keySet()) {
            out.writeString(key);
            out.writeString(paramData.get(key));
        }
    }

    public static ResponsePacket readFromParcel(Parcel in) {
        String requestId = in.readString();
        int status = in.readInt();
        HashMap<String, String> paramData = new HashMap<String, String>();
        int paramSize = in.readInt();
        for (int i = 0; i < paramSize; i++) {
            String key = in.readString();
            String value = in.readString();
            paramData.put(key, value);
        }
        ResponsePacket response = new ResponsePacket(requestId, status, paramData);
        return response;
    }

}
