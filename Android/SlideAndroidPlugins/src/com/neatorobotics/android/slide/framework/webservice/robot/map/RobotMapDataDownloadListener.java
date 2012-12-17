package com.neatorobotics.android.slide.framework.webservice.robot.map;

public interface RobotMapDataDownloadListener {
	public void onMapDataDownloaded(String robotId, String mapId, String mapOverlay, String mapImage);
	public void onMapDataDownloadError(String robotId, String mapId, String errMessage);
}
