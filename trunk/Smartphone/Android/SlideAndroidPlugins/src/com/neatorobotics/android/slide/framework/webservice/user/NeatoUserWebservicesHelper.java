package com.neatorobotics.android.slide.framework.webservice.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.MobileWebServiceClient;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceUtils;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.AssociateNeatoRobotToUser;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.DisassociateNeatoRobotToUser;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.DissociateAllNeatoRobotsFromUser;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotAssociationDisassociationResult;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.ChangePassword;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.CreateNeatoUser;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.CreateNeatoUser2;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.ForgetPassword;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.GetNeatoUserDetails;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.GetUserAssociatedRobots;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.IsUserValidated;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.LoginNeatoUser;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.RegisterPushNotifications;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.ResendValidationMail;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.SendMessageToRobot;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.SetUserAttributes;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.UnregisterPushNotifications;
public class NeatoUserWebservicesHelper {
	
	public static GetNeatoUserDetailsResult getNeatoUserDetails(Context context, String email, String authToken) 
				throws UserUnauthorizedException, NeatoServerException, IOException {		
		
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put(GetNeatoUserDetails.Attribute.EMAIL, email);
		reqParams.put(GetNeatoUserDetails.Attribute.AUTHENTICATION_TOKEN, authToken);		

		String response = MobileWebServiceClient.executeHttpPost(context, GetNeatoUserDetails.METHOD_NAME, reqParams);
		return checkResponseResult(response, GetNeatoUserDetailsResult.class);
	}
	
	public static LoginNeatoUserTokenResult loginNeatoUserToken(Context context, String email, String password) 
				throws UserUnauthorizedException, NeatoServerException, IOException {
		
		Map<String, String> loginNeatoUserResult = new HashMap<String, String>();
		loginNeatoUserResult.put(LoginNeatoUser.Attribute.ACCOUNT_TYPE, NeatoUserWebServicesAttributes.ACCOUNT_TYPE_NATIVE);
		loginNeatoUserResult.put(LoginNeatoUser.Attribute.EMAIL, email);
		loginNeatoUserResult.put(LoginNeatoUser.Attribute.PASSWORD, password);
		
		String loginResponse = MobileWebServiceClient.executeHttpPost(context, LoginNeatoUser.METHOD_NAME, loginNeatoUserResult);
		LoginNeatoUserTokenResult loginResult = NeatoWebserviceUtils.readValueHelper(loginResponse, LoginNeatoUserTokenResult.class);
		
		// NOTE: AUTH_KEY value is NULL if validation_status is -2 in that case we need to pass 
		// server error message to caller
		
		if (loginResult.success()) {
			return loginResult;
		}
		
		if (loginResult.extra_params != null) {
			throw new UserUnauthorizedException(loginResult.mResponseStatus, loginResult.extra_params.message);
		}
		else {
			throw new NeatoServerException(loginResult.mResponseStatus, loginResult.message);
		}
		
	}
	
	public static CreateNeatoUserResult createNeatoUserRequestNative(Context context, String name, String email, String password) 
				throws UserUnauthorizedException, NeatoServerException, IOException {
		
		Map<String, String> createUserReqParams = new HashMap<String, String>();
		createUserReqParams.put(CreateNeatoUser.Attribute.ACCOUNT_TYPE, NeatoUserWebServicesAttributes.ACCOUNT_TYPE_NATIVE);
		createUserReqParams.put(CreateNeatoUser.Attribute.NAME, name);
		createUserReqParams.put(CreateNeatoUser.Attribute.EMAIL, email);
		createUserReqParams.put(CreateNeatoUser.Attribute.PASSWORD, password);
	
		
		String createUserResponse = MobileWebServiceClient.executeHttpPost(context, CreateNeatoUser.METHOD_NAME, createUserReqParams);		
		return checkResponseResult(createUserResponse, CreateNeatoUserResult.class);
	}
	
	public static CreateNeatoUserResult createNeatoUser2RequestNative(Context context, String name, String email, String alternateEmail, 
			String password) throws UserUnauthorizedException, NeatoServerException, IOException {
		
		Map<String, String> createUser2ReqParams = new HashMap<String, String>();
		createUser2ReqParams.put(CreateNeatoUser2.Attribute.ACCOUNT_TYPE, NeatoUserWebServicesAttributes.ACCOUNT_TYPE_NATIVE);
		createUser2ReqParams.put(CreateNeatoUser2.Attribute.NAME, name);
		createUser2ReqParams.put(CreateNeatoUser2.Attribute.EMAIL, email);
		createUser2ReqParams.put(CreateNeatoUser2.Attribute.PASSWORD, password);
		if (!TextUtils.isEmpty(alternateEmail)) {
			createUser2ReqParams.put(CreateNeatoUser2.Attribute.ALTERNATE_EMAIL, alternateEmail);
		}
		
		String response = MobileWebServiceClient.executeHttpPost(context, CreateNeatoUser2.METHOD_NAME, createUser2ReqParams);
		return checkResponseResult(response, CreateNeatoUserResult.class);
	}
	
	public static IsUserValidatedResult isUserValidatedRequest(Context context, String email) 
			throws UserUnauthorizedException, NeatoServerException, IOException {
		
		Map<String, String> validateReqParams = new HashMap<String, String>();
		validateReqParams.put(IsUserValidated.Attribute.EMAIL, email);
		
		String validateUserResponse = MobileWebServiceClient.executeHttpPost(context, IsUserValidated.METHOD_NAME, validateReqParams);
		return checkResponseResult(validateUserResponse, IsUserValidatedResult.class);
	}
	
	public static ResendValidationMailResult resendValidationMailResult(Context context, String email)
			throws UserUnauthorizedException, NeatoServerException, IOException {
		
		Map<String, String> resendValidationMailParams = new HashMap<String, String>();
		resendValidationMailParams.put(ResendValidationMail.Attribute.EMAIL, email);
		
		String resendValidationMailResponse = MobileWebServiceClient.executeHttpPost(context, ResendValidationMail.METHOD_NAME, resendValidationMailParams);
		return checkResponseResult(resendValidationMailResponse, ResendValidationMailResult.class);
	}
	
	public static RobotAssociationDisassociationResult associateNeatoRobotRequest(Context context, String email, String robotId)
				throws UserUnauthorizedException, NeatoServerException, IOException {		
		
		Map<String, String> associateRobotReqParams = new HashMap<String, String>();
		associateRobotReqParams.put(AssociateNeatoRobotToUser.Attribute.EMAIL, email);
		associateRobotReqParams.put(AssociateNeatoRobotToUser.Attribute.SERIAL_NUMBER, robotId);
		
		String associateRobotResponse = MobileWebServiceClient.executeHttpPost(context, AssociateNeatoRobotToUser.METHOD_NAME, associateRobotReqParams);
		return checkResponseResult(associateRobotResponse, RobotAssociationDisassociationResult.class);
	}
	
	public static RobotAssociationDisassociationResult disassociateNeatoRobotRequest(Context context, String email, String robotId)
				throws UserUnauthorizedException, NeatoServerException, IOException {
				
		Map<String, String> associateRobotReqParams = new HashMap<String, String>();
		associateRobotReqParams.put(DisassociateNeatoRobotToUser.Attribute.EMAIL, email);
		associateRobotReqParams.put(DisassociateNeatoRobotToUser.Attribute.SERIAL_NUMBER, robotId);
		
		String dissociateResponse = MobileWebServiceClient.executeHttpPost(context, DisassociateNeatoRobotToUser.METHOD_NAME, associateRobotReqParams);		
		return checkResponseResult(dissociateResponse, RobotAssociationDisassociationResult.class);
	}
	
	public static RobotAssociationDisassociationResult dissociateAllNeatoRobotsRequest(Context context, String email)
				throws UserUnauthorizedException, NeatoServerException, IOException {		
		
		Map<String, String> dissociateRobotReqParams = new HashMap<String, String>();
		dissociateRobotReqParams.put(DisassociateNeatoRobotToUser.Attribute.EMAIL, email);
		dissociateRobotReqParams.put(DisassociateNeatoRobotToUser.Attribute.SERIAL_NUMBER, "");	
		
		String dissociateResponse = MobileWebServiceClient.executeHttpPost(context, DissociateAllNeatoRobotsFromUser.METHOD_NAME, dissociateRobotReqParams);		
		return checkResponseResult(dissociateResponse, RobotAssociationDisassociationResult.class);
	}
	
	public static GetUserAssociatedRobotsResult getUserAssociatedRobots(Context context, String email, String authToken) 
				throws UserUnauthorizedException, NeatoServerException, IOException { 
		
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put(GetUserAssociatedRobots.Attribute.EMAIL, email);
		reqParams.put(GetUserAssociatedRobots.Attribute.AUTHENTICATION_TOKEN, authToken);		
		String associatedRobotsResponse = MobileWebServiceClient.executeHttpPost(context, GetUserAssociatedRobots.METHOD_NAME, reqParams);		
		return checkResponseResult(associatedRobotsResponse, GetUserAssociatedRobotsResult.class);
	}
	
	public static SetUserAttributesResult setUserAttributeRequest(Context context, String authToken, HashMap<String, String> profileDetailsParams) 
				throws UserUnauthorizedException, NeatoServerException, IOException {		
		
		Map<String, String> userSetAttributesParams = new HashMap<String, String>();
		userSetAttributesParams.put(SetUserAttributes.Attribute.AUTHENTICATION_TOKEN, authToken);
		userSetAttributesParams.putAll(addProfilePrefix(profileDetailsParams));
		String associatedRobotsResponse = MobileWebServiceClient.executeHttpPost(context, SetUserAttributes.METHOD_NAME, userSetAttributesParams);
		return checkResponseResult(associatedRobotsResponse, SetUserAttributesResult.class);
	}
	
	public static SendMessageToRobotResult sendMessageToRobotRequest(Context context, String userId, String robotId, String message)
				throws UserUnauthorizedException, NeatoServerException, IOException {
		
		Map<String, String> sendRobotMessageParams = new HashMap<String, String>();
		sendRobotMessageParams.put(SendMessageToRobot.Attribute.USER_ID, userId);
		sendRobotMessageParams.put(SendMessageToRobot.Attribute.SERIAL_NUMBER, robotId);
		sendRobotMessageParams.put(SendMessageToRobot.Attribute.MESSAGE, message);
		String response = MobileWebServiceClient.executeHttpPost(context, SendMessageToRobot.METHOD_NAME, sendRobotMessageParams);		
		return checkResponseResult(response, SendMessageToRobotResult.class);
	}
	
	public static ForgetPasswordResult forgetPasswordRequest(Context context, String email) 
				throws UserUnauthorizedException, NeatoServerException, IOException	{		
		
		Map<String, String> changePasswordParams = new HashMap<String, String>();
		changePasswordParams.put(ForgetPassword.Attribute.EMAIL, email);
		
		String response = MobileWebServiceClient.executeHttpPost(context, ForgetPassword.METHOD_NAME, changePasswordParams);
		return checkResponseResult(response, ForgetPasswordResult.class);
	}
	
	public static RegisterPushNotificationResult registerPushNotification(Context context, String email, int deviceType, String registrationId)
				throws UserUnauthorizedException, NeatoServerException, IOException {
		Map<String, String> registerPushNotificationParams = new HashMap<String, String>();
		registerPushNotificationParams.put(RegisterPushNotifications.Attribute.EMAIL, email);
		registerPushNotificationParams.put(RegisterPushNotifications.Attribute.DEVICE_TYPE, Integer.toString(deviceType));
		registerPushNotificationParams.put(RegisterPushNotifications.Attribute.REGISTRATION_ID, registrationId);
		
		String response = MobileWebServiceClient.executeHttpPost(context, RegisterPushNotifications.METHOD_NAME, registerPushNotificationParams);
		return checkResponseResult(response, RegisterPushNotificationResult.class);
	}
	
	public static UnregisterPushNotificationResult unregisterPushNotification(Context context, String registrationId)
				throws UserUnauthorizedException, NeatoServerException, IOException {
		Map<String, String> unregisterPushNotificationParams = new HashMap<String, String>();
		unregisterPushNotificationParams.put(UnregisterPushNotifications.Attribute.REGISTRATION_ID, registrationId);
		
		String response = MobileWebServiceClient.executeHttpPost(context, UnregisterPushNotifications.METHOD_NAME, unregisterPushNotificationParams);
		return checkResponseResult(response, UnregisterPushNotificationResult.class);
	}
	
	public static ChangePasswordResult changePasswordRequest(Context context, String authToken, String oldPassword, String newPassword)
				throws UserUnauthorizedException, NeatoServerException, IOException	{		
		
		Map<String, String> changePasswordParams = new HashMap<String, String>();
		changePasswordParams.put(ChangePassword.Attribute.AUTHENTICATION_TOKEN, authToken);
		changePasswordParams.put(ChangePassword.Attribute.PASSWORD_OLD, oldPassword);
		changePasswordParams.put(ChangePassword.Attribute.PASSWORD_NEW, newPassword);
		
		String response = MobileWebServiceClient.executeHttpPost(context, ChangePassword.METHOD_NAME, changePasswordParams);
		return checkResponseResult(response, ChangePasswordResult.class);		
	}	
	
	private static <T extends NeatoWebserviceResult> T  checkResponseResult(String response, Class<T> responseClassType) throws NeatoServerException {
		T result = NeatoWebserviceUtils.readValueHelper(response, responseClassType);
		if (!result.success()) {
			throw new NeatoServerException(result.mResponseStatus, result.message);
		}
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
