/**
 * FileDownloadHelper class
 * Helper class to download the file and save it into the file
 * Caller needs to pass the absolute file path where the file will be stored
 * 
 */
package com.neatorobotics.android.slide.framework.http.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import android.content.Context;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.FileUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpClient;

public class FileDownloadHelper {
	
	private static final String TAG = FileDownloadHelper.class.getSimpleName();
	private String mUrl;
	private String mDownloadFilePath;
	private WeakReference<FileDownloadListener> mListener;
	private Context mContext;
	private FileDownloadHelper(Context context, String url, String filePath, FileDownloadListener listener)
	{
		mUrl = url;
		mDownloadFilePath = filePath;
		mListener = new WeakReference<FileDownloadListener>(listener);
		mContext = context.getApplicationContext();
	}
	
	private void download()
	{
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				FileUtils.ensureFolderExists(mDownloadFilePath);
				
				HttpGet httpGet = new HttpGet(mUrl);
				HttpEntity responseEntity = null;
				InputStream responseInputStream = null;
				FileDownloadListener listener = null;
				boolean downloadSuccessful = false;

				try {
					final HttpResponse postHttpResponse = NeatoHttpClient.execute(mContext, httpGet);
					final int responseStatusCode = postHttpResponse.getStatusLine().getStatusCode();

					switch(responseStatusCode) {
					case HttpStatus.SC_OK:
						responseEntity = postHttpResponse.getEntity();
						responseInputStream = responseEntity.getContent();
						saveFile(responseInputStream, mDownloadFilePath);
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
					listener = mListener.get();
					if (listener != null) {
						if(downloadSuccessful) {
							listener.onDownloadComplete(mUrl, mDownloadFilePath);
						}
						else {
							listener.onDownloadError(mUrl);
						}
					}
				}
				
			}
		};
		
		TaskUtils.scheduleTask(task, 0);
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
	
	public static void downloadFile(Context context, String url, String filePath, FileDownloadListener listener)
	{
		FileDownloadHelper fileDownloadHelper = new FileDownloadHelper(context, url, filePath, listener);
		fileDownloadHelper.download();
	}

}
