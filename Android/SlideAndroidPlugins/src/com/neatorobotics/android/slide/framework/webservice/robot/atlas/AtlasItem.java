package com.neatorobotics.android.slide.framework.webservice.robot.atlas;

/*
 * Helper class for an Atlas data
 */
public class AtlasItem {	
	private String mId;
	private String mVersion;	
	private String mXmlCachePath;
	
	public void setId(String id) {
		mId = id;
	}
	
	public void setXmlVersion(String version) {
		mVersion = version;
	}
	
	public void setXmlFilePath(String xmlFilePath) {
		mXmlCachePath = xmlFilePath;
	}
	
	public String getId() {
		return mId;
	}
	
	public String getXmlVersion() {
		return mVersion;
	}
	
	public String getXmlFilePath() {
		return mXmlCachePath;
	}
}
