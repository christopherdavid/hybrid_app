package com.neatorobotics.android.slide.framework.webservice.robot.map;

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
import android.text.TextUtils;
import android.util.Log;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.map.NeatoRobotMapWebServicesAttributes.DeleteNeatoRobotMapData;
import com.neatorobotics.android.slide.framework.webservice.robot.map.NeatoRobotMapWebServicesAttributes.GetNeatoRobotMapData;
import com.neatorobotics.android.slide.framework.webservice.robot.map.NeatoRobotMapWebServicesAttributes.GetNeatoRobotMaps;
import com.neatorobotics.android.slide.framework.webservice.robot.map.NeatoRobotMapWebServicesAttributes.PostNeatoRobotMapData;
import com.neatorobotics.android.slide.framework.webservice.robot.map.NeatoRobotMapWebServicesAttributes.UpdateNeatoRobotMapData;

public class NeatoRobotMapWebservicesHelper {
	private static final String TAG = NeatoRobotMapWebservicesHelper.class.getSimpleName();
	private static ObjectMapper resultMapper = new ObjectMapper();
	public static final String INVALID_VERSION_NUMBER = "-1";

	public static GetNeatoRobotMapsResult getNeatoRobotMapsRequest(Context context, String serial_number) {
		GetNeatoRobotMapsResult result = null;
		Map<String, String> getNeatoRobotMapsReqParams = new HashMap<String, String>();
		getNeatoRobotMapsReqParams.put(GetNeatoRobotMaps.Attribute.SERIAL_NUMBER, serial_number);


		NeatoHttpResponse getNeatoRobotMapsResponse = NeatoWebserviceHelper.executeHttpPost(context, GetNeatoRobotMaps.METHOD_NAME, getNeatoRobotMapsReqParams);
		if (getNeatoRobotMapsResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Get Maps for Neato Robot completed. Reading response");
				String json = convertStreamToString(getNeatoRobotMapsResponse.mResponseInputStream);
				Log.i(TAG, "JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<GetNeatoRobotMapsResult>() {});
				LogHelper.log(TAG, "Get Maps for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotMapsRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotMapsRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotMapsRequest" ,e);

			}
		}
		else { 
			LogHelper.log(TAG, " GetNeatoRobotMapsRequest not completed.");
			result = new GetNeatoRobotMapsResult(getNeatoRobotMapsResponse);
		}

		return result;
	}
	public static GetNeatoRobotMapDataResult getNeatoRobotMapDataRequest(Context context, String robot_map_id) {
		GetNeatoRobotMapDataResult result = null;
		Map<String, String> getNeatoRobotMapDataReqParams = new HashMap<String, String>();
		getNeatoRobotMapDataReqParams.put(GetNeatoRobotMapData.Attribute.ROBOT_MAP_ID, robot_map_id);


		NeatoHttpResponse getNeatoRobotMapDataResponse = NeatoWebserviceHelper.executeHttpPost(context, GetNeatoRobotMapData.METHOD_NAME, getNeatoRobotMapDataReqParams);
		if (getNeatoRobotMapDataResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Get map data for Neato Robot completed. Reading response");
				String json = convertStreamToString(getNeatoRobotMapDataResponse.mResponseInputStream);
				Log.i(TAG, "JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<GetNeatoRobotMapDataResult>() {});
				LogHelper.log(TAG, "Get map data for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotMapDataRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotMapDataRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotMapDataRequest" ,e);

			}
		}
		else { 
			LogHelper.log(TAG, "GetNeatoRobotMapDataRequest  not completed.");
			result = new GetNeatoRobotMapDataResult(getNeatoRobotMapDataResponse);
		}

		return result;
	}

	public static AddNeatoRobotMapDataResult addNeatoRobotMapData(Context context, String serial_number, 
					String xml_data, byte [] imageData) {
		AddNeatoRobotMapDataResult result = null;
		Map<String, String> postNeatoRobotMapDataReqParams = new HashMap<String, String>();
		postNeatoRobotMapDataReqParams.put(PostNeatoRobotMapData.Attribute.SERIAL_NUMBER, serial_number);

		if (!TextUtils.isEmpty(xml_data)) {
			postNeatoRobotMapDataReqParams.put(PostNeatoRobotMapData.Attribute.XML_DATA, xml_data);
		}
		
		if (imageData != null) {
			String blobData = AppUtils.convertToBase64(imageData);
			// LogHelper.log(TAG, "Base64 Encoding = " + blobData);
			postNeatoRobotMapDataReqParams.put(PostNeatoRobotMapData.Attribute.BLOB_DATA, blobData);
		}

		NeatoHttpResponse postNeatoRobotMapDataResponse = NeatoWebserviceHelper.executeHttpPost(context, PostNeatoRobotMapData.METHOD_NAME, postNeatoRobotMapDataReqParams);
		if (postNeatoRobotMapDataResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Add Map data for Neato Robot completed. Reading response");
				String json = convertStreamToString(postNeatoRobotMapDataResponse.mResponseInputStream);
				Log.i(TAG, "JSON = " + json);

				result = resultMapper.readValue(json, new TypeReference<AddNeatoRobotMapDataResult>() {});
				LogHelper.log(TAG, "Get Map data for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in AddNeatoRobotMapData" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in AddNeatoRobotMapData" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in AddNeatoRobotMapData" ,e);

			}
		}	
		else { 
			LogHelper.log(TAG, "AddNeatoRobotMapData  not completed.");
			result = new AddNeatoRobotMapDataResult(postNeatoRobotMapDataResponse);
		}

		return result;
	}
	
	
	public static UpdateNeatoRobotMapResult updateNeatoRobotMapDataRequest(Context context, String robot_map_id, 
					String xmlDataVersion, String xml_data, String imageDataVersion, byte [] imageData) {
		UpdateNeatoRobotMapResult result = null;
		Map<String, String> updateNeatoRobotMapDataReqParams = new HashMap<String, String>();
		updateNeatoRobotMapDataReqParams.put(UpdateNeatoRobotMapData.Attribute.ROBOT_MAP_ID, robot_map_id);
		if (!xmlDataVersion.equalsIgnoreCase(INVALID_VERSION_NUMBER) && !TextUtils.isEmpty(xml_data)) {
			LogHelper.log(TAG, "XML Version = " + xmlDataVersion);
			updateNeatoRobotMapDataReqParams.put(UpdateNeatoRobotMapData.Attribute.XML_DATA_VERSION, xmlDataVersion);
			updateNeatoRobotMapDataReqParams.put(UpdateNeatoRobotMapData.Attribute.XML_DATA, xml_data);
		}
		
		if (!imageDataVersion.equalsIgnoreCase(INVALID_VERSION_NUMBER) && (imageData != null)) {
			LogHelper.log(TAG, "Image Version = " + imageDataVersion);
			updateNeatoRobotMapDataReqParams.put(UpdateNeatoRobotMapData.Attribute.BLOB_DATA_VERSION, imageDataVersion);
			String blobData = AppUtils.convertToBase64(imageData);
			// LogHelper.log(TAG, "Base64 Encoding = " + blobData);
			updateNeatoRobotMapDataReqParams.put(UpdateNeatoRobotMapData.Attribute.BLOB_DATA, blobData);
		}

		NeatoHttpResponse updateNeatoRobotMapDataResponse = NeatoWebserviceHelper.executeHttpPost(context, UpdateNeatoRobotMapData.METHOD_NAME, updateNeatoRobotMapDataReqParams);
		if (updateNeatoRobotMapDataResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Updated Map data for Neato Robot completed. Reading response");
				String json = convertStreamToString(updateNeatoRobotMapDataResponse.mResponseInputStream);
				Log.i(TAG, "JSON = " + json);

				result = resultMapper.readValue(json, new TypeReference<UpdateNeatoRobotMapResult>() {});
				LogHelper.log(TAG, "Get Map data for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotMapDataRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotMapDataRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotMapDataRequest" ,e);

			}
		}
		else { 
			LogHelper.log(TAG, "GetNeatoRobotMapDataRequest  not completed.");
			result = new UpdateNeatoRobotMapResult(updateNeatoRobotMapDataResponse);
		}

		return result;
	}
	
	public static UpdateNeatoRobotMapResult updateNeatoMapXmlData(Context context, String robot_map_id, 
			String xmlDataVersion, String xml_data) {
		return updateNeatoRobotMapDataRequest(context, robot_map_id, xmlDataVersion, xml_data, INVALID_VERSION_NUMBER, null);
	}
	
	public static UpdateNeatoRobotMapResult updateNeatoMapBlobData(Context context, String robot_map_id, 
			String imageDataVersion, byte [] imageData) {
		return updateNeatoRobotMapDataRequest(context, robot_map_id, INVALID_VERSION_NUMBER, null, imageDataVersion, imageData);
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
	 
	 public static DeleteNeatoRobotMapsResult deleteNeatoRobotMap(Context context, String mapId) {
		 DeleteNeatoRobotMapsResult result = null;
			Map<String, String> deleteNeatoRobotMapReqParams = new HashMap<String, String>();
			deleteNeatoRobotMapReqParams.put(DeleteNeatoRobotMapData.Attribute.ROBOT_MAP_ID, mapId);

			NeatoHttpResponse deleteNeatoRobotMapResponse = NeatoWebserviceHelper.executeHttpPost(context, DeleteNeatoRobotMapData.METHOD_NAME, deleteNeatoRobotMapReqParams);
			if (deleteNeatoRobotMapResponse.completed()) { 
				try {
					LogHelper.logD(TAG, "Deleting Maps for Neato Robot completed. Reading response");
					String json = convertStreamToString(deleteNeatoRobotMapResponse.mResponseInputStream);
					Log.i(TAG, "JSON = " + json);
					result = resultMapper.readValue(json, new TypeReference<DeleteNeatoRobotMapsResult>() {});
					LogHelper.log(TAG, "Delete Map for robot completed.");
				} catch (JsonParseException e) {
					LogHelper.log(TAG, "Exception in deleteNeatoRobotMap" ,e);

				} catch (JsonMappingException e) {
					LogHelper.log(TAG, "Exception in deleteNeatoRobotMap" ,e);

				} catch (IOException e) {
					LogHelper.log(TAG, "Exception in deleteNeatoRobotMap" ,e);

				}
			}
			else { 
				LogHelper.log(TAG, " deleteNeatoRobotMap not completed.");
				result = new DeleteNeatoRobotMapsResult(deleteNeatoRobotMapResponse);
			}

			return result;
		}

}
