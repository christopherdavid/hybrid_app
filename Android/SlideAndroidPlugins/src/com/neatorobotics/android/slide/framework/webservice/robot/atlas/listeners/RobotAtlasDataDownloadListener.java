package com.neatorobotics.android.slide.framework.webservice.robot.atlas.listeners;

public interface RobotAtlasDataDownloadListener {
	public void onAtlasDataDownloaded(String atlasId, String fileUrl);
	public void onAtlasDataDownloadError(String atlasId, String errMessage);
}
