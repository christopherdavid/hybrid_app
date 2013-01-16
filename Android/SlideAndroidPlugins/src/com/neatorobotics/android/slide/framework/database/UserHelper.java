package com.neatorobotics.android.slide.framework.database;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;

public class UserHelper {
	// private static final String LOCAL_TAG = "UserHelper";
	
	public static boolean saveUserDetails(Context context, UserItem userItem) {
		boolean result = false;
		
		if (userItem != null) {
			result = DBHelper.getInstance(context).saveUser(userItem);			
		}		
		
		return result;
	}
	
	public static boolean saveLoggedInUserDetails(Context context, UserItem userItem, String authKey) {
		boolean result = saveUserDetails(context, userItem);
		if (result) {
			NeatoPrefs.saveUserEmailId(context, userItem.getEmail());
			NeatoPrefs.saveNeatoUserAuthToken(context, authKey);
		}
		
		return result;
	}
	
	public static UserItem getLoggedInUserDetails(Context context) {
		UserItem userDetails = null;
		
		if (isUserLoggedIn(context)) {
			userDetails = DBHelper.getInstance(context).getUserByEmail(NeatoPrefs.getUserEmailId(context));
		}
		
		return userDetails;
	}
	
	public static String getChatId(Context context) {
		UserItem userDetails = getLoggedInUserDetails(context);
		if (userDetails != null) {
			return userDetails.getChatId();
		}
		
		return null;
	}
	
	public static String getChatPwd(Context context) {
		UserItem userDetails = getLoggedInUserDetails(context);
		if (userDetails != null) {
			return userDetails.getChatPwd();
		}
		
		return null;
	}
	
	public static void logout(Context context) {
		if (isUserLoggedIn(context)) {
			String email = NeatoPrefs.getUserEmailId(context);
			DBHelper dbHelper = DBHelper.getInstance(context);
			dbHelper.deleteUserByEmail(email);
			dbHelper.clearAllAssociatedRobots();
			NeatoPrefs.clearUserEmailAndAuthToken(context);
			NeatoPrefs.clearManagedRobotSerialId(context);
		}
	}
	
	public static boolean isUserLoggedIn(Context context) {
		String emailId = NeatoPrefs.getUserEmailId(context);
		if (!TextUtils.isEmpty(emailId)) {
			String authToken = NeatoPrefs.getNeatoUserAuthToken(context);
			if (!TextUtils.isEmpty(authToken)) { 
				return true;
			}
		}
		
		return false;		
	}
}
