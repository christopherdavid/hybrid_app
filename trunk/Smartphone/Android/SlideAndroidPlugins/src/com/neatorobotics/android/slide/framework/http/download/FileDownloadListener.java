package com.neatorobotics.android.slide.framework.http.download;

public interface FileDownloadListener {
	public void onDownloadComplete(String url, String filePath);
	public void onDownloadError(String url);
}
