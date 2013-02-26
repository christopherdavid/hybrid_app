package com.neatorobotics.android.slide.framework.webservice.user;


import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceHelper;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.DisassociateNeatoRobotToUser;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.DissociateAllNeatoRobotsFromUser;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotAssociationDisassociationResult;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.AssociateNeatoRobotToUser;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.ChangePassword;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.CreateNeatoUser;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.ForgetPassword;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.GetNeatoUserDetails;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.LoginNeatoUser;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.GetUserAssociatedRobots;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.SendMessageToRobot;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.SetUserAttributes;
public class NeatoUserWebservicesHelper {

	private static final String TAG = NeatoUserWebservicesHelper.class.getSimpleName();
	
	public static CreateNeatoUserResult createNeatoUserRequestNative(Context context, String name, String email, String password) {
		Map<String, String> createUserReqParams = new HashMap<String, String>();
		createUserReqParams.put(CreateNeatoUser.Attribute.ACCOUNT_TYPE, NeatoUserWebServicesAttributes.ACCOUNT_TYPE_NATIVE);
		createUserReqParams.put(CreateNeatoUser.Attribute.NAME, name);
		createUserReqParams.put(CreateNeatoUser.Attribute.EMAIL, email);
		createUserReqParams.put(CreateNeatoUser.Attribute.PASSWORD, password);
	
		
		NeatoHttpResponse createUserResponse = NeatoWebserviceHelper.executeHttpPost(context, CreateNeatoUser.METHOD_NAME, createUserReqParams);
		CreateNeatoUserResult result = NeatoWebserviceUtils.readValueHelper(createUserResponse, CreateNeatoUserResult.class);
		return result;
	}
	
	public static LoginNeatoUserTokenResult loginNeatoUserToken(Context context, String email, String password) {
		
		Map<String, String> loginNeatoUserResult = new HashMap<String, String>();
		loginNeatoUserResult.put(LoginNeatoUser.Attribute.ACCOUNT_TYPE, NeatoUserWebServicesAttributes.ACCOUNT_TYPE_NATIVE);
		loginNeatoUserResult.put(LoginNeatoUser.Attribute.EMAIL, email);
		loginNeatoUserResult.put(LoginNeatoUser.Attribute.PASSWORD, password);
		
		NeatoHttpResponse loginUserResponse = NeatoWebserviceHelper.executeHttpPost(context, LoginNeatoUser.METHOD_NAME, loginNeatoUserResult);
		LoginNeatoUserTokenResult result = NeatoWebserviceUtils.readValueHelper(loginUserResponse, LoginNeatoUserTokenResult.class);
		return result;
	}
	
	public static GetNeatoUserDetailsResult getNeatoUserDetails(Context context, String email, String authToken) { 
		
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put(GetNeatoUserDetails.Attribute.EMAIL, email);
		reqParams.put(GetNeatoUserDetails.Attribute.AUTHENTICATION_TOKEN, authToken);
		

		NeatoHttpResponse getNeatoUserDetailsResponse = NeatoWebserviceHelper.executeHttpPost(context, GetNeatoUserDetails.METHOD_NAME, reqParams);
		GetNeatoUserDetailsResult result = NeatoWebserviceUtils.readValueHelper(getNeatoUserDetailsResponse, GetNeatoUserDetailsResult.class);
		return result;

	}
	
	public static GetUserAssociatedRobotsResult getUserAssociatedRobots(Context context, String email, String authToken) { 
		
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put(GetUserAssociatedRobots.Attribute.EMAIL, email);
		reqParams.put(GetUserAssociatedRobots.Attribute.AUTHENTICATION_TOKEN, authToken);
		GetUserAssociatedRobotsResult result = null;
		NeatoHttpResponse getUserAssociatedRobotsResponse = NeatoWebserviceHelper.executeHttpPost(context, GetUserAssociatedRobots.METHOD_NAME, reqParams);
		result = NeatoWebserviceUtils.readValueHelper(getUserAssociatedRobotsResponse, GetUserAssociatedRobotsResult.class);
		return result;
	}
	
	public static RobotAssociationDisassociationResult disassociateNeatoRobotRequest(Context context, String email, String robotId) {
		RobotAssociationDisassociationResult result = null;
		Map<String, String> associateRobotReqParams = new HashMap<String, String>();
		associateRobotReqParams.put(DisassociateNeatoRobotToUser.Attribute.EMAIL, email);
		associateRobotReqParams.put(DisassociateNeatoRobotToUser.Attribute.SERIAL_NUMBER, robotId);
		NeatoHttpResponse disassociateRobotResponse = NeatoWebserviceHelper.executeHttpPost(context, DisassociateNeatoRobotToUser.METHOD_NAME, associateRobotReqParams);
		result = NeatoWebserviceUtils.readValueHelper(disassociateRobotResponse, RobotAssociationDisassociationResult.class);
		return result;
	}
	
	public static RobotAssociationDisassociationResult dissociateAllNeatoRobotsRequest(Context context, String email) {
		RobotAssociationDisassociationResult result = null;
		Map<String, String> dissociateRobotReqParams = new HashMap<String, String>();
		dissociateRobotReqParams.put(DisassociateNeatoRobotToUser.Attribute.EMAIL, email);
		dissociateRobotReqParams.put(DisassociateNeatoRobotToUser.Attribute.SERIAL_NUMBER, "");	
		NeatoHttpResponse disassociateRobotResponse = NeatoWebserviceHelper.executeHttpPost(context, DissociateAllNeatoRobotsFromUser.METHOD_NAME, dissociateRobotReqParams);
		result = NeatoWebserviceUtils.readValueHelper(disassociateRobotResponse, RobotAssociationDisassociationResult.class);
		return result;
	}

	public static RobotAssociationDisassociationResult associateNeatoRobotRequest(Context context, String email, String robotId) {
		RobotAssociationDisassociationResult result = null;
		Map<String, String> associateRobotReqParams = new HashMap<String, String>();
		associateRobotReqParams.put(AssociateNeatoRobotToUser.Attribute.EMAIL, email);
		associateRobotReqParams.put(AssociateNeatoRobotToUser.Attribute.SERIAL_NUMBER, robotId);
		NeatoHttpResponse associateRobotResponse = NeatoWebserviceHelper.executeHttpPost(context, AssociateNeatoRobotToUser.METHOD_NAME, associateRobotReqParams);
		result = NeatoWebserviceUtils.readValueHelper(associateRobotResponse, RobotAssociationDisassociationResult.class);
		return result;
	}
	
	public static SetUserAttributesResult setUserAttributeRequest(Context context, String authToken, HashMap<String, String> profileDetailsParams) 
	{
		SetUserAttributesResult result = null;
		Map<String, String> userSetAttributesParams = new HashMap<String, String>();
		userSetAttributesParams.put(SetUserAttributes.Attribute.AUTHENTICATION_TOKEN, authToken);
		userSetAttributesParams.putAll(addProfilePrefix(profileDetailsParams));
		NeatoHttpResponse userSetAttributeResponse = NeatoWebserviceHelper.executeHttpPost(context, SetUserAttributes.METHOD_NAME, userSetAttributesParams);
		result = NeatoWebserviceUtils.readValueHelper(userSetAttributeResponse, SetUserAttributesResult.class);
		
		return result;
		
	}
	
	public static SendMessageToRobotResult sendMessageToRobotRequest(Context context, String userId, String robotId, String message) 
	{
		SendMessageToRobotResult result = null;
		Map<String, String> sendRobotMessageParams = new HashMap<String, String>();
		sendRobotMessageParams.put(SendMessageToRobot.Attribute.USER_ID, userId);
		sendRobotMessageParams.put(SendMessageToRobot.Attribute.SERIAL_NUMBER, robotId);
		sendRobotMessageParams.put(SendMessageToRobot.Attribute.MESSAGE, message);
		NeatoHttpResponse sendRobotMessageResponse = NeatoWebserviceHelper.executeHttpPost(context, SendMessageToRobot.METHOD_NAME, sendRobotMessageParams);
		result = NeatoWebserviceUtils.readValueHelper(sendRobotMessageResponse, SendMessageToRobotResult.class);
		return result;
	}
	
	public static ChangePasswordResult changePasswordRequest(Context context, String authToken, String oldPassword, String newPassword) 
	{
		ChangePasswordResult result = null;
		Map<String, String> changePasswordParams = new HashMap<String, String>();
		changePasswordParams.put(ChangePassword.Attribute.AUTHENTICATION_TOKEN, authToken);
		changePasswordParams.put(ChangePassword.Attribute.PASSWORD_OLD, oldPassword);
		changePasswordParams.put(ChangePassword.Attribute.PASSWORD_NEW, newPassword);
		
		NeatoHttpResponse changePasswordResponse = NeatoWebserviceHelper.executeHttpPost(context, ChangePassword.METHOD_NAME, changePasswordParams);
		result = NeatoWebserviceUtils.readValueHelper(changePasswordResponse, ChangePasswordResult.class);

		return result;
	}
	
	public static ForgetPasswordResult forgetPasswordRequest(Context context, String email) 
	{
		ForgetPasswordResult result = null;
		Map<String, String> changePasswordParams = new HashMap<String, String>();
		changePasswordParams.put(ForgetPassword.Attribute.EMAIL, email);
		
		NeatoHttpResponse forgetPasswordResponse = NeatoWebserviceHelper.executeHttpPost(context, ForgetPassword.METHOD_NAME, changePasswordParams);
		result = NeatoWebserviceUtils.readValueHelper(forgetPasswordResponse, ForgetPasswordResult.class);
		
		return result;
	}
	
	private static HashMap<String, String> addProfilePrefix(HashMap<String, String> profileParams) {
		HashMap<String, String> profile = new HashMap<String, String>();
		String profilePrefix = SetUserAttributes.Attribute.PROFILE;
		for (HashMap.Entry<String, String> entry : profileParams.entrySet()) {
			
			String keyWithProfilePrefix = profilePrefix+"[" + entry.getKey() + "]";
			profile.put(keyWithProfilePrefix, entry.getValue());	        		        
		}
		return profile;
	}

}
