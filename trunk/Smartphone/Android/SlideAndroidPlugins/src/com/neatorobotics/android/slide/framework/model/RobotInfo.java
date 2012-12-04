package com.neatorobotics.android.slide.framework.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RobotInfo implements Parcelable {

	private String mRobotId;
	private String mRobotIpAddress;
	private String mSerialId;
	private int mRobotPort;
	private String mRobotName;
	
	public RobotInfo()
	{
		
	}
	
	private RobotInfo(Parcel in) {
        readFromParcel(in);
    }	
	
	   
    // 	Read the content from Parcel and assign it to members
    public void readFromParcel(Parcel in) {
    	mRobotId = in.readString();
    	mRobotIpAddress = in.readString();
    	mSerialId = in.readString();
    	mRobotPort = in.readInt();
    	mRobotName = in.readString();
    }   
	
	public static final Parcelable.Creator<RobotInfo> CREATOR = new Parcelable.Creator<RobotInfo>() {
        public RobotInfo createFromParcel(Parcel in) {
            return new RobotInfo(in);
        }

        public RobotInfo[] newArray(int size) {
            return new RobotInfo[size];
        }
    };	
    
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mRobotId);
		dest.writeString(mRobotIpAddress);
		dest.writeString(mSerialId);
		dest.writeInt(mRobotPort);
		dest.writeString(mRobotName);
	}
	
	public String getRobotId() {
		return mRobotId;
	}
	
	public void setRobotId(String robotId) {
		this.mRobotId = robotId;
	}
	
	public String getRobotIpAddress() {
		return mRobotIpAddress;
	}
	
	public void setRobotIpAddress(String robotIpAddress) {
		this.mRobotIpAddress = robotIpAddress;
	}
	
	public String getSerialId() {
		return mSerialId;
	}
	
	public void setSerialId(String serialId) {
		this.mSerialId = serialId;
	}
	
	public void setRobotPort(int robotPort)
	{
		mRobotPort = robotPort;
	}
	
	public int getRobotPort()
	{
		return mRobotPort;
	}
	
	
	public String getRobotName() {
		return mRobotName;
	}
	
	public void setRobotName(String robotName) {
		this.mRobotName = robotName;
	}

	//TODO : This will be displayed in the list of robots. Need to decide what to show to the user.
	 @Override
	public String toString() {
	    return mSerialId;
	}
}
