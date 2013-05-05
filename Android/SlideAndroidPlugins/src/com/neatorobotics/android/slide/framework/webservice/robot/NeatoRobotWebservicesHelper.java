package com.neatorobotics.android.slide.framework.webservice.robot;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.MobileWebServiceClient;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.GetRobotDetails;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.GetRobotOnlineStatus;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.SetRobotProfileDetails;


public class NeatoRobotWebservicesHelper {
	
	public static SetRobotProfileDetailsResult setRobotProfileDetailsRequest(Context context, String robotId, HashMap<String, String> profileDetailsParams) 
			throws UserUnauthorizedException, NeatoServerException, IOException {		
		
		Map<String, String> robotSetDetailParams = new HashMap<String, String>();
		robotSetDetailParams.put(SetRobotProfileDetails.Attribute.SERIAL_NUMBER, robotId);
		robotSetDetailParams.putAll(addProfilePrefix(profileDetailsParams));
		String response = MobileWebServiceClient.executeHttpPost(context, SetRobotProfileDetails.METHOD_NAME, robotSetDetailParams);
		return AppUtils.checkResponseResult(response, SetRobotProfileDetailsResult.class);
	}
	
	private static HashMap<String, String> addProfilePrefix(HashMap<String, String> profileParams) {
		HashMap<String, String> profile = new HashMap<String, String>();
		String profilePrefix = SetRobotProfileDetails.Attribute.PROFILE;
		for (HashMap.Entry<String, String> entry : profileParams.entrySet()) {
			String keyWithProfilePrefix = profilePrefix+"[" + entry.getKey() + "]";
			profile.put(keyWithProfilePrefix, entry.getValue());	        		        
		}
		return profile;
	}
	
	public static RobotDetailResult getRobotDetail(Context context, String serialNumber) 
			throws UserUnauthorizedException, NeatoServerException, IOException {		
		
		Map<String, String> robotGetDetailParams = new HashMap<String, String>();
		robotGetDetailParams.put(GetRobotDetails.Attribute.SERIAL_NUMBER, serialNumber);
		String response = MobileWebServiceClient.executeHttpPost(context, GetRobotDetails.METHOD_NAME, robotGetDetailParams);
		return AppUtils.checkResponseResult(response, RobotDetailResult.class);
	}
	
	public static RobotOnlineStatusResult getRobotOnlineStatus(Context context, String serialNumber)
			throws UserUnauthorizedException, NeatoServerException, IOException {
		
		Map<String, String> robotGetOnlineStatus = new HashMap<String, String>();
		robotGetOnlineStatus.put(GetRobotOnlineStatus.Attribute.SERIAL_NUMBER, serialNumber);	
		String response =  MobileWebServiceClient.executeHttpPost(context, GetRobotOnlineStatus.METHOD_NAME, robotGetOnlineStatus);
		return AppUtils.checkResponseResult(response, RobotOnlineStatusResult.class);		
	}	
}
