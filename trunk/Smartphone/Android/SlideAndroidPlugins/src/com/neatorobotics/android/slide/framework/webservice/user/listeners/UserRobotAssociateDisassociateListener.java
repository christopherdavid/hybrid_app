package com.neatorobotics.android.slide.framework.webservice.user.listeners;

public interface UserRobotAssociateDisassociateListener {
	public void onComplete();
	public void onNetworkError(String errorMessage);
	public void onServerError(String errorMessage);
}
