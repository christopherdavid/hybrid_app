package com.neatorobotics.android.slide.framework.webservice.user;


public interface UserDetailsListener {
	public void onUserDetailsReceived(UserItem userItem);
	public void onNetworkError();
	public void onServerError();

}
