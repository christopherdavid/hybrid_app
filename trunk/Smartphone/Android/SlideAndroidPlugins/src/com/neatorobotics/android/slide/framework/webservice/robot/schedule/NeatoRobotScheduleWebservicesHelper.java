package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

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
import com.neatorobotics.android.slide.framework.webservice.robot.map.DeleteNeatoRobotMapsResult;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.DeleteNeatoRobotScheduleData;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.GetNeatoRobotScheduleData;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.GetNeatoRobotSchedules;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.PostNeatoRobotScheduleData;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.UpdateNeatoRobotScheduleData;

public class NeatoRobotScheduleWebservicesHelper {
	private static final String TAG = NeatoRobotScheduleWebservicesHelper.class.getSimpleName();
	private static ObjectMapper resultMapper = new ObjectMapper();



	public static GetNeatoRobotSchedulesResult getNeatoRobotSchedulesRequest(Context context, String serial_number) {
		GetNeatoRobotSchedulesResult result = null;
		Map<String, String> getNeatoRobotSchedulesReqParams = new HashMap<String, String>();
		getNeatoRobotSchedulesReqParams.put(GetNeatoRobotSchedules.Attribute.SERIAL_NUMBER, serial_number);


		NeatoHttpResponse getNeatoRobotSchedulesResponse = NeatoWebserviceHelper.executeHttpPost(context, GetNeatoRobotSchedules.METHOD_NAME, getNeatoRobotSchedulesReqParams);
		if (getNeatoRobotSchedulesResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Get Schedules for Neato Robot completed. Reading response");
				result = resultMapper.readValue(getNeatoRobotSchedulesResponse.mResponseInputStream, new TypeReference<GetNeatoRobotSchedulesResult>() {});
				LogHelper.log(TAG, "Get Schedules for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotSchedulesRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotSchedulesRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotSchedulesRequest" ,e);

			}
		}
		else { 
			LogHelper.log(TAG, "GetNeatoRobotSchedulesRequest  not completed.");
			result = new GetNeatoRobotSchedulesResult(getNeatoRobotSchedulesResponse);
		}

		return result;
	}

	public static GetNeatoRobotScheduleDataResult getNeatoRobotScheduleDataRequest(Context context, String robot_schedule_id) {
		GetNeatoRobotScheduleDataResult result = null;
		Map<String, String> getNeatoRobotScheduleDataReqParams = new HashMap<String, String>();
		getNeatoRobotScheduleDataReqParams.put(GetNeatoRobotScheduleData.Attribute.ROBOT_SCHEDULE_ID, robot_schedule_id);


		NeatoHttpResponse getNeatoRobotScheduleDataResponse = NeatoWebserviceHelper.executeHttpPost(context, GetNeatoRobotScheduleData.METHOD_NAME, getNeatoRobotScheduleDataReqParams);
		if (getNeatoRobotScheduleDataResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Get Schedule data for Neato Robot completed. Reading response");
				result = resultMapper.readValue(getNeatoRobotScheduleDataResponse.mResponseInputStream, new TypeReference<GetNeatoRobotSchedulesResult>() {});
				LogHelper.log(TAG, "Get Schedule data for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotScheduleDataRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotScheduleDataRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotScheduleDataRequest" ,e);

			}
		}
		else { 
			LogHelper.log(TAG, "GetNeatoRobotScheduleDataRequest  not completed.");
			result = new GetNeatoRobotScheduleDataResult(getNeatoRobotScheduleDataResponse);
		}

		return result;
	}

	//TODO : right now i have set blog data type to String. Not really sure.
	public static AddNeatoRobotScheduleDataResult addNeatoRobotScheduleDataRequest(Context context, String serial_number,String schedule_type ,String xml_data, String blob_data) {
		AddNeatoRobotScheduleDataResult result = null;
		Map<String, String> postNeatoRobotScheduleDataReqParams = new HashMap<String, String>();
		postNeatoRobotScheduleDataReqParams.put(PostNeatoRobotScheduleData.Attribute.SERIAL_NUMBER, serial_number);
		postNeatoRobotScheduleDataReqParams.put(PostNeatoRobotScheduleData.Attribute.SCHEDULE_TYPE, schedule_type);

		postNeatoRobotScheduleDataReqParams.put(PostNeatoRobotScheduleData.Attribute.XML_DATA, xml_data);

		NeatoHttpResponse postNeatoRobotScheduleDataResponse = NeatoWebserviceHelper.executeHttpPost(context, PostNeatoRobotScheduleData.METHOD_NAME, postNeatoRobotScheduleDataReqParams);
		if (postNeatoRobotScheduleDataResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Post Schedule data for Neato Robot completed. Reading response");
				result = resultMapper.readValue(postNeatoRobotScheduleDataResponse.mResponseInputStream, new TypeReference<AddNeatoRobotScheduleDataResult>() {});
				LogHelper.log(TAG, "Post Schedule data for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in PostNeatoRobotScheduleDataRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in PostNeatoRobotScheduleDataRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in PostNeatoRobotScheduleDataRequest" ,e);

			}
		}	else { 
			LogHelper.log(TAG, "PostNeatoRobotScheduleDataRequest  not completed.");
			result = new AddNeatoRobotScheduleDataResult(postNeatoRobotScheduleDataResponse);
		}

		return result;
	}

	public static UpdateNeatoRobotScheduleResult updateNeatoRobotScheduleDataRequest(Context context, String robot_schedule_id, String schedule_type , String xml_data, String xml_data_version) {
		UpdateNeatoRobotScheduleResult result = null;
		Map<String, String> updateNeatoRobotScheduleDataReqParams = new HashMap<String, String>();
		updateNeatoRobotScheduleDataReqParams.put(UpdateNeatoRobotScheduleData.Attribute.ROBOT_SCHEDULE_ID, robot_schedule_id);
		updateNeatoRobotScheduleDataReqParams.put(UpdateNeatoRobotScheduleData.Attribute.SCHEDULE_TYPE, schedule_type);
		updateNeatoRobotScheduleDataReqParams.put(UpdateNeatoRobotScheduleData.Attribute.XML_DATA_VERSION, xml_data_version);
		updateNeatoRobotScheduleDataReqParams.put(UpdateNeatoRobotScheduleData.Attribute.XML_DATA, xml_data);

		NeatoHttpResponse updateNeatoRobotScheduleDataResponse = NeatoWebserviceHelper.executeHttpPost(context, UpdateNeatoRobotScheduleData.METHOD_NAME, updateNeatoRobotScheduleDataReqParams);
		if (updateNeatoRobotScheduleDataResponse.completed()) { 
			try {
				LogHelper.logD(TAG, "Update Schedule data for Neato Robot completed. Reading response");
				result = resultMapper.readValue(updateNeatoRobotScheduleDataResponse.mResponseInputStream, new TypeReference<UpdateNeatoRobotScheduleResult>() {});
				LogHelper.log(TAG, "Update Schedule data for robot completed.");
			} catch (JsonParseException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotScheduleDataRequest" ,e);

			} catch (JsonMappingException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotScheduleDataRequest" ,e);

			} catch (IOException e) {
				LogHelper.log(TAG, "Exception in GetNeatoRobotScheduleDataRequest" ,e);

			}
		}
		else { 
			LogHelper.log(TAG, "GetNeatoRobotScheduleDataRequest  not completed.");
			result = new UpdateNeatoRobotScheduleResult(updateNeatoRobotScheduleDataResponse);
		}

		return result;
	}


	 public static DeleteNeatoRobotScheduleResult deleteNeatoRobotSchedule(Context context, String scheduleId) {
		 	DeleteNeatoRobotScheduleResult result = null;
			Map<String, String> deleteNeatoRobotScheduleReqParams = new HashMap<String, String>();
			deleteNeatoRobotScheduleReqParams.put(DeleteNeatoRobotScheduleData.Attribute.ROBOT_SCHEDULE_ID, scheduleId);

			NeatoHttpResponse deleteNeatoRobotScheduleResponse = NeatoWebserviceHelper.executeHttpPost(context, DeleteNeatoRobotScheduleData.METHOD_NAME, deleteNeatoRobotScheduleReqParams);
			if (deleteNeatoRobotScheduleResponse.completed()) { 
				try {
					LogHelper.logD(TAG, "Deleting schedule for Neato Robot completed. Reading response");
					String json = convertStreamToString(deleteNeatoRobotScheduleResponse.mResponseInputStream);
					Log.i(TAG, "JSON = " + json);
					result = resultMapper.readValue(json, new TypeReference<DeleteNeatoRobotMapsResult>() {});
					LogHelper.log(TAG, "Delete schedule for robot completed.");
				} catch (JsonParseException e) {
					LogHelper.log(TAG, "Exception in DeleteNeatoRobotSchedule" ,e);

				} catch (JsonMappingException e) {
					LogHelper.log(TAG, "Exception in DeleteNeatoRobotSchedule" ,e);

				} catch (IOException e) {
					LogHelper.log(TAG, "Exception in DeleteNeatoRobotSchedule" ,e);

				}
			}
			else { 
				LogHelper.log(TAG, " DeleteNeatoRobotSchedule not completed.");
				result = new DeleteNeatoRobotScheduleResult(deleteNeatoRobotScheduleResponse);
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
