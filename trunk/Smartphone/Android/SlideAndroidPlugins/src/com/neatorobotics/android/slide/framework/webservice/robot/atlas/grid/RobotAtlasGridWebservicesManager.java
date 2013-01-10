package com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid;

import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Time;

import com.neatorobotics.android.slide.framework.http.download.FileDownloadHelper;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadListener;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.RobotAtlasWebservicesManager;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.listeners.RobotGridDataDownloadListener;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.listeners.RobotAtlasDataDownloadListener;

public class RobotAtlasGridWebservicesManager {


	private static final String TAG = RobotAtlasGridWebservicesManager.class.getSimpleName();
	private Context mContext;
	private static RobotAtlasGridWebservicesManager sRobotAtlasGridWebservicesManager;
	private static final Object INSTANCE_LOCK = new Object(); 
	private static final String GRID_FILE_NAME = "grid.xml";
	private static final String ROBOT_MAP_CACHE_DATA_DIR = "/neato/map_data/";


	private RobotAtlasGridWebservicesManager(Context context)
	{
		mContext = context.getApplicationContext();
	}

	public static RobotAtlasGridWebservicesManager getInstance(Context context)
	{
		synchronized (INSTANCE_LOCK) {
			if (sRobotAtlasGridWebservicesManager == null) {
				sRobotAtlasGridWebservicesManager = new RobotAtlasGridWebservicesManager(context);
			}
		}
		return sRobotAtlasGridWebservicesManager;
	}

	private String getAtlasGridFilePath(String atlasId, String gridId) {

		if ((atlasId == null) || (gridId == null)) { 
			return null;
		}
		Date d = new Date();
		//TODO: find a better way to timestamp so that the fils have different names. There
		// are chances that if the image is cached then even if the robot grid is changed, but 
		// the file name is same, we get the old grid in JS.
		StringBuilder builder = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath()).
				append(ROBOT_MAP_CACHE_DATA_DIR).append(atlasId).
				append("/").append(GRID_FILE_NAME).append("_").append(d.getMinutes()).append(d.getSeconds());
		return builder.toString();
	}


	//Always to be called in a secondary thread.
	public String getAtlasGridDataUrl(final String atlas_id, final String gridId) {

		if (TextUtils.isEmpty(atlas_id)) {
			return null;
		}
		GetAtlasGridDataResult result = RobotAtlasGridWebservicesHelper.getAtlasGridDataRequest(mContext, atlas_id);
		if (result.success()) {
			LogHelper.log(TAG, "grid  set retrieved successfully");
			//TODO: Once we get the correct grid_id from the plugin method, replace the code with the below code
			/*for (int i=0; i < result.mResult.length; i++) {
				if(result.mResult[i].mId_Grid.equals(gridId)) {
					return result.mResult[i].mGrid_Data_Url;
				}
			}
			*/
			return result.mResult[0].mGrid_Data_Url;
		} 
		else {
			//TODO:
			LogHelper.log(TAG, "Could not get grid ");
		}	
		return null;
	}

	public void getAtlasGridData(final String robotId, final String gridId, final RobotGridDataDownloadListener listener) {
   
		if (TextUtils.isEmpty(robotId)) {
			listener.onGridDataDownloadError("", "", "Robot Id is empty");
			return;
		}

		Runnable task = new Runnable() {
			@Override
			public void run() {	
				//TODO: Save the atlas id and retrieve that from the database.
				String atlasId = RobotAtlasWebservicesManager.getInstance(mContext).getAtlasId(robotId);
				String gridUrl = getAtlasGridDataUrl(atlasId, gridId);
				if (!TextUtils.isEmpty(gridUrl)) {
					LogHelper.log(TAG, String.format("XML Url = [%s]", gridUrl));
					downloadGridFile(atlasId, gridId, gridUrl, listener);
				} else {
					//TODO: right now sending empty strings as at this point we do not know gridId and we calculate the same
					listener.onGridDataDownloadError("", "", "No grid data exists");
					LogHelper.log(TAG, "Could not get grid data");
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	//TODO: add listener.
	public void postGridImage(final String atlas_id, final String grid_id, final String blob_data) {

		if (TextUtils.isEmpty(atlas_id)) {
			return;
		}
		Runnable task = new Runnable() {
			@Override
			public void run() {	
				PostGridImageResult result = RobotAtlasGridWebservicesHelper.postGridImageRequest(mContext, atlas_id, grid_id, blob_data);
				if (result.success()) {
					LogHelper.log(TAG, "posted grid image successfully");
				} 
				else {
					LogHelper.log(TAG, "Could not post grid image data");
				}	
				return;
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	//TODO: add listener.
	public void updateGridImage(final String atlas_id, final String grid_id, final String blob_data) {

		if (TextUtils.isEmpty(atlas_id)) {
			return;
		}
		Runnable task = new Runnable() {
			@Override
			public void run() {	
				UpdateGridImageResult result = RobotAtlasGridWebservicesHelper.updateGridImageRequest(mContext, atlas_id, grid_id, blob_data);
				if (result.success()) {
					LogHelper.log(TAG, "updated grid image successfully");
				} 
				else {
					LogHelper.log(TAG, "Could not update grid data");
				}	
				return;
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	private void downloadGridFile(final String atlas_id, final String gridId, final String xmlDataUrl, final RobotGridDataDownloadListener listener) {

		LogHelper.log(TAG, "downloadGridFile called");
		final String atlasFilePath = getAtlasGridFilePath(atlas_id, gridId);

		FileDownloadHelper.downloadFile(mContext, xmlDataUrl, atlasFilePath, new FileDownloadListener() {
			
			@Override
			public void onDownloadError(String url) {
				listener.onGridDataDownloadError(atlas_id, gridId, "Download Error");
			}
			
			@Override
			public void onDownloadComplete(String url, String filePath) {
				listener.onGridDataDownloaded(atlas_id, gridId, filePath);
			}
		});
	
	}

}
