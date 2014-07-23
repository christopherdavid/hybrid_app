package com.neatorobotics.android.slide.framework.database;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserValidationHelper;

public class UserHelper {
    private static final String TAG = UserHelper.class.getSimpleName();

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
            NeatoPrefs.saveUserEmailId(context, userItem.email);
            NeatoPrefs.saveNeatoUserAuthToken(context, authKey);
            NeatoPrefs.saveNeatoUserValidationStatus(context, userItem.validation_status);
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
            return userDetails.chat_id;
        }

        return null;
    }

    public static String getChatPwd(Context context) {
        UserItem userDetails = getLoggedInUserDetails(context);
        if (userDetails != null) {
            return userDetails.chat_pwd;
        }

        return null;
    }

    public static void logout(Context context) {
        String email = NeatoPrefs.getUserEmailId(context);
        DBHelper dbHelper = DBHelper.getInstance(context);
        dbHelper.deleteUserByEmail(email);
        dbHelper.clearAllData();
        NeatoPrefs.clearPreferences(context);
    }

    public static boolean isUserLoggedIn(Context context) {
        String emailId = NeatoPrefs.getUserEmailId(context);
        if (!TextUtils.isEmpty(emailId)) {
            String authToken = NeatoPrefs.getNeatoUserAuthToken(context);
            if (!TextUtils.isEmpty(authToken)) {
                int validationStatus = NeatoPrefs.getNeatoUserValidationStatus(context,
                        UserValidationHelper.VALIDATION_STATUS_UNKNOWN);
				
                if (validationStatus == UserValidationHelper.USER_VALIDATION_STATUS_VALIDATED) {
                    return true;
                }
				LogHelper.log(TAG, "User email is NOT validated");
            }
        }
        return false;
    }
}
