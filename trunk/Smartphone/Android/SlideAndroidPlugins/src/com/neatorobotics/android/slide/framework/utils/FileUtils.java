package com.neatorobotics.android.slide.framework.utils;

import java.io.File;

public class FileUtils {

	public static boolean ensureFolderExists(String fileName)
	{
		File file = new File(fileName);
		if (file.exists()) {
			return true;
		}
		
		if (file.isDirectory()) {
			return file.mkdirs();
		}
		
		File parentDir = file.getParentFile();
		if(parentDir != null) {
			return parentDir.mkdirs();
		}
		return false;
	}
	
}
