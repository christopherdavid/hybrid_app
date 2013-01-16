package com.neatorobotics.android.slide.framework.webservice.robot.map;

import java.util.ArrayList;
import java.util.List;

import com.neatorobotics.android.slide.framework.http.download.FileCachePath;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadWorkItem;
import com.neatorobotics.android.slide.framework.http.download.MultipleFileDownloadHelper;
import com.neatorobotics.android.slide.framework.http.download.MultipleFileDownloadListener;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

public class RobotMapWebservicesManager {
	private static final String TAG = RobotMapWebservicesManager.class.getSimpleName();
	Context mContext;
	
	private Handler mHandler;

	private static String mMapId;
	private static String mOverlayVersion;
	private static String mBlobVersion;

	private static RobotMapWebservicesManager sRobotMapWebservicesManager;
	private static final Object INSTANCE_LOCK = new Object(); 
	private RobotMapWebservicesManager(Context context)
	{
		mContext = context.getApplicationContext();
	}

	public static RobotMapWebservicesManager getInstance(Context context)
	{
		synchronized (INSTANCE_LOCK) {
			if (sRobotMapWebservicesManager == null) {
				sRobotMapWebservicesManager = new RobotMapWebservicesManager(context);
			}
		}

		return sRobotMapWebservicesManager;
	}
	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	public void getRobotMapData(final String robotId, final RobotMapDataDownloadListener listener) {

		if (TextUtils.isEmpty(robotId)) {
			return;
		}

		Runnable task = new Runnable() {
			
			@Override
			public void run() {

				String blobDataUrl;
				String xmlDataUrl;
				boolean robotHasMap = getMapIdAndUpdatedVersions(robotId);
				if (robotHasMap) {				
					GetNeatoRobotMapDataResult result = NeatoRobotMapWebservicesHelper.getNeatoRobotMapDataRequest(mContext, mMapId);
					if (result.success()) {
						LogHelper.log(TAG, String.format("XML Url = [%s] Image URL = [%s]", result.mResult.mXml_Data_Url, result.mResult.mBlob_Data_Url));
						blobDataUrl = result.mResult.mBlob_Data_Url;
						xmlDataUrl = result.mResult.mXml_Data_Url;
						downloadMapImageAndOverlayFiles(robotId , xmlDataUrl, blobDataUrl, listener);
					} 
					else {
						LogHelper.log(TAG, "Could not retrieve map data");
					}
				}	
				else {
					LogHelper.log(TAG, "Map does not exist.");
				}
				return;
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	// Fetching map Id and updated data versions from the server for associate robot
	// NOTE: Don't call this function on UI thread call it on secondary thread
	//
	private boolean getMapIdAndUpdatedVersions(String robotId) {
		LogHelper.log(TAG, "getMapIdAndUpdatedVersions called");
		boolean robotMapped = false;
		GetNeatoRobotMapsResult result = NeatoRobotMapWebservicesHelper.getNeatoRobotMapsRequest(mContext, robotId);
		// Note: Here assume only one map id will get 
		if (result.success()) {				
			if ((result.mResult != null) && result.mResult.size() > 0) { 
				mMapId  = result.mResult.get(0).mId;
				mOverlayVersion = result.mResult.get(0).mXml_Data_Version;
				mBlobVersion = result.mResult.get(0).mBlob_Data_Version;
				robotMapped = true;
			}
		}
		else {
			mMapId = null;			
		}

		return robotMapped;
	}

	public void setRobotOverlayData(final String robotId, final String mapOverlayData, final UpdateRobotMapListener listener) {
		
		Runnable task = new Runnable() {
			public void run() {

				boolean robotHasMap = getMapIdAndUpdatedVersions(robotId);
				if (robotHasMap) {
					UpdateNeatoRobotMapResult result = NeatoRobotMapWebservicesHelper.updateNeatoMapXmlData(mContext, mMapId, mOverlayVersion, mapOverlayData);
					if (result != null && result.success()) {
						//Now the map id and version might change. So we should send the updated version nad mapid as result.
						robotHasMap = getMapIdAndUpdatedVersions(robotId);
						if (listener != null) {
							listener.onSuccess(mMapId, mOverlayVersion, mBlobVersion);
						} else {
							LogHelper.log(TAG, "UpdateRobotMapListener is null. Update success.");
						}
					} else {
						if (listener != null) {
							listener.onError("Error in updating the overdata for map");
						} 
						else {
							LogHelper.log(TAG, "UpdateRobotMapListener is null. Update failed.");
						}
					}
				
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	private void downloadMapImageAndOverlayFiles(final String robotId, final String overlayFileUrl, final String imageFileUrl, final RobotMapDataDownloadListener listener) {

		LogHelper.log(TAG, "downloadMapDataAndShow called");
		final String overlayFilePath = FileCachePath.getMapXMLFilePath(mContext, robotId, mMapId);
		final String mapImageFile = FileCachePath.getImageFilePath(mContext, robotId, mMapId);
		
		ArrayList<FileDownloadWorkItem> items = new ArrayList<FileDownloadWorkItem>();
		FileDownloadWorkItem overlayFileItem = new FileDownloadWorkItem(overlayFileUrl, overlayFilePath);
		items.add(overlayFileItem);
		FileDownloadWorkItem mapImageFileItem = new FileDownloadWorkItem(imageFileUrl, mapImageFile);
		items.add(mapImageFileItem);
		
		MultipleFileDownloadHelper.startDownload(mContext, items, new MultipleFileDownloadListener() {
			
			@Override
			public void onDownloadComplete(List<FileDownloadWorkItem> items) {
				boolean allSuccess = true;
				if (items != null && items.size() > 0) {
					
					for (FileDownloadWorkItem item: items) {
						
						if(item.getStatus() != 0) {
							allSuccess = false;
						}
					}
				}
				else {
					allSuccess = false;
				}
				
				if (allSuccess) {
					if (mHandler != null) {
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								listener.onMapDataDownloaded(robotId, mMapId, overlayFilePath, mapImageFile);
							}
						});
					}
					else {
						listener.onMapDataDownloaded(robotId, mMapId, overlayFilePath, mapImageFile);
					}
				} else {
					if (mHandler != null) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								listener.onMapDataDownloadError(robotId, mMapId, "Map could not be downloaded");
							}
						});
						
					}
					else {
						listener.onMapDataDownloadError(robotId, mMapId, "Map could not be downloaded");
					}
				}
				
			}
		});
	}
}
