package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import java.util.ArrayList;
import android.content.Context;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.database.ScheduleHelper;
import com.neatorobotics.android.slide.framework.http.download.FileCachePath;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadHelper;
import com.neatorobotics.android.slide.framework.http.download.FileDownloadListener;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.robot.schedule2.Schedule2;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleGroup2;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleXmlHelper2;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;

public class RobotSchedulerManager2 {

	private static final String TAG = RobotSchedulerManager2.class.getSimpleName();	
	private Context mContext;
	private static RobotSchedulerManager2 sRobotSchedulerManager;
	private static final Object INSTANCE_LOCK = new Object();
	private static final String DEFAULT_TEMP_SCHEDULE_ID = "default_temp_id";
	private static final String DEFAULT_TEMP_SCHEDULE_VERSION = "default_temp_version";
	private static final String EMPTY_STRING = "";
	
	private RobotSchedulerManager2(Context context)
	{
		mContext = context.getApplicationContext();
	}

	public static RobotSchedulerManager2 getInstance(Context context)
	{
		synchronized (INSTANCE_LOCK) {
			if (sRobotSchedulerManager == null) {
				sRobotSchedulerManager = new RobotSchedulerManager2(context);
			}
		}
		return sRobotSchedulerManager;
	}
	
	/*
	 * This API should be used to get the scheduleId and the array of schedule events.
	 */
	public void getScheduleEvents (final String robotId, final int scheduleType, final GetScheduleEventsListener listener) {		
		getRobotScheduleFromRobot(robotId, scheduleType, new GetScheduleListener2() {

			@Override
			public void onSuccess(ScheduleGroup2 group, String scheduleId,
					int scheduleType, String scheduleVersion) {
				if (group != null) {
					ArrayList<String> events = group.getEventIds();
					String id = ScheduleHelper.getScheduleIdFromDB(mContext, robotId, scheduleType);
					listener.onSuccess(id, events);
				} else {
					listener.onServerError(ErrorTypes.NO_SCHEDULE_FOR_ROBOT, "No schedule exists on server for the given robotid");
				}
			}

			@Override
			public void onServerError(int errorCode, String errMessage) {
				listener.onServerError(errorCode, errMessage);
			}

			@Override
			public void onNetworkError(String errMessage) {
				listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);

			}
		});
	}
	
	/*
	 * This API will create an empty schedule of the given type in our local database. This should not be called if a 
	 * schedule exists for the robot for the given schedule type.
	 */
	public void createSchedule(final String robotId, final int scheduleType, final GetScheduleListener2 listener) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				final ScheduleGroup2 schedule = SchedulerConstants2.getEmptySchedule(scheduleType);
				String data = schedule.getXml();
				String scheduleTypeStr = SchedulerConstants2.getScheduleType(scheduleType);
				String id = schedule.getId();
				String scheduleVersion = DEFAULT_TEMP_SCHEDULE_VERSION;
				String serverId = DEFAULT_TEMP_SCHEDULE_ID;
				ScheduleHelper.saveScheduleId(mContext, robotId, id, scheduleType);
				ScheduleHelper.saveScheduleInfo(mContext, id, serverId, scheduleVersion, scheduleTypeStr, data);
				listener.onSuccess(schedule, id, scheduleType, scheduleVersion);
			}
		};	
		TaskUtils.scheduleTask(task, 0);
	}

	/*
	 * This API will return the Schedule Event data for the given pair of type eventId and scheduleId.
	 */
	public void getScheduleEventData(String id, final String eventId, final GetScheduleEventData listener) {
		ScheduleGroup2 scheduleGroup = ScheduleHelper.getScheduleFromId(mContext, id);
		if (scheduleGroup == null) {
			listener.onServerError(ErrorTypes.INVALID_SCHEDULE_ID, "No schedule id found in DB.");
		}
		else {
			Schedule2 event = scheduleGroup.getEvent(eventId);
			if (event == null) {
				listener.onServerError(ErrorTypes.INVALID_EVENT_ID, "No Event found for given event id");
				return;
			}
			listener.onSuccess(id, eventId, event);
		}
	}

	/*
	 * This API will add a event in the existing schedule object.
	 */
	public void addScheduleEvent(final Schedule2 event, String id, final ScheduleEventListener listener) {
		ScheduleGroup2 scheduleGroup = ScheduleHelper.getScheduleFromId(mContext, id);
		if (scheduleGroup == null) {
			listener.onServerError(ErrorTypes.INVALID_SCHEDULE_ID, "No schedule Id found in DB.");
		}
		else {
			ScheduleHelper.addScheduleEvent(mContext, id, event);
			listener.onSuccess();
		}
	}

	/*
	 * This API will delete the event from the existing schedule object
	 */
	public void deleteScheduleEvent(final String id, final String eventId, final ScheduleEventListener listener) {
		ScheduleGroup2 scheduleGroup = ScheduleHelper.getScheduleFromId(mContext, id);
		if (scheduleGroup == null) {
			listener.onServerError(ErrorTypes.INVALID_SCHEDULE_ID, "No schedule Id found in DB.");
		} else {
			ScheduleHelper.deleteScheduleEvent(mContext, id, eventId);
			listener.onSuccess();
		}
	}

	/*
	 * This will update the event from the existing schedule object.
	 */
	public void updateScheduleEvent(final Schedule2 event, String id, final ScheduleEventListener listener) {
		ScheduleGroup2 scheduleGroup = ScheduleHelper.getScheduleFromId(mContext, id);
		if (scheduleGroup == null) {
			listener.onServerError(ErrorTypes.INVALID_SCHEDULE_ID, "No schedule id found in DB.");
		} else {
			ScheduleHelper.updateScheduleEvent(mContext, id, event);
			listener.onSuccess();
		}
	}

	/*
	 * This will update the local copy of the schedule to the server.
	 */
	public void updateSchedule(String id, final ScheduleWebserviceListener listener) {
		ScheduleGroup2 schedule = ScheduleHelper.getScheduleFromId(mContext, id);
		String scheduleType = ScheduleHelper.getScheduleType(mContext, id);
		String robotId = ScheduleHelper.getRobotIdForSchedule(mContext, id);
		addUpdateSchedule(schedule, robotId, id, scheduleType, listener);
	}
	
	public void getSchedule(String id, final GetScheduleListener2 listener) {
		ScheduleGroup2 schedule = ScheduleHelper.getScheduleFromId(mContext, id);
		String scheduleType = ScheduleHelper.getScheduleType(mContext, id);
		String version = ScheduleHelper.getScheduleVersion(mContext, id);
		if (schedule != null) {
			LogHelper.logD(TAG, "Schedule already exists in the DB");
			listener.onSuccess(schedule, id, SchedulerConstants2.getScheduleIntType(scheduleType), version);
		} else {
			LogHelper.logD(TAG, "No Schedule found for the given Id");
		}
	}
	
	/*
	 * This will download the schedule from the server.
	 */
	private void getRobotScheduleFromRobot(final String robotId, final int scheduleTypeInt, final GetScheduleListener2 listener) {

		Runnable task = new Runnable() {
			@Override
			public void run() {
				
				final String scheduleType = SchedulerConstants2.getScheduleType(scheduleTypeInt);
				if (scheduleType == null) {
					LogHelper.log(TAG, "Invalid schedule type: " + scheduleTypeInt);
					listener.onServerError(ErrorTypes.INVALID_SCHEDULE_TYPE, "Invalid Schedule Type");
					return;
				}
				ScheduleDetails scheduleInfo = getCurrentScheduleIdAndVersion(robotId, scheduleTypeInt);
				if (scheduleInfo == null) {
					LogHelper.log(TAG, "No schedule exists for the given robotId and scheduleType.");
					listener.onServerError(ErrorTypes.NO_SCHEDULE_FOR_ROBOT, "No schedule exists for the given robot");
					return;
				}
				final String scheduleId = scheduleInfo.getServerScheduleId();
				final String scheduleVersion = scheduleInfo.getScheduleVersion();
				GetNeatoRobotScheduleDataResult resultData = NeatoRobotScheduleWebservicesHelper.getNeatoRobotScheduleDataRequest(mContext, scheduleId);
				if (resultData.success()) {
					LogHelper.logD(TAG, "Sucessfully got scheduling data with xml url:" + resultData.mResult.mXml_Data_Url);

					String schduleFileUrl = resultData.mResult.mXml_Data_Url;
					String filePath = FileCachePath.getScheduleDataFilePath(mContext, robotId, scheduleId);	
					FileDownloadHelper.downloadFile(mContext, schduleFileUrl, filePath, new FileDownloadListener() {

						@Override
						public void onDownloadError(String url) {
							listener.onServerError(ErrorTypes.FILE_DOWNLOAD_ERROR, "Could not download schedule data");
						}

						@Override
						public void onDownloadComplete(String url, String filePath) {
							ScheduleGroup2 schedule = ScheduleXmlHelper2.readFileXml(filePath, scheduleType);
							if (schedule != null) {
								String id = schedule.getId();
								if (TextUtils.isEmpty(id)) {
									LogHelper.log(TAG, "Old Schedule Data exists. Please update the with schedule");
									listener.onServerError(ErrorTypes.FILE_PARSE_ERROR, "Invalid Schedule");
									return;
								}
								// If there is no schedule on the robot, then we will get schedule as null
								ScheduleHelper.saveScheduleId(mContext, robotId, id, scheduleTypeInt);
								ScheduleHelper.saveScheduleInfo(mContext, id, scheduleId, scheduleVersion, scheduleType, schedule.getXml());
								listener.onSuccess(schedule, id, scheduleTypeInt, scheduleVersion);
							}
							else {
								listener.onServerError(ErrorTypes.FILE_PARSE_ERROR, "Invalid Schedule");
							}
						}
					});
				} 
				else if (resultData.isNetworkError()) {
					LogHelper.log(TAG, "Error in fetching Robot Schedule data request: Error: ");
					listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);
					return;
				}
				else {
					LogHelper.log(TAG, "Error in fetching Robot Schedule data request: Error: "+ resultData.mMessage);
					listener.onServerError(ErrorTypes.ERROR_SERVER_ERROR, resultData.mMessage);
					return;
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	private ScheduleDetails getCurrentScheduleIdAndVersion(String robotId, int scheduleInt) {
		ScheduleDetails scheduleDetails = null;
		String scheduleType = SchedulerConstants2.getScheduleType(scheduleInt);
		if (scheduleType == null) {
			LogHelper.log(TAG, "Invalid schedule type: " + scheduleInt);
			return null;
		}
		GetNeatoRobotSchedulesResult schedulesResult = NeatoRobotScheduleWebservicesHelper.getNeatoRobotSchedulesRequest(mContext, robotId);
		if (schedulesResult.success()) {
			int scheduleListSize = schedulesResult.mResult.size();
			// NOTE: Though server supports multiple schedules but ideally we should not have
			// multiple schedules on server. We programatically takes care of not creating
			// multiple schedules on server but in case there are multiple schedules, we update
			// the latest schedule on server
			for (int scheduleIterator = (scheduleListSize-1); scheduleIterator >= 0 ; scheduleIterator--) {
				if (scheduleType.equals(schedulesResult.mResult.get(scheduleIterator).mSchedule_Type)) {
					String scheduleId = schedulesResult.mResult.get(scheduleIterator).mId;
					String scheduleVersion = schedulesResult.mResult.get(scheduleIterator).mXml_Data_Version;
					scheduleDetails = new ScheduleDetails(scheduleId, scheduleVersion);
					LogHelper.log(TAG, "Current schedule id is: "+scheduleId);
					LogHelper.log(TAG, "Current schedule version is: "+scheduleVersion);
					break;
				}
			}
		} 
		else if (schedulesResult.isNetworkError()) {
			LogHelper.log(TAG, "sendRobotSchedule error: Network Error");
		}
		else {
			LogHelper.log(TAG, "sendRobotSchedule error: Server Error: "+schedulesResult.mMessage);
		}
		return scheduleDetails;
	}
	

	// To be called from secondary thread. This is a temp function. Later when
	// webservice starts sending version along with schedule data
	// This function can be removed.
	private String getCurrentScheduleVerion(String localId) {
		String robotId = ScheduleHelper.getRobotIdForSchedule(mContext, localId);
		String serverId = ScheduleHelper.getScheduleServerId(mContext, localId);
		String currentVersion = null;
		GetNeatoRobotSchedulesResult schedulesResult = NeatoRobotScheduleWebservicesHelper.getNeatoRobotSchedulesRequest(mContext, robotId);
		if (schedulesResult.success()) {
			int scheduleListSize = schedulesResult.mResult.size();
			for (int scheduleIterator = (scheduleListSize-1); scheduleIterator >= 0 ; scheduleIterator--) {
				if (serverId.equals(schedulesResult.mResult.get(scheduleIterator).mId)) {
					currentVersion = schedulesResult.mResult.get(scheduleIterator).mXml_Data_Version;
					LogHelper.log(TAG, "Current schedule version is: " +currentVersion);
					break;
				}
			}
		} 
		else if (schedulesResult.isNetworkError()) {
			LogHelper.log(TAG, "sendRobotSchedule error: Network Error");
		}
		else {
			LogHelper.log(TAG, "sendRobotSchedule error: Server Error: "+schedulesResult.mMessage);
		}
		return currentVersion;
	}
	
	//Note: This function is called to replace the existing schedule with a new schedule.
	public void addUpdateSchedule(final ScheduleGroup2 robotScheduleGroup, final String robotId, final String localId, final String scheduleType, final ScheduleWebserviceListener listener) {
		final String xmlSchedule = robotScheduleGroup.getXml();
		final String blobData = robotScheduleGroup.getBlobData();
		Runnable task = new Runnable() {
			public void run() 
			{
				String serverId = ScheduleHelper.getScheduleServerId(mContext, localId);
				String scheduleVersion = ScheduleHelper.getScheduleVersion(mContext, localId);
				if (serverId.equals(DEFAULT_TEMP_SCHEDULE_ID)) {
					ScheduleDetails details = getCurrentScheduleIdAndVersion(robotId, SchedulerConstants2.getScheduleIntType(scheduleType));
					if (details != null) {
						serverId = details.getServerScheduleId();
						scheduleVersion = details.getScheduleVersion();
					} else {
						serverId = EMPTY_STRING;
						scheduleVersion = EMPTY_STRING;
					}
				}
				if (!TextUtils.isEmpty(serverId)) { 
					UpdateNeatoRobotScheduleResult result = NeatoRobotScheduleWebservicesHelper.updateNeatoRobotScheduleDataRequest(mContext, serverId, scheduleType, xmlSchedule, scheduleVersion);

					if (result.success()) {
						LogHelper.log(TAG, "Sucessfully updated scheduling data with robot schedule id: " + serverId);
						// TODO: Server should send a new version of the schedule saved after updating.
						if (listener != null) {
							String newVersion = getCurrentScheduleVerion(localId);
							ScheduleHelper.updateScheduleVersion(mContext, localId, newVersion);
							listener.onSuccess();
						}
					} 
					else if (result.isNetworkError()) {
						LogHelper.log(TAG, "Error in updating schedule");
						if (listener != null) {
							listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);
						}
					}
					else {
						LogHelper.log(TAG, "Error in updating schedule");
						if (listener != null) {
							listener.onServerError(result.mMessage);
						}
					}
				} 
				else {
					AddNeatoRobotScheduleDataResult result = NeatoRobotScheduleWebservicesHelper.addNeatoRobotScheduleDataRequest(mContext, robotId, scheduleType, xmlSchedule, blobData);
					if (result.success()) {
						LogHelper.log(TAG, "Sucessfully posted scheduling data with robot schedule id:" + result.mResult.mRobot_Schedule_Id);
						if (listener != null) {
							ScheduleHelper.setServerScheduleIdAndVersion(mContext, localId, result.mResult.mRobot_Schedule_Id, result.mResult.mXml_Data_Version);
							listener.onSuccess();
						}
					} 
					else if (result.isNetworkError()) {
						LogHelper.log(TAG, "sendRobotSchedule error: Network Error");
						if (listener != null) {
							listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);						
						}
					}
					else {
						LogHelper.log(TAG, "sendRobotSchedule error: Server Error: "+result.mMessage);
						if (listener != null) {
							listener.onServerError(result.mMessage);
						}					
					}
				
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void syncSchedulesFromServer(final String robotId, final GetScheduleListener2 listener) {
		Runnable task = new Runnable() {

			@Override
			public void run() {
				getRobotScheduleFromRobot(robotId, SchedulerConstants2.SCHEDULE_TYPE_ADVANCED, listener);
				getRobotScheduleFromRobot(robotId, SchedulerConstants2.SCHEDULE_TYPE_BASIC, listener);
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	private static class ScheduleDetails {
		private String mServerScheduleId = null;
		private String mScheduleVersion = null;
		ScheduleDetails (String serverScheduleId, String scheduleVersion) {
			mServerScheduleId = serverScheduleId;
			mScheduleVersion = scheduleVersion;
		}

		public String getServerScheduleId() {
			return mServerScheduleId;
		}
		public String getScheduleVersion() {
			return mScheduleVersion;
		}
	}	
}
