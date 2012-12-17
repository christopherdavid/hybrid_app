package com.neatorobotics.android.slide.framework.webservice.user.listeners;

import com.neatorobotics.android.slide.framework.webservice.user.UserItem;

import android.os.Handler;


public class UserDetailsListenerWrapper implements UserDetailsListener{



	private Handler mHandler;
	private UserDetailsListener mListener;
	public UserDetailsListenerWrapper(UserDetailsListener listener)
	{
		this(listener, null);
	}
	
	public UserDetailsListenerWrapper(UserDetailsListener listener, Handler handler)
	{
		mHandler = handler;
		mListener = listener;
	}
	
	public void onUserDetailsReceived(final UserItem userItem) {
		if (mListener == null) {
			return;
		}
		
		if (mHandler == null) {
			mListener.onUserDetailsReceived(userItem);
			return;
		}
		
		mHandler.post(new Runnable() {
			
			public void run() {
				mListener.onUserDetailsReceived(userItem);
				
			}
		});
		
		
	}

	public void onNetworkError() {
		
		if (mListener == null) {
			return;
		}
		
		if (mHandler == null) {
			mListener.onNetworkError();
			return;
		}
		
		mHandler.post(new Runnable() {
			
			public void run() {
				mListener.onNetworkError();
			}
		});
		
	}

	public void onServerError() {
		
		if (mListener == null) {
			return;
		}
		
		if (mHandler == null) {
			mListener.onServerError();
			return;
		}
		
		mHandler.post(new Runnable() {
			
			public void run() {
				mListener.onServerError();
				
			}
		});
		
	}
}
