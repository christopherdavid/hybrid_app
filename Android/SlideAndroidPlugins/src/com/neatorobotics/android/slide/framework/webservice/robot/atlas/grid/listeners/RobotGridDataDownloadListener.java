package com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.listeners;

public interface RobotGridDataDownloadListener {
	public void onGridDataDownloaded(String atlasId, String gridId, String fileUrl);
	public void onGridDataDownloadError(String atlasId, String gridId, String errMessage);
}
