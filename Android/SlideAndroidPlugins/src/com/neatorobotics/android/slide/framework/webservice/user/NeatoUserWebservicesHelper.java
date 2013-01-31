package com.neatorobotics.android.slide.framework.webservice.user;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import android.content.Context;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.DisassociateNeatoRobotToUser;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.DissociateAllNeatoRobotsFromUser;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotAssociationDisassociationResult;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.AssociateNeatoRobotToUser;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.CreateNeatoUser;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.GetNeatoUserDetails;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.LoginNeatoUser;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebServicesAttributes.GetUserAssociatedRobots;
public class NeatoUserWebservicesHelper {

	private static final String TAG = NeatoUserWebservicesHelper.class.getSimpleName();
	private static ObjectMapper resultMapper = new ObjectMapper();
	
	public static CreateNeatoUserResult createNeatoUserRequestNative(Context context, String name, String email, String password) {
		CreateNeatoUserResult result = null;
		Map<String, String> createUserReqParams = new HashMap<String, String>();
		createUserReqParams.put(CreateNeatoUser.Attribute.ACCOUNT_TYPE, NeatoUserWebServicesAttributes.ACCOUNT_TYPE_NATIVE);
		createUserReqParams.put(CreateNeatoUser.Attribute.NAME, name);
		createUserReqParams.put(CreateNeatoUser.Attribute.EMAIL, email);
		createUserReqParams.put(CreateNeatoUser.Attribute.PASSWORD, password);
	
		
		NeatoHttpResponse createUserResponse = NeatoWebserviceHelper.executeHttpPost(context, CreateNeatoUser.METHOD_NAME, createUserReqParams);
		if (createUserResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Creating Neato User completed. Reading response");
				String createUserJson = AppUtils.convertStreamToString(createUserResponse.mResponseInputStream);
				LogHelper.log(TAG, "Create New User Json = " + createUserJson);
				result = resultMapper.readValue(createUserJson, new TypeReference<CreateNeatoUserResult>() {});
				LogHelper.log(TAG, "Creating user completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in createNeatoUser", e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in createNeatoUser", e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in createNeatoUser", e);

			}
		}
		else { 
			LogHelper.log(TAG, "Create Neato User request not completed.");
			result = new CreateNeatoUserResult(createUserResponse);
		}
		
		return result;
	}
	
	public static LoginNeatoUserTokenResult loginNeatoUserToken(Context context, String email, String password) {
		LoginNeatoUserTokenResult result = null;
		
		Map<String, String> loginNeatoUserResult = new HashMap<String, String>();
		loginNeatoUserResult.put(LoginNeatoUser.Attribute.ACCOUNT_TYPE, NeatoUserWebServicesAttributes.ACCOUNT_TYPE_NATIVE);
		loginNeatoUserResult.put(LoginNeatoUser.Attribute.EMAIL, email);
		loginNeatoUserResult.put(LoginNeatoUser.Attribute.PASSWORD, password);
		
		NeatoHttpResponse loginUserResponse = NeatoWebserviceHelper.executeHttpPost(context, LoginNeatoUser.METHOD_NAME, loginNeatoUserResult);
		if (loginUserResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Login completed. Reading response");
				String json = AppUtils.convertStreamToString(loginUserResponse.mResponseInputStream);
				LogHelper.logD(TAG, "LoginNeatoUserToken JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<LoginNeatoUserTokenResult>() {});
				LogHelper.log(TAG, "Login Neato User completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in LoginNeatoUser" , e);
			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in LoginNeatoUser" , e);
			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in LoginNeatoUser" , e);
			}
		}
		else { 
			LogHelper.log(TAG, "Login Neato User not completed.");
			result = new LoginNeatoUserTokenResult(loginUserResponse);
		}
		return result;
	}
	
	public static GetNeatoUserDetailsResult getNeatoUserDetails(Context context, String email, String authToken) { 
		
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put(GetNeatoUserDetails.Attribute.EMAIL, email);
		reqParams.put(GetNeatoUserDetails.Attribute.AUTHENTICATION_TOKEN, authToken);
		GetNeatoUserDetailsResult result = null;

		NeatoHttpResponse getNeatoUserDetailsResponse = NeatoWebserviceHelper.executeHttpPost(context, GetNeatoUserDetails.METHOD_NAME, reqParams);
		if (getNeatoUserDetailsResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "authkey = " + authToken);
				String json = AppUtils.convertStreamToString(getNeatoUserDetailsResponse.mResponseInputStream);
				LogHelper.logD(TAG, "JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<GetNeatoUserDetailsResult>() {});
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "GetNeatoUserDetails", e);
				
			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "GetNeatoUserDetails", e);
				
			} catch (IOException e) {
				LogHelper.log(TAG, "GetNeatoUserDetails", e);
			}
		}
		else { 
			result = new GetNeatoUserDetailsResult(getNeatoUserDetailsResponse);
		}
		
		return result;

	}
	
	public static GetUserAssociatedRobotsResult getUserAssociatedRobots(Context context, String email, String authToken) { 
		
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put(GetUserAssociatedRobots.Attribute.EMAIL, email);
		reqParams.put(GetUserAssociatedRobots.Attribute.AUTHENTICATION_TOKEN, authToken);
		GetUserAssociatedRobotsResult result = null;

		NeatoHttpResponse getUserAssociatedRobotsResponse = NeatoWebserviceHelper.executeHttpPost(context, GetUserAssociatedRobots.METHOD_NAME, reqParams);
		if (getUserAssociatedRobotsResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "authkey = " + authToken);
				String json = AppUtils.convertStreamToString(getUserAssociatedRobotsResponse.mResponseInputStream);
				LogHelper.logD(TAG, "JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<GetUserAssociatedRobotsResult>() {});
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in GetUserAssociatedRobotsResult", e);
				
			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in GetUserAssociatedRobotsResult", e);
				
			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in GetUserAssociatedRobotsResult", e);
			}
		}
		else { 
			result = new GetUserAssociatedRobotsResult(getUserAssociatedRobotsResponse);
		}
		
		return result;

	}
	
	public static RobotAssociationDisassociationResult disassociateNeatoRobotRequest(Context context, String email, String robotId) {
		RobotAssociationDisassociationResult result = null;
		Map<String, String> associateRobotReqParams = new HashMap<String, String>();
		associateRobotReqParams.put(DisassociateNeatoRobotToUser.Attribute.EMAIL, email);
		associateRobotReqParams.put(DisassociateNeatoRobotToUser.Attribute.SERIAL_NUMBER, robotId);
	
		
		NeatoHttpResponse disassociateRobotResponse = NeatoWebserviceHelper.executeHttpPost(context, DisassociateNeatoRobotToUser.METHOD_NAME, associateRobotReqParams);
		if (disassociateRobotResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Disassociating Neato Robot completed. Reading response");
				String json = AppUtils.convertStreamToString(disassociateRobotResponse.mResponseInputStream);
				LogHelper.logD(TAG, "JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<RobotAssociationDisassociationResult>() {});				
				LogHelper.log(TAG, "Robot disassociated.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in disassociateNeatoRobotRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in disassociateNeatoRobotRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in disassociateNeatoRobotRequest" ,e);

			}
		}
		else { 
			LogHelper.log(TAG, "Robot disassociation failed.");
			result = new RobotAssociationDisassociationResult(disassociateRobotResponse);
		}
		
		return result;
	}
	
	public static RobotAssociationDisassociationResult dissociateAllNeatoRobotsRequest(Context context, String email) {
		RobotAssociationDisassociationResult result = null;
		Map<String, String> dissociateRobotReqParams = new HashMap<String, String>();
		dissociateRobotReqParams.put(DisassociateNeatoRobotToUser.Attribute.EMAIL, email);
		dissociateRobotReqParams.put(DisassociateNeatoRobotToUser.Attribute.SERIAL_NUMBER, "");	
		
		NeatoHttpResponse disassociateRobotResponse = NeatoWebserviceHelper.executeHttpPost(context, DissociateAllNeatoRobotsFromUser.METHOD_NAME, dissociateRobotReqParams);
		if (disassociateRobotResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Dissociating all Neato Robots completed. Reading response");
				String json = AppUtils.convertStreamToString(disassociateRobotResponse.mResponseInputStream);
				LogHelper.logD(TAG, "JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<RobotAssociationDisassociationResult>() {});				
				LogHelper.log(TAG, "All Robots dissociated.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in dissociateAllNeatoRobotsRequest" ,e);
			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in dissociateAllNeatoRobotsRequest" ,e);
			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in dissociateAllNeatoRobotsRequest" ,e);
			}
		}
		else { 
			LogHelper.log(TAG, "All Robots dissociation failed.");
			result = new RobotAssociationDisassociationResult(disassociateRobotResponse);
		}
		
		return result;
	}

	public static RobotAssociationDisassociationResult associateNeatoRobotRequest(Context context, String email, String robotId) {
		RobotAssociationDisassociationResult result = null;
		Map<String, String> associateRobotReqParams = new HashMap<String, String>();
		associateRobotReqParams.put(AssociateNeatoRobotToUser.Attribute.EMAIL, email);
		associateRobotReqParams.put(AssociateNeatoRobotToUser.Attribute.SERIAL_NUMBER, robotId);
	
		
		NeatoHttpResponse associateRobotResponse = NeatoWebserviceHelper.executeHttpPost(context, AssociateNeatoRobotToUser.METHOD_NAME, associateRobotReqParams);
		if (associateRobotResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Associating Neato Robot completed. Reading response");
				result = resultMapper.readValue(associateRobotResponse.mResponseInputStream, new TypeReference<RobotAssociationDisassociationResult>() {});
				LogHelper.log(TAG, "Associating robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in AssociateNeatoRobotRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in AssociateNeatoRobotRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in AssociateNeatoRobotRequest" ,e);

			}
		}
		else { 
			LogHelper.log(TAG, "Association of  Neato Robot request not completed.");
			result = new RobotAssociationDisassociationResult(associateRobotResponse);
		}
		
		return result;
	}
}
