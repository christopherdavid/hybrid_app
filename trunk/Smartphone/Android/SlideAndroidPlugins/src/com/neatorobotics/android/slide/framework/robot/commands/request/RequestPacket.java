package com.neatorobotics.android.slide.framework.robot.commands.request;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestPacket implements Parcelable {

	private int command;
	private String requestId;
	private String timestamp;
	private int retryCount;
	private int distributionMode;
	private boolean responseNeeded;
	private String replyToAddress;
	private HashMap<String, String> commandParams;
	
	private RequestPacket(int command)
	{
		this.command = command;
		commandParams = new HashMap<String, String>();
	}
	
	private RequestPacket(int command, Map<String, String> commandParams)
	{
		this.command = command;
		//put check for null otherwise it will crash.
		if (commandParams == null) {
			this.commandParams = new HashMap<String, String>();
		} else {
			this.commandParams = new HashMap<String, String>(commandParams);
		}
	}

	public static RequestPacket createRobotCommand(int commandId)
	{
		RequestPacket robotCommand = new RequestPacket(commandId);
		return robotCommand;
	}
	
	public static RequestPacket createRobotCommandWithParams(int commandId, Map<String, String> commandParams)
	{
		RequestPacket robotCommand = new RequestPacket(commandId, commandParams);
		return robotCommand;
	}

	public int getCommand() {
		return command;
	}
	
	public String getCommandParam(String paramKey)
	{
		if (commandParams.containsKey(paramKey)) {
			return commandParams.get(paramKey);
		}
		
		return "";
	}
	
	public Map<String, String> getCommandParams()
	{
		return commandParams;
	}
	
	
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


	public int getDistributionMode() {
		return distributionMode;
	}

	public void setDistributionMode(int distributionMode) {
		this.distributionMode = distributionMode;
	}

	public boolean isResponseNeeded() {
		return responseNeeded;
	}

	public void setResponseNeeded(boolean responseNeeded) {
		this.responseNeeded = responseNeeded;
	}

	public String getReplyToAddress() {
		return replyToAddress;
	}

	public void setReplyToAddress(String replyToAddress) {
		this.replyToAddress = replyToAddress;
	}
	
	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n*** Request Packet ***\n");
		sb.append("----------------------\n");
		sb.append(" Command = " + command);
		sb.append("\n Request Id = " + requestId);
		sb.append("\n Time Stamp = " + timestamp);
		sb.append("\n Distribution Mode = " + distributionMode);
		sb.append("\n reply To = " + replyToAddress);
		sb.append("\n responseNeeded = " + responseNeeded);
		sb.append("\n retryCount = " + retryCount);
		sb.append("\n Params...\n");
		
		for (String key: commandParams.keySet()) {
			sb.append("\t");
			sb.append(key);
			sb.append("\t");
			sb.append(commandParams.get(key));
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
		out.writeInt(command);
		out.writeString(requestId);
		out.writeString(timestamp);
		out.writeInt(distributionMode);
		out.writeString(replyToAddress);
		out.writeByte((byte) (responseNeeded ? 1 : 0));     //if responseNeeded == true, byte == 1
		out.writeInt(retryCount);
		out.writeInt(commandParams.size());
		for(String key : commandParams.keySet()){
			out.writeString(key);
			out.writeString(commandParams.get(key));
		}
	}

	public static RequestPacket readFromParcel(Parcel in) {
		int command = in.readInt();
		RequestPacket request = new RequestPacket(command);

		request.setRequestId(in.readString());
		request.setTimestamp(in.readString());
		request.setDistributionMode(in.readInt());
		request.setReplyToAddress(in.readString());
		boolean responseNeeded = in.readByte() == 1;   //if responseNeeded == true, byte == 1
		request.setResponseNeeded(responseNeeded);
		request.setRetryCount(in.readInt());
		int size = in.readInt();
		for(int i = 0; i < size; i++){
			String key = in.readString();
			String value = in.readString();
			request.getCommandParams().put(key,value);
		}
		return request;
	}

	public static final Parcelable.Creator<RequestPacket> CREATOR = new Parcelable.Creator<RequestPacket>() {
		public RequestPacket createFromParcel(Parcel in) {
			return readFromParcel(in);
		}
		public RequestPacket[] newArray(int size) {
			return new RequestPacket[size];
		}
	};
}
