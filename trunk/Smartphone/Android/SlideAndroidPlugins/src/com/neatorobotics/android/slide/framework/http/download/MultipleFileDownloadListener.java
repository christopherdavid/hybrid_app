package com.neatorobotics.android.slide.framework.http.download;

import java.util.List;

public interface MultipleFileDownloadListener {
	public void onDownloadComplete(List<FileDownloadWorkItem> items);
}
