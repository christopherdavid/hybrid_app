package com.neatorobotics.android.slide.framework.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
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

}
