package com.neatorobotics.android.slide.framework.http.download;


public class FileDownloadWorkItem {
	
	private String mUrl;
	private String mFileName;
	private int mStatus;
	public FileDownloadWorkItem(String url, String fileName)
	{
		mUrl = url;
		mFileName = fileName;
		mStatus = -1;
	}
	
	public String getUrl()
	{
		return mUrl;
	}
	
	public String getFileName()
	{
		return mFileName;
	}
	
	public int getStatus()
	{
		return mStatus;
	}
	
	public void setStatus(int status)
	{
		mStatus = status;
	}

}
