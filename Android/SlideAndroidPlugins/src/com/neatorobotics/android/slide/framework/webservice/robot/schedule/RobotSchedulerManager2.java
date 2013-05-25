package com.neatorobotics.android.slide.framework.webservice.robot.schedule;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.database.ScheduleHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleEvent;
import com.neatorobotics.android.slide.framework.robot.schedule2.Schedules;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleInfo2;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.SetRobotProfileDetailsResult3;

public class RobotSchedulerManager2 {

	private static final String TAG = RobotSchedulerManager2.class.getSimpleName();	
	private Context mContext;
	private static RobotSchedulerManager2 sRobotSchedulerManager;
	private static final Object INSTANCE_LOCK = new Object();
	private static final String DEFAULT_TEMP_SCHEDULE_ID = "default_temp_id";
	private static final String DEFAULT_TEMP_SCHEDULE_VERSION = "default_temp_version";	
	
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
	 * This API will create an empty schedule of the given type in our local database. This should not be called if a 
	 * schedule exists for the robot for the given schedule type.
	 * 
	 */
	public void createSchedule(final String robotId, final int scheduleType, final ScheduleRequestListener listener) {		
		Runnable task = new Runnable() {
			@Override
			public void run() {
				final Schedules schedule = SchedulerConstants2.getEmptySchedule(scheduleType);
				String scheduleTypeStr = SchedulerConstants2.getScheduleType(scheduleType);
						
				ScheduleHelper.saveScheduleId(mContext, robotId, schedule.getId(), scheduleType);
				ScheduleHelper.saveScheduleInfo(mContext, schedule.getId(), DEFAULT_TEMP_SCHEDULE_ID, DEFAULT_TEMP_SCHEDULE_VERSION, scheduleTypeStr, schedule.getJSON());
				
				JSONObject resultJson = new JSONObject();
				try {
					resultJson.put(JsonMapKeys.KEY_SCHEDULE_ID, schedule.getId());
					resultJson.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
					resultJson.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleType);
					listener.onScheduleData(resultJson);
				} catch (JSONException e) {
					LogHelper.logD(TAG, "JSONException in createSchedule result JSONObject");
					listener.onServerError(ErrorTypes.JSON_CREATION_ERROR, "Unable to create result JSON object");
				}
			}
		};	
		TaskUtils.scheduleTask(task, 0);
	}
	
	/*
	 * This API will return the Schedule Event data for the given pair of type eventId and scheduleId.
	 */
	public void getScheduleEventData(final String scheduleId, final String eventId, final ScheduleRequestListener listener) {		
		Runnable task = new Runnable() {
			@Override
			public void run() {
				Schedules scheduleGroup = ScheduleHelper.getScheduleGroupById(mContext, scheduleId);
				if (scheduleGroup == null) {
					listener.onServerError(ErrorTypes.INVALID_SCHEDULE_ID, "No schedule id found in DB.");
				}
				else {
					ScheduleEvent event = scheduleGroup.getEvent(eventId);
					if (event != null) {
						JSONObject jsonResult = new JSONObject();
						try {
							jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
							jsonResult.put(JsonMapKeys.KEY_SCHEDULE_EVENT_ID, eventId);
							jsonResult.put(JsonMapKeys.KEY_SCHEDULE_EVENT_DATA, event.toJsonObject());
							listener.onScheduleData(jsonResult);
							
						} catch (JSONException ex) {
							LogHelper.log(TAG, "JSONException in getScheduleEventData", ex);
							listener.onServerError(ErrorTypes.JSON_CREATION_ERROR, "Unable to create result JSON object");
						}													
					}
					else {
						listener.onServerError(ErrorTypes.INVALID_EVENT_ID, "No Event found for given event id");
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	/*
	 * This API will add a event in the existing schedule object.
	 */	
	public void addScheduleEvent(final ScheduleEvent event, final String scheduleId, final ScheduleRequestListener listener) {		
		Runnable task = new Runnable() {
			@Override
			public void run() {
				Schedules scheduleGroup = ScheduleHelper.getScheduleGroupById(mContext, scheduleId);
				if (scheduleGroup == null) {
					listener.onServerError(ErrorTypes.INVALID_SCHEDULE_ID, "No schedule Id found in DB.");
				}
				else {
					scheduleGroup.addSchedule(event);
					String scheduleData = scheduleGroup.getJSON();
					boolean success = ScheduleHelper.saveScheduleData(mContext, scheduleId, scheduleData);
					if (success) {						
						try {
							JSONObject jsonResult = new JSONObject();
							jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
							jsonResult.put(JsonMapKeys.KEY_SCHEDULE_EVENT_ID, event.getEventId());
							listener.onScheduleData(jsonResult);
						} 
						catch (JSONException ex) {
							LogHelper.log(TAG, "JSONException in addScheduleEvent", ex);
							listener.onServerError(ErrorTypes.JSON_CREATION_ERROR, "Unable to create result JSON object");
						}
					}
					else {
						LogHelper.log(TAG, "addScheduleEvent - Unable to insert newly created event in DB");
						listener.onServerError(ErrorTypes.ERROR_DB_ERROR, "Unable to add event info in DB");
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	/*
	 * This API will delete the event from the existing schedule object
	 */
	public void deleteScheduleEvent(final String scheduleId, final String eventId, final ScheduleRequestListener listener) {		
		Runnable task = new Runnable() {
			@Override
			public void run() {
				Schedules scheduleGroup = ScheduleHelper.getScheduleGroupById(mContext, scheduleId);
				if (scheduleGroup == null) {
					listener.onServerError(ErrorTypes.INVALID_SCHEDULE_ID, "No schedule Id found in DB.");
				} 
				else {					
					boolean success = scheduleGroup.removeScheduleEvent(eventId);
					if (success) {
						String scheduleData = scheduleGroup.getJSON();
						success = ScheduleHelper.saveScheduleData(mContext, scheduleId, scheduleData);
						if (success) {						
							try {
								JSONObject jsonResult = new JSONObject();
								jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
								jsonResult.put(JsonMapKeys.KEY_SCHEDULE_EVENT_ID, eventId);
								listener.onScheduleData(jsonResult);
							} 
							catch (JSONException ex) {
								success = false;
								LogHelper.log(TAG, "JSONException in deleteScheduleEvent", ex);								
								listener.onServerError(ErrorTypes.JSON_CREATION_ERROR, "Unable to create result JSON object");
							}
						}
					}
					
					if (!success) {
						LogHelper.log(TAG, "deleteScheduleEvent - Unable to delete event");
						listener.onServerError(ErrorTypes.ERROR_DB_ERROR, "Unable to delete event info");
					}	
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	/*
	 * This will update the event from the existing schedule object.
	 */	
	public void updateScheduleEvent(final ScheduleEvent event, final String scheduleId, final ScheduleRequestListener listener) {		
		Runnable task = new Runnable() {
			@Override
			public void run() {
				Schedules scheduleGroup = ScheduleHelper.getScheduleGroupById(mContext, scheduleId);
				if (scheduleGroup == null) {
					listener.onServerError(ErrorTypes.INVALID_SCHEDULE_ID, "No schedule id found in DB.");
				} 
				else {
					boolean success  = scheduleGroup.updateScheduleEvent(event);
					if (success) {
						String scheduleData = scheduleGroup.getJSON();
						success = ScheduleHelper.saveScheduleData(mContext, scheduleId, scheduleData);
						try {
							JSONObject jsonResult = new JSONObject();
							jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
							jsonResult.put(JsonMapKeys.KEY_SCHEDULE_EVENT_ID, event.getEventId());
							listener.onScheduleData(jsonResult);
						} 
						catch (JSONException ex) {
							success = false;
							LogHelper.log(TAG, "JSONException in updateScheduleEvent", ex);								
							listener.onServerError(ErrorTypes.JSON_CREATION_ERROR, "Unable to create result JSON object");
						}
					}
					
					if (!success) {
						LogHelper.log(TAG, "updateScheduleEvent - Unable to update event");
						listener.onServerError(ErrorTypes.ERROR_DB_ERROR, "Unable to update event info");
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void getSchedule(final String scheduleId, final ScheduleRequestListener listener) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				Schedules scheduleGroup = ScheduleHelper.getScheduleGroupById(mContext, scheduleId);			
				if (scheduleGroup != null) {
					LogHelper.logD(TAG, "Schedule already exists in the DB");
					try {
						JSONObject resultObj = new JSONObject();
						resultObj.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleGroup.getScheduleType());
						resultObj.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleGroup.getId());
						
						// Process events of a Schedule
						JSONArray schedules = new JSONArray();
						int count = scheduleGroup.eventCount();	
						for (int index = 0; index < count; index++) {
							ScheduleEvent event = scheduleGroup.getEvent(index);								
							schedules.put(event.toJsonObject());
						}							
						resultObj.put(JsonMapKeys.KEY_SCHEDULES, schedules);
						
						listener.onScheduleData(resultObj);
					}
					catch (JSONException ex) {
						LogHelper.logD(TAG, "JSONException in getSchedule - ScheduleId = " + scheduleId);
						listener.onServerError(ErrorTypes.JSON_PARSING_ERROR, "Error in JSON parsing");
					}
					
				} else {
					LogHelper.logD(TAG, "No Schedule found for ScheduleId = " + scheduleId);					
					listener.onServerError(ErrorTypes.INVALID_SCHEDULE_ID, "No Schedule found for the given Id");
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
	
	/*
	 * This will update the local copy of the schedule to the server.
	 */
	public void addUpdateSchedule(final String scheduleId, final ScheduleRequestListener listener) {
		Runnable task = new Runnable() {
			public void run() 
			{
				try {
					String serverId = "";
					String scheduleVersion = "";
					String scheduleType =  "";
					String scheduleJson =  "";
					String blobData =  "";
					
					String robotId = ScheduleHelper.getRobotIdForSchedule(mContext, scheduleId);
					
					ScheduleInfo2 scheduleInfo = ScheduleHelper.getScheduleInfoById(mContext, scheduleId);
					if (scheduleInfo != null) {
						
						serverId = scheduleInfo.getServerId();
						scheduleVersion = scheduleInfo.getDataVersion();
						scheduleType = scheduleInfo.getScheduleType();
						
						Schedules scheduleGroup = ScheduleHelper.getScheduleGroupById(mContext, scheduleId);						
						scheduleJson = scheduleGroup.getJSON();				
					}
					
					if (serverId.equals(DEFAULT_TEMP_SCHEDULE_ID)) {
						ScheduleDetails details = getCurrentScheduleIdAndVersion(robotId, SchedulerConstants2.getScheduleIntType(scheduleType));
						if (details != null) {
							serverId = details.getServerScheduleId();
							scheduleVersion = details.getScheduleVersion();
						} else {
							serverId = "";
							scheduleVersion = "";
						}
					}
					
					boolean success = false;
					if (!TextUtils.isEmpty(serverId)) { 
						UpdateNeatoRobotScheduleResult result = NeatoRobotScheduleWebservicesHelper.updateNeatoRobotScheduleDataRequest(mContext, serverId, scheduleType, scheduleJson, scheduleVersion);
						success = result.success();
						LogHelper.log(TAG, "Sucessfully updated scheduling data with robot schedule id: " + serverId);
						ScheduleHelper.updateScheduleVersion(mContext, scheduleId, result.result.schedule_version);
					} 
					else {
						AddNeatoRobotScheduleDataResult result = NeatoRobotScheduleWebservicesHelper.addNeatoRobotScheduleDataRequest(mContext, robotId, scheduleType, scheduleJson, blobData);
						success = result.success();
					}
					if (success) {
						try {
							JSONObject jsonResult = new JSONObject();				
							jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
							listener.onScheduleData(jsonResult);
						}
						catch (JSONException ex) {
							LogHelper.logD(TAG, "JSONException in addUpdateSchedule");
							listener.onServerError(ErrorTypes.JSON_CREATION_ERROR, "Unable to create result JSON object");
						}
					}
				}
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				} 
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void syncSchedulesFromServer(final String robotId, final ScheduleRequestListener listener) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				// getRobotScheduleFromRobot(robotId, SchedulerConstants2.SCHEDULE_TYPE_ADVANCED, listener);
				// getRobotScheduleFromRobot(robotId, SchedulerConstants2.SCHEDULE_TYPE_BASIC, listener);
				getScheduleByType(robotId, SchedulerConstants2.SCHEDULE_TYPE_BASIC, listener);
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}	
	
	public void getScheduleByType(final String robotId, final int scheduleType, final ScheduleRequestListener listener) {
		Runnable task = new Runnable() {			
			@Override
			public void run() {
				try {
					final int serverScheduleType = SchedulerConstants2.convertToServerConstants(scheduleType);
					GetRobotScheduleByTypeResult result = NeatoRobotScheduleWebservicesHelper.getScheduleBasedOnType(mContext, robotId, String.valueOf(serverScheduleType));				
					
					// Update schedule data into the DB if available
					if (result.isRobotScheduleAvailable()) {
						GetRobotScheduleByTypeResult.Result scheduleResult = result.getScheduleData();
						ScheduleHelper.saveScheduleId(mContext, robotId, scheduleResult.getScheduleUID(), serverScheduleType);
						ScheduleHelper.saveScheduleInfo(mContext, 
														scheduleResult.getScheduleUID(), 
														scheduleResult.schedule_id, 
														scheduleResult.schedule_version, 
														SchedulerConstants2.getScheduleType(scheduleType),
														scheduleResult.schedule_data);														
							
						try {
							JSONObject resultObj = new JSONObject();
							resultObj.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleResult.getScheduleUID());
							resultObj.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
							resultObj.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleType);
							resultObj.put(JsonMapKeys.KEY_SCHEDULE_EVENTS_LIST, scheduleResult.toEventIDJsonArray());
							
							listener.onScheduleData(resultObj);
						}
						catch (JSONException ex) {
							LogHelper.logD(TAG, "JSONException in getScheduleByType");
							listener.onServerError(ErrorTypes.JSON_CREATION_ERROR, "Unable to create result JSON object");
						}
					}												
				}
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void setEnableSchedule(final String robotId, final int scheduleType, final boolean enableSchedule, final ScheduleRequestListener listener) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				try {
					if(scheduleType == SchedulerConstants2.SERVER_SCHEDULE_TYPE_BASIC) {
						SetRobotProfileDetailsResult3 result = NeatoRobotScheduleWebservicesHelper.setEnableSchedule(mContext, robotId, scheduleType, enableSchedule);
						if(result.success()) {
							listener.onReceived(result);
						} else {
							listener.onServerError(ErrorTypes.ERROR_TYPE_UNKNOWN, "Result is not of type set profile details result");
						}						
					} else if(scheduleType == SchedulerConstants2.SERVER_SCHEDULE_TYPE_ADVANCED){
						listener.onServerError(ErrorTypes.INVALID_SCHEDULE_TYPE, "Advanced Schedule Type not supported yet");
					}					
				} catch (UserUnauthorizedException e) {
					listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, e.getErrorMessage());
				} catch (NeatoServerException e) {
					listener.onServerError(e.getErrorMessage());
				} catch (IOException e) {
					listener.onNetworkError(e.getMessage());
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void isScheduleEnabled(final String robotId, final int scheduleType, final ScheduleRequestListener listener) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				try {
					IsScheduleEnabledResult result = NeatoRobotScheduleWebservicesHelper.isScheduleEnabled(mContext, robotId, scheduleType);
					listener.onReceived(result);
				} catch (UserUnauthorizedException e) {
					listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, e.getErrorMessage());
				} catch (NeatoServerException e) {
					listener.onServerError(e.getErrorMessage());
				} catch (IOException e) {
					listener.onNetworkError(e.getMessage());
				}
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
