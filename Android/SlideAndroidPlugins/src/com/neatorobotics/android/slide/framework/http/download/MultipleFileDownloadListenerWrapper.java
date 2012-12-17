/**
 * FileDownloadListenerWrapper class
 * FileDownloadListener wrapper class to notify the file download status in UI thread
 * if caller pass the handler, file download will be done on UI thread, otherwise it will
 * be done on the background thread
 * 
 */

package com.neatorobotics.android.slide.framework.http.download;

import java.util.List;

import android.os.Handler;

public class MultipleFileDownloadListenerWrapper implements MultipleFileDownloadListener {
	
	private Handler mHandler;
	private MultipleFileDownloadListener mListener;
	
	public MultipleFileDownloadListenerWrapper(Handler handler, MultipleFileDownloadListener listener)
	{
		mHandler = handler;
		mListener = listener;
	}
	
	@Override
	public void onDownloadComplete(final List<FileDownloadWorkItem> items) {
		if (mListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						mListener.onDownloadComplete(items);
					}
				});
			}
			else {
				mListener.onDownloadComplete(items);
			}
		}
	}
}
