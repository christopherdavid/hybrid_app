package com.neatorobotics.android.slide.framework.webservice.robot.schedule;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;

import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.MobileWebServiceClient;
import com.neatorobotics.android.slide.framework.webservice.NeatoHttpResponse;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceHelper;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceUtils;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.DeleteNeatoRobotScheduleData;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.GetNeatoRobotScheduleData;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.GetNeatoRobotSchedules;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.GetScheduleBasedOnType;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.PostNeatoRobotScheduleData;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.UpdateNeatoRobotScheduleData;

public class NeatoRobotScheduleWebservicesHelper {

	public static GetNeatoRobotSchedulesResult getNeatoRobotSchedulesRequest(Context context, String serial_number) {
		GetNeatoRobotSchedulesResult result = null;
		Map<String, String> getNeatoRobotSchedulesReqParams = new HashMap<String, String>();
		getNeatoRobotSchedulesReqParams.put(GetNeatoRobotSchedules.Attribute.SERIAL_NUMBER, serial_number);
		NeatoHttpResponse getNeatoRobotSchedulesResponse = NeatoWebserviceHelper.executeHttpPost(context, GetNeatoRobotSchedules.METHOD_NAME, getNeatoRobotSchedulesReqParams);
		result = NeatoWebserviceUtils.readValueHelper(getNeatoRobotSchedulesResponse, GetNeatoRobotSchedulesResult.class);
		return result;
	}

	public static GetNeatoRobotScheduleDataResult getNeatoRobotScheduleDataRequest(Context context, String robot_schedule_id) {
		GetNeatoRobotScheduleDataResult result = null;
		Map<String, String> getNeatoRobotScheduleDataReqParams = new HashMap<String, String>();
		getNeatoRobotScheduleDataReqParams.put(GetNeatoRobotScheduleData.Attribute.ROBOT_SCHEDULE_ID, robot_schedule_id);
		NeatoHttpResponse getNeatoRobotScheduleDataResponse = NeatoWebserviceHelper.executeHttpPost(context, GetNeatoRobotScheduleData.METHOD_NAME, getNeatoRobotScheduleDataReqParams);
		result = NeatoWebserviceUtils.readValueHelper(getNeatoRobotScheduleDataResponse, GetNeatoRobotScheduleDataResult.class);

		return result;
	}

	// TODO : right now i have set blob data type to String. Not really sure.
	public static AddNeatoRobotScheduleDataResult addNeatoRobotScheduleDataRequest(Context context, String serial_number,String schedule_type ,String xml_data, String blob_data)
		throws UserUnauthorizedException, NeatoServerException, IOException {
		
		Map<String, String> postNeatoRobotScheduleDataReqParams = new HashMap<String, String>();
		postNeatoRobotScheduleDataReqParams.put(PostNeatoRobotScheduleData.Attribute.SERIAL_NUMBER, serial_number);
		postNeatoRobotScheduleDataReqParams.put(PostNeatoRobotScheduleData.Attribute.SCHEDULE_TYPE, schedule_type);
		postNeatoRobotScheduleDataReqParams.put(PostNeatoRobotScheduleData.Attribute.XML_DATA, xml_data);
		String response = MobileWebServiceClient.executeHttpPost(context, PostNeatoRobotScheduleData.METHOD_NAME, postNeatoRobotScheduleDataReqParams);
		return AppUtils.checkResponseResult(response, AddNeatoRobotScheduleDataResult.class);
	}

	public static UpdateNeatoRobotScheduleResult updateNeatoRobotScheduleDataRequest(Context context, String robot_schedule_id, String schedule_type , String xml_data, String xml_data_version)
		throws UserUnauthorizedException, NeatoServerException, IOException {
		
		Map<String, String> updateNeatoRobotScheduleDataReqParams = new HashMap<String, String>();
		updateNeatoRobotScheduleDataReqParams.put(UpdateNeatoRobotScheduleData.Attribute.ROBOT_SCHEDULE_ID, robot_schedule_id);
		updateNeatoRobotScheduleDataReqParams.put(UpdateNeatoRobotScheduleData.Attribute.SCHEDULE_TYPE, schedule_type);
		updateNeatoRobotScheduleDataReqParams.put(UpdateNeatoRobotScheduleData.Attribute.XML_DATA_VERSION, xml_data_version);
		updateNeatoRobotScheduleDataReqParams.put(UpdateNeatoRobotScheduleData.Attribute.XML_DATA, xml_data);
		String response = MobileWebServiceClient.executeHttpPost(context, UpdateNeatoRobotScheduleData.METHOD_NAME, updateNeatoRobotScheduleDataReqParams);
		return AppUtils.checkResponseResult(response, UpdateNeatoRobotScheduleResult.class);
	}


	 public static DeleteNeatoRobotScheduleResult deleteNeatoRobotSchedule(Context context, String scheduleId) {
	 	DeleteNeatoRobotScheduleResult result = null;
		Map<String, String> deleteNeatoRobotScheduleReqParams = new HashMap<String, String>();
		deleteNeatoRobotScheduleReqParams.put(DeleteNeatoRobotScheduleData.Attribute.ROBOT_SCHEDULE_ID, scheduleId);
		NeatoHttpResponse deleteNeatoRobotScheduleResponse = NeatoWebserviceHelper.executeHttpPost(context, DeleteNeatoRobotScheduleData.METHOD_NAME, deleteNeatoRobotScheduleReqParams);
		result = NeatoWebserviceUtils.readValueHelper(deleteNeatoRobotScheduleResponse, DeleteNeatoRobotScheduleResult.class);
		return result;
	}
	 
	public static GetRobotScheduleByTypeResult getScheduleBasedOnType(Context context, String robotSerialNumber, String scheduleType)
		throws UserUnauthorizedException, NeatoServerException, IOException {		
		
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put(GetScheduleBasedOnType.Attribute.ROBOT_SERIAL_NUMBER, robotSerialNumber);
		requestParams.put(GetScheduleBasedOnType.Attribute.SCHEDULE_TYPE, scheduleType);		
		
		String response = MobileWebServiceClient.executeHttpPost(context, GetScheduleBasedOnType.METHOD_NAME, requestParams);
		return AppUtils.checkResponseResult(response, GetRobotScheduleByTypeResult.class);		
	}
}
