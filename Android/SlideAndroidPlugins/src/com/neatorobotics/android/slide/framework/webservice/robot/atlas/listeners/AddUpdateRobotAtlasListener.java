package com.neatorobotics.android.slide.framework.webservice.robot.atlas.listeners;

public interface AddUpdateRobotAtlasListener {
	public void onSuccess(String atlas_id, String xml_version);
	public void onServerError(String errMessage);
	public void onNetworkError(String errMessage);
}
