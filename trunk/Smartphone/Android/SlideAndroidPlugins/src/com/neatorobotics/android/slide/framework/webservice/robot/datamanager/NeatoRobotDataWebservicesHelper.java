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
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.GetRobotProfileDetails2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails2;


public class NeatoRobotDataWebservicesHelper {
	
	public static SetRobotProfileDetailsResult2 setRobotProfileDetailsRequest2(Context context, String robotId, HashMap<String, String> profileDetailsParams) 
			throws UserUnauthorizedException, NeatoServerException, IOException {		
		
		Map<String, String> robotSetDetailParams = new HashMap<String, String>();
		robotSetDetailParams.put(SetRobotProfileDetails2.Attribute.SERIAL_NUMBER, robotId);
		robotSetDetailParams.put(SetRobotProfileDetails2.Attribute.SOURCE_SMARTAPP_ID, NeatoPrefs.getUserEmailId(context));
		robotSetDetailParams.putAll(addProfilePrefix(profileDetailsParams));
		String response = MobileWebServiceClient.executeHttpPost(context, SetRobotProfileDetails2.METHOD_NAME, robotSetDetailParams);
		return AppUtils.checkResponseResult(response, SetRobotProfileDetailsResult2.class);
	}
	
	public static GetRobotPresenceStatusResult getRobotPresenceStatus (Context context, String robotId) 
			throws UserUnauthorizedException, NeatoServerException, IOException {		
		
		Map<String, String> robotSetDetailParams = new HashMap<String, String>();
		robotSetDetailParams.put(SetRobotProfileDetails2.Attribute.SERIAL_NUMBER, robotId);
		String response = MobileWebServiceClient.executeHttpPost(context, SetRobotProfileDetails2.METHOD_NAME, robotSetDetailParams);
		return AppUtils.checkResponseResult(response, GetRobotPresenceStatusResult.class);
	}
	
	public static GetRobotProfileDetailsResult2 getRobotProfileDetailsRequest2 (Context context, String robotId, String key) 
			throws UserUnauthorizedException, NeatoServerException, IOException {		
		Map<String, String> getRobotProfileParams = new HashMap<String, String>();
		getRobotProfileParams.put(
				GetRobotProfileDetails2.Attribute.SERIAL_NUMBER, robotId);
		if (!TextUtils.isEmpty(key)) {
			getRobotProfileParams.put(
					GetRobotProfileDetails2.Attribute.KEY, key);
		}
		String response = MobileWebServiceClient.executeHttpPost(context, GetRobotProfileDetails2.METHOD_NAME, getRobotProfileParams);
		return AppUtils.checkResponseResult(response, GetRobotProfileDetailsResult2.class);
	}
	
	private static HashMap<String, String> addProfilePrefix(HashMap<String, String> profileParams) {
		HashMap<String, String> profile = new HashMap<String, String>();
		String profilePrefix = SetRobotProfileDetails2.Attribute.PROFILE;
		for (HashMap.Entry<String, String> entry : profileParams.entrySet()) {
			String keyWithProfilePrefix = profilePrefix+"[" + entry.getKey() + "]";
			profile.put(keyWithProfilePrefix, entry.getValue());	        		        
		}
		return profile;
	}
	
	
}
