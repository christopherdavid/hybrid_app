package com.neatorobotics.android.slide.framework.webservice.robot.schedule;


import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.robotdata.RobotProfileConstants;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.MobileWebServiceClient;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebservicesHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.SetRobotProfileDetailsResult3;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.DeleteNeatoRobotScheduleData;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.GetScheduleBasedOnType;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.PostNeatoRobotScheduleData;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.NeatoRobotScheduleWebServicesAttributes.UpdateNeatoRobotScheduleData;

public class NeatoRobotScheduleWebservicesHelper {

	@SuppressWarnings("unused")
	private static final String TAG = NeatoRobotScheduleWebservicesHelper.class.getSimpleName();

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


	 public static DeleteNeatoRobotScheduleResult deleteNeatoRobotSchedule(Context context, String scheduleId) 
			 throws UserUnauthorizedException, NeatoServerException, IOException {
		Map<String, String> deleteNeatoRobotScheduleReqParams = new HashMap<String, String>();
		deleteNeatoRobotScheduleReqParams.put(DeleteNeatoRobotScheduleData.Attribute.ROBOT_SCHEDULE_ID, scheduleId);
		
		String response = MobileWebServiceClient.executeHttpPost(context, DeleteNeatoRobotScheduleData.METHOD_NAME, deleteNeatoRobotScheduleReqParams);
		return AppUtils.checkResponseResult(response, DeleteNeatoRobotScheduleResult.class);
	}
	 
	public static GetRobotScheduleByTypeResult getScheduleBasedOnType(Context context, String robotSerialNumber, String scheduleType)
		throws UserUnauthorizedException, NeatoServerException, IOException {		
		
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put(GetScheduleBasedOnType.Attribute.ROBOT_SERIAL_NUMBER, robotSerialNumber);
		requestParams.put(GetScheduleBasedOnType.Attribute.SCHEDULE_TYPE, scheduleType);		
		
		String response = MobileWebServiceClient.executeHttpPost(context, GetScheduleBasedOnType.METHOD_NAME, requestParams);
		return AppUtils.checkResponseResult(response, GetRobotScheduleByTypeResult.class);		
	}
	
	public static SetRobotProfileDetailsResult3 setEnableSchedule(Context context, String robotId, int scheduleType, boolean enableSchedule)
		throws UserUnauthorizedException, NeatoServerException, IOException {
		
		HashMap<String, String> setEnableScheduleReqParams = new HashMap<String, String>();
		String scheduleTypeKey = getScheduleFieldKeyToSetOnServer(scheduleType);
		if (TextUtils.isEmpty(scheduleTypeKey)) {
			throw new InvalidParameterException("Invalid Schedule Key");
		}
		
		setEnableScheduleReqParams.put(scheduleTypeKey, String.valueOf(enableSchedule));
		return NeatoRobotDataWebservicesHelper.setRobotProfileDetailsRequest3(context, robotId, setEnableScheduleReqParams);
	}
	
	private static String getScheduleFieldKeyToSetOnServer(int scheduleType) {
		String scheduleTypeInStr = RobotProfileConstants.getScheduleKey(scheduleType);
		
		if (!TextUtils.isEmpty(scheduleTypeInStr)) {
			return scheduleTypeInStr;
		}
		
		return null;
	}
	
}
