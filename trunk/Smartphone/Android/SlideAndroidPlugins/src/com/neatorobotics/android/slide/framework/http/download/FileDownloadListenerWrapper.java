/**
 * FileDownloadListenerWrapper class
 * FileDownloadListener wrapper class to notify the file download status in UI thread
 * if caller pass the handler, file download will be done on UI thread, otherwise it will
 * be done on the background thread
 * 
 */

package com.neatorobotics.android.slide.framework.http.download;

import android.os.Handler;

public class FileDownloadListenerWrapper implements FileDownloadListener {
	
	private Handler mHandler;
	private FileDownloadListener mListener;
	
	public FileDownloadListenerWrapper(Handler handler, FileDownloadListener listener)
	{
		mHandler = handler;
		mListener = listener;
	}
	public void onDownloadComplete(final String url, final String filePath)
	{
		if (mListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						mListener.onDownloadComplete(url, filePath);
					}
				});
			}
			else {
				mListener.onDownloadComplete(url, filePath);
			}
		}
	}
	public void onDownloadError(final String url)
	{
		if (mListener != null) {
			if (mHandler != null) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mListener.onDownloadError(url);
					}
				});
			}
			else {
				mListener.onDownloadError(url);
			}
		}
	}
}
