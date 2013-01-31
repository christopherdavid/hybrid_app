package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import android.os.Handler;

public class ScheduleWebserviceListenerWrapper implements ScheduleWebserviceListener {
	private Handler mHandler;
	private ScheduleWebserviceListener mListener;
	
	public ScheduleWebserviceListenerWrapper(Handler handler, ScheduleWebserviceListener listener)
	{
		mHandler = handler;
		mListener = listener;
	}

	@Override
	public void onSuccess() {
		if (mListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						mListener.onSuccess();
					}
				});
			}
			else {
				mListener.onSuccess();
			}
		}

		
	}

	@Override
	public void onNetworkError(final String errMessage) {

		if (mListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						mListener.onNetworkError(errMessage);
					}
				});
			}
			else {
				mListener.onNetworkError(errMessage);
			}
		}

		
	
		
	}

	@Override
	public void onServerError(final String errMessage) {

		if (mListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						mListener.onNetworkError(errMessage);
					}
				});
			}
			else {
				mListener.onNetworkError(errMessage);
			}
		}

		
	
		
	}

	

}
