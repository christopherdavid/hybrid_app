package com.neatorobotics.android.slide.framework.robot.schedule2;

public class ScheduleInfo2 {
	
	private String mServerId = "-1";
	private String mScheduleDataVersion = "";
	private String mScheduleType = "";
	private String mScheduleData = "";
	private String mScheduleId = "";
	
	public void setScheduleId(String id) {
		mScheduleId = id;
	}
	public String getScheduleId() {
		return mScheduleId;
	}
	public void setServerId(String serverId) {
		mServerId = serverId;
	}

	public void setScheduleData(String scheduleData) {
		mScheduleData = scheduleData;
	}
	public void setDataVersion(String scheduleDataVersion) {
		mScheduleDataVersion = scheduleDataVersion;
	}

	public void setScheduleType(String scheduleType) {
		mScheduleType = scheduleType;
	}
	
	public String getServerId() {
		return mServerId;
	}

	public String getDataVersion() {
		return mScheduleDataVersion;
	}

	public String getScheduleType() {
		return mScheduleType;
	}
	
	public String getScheduleData() {
		return mScheduleData;
	}
}
