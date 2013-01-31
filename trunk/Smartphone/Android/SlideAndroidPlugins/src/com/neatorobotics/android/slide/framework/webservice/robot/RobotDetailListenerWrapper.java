package com.neatorobotics.android.slide.framework.webservice.robot;

import android.os.Handler;

public class RobotDetailListenerWrapper implements RobotDetailListener {

	private Handler mHandler;
	private RobotDetailListener mListener;
	public RobotDetailListenerWrapper(RobotDetailListener listener)
	{
		this(listener, null);
	}
	
	public RobotDetailListenerWrapper(RobotDetailListener listener, Handler handler)
	{
		mHandler = handler;
		mListener = listener;
	}
	
	public void onRobotDetailReceived(final RobotItem robotItem) {
		if (mListener == null) {
			return;
		}
		
		if (mHandler == null) {
			mListener.onRobotDetailReceived(robotItem);
			return;
		}
		
		mHandler.post(new Runnable() {
			
			public void run() {
				mListener.onRobotDetailReceived(robotItem);
				
			}
		});
		
		
	}

	public void onNetworkError(final String errMessage) {
		
		if (mListener == null) {
			return;
		}
		
		if (mHandler == null) {
			mListener.onNetworkError(errMessage);
			return;
		}
		
		mHandler.post(new Runnable() {
			
			public void run() {
				mListener.onNetworkError(errMessage);
			}
		});
		
	}

	public void onServerError(final String errMessage) {
		
		if (mListener == null) {
			return;
		}
		
		if (mHandler == null) {
			mListener.onServerError(errMessage);
			return;
		}
		
		mHandler.post(new Runnable() {
			
			public void run() {
				mListener.onServerError(errMessage);
				
			}
		});
		
	}
}
