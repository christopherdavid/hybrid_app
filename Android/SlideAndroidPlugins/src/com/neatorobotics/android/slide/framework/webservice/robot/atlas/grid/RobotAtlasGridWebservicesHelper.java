package com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid;


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
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.RobotAtlasGridWebServicesAttributes.GetAtlasGridMetadata;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.RobotAtlasGridWebServicesAttributes.PostGridImage;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.RobotAtlasGridWebServicesAttributes.UpdateGridImage;

public class RobotAtlasGridWebservicesHelper {


	private static final String TAG = RobotAtlasGridWebservicesHelper.class.getSimpleName();
	private static ObjectMapper resultMapper = new ObjectMapper();
	public static final String INVALID_VERSION_NUMBER = "-1";

	public static PostGridImageResult postGridImageRequest(Context context, String atlas_id, String grid_id, String blob_data) {
		PostGridImageResult result = null;
		Map<String, String>postGridImageReqParams = new HashMap<String, String>();
		postGridImageReqParams.put(PostGridImage.Attribute.ATLAS_ID, atlas_id);
		postGridImageReqParams.put(PostGridImage.Attribute.GRID_ID, grid_id);
		postGridImageReqParams.put(PostGridImage.Attribute.BLOB_DATA, blob_data);

		NeatoHttpResponse postGridImageResponse = NeatoWebserviceHelper.executeHttpPost(context, PostGridImage.METHOD_NAME, postGridImageReqParams);
		if (postGridImageResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Post Grid image for Neato Robot completed. Reading response");
				String json = AppUtils.convertStreamToString(postGridImageResponse.mResponseInputStream);
				LogHelper.logD(TAG, "JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<PostGridImageResult>() {});
				LogHelper.log(TAG, "Post Grid Image for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in postGridImageRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in postGridImageRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in postGridImageRequest" ,e);

			}
		}
		else { 
			LogHelper.log(TAG, " postGridImageRequest not completed.");
			result = new PostGridImageResult(postGridImageResponse);
		}
		return result;
	}
	
	public static UpdateGridImageResult updateGridImageRequest(Context context, String atlas_id, String grid_id, String blob_data) {
		UpdateGridImageResult result = null;
		Map<String, String> updateGridImageReqParams = new HashMap<String, String>();
		updateGridImageReqParams.put(UpdateGridImage.Attribute.ATLAS_ID, atlas_id);
		updateGridImageReqParams.put(UpdateGridImage.Attribute.GRID_ID, grid_id);
		updateGridImageReqParams.put(UpdateGridImage.Attribute.BLOB_DATA, blob_data);

		NeatoHttpResponse updateGridImageResponse = NeatoWebserviceHelper.executeHttpPost(context, UpdateGridImage.METHOD_NAME, updateGridImageReqParams);
		if (updateGridImageResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Update Grid for Neato Robot completed. Reading response");
				String json = AppUtils.convertStreamToString(updateGridImageResponse.mResponseInputStream);
				LogHelper.logD(TAG, "JSON = " + json);
				result = resultMapper.readValue(json, new TypeReference<UpdateGridImageResult>() {});
				LogHelper.log(TAG, "Update Grid Data for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in updateGridImageRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in updateGridImageRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in updateGridImageRequest" ,e);
			}
		}
		else { 
			LogHelper.log(TAG, "updateGridImageRequest  not completed.");
			result = new UpdateGridImageResult(updateGridImageResponse);
		}
		return result;
	}

	public static GetAtlasGridDataResult getAtlasGridDataRequest(Context context, String atlas_id) {
		GetAtlasGridDataResult result = null;
		Map<String, String>getAtlasGridMetaDataReqParams = new HashMap<String, String>();
		getAtlasGridMetaDataReqParams.put(GetAtlasGridMetadata.Attribute.ATLAS_ID, atlas_id);

		NeatoHttpResponse getAtlasGridMetaDataResponse = NeatoWebserviceHelper.executeHttpPost(context, GetAtlasGridMetadata.METHOD_NAME, getAtlasGridMetaDataReqParams);
		if (getAtlasGridMetaDataResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Get atlas grid metadata for Neato Robot completed. Reading response");
				String json = AppUtils.convertStreamToString(getAtlasGridMetaDataResponse.mResponseInputStream);
				LogHelper.logD(TAG, "JSON = " + json);

				result = resultMapper.readValue(json, new TypeReference<GetAtlasGridDataResult>() {});
				LogHelper.log(TAG, "Get atlas grid metadata for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in getAtlasGridMetadataRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in getAtlasGridMetadataRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in getAtlasGridMetadataRequest" ,e);

			}
		}	
		else { 
			LogHelper.log(TAG, "getAtlasGridMetadataRequest  not completed.");
			result = new GetAtlasGridDataResult(getAtlasGridMetaDataResponse);
		}

		return result;
	}
}
