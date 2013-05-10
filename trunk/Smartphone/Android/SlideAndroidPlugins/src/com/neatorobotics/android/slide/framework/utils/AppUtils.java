package com.neatorobotics.android.slide.framework.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceUtils;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Base64;

public class AppUtils {
	
	// Public static helper function to return the Version number of the application.
	// This returns the version number as specified in AndroidManifest.xml
	// typically this is "x.x.xx.xx" format
	public static String getVersionWithBuildNumber(Context context)
	{
		try {
	    	String packageName = context.getPackageName();
	    	String version = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
	    	return version;
    	}
    	catch (NameNotFoundException e) {
    	}
    	return "";
	}
	
	// Public static helper function to return the Version number of the application.
	// This returns the version number after removing the build information. It means returns
	// only major version and minor version
	// typically this is "x.xx" format
	public static String getVersion(Context context)
	{
		String appVersion = getVersionWithBuildNumber(context);
		appVersion = removeBuildNumber(appVersion);
		return appVersion;
	}
	
	// Private static helper method to remove the Build number information
	// from the application version
	private static String removeBuildNumber(String version)
	{
		int majorVersionPositionDot = version.indexOf('.');
		if (majorVersionPositionDot >= 0) {
			int minorVersionPositionDot = version.indexOf('.', majorVersionPositionDot + 1);
			if (minorVersionPositionDot >= 0) {
				version = version.substring(0, minorVersionPositionDot);
			}
		}
		return version;
	}
	
	public static String convertToBase64(byte [] data)
	{
		String encodedString = Base64.encodeToString(data, Base64.DEFAULT);
		return encodedString;
	}
	
	public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
	 }
	 
	public static String generateNewRequestId(Context context)
	{
		String requestId = UUID.randomUUID().toString();
		return requestId;
	}
	

	private static String generateUuid()
	{
		return UUID.randomUUID().toString();
	}
	
	public static String generateNewScheduleEventId() 
	{
		String eventId = generateUuid();
		return eventId;
	}

	public static String generateScheduleUUId() 
	{
		String scheduleId = generateUuid();
		return scheduleId;
	}

	public static String getLoggedInUserId(Context context)
	{
		String userId = null;
		UserItem userItem = UserHelper.getLoggedInUserDetails(context);
		if (userItem != null) {
			userId = userItem.id;
		}
		return userId;
	}
	
	public static <T extends NeatoWebserviceResult> T  checkResponseResult(String response, Class<T> responseClassType) throws NeatoServerException {
		T result = NeatoWebserviceUtils.readValueHelper(response, responseClassType);
		if (!result.success()) {
			throw new NeatoServerException(result.mResponseStatus, result.message);
		}
		return result;
	}
	
	public static void addToJsonObjectIfNotEmpty(JSONObject jsonObject, String jsonKey, String value) {
		try {
			if (!TextUtils.isEmpty(value)) {
				jsonObject.put(jsonKey, value);
			}
		}
		catch (JSONException e) {
			
		}
		
	}
}
