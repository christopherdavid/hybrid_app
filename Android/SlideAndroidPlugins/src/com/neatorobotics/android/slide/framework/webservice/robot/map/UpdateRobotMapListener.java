package com.neatorobotics.android.slide.framework.webservice.robot.map;

public interface UpdateRobotMapListener {

	public void onSuccess(String robot_map_id, String map_overlay_version, String map_blob_version);
	public void onServerError(String errMessage);
	public void onNetworkError(String errMessage);
}
