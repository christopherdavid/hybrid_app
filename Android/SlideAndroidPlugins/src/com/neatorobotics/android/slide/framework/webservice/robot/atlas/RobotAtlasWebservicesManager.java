package com.neatorobotics.android.slide.framework.webservice.robot.atlas;


import java.io.File;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.database.DBHelper;
import com.neatorobotics.android.slide.framework.http.download.FileCachePath;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadHelper;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadListener;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.listeners.AddUpdateRobotAtlasListener;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.listeners.RobotAtlasDataDownloadListener;

public class RobotAtlasWebservicesManager {

	private static final String TAG = RobotAtlasWebservicesManager.class.getSimpleName();
	private Context mContext;
	private static RobotAtlasWebservicesManager sRobotAtlasWebservicesManager;
	private static final Object INSTANCE_LOCK = new Object(); 

	private RobotAtlasWebservicesManager(Context context)
	{
		mContext = context.getApplicationContext();
	}

	public static RobotAtlasWebservicesManager getInstance(Context context)
	{
		synchronized (INSTANCE_LOCK) {
			if (sRobotAtlasWebservicesManager == null) {
				sRobotAtlasWebservicesManager = new RobotAtlasWebservicesManager(context);
			}
		}

		return sRobotAtlasWebservicesManager;
	}	

	public void addRobotAtlasData(final String robotId, final String atlas_data, final AddUpdateRobotAtlasListener listener) {

		if (TextUtils.isEmpty(robotId)) {
			listener.onServerError("Robot Id is null");
			return;
		}
		Runnable task = new Runnable() {
			@Override
			public void run() {	
				AddUpdateRobotAtlasMetadataResult result = RobotAtlasWebservicesHelper.addRobotAtlasRequest(mContext, robotId, atlas_data);
				if (result != null) {
					if (result.success()) {
						LogHelper.log(TAG, "Atlas data added successfully");
						String atlasId = result.mResult.mRobot_Atlas_Id;
						String xmlVersion = result.mResult.mXml_Data_Version;
						listener.onSuccess(atlasId, xmlVersion);
					} 
					else {
						LogHelper.log(TAG, "Could not add atlas data");
						listener.onServerError(result.mMessage);
					}	
				} else {
					listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);
				}
				return;
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	public void updateRobotAtlasData(final String robotId, final String atlas_data_version, final String atlas_data, final AddUpdateRobotAtlasListener listener) {

		Runnable task = new Runnable() {
			@Override
			public void run() {	
				//TODO: right now we will not use  XML version inside and calculate the needed XML version as well as id. later store in the database
				String atlas_id = getAtlasId(robotId);
				String currentXmlVersion = getAtlasVersion(robotId);
				
				AddUpdateRobotAtlasMetadataResult result = RobotAtlasWebservicesHelper.updateRobotAtlasDataRequest(mContext, atlas_id, atlas_data, currentXmlVersion);
				if (result != null) {
					if (result.success()) {
						LogHelper.log(TAG, "Data updated successfully");
						//As updated xml version is not sent, we should retrieve it.
						String xmlVersion = getAtlasVersion(atlas_id);
						listener.onSuccess(atlas_id, xmlVersion);
					} 
					else {
						LogHelper.log(TAG, "Could not update atlas data");
						listener.onServerError(result.mMessage);
					}	
				}
				else {
					listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);
				}
				return;
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	// Fetching updated atlas data versions from the server for robot
	// NOTE: Don't call this function on UI thread call it on secondary thread
	public String getAtlasVersion(String robotId) {
		String atlasVersion = null;
		LogHelper.log(TAG, "getAtlasVersion called");
		GetRobotAtlasDataResult result = RobotAtlasWebservicesHelper.getRobotAtlasDataRequest(mContext, robotId);
		if (result.success()) {				 
			atlasVersion = result.mResult.mXml_Data_Version;
		}
		return atlasVersion;
	}

	public String getAtlasId(String robotId) {
		String atlasId = null;
		LogHelper.log(TAG, "getAtlasId called");
		GetRobotAtlasDataResult result = RobotAtlasWebservicesHelper.getRobotAtlasDataRequest(mContext, robotId);
		if (result.success()) {				 
			atlasId = result.mResult.mAtlas_Id;
		}
		return atlasId;
	}
	
	public void getRobotAtlasData(final String robotId, final RobotAtlasDataDownloadListener listener) {

		if (TextUtils.isEmpty(robotId)) {
			listener.onAtlasDataDownloadError("" , "Robot Id is null");
			return;
		}
		Runnable task = new Runnable() {
			@Override
			public void run() {			
				GetRobotAtlasDataResult result = RobotAtlasWebservicesHelper.getRobotAtlasDataRequest(mContext, robotId);
				if (result.success()) {
					LogHelper.log(TAG, "Data got successfully");
					LogHelper.log(TAG, String.format("XML Url = [%s]", result.mResult.mXml_Data_Url));
					String atlasId = result.mResult.mAtlas_Id;
					String xmlDataUrl = result.mResult.mXml_Data_Url;
					String atlasVersion = result.mResult.mXml_Data_Version; 
					
					if (!TextUtils.isEmpty(xmlDataUrl)) {
						AtlasItem atlasItem = DBHelper.getInstance(mContext).saveAtlasData(atlasId, atlasVersion);	
						if (needToDownloadAtlasXml(atlasItem)) {
							downloadAtlasFile(robotId, atlasId, xmlDataUrl, listener);
						}
						else {
							listener.onAtlasDataDownloaded(atlasId, atlasItem.getXmlFilePath());
						}
					} 
					else {
						listener.onAtlasDataDownloadError(robotId, "Atlas does not exist");
					}
				} 
				else {
					listener.onAtlasDataDownloadError(robotId, "Atlas could not be retrieved");
					LogHelper.log(TAG, "Could not get atlas data");
				}	
				return;
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	// Return true if Xml file is exist in the cache directory
	private boolean needToDownloadAtlasXml(AtlasItem atlasItem) {
		boolean fileExist = false;
		if (!TextUtils.isEmpty(atlasItem.getXmlFilePath())) {
			fileExist = new File(atlasItem.getXmlFilePath()).exists();
		}
		
		return (!fileExist);
	}
	
	private void downloadAtlasFile(final String robotId, final String atlas_id, final String xmlDataUrl, final RobotAtlasDataDownloadListener listener) {

		LogHelper.log(TAG, "downloadAtlasFile called");
		final String atlasFilePath = FileCachePath.getAtlasMapXMLFilePath(mContext, robotId, atlas_id);

		FileDownloadHelper.downloadFile(mContext, xmlDataUrl, atlasFilePath, new FileDownloadListener() {
			
			@Override
			public void onDownloadError(String url) {
				listener.onAtlasDataDownloadError(atlas_id, "Download Error");
			}
			
			@Override
			public void onDownloadComplete(String url, String filePath) {
				DBHelper.getInstance(mContext).updateAtlasXmlFilePath(atlas_id, filePath);
				listener.onAtlasDataDownloaded(atlas_id, filePath);
			}
		});
	
	}
}
