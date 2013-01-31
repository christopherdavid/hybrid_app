package com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid;

/*
 * Helper class for a Grid data
 */
public class GridItem {	
	private String mId;
	private String mDataVersion;	
	private String mDataCachePath;
	
	public void setId(String id) {
		mId = id;
	}
	
	public void setDataVersion(String version) {
		mDataVersion = version;
	}
	
	public void setDataFilePath(String dataFilePath) {
		mDataCachePath = dataFilePath;
	}
	
	public String getId() {
		return mId;
	}
	
	public String getDataVersion() {
		return mDataVersion;
	}
	
	public String getDataFilePath() {
		return mDataCachePath;
	}
}
