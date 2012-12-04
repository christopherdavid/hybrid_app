package com.neatorobotics.android.slide.framework.webservice.robot;

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
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.AssociateNeatoRobotToUser;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.CreateNeatoRobot;
import com.neatorobotics.android.slide.framework.webservice.robot.NeatoRobotWebServicesAttributes.GetRobotDetails;


public class NeatoRobotWebservicesHelper {
	private static final String TAG = NeatoRobotWebservicesHelper.class.getSimpleName();
	private static ObjectMapper resultMapper = new ObjectMapper();
	
	public static CreateNeatoRobotResult createNeatoRobotRequest(Context context, String name, String serial_number) {
		CreateNeatoRobotResult result = null;
		Map<String, String> createRobotReqParams = new HashMap<String, String>();
		createRobotReqParams.put(CreateNeatoRobot.Attribute.NAME, name);
		createRobotReqParams.put(CreateNeatoRobot.Attribute.SERIAL_NUMBER, serial_number);
	
		
		NeatoHttpResponse createRobotResponse = NeatoWebserviceHelper.executeHttpPost(context, CreateNeatoRobot.METHOD_NAME, createRobotReqParams);
		if (createRobotResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Creating Neato Robot completed. Reading response");
				result = resultMapper.readValue(createRobotResponse.mResponseInputStream, new TypeReference<CreateNeatoRobotResult>() {});
				LogHelper.log(TAG, "Creating robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in CreateNeatoRobotRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in CreateNeatoRobotRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in CreateNeatoRobotRequest" ,e);

			}
		}
		else { 
			LogHelper.log(TAG, "Create Neato Robot request not completed.");
			result = new CreateNeatoRobotResult(createRobotResponse);
		}
		
		return result;
	}

	public static AssociateNeatoRobotResult associateNeatoRobotRequest(Context context, String email, String serial_number) {
		AssociateNeatoRobotResult result = null;
		Map<String, String> associateRobotReqParams = new HashMap<String, String>();
		associateRobotReqParams.put(AssociateNeatoRobotToUser.Attribute.EMAIL, email);
		associateRobotReqParams.put(AssociateNeatoRobotToUser.Attribute.SERIAL_NUMBER, serial_number);
	
		
		NeatoHttpResponse associateRobotResponse = NeatoWebserviceHelper.executeHttpPost(context, AssociateNeatoRobotToUser.METHOD_NAME, associateRobotReqParams);
		if (associateRobotResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Associating Neato Robot completed. Reading response");
				result = resultMapper.readValue(associateRobotResponse.mResponseInputStream, new TypeReference<AssociateNeatoRobotResult>() {});
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
			result = new AssociateNeatoRobotResult(associateRobotResponse);
		}
		
		return result;
	}
	
	
	public static RobotDetailResult getRobotDetail(Context context, String serialNumber) {
		RobotDetailResult result = null;
		Map<String, String> robotGetDetailParams = new HashMap<String, String>();
		robotGetDetailParams.put(GetRobotDetails.Attribute.SERIAL_NUMBER, serialNumber);
	
		
		NeatoHttpResponse robotDetailResponse = NeatoWebserviceHelper.executeHttpPost(context, GetRobotDetails.METHOD_NAME, robotGetDetailParams);
		if (robotDetailResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Get Robot detail completed. Reading response");
				String robotDetailJson = convertStreamToString(robotDetailResponse.mResponseInputStream);
				LogHelper.logD(TAG, "robotDetailJson = " + robotDetailJson);
				result = resultMapper.readValue(robotDetailJson, new TypeReference<RobotDetailResult>() {});
				LogHelper.log(TAG, "Robot detail fetched .");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in getRobotDetail" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in getRobotDetail" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in getRobotDetail" ,e);

			}
		}
		else { 
			LogHelper.log(TAG, "Failed to get the Robot detail.");
			result = new RobotDetailResult(robotDetailResponse);
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
