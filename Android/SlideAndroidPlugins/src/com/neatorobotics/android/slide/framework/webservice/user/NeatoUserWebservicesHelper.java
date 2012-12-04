package com.neatorobotics.android.slide.framework.webservice.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.content.Context;
import android.util.Log;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceHelper;
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
				String createUserJson = convertStreamToString(createUserResponse.mResponseInputStream);
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
				String json = convertStreamToString(loginUserResponse.mResponseInputStream);
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
		reqParams.put(GetNeatoUserDetails.Attribute.AUTHHENTICATION_TOKEN, authToken);
		GetNeatoUserDetailsResult result = null;

		NeatoHttpResponse getNeatoUserDetailsResponse = NeatoWebserviceHelper.executeHttpPost(context, GetNeatoUserDetails.METHOD_NAME, reqParams);
		if (getNeatoUserDetailsResponse.completed()) { 
			try {
				Log.i(TAG, "authkey = " + authToken);
				String json = convertStreamToString(getNeatoUserDetailsResponse.mResponseInputStream);
				Log.i(TAG, "JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<GetNeatoUserDetailsResult>() {});
			} catch (JsonParseException e) {
				Log.e(TAG, "GetNeatoUserDetails", e);
				
			} catch (JsonMappingException e) {
				Log.e(TAG, "GetNeatoUserDetails", e);
				
			} catch (IOException e) {
				Log.e(TAG, "GetNeatoUserDetails", e);
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
		reqParams.put(GetUserAssociatedRobots.Attribute.AUTHHENTICATION_TOKEN, authToken);
		GetUserAssociatedRobotsResult result = null;

		NeatoHttpResponse getUserAssociatedRobotsResponse = NeatoWebserviceHelper.executeHttpPost(context, GetUserAssociatedRobots.METHOD_NAME, reqParams);
		if (getUserAssociatedRobotsResponse.completed()) { 
			try {
				Log.i(TAG, "authkey = " + authToken);
				String json = convertStreamToString(getUserAssociatedRobotsResponse.mResponseInputStream);
				Log.i(TAG, "JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<GetUserAssociatedRobotsResult>() {});
			} catch (JsonParseException e) {
				Log.e(TAG, "Exception in GetUserAssociatedRobotsResult", e);
				
			} catch (JsonMappingException e) {
				Log.e(TAG, "Exception in GetUserAssociatedRobotsResult", e);
				
			} catch (IOException e) {
				Log.e(TAG, "Exception in GetUserAssociatedRobotsResult", e);
			}
		}
		else { 
			result = new GetUserAssociatedRobotsResult(getUserAssociatedRobotsResponse);
		}
		
		return result;

	}

	 private static String convertStreamToString(InputStream is) {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
	 
	        String line = null;
	        try {
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                is.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return sb.toString();
	 }

	
}
