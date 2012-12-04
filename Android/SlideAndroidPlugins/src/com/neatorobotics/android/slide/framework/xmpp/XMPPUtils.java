package com.neatorobotics.android.slide.framework.xmpp;

import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;

public class XMPPUtils {
	private static final String TAG = XMPPUtils.class.getSimpleName();

	public static  String getRobotJabberId(Context context)
	{
		RobotItem robotItem = NeatoPrefs.getRobotItem(context);
		if (robotItem == null) {
			LogHelper.log(TAG, "Robot item is null. returning null chat id");
			return null;
		}
		else {
			String chatId = robotItem.getChatId();
			LogHelper.log(TAG, "Robot Chat id = " + chatId);
			return chatId;
		}
	}

/*
	public static String getRobotJabberPwd(Context context)
	{
		RobotItem robotItem = NeatoPrefs.getRobotItem(context);
		if (robotItem == null) {
			String chatPwd = AppConstants.JABBER_ROBOT_PWD;
			LogHelper.log(TAG, "Hardcoded Robot Chat Pwd = " + chatPwd);
			return chatPwd;
		}
		else {
			String chatPwd = robotItem.getChatPwd();
			LogHelper.log(TAG, "Robot Chat Pwd = " + chatPwd);
			return chatPwd;
		}
	}

	*/
	//Not needed.
//	public static String getRobotJabberIdWithoutDomain(Context context) {
//		return removeJabberDomain(getRobotJabberId(context));
//
//	}

	public static String removeJabberDomain(String chat_id) {
		
		String userId = chat_id;
		if (TextUtils.isEmpty(chat_id)) {
			return chat_id;
		}
		
		int index = chat_id.indexOf('@');
		if (index != -1) {
			userId = chat_id.substring(0, index);
		}
		return userId;

	}
	public static String  appendJabberDomain(String user_id) {
		return user_id + AppConstants.JABBER_ID_DOMAIN;
	}

}
