package com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.database.DBHelper;
import com.neatorobotics.android.slide.framework.http.download.FileCachePath;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadHelper;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadListener;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.RobotAtlasWebservicesManager;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.listeners.RobotGridDataDownloadListener;

public class RobotAtlasGridWebservicesManager {
	private static final String TAG = RobotAtlasGridWebservicesManager.class.getSimpleName();
	private Context mContext;
	private static RobotAtlasGridWebservicesManager sRobotAtlasGridWebservicesManager;
	private static final Object INSTANCE_LOCK = new Object();

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

	//Always to be called in a secondary thread.
	public String getAtlasGridDataUrl(final String atlas_id, final String gridId) {

		if (TextUtils.isEmpty(atlas_id)) {
			return null;
		}
		GetAtlasGridDataResult result = RobotAtlasGridWebservicesHelper.getAtlasGridDataRequest(mContext, atlas_id);
		if (result.success()) {
			LogHelper.log(TAG, "grid data retrieved successfully");
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
	
	// Fetches grid data from the server based on atlas id 
	// and add/update into the DB  
	private List<GridData> getGridDataAndUpdateDB(String atlasId) {
		if (TextUtils.isEmpty(atlasId)) {
			return null;
		}
		
		List<GridData> grids = new ArrayList<GridData>();
		GetAtlasGridDataResult result = RobotAtlasGridWebservicesHelper.getAtlasGridDataRequest(mContext, atlasId);
		if (result.success()) {
			if ((result.mResult != null) && (result.mResult.length > 0)) {
				LogHelper.log(TAG, "getGridDataAndUpdateDB - " + result.mResult.length);
				
				for (GetAtlasGridDataResult.Result gridDataResult : result.mResult) {
					GridData data = new GridData(gridDataResult.mId_Grid, gridDataResult.mGrid_Data_Url, gridDataResult.mGrid_Data_Version);
					grids.add(data);
				}
			}
		}
		
		return grids;
	}
	
	public void getAtlasGridData(final String robotId, final String gridId, final RobotGridDataDownloadListener listener) {
   
		if (TextUtils.isEmpty(robotId)) {
			listener.onGridDataDownloadError("", "", "Robot Id is empty");
			return;
		}

		Runnable task = new Runnable() {
			@Override
			public void run() {
				String atlasId = RobotAtlasWebservicesManager.getInstance(mContext).getAtlasId(robotId);				
				List<GridData> grids = getGridDataAndUpdateDB(atlasId);
				boolean noGridData = true;
				if (grids.size() > 0) {					
					GridData gridData = getGridDataById(gridId, grids);
					if (!TextUtils.isEmpty(gridData.mDataUrl)) {
						noGridData = false;
						LogHelper.log(TAG, String.format("XML Url = [%s]", gridData.mDataUrl));
						GridItem gridItem = DBHelper.getInstance(mContext).saveGridData(gridData.mId, gridData.mDataVersion);
						if (needToDownloadGridData(gridItem)) {
							downloadGridFile(robotId, atlasId, gridData.mId, gridData.mDataUrl, listener);
						}
						else {
							listener.onGridDataDownloaded(atlasId, gridItem.getId(), gridItem.getDataFilePath());
						}
					}
				}
				
				if (noGridData) { // If grid data is not exist for the robot
					//TODO: right now sending empty strings as at this point we do not know gridId and we calculate the same
					listener.onGridDataDownloadError("", "", "No grid data exists");
					LogHelper.log(TAG, "Could not get grid data");
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	// TODO: Once we get correct input gridId we take only that id data. 
	// For now we take first item from the list
	private GridData getGridDataById(String gridId, List<GridData> grids) {
		GridData matchGridData = grids.get(0);
		
		if (!TextUtils.isEmpty(gridId)) {		
			for (GridData gridData : grids) {
				if (gridId.equals(gridData.mId)) {
					matchGridData = gridData;
					break;
				}
			}
		}
		
		return matchGridData;
	}
	
	// Return true if data file is exist in the cache directory
	private boolean needToDownloadGridData(GridItem gridItem) {
		boolean fileExist = false;
		if (!TextUtils.isEmpty(gridItem.getDataFilePath())) {
			fileExist = new File(gridItem.getDataFilePath()).exists();
		}
		
		return (!fileExist);
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
	
	private void downloadGridFile(final String robotId, final String atlas_id, final String gridId, final String xmlDataUrl, final RobotGridDataDownloadListener listener) {

		LogHelper.log(TAG, "downloadGridFile called");
		final String gridFilePath = FileCachePath.getAtlasGridFilePath(mContext, atlas_id, gridId);		
		
		FileDownloadHelper.downloadFile(mContext, xmlDataUrl, gridFilePath, new FileDownloadListener() {
			
			@Override
			public void onDownloadError(String url) {
				listener.onGridDataDownloadError(atlas_id, gridId, "Download Error");
			}
			
			@Override
			public void onDownloadComplete(String url, String filePath) {
				// Update path into the DB for the grid
				DBHelper.getInstance(mContext).updateGridDataFilePath(gridId, filePath);
				listener.onGridDataDownloaded(atlas_id, gridId, filePath);
			}
		});
	
	}

	private static class GridData {
		String mId;
		String mDataUrl;
		String mDataVersion;
		
		public GridData(String gridId, String dataUrl, String dataVersion) {
			mId = gridId;
			mDataUrl = dataUrl;
			mDataVersion = dataVersion;
		}
	}
}
