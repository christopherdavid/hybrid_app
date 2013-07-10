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
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.GetRobotVirtualOnlineStatus;


public class NeatoRobotWebservicesHelper {
	
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
	
	public static RobotVirtualOnlineStatusResult getRobotVirtualOnlineStatus(Context context, String robotId)
			throws UserUnauthorizedException, NeatoServerException, IOException {
		
		Map<String, String> robotGetOnlineStatus = new HashMap<String, String>();
		robotGetOnlineStatus.put(GetRobotVirtualOnlineStatus.Attribute.SERIAL_NUMBER, robotId);	
		String response =  MobileWebServiceClient.executeHttpPost(context, GetRobotVirtualOnlineStatus.METHOD_NAME, robotGetOnlineStatus);
		return AppUtils.checkResponseResult(response, RobotVirtualOnlineStatusResult.class);		
	}
}
