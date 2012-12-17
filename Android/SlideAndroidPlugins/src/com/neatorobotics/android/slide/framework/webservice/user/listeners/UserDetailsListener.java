package com.neatorobotics.android.slide.framework.webservice.user.listeners;

import com.neatorobotics.android.slide.framework.webservice.user.UserItem;


public interface UserDetailsListener {
	public void onUserDetailsReceived(UserItem userItem);
	public void onNetworkError();
	public void onServerError();

}
