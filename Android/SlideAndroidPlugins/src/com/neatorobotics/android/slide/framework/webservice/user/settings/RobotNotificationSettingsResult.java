package com.neatorobotics.android.slide.framework.webservice.user.settings;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class RobotNotificationSettingsResult extends NeatoWebserviceResult {
	private static final String TAG = RobotNotificationSettingsResult.class.getSimpleName();
	
	public Result result;
	
	public static class Result {
		public boolean global;
		public ArrayList<Notification> notifications;
	}
	
	public static class Notification {
		public String key;
		public boolean value;
	}
	
	public JSONObject getNotificationsJson() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(JsonMapKeys.KEY_GLOBAL_NOTIFICATIONS, result.global);
			
			JSONArray notificationsArray = new JSONArray();
			for (Notification notification : result.notifications) {
				JSONObject notificationObj = new JSONObject();
				notificationObj.put(JsonMapKeys.KEY_NOTIFICATION_KEY, notification.key);
				notificationObj.put(JsonMapKeys.KEY_NOTIFICATION_VALUE, notification.value);
				notificationsArray.put(notificationObj);
			} 
			jsonObject.put(JsonMapKeys.KEY_NOTIFICATIONS, notificationsArray);
		}
		catch(JSONException ex) {	
			LogHelper.logD(TAG, "JSONException in getNotificationsJson", ex);
		}
		
		return jsonObject;
	}
	
	public boolean isNotificationEnable(String notificationId) {
		boolean enable = false;
		if (notificationId.equals(RobotCommandPacketConstants.NOTIFICATIONS_ID_GLOBAL)) {
			enable = result.global;
		}	
		else {
			for (Notification notification : result.notifications) {				
				if (notification.key.equals(notificationId)) {
					enable = notification.value;
					break;
				}
			}
		}
		
		return enable;
	}
}
