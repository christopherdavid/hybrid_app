package com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid;


import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceHelper;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.RobotAtlasGridWebServicesAttributes.GetAtlasGridMetadata;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.RobotAtlasGridWebServicesAttributes.PostGridImage;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.RobotAtlasGridWebServicesAttributes.UpdateGridImage;

public class RobotAtlasGridWebservicesHelper {

	public static final String INVALID_VERSION_NUMBER = "-1";

	public static GetAtlasGridDataResult getAtlasGridDataRequest(Context context, String atlas_id) {
		GetAtlasGridDataResult result = null;
		Map<String, String>getAtlasGridMetaDataReqParams = new HashMap<String, String>();
		getAtlasGridMetaDataReqParams.put(GetAtlasGridMetadata.Attribute.ATLAS_ID, atlas_id);
		NeatoHttpResponse getAtlasGridMetaDataResponse = NeatoWebserviceHelper.executeHttpPost(context, GetAtlasGridMetadata.METHOD_NAME, getAtlasGridMetaDataReqParams);
		result = NeatoWebserviceUtils.readValueHelper(getAtlasGridMetaDataResponse, GetAtlasGridDataResult.class);
		return result;
	}
}
