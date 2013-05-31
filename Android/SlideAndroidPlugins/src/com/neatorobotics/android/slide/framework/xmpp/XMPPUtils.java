package com.neatorobotics.android.slide.framework.xmpp;

import android.content.Context;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;

public class XMPPUtils {
	private static final String TAG = XMPPUtils.class.getSimpleName();

	public static  String getRobotChatId(Context context, String robotId)
	{
		LogHelper.log(TAG, "robotId = " + robotId);
		RobotItem robotItem = RobotHelper.getRobotItem(context, robotId);
		if (robotItem == null) {
			LogHelper.log(TAG, "Robot item is null. returning null chat id");
			return null;
		}
		else {
			String chatId = robotItem.chat_id;
			LogHelper.log(TAG, "Robot Chat id = " + chatId);
			return chatId;
		}
	}

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
	
	//Used to see if the message is incoming from the given robotId
	public static boolean isRobotChatId(Context context, String from, String robotId) {
		String robotChatId = getRobotChatId(context, robotId);
		if ((!TextUtils.isEmpty(robotChatId)) && (from.equals(robotChatId))) {
			return true;
		}
		return false;
	}
}
