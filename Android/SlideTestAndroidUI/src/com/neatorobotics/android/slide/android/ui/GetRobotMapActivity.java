package com.neatorobotics.android.slide.android.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.neatorobotics.android.slide.android.ui.image.ImageResizer;
import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.http.download.FileCachePath;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadHelper;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadListener;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadListenerWrapper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.map.GetNeatoRobotMapDataResult;
import com.neatorobotics.android.slide.framework.webservice.robot.map.GetNeatoRobotMapsResult;
import com.neatorobotics.android.slide.framework.webservice.robot.map.NeatoRobotMapWebservicesHelper;

public class GetRobotMapActivity extends Activity {
	private static final String TAG = "GetRobotMapActivity";
	
	private ProgressBar mProgressView;
	private ImageView mImgMapData;
	private TextView  mTxtMapData;
	
	private String mRobotSerialNo;
	
	private String mMapId;
	private String mBlobDataUrl;
	private String mXmlDataUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_map);
		
		mProgressView = (ProgressBar)findViewById(R.id.progress_getMap);
		
		mImgMapData = (ImageView) findViewById(R.id.img_map_image);
		mTxtMapData = (TextView) findViewById(R.id.txt_map_xml);
		mTxtMapData.setMovementMethod(new ScrollingMovementMethod());
		
		findViewById(R.id.btn_getmap).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clearMapDataView();
				getRobotMapDataAsync();
			}
		});
		
		
		RobotItem robotItem = RobotHelper.getManagedRobot(getApplicationContext());
		
		if (robotItem != null) {
			mRobotSerialNo = robotItem.getSerialNumber();
			String message = getString(R.string.text_getmap_hdr_title) + " " + mRobotSerialNo;
			((TextView)findViewById(R.id.txt_getmap_hdr)).setText(message);
		} else {
			String message = getString(R.string.text_getmap_hdr_title_default);
			((TextView)findViewById(R.id.txt_getmap_hdr)).setText(message);

		}
	}	
	
	
	private void showProgressView() {
		mProgressView.setVisibility(ProgressBar.VISIBLE);
	}
	
	private void hideProgressView() {
		mProgressView.setVisibility(ProgressBar.GONE);
	}
	
	private void clearMapDataView() {
		mTxtMapData.setText("");
		mTxtMapData.setText("Retrieving robot uploaded map data");		
		mImgMapData.setImageBitmap(null);
	}
	
	/*private void setMessage(String msg) {
		mTxtMapData.setText("");
		mTxtMapData.setText(msg);
	}*/
	
	private void getRobotMapDataAsync() {
		if (TextUtils.isEmpty(mRobotSerialNo)) {
			Toast.makeText(getApplicationContext(), "No Robot is associated", Toast.LENGTH_SHORT).show();
		}
		else {
			new FetchRobotMapDataTask().execute();
		}
	}
	
	private class FetchRobotMapDataTask extends AsyncTask<Void, Void, Boolean> {
		private boolean mNoMap = false;
		
		@Override
		protected void onPreExecute() {
			showProgressView();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {	
			LogHelper.log(TAG, "FetchRobotMapDataTask:doInBackground called");
			boolean success = false;
			
			boolean robotHasMap = getMapIdAndUpdatedVersionsAsync();
			if (robotHasMap) {				
				GetNeatoRobotMapDataResult result = NeatoRobotMapWebservicesHelper.getNeatoRobotMapDataRequest(getApplicationContext(), mMapId);
				
				if (result.success()) {
					success = true;					
					LogHelper.log(TAG, String.format("XML Url = [%s] Image URL = [%s]", 
							result.mResult.mXml_Data_Url, result.mResult.mBlob_Data_Url));
					
					mBlobDataUrl = result.mResult.mBlob_Data_Url;
					mXmlDataUrl = result.mResult.mXml_Data_Url;
				}
			}	
			else {
				mNoMap = true;
			}
			
			return success;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			LogHelper.log(TAG, "FetchRobotMapDataTask:onPostExecute - " + result);
			hideProgressView();
			if (result) {
				downloadMapDataAndShowAsync();
			}
			else if (mNoMap) {
				Toast.makeText(getApplicationContext(), getString(R.string.text_no_updated_map_msg), Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(getApplicationContext(), getString(R.string.text_getmap_failed_msg), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private Handler mHandler = new Handler();
	private void downloadMapDataAndShowAsync() {
		LogHelper.log(TAG, "downloadMapDataAndShowAsync called");
		showProgressView();
		
		FileDownloadListenerWrapper fileDownloadListener = new FileDownloadListenerWrapper(mHandler, mFileDownloadListener);	
		FileDownloadHelper.downloadFile(getApplicationContext(), mXmlDataUrl, getExtMapXMLFilePath(), fileDownloadListener);		
		FileDownloadHelper.downloadFile(getApplicationContext(), mBlobDataUrl, getExtImageFilePath(), fileDownloadListener);
	}
	
	private String getExtMapXMLFilePath() {
		return FileCachePath.getMapXMLFilePath(getApplicationContext(), mRobotSerialNo, mMapId);
	}
	
	private String getExtImageFilePath() {
		return FileCachePath.getImageFilePath(getApplicationContext(), mRobotSerialNo, mMapId);
	}
	
	private FileDownloadListener mFileDownloadListener = new FileDownloadListener() {
		
		@Override
		public void onDownloadError(String url) {
			Toast.makeText(getApplicationContext(), "Error in downloading map data", Toast.LENGTH_LONG).show();
			LogHelper.log(TAG, String.format("FileDownloadListener:onDownloadError - Url = [%s]", url));
			hideProgressView();
		}
		
		@Override
		public void onDownloadComplete(String url, final String filePath) {
			LogHelper.log(TAG, String.format("FileDownloadListener:onDownloadComplete - Url = [%s] File = [%s]", url, filePath));
			hideProgressView();
			
			if (url.equalsIgnoreCase(mXmlDataUrl)) {
				// displayMapXML(filePath);
			}
			else if (url.equalsIgnoreCase(mBlobDataUrl)){
				displayMapImage(filePath);
			}
		}
	};
	
	/*private void displayMapXML(String filePath) {
		LogHelper.log(TAG, "displayMapXML called");
		
		if (TextUtils.isEmpty(filePath)) {
			LogHelper.log(TAG, "displayMapXML XML file path is empty");
			return;
		}
		
		final String xmlContent = readFileAsString(filePath);					
			if (!TextUtils.isEmpty(xmlContent)) {
				mTxtMapData.setText(xmlContent);
			}
			else {
				mTxtMapData.setText("");
			}
	}*/
	
	/*public static String readFileAsString(String filePath) {
		LogHelper.log(TAG, "readFileAsString File - " + filePath);
		String content = null;
		try {
		    BufferedReader reader = new BufferedReader(new FileReader(filePath));
		    String line; 
		    StringBuilder builder = new StringBuilder();
		    
		    while( ( line = reader.readLine() ) != null){
		    	builder.append(line).append("\n");
		    }
		    reader.close();
		    
		    content = builder.toString();
		}
		catch (FileNotFoundException ex) {
			LogHelper.log(TAG, "FileNotFoundException - readFileAsString - " + filePath);
		}
		catch (IOException ex) {
			LogHelper.log(TAG, "FileNotFoundException - readFileAsString - " + filePath);
		}		
		
		return content;
	}*/
	
	private void displayMapImage(String filePath) {
		LogHelper.log(TAG, "displayMapImage called");
		if (TextUtils.isEmpty(filePath)) {
			LogHelper.log(TAG, "displayMapImage image file path is empty");
			return;
		}
		
		try {
			int newImgSize = getMapImageViewSize();
			Bitmap mapImgBitmap = ImageResizer.getScaleImage(filePath, newImgSize);
			if (mapImgBitmap != null) {
				mImgMapData.setImageBitmap(mapImgBitmap);
			}
			else {
				Toast.makeText(getApplicationContext(), "Could't load the downloaded map image", Toast.LENGTH_SHORT).show();
			}
			// mImgMapData.setImageBitmap(BitmapFactory.decodeFile(filePath));
		}
		catch (OutOfMemoryError ex) {
			LogHelper.log(TAG, "OutOfMemoryError - displayMapImage image is too big to load");
		}
	}
	
	private int getMapImageViewSize() {
		int width = mImgMapData.getMeasuredWidth();
		int height = mImgMapData.getMeasuredHeight();
		
		return (width > height) ? width : height;
	}
	
	// Fetching map Id and updated data versions from the server for associate robot
	// NOTE: Don't call this function on UI thread call it on secondary thread
	private boolean getMapIdAndUpdatedVersionsAsync() {
		LogHelper.log(TAG, "getMapIdAndUpdatedVersionsAsync called");
		boolean robotMapped = false;
		GetNeatoRobotMapsResult result = NeatoRobotMapWebservicesHelper.getNeatoRobotMapsRequest(getApplicationContext(), mRobotSerialNo);
		// Note: Here assume only one map id will get 
		if (result.success()) {				
			if ((result.mResult != null) && result.mResult.size() > 0) { 
				mMapId  = result.mResult.get(0).mId;
				robotMapped = true;
			}
		}
		else {
			mMapId = null;			
		}
		
		return robotMapped;
	}
}
