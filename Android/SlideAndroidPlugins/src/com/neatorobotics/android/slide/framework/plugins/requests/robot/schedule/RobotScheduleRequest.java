package com.neatorobotics.android.slide.framework.plugins.requests.robot.schedule;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.database.ScheduleHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.pluginhelper.ScheduleJsonDataHelper2;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleEvent;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2;
import com.neatorobotics.android.slide.framework.robotdata.RobotDataManager;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.RobotSchedulerManager2;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.ScheduleRequestListener;

public class RobotScheduleRequest extends RobotManagerRequest {
	
	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotManagerRequest request = getRequest(action);
		if (request != null) {
			request.execute(action, data, callbackId);
		}
	}
	
	private class CreateScheduleRequest extends RobotManagerRequest {
		
		private void createSchedule(Context context, RobotJsonData jsonData, final String callbackId) {			
			int scheduleType = jsonData.getInt(JsonMapKeys.KEY_SCHEDULE_TYPE);
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);			
			RobotSchedulerManager2.getInstance(context).createSchedule(robotId, scheduleType, new ScheduleRequestListenerWrapper(callbackId));
		}

		@Override
		public void execute(String action, JSONArray data, String callbackId) {
			RobotJsonData jsonData = new RobotJsonData(data);
			createSchedule(mContext, jsonData, callbackId);
		}		
	}
	
	private class GetScheduleEventDataRequest extends RobotManagerRequest {
		
		private void getScheduleEventData(Context context, RobotJsonData jsonData, final String callbackId) {
			String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			final String scheduleEventId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_EVENT_ID);
			RobotSchedulerManager2.getInstance(context).getScheduleEventData(scheduleId, scheduleEventId, new ScheduleRequestListenerWrapper(callbackId));
		}

		@Override
		public void execute(String action, JSONArray data, String callbackId) {
			RobotJsonData jsonData = new RobotJsonData(data);
			getScheduleEventData(mContext, jsonData, callbackId);
		}
	}
	
	private class UpdateScheduleEventRequest extends RobotManagerRequest {
		private void updateScheduleEvent(Context context, RobotJsonData jsonData,
				final String callbackId) {
			final String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			final String scheduleEventId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_EVENT_ID);
			JSONObject event = jsonData.getJsonObject(JsonMapKeys.KEY_SCHEDULE_EVENT_DATA);
			int scheduleType = getScheduleTypeFromId(context, scheduleId);
			ScheduleEvent scheduleEvent = ScheduleJsonDataHelper2.jsonToSchedule(event, scheduleEventId, scheduleType);
			RobotSchedulerManager2.getInstance(context).updateScheduleEvent(scheduleEvent, scheduleId, new ScheduleRequestListenerWrapper(callbackId));
		}

		@Override
		public void execute(String action, JSONArray data, String callbackId) {
			RobotJsonData jsonData = new RobotJsonData(data);
			updateScheduleEvent(mContext, jsonData, callbackId);
		}
	}

	private class DeleteScheduleEventRequest extends RobotManagerRequest {
		private void deleteScheduleEvent(Context context, RobotJsonData jsonData, final String callbackId) {
			final String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			final String scheduleEventId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_EVENT_ID);
			RobotSchedulerManager2.getInstance(context).deleteScheduleEvent(scheduleId, scheduleEventId, new ScheduleRequestListenerWrapper(callbackId));
		}

		@Override
		public void execute(String action, JSONArray data, String callbackId) {
			RobotJsonData jsonData = new RobotJsonData(data);
			deleteScheduleEvent(mContext, jsonData, callbackId);
		}
	}

	private class AddScheduleEventRequest extends RobotManagerRequest {
		private void addScheduleEvent(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.log(TAG, "JSON Received: "+ jsonData.toString());
			
			final String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			JSONObject event = jsonData.getJsonObject(JsonMapKeys.KEY_SCHEDULE_EVENT_DATA);
			final String eventId = AppUtils.generateNewScheduleEventId();
			int scheduleType = getScheduleTypeFromId(context, scheduleId);
			ScheduleEvent scheduleEvent = ScheduleJsonDataHelper2.jsonToSchedule(event, eventId, scheduleType);
			RobotSchedulerManager2.getInstance(context).addScheduleEvent(scheduleEvent, scheduleId, new ScheduleRequestListenerWrapper(callbackId));
		}

		@Override
		public void execute(String action, JSONArray data, String callbackId) {
			RobotJsonData jsonData = new RobotJsonData(data);
			addScheduleEvent(mContext, jsonData, callbackId);
		}
	}

	private class GetScheduleEventsRequest extends RobotManagerRequest {
		private void getScheduleEvents(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.log(TAG, "getScheduleEvents");
			final int scheduleType = jsonData.getInt(JsonMapKeys.KEY_SCHEDULE_TYPE);
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			RobotSchedulerManager2.getInstance(context).getScheduleByType(robotId, scheduleType, new ScheduleRequestListenerWrapper(callbackId));				
		}

		@Override
		public void execute(String action, JSONArray data, String callbackId) {
			RobotJsonData jsonData = new RobotJsonData(data);
			getScheduleEvents(mContext, jsonData, callbackId);
		}
	}
	
	// Newly added methods for new schedule pattern.
	private class GetScheduleDataRequest extends RobotManagerRequest {
		private void getScheduleData(Context context, RobotJsonData jsonData, final String callbackId) {
			String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			RobotSchedulerManager2.getInstance(context).getSchedule(scheduleId, new ScheduleRequestListenerWrapper(callbackId));			
		}

		@Override
		public void execute(String action, JSONArray data, String callbackId) {
			RobotJsonData jsonData = new RobotJsonData(data);
			getScheduleData(mContext, jsonData, callbackId);
		}
	}
	
	private class UpdateScheduleRequest extends RobotManagerRequest {
		
		private void updateSchedule(final Context context, RobotJsonData jsonData,
				final String callbackId) {
			final String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			RobotSchedulerManager2.getInstance(context).addUpdateSchedule(scheduleId, new ScheduleRequestListenerWrapper(callbackId) {
				@Override
				public void onScheduleData(JSONObject scheduleJson) {
					super.onScheduleData(scheduleJson);
					final String robotId = ScheduleHelper.getRobotIdForSchedule(context, scheduleId);
					if (AppConstants.isServerDataModeEnabled()) {
						// Passing NoActionWebServiceRequestListener in listener because we don't want to 
						// send the response to the UI layer
						// TODO:
						// However this has one issue. If Schedule is updated but the API to tell the robot that schedule is updated
						// fails then robot will not know that scheduled has changed. Either server should send the message
						// to the robot whenever schedule is updated or if this API fails, SmartApp needs to save this information
						// and try again
						RobotDataManager.sendRobotScheduleUpdated(context, robotId, new NoActionWebServiceRequestListener());
					}
				}
			});
		}

		@Override
		public void execute(String action, JSONArray data, String callbackId) {
			RobotJsonData jsonData = new RobotJsonData(data);
			updateSchedule(mContext, jsonData, callbackId);
		}
	}
	
	private int getScheduleTypeFromId(Context context, String id) {
		String scheduleType = ScheduleHelper.getScheduleType(context, id);
		return SchedulerConstants2.getScheduleIntType(scheduleType);
	}
	
	
	protected class ScheduleRequestListenerWrapper implements ScheduleRequestListener {
		private String mCallbackId;
		
		public ScheduleRequestListenerWrapper(String callbackId) {
			mCallbackId = callbackId;
		}
		
		@Override
		public void onReceived(NeatoWebserviceResult responseResult) {
			LogHelper.logD(TAG, "Request processed successfully");
			try {
				JSONObject resultObj = getResultObject(responseResult);
				if (resultObj != null) {						
					PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, resultObj);
					pluginResult.setKeepCallback(false);
					sendSuccessPluginResult(pluginResult, mCallbackId);
				}
				else {
					LogHelper.logD(TAG, "Unknown Error");
					sendError(mCallbackId, ErrorTypes.ERROR_TYPE_UNKNOWN, "Unknown Error");
				}
			}
			catch (JSONException ex) {
				LogHelper.logD(TAG, "JSON Error");
				sendError(mCallbackId, ErrorTypes.JSON_PARSING_ERROR, ex.getMessage());
			}
		}	
		
		public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
			return new JSONObject(); 
		}

		@Override
		public void onServerError(int errorType, String errMessage) {
			LogHelper.logD(TAG, "Server Error: " + errMessage);
			sendError(mCallbackId, errorType, errMessage);
		}
		
		@Override
		public void onNetworkError(String errorMessage) {
			LogHelper.logD(TAG, "Network Error: " + errorMessage);
			sendError(mCallbackId, ErrorTypes.ERROR_NETWORK_ERROR, errorMessage);
		}
		
		@Override
		public void onScheduleData(JSONObject scheduleJson) {			
			LogHelper.logD(TAG, "Received schedule data");
			PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, scheduleJson);
			pluginResult.setKeepCallback(false);
			sendSuccessPluginResult(pluginResult, mCallbackId);
		}
	}
	
	private class ScheduleActionTypes {
		public static final String UPDATE_SCHEDULE = "updateSchedule";
		public static final String DELETE_ROBOT_SCHEDULE_EVENT = "deleteScheduleEvent";
		public static final String UPDATE_ROBOT_SCHEDULE_EVENT = "updateScheduleEvent";
		public static final String GET_SCHEDULE_EVENT_DATA = "getScheduleEventData";
		public static final String ADD_ROBOT_SCHEDULE_EVENT = "addScheduleEventData";
		public static final String GET_SCHEDULE_EVENTS = "getScheduleEvents";
		public static final String GET_SCHEDULE_DATA = "getScheduleData";
		public static final String CREATE_SCHEDULE = "createSchedule";
	}
	
	private UpdateScheduleRequest mUpdateScheduleRequest = new UpdateScheduleRequest();
	private DeleteScheduleEventRequest mDeleteScheduleEventRequest = new DeleteScheduleEventRequest();
	private UpdateScheduleEventRequest mUpdateScheduleEventRequest = new UpdateScheduleEventRequest();
	private GetScheduleEventDataRequest mGetScheduleEventDataRequest = new GetScheduleEventDataRequest();
	private AddScheduleEventRequest mAddScheduleEventRequest = new AddScheduleEventRequest();
	private GetScheduleEventsRequest mGetScheduleEventsRequest = new GetScheduleEventsRequest();
	private GetScheduleDataRequest mGetScheduleDataRequest = new GetScheduleDataRequest();
	private CreateScheduleRequest mCreateScheduleRequest = new CreateScheduleRequest();
	
	private RobotManagerRequest getRequest(String action) {
		if (action.equals(ScheduleActionTypes.UPDATE_SCHEDULE)) {
			return mUpdateScheduleRequest;
		} else if (action.equals(ScheduleActionTypes.DELETE_ROBOT_SCHEDULE_EVENT)) {
			return mDeleteScheduleEventRequest;
		} else if (action.equals(ScheduleActionTypes.UPDATE_ROBOT_SCHEDULE_EVENT)) {
			return mUpdateScheduleEventRequest;
		} else if (action.equals(ScheduleActionTypes.GET_SCHEDULE_EVENT_DATA)) {
			return mGetScheduleEventDataRequest;
		} else if (action.equals(ScheduleActionTypes.ADD_ROBOT_SCHEDULE_EVENT)) {
			return mAddScheduleEventRequest;
		} else if (action.equals(ScheduleActionTypes.GET_SCHEDULE_EVENTS)) {
			return mGetScheduleEventsRequest;
		} else if (action.equals(ScheduleActionTypes.GET_SCHEDULE_DATA)) {
			return mGetScheduleDataRequest;
		} else if (action.equals(ScheduleActionTypes.CREATE_SCHEDULE)) {
			return mCreateScheduleRequest;
		}
		return null;
	}
	
	@Override
	public void initalize(Context context, Plugin plugin) {
		super.initalize(context, plugin);
		mUpdateScheduleRequest.initalize(context, plugin);
		mDeleteScheduleEventRequest.initalize(context, plugin);
		mUpdateScheduleEventRequest.initalize(context, plugin);
		mGetScheduleEventDataRequest.initalize(context, plugin);
		mAddScheduleEventRequest.initalize(context, plugin);
		mGetScheduleEventsRequest.initalize(context, plugin);
		mGetScheduleDataRequest.initalize(context, plugin);
		mCreateScheduleRequest.initalize(context, plugin);
	}
}
