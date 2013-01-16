package com.neatorobotics.android.slide.framework.http.download;

import java.io.File;

import android.content.Context;
import android.text.TextUtils;

/*
 * This class exposed cache file path helper functions for the robot atlas, 
 * map & schedule data
 * TODO: Handle a case if no external directory exist  
 */
public class FileCachePath {
	
	// Atlas data path constants
	private static final String ROBOT_ATLAS_CACHE_DATA_DIR = "/atlas_data/";
	private static final String ATLAS_FILE_NAME = "atlas.xml";
	private static final String GRID_FILE_NAME = "grid.xml";
	
	// Map data path constants
	private static final String ROBOT_MAP_CACHE_DATA_DIR = "/map_data/";
	private static final String OVERLAY_FILE_NAME = "overlayFile.xml";
	private static final String MAP_IMAGE_FILE_NAME = "image.jpg";
	
	// Schedule data path constants	
	private static final String ROBOT_SCHEDULE_CACHE_DATA_DIR = "/schedule_data/";
	private static final String SCHEDULE_DATA_FILE_NAME = "data.xml";
	
	/*
	 * Returns the path of the directory path holding application cache files 
	 * on external storage
	 */
	public static String getExtCahceBaseDir(Context context) {
		File file = context.getExternalCacheDir();
		if (file != null) {
			return file.getAbsolutePath();
		}
		
		return null;
	}
	
	public static String getAtlasMapXMLFilePath(Context context, String robotSerialNo, String atlasId) {
		if (TextUtils.isEmpty(atlasId) || TextUtils.isEmpty(robotSerialNo)) { 
			return null;
		}
		
		String cacheBaseDir = getExtCahceBaseDir(context);
		if (!TextUtils.isEmpty(cacheBaseDir)) {
			StringBuilder builder = new StringBuilder(cacheBaseDir).append(ROBOT_ATLAS_CACHE_DATA_DIR).
					append(atlasId).append("_").append(robotSerialNo).
					append(File.separator).append(ATLAS_FILE_NAME);
			return builder.toString();
		}
		
		return null;
	}
	
	public static String getAtlasGridFilePath(Context context, String robotSerialNo, String atlasId) {
		if (TextUtils.isEmpty(atlasId) || TextUtils.isEmpty(robotSerialNo)) { 
			return null;
		}
		
		String cacheBaseDir = getExtCahceBaseDir(context);
		if (!TextUtils.isEmpty(cacheBaseDir)) {
			StringBuilder builder = new StringBuilder(cacheBaseDir).append(ROBOT_ATLAS_CACHE_DATA_DIR).
							append(atlasId).append("_").append(robotSerialNo).
							append(File.separator).append(GRID_FILE_NAME);
		
			return builder.toString();
		}
		
		return null;
	}
	
	public static String getImageFilePath(Context context, String robotSerialNo, String mapId) {
		if (TextUtils.isEmpty(mapId) || TextUtils.isEmpty(robotSerialNo)) { 
			return null;
		}
		
		String cacheBaseDir = getExtCahceBaseDir(context);
		if (!TextUtils.isEmpty(cacheBaseDir)) {
			StringBuilder builder = new StringBuilder(cacheBaseDir).append(ROBOT_MAP_CACHE_DATA_DIR).
			append(mapId).append("_").append(robotSerialNo).append(File.separator).append(MAP_IMAGE_FILE_NAME);
	
			return builder.toString();
		}
		
		return null;
	}
	
	public static String getMapXMLFilePath(Context context, String robotSerialNo, String mapId) {
		if (TextUtils.isEmpty(mapId) || TextUtils.isEmpty(robotSerialNo)) { 
			return null;
		}
		
		String cacheBaseDir = getExtCahceBaseDir(context);
		if (!TextUtils.isEmpty(cacheBaseDir)) {
			StringBuilder builder = new StringBuilder(cacheBaseDir).
					append(ROBOT_MAP_CACHE_DATA_DIR).append(mapId).append("_").append(robotSerialNo).
					append(File.separator).append(OVERLAY_FILE_NAME);
			return builder.toString();
		}
		
		return null;
	}
	
	public static String getScheduleDataFilePath(Context context, String robotSerialNo, String scheduleId) {
		if (TextUtils.isEmpty(scheduleId) || TextUtils.isEmpty(robotSerialNo)) { 
			return null;
		}
		
		String cacheBaseDir = getExtCahceBaseDir(context);
		if (!TextUtils.isEmpty(cacheBaseDir)) {
			StringBuilder builder = new StringBuilder(cacheBaseDir).append(ROBOT_SCHEDULE_CACHE_DATA_DIR).
									append(scheduleId).append("_").append(robotSerialNo).
									append(File.separator).append(SCHEDULE_DATA_FILE_NAME);
			
			return builder.toString();
		}
		
		return null;
	}
}
