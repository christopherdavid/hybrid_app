package com.neatorobotics.android.slide.android.ui.image;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class ImageResizer {
	private static final String TAG = "ImageResizer";
		
	private static Bitmap loadImage(String imagePath, BitmapFactory.Options options) {
		Bitmap bitmapImg = null;		
		try {
			if (options != null) {
				bitmapImg = BitmapFactory.decodeFile(imagePath, options);
			}
			else {
				bitmapImg = BitmapFactory.decodeFile(imagePath);
			}
		}
		catch (OutOfMemoryError ex) {
			LogHelper.log(TAG, "loadImage - Image too larger to load", ex);
			return null;
		}
		
		return bitmapImg;
	}
	
	public static Bitmap getScaleImage(String imagePath, int scaleSize)  {
		LogHelper.log(TAG, "getResizedImage - Image file path - " + imagePath);
		
		if (TextUtils.isEmpty(imagePath)) {
			LogHelper.log(TAG, "getResizedImage - Invalid image path");
			return null; 
		}
		
		File imageFile = new File(imagePath);
		if (!imageFile.exists()) {
			LogHelper.log(TAG, String.format("getResizedImage - Image file [%s] not found ", imagePath));			
			return null;			
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmapImage = loadImage(imagePath, options);
		
		int imgWidth = options.outWidth;
		int imgHeight = options.outHeight;
		
		if ((imgWidth == 0) && (imgHeight == 0)) {
			LogHelper.log(TAG, "getResizedImage - Invalid image file");
			return null;
		}
		options = new BitmapFactory.Options();
		int imageDimension = (imgWidth > imgHeight)? imgWidth:imgHeight;		
		
		int size = Math.round((float)imageDimension / (float)scaleSize);
		options.inSampleSize = (int) Math.pow(2, Math.ceil(Math.log(size)/Math.log(2)));
		
		bitmapImage = loadImage(imagePath, options);		
		return bitmapImage;
	}
}
