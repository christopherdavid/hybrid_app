package com.neatorobotics.android.slide.framework.plugins;

import java.util.HashMap;
import java.util.Map;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.database.ScheduleHelper;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationConstants;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationListener;
import com.neatorobotics.android.slide.framework.gcm.PushNotificationMessageHandler;
import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationUtil;
import com.neatorobotics.android.slide.framework.pluginhelper.ScheduleJsonDataHelper2;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDataListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotNotificationsListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotStateListener;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleEvent;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2;
import com.neatorobotics.android.slide.framework.robot.settings.CleaningSettings;
import com.neatorobotics.android.slide.framework.robot.settings.CleaningSettingsListener;
import com.neatorobotics.android.slide.framework.robot.settings.SettingsManager;
import com.neatorobotics.android.slide.framework.robotdata.RobotDataManager;
import com.neatorobotics.android.slide.framework.robotdata.RobotProfileConstants;
import com.neatorobotics.android.slide.framework.robotdata.RobotProfileDataUtils;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotDetailResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotOnlineStatusResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotVirtualOnlineStatusResult;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.SetRobotProfileDetailsResult3;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.IsScheduleEnabledResult;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.RobotSchedulerManager2;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.ScheduleRequestListener;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;


public class RobotManagerPlugin extends Plugin {

	private static final String TAG = RobotManagerPlugin.class.getSimpleName();

	private static final HashMap<String, RobotManagerPluginMethods> ACTION_MAP = new HashMap<String, RobotManagerPluginMethods>();

	// If we add more action type, please ensure to add it into the ACTION_MAP
	private enum RobotManagerPluginMethods {DISCOVER_NEAR_BY_ROBOTS, SET_ROBOT_SCHEDULE, 
		GET_ROBOT_SCHEDULE, DISCONNECT_DIRECT_CONNECTION, SET_ROBOT_NAME, DELETE_ROBOT_SCHEDULE, GET_ROBOT_DETAIL, 
		SET_ROBOT_NAME_2, TRY_DIRECT_CONNECTION2, SEND_COMMAND_TO_ROBOT2, ROBOT_ONLINE_STATUS, GET_ROBOT_VIRTUAL_ONLINE_STATUS,
		REGISTER_ROBOT_NOTIFICATIONS, UNREGISTER_ROBOT_NOTIFICATIONS,
		GET_SCHEDULE_DATA, GET_SCHEDULE_EVENTS, ADD_ROBOT_SCHEDULE_EVENT,
		GET_SCHEDULE_EVENT_DATA, UPDATE_ROBOT_SCHEDULE_EVENT, DELETE_ROBOT_SCHEDULE_EVENT, 
		UPDATE_SCHEDULE, CREATE_SCHEDULE, SYNC_SCHEDULE_FROM_SERVER, IS_SCHEDULE_ENABLED, 
		ENABLE_SCHEDULE, SET_SPOT_DEFINITION,
		GET_SPOT_DEFINITION, START_CLEANING, STOP_CLEANING, PAUSE_CLEANING, RESUME_CLEANING,
		DRIVE_ROBOT, TURN_VACUUM_ON_OFF, TURN_WIFI_ON_OFF, REGISTER_FOR_ROBOT_MESSAGES, UNREGISTER_FOR_ROBOT_MESSAGES, 
		REGISTER_ROBOT_NOTIFICATIONS2, UNREGISTER_ROBOT_NOTIFICATIONS2, GET_ROBOT_CLEANING_STATE};

		static {
			ACTION_MAP.put(ActionTypes.DISCOVER_NEAR_BY_ROBOTS, RobotManagerPluginMethods.DISCOVER_NEAR_BY_ROBOTS);
			ACTION_MAP.put(ActionTypes.TRY_DIRECT_CONNECTION2, RobotManagerPluginMethods.TRY_DIRECT_CONNECTION2);
			ACTION_MAP.put(ActionTypes.SEND_COMMAND_TO_ROBOT2, RobotManagerPluginMethods.SEND_COMMAND_TO_ROBOT2);
			ACTION_MAP.put(ActionTypes.SET_ROBOT_SCHEDULE, RobotManagerPluginMethods.SET_ROBOT_SCHEDULE);
			ACTION_MAP.put(ActionTypes.GET_ROBOT_SCHEDULE, RobotManagerPluginMethods.GET_ROBOT_SCHEDULE);
			ACTION_MAP.put(ActionTypes.DISCONNECT_DIRECT_CONNECTION, RobotManagerPluginMethods.DISCONNECT_DIRECT_CONNECTION);
			ACTION_MAP.put(ActionTypes.SET_ROBOT_NAME, RobotManagerPluginMethods.SET_ROBOT_NAME);
			ACTION_MAP.put(ActionTypes.DELETE_ROBOT_SCHEDULE, RobotManagerPluginMethods.DELETE_ROBOT_SCHEDULE);
			ACTION_MAP.put(ActionTypes.GET_ROBOT_DETAIL, RobotManagerPluginMethods.GET_ROBOT_DETAIL);
			ACTION_MAP.put(ActionTypes.SET_ROBOT_NAME_2, RobotManagerPluginMethods.SET_ROBOT_NAME_2);
			ACTION_MAP.put(ActionTypes.GET_ROBOT_ONLINE_STATUS, RobotManagerPluginMethods.ROBOT_ONLINE_STATUS);
			ACTION_MAP.put(ActionTypes.GET_ROBOT_VIRTUAL_ONLINE_STATUS, RobotManagerPluginMethods.GET_ROBOT_VIRTUAL_ONLINE_STATUS);
			ACTION_MAP.put(ActionTypes.REGISTER_ROBOT_NOTIFICATIONS, RobotManagerPluginMethods.REGISTER_ROBOT_NOTIFICATIONS);
			ACTION_MAP.put(ActionTypes.UNREGISTER_ROBOT_NOTIFICATIONS, RobotManagerPluginMethods.UNREGISTER_ROBOT_NOTIFICATIONS);
			ACTION_MAP.put(ActionTypes.GET_SCHEDULE_DATA, RobotManagerPluginMethods.GET_SCHEDULE_DATA);
			ACTION_MAP.put(ActionTypes.GET_SCHEDULE_EVENTS, RobotManagerPluginMethods.GET_SCHEDULE_EVENTS);
			ACTION_MAP.put(ActionTypes.ADD_ROBOT_SCHEDULE_EVENT, RobotManagerPluginMethods.ADD_ROBOT_SCHEDULE_EVENT);
			ACTION_MAP.put(ActionTypes.GET_SCHEDULE_EVENT_DATA, RobotManagerPluginMethods.GET_SCHEDULE_EVENT_DATA);
			ACTION_MAP.put(ActionTypes.UPDATE_ROBOT_SCHEDULE_EVENT, RobotManagerPluginMethods.UPDATE_ROBOT_SCHEDULE_EVENT);
			ACTION_MAP.put(ActionTypes.DELETE_ROBOT_SCHEDULE_EVENT, RobotManagerPluginMethods.DELETE_ROBOT_SCHEDULE_EVENT);
			ACTION_MAP.put(ActionTypes.UPDATE_SCHEDULE, RobotManagerPluginMethods.UPDATE_SCHEDULE);
			ACTION_MAP.put(ActionTypes.CREATE_SCHEDULE, RobotManagerPluginMethods.CREATE_SCHEDULE);
			ACTION_MAP.put(ActionTypes.SYNC_SCHEDULE_FROM_SERVER, RobotManagerPluginMethods.SYNC_SCHEDULE_FROM_SERVER);
			ACTION_MAP.put(ActionTypes.IS_SCHEDULE_ENABLED, RobotManagerPluginMethods.IS_SCHEDULE_ENABLED);
			ACTION_MAP.put(ActionTypes.ENABLE_SCHEDULE, RobotManagerPluginMethods.ENABLE_SCHEDULE);

			ACTION_MAP.put(ActionTypes.SET_SPOT_DEFINITION, RobotManagerPluginMethods.SET_SPOT_DEFINITION);
			ACTION_MAP.put(ActionTypes.GET_SPOT_DEFINITION, RobotManagerPluginMethods.GET_SPOT_DEFINITION);
			
			ACTION_MAP.put(ActionTypes.START_CLEANING, RobotManagerPluginMethods.START_CLEANING);
			ACTION_MAP.put(ActionTypes.STOP_CLEANING, RobotManagerPluginMethods.STOP_CLEANING);
			ACTION_MAP.put(ActionTypes.PAUSE_CLEANING, RobotManagerPluginMethods.PAUSE_CLEANING);
			ACTION_MAP.put(ActionTypes.RESUME_CLEANING, RobotManagerPluginMethods.RESUME_CLEANING);

			ACTION_MAP.put(ActionTypes.DRIVE_ROBOT, RobotManagerPluginMethods.DRIVE_ROBOT);
			
			ACTION_MAP.put(ActionTypes.TURN_VACUUM_ON_OFF, RobotManagerPluginMethods.TURN_VACUUM_ON_OFF);
			ACTION_MAP.put(ActionTypes.TURN_WIFI_ON_OFF, RobotManagerPluginMethods.TURN_WIFI_ON_OFF);			
			
			ACTION_MAP.put(ActionTypes.REGISTER_FOR_ROBOT_MESSAGES, RobotManagerPluginMethods.REGISTER_FOR_ROBOT_MESSAGES);
			ACTION_MAP.put(ActionTypes.UNREGISTER_FOR_ROBOT_MESSAGES, RobotManagerPluginMethods.UNREGISTER_FOR_ROBOT_MESSAGES);
			ACTION_MAP.put(ActionTypes.REGISTER_ROBOT_NOTIFICATIONS2, RobotManagerPluginMethods.REGISTER_ROBOT_NOTIFICATIONS2);
			ACTION_MAP.put(ActionTypes.UNREGISTER_ROBOT_NOTIFICATIONS2, RobotManagerPluginMethods.UNREGISTER_ROBOT_NOTIFICATIONS2);
			ACTION_MAP.put(ActionTypes.GET_ROBOT_CLEANING_STATE, RobotManagerPluginMethods.GET_ROBOT_CLEANING_STATE);
		}

		private RobotPluginDiscoveryListener mRobotPluginDiscoveryListener;
		private RobotNotificationsPluginListener mRobotNotificationsPluginListener;		
		private RobotStateNotificationPluginListener mRobotStateNotificationPluginListener;

		@Override
		public PluginResult execute(final String action, final JSONArray data, final String callbackId) {
			
			if (!isValidAction(action)) {
				LogHelper.logD(TAG, "Action is not a valid action. Action = " + action);
				PluginResult pluginResult = new PluginResult(PluginResult.Status.INVALID_ACTION);
				return pluginResult;
			}
			
			LogHelper.logD(TAG, "RobotManagerPlugin execute with action :" + action);
			LogHelper.logD(TAG, "\tdata :" + data);

			handlePluginExecute(action, data, callbackId);
			PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(true);
			return pluginResult;
		}
		
		// Private helper method to check, if we support the action
		// returns true, if we support the action, false otherwise
		private boolean isValidAction(String action) {
			return ACTION_MAP.containsKey(action);
		}

		private void handlePluginExecute(String action, JSONArray data, String callbackId) {
			RobotJsonData jsonData = new RobotJsonData(data);
			Context context = cordova.getActivity();
			RobotManagerPluginMethods commandId = convertToInternalAction(action);
			switch(commandId) {
			case DISCOVER_NEAR_BY_ROBOTS:
				LogHelper.log(TAG, "DISCOVER action initiated");
				discoverRobot(context, jsonData , callbackId);
				break;
			case TRY_DIRECT_CONNECTION2:	
				LogHelper.log(TAG, "try connection 2 called");
				tryDirectConnection2(context, jsonData , callbackId);
				break;
			case SEND_COMMAND_TO_ROBOT2:
				LogHelper.log(TAG, "ROBOTCOMMAND action initiated");
				sendCommand2(context, jsonData, callbackId);
				// No need to wait for other callbacks
				break;
			case SET_ROBOT_SCHEDULE:
				LogHelper.log(TAG, "ADVANCED_SCHEDULE_ROBOT action initiated");
				setAdvancedSchedule(context, jsonData, callbackId);
				break;
			case GET_ROBOT_SCHEDULE:
				LogHelper.log(TAG, "GET_ROBOT_SCHEDULE action initiated");
				getRobotSchedule(context, jsonData, callbackId);				
				break;
				
			case DELETE_ROBOT_SCHEDULE:
				LogHelper.log(TAG, "DELETE_ROBOT_SCHEDULE action initiated");				
				deleteRobotSchedule(context, jsonData, callbackId);
				break;
			case DISCONNECT_DIRECT_CONNECTION:
				LogHelper.log(TAG, "DISCONNECT_PEER action initiated");
				disconnectPeerConnection(context, jsonData, callbackId);
				break;
			case SET_ROBOT_NAME:
				LogHelper.log(TAG, "SET_ROBOT_NAME action initiated");
				setRobotName(context, jsonData, callbackId);
				break;
			case SET_ROBOT_NAME_2:
				LogHelper.log(TAG, "SET_ROBOT_NAME_2 action initiated");
				setRobotName2(context, jsonData, callbackId);
				break;
			case GET_ROBOT_DETAIL:
				LogHelper.log(TAG, "GET_ROBOT_DETAIL action initiated");
				getRobotDetail(context, jsonData, callbackId);
				break;
				
			case ROBOT_ONLINE_STATUS:
				LogHelper.log(TAG, "ROBOT_ONLINE_STATUS action initiated");
				getRobotOnlineStatus(context, jsonData, callbackId);
				break;
				
			case GET_ROBOT_VIRTUAL_ONLINE_STATUS:
				LogHelper.log(TAG, "GET_ROBOT_VIRTUAL_ONLINE_STATUS action initiated");
				getRobotVirtualOnlineStatus(context, jsonData, callbackId);
				break;
				
			case REGISTER_ROBOT_NOTIFICATIONS:
				LogHelper.log(TAG, "REGISTER_ROBOT_NOTIFICATIONS action initiated");
				registerRobotNotifications(context, jsonData, callbackId);
				break;
				
			case UNREGISTER_ROBOT_NOTIFICATIONS:
				LogHelper.log(TAG, "UNREGISTER_ROBOT_NOTIFICATIONS action initiated");
				unregisterRobotStatusNotification(context, jsonData, callbackId);
				break;
			case GET_SCHEDULE_EVENTS:
				LogHelper.log(TAG, "GET_SCHEDULE_EVENTS action initiated");
				getScheduleEvents(context, jsonData, callbackId);
				break;
			case ADD_ROBOT_SCHEDULE_EVENT:
				LogHelper.log(TAG, "ADD_ROBOT_SCHEDULE_EVENT action initiated");
				addScheduleEvent(context, jsonData, callbackId);
				break;
			case UPDATE_ROBOT_SCHEDULE_EVENT:
				LogHelper.log(TAG, "UPDATE_ROBOT_SCHEDULE_EVENT action initiated");
				updateScheduleEvent(context, jsonData, callbackId);
				break;
			case DELETE_ROBOT_SCHEDULE_EVENT:
				LogHelper.log(TAG, "DELETE_ROBOT_SCHEDULE_EVENT action initiated");
				deleteScheduleEvent(context, jsonData, callbackId);
				break;
			case GET_SCHEDULE_EVENT_DATA:
				LogHelper.log(TAG, "GET_SCHEDULE_EVENT_DATA action initiated");
				getScheduleEventData(context, jsonData, callbackId);
				break;
			case UPDATE_SCHEDULE:
				LogHelper.log(TAG, "UPDATE_SCHEDULE action initiated");
				updateSchedule(context, jsonData, callbackId);
				break;
			case CREATE_SCHEDULE:
				LogHelper.log(TAG, "CREATE_SCHEDULE action initiated");
				createSchedule(context, jsonData, callbackId);
				break;
			case GET_SCHEDULE_DATA:
				LogHelper.log(TAG, "GET_SCHEDULE_DATA action initiated");
				getScheduleData(context, jsonData, callbackId);
				break;
			case SYNC_SCHEDULE_FROM_SERVER:
				LogHelper.log(TAG, "SYNC_SCHEDULE_FROM_SERVER action initiated");
				syncScheduleFromServer(context, jsonData, callbackId);
				break;
			case SET_SPOT_DEFINITION:
				LogHelper.log(TAG, "SET_SPOT_DEFINITION action initiated");
				setSpotDefinition(context, jsonData, callbackId);
				break;
			case GET_SPOT_DEFINITION:
				LogHelper.log(TAG, "GET_SPOT_DEFINITION action initiated");
				getSpotDefinition(context, jsonData, callbackId);
				break;
			case START_CLEANING:
				LogHelper.log(TAG, "START_CLEANING COMMAND initiated");
				sendCleaningCommandToRobot(context, RobotCommandPacketConstants.COMMAND_ROBOT_START, jsonData, callbackId);
				break;
			case STOP_CLEANING:
				LogHelper.log(TAG, "STOP_CLEANING COMMAND initiated");
				sendCleaningCommandToRobot(context, RobotCommandPacketConstants.COMMAND_ROBOT_STOP, jsonData, callbackId);
				break;
			case PAUSE_CLEANING:
				LogHelper.log(TAG, "PAUSE_CLEANING COMMAND initiated");
				sendCleaningCommandToRobot(context, RobotCommandPacketConstants.COMMAND_PAUSE_CLEANING, jsonData, callbackId);
				break;
			case RESUME_CLEANING:
				LogHelper.log(TAG, "RESUME_CLEANING COMMAND initiated");
				sendCleaningCommandToRobot(context, RobotCommandPacketConstants.COMMAND_RESUME_CLEANING, jsonData, callbackId);
				break;
			case DRIVE_ROBOT:
				LogHelper.log(TAG, "DRIVE_ROBOT action initiated");
				driveRobot(context, jsonData, callbackId);
				break;
			case IS_SCHEDULE_ENABLED:
				LogHelper.log(TAG, "IS_SCHEDULE_ENABLED action initiated");
				isScheduleEnabled(context, jsonData, callbackId);
				break;
			case ENABLE_SCHEDULE:
				LogHelper.log(TAG, "ENABLE_SCHEDULE action initiated");
				enableSchedule(context, jsonData, callbackId);
				break;
			case TURN_VACUUM_ON_OFF:
				LogHelper.log(TAG, "TURN_VACUUM_ON_OFF action initiated");
				turnVacuumOnOff(context, jsonData, callbackId);
				break;
			case TURN_WIFI_ON_OFF:
				LogHelper.log(TAG, "TURN_WIFI_ON_OFF action Initiated");
				turnWiFiOnOff(context, jsonData, callbackId);
				break;			
			case REGISTER_FOR_ROBOT_MESSAGES:
				LogHelper.log(TAG, "REGISTER_FOR_ROBOT_MESSAGES initiated");
				registerForRobotsMessages(context, jsonData, callbackId);
				break;
			case UNREGISTER_FOR_ROBOT_MESSAGES:
				LogHelper.log(TAG, "UNREGISTER_FOR_ROBOT_MESSAGES initiated");
				unregisterForRobotMessages(context, jsonData, callbackId);
				break;
			case REGISTER_ROBOT_NOTIFICATIONS2:
				LogHelper.log(TAG, "REGISTER_ROBOT_NOTIFICATIONS2 initiated");
				registerRobotNotifications2(context, jsonData, callbackId);
				break;
			
			case UNREGISTER_ROBOT_NOTIFICATIONS2:
				LogHelper.log(TAG, "UNREGISTER_ROBOT_NOTIFICATIONS2 initiated");
				unregisterRobotNotifications2(context, jsonData, callbackId);
				break;
			case GET_ROBOT_CLEANING_STATE:
				LogHelper.log(TAG, "GET_ROBOT_CLEANING_STATE initiated");
				getRobotCleaningState(context, jsonData, callbackId);
				break;
			}
		}
		
		private void getRobotCleaningState(final Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "getRobotCleaningState is called");
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			RobotManager.getInstance(context).getRobotCleaningState(context, robotId, new RobotRequestListenerWrapper(callbackId) {
				@Override
				public JSONObject getResultObject(
						NeatoWebserviceResult responseResult)
						throws JSONException {
					JSONObject jsonResult = new JSONObject();
					if((responseResult != null) && (responseResult instanceof GetRobotProfileDetailsResult2)) {
						GetRobotProfileDetailsResult2 result = (GetRobotProfileDetailsResult2) responseResult;
						String currentState = RobotProfileDataUtils.getRobotCurrentState(context, result);
						String state = RobotProfileDataUtils.getState(context, result);
						if (!TextUtils.isEmpty(currentState)) {
							jsonResult.put(JsonMapKeys.KEY_ROBOT_CURRENT_STATE, currentState);
						}
						if (!TextUtils.isEmpty(state)) {
							jsonResult.put(JsonMapKeys.KEY_ROBOT_NEW_VIRTUAL_STATE, state);
						}
						jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
					}
					return jsonResult;
				}
			});
		}

		private void registerForRobotsMessages(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "registerForRobotsMessages called");
			PushNotificationMessageHandler.getInstance(context).addPushNotificationListener(new RobotPushNotificationListener(callbackId));
		}
		
		private void unregisterForRobotMessages(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "unregisterForRobotMessages called");
			PushNotificationMessageHandler.getInstance(context).removePushNotificationListener();
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
			pluginResult.setKeepCallback(false);
			success(pluginResult, callbackId);
		}
		
		// Private helper method to send the cleaning commands. Keeping Cleaning command helper method separate so that
		// we can detect the kind of cleaning and the extra params required for the cleaning command
		private void sendCleaningCommandToRobot(Context context, int commandId, RobotJsonData jsonData,
				final String callbackId) {
			
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			JSONObject commandParams = jsonData.getJsonObject(JsonMapKeys.KEY_COMMAND_PARAMETERS);
			HashMap<String, String> commadParamsMap = getCommandParams(commandParams);
			LogHelper.logD(TAG, "sendCommand2 - COMMAND_ROBOT_START");
			// Get cleaning category
			int cleaningCategory = 0;
			if (commandId == RobotCommandPacketConstants.COMMAND_ROBOT_START) {
				if (!TextUtils.isEmpty(commadParamsMap.get(JsonMapKeys.KEY_CLEANING_CATEGORY))) {
					cleaningCategory = Integer.valueOf(commadParamsMap.get(JsonMapKeys.KEY_CLEANING_CATEGORY));
				}
				if (cleaningCategory == RobotCommandPacketConstants.CLEANING_CATEGORY_SPOT) {
					CleaningSettings cleaningSettings = RobotHelper.getCleaningSettings(context, robotId);
					if (cleaningSettings == null) {
						LogHelper.log(TAG, "Spot definition not set. Callback Id = " + callbackId);
						sendError(callbackId, ErrorTypes.INVALID_PARAMETER, "Spot Definition Not Set");
						return;
					}
					String spotAreaLength = String.valueOf(cleaningSettings.getSpotAreaLength());
					String spotAreaHeight = String.valueOf(cleaningSettings.getSpotAreaHeight());
					commadParamsMap.put(JsonMapKeys.KEY_SPOT_CLEANING_AREA_LENGTH, spotAreaLength);
					commadParamsMap.put(JsonMapKeys.KEY_SPOT_CLEANING_AREA_HEIGHT, spotAreaHeight);
				}
			}
			
			sendCommandHelper(context, robotId, commandId, commadParamsMap, callbackId);
		}

		private void driveRobot(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "driveRobot action initiated in Robot plugin");	
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			String navigationControlId = jsonData.getString(JsonMapKeys.KEY_NAVIGATION_CONTROL_ID);
			LogHelper.logD(TAG, "Params\n\tRobotId=" + robotId);
			LogHelper.logD(TAG, "\n\tNavigation Control Id = " + navigationControlId);
			
			HashMap<String, String> commandParams = new HashMap<String, String>();
			commandParams.put(JsonMapKeys.KEY_NAVIGATION_CONTROL_ID, navigationControlId);
			
			sendCommandHelper(context, robotId, RobotCommandPacketConstants.COMMAND_DRIVE_ROBOT, 
					commandParams, callbackId);
		}
		
		// Private helper method to turn on/off vacuum
		// These are utilities functions and can be used by the PhoneGap plugin to turn on/off the
		// vacuum. As of now there is no UI to turn on/off the vacuum
		private void turnVacuumOnOff(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "turnVacuumOnOff called");

			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			String flagOn = jsonData.getString(JsonMapKeys.KEY_FLAG_ON);

			HashMap<String, String> commandParamsMap = new HashMap<String, String>();
			commandParamsMap.put(JsonMapKeys.KEY_FLAG_ON_OFF, flagOn);
						
			sendCommandHelper(context, robotId, RobotCommandPacketConstants.COMMAND_TURN_VACUUM_ONOFF, 
					commandParamsMap, callbackId);
		}

		// Private helper method to turn on/off WiFi
		// When switching on the WiFi we also except duration in milliseconds to keep the 
		// WiFi on. After this time out WiFi may turn off
		// These are utilities functions and can be used by the PhoneGap plugin to turn on/off the
		// vacuum. As of now there is no UI to turn on/off the vacuum
		private void turnWiFiOnOff(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "turnWiFiOnOff called");
			
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			JSONObject commandParams = jsonData.getJsonObject(JsonMapKeys.KEY_COMMAND_PARAMETERS);
			HashMap<String, String> commadParamsMap = getCommandParams(commandParams);
			
			sendCommandHelper(context, robotId, RobotCommandPacketConstants.COMMAND_TURN_WIFI_ONOFF, 
					commadParamsMap, callbackId);
		}
		
		private void updateSchedule(final Context context, RobotJsonData jsonData,
				final String callbackId) {
			final String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			RobotSchedulerManager2.getInstance(context).addUpdateSchedule(scheduleId, new ScheduleRequestListenerWrapper(callbackId) {
				@Override
				public void onScheduleData(JSONObject scheduleJson) {
					super.onScheduleData(scheduleJson);
					final String robotId = ScheduleHelper.getRobotIdForSchedule(context, scheduleId);
					sendDataChangedCommand(context, robotId, RobotCommandPacketConstants.KEY_ROBOT_SCHEDULE_CHANGED);
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

		private void createSchedule(Context context, RobotJsonData jsonData, final String callbackId) {			
			int scheduleType = jsonData.getInt(JsonMapKeys.KEY_SCHEDULE_TYPE);
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);			
			RobotSchedulerManager2.getInstance(context).createSchedule(robotId, scheduleType, new ScheduleRequestListenerWrapper(callbackId));
		}		
		
		private void getScheduleEventData(Context context, RobotJsonData jsonData, final String callbackId) {
			String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			final String scheduleEventId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_EVENT_ID);
			RobotSchedulerManager2.getInstance(context).getScheduleEventData(scheduleId, scheduleEventId, new ScheduleRequestListenerWrapper(callbackId));
		}			
		
		private void updateScheduleEvent(Context context, RobotJsonData jsonData,
				final String callbackId) {
			final String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			final String scheduleEventId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_EVENT_ID);
			JSONObject event = jsonData.getJsonObject(JsonMapKeys.KEY_SCHEDULE_EVENT_DATA);
			int scheduleType = getScheduleTypeFromId(context, scheduleId);
			ScheduleEvent scheduleEvent = ScheduleJsonDataHelper2.jsonToSchedule(event, scheduleEventId, scheduleType);
			RobotSchedulerManager2.getInstance(context).updateScheduleEvent(scheduleEvent, scheduleId, new ScheduleRequestListenerWrapper(callbackId));
		}

		private void deleteScheduleEvent(Context context, RobotJsonData jsonData, final String callbackId) {
			final String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			final String scheduleEventId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_EVENT_ID);
			RobotSchedulerManager2.getInstance(context).deleteScheduleEvent(scheduleId, scheduleEventId, new ScheduleRequestListenerWrapper(callbackId));
		}

		private void addScheduleEvent(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.log(TAG, "JSON Received: "+ jsonData.toString());
			
			final String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			JSONObject event = jsonData.getJsonObject(JsonMapKeys.KEY_SCHEDULE_EVENT_DATA);
			final String eventId = AppUtils.generateNewScheduleEventId();
			int scheduleType = getScheduleTypeFromId(context, scheduleId);
			ScheduleEvent scheduleEvent = ScheduleJsonDataHelper2.jsonToSchedule(event, eventId, scheduleType);
			RobotSchedulerManager2.getInstance(context).addScheduleEvent(scheduleEvent, scheduleId, new ScheduleRequestListenerWrapper(callbackId));
		}

		
		
		private void getScheduleEvents(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.log(TAG, "getScheduleEvents");
			final int scheduleType = jsonData.getInt(JsonMapKeys.KEY_SCHEDULE_TYPE);
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			RobotSchedulerManager2.getInstance(context).getScheduleByType(robotId, scheduleType, new ScheduleRequestListenerWrapper(callbackId));				
		}
		
		// Newly added methods for new schedule pattern.
		private void getScheduleData(Context context, RobotJsonData jsonData, final String callbackId) {
			String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			RobotSchedulerManager2.getInstance(context).getSchedule(scheduleId, new ScheduleRequestListenerWrapper(callbackId));			
		}

		private void syncScheduleFromServer(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "syncScheduleFromServer action initiated in Robot plugin");	
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			RobotSchedulerManager2.getInstance(context).syncSchedulesFromServer(robotId, new ScheduleRequestListenerWrapper(callbackId));
		}
		
		// Private helper method to return the schedule enable/disable state.
		private void isScheduleEnabled(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "isScheduleEnabled action initiated in Robot plugin");
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			final int scheduleType = jsonData.getInt(JsonMapKeys.KEY_SCHEDULE_TYPE);
			int scheduleTypeOnServer = SchedulerConstants2.convertToServerConstants(scheduleType);
			
			RobotSchedulerManager2.getInstance(context).isScheduleEnabled(robotId, scheduleTypeOnServer, new ScheduleRequestListenerWrapper(callbackId) {

				@Override
				public JSONObject getResultObject(NeatoWebserviceResult responseResult)
						throws JSONException {
					JSONObject jsonResult;
					if((responseResult != null) && (responseResult instanceof IsScheduleEnabledResult)) {
						IsScheduleEnabledResult result = (IsScheduleEnabledResult) responseResult;					
						jsonResult = new JSONObject();
						jsonResult.put(JsonMapKeys.KEY_IS_SCHEDULE_ENABLED, result.isScheduledEnabled);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleType);
						jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
					} else {
						jsonResult = getErrorJsonObject(ErrorTypes.ERROR_TYPE_UNKNOWN, "response result is not of type is schedule enabled result");
					}
					return jsonResult;
				}				
			});
		}
		
		// Private helper method to return the schedule enable/disable state.
		// As of now we are storing the enable/disable status in shared preferences
		// later we need to call the Web API to enable/disable schedule
		private void enableSchedule(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "enableSchedule called");
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			final int scheduleType = jsonData.getInt(JsonMapKeys.KEY_SCHEDULE_TYPE);
			final boolean enableSchedule = jsonData.getBoolean(JsonMapKeys.KEY_ENABLE_SCHEDULE);
			
			final int scheduleTypeOnServer = SchedulerConstants2.convertToServerConstants(scheduleType);
			RobotSchedulerManager2.getInstance(context).setEnableSchedule(robotId, scheduleTypeOnServer, enableSchedule, new ScheduleRequestListenerWrapper(callbackId) {

				@Override
				public JSONObject getResultObject(NeatoWebserviceResult responseResult)
						throws JSONException {
					JSONObject jsonResult;
					if((responseResult != null) && (responseResult instanceof SetRobotProfileDetailsResult3)) {
						jsonResult = new JSONObject();
						jsonResult.put(JsonMapKeys.KEY_IS_SCHEDULE_ENABLED, enableSchedule);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleType);
						jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
					} else {
						jsonResult = getErrorJsonObject(ErrorTypes.ERROR_TYPE_UNKNOWN, "response result is not of type set profile details result");
					}
					return jsonResult;
				}				
			});
		}

		private void setSpotDefinition(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "setSpotDefinition action initiated in Robot plugin");	
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			int spotCleaningAreaLength = 0;
			int spotCleaningAreaHeight = 0;
			
			try {
				if (!TextUtils.isEmpty(jsonData.getString(JsonMapKeys.KEY_SPOT_CLEANING_AREA_LENGTH))) {
					spotCleaningAreaLength = Integer.valueOf(jsonData.getString(JsonMapKeys.KEY_SPOT_CLEANING_AREA_LENGTH));
				}
				if (!TextUtils.isEmpty(jsonData.getString(JsonMapKeys.KEY_SPOT_CLEANING_AREA_HEIGHT))) {
					spotCleaningAreaHeight = Integer.valueOf(jsonData.getString(JsonMapKeys.KEY_SPOT_CLEANING_AREA_HEIGHT));
				}
				
				LogHelper.logD(TAG, "Params\n\tRobotId= " + robotId + "\n\tSpot Area Length = " + spotCleaningAreaLength + 
						"\n\tSpot Area Height = " + spotCleaningAreaHeight);
				
				SettingsManager.getInstance(context).updateSpotDefinition(robotId, spotCleaningAreaLength, 
						spotCleaningAreaHeight, new CleaningSettingsListener() {
					@Override
					public void onSuccess(CleaningSettings cleaningSettings) {
						PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
						pluginResult.setKeepCallback(false);
						success(pluginResult, callbackId);
					}

					@Override
					public void onError() {
						LogHelper.log(TAG, "Unable to update robot spot definition");
						sendError(callbackId, ErrorTypes.ERROR_DB_ERROR, "Unable to update robot spot definition");
					}
				});
			}
			catch (NumberFormatException e) {
				LogHelper.logD(TAG, "Exception in setSpotDefinition", e);
				sendError(callbackId, ErrorTypes.INVALID_PARAMETER, "Invalid spot definition parameters specified");
			}
		}
		
		private JSONObject getSpotDefinitionJsonObject(CleaningSettings cleaningSettings) {
			JSONObject spotDefinitionJsonObj = null;
			try {
				spotDefinitionJsonObj = new JSONObject();
				spotDefinitionJsonObj.put(JsonMapKeys.KEY_SPOT_CLEANING_AREA_LENGTH, cleaningSettings.getSpotAreaLength());
				spotDefinitionJsonObj.put(JsonMapKeys.KEY_SPOT_CLEANING_AREA_HEIGHT, cleaningSettings.getSpotAreaHeight());				
			}
			catch (JSONException e) {
				LogHelper.logD(TAG, "Exception in getSpotDefinitionJsonObject", e);
			}
			
			return spotDefinitionJsonObj;	
		}
		
		private void getSpotDefinition(Context context, RobotJsonData jsonData,
				final String callbackId) {
			LogHelper.logD(TAG, "getSpotDefinition action initiated in Robot plugin");	
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			LogHelper.logD(TAG, "Params\nRobotId=" + robotId);
			
			SettingsManager.getInstance(context).getCleaningSettings(robotId, new CleaningSettingsListener() {				
				@Override
				public void onSuccess(CleaningSettings cleaningSettings) {
					JSONObject spotDefinitionJsonObj = getSpotDefinitionJsonObject(cleaningSettings);
					if (spotDefinitionJsonObj != null) {
						PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, spotDefinitionJsonObj);
						pluginResult.setKeepCallback(false);
						success(pluginResult, callbackId);
					}
					else {
						LogHelper.log(TAG, "Unable to get spot definition JSON object");
						sendError(callbackId, ErrorTypes.JSON_CREATION_ERROR, "Unable to get spot definition JSON object");
					}
				}

				@Override
				public void onError() {
					LogHelper.log(TAG, "Unable to get robot spot definition");
					sendError(callbackId, ErrorTypes.ERROR_DB_ERROR, "Unable to get robot spot definition");
				}
			});
		}
		
		private void registerRobotNotifications(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "registerRobotNotifications action initiated in Robot plugin");
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			
			if (mRobotNotificationsPluginListener == null) {
				mRobotNotificationsPluginListener  = new RobotNotificationsPluginListener();
			}			
			mRobotNotificationsPluginListener.addRegisterCallbackId(robotId, callbackId);
			
			RobotCommandServiceManager.registerRobotNotificationsListener(context, robotId, mRobotNotificationsPluginListener);
		}
		
		private void unregisterRobotStatusNotification(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "unregisterRobotStatusNotification action initiated in Robot plugin");
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			
			if (mRobotNotificationsPluginListener == null) {
				mRobotNotificationsPluginListener  = new RobotNotificationsPluginListener();			
				ApplicationConfig.getInstance(context).getRobotResultReceiver().addRobotNotificationsListener(mRobotNotificationsPluginListener);
			}			
			mRobotNotificationsPluginListener.addUnregisterCallbackId(robotId, callbackId);			
			
			RobotCommandServiceManager.unregisteredRobotNotificationsListener(context, robotId);
		}

		private void registerRobotNotifications2(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "registerRobotNotifications2 action initiated in Robot plugin");
			
			RobotNotificationUtil.addRobotDataChangedListener(context, new RobotDataListener() {
				@Override
				public void onDataReceived(String robotId, int dataCode, HashMap<String, String> data) {
					JSONObject robotData = RobotNotificationUtil.getNotificationObject(robotId, dataCode, data);
					if (robotData != null) {
						PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, robotData);
						pluginResult.setKeepCallback(true);
						success(pluginResult, callbackId);
					}
				}
			});
		}
		
		private void unregisterRobotNotifications2(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "unregisterRobotNotifications2 action initiated in Robot plugin");
			RobotNotificationUtil.removeRobotDataChangedListener(context);
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
			pluginResult.setKeepCallback(false);
			success(pluginResult, callbackId);
		}
		
		private void setRobotName(final Context context, RobotJsonData jsonData,
				final String callbackId) {
			LogHelper.logD(TAG, "setRobotName action initiated in Robot plugin");	
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			String robotName = jsonData.getString(JsonMapKeys.KEY_ROBOT_NAME);
			
			RobotManager.getInstance(context).setRobotName(robotId, robotName, new RobotRequestListenerWrapper(callbackId) {
				
				@Override
				public void onReceived(NeatoWebserviceResult responseResult) {	
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);					
					
					sendDataChangedCommand(context, robotId, RobotCommandPacketConstants.KEY_ROBOT_DETAILS_CHANGED);					
				}
			});
		}
		
		private void setRobotName2(final Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "setRobotName2 action initiated in Robot plugin");	
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			final String robotName = jsonData.getString(JsonMapKeys.KEY_ROBOT_NAME);
			
			RobotManager.getInstance(context).setRobotName(robotId, robotName, new RobotRequestListenerWrapper(callbackId) {
				
				@Override
				public void onReceived(NeatoWebserviceResult responseResult) {					
					JSONObject robotJsonObj = getRobotDetailJsonObject(robotId, robotName);
					PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, robotJsonObj);	
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);
					sendDataChangedCommand(context, robotId, RobotCommandPacketConstants.KEY_ROBOT_DETAILS_CHANGED);
				}
			});	
		}

		private void getRobotDetail(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "getRobotDetail action initiated in Robot plugin");	
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			
			RobotManager.getInstance(context).getRobotDetail(robotId, new RobotRequestListenerWrapper(callbackId) {
				
				@Override
				public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
					JSONObject robotDetail = getRobotDetailJsonObject(responseResult);
					return robotDetail;
				}
			});
		}

		private void getRobotOnlineStatus(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "getRobotOnlineStatus action initiated in Robot plugin");	
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			
			RobotManager.getInstance(context).getRobotOnlineStatus(robotId, new RobotRequestListenerWrapper(callbackId) {
				
				@Override
				public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
					JSONObject resultJsonObj = null;
					if (responseResult instanceof RobotOnlineStatusResult) {
						resultJsonObj = getRobotOnlineStatusJsonObject(robotId, 
									((RobotOnlineStatusResult)responseResult).result.online);
						
					}
					return resultJsonObj;
				}						
			});
		}
		
		private void getRobotVirtualOnlineStatus(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "getRobotVirtualOnlineStatus action initiated in Robot plugin");	
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			
			RobotManager.getInstance(context).getRobotVirtualOnlineStatus(robotId, new RobotRequestListenerWrapper(callbackId) {
				
				@Override
				public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
					JSONObject resultJsonObj = null;
					if (responseResult instanceof RobotVirtualOnlineStatusResult) {
						resultJsonObj = getRobotOnlineStatusJsonObject(robotId, 
									((RobotVirtualOnlineStatusResult)responseResult).result.online);
						
					}
					return resultJsonObj;
				}						
			});
		}
		
		private JSONObject getRobotDetailJsonObject(RobotItem robotItem) {
			JSONObject robotJsonObj = new JSONObject();
			
			if (robotItem != null) {				
				try {
					robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, robotItem.serial_number);
					robotJsonObj.put(JsonMapKeys.KEY_ROBOT_NAME, robotItem.name);				
				}
				catch (JSONException e) {
					LogHelper.logD(TAG, "Exception in getRobotDetailJsonObject", e);
				}
			}
			
			return robotJsonObj;
		}

		// Private helper method to create the Robot JSON
		private JSONObject getRobotDetailJsonObject(String robotId, String robotName) {
			JSONObject robotJsonObj = new JSONObject();
			try {
				robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
				robotJsonObj.put(JsonMapKeys.KEY_ROBOT_NAME, robotName);				
			}
			catch (JSONException e) {
				LogHelper.logD(TAG, "Exception in getRobotDetailJsonObject", e);
			}
			return robotJsonObj;
		}
		
		private JSONObject getRobotDetailJsonObject(NeatoWebserviceResult responseResult) throws JSONException {
			JSONObject robotJsonObj = null;					
			if ((responseResult != null) && (responseResult instanceof RobotDetailResult)) {
				RobotItem  robotItem = ((RobotDetailResult)responseResult).result;
				if (robotItem != null) {
					robotJsonObj = new JSONObject();
					robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, robotItem.serial_number);
					robotJsonObj.put(JsonMapKeys.KEY_ROBOT_NAME, robotItem.name);	
				}
			}
			
			return robotJsonObj;
		}
		
		private JSONObject getRobotOnlineStatusJsonObject(String robotId, boolean online) throws JSONException {
			JSONObject robotJsonObj = new JSONObject();			
			robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
			robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ONLINE_STATUS, online);				
			
			return robotJsonObj;
		}
		
		private void disconnectPeerConnection(Context context,
				RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "disconnectPeerConnection action initiated in Robot plugin");
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			RobotCommandServiceManager.disconnectDirectConnection(context, robotId, new RobotPluginPeerConnectionListener(callbackId));
			PluginResult pluginLogoutResult = new PluginResult(PluginResult.Status.OK);
			pluginLogoutResult.setKeepCallback(false);
			success(pluginLogoutResult, callbackId);
		}

		private void tryDirectConnection2(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "Try direct connection action initiated in Robot plugin");
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			RobotCommandServiceManager.tryDirectConnection2(context, robotId, new RobotPluginPeerConnectionListener(callbackId));	
		}


		private void setAdvancedSchedule(final Context context, RobotJsonData jsonData, final String callbackId) {

			PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(false);
			sendError(callbackId, ErrorTypes.ERROR_NOT_SUPPORTED, "This API is not supported");
		}

		private void sendCommandHelper(Context context, String robotId, int commandId, HashMap<String, String> params, String callbackId) {
			// Create Robot state notification listener to notify when we get a robot state info
			// from the robot
			
			if (AppConstants.isServerDataModeEnabled() && RobotProfileConstants.isTimedModeSupportedForCommand(commandId)) {
				LogHelper.logD(TAG, "Sending command VIA webservice");
				RobotDataManager.sendRobotCommand(context, robotId, commandId, params, new RobotSetProfileDataRequestListener(callbackId));
				return;
			}
			
			if (commandId == RobotCommandPacketConstants.COMMAND_GET_ROBOT_STATE) {
				if (mRobotStateNotificationPluginListener == null) {
					mRobotStateNotificationPluginListener = new RobotStateNotificationPluginListener();
					RobotCommandServiceManager.registerRobotStateNotificationListener(context, mRobotStateNotificationPluginListener);
				}
				
				mRobotStateNotificationPluginListener.addCallbackId(robotId, callbackId);
			}
			
			RobotCommandServiceManager.sendCommand2(context, robotId, commandId, params);
			
			if (commandId != RobotCommandPacketConstants.COMMAND_GET_ROBOT_STATE) {
				PluginResult pluginStartResult = new PluginResult(PluginResult.Status.OK);
				pluginStartResult.setKeepCallback(false);
				success(pluginStartResult,callbackId);
			} 
		}
		
		private void sendCommand2(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "Send command action initiated in Robot plugin " + jsonData.toString());
			int commandId = jsonData.getInt(JsonMapKeys.KEY_COMMAND_ID);
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			JSONObject commandParams = jsonData.getJsonObject(JsonMapKeys.KEY_COMMAND_PARAMETERS);
			HashMap<String, String> commadParamsMap = getCommandParams(commandParams);
			sendCommandHelper(context, robotId, commandId, commadParamsMap, callbackId);
		} 

		private void discoverRobot(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "Discovery action initiated in Robot plugin");
			mRobotPluginDiscoveryListener  = new RobotPluginDiscoveryListener(callbackId);
			RobotCommandServiceManager.discoverRobot(context, mRobotPluginDiscoveryListener);
		}


		private void getRobotSchedule(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "getRobotSchedule action initiated in Robot plugin");
			PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(false);
			sendError(callbackId, ErrorTypes.ERROR_NOT_SUPPORTED, "This API is not supported");
		}
		
		private void deleteRobotSchedule(final Context context, final RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "Delete robot schedule action initiated in Robot plugin");
			@SuppressWarnings("unused")
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			
			// TODO: Needs to be implemented
			
		}

		private RobotManagerPluginMethods convertToInternalAction(String action) {
			LogHelper.logD(TAG, "convertToInternalAction - action = " + action);
			RobotManagerPluginMethods robotManagerPluginMethod = ACTION_MAP.get(action);
			return robotManagerPluginMethod;
		}


		private static class ActionTypes {
			public static final String DISCOVER_NEAR_BY_ROBOTS = "discoverNearByRobots";
			public static final String TRY_DIRECT_CONNECTION2 = "tryDirectConnection2";
			public static final String SEND_COMMAND_TO_ROBOT2 = "sendCommandToRobot2";
			public static final String SET_ROBOT_SCHEDULE = "robotSetSchedule";
			public static final String GET_ROBOT_SCHEDULE = "getSchedule";
			public static final String DISCONNECT_DIRECT_CONNECTION = "disconnectDirectConnection";
			public static final String SET_ROBOT_NAME = "setRobotName";
			public static final String DELETE_ROBOT_SCHEDULE = "deleteScheduleData";
			public static final String GET_ROBOT_DETAIL = "getRobotDetail";
			public static final String SET_ROBOT_NAME_2 = "setRobotName2";
			public static final String GET_ROBOT_ONLINE_STATUS = "getRobotOnlineStatus";
			public static final String GET_ROBOT_VIRTUAL_ONLINE_STATUS = "getRobotVirtualOnlineStatus";
			public static final String REGISTER_ROBOT_NOTIFICATIONS = "registerRobotNotifications";
			public static final String UNREGISTER_ROBOT_NOTIFICATIONS = "unregisterRobotNotifications";
			public static final String UPDATE_SCHEDULE = "updateSchedule";
			public static final String DELETE_ROBOT_SCHEDULE_EVENT = "deleteScheduleEvent";
			public static final String UPDATE_ROBOT_SCHEDULE_EVENT = "updateScheduleEvent";
			public static final String GET_SCHEDULE_EVENT_DATA = "getScheduleEventData";
			public static final String ADD_ROBOT_SCHEDULE_EVENT = "addScheduleEventData";
			public static final String GET_SCHEDULE_EVENTS = "getScheduleEvents";
			public static final String GET_SCHEDULE_DATA = "getScheduleData";
			public static final String CREATE_SCHEDULE = "createSchedule";
			public static final String SYNC_SCHEDULE_FROM_SERVER = "syncScheduleFromServer";
			public static final String SET_SPOT_DEFINITION = "setSpotDefinition";
			public static final String GET_SPOT_DEFINITION = "getSpotDefinition";
			public static final String START_CLEANING = "startCleaning";
			public static final String STOP_CLEANING = "stopCleaning";
			public static final String PAUSE_CLEANING = "pauseCleaning";
			public static final String RESUME_CLEANING = "resumeCleaning";
			public static final String DRIVE_ROBOT = "driveRobot";
			public static final String TURN_VACUUM_ON_OFF = "turnVacuumOnOff";
			public static final String TURN_WIFI_ON_OFF = "turnWiFiOnOff";
			public static final String IS_SCHEDULE_ENABLED = "isScheduleEnabled";
			public static final String ENABLE_SCHEDULE = "enableSchedule";
			public static final String REGISTER_FOR_ROBOT_MESSAGES = "registerForRobotMessges";
			public static final String UNREGISTER_FOR_ROBOT_MESSAGES = "unregisterForRobotMessages";
			public static final String REGISTER_ROBOT_NOTIFICATIONS2 = "registerRobotNotifications2";
			public static final String UNREGISTER_ROBOT_NOTIFICATIONS2 = "unregisterRobotNotifications2";
			public static final String GET_ROBOT_CLEANING_STATE = "getRobotCleaningState";
		}

		private JSONObject getErrorJsonObject(int errorCode, String errMessage) {
			JSONObject error = new JSONObject();
			try {
				error.put(JsonMapKeys.KEY_ERROR_CODE, errorCode);
				error.put(JsonMapKeys.KEY_ERROR_MESSAGE, errMessage);
			} catch (JSONException e) {
				LogHelper.logD(TAG, "Exception in getErrorJsonObject", e);
			}
			return error;
		}
		
		@SuppressWarnings("unused")
		private void dispatchEvent(String eventName) {
			LogHelper.log(TAG, "Event called");
			String javascriptTemplate = "var e = document.createEvent('Events');\n" +
					"e.initEvent('"+eventName+"');\n" +	                    
					"document.dispatchEvent(e);";
			this.sendJavascript(javascriptTemplate);		
		}

		private class RobotPluginDiscoveryListener implements RobotDiscoveryListener {

			private String mCallBackId;
			private JSONArray robotList = new JSONArray();

			RobotPluginDiscoveryListener(String callBackId) {
				mCallBackId = callBackId;
			}

			@Override
			public void onNewRobotFound(RobotInfo robotInfo) {
				LogHelper.log(TAG, "Robot Found with name: " + robotInfo.getRobotName());
				addRobotToList(robotInfo);
			}

			@Override
			public void onDiscoveryStarted() {
				LogHelper.log(TAG, "Discovery started");
			}

			@Override
			public void onDiscoveryFinished() {
				LogHelper.log(TAG, "Discovery Finished");

				PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.OK, robotList);
				loginUserPluginResult.setKeepCallback(false);
				success(loginUserPluginResult, mCallBackId);

			}
			@Override
			public void discoveryError() {
				JSONObject error = getErrorJsonObject(0, "Discovery Error");
				PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.ERROR, error);
				error(loginUserPluginResult, mCallBackId);

			}

			public void addRobotToList(RobotInfo robotInfo) {
				robotList.put(robotJsonObject(robotInfo));
			}

			public JSONObject robotJsonObject(RobotInfo robotInfo) {
				JSONObject robot = new JSONObject();
				try {
					robot.put(JsonMapKeys.KEY_ROBOT_NAME, robotInfo.getRobotName());
					robot.put(JsonMapKeys.KEY_ROBOT_ID, robotInfo.getSerialId());
				} catch (JSONException e) {
					LogHelper.log(TAG, "Exception in robotJsonObject", e);
				}
				return robot;
			}
		}


		private class RobotPluginPeerConnectionListener implements RobotPeerConnectionListener {

			private String mCallBackId;

			RobotPluginPeerConnectionListener(String callbackId) {
				mCallBackId = callbackId;
			}

			@Override
			public void onRobotConnected(String robotId) {
				PluginResult peerConnectionPluginResult = new  PluginResult(PluginResult.Status.OK);
				peerConnectionPluginResult.setKeepCallback(true);
				LogHelper.log(TAG, "Connected to peer");
				success(peerConnectionPluginResult, mCallBackId);
			}

			@Override
			public void onRobotDisconnected(String robotId) {
				PluginResult peerConnectionPluginResult = new  PluginResult(PluginResult.Status.OK);
				LogHelper.log(TAG, "Disconnected from peer");
				peerConnectionPluginResult.setKeepCallback(false);
				success(peerConnectionPluginResult, mCallBackId);			
			}

			@Override
			public void errorInConnecting(String robotId) {
				PluginResult peerConnectionPluginResult = new  PluginResult(PluginResult.Status.ERROR);
				peerConnectionPluginResult.setKeepCallback(true);
				LogHelper.log(TAG, "Robot could not be connected");
				error(peerConnectionPluginResult, mCallBackId);
			}

		} 

		
		private class RobotNotificationsPluginListener implements RobotNotificationsListener {
			private Map<String, String> mRegCallbackIdsMap = new HashMap<String, String>();
			private Map<String, String> mUnregCallbackIdsMap = new HashMap<String, String>();			
			
			public RobotNotificationsPluginListener() {
			}
			
			public void addUnregisterCallbackId(String robotId, String callbackId) {
				LogHelper.logD(TAG, String.format("RobotNotificationsPluginListener:addUnregisterCallbackId - RobotID = [%s] CallbackId = [%s]", robotId, callbackId));
				mUnregCallbackIdsMap.put(robotId, callbackId);				
			}			
			
			public void addRegisterCallbackId(String robotId, String regCallbackId) {
				LogHelper.logD(TAG, String.format("RobotNotificationsPluginListener:addRegisterCallbackId - RobotID = [%s] CallbackId = [%s]", robotId, regCallbackId));
				mRegCallbackIdsMap.put(robotId, regCallbackId);				
			}
			
			@Override
			public void onStatusChanged(String robotId, Bundle bundle) {			
				LogHelper.logD(TAG, "RobotNotificationsPluginListener:onStatusChanged - " + robotId);
				if (bundle != null) { 
					for (String key: bundle.keySet()) {
						LogHelper.logD(TAG, key + " = " + bundle.getString(key));
					}
				}
				
				String callbackId = mRegCallbackIdsMap.get(robotId);
				LogHelper.logD(TAG, "RobotNotificationsPluginListener:onStatusChanged - CallbackId = " + callbackId);
				if (!TextUtils.isEmpty(callbackId)) {
					JSONObject eventJsonObj = getEventJsonObject(JsonMapKeys.EVENT_ID_STATUS, robotId, bundle);
					PluginResult successPluginResult = new PluginResult(PluginResult.Status.OK, eventJsonObj);
					successPluginResult.setKeepCallback(true);				
					success(successPluginResult, callbackId);					
				}
			}
			
			@Override
			public void onRegister(String robotId, Bundle bundle) {
				LogHelper.logD(TAG, "RobotNotificationsPluginListener:onRegister - " + robotId);	
	
				String callbackId= mRegCallbackIdsMap.get(robotId);
				if (!TextUtils.isEmpty(callbackId)) {
					boolean resultSuccess = getRegisterResult(bundle);					
					JSONObject eventJsonObj = getEventJsonObject(JsonMapKeys.EVENT_ID_REGISTER, robotId, bundle);
					PluginResult successPluginResult = new  PluginResult(PluginResult.Status.OK, eventJsonObj);
					successPluginResult.setKeepCallback(resultSuccess);	
					LogHelper.logD(TAG, "RobotNotificationsPluginListener:onRegister - CallbackId = " + callbackId);					
					if (resultSuccess) {
						success(successPluginResult, callbackId);
					}
					else {
						error(successPluginResult, callbackId);
						if (!resultSuccess) {
							mRegCallbackIdsMap.remove(robotId);
						}
					}
					
				}
			}
			
			@Override
			public void onUnregister(String robotId, Bundle bundle) {				
				LogHelper.logD(TAG, "RobotNotificationsPluginListener:onUnRegister - " + robotId);
				
				String callbackId = mUnregCallbackIdsMap.get(robotId);
				if (!TextUtils.isEmpty(callbackId)) {
					boolean resultSuccess = getUnregisterResult(bundle);
					
					JSONObject eventJsonObj = getEventJsonObject(JsonMapKeys.EVENT_ID_UNREGISTER, robotId, bundle);
					PluginResult successPluginResult = new  PluginResult(PluginResult.Status.OK, eventJsonObj);
					successPluginResult.setKeepCallback(false);
					if (resultSuccess) {
						success(successPluginResult, callbackId);
					}
					else {
						error(successPluginResult, callbackId);
					}
					
					LogHelper.logD(TAG, "RobotNotificationsPluginListener:onUnRegister - CallbackId = " + callbackId);
					mUnregCallbackIdsMap.remove(robotId);
					
					if (resultSuccess) {						
						mRegCallbackIdsMap.remove(robotId);
					}
				}
			}
			
			private boolean getRegisterResult(Bundle bundle) {
				String resultValue = bundle.getString(JsonMapKeys.KEY_REGISTER_RESULT);
				return Boolean.valueOf(resultValue);
			}
			
			private boolean getUnregisterResult(Bundle bundle) {
				String resultValue = bundle.getString(JsonMapKeys.KEY_UNREGISTER_RESULT);
				return Boolean.valueOf(resultValue);
			}
			
			private JSONObject getEventJsonObject(int eventId, String robotId, Bundle bundle) {
				JSONObject eventInfo = new JSONObject();
				try {
					eventInfo.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
					eventInfo.put(JsonMapKeys.KEY_EVENT_NOTIFICATION_ID, eventId);
					JSONObject paramsObj = new JSONObject();
					if (bundle != null) {
						for (String key: bundle.keySet()) {
							paramsObj.put(key, bundle.getString(key));						
						}
					}
					eventInfo.put(JsonMapKeys.KEY_EVENT_NOTIFICATION_PARAMS, paramsObj);					
				} catch (JSONException e) {
					LogHelper.logD(TAG, "Exception in getEventJsonObject", e);
				}
				return eventInfo;
			}

			@Override
			public void onRobotCommandReceived(String robotId, String commandId, Bundle bundle) {
				JSONObject commandObj = new JSONObject();
				try {
					commandObj.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
					JSONObject paramsObj = new JSONObject();
					for (String key: bundle.keySet()) {
						paramsObj.put(key, bundle.getString(key));						
					}
					commandObj.put(JsonMapKeys.KEY_COMMAND_PARAMETERS, paramsObj);					
				} catch (JSONException e) {
					LogHelper.logD(TAG, "Exception in onRobotCommandReceived", e);
				}
			}
			
		}
		
		private class RobotStateNotificationPluginListener implements RobotStateListener {			
			private Map<String, String> mCallbackIdsMap = new HashMap<String, String>();
			
			public RobotStateNotificationPluginListener() {
			
			}
			
			public void addCallbackId(String robotId, String callbackId) {
				mCallbackIdsMap.put(robotId, callbackId);
			}
			
			@Override
			public void onStateReceived(String robotId, Bundle bundle) {
				LogHelper.logD(TAG, "RobotStateNotificationPluginListener:onStateReceived for " + robotId);
				for (String key: bundle.keySet()) {
					LogHelper.logD(TAG, key + " = " + bundle.getString(key));
				}
				
				sendStateInfo(robotId, bundle);			
			}
			
			private void sendStateInfo(String robotId, Bundle bundle) {
				String callbackId = mCallbackIdsMap.get(robotId);
				if (!TextUtils.isEmpty(callbackId)) {
					JSONObject stateInfo = getStateJsonObject(robotId, bundle);
					PluginResult successPluginResult = new  PluginResult(PluginResult.Status.OK, stateInfo);
					successPluginResult.setKeepCallback(false);
					success(successPluginResult, callbackId);
					LogHelper.logD(TAG, "sendStateInfo for CallbackId " + callbackId);
					mCallbackIdsMap.remove(robotId);
				}				
			}
			
			private JSONObject getStateJsonObject(String robotId, Bundle bundle) {
				JSONObject stateInfo = new JSONObject();
				try {
					stateInfo.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
					for (String key: bundle.keySet()) {
						stateInfo.put(key, bundle.getString(key));						
					}
					
				} catch (JSONException e) {
					LogHelper.logD(TAG, "Exception in getStateJsonObject", e);
				}
				return stateInfo;
			}
		}
		
		private void sendError(String callbackId, int errorCode, String message)
		{
			JSONObject errorInfo = getErrorJsonObject(errorCode, message);
			PluginResult errorPluginResult = new  PluginResult(PluginResult.Status.ERROR, errorInfo);
			errorPluginResult.setKeepCallback(false);
			error(errorPluginResult, callbackId);
		}
		
		@SuppressWarnings("unused")
		private void sendError(String callbackId, int errorCode, int errorResId)
		{
			String message = cordova.getActivity().getString(errorResId);
			sendError(callbackId, errorCode, message);
		}
		
		
		private static HashMap<String, String> getCommandParams(JSONObject jObject) {
			HashMap<String, String> commandParamsMap = null;
			if (jObject == null) {
				return new HashMap<String, String>();
			}
			if (jObject.has(RobotCommandPacketConstants.KEY_COMMAND_PARAMS_TAG)) {
				// Command params are present. Need to parse the JSON object and convert it to HashMap
				try {
					JSONObject commandParams = jObject.getJSONObject(RobotCommandPacketConstants.KEY_COMMAND_PARAMS_TAG);
					commandParamsMap = DataConversionUtils.jsonObjectToHashMap(commandParams);
				} catch (JSONException e) {
					LogHelper.log(TAG, "Exception in getCommandParams", e);
				}	
			}
			return commandParamsMap;
		}
		
		private void sendDataChangedCommand(Context context, String robotId, int dataCode) 
		{
			HashMap<String, String> commandParams = new HashMap<String, String>();
			String dataCodeStr = DataConversionUtils.convertIntToString(dataCode);
			commandParams.put(RobotCommandPacketConstants.KEY_DATA_CODE_CHANGED_ON_SERVER, dataCodeStr);
			RobotCommandServiceManager.sendCommand2(context, robotId, RobotCommandPacketConstants.COMMAND_DATA_CHANGED_ON_SERVER, commandParams);
		}
		
		public int getScheduleTypeFromId(Context context, String id) {
			String scheduleType = ScheduleHelper.getScheduleType(context, id);
			return SchedulerConstants2.getScheduleIntType(scheduleType);
		}
		
		
		private class RobotRequestListenerWrapper implements WebServiceBaseRequestListener {
			private String mCallbackId;
			
			public RobotRequestListenerWrapper(String callbackId) {
				mCallbackId = callbackId;
			}
			
			@Override
			public void onNetworkError(String errorMessage) {
				LogHelper.logD(TAG, "Network Error: " + errorMessage);
				sendError(mCallbackId, ErrorTypes.ERROR_NETWORK_ERROR, errorMessage);
			}
			
			@Override
			public void onReceived(NeatoWebserviceResult responseResult) {
				LogHelper.logD(TAG, "Request processed successfully");
				try {
					JSONObject resultObj = getResultObject(responseResult);
					if (resultObj != null) {
						PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, resultObj);		
						success(pluginResult, mCallbackId);
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
			public void onServerError(int errorType, String errorMessage) {
				LogHelper.logD(TAG, "Server Error: " + errorMessage);
				sendError(mCallbackId, errorType, errorMessage);
			}
		}
		
		// Use for time-mode request purposes
		private class RobotSetProfileDataRequestListener extends RobotRequestListenerWrapper {

			public RobotSetProfileDataRequestListener(String callbackId) {
				super(callbackId);
			}
			
			@Override
			public JSONObject getResultObject(NeatoWebserviceResult result)
					throws JSONException {
				JSONObject object = null;
				if (result != null && result instanceof SetRobotProfileDetailsResult3) {
					object = new JSONObject();
					SetRobotProfileDetailsResult3 profileResult = (SetRobotProfileDetailsResult3) result;
					object.put(JsonMapKeys.KEY_EXPECTED_TIME_TO_EXECUTE, profileResult.extra_params.expected_time);
				}
				return object;
			}
		}
		
		private class ScheduleRequestListenerWrapper implements ScheduleRequestListener {
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
						success(pluginResult, mCallbackId);
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
				success(pluginResult, mCallbackId);
			}
		}
		
		private class RobotPushNotificationListener implements PushNotificationListener {
			private String mCallbackId;
			
			public RobotPushNotificationListener(String callbackId) {
				mCallbackId = callbackId;
			}

			@Override
			public void onShowPushNotification(Bundle bundle) {
				LogHelper.log(TAG, "onShowPushNotification: " + bundle);
				JSONObject jsonObject = convertBundleToJsonObject(bundle);
				PluginResult pluginPushNotificationResult = new PluginResult(PluginResult.Status.OK, jsonObject);
				pluginPushNotificationResult.setKeepCallback(true);
				success(pluginPushNotificationResult, mCallbackId);
			}
		}
		
		
		private static final JSONObject convertBundleToJsonObject(Bundle bundle) {
			JSONObject jsonObject = new JSONObject();
			// bundle.
			
			if (bundle != null) {
				String notificationId = bundle.getString(PushNotificationConstants.NOTIFICATION_ID_KEY);
				String message = bundle.getString(PushNotificationConstants.NOTIFICATION_MESSAGE_KEY);
				String robotId = bundle.getString(PushNotificationConstants.ROBOT_ID_KEY);
				AppUtils.addToJsonObjectIfNotEmpty(jsonObject, PushNotificationConstants.NOTIFICATION_ID_KEY, notificationId);
				AppUtils.addToJsonObjectIfNotEmpty(jsonObject, PushNotificationConstants.NOTIFICATION_MESSAGE_KEY, message);
				AppUtils.addToJsonObjectIfNotEmpty(jsonObject, PushNotificationConstants.ROBOT_ID_KEY, robotId);
			}
			
			return jsonObject;
		}
		
		private static final class NoActionWebServiceRequestListener implements WebServiceBaseRequestListener {

			@Override
			public void onReceived(NeatoWebserviceResult responseResult) {
				LogHelper.log(TAG, "NoActionWebServiceRequestListener: onReceive responseResult = " + responseResult);
			}

			@Override
			public void onNetworkError(String errMessage) {
				LogHelper.log(TAG, "NoActionWebServiceRequestListener: onNetworkError errMessage = " + errMessage);
			}


			@Override
			public void onServerError(int errorType, String errMessage) {
				LogHelper.log(TAG, "NoActionWebServiceRequestListener: onServerError errMessage = " + errMessage);				
			}
			
		}
}

