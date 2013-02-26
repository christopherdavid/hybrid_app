package com.neatorobotics.android.slide.framework.webservice.robot.atlas;

import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceHelper;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.RobotAtlasWebservicesAttributes.GetRobotAtlasData;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.RobotAtlasWebservicesAttributes.AddUpdateRobotAtlasData;

public class RobotAtlasWebservicesHelper {

	public static final String INVALID_VERSION_NUMBER = "-1";

	public static AddUpdateRobotAtlasMetadataResult addRobotAtlasRequest(Context context, String serial_number, String atlas_data) {
		AddUpdateRobotAtlasMetadataResult result = null;
		Map<String, String>addRobotAtlasReqParams = new HashMap<String, String>();
		addRobotAtlasReqParams.put(AddUpdateRobotAtlasData.Attribute.ROBOT_ID, serial_number);
		addRobotAtlasReqParams.put(AddUpdateRobotAtlasData.Attribute.XML_DATA, atlas_data);
		addRobotAtlasReqParams.put(AddUpdateRobotAtlasData.Attribute.ATLAS_ID, "0");
		addRobotAtlasReqParams.put(AddUpdateRobotAtlasData.Attribute.XML_DATA_VERSION, "0");
		NeatoHttpResponse addRobotAtlasResponse = NeatoWebserviceHelper.executeHttpPost(context, AddUpdateRobotAtlasData.METHOD_NAME, addRobotAtlasReqParams);
		result = NeatoWebserviceUtils.readValueHelper(addRobotAtlasResponse, AddUpdateRobotAtlasMetadataResult.class);

		return result;
	}
	public static GetRobotAtlasDataResult getRobotAtlasDataRequest(Context context, String robotId) {
		GetRobotAtlasDataResult result = null;
		Map<String, String> getRobotAtlasReqParams = new HashMap<String, String>();
		getRobotAtlasReqParams.put(GetRobotAtlasData.Attribute.ROBOT_ID, robotId);
		NeatoHttpResponse getRobotAtlasDataResponse = NeatoWebserviceHelper.executeHttpPost(context, GetRobotAtlasData.METHOD_NAME, getRobotAtlasReqParams);
		result = NeatoWebserviceUtils.readValueHelper(getRobotAtlasDataResponse, GetRobotAtlasDataResult.class);
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
		result = NeatoWebserviceUtils.readValueHelper(updateRobotAtlasDataResponse, AddUpdateRobotAtlasMetadataResult.class);
		return result;
	}
}
