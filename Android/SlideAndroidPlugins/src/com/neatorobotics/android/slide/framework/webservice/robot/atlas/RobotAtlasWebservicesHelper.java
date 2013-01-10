package com.neatorobotics.android.slide.framework.webservice.robot.atlas;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import android.content.Context;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.RobotAtlasWebservicesAttributes.GetRobotAtlasData;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.RobotAtlasWebservicesAttributes.AddUpdateRobotAtlasData;

public class RobotAtlasWebservicesHelper {

	private static final String TAG = RobotAtlasWebservicesHelper.class.getSimpleName();
	private static ObjectMapper resultMapper = new ObjectMapper();
	public static final String INVALID_VERSION_NUMBER = "-1";

	public static AddUpdateRobotAtlasMetadataResult addRobotAtlasRequest(Context context, String serial_number, String atlas_data) {
		AddUpdateRobotAtlasMetadataResult result = null;
		Map<String, String>addRobotAtlasReqParams = new HashMap<String, String>();
		addRobotAtlasReqParams.put(AddUpdateRobotAtlasData.Attribute.ROBOT_ID, serial_number);
		addRobotAtlasReqParams.put(AddUpdateRobotAtlasData.Attribute.XML_DATA, atlas_data);
		addRobotAtlasReqParams.put(AddUpdateRobotAtlasData.Attribute.ATLAS_ID, "0");
		addRobotAtlasReqParams.put(AddUpdateRobotAtlasData.Attribute.XML_DATA_VERSION, "0");

		NeatoHttpResponse addRobotAtlasResponse = NeatoWebserviceHelper.executeHttpPost(context, AddUpdateRobotAtlasData.METHOD_NAME, addRobotAtlasReqParams);
		if (addRobotAtlasResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Add Atlas for Neato Robot completed. Reading response");
				String json = AppUtils.convertStreamToString(addRobotAtlasResponse.mResponseInputStream);
				LogHelper.logD(TAG, "JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<AddUpdateRobotAtlasMetadataResult>() {});
				LogHelper.log(TAG, "Add Atlas for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in addRobotAtlasRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in addRobotAtlasRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in addRobotAtlasRequest" ,e);

			}
		}
		else { 
			LogHelper.log(TAG, " addRobotAtlasRequest not completed.");
			result = new AddUpdateRobotAtlasMetadataResult(addRobotAtlasResponse);
		}

		return result;
	}
	public static GetRobotAtlasDataResult getRobotAtlasDataRequest(Context context, String robotId) {
		GetRobotAtlasDataResult result = null;
		Map<String, String> getRobotAtlasReqParams = new HashMap<String, String>();
		getRobotAtlasReqParams.put(GetRobotAtlasData.Attribute.ROBOT_ID, robotId);

		NeatoHttpResponse getRobotAtlasDataResponse = NeatoWebserviceHelper.executeHttpPost(context, GetRobotAtlasData.METHOD_NAME, getRobotAtlasReqParams);
		if (getRobotAtlasDataResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Get map data for Neato Robot completed. Reading response");
				String json = AppUtils.convertStreamToString(getRobotAtlasDataResponse.mResponseInputStream);
				LogHelper.logD(TAG, "JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<GetRobotAtlasDataResult>() {});
				LogHelper.log(TAG, "Get atlas data for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in getRobotAtlasDataRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in getRobotAtlasDataRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in getRobotAtlasDataRequest" ,e);
			}
		}
		else { 
			LogHelper.log(TAG, "getRobotAtlasDataRequest  not completed.");
			result = new GetRobotAtlasDataResult(getRobotAtlasDataResponse);
		}

		return result;
	}

	public static AddUpdateRobotAtlasMetadataResult updateRobotAtlasDataRequest(Context context, String atlas_id, 
			String xml_data, String xml_data_version) {
		AddUpdateRobotAtlasMetadataResult result = null;
		Map<String, String> updateRobotAtlasDataReqParams = new HashMap<String, String>();
		updateRobotAtlasDataReqParams.put(AddUpdateRobotAtlasData.Attribute.ATLAS_ID, atlas_id);
		updateRobotAtlasDataReqParams.put(AddUpdateRobotAtlasData.Attribute.XML_DATA_VERSION, xml_data_version);
		if (!TextUtils.isEmpty(xml_data)) {
			updateRobotAtlasDataReqParams.put(AddUpdateRobotAtlasData.Attribute.XML_DATA, xml_data);
		}
		NeatoHttpResponse updateRobotAtlasDataResponse = NeatoWebserviceHelper.executeHttpPost(context, AddUpdateRobotAtlasData.METHOD_NAME, updateRobotAtlasDataReqParams);
		if (updateRobotAtlasDataResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Update atlas data for Neato Robot completed. Reading response");
				String json = AppUtils.convertStreamToString(updateRobotAtlasDataResponse.mResponseInputStream);
				LogHelper.logD(TAG, "JSON = " + json);

				result = resultMapper.readValue(json, new TypeReference<AddUpdateRobotAtlasMetadataResult>() {});
				LogHelper.log(TAG, "Update atlas data for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in updateRobotAtlasData" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in updateRobotAtlasData" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in updateRobotAtlasData" ,e);

			}
		}	
		else { 
			LogHelper.log(TAG, "updateRobotAtlasData  not completed.");
			result = new AddUpdateRobotAtlasMetadataResult(updateRobotAtlasDataResponse);
		}

		return result;
	}
}
