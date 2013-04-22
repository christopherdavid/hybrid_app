package com.neatorobotics.android.slide.framework.webservice.robot;


import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceHelper;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.AssociateNeatoRobotToUser;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.GetRobotDetails;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.GetRobotOnlineStatus;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.SetRobotProfileDetails;


public class NeatoRobotWebservicesHelper {
	
	public static RobotAssociationDisassociationResult associateNeatoRobotRequest(Context context, String email, String serial_number) {
		RobotAssociationDisassociationResult result = null;
		Map<String, String> associateRobotReqParams = new HashMap<String, String>();
		associateRobotReqParams.put(AssociateNeatoRobotToUser.Attribute.EMAIL, email);
		associateRobotReqParams.put(AssociateNeatoRobotToUser.Attribute.SERIAL_NUMBER, serial_number);
		NeatoHttpResponse associateRobotResponse = NeatoWebserviceHelper.executeHttpPost(context, AssociateNeatoRobotToUser.METHOD_NAME, associateRobotReqParams);
		result = NeatoWebserviceUtils.readValueHelper(associateRobotResponse, RobotAssociationDisassociationResult.class);
		return result;
	}
	
	public static SetRobotProfileDetailsResult setRobotProfileDetailsRequest(Context context, String robotId, HashMap<String, String> profileDetailsParams) 
	{
		SetRobotProfileDetailsResult result = null;
		Map<String, String> robotSetDetailParams = new HashMap<String, String>();
		robotSetDetailParams.put(SetRobotProfileDetails.Attribute.SERIAL_NUMBER, robotId);
		robotSetDetailParams.putAll(addProfilePrefix(profileDetailsParams));
		NeatoHttpResponse robotSetDetailResponse = NeatoWebserviceHelper.executeHttpPost(context, SetRobotProfileDetails.METHOD_NAME, robotSetDetailParams);
		result = NeatoWebserviceUtils.readValueHelper(robotSetDetailResponse, SetRobotProfileDetailsResult.class);
		return result;
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
	
	public static RobotDetailResult getRobotDetail(Context context, String serialNumber) {
		RobotDetailResult result = null;
		Map<String, String> robotGetDetailParams = new HashMap<String, String>();
		robotGetDetailParams.put(GetRobotDetails.Attribute.SERIAL_NUMBER, serialNumber);
		NeatoHttpResponse robotDetailResponse = NeatoWebserviceHelper.executeHttpPost(context, GetRobotDetails.METHOD_NAME, robotGetDetailParams);
		result = NeatoWebserviceUtils.readValueHelper(robotDetailResponse, RobotDetailResult.class);
		return result;
	}
	
	public static RobotOnlineStatusResult getRobotOnlineStatus(Context context, String serialNumber) {
		RobotOnlineStatusResult result = null;
		Map<String, String> robotGetOnlineStatus = new HashMap<String, String>();
		robotGetOnlineStatus.put(GetRobotOnlineStatus.Attribute.SERIAL_NUMBER, serialNumber);	
		NeatoHttpResponse robotStatusResponse = NeatoWebserviceHelper.executeHttpPost(context, GetRobotOnlineStatus.METHOD_NAME, robotGetOnlineStatus);
		result = NeatoWebserviceUtils.readValueHelper(robotStatusResponse, RobotOnlineStatusResult.class);
		return result;
	}	
}
