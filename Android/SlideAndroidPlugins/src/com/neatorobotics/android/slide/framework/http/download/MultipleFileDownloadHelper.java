package com.neatorobotics.android.slide.framework.http.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.FileUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpClient;

public class MultipleFileDownloadHelper {

	private static final String TAG = MultipleFileDownloadHelper.class.getSimpleName();
			
	private ArrayList<FileDownloadWorkItem> mFileDownloadWorkItemList = new ArrayList<FileDownloadWorkItem>();
	private int mRetryCount = 0;
	private static final int MAX_RETRY_COUNT = 2;
	private Context mContext;
	private MultipleFileDownloadListener mListener;
	
	private MultipleFileDownloadHelper(Context context, MultipleFileDownloadListener listener)
	{
		mContext = context.getApplicationContext();
		mListener = listener;
	}
	
	private void addWorkItem(FileDownloadWorkItem item)
	{
		mFileDownloadWorkItemList.add(item);
	}
	
	private void addWorkItem(List<FileDownloadWorkItem> items)
	{
		mFileDownloadWorkItemList.addAll(items);
	}
	
	private void download()
	{
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				int count = mFileDownloadWorkItemList.size();
				for (int i = 0; i < count; i++) {
					mRetryCount = 0;
					FileDownloadWorkItem item = mFileDownloadWorkItemList.get(i);
					boolean success = false;
					do {
						success = downloadHelper(item.getUrl(), item.getFileName());
						if (success) {
							break;
						}
						mRetryCount++;
					} while (mRetryCount < MAX_RETRY_COUNT);
					int status = (success) ? 0:-1;
					item.setStatus(status);
				}
				
				if (mListener != null) {
					mListener.onDownloadComplete(mFileDownloadWorkItemList);
				}
			}
			
		};
		
		TaskUtils.scheduleTask(task, 0);
	}
	
	private boolean downloadHelper(String url, String fileName) {
		FileUtils.ensureFolderExists(fileName);
		
		HttpGet httpGet = new HttpGet(url);
		HttpEntity responseEntity = null;
		InputStream responseInputStream = null;
		boolean downloadSuccessful = false;

		try {
			final HttpResponse postHttpResponse = NeatoHttpClient.execute(mContext, httpGet);
			final int responseStatusCode = postHttpResponse.getStatusLine().getStatusCode();

			switch(responseStatusCode) {
			case HttpStatus.SC_OK:
				responseEntity = postHttpResponse.getEntity();
				responseInputStream = responseEntity.getContent();
				saveFile(responseInputStream, fileName);
				downloadSuccessful = true;
				break;
			default:	
				break;
			}	    

		} 
		catch (UnsupportedEncodingException e) {
			LogHelper.log(TAG, "Exception in doing HTTP Post request", e);
		}
		catch (IllegalStateException e) {
			LogHelper.log(TAG, "Exception in doing HTTP Post request", e);

		} catch (IOException e) {
			LogHelper.log(TAG, "Exception in doing HTTP Post request", e);
		}
		finally {
			
		}
		
		return downloadSuccessful;
	}
	
	
	private static final int BUFFER_SIZE = 16 * 1024;
	private void saveFile(InputStream is, String filePath) throws IOException
	{
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		
		try {
			byte [] data = new byte[BUFFER_SIZE];
			int len = 0;
			while((len = is.read(data)) > 0) {
				fos.write(data, 0, len);
			}
		}
		finally {
			if (fos != null) {
				fos.close();
			}
		}
		
	}
	
	public static void startDownload(Context context, List<FileDownloadWorkItem> items, MultipleFileDownloadListener listener)
	{
		MultipleFileDownloadHelper multipleFileDownloadHelper = new MultipleFileDownloadHelper(context, listener);
		multipleFileDownloadHelper.addWorkItem(items);
		multipleFileDownloadHelper.download();
	}
	
	public static void startDownload(Context context, FileDownloadWorkItem item, MultipleFileDownloadListener listener)
	{
		MultipleFileDownloadHelper multipleFileDownloadHelper = new MultipleFileDownloadHelper(context, listener);
		multipleFileDownloadHelper.addWorkItem(item);
		multipleFileDownloadHelper.download();
	}
	
}
