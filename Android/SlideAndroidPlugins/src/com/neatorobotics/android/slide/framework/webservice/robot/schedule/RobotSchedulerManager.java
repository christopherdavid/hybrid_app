package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import android.content.Context;

import com.neatorobotics.android.slide.framework.http.download.FileCachePath;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadHelper;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadListener;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedRobotSchedule;
import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedScheduleGroup;
import com.neatorobotics.android.slide.framework.robot.schedule.ScheduleXmlHelper;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;


public class RobotSchedulerManager {
	private static final String TAG = RobotSchedulerManager.class.getSimpleName();	
	Context mContext;
	private static RobotSchedulerManager sRobotSchedulerManager;
	private static final Object INSTANCE_LOCK = new Object();

	private RobotSchedulerManager(Context context)
	{
		mContext = context.getApplicationContext();
	}
	
	public static RobotSchedulerManager getInstance(Context context)
	{
		synchronized (INSTANCE_LOCK) {
			if (sRobotSchedulerManager == null) {
				sRobotSchedulerManager = new RobotSchedulerManager(context);
			}
		}

		return sRobotSchedulerManager;
	}


	// Public helper method to set the Robot schedule on the server. 
	public void sendRobotSchedule(AdvancedScheduleGroup robotScheduleGroup, final String serial_number, final ScheduleWebserviceListener scheduleDetailsListener) {
		final String xmlSchedule = robotScheduleGroup.getXml();
		final String blobData = robotScheduleGroup.getBlobData();
		final String scheduleType = NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_ADVANCED;

		Runnable task = new Runnable() {

			public void run() {
				ScheduleWebserviceListener scheduleListener  = scheduleDetailsListener;
				String currentScheduleId = null;
				String currentxml_version = null;
				GetNeatoRobotSchedulesResult schedulesResult = NeatoRobotScheduleWebservicesHelper.getNeatoRobotSchedulesRequest(mContext, serial_number);
				if (schedulesResult != null && schedulesResult.success()) {
					int scheduleListSize = schedulesResult.mResult.size();

					//TODO: This is under assumption that the schedules are sent in descending order.
					for (int scheduleIterator = (scheduleListSize-1); scheduleIterator >= 0 ; scheduleIterator--) {
						if (NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_ADVANCED.equals(schedulesResult.mResult.get(scheduleIterator).mSchedule_Type)) {
							currentScheduleId = schedulesResult.mResult.get(scheduleIterator).mId;
							currentxml_version = schedulesResult.mResult.get(scheduleIterator).mXml_Data_Version;
							LogHelper.log(TAG, "Current schedule id is: "+currentScheduleId);
							break;
						}
					}
				} else {
					LogHelper.log(TAG, "No scheduling data exists.");
				}

				if (currentScheduleId == null) {
					AddNeatoRobotScheduleDataResult result = NeatoRobotScheduleWebservicesHelper.addNeatoRobotScheduleDataRequest(mContext, serial_number, scheduleType, xmlSchedule, blobData);
					if (result != null && result.success()) {
						LogHelper.log(TAG, "Sucessfully posted scheduling data with robot schedule id:"+result.mResult.mRobot_Schedule_Id);
						if (scheduleListener != null) {
							scheduleListener.onSuccess();
						}
					} else {
						LogHelper.log(TAG, "Error: "+result.mMessage);
						if (scheduleListener != null) {
							scheduleListener.onNetworkError();
						}
					}
				} else {
					// Robot schedule exists. Hence update schedule
					LogHelper.log(TAG, "Currrent scheduel ID: :" + currentScheduleId);
					UpdateNeatoRobotScheduleResult result = NeatoRobotScheduleWebservicesHelper.updateNeatoRobotScheduleDataRequest(mContext, currentScheduleId, NeatoRobotScheduleWebServicesAttributes.SCHEDULE_TYPE_ADVANCED, xmlSchedule, currentxml_version);
					if (result != null && result.success()) {
						LogHelper.log(TAG, "Sucessfully updated scheduling data with robot schedule id: " + currentScheduleId);
						if (scheduleListener != null) {
							scheduleListener.onSuccess();
						}
					} else {
						//TODO: Take into account server error also.
						LogHelper.log(TAG, "Error in updating schedule");
						if (scheduleListener != null) {
							scheduleListener.onNetworkError();
						}
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}


	public void sendRobotSchedule(AdvancedRobotSchedule robotSchedule, final String robotId, ScheduleWebserviceListener scheduleDetailsListener) {
		AdvancedScheduleGroup robotScheduleGroup = new AdvancedScheduleGroup();
		robotScheduleGroup.addSchedule(robotSchedule);
		sendRobotSchedule(robotScheduleGroup, robotId, scheduleDetailsListener);
	}

	public void getRobotSchedule(final String robotId, final GetScheduleListener listener) {

		Runnable task = new Runnable() {

			@Override
			public void run() {
				GetNeatoRobotSchedulesResult result = NeatoRobotScheduleWebservicesHelper.getNeatoRobotSchedulesRequest(mContext, robotId);		
				
				if (result == null) {
					listener.onError("");
					return;
				}
				
				if (!result.success()) {
					LogHelper.log(TAG, "Error: "+ result.mMessage);
					listener.onError(result.mMessage);
					return;
				}
				
				if (result.mResult.size() == 0) {
					LogHelper.log(TAG, "No schedules exist");
					listener.onSuccess(null, "");
					return;
				}

				// Server assumes that there could be multiple schedules and server sends the array 
				// Since server sends the array of schedule but we assume that there is going to be only
				// single schedule we are just using the first available item. I think server needs to change
				// to return only one schedule. 
				final String scheduleId = result.mResult.get(0).mId;

				GetNeatoRobotScheduleDataResult resultData = NeatoRobotScheduleWebservicesHelper.getNeatoRobotScheduleDataRequest(mContext, scheduleId);
				if (resultData == null) {
					LogHelper.log(TAG, "Error in fetching Robot Schedule data request");
					listener.onError("");
					return;
				}
				
				if (!resultData.success()) {
					LogHelper.log(TAG, "Error in fetching Robot Schedule data request: Error: "+ result.mMessage);
					listener.onError(result.mMessage);
					return;
				}
				
				LogHelper.logD(TAG, "Sucessfully got scheduling data with xml url:" + resultData.mResult.mXml_Data_Url);

				String schduleFileUrl = resultData.mResult.mXml_Data_Url;
				String filePath = FileCachePath.getScheduleDataFilePath(mContext, robotId, scheduleId);	
				
				FileDownloadHelper.downloadFile(mContext, schduleFileUrl, filePath, new FileDownloadListener() {
					
					@Override
					public void onDownloadError(String url) {
						listener.onError("Could not download schedule data");
					}
					
					@Override
					public void onDownloadComplete(String url, String filePath) {
						AdvancedScheduleGroup schedule = ScheduleXmlHelper.readFileXml(filePath);
						// If there is no schdule on the robot, then we will get schedule as null
						listener.onSuccess(schedule, scheduleId);
					}
				});
			}

		};
		TaskUtils.scheduleTask(task, 0);
	}	
}
