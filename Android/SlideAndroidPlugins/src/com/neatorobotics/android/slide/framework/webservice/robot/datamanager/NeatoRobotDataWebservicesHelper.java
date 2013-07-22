package com.neatorobotics.android.slide.framework.webservice.robot.datamanager;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.MobileWebServiceClient;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.DeleteRobotProfileKey;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.GetRobotPresenceStatus;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.GetRobotProfileDetails2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails3;


public class NeatoRobotDataWebservicesHelper {
	
	private static final String PROFILE_KEY_FORMAT = "%s[%s]";
	


	public static SetRobotProfileDetailsResult3 setRobotProfileDetailsRequest3(Context context, String robotId, HashMap<String, String> profileDetailsParams) 
			throws UserUnauthorizedException, NeatoServerException, IOException {		
		
		Map<String, String> robotSetDetailParams = new HashMap<String, String>();
		robotSetDetailParams.put(SetRobotProfileDetails3.Attribute.SERIAL_NUMBER, robotId);
		robotSetDetailParams.put(SetRobotProfileDetails3.Attribute.SOURCE_SMARTAPP_ID, NeatoPrefs.getUserEmailId(context));
		robotSetDetailParams.put(SetRobotProfileDetails3.Attribute.CAUSING_AGENT_ID, NeatoPrefs.getNeatoUserDeviceId(context));
		robotSetDetailParams.put(DeleteRobotProfileKey.Attribute.NOTIFICATION_FLAG, DeleteRobotProfileKey.DATA_CHANGED_NOTIFICATION_FLAG_ON);
		robotSetDetailParams.putAll(addProfilePrefix(profileDetailsParams));
		String response = MobileWebServiceClient.executeHttpPost(context, SetRobotProfileDetails3.METHOD_NAME, robotSetDetailParams);
		return AppUtils.checkResponseResult(response, SetRobotProfileDetailsResult3.class);
	}
	
	public static GetRobotPresenceStatusResult getRobotPresenceStatus (Context context, String robotId) 
			throws UserUnauthorizedException, NeatoServerException, IOException {		
		Map<String, String> robotGetRobotPresenceStatusParams = new HashMap<String, String>();
		robotGetRobotPresenceStatusParams.put(GetRobotPresenceStatus.Attribute.SERIAL_NUMBER, robotId);
		String response = MobileWebServiceClient.executeHttpPost(context, GetRobotPresenceStatus.METHOD_NAME, robotGetRobotPresenceStatusParams);
		return AppUtils.checkResponseResult(response, GetRobotPresenceStatusResult.class);
	}
	
	public static GetRobotProfileDetailsResult2 getRobotProfileDetailsRequest2 (Context context, String robotId, String key) 
			throws UserUnauthorizedException, NeatoServerException, IOException {		
		Map<String, String> getRobotProfileParams = new HashMap<String, String>();
		getRobotProfileParams.put(GetRobotProfileDetails2.Attribute.SERIAL_NUMBER, robotId);
		if (!TextUtils.isEmpty(key)) {
			getRobotProfileParams.put(GetRobotProfileDetails2.Attribute.KEY, key);
		}
		String response = MobileWebServiceClient.executeHttpPost(context, GetRobotProfileDetails2.METHOD_NAME, getRobotProfileParams);
		return AppUtils.checkResponseResult(response, GetRobotProfileDetailsResult2.class);
	}
	
	private static HashMap<String, String> addProfilePrefix(HashMap<String, String> profileParams) {
		HashMap<String, String> profile = new HashMap<String, String>();
		for (HashMap.Entry<String, String> entry : profileParams.entrySet()) {
			String keyWithProfilePrefix = getProfileKey(entry.getKey());
			profile.put(keyWithProfilePrefix, entry.getValue());	        		        
		}
		return profile;
	}

	private static String getProfileKey(String key) {
		String keyWithProfilePrefix = String.format(PROFILE_KEY_FORMAT, SetRobotProfileDetails3.Attribute.PROFILE, key);
		return keyWithProfilePrefix;
	}
	
	public static DeleteRobotProfileKeyResult deleteRobotProfileKey(Context context, String robotId, String key, boolean notify) 
			throws UserUnauthorizedException, NeatoServerException, IOException {		
		
		Map<String, String> robotDeleteKeyParams = new HashMap<String, String>();
		robotDeleteKeyParams.put(DeleteRobotProfileKey.Attribute.SERIAL_NUMBER, robotId);
		robotDeleteKeyParams.put(DeleteRobotProfileKey.Attribute.PROFILE_KEY, key);
		robotDeleteKeyParams.put(DeleteRobotProfileKey.Attribute.CAUSING_AGENT_ID, NeatoPrefs.getNeatoUserDeviceId(context));
		if (notify) {
			robotDeleteKeyParams.put(DeleteRobotProfileKey.Attribute.NOTIFICATION_FLAG, DeleteRobotProfileKey.DATA_CHANGED_NOTIFICATION_FLAG_ON);
		}
		else {
			robotDeleteKeyParams.put(DeleteRobotProfileKey.Attribute.NOTIFICATION_FLAG, DeleteRobotProfileKey.DATA_CHANGED_NOTIFICATION_FLAG_OFF);
		}
		String response = MobileWebServiceClient.executeHttpPost(context, DeleteRobotProfileKey.METHOD_NAME, robotDeleteKeyParams);
		return AppUtils.checkResponseResult(response, DeleteRobotProfileKeyResult.class);
	}
	
	public static SetRobotProfileDetailsResult3 resetRobotProfileValue(Context context, String robotId, String... keys) throws UserUnauthorizedException, 
	NeatoServerException, IOException {
		HashMap<String, String> robotStateChangeMap = new HashMap<String, String>();
		for (String key : keys) {
			robotStateChangeMap.put(key, "");
		}
		return setRobotProfileDetailsRequest3(context, robotId, robotStateChangeMap);
	}
}
