package com.neatorobotics.android.slide.framework.plugins;

import java.util.HashMap;
import java.util.Map;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.database.ScheduleHelper;
import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.json.JsonHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.pluginhelper.ScheduleJsonDataHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ScheduleJsonDataHelper2;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotNotificationsListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotStateListener;
import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedScheduleGroup;
import com.neatorobotics.android.slide.framework.robot.schedule2.Schedule2;
import com.neatorobotics.android.slide.framework.robot.schedule2.ScheduleGroup2;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotDetailResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotOnlineStatusResult;
import com.neatorobotics.android.slide.framework.robot.settings.CleaningSettings;
import com.neatorobotics.android.slide.framework.robot.settings.SettingsManager;
import com.neatorobotics.android.slide.framework.robot.settings.CleaningSettingsListener;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.RobotAtlasWebservicesManager;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.RobotAtlasGridWebservicesManager;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.listeners.RobotGridDataDownloadListener;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.listeners.AddUpdateRobotAtlasListener;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.listeners.RobotAtlasDataDownloadListener;
import com.neatorobotics.android.slide.framework.webservice.robot.map.RobotMapDataDownloadListener;
import com.neatorobotics.android.slide.framework.webservice.robot.map.RobotMapWebservicesManager;
import com.neatorobotics.android.slide.framework.webservice.robot.map.UpdateRobotMapListener;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.DeleteScheduleListener;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.GetScheduleEventData;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.GetScheduleEventsListener;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.GetScheduleListener;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.GetScheduleListener2;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.RobotSchedulerManager;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.RobotSchedulerManager2;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.ScheduleEventListener;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.ScheduleWebserviceListener;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;


public class RobotManagerPlugin extends Plugin {

	private static final String TAG = RobotManagerPlugin.class.getSimpleName();

	private static final String FILE_PREFIX = "file://";

	private static final HashMap<String, RobotManagerPluginMethods> ACTION_MAP = new HashMap<String, RobotManagerPluginMethods>();

	// If we add more action type, please ensure to add it into the ACTION_MAP
	private enum RobotManagerPluginMethods {DISCOVER_NEAR_BY_ROBOTS, TRY_DIRECT_CONNECTION, 
		SEND_COMMAND_TO_ROBOT, SET_ROBOT_SCHEDULE, 
		SET_MAP_OVERLAY_DATA, GET_ROBOT_MAP, 
		GET_ROBOT_SCHEDULE, DISCONNECT_DIRECT_CONNECTION, SET_ROBOT_NAME, GET_ROBOT_ATLAS_METADATA,
		UPDATE_ROBOT_ATLAS_METADATA, GET_ATLAS_GRID_DATA, DELETE_ROBOT_SCHEDULE, GET_ROBOT_DETAIL, 
		SET_ROBOT_NAME_2, TRY_DIRECT_CONNECTION2, SEND_COMMAND_TO_ROBOT2, ROBOT_ONLINE_STATUS,
		REGISTER_ROBOT_NOTIFICATIONS, UNREGISTER_ROBOT_NOTIFICATIONS,
		GET_SCHEDULE_DATA, GET_SCHEDULE_EVENTS, ADD_ROBOT_SCHEDULE_EVENT,
		GET_SCHEDULE_EVENT_DATA, UPDATE_ROBOT_SCHEDULE_EVENT, DELETE_ROBOT_SCHEDULE_EVENT, 
		UPDATE_SCHEDULE, CREATE_SCHEDULE, SYNC_SCHEDULE_FROM_SERVER, IS_SCHEDULE_ENABLED, 
		ENABLE_SCHEDULE, SET_SPOT_DEFINITION,
		GET_SPOT_DEFINITION, START_CLEANING, STOP_CLEANING, PAUSE_CLEANING, RESUME_CLEANING,
		DRIVE_ROBOT, TURN_VACUUM_ON_OFF, TURN_WIFI_ON_OFF};

		static {
			ACTION_MAP.put(ActionTypes.DISCOVER_NEAR_BY_ROBOTS, RobotManagerPluginMethods.DISCOVER_NEAR_BY_ROBOTS);
			ACTION_MAP.put(ActionTypes.TRY_DIRECT_CONNECTION, RobotManagerPluginMethods.TRY_DIRECT_CONNECTION);
			ACTION_MAP.put(ActionTypes.TRY_DIRECT_CONNECTION2, RobotManagerPluginMethods.TRY_DIRECT_CONNECTION2);
			ACTION_MAP.put(ActionTypes.SEND_COMMAND_TO_ROBOT, RobotManagerPluginMethods.SEND_COMMAND_TO_ROBOT);
			ACTION_MAP.put(ActionTypes.SEND_COMMAND_TO_ROBOT2, RobotManagerPluginMethods.SEND_COMMAND_TO_ROBOT2);
			ACTION_MAP.put(ActionTypes.SET_ROBOT_SCHEDULE, RobotManagerPluginMethods.SET_ROBOT_SCHEDULE);
			ACTION_MAP.put(ActionTypes.SET_MAP_OVERLAY_DATA, RobotManagerPluginMethods.SET_MAP_OVERLAY_DATA);
			ACTION_MAP.put(ActionTypes.GET_ROBOT_MAP, RobotManagerPluginMethods.GET_ROBOT_MAP);
			ACTION_MAP.put(ActionTypes.GET_ROBOT_SCHEDULE, RobotManagerPluginMethods.GET_ROBOT_SCHEDULE);
			ACTION_MAP.put(ActionTypes.DISCONNECT_DIRECT_CONNECTION, RobotManagerPluginMethods.DISCONNECT_DIRECT_CONNECTION);
			ACTION_MAP.put(ActionTypes.GET_ROBOT_ATLAS_METADATA, RobotManagerPluginMethods.GET_ROBOT_ATLAS_METADATA);
			ACTION_MAP.put(ActionTypes.UPDATE_ROBOT_ATLAS_METADATA, RobotManagerPluginMethods.UPDATE_ROBOT_ATLAS_METADATA);
			ACTION_MAP.put(ActionTypes.GET_ATLAS_GRID_DATA, RobotManagerPluginMethods.GET_ATLAS_GRID_DATA);
			ACTION_MAP.put(ActionTypes.SET_ROBOT_NAME, RobotManagerPluginMethods.SET_ROBOT_NAME);
			ACTION_MAP.put(ActionTypes.DELETE_ROBOT_SCHEDULE, RobotManagerPluginMethods.DELETE_ROBOT_SCHEDULE);
			ACTION_MAP.put(ActionTypes.GET_ROBOT_DETAIL, RobotManagerPluginMethods.GET_ROBOT_DETAIL);
			ACTION_MAP.put(ActionTypes.SET_ROBOT_NAME_2, RobotManagerPluginMethods.SET_ROBOT_NAME_2);
			ACTION_MAP.put(ActionTypes.GET_ROBOT_ONLINE_STATUS, RobotManagerPluginMethods.ROBOT_ONLINE_STATUS);
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
		}

		private RobotPluginDiscoveryListener mRobotPluginDiscoveryListener;
		private RobotNotificationsPluginListener mRobotNotificationsPluginListener;		
		private RobotStateNotificationPluginListener mRobotStateNotificationPluginListener;

		@Override
		public PluginResult execute(final String action, final JSONArray data, final String callbackId) {

			LogHelper.logD(TAG, "RobotManagerPlugin execute with action :" + action);
			LogHelper.logD(TAG, "\tdata :" + data);

			handlePluginExecute(action, data, callbackId);
			PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(true);
			return pluginResult;
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
			case TRY_DIRECT_CONNECTION:	
				LogHelper.log(TAG, "Form Peer Connection action initiated");
				tryDirectConnection(context, jsonData , callbackId);
				break;
			case TRY_DIRECT_CONNECTION2:	
				LogHelper.log(TAG, "try connection 2 called");
				tryDirectConnection2(context, jsonData , callbackId);
				break;
			case SEND_COMMAND_TO_ROBOT:
				LogHelper.log(TAG, "ROBOTCOMMAND action initiated");
				sendCommand(context, jsonData, callbackId);
				// No need to wait for other callbacks
				break;
			case SEND_COMMAND_TO_ROBOT2:
				LogHelper.log(TAG, "ROBOTCOMMAND action initiated");
				sendCommand2(context, jsonData, callbackId);
				// No need to wait for other callbacks
				break;
			case SET_ROBOT_SCHEDULE:
				LogHelper.log(TAG, "ADVANCED_SCHEDULE_ROBOT action initiated");
				setAdvancedSchedule(context, jsonData, callbackId);
				// TODO: handle with a listener
				break;
			case SET_MAP_OVERLAY_DATA:
				LogHelper.log(TAG, "SET_MAP_OVERLAY_DATA action initiated");
				// TODO: handle with a listener
				setMapOverlayData(context, jsonData, callbackId);
				break;
			case GET_ROBOT_MAP:
				LogHelper.log(TAG, "GET_ROBOT_MAP action initiated");
				// TODO: handle with a listener
				getRobotMaps(context, jsonData, callbackId);
				break;
			case GET_ROBOT_SCHEDULE:
				LogHelper.log(TAG, "GET_ROBOT_SCHEDULE action initiated");
				// TODO: handle with a listener
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
			case GET_ROBOT_ATLAS_METADATA:
				LogHelper.log(TAG, "GET_ROBOT_ATLAS_METADATA action initiated");
				getRobotAtlasMetadata(context, jsonData, callbackId);
				break;
			case UPDATE_ROBOT_ATLAS_METADATA:
				LogHelper.log(TAG, "UPDATE_ROBOT_ATLAS_METADATA action initiated");
				updateAtlasMetaData(context, jsonData, callbackId);
				break;
			case GET_ATLAS_GRID_DATA:
				LogHelper.log(TAG, "GET_ATLAS_GRID_DATA action initiated");
				getAtlasGridData(context, jsonData, callbackId);
				break;
			case GET_ROBOT_DETAIL:
				LogHelper.log(TAG, "GET_ROBOT_DETAIL action initiated");
				getRobotDetail(context, jsonData, callbackId);
				break;
				
			case ROBOT_ONLINE_STATUS:
				LogHelper.log(TAG, "ROBOT_ONLINE_STATUS action initiated");
				getRobotOnlineStatus(context, jsonData, callbackId);
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
			}
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
				if (!TextUtils.isEmpty(commadParamsMap.get(JsonMapKeys.KEY_SPOT_CLEANING_CATEGORY))) {
					cleaningCategory = Integer.valueOf(commadParamsMap.get(JsonMapKeys.KEY_SPOT_CLEANING_CATEGORY));
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
		// TODO: We directly send this information to the robot via XMPP. We may want to send
		// this information to the robot via server
		// There is no API for get the vacuum state as of now
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
		// TODO: We directly send this information to the robot via XMPP. We may want to send
		// this information to the robot via server
		private void turnWiFiOnOff(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "turnWiFiOnOff called");
			
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			JSONObject commandParams = jsonData.getJsonObject(JsonMapKeys.KEY_COMMAND_PARAMETERS);
			HashMap<String, String> commadParamsMap = getCommandParams(commandParams);
			
			sendCommandHelper(context, robotId, RobotCommandPacketConstants.COMMAND_TURN_WIFI_ONOFF, 
					commadParamsMap, callbackId);
		}
		
		private void updateSchedule(Context context, RobotJsonData jsonData,
				final String callbackId) {
			final String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			RobotSchedulerManager2.getInstance(context).updateSchedule(scheduleId, new ScheduleWebserviceListener() {

				@Override
				public void onSuccess() {
					JSONObject jsonResult = new JSONObject();
					try {
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
					} catch (JSONException e) {
						LogHelper.log(TAG, "Exception in createSchedule", e);
					}

					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonResult);
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);
				}

				@Override
				public void onServerError(String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}
			});
		}

		private void createSchedule(Context context, RobotJsonData jsonData, final String callbackId) {
			
			int scheduleType = jsonData.getInt(JsonMapKeys.KEY_SCHEDULE_TYPE);
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			RobotSchedulerManager2.getInstance(context).createSchedule(robotId, scheduleType, new GetScheduleListener2() {

				@Override
				public void onSuccess(ScheduleGroup2 group, String scheduleId, int scheduleType, 
						String scheduleVersion) {
					JSONObject jsonResult = new JSONObject();
					try {
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
						jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleType);
					} catch (JSONException e) {
						LogHelper.log(TAG, "Exception in createSchedule", e);
					}

					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonResult);
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);
				}

				@Override
				public void onServerError(int errorCode, String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
				}

				@Override
				public void onNetworkError(String errMessage) {

					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);

				}
			});
		}



		private void getScheduleEventData(Context context, RobotJsonData jsonData, final String callbackId) {
			
			String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			String scheduleEventId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_EVENT_ID);
			RobotSchedulerManager2.getInstance(context).getScheduleEventData(scheduleId, scheduleEventId, new GetScheduleEventData() {

				@Override
				public void onSuccess(String scheduleId, String eventId, Schedule2 schedule) {
					JSONObject jsonResult = new JSONObject();
					try {
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_EVENT_ID, eventId);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_EVENT_DATA, schedule.toJsonObject());
					} catch (JSONException e) {
						LogHelper.log(TAG, "Exception in getScheduleEventData", e);
					}

					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonResult);
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);

				}

				@Override
				public void onServerError(int errorCode, String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}
			});
		}

		private void updateScheduleEvent(Context context, RobotJsonData jsonData,
				final String callbackId) {
			final String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			final String scheduleEventId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_EVENT_ID);
			JSONObject event = jsonData.getJsonObject(JsonMapKeys.KEY_SCHEDULE_EVENT_DATA);
			int scheduleType = getScheduleTypeFromId(context, scheduleId);
			Schedule2 scheduleEvent = ScheduleJsonDataHelper2.jsonToSchedule(event, scheduleEventId, scheduleType);
			RobotSchedulerManager2.getInstance(context).updateScheduleEvent(scheduleEvent, scheduleId, new ScheduleEventListener() {

				@Override
				public void onSuccess() {
					JSONObject jsonResult = new JSONObject();
					try {
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_EVENT_ID, scheduleEventId);
					} catch (JSONException e) {
						LogHelper.log(TAG, "Exception in updateScheduleEvent", e);
					}

					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonResult);
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);

				}

				@Override
				public void onServerError(int errorCode, String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}
			});
		}

		private void deleteScheduleEvent(Context context, RobotJsonData jsonData, final String callbackId) {
			
			final String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			final String scheduleEventId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_EVENT_ID);
			RobotSchedulerManager2.getInstance(context).deleteScheduleEvent(scheduleId, scheduleEventId, new ScheduleEventListener() {

				@Override
				public void onSuccess() {
					JSONObject jsonResult = new JSONObject();
					try {
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_EVENT_ID, scheduleEventId);
					} catch (JSONException e) {
						LogHelper.log(TAG, "Exception in deleteScheduleEvent", e);
					}

					// TODO Auto-generated method stub
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonResult);
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);
					LogHelper.log(TAG, "delete event success");
				}

				@Override
				public void onServerError(int errorCode, String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}
			});
		}

		private void addScheduleEvent(Context context, RobotJsonData jsonData,
				final String callbackId) {
			LogHelper.log(TAG, "JSON Received: "+ jsonData.toString());
			final String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			JSONObject event = jsonData.getJsonObject(JsonMapKeys.KEY_SCHEDULE_EVENT_DATA);
			final String eventId = AppUtils.generateNewScheduleEventId();
			int scheduleType = getScheduleTypeFromId(context, scheduleId);
			Schedule2 scheduleEvent = ScheduleJsonDataHelper2.jsonToSchedule(event, eventId, scheduleType);
			RobotSchedulerManager2.getInstance(context).addScheduleEvent(scheduleEvent, scheduleId, new ScheduleEventListener() {
				@Override
				public void onSuccess() {
					JSONObject jsonResult = new JSONObject();
					try {
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_EVENT_ID, eventId);
					} catch (JSONException e) {
						LogHelper.log(TAG, "Exception in updateScheduleEvent", e);
					}

					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonResult);
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);
					LogHelper.log(TAG, "Add event success");
				}

				@Override
				public void onServerError(int errorCode, String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}
			});
		}

		private void getScheduleEvents(Context context, RobotJsonData jsonData,
				final String callbackId) 
		{
			LogHelper.log(TAG, "getScheduleEvents");
			final int scheduleType = jsonData.getInt(JsonMapKeys.KEY_SCHEDULE_TYPE);
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			RobotSchedulerManager2.getInstance(context).getScheduleEvents(robotId, scheduleType, new GetScheduleEventsListener() {

				@Override
				public void onSuccess(String scheduleId, ArrayList<String> events) {
					// TODO Auto-generated method stub
					LogHelper.log(TAG, scheduleId);
					JSONObject jsonResult = new JSONObject();
					try {
						JSONArray eventsArray = DataConversionUtils.toJsonArray(events);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
						jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleType);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_EVENTS_LIST, eventsArray);
					} catch (JSONException e) {
						LogHelper.log(TAG, "Exception in createSchedule", e);
					}

					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonResult);
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);
				}

				@Override
				public void onServerError(int errorCode, String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}
			});
		}
		
		// Newly added methods for new schedule pattern.
		private void getScheduleData(Context context, RobotJsonData jsonData, final String callbackId) {

			String scheduleId = jsonData.getString(JsonMapKeys.KEY_SCHEDULE_ID);
			RobotSchedulerManager2.getInstance(context).getSchedule(scheduleId, new GetScheduleListener2() {

				@Override
				public void onSuccess(ScheduleGroup2 group, String scheduleId, int scheduleType, String scheduleVersion) {
					JSONObject jsonResult = new JSONObject();
					try {
						if (group != null) {
							jsonResult.put(JsonMapKeys.KEY_SCHEDULES, group.toJsonArray());
						}
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleType);
						
					} catch (JSONException e) {
						LogHelper.log(TAG, "Exception in createSchedule", e);
					}

					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonResult);
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);					
				}

				@Override
				public void onServerError(int errorCode, String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}
			});
		}

		private void syncScheduleFromServer(Context context, RobotJsonData jsonData,
				final String callbackId) {
			LogHelper.logD(TAG, "syncScheduleFromServer action initiated in Robot plugin");	
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			RobotSchedulerManager2.getInstance(context).syncSchedulesFromServer(robotId, new GetScheduleListener2() {

				@Override
				public void onSuccess(ScheduleGroup2 group,
						String scheduleId, int scheduleType, String scheduleVersion) {
					JSONObject jsonResult = new JSONObject();
					try {
						if (group != null) {
							jsonResult.put(JsonMapKeys.KEY_SCHEDULES, group.toJsonArray());
						}
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
						jsonResult.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleType);
						
					} catch (JSONException e) {
						LogHelper.log(TAG, "Exception in createSchedule", e);
					}

					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonResult);
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);					
				}

				@Override
				public void onServerError(int errorCode, String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}
			});
		}
		
		// Private helper method to return the schedule enable/disable state.
		// TODO: Use webservice calls for isScheduleEnabled.
		private void isScheduleEnabled(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "isScheduleEnabled action initiated in Robot plugin");
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			int scheduleType = jsonData.getInt(JsonMapKeys.KEY_SCHEDULE_TYPE);
			JSONObject jsonResult = new JSONObject();
			try {
				jsonResult.put(JsonMapKeys.KEY_IS_SCHEDULE_ENABLED, NeatoPrefs.getIsScheduleEnabled(context, scheduleType));
				jsonResult.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleType);
				jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
			} catch (JSONException e) {
				LogHelper.logD(TAG, "Exception in getSpotDefinitionJsonObject", e);
			}
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonResult);
			pluginResult.setKeepCallback(false);
			success(pluginResult, callbackId);
		}
		
		// Private helper method to return the schedule enable/disable state.
		// As of now we are storing the enable/disable status in shared preferences
		// later we need to call the Web API to enable/disable schedule
		// TODO: revisit once web service is implemented
		private void enableSchedule(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "enableSchedule called");
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			int scheduleType = jsonData.getInt(JsonMapKeys.KEY_SCHEDULE_TYPE);
			boolean enableSchedule = jsonData.getBoolean(JsonMapKeys.KEY_ENABLE_SCHEDULE);
			NeatoPrefs.saveIsScheduleEnabled(context, scheduleType, enableSchedule);
			JSONObject jsonResult = new JSONObject();
			try {
				jsonResult.put(JsonMapKeys.KEY_IS_SCHEDULE_ENABLED, NeatoPrefs.getIsScheduleEnabled(context, scheduleType));
				jsonResult.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleType);
				jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
			} catch (JSONException e) {
				LogHelper.logD(TAG, "Exception in getSpotDefinitionJsonObject", e);
			}
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonResult);
			pluginResult.setKeepCallback(false);
			success(pluginResult, callbackId);
			
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
			String robotName = jsonData.getString(JsonMapKeys.KEY_ROBOT_NAME);
			
			RobotManager.getInstance(context).setRobotName(robotId, robotName, new RobotRequestListenerWrapper(callbackId) {
				
				@Override
				public void onReceived(NeatoWebserviceResult responseResult) {					
					RobotItem robotItem = RobotHelper.getRobotItem(context, robotId);
					if (robotItem != null) {
						JSONObject robotJsonObj = getRobotDetailJsonObject(robotItem);
						
						PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, robotJsonObj);	
						pluginResult.setKeepCallback(false);
						success(pluginResult, callbackId);
						
						sendDataChangedCommand(context, robotItem.serial_number, RobotCommandPacketConstants.KEY_ROBOT_DETAILS_CHANGED);
					}
					else {
						sendError(callbackId, ErrorTypes.ERROR_TYPE_UNKNOWN, "Unknown Error");
					}
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
			//TODO	
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			RobotCommandServiceManager.disconnectDirectConnection(context, robotId, new RobotPluginPeerConnectionListener(callbackId));
			PluginResult pluginLogoutResult = new PluginResult(PluginResult.Status.OK);
			pluginLogoutResult.setKeepCallback(false);
			success(pluginLogoutResult, callbackId);
		}


		private void tryDirectConnection(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "Try direct connection action initiated in Robot plugin");
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			RobotCommandServiceManager.tryDirectConnection(context, robotId, new RobotPluginPeerConnectionListener(callbackId));	
		}
		
		private void tryDirectConnection2(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "Try direct connection action initiated in Robot plugin");
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			RobotCommandServiceManager.tryDirectConnection2(context, robotId, new RobotPluginPeerConnectionListener(callbackId));	
		}


		private void setAdvancedSchedule(final Context context, RobotJsonData jsonData, final String callbackId) {

			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			JSONArray scheduleArray= jsonData.getJsonArray("schedule");
			AdvancedScheduleGroup schedules = ScheduleJsonDataHelper.jsonToScheduleGroup(scheduleArray);
			RobotSchedulerManager schedulerManager = RobotSchedulerManager.getInstance(context);
			schedulerManager.sendRobotSchedule(schedules, robotId, new ScheduleWebserviceListener() {
				@Override
				public void onSuccess() {
					LogHelper.log(TAG, "ScheduleDetailsPluginListener onSuccess Callback Id = " + callbackId);
					PluginResult scheduleRobotPluginResult = new  PluginResult(PluginResult.Status.OK);
					success(scheduleRobotPluginResult, callbackId);
					sendDataChangedCommand(context, robotId, RobotCommandPacketConstants.KEY_ROBOT_SCHEDULE_CHANGED);
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "ScheduleDetailsPluginListener onNetworkError Callback Id = " + callbackId);
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}

				@Override
				public void onServerError(String errMessage) {
					JSONObject error = getErrorJsonObject(0, "Server Error");
					LogHelper.log(TAG, "ScheduleDetailsPluginListener onServerError Callback Id = " + callbackId);
					PluginResult scheduleRobotPluginResult = new  PluginResult(PluginResult.Status.ERROR, error);
					error(scheduleRobotPluginResult, callbackId);
				}

			});
		}

		private void sendCommand(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "Send command action initiated in Robot plugin");
			int commandId = jsonData.getInt(JsonMapKeys.KEY_COMMAND_ID);
			String robot_id = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);

			@SuppressWarnings("unused")
			JSONObject commandParams = jsonData.getJsonObject(JsonMapKeys.KEY_COMMAND_PARAMETERS);

			RobotCommandServiceManager.sendCommand(context, robot_id, commandId);
			//TODO: Success should be send in a thread way.
			PluginResult pluginStartResult = new PluginResult(PluginResult.Status.OK);
			pluginStartResult.setKeepCallback(false);
			success(pluginStartResult, callbackId);
		} 
		
		
		private void sendCommandHelper(Context context, String robotId, int commandId, HashMap<String, String> params, String callbackId) {
			// Create Robot state notification listener to notify when we get a robot state info
			// from the robot
			if (commandId == RobotCommandPacketConstants.COMMAND_GET_ROBOT_STATE) {
				if (mRobotStateNotificationPluginListener == null) {
					mRobotStateNotificationPluginListener = new RobotStateNotificationPluginListener();
					RobotCommandServiceManager.registerRobotStateNotificationListener(context, mRobotStateNotificationPluginListener);
				}
				
				mRobotStateNotificationPluginListener.addCallbackId(robotId, callbackId);
			}
			RobotCommandServiceManager.sendCommand2(context, robotId, commandId, params);
			
			if (commandId == RobotCommandPacketConstants.COMMAND_ENABLE_SCHEDULE) {
				saveEnableDisableScheduleState(context, params);
			}
			
			if (commandId != RobotCommandPacketConstants.COMMAND_GET_ROBOT_STATE) {
				PluginResult pluginStartResult = new PluginResult(PluginResult.Status.OK);
				pluginStartResult.setKeepCallback(false);
				success(pluginStartResult,callbackId);
			} 
		}
		
		// Private helper method to save the schedule enable/disable information
		// TODO: We need to eventually use the web service to enable/disable schedule
		private void saveEnableDisableScheduleState(Context context, HashMap<String, String> params) {
			String scheduleType = params.get(JsonMapKeys.KEY_SCHEDULE_TYPE);
			String enableSchedule = params.get(JsonMapKeys.KEY_ENABLE_SCHEDULE);
			if (scheduleType == null || enableSchedule == null) {
				return;
			}
			
			int type = Integer.valueOf(scheduleType);
			boolean enable = Boolean.valueOf(enableSchedule);
			NeatoPrefs.saveIsScheduleEnabled(context, type, enable);
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
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			
			RobotSchedulerManager.getInstance(context).getRobotSchedule(robotId, new GetScheduleListener() {

				
				@Override
				public void onSuccess(AdvancedScheduleGroup schedules, String scheduleId) {
					JSONObject schedule = new JSONObject();
					try {
						LogHelper.log(TAG, "Schedule retrieved. Sending PluginResult");
						schedule.put(JsonMapKeys.KEY_SCHEDULE_ID, scheduleId);
						// If there is no schedule set on the Robot then "schedules" may be null
						if (schedules != null) {
							schedule.put(JsonMapKeys.KEY_SCHEDULES, schedules.toJsonArray());
						}
						else {
							JSONArray emptySchedule = new JSONArray();
							schedule.put(JsonMapKeys.KEY_SCHEDULES, emptySchedule);
						}
					} catch (JSONException e) {
						LogHelper.log(TAG, "Exception in GetScheduleListener", e);
					}

					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, schedule);
					pluginResult.setKeepCallback(false);
					success(pluginResult,callbackId);
				}

				@Override
				public void onServerError(String errMessage) {
					LogHelper.log(TAG, "Schedule not retrieved. Sending Error PluginResult");
					PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR);
					pluginResult.setKeepCallback(false);
					error(pluginResult, callbackId);
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "Schedule not retrieved. Sending Error PluginResult");
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
					
				}
			});
		}
		
		private void deleteRobotSchedule(final Context context, final RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "Delete robot schedule action initiated in Robot plugin");
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			
			RobotSchedulerManager.getInstance(context).deleteRobotSchedule(robotId, new DeleteScheduleListener() {
				
				@Override
				public void onSuccess(String robotId) {					
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
					pluginResult.setKeepCallback(false);
					success(pluginResult,callbackId);
				}
				
				@Override
				public void onServerError(String errMessage) {
					LogHelper.log(TAG, "DeleteScheduleListener:onServerError - " + errMessage);
					sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "DeleteScheduleListener:onNetworkError - " + errMessage);
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
					
				}
			});
		}

		private void getRobotMaps(final Context context, final RobotJsonData jsonData, final String callbackId) {

			LogHelper.logD(TAG, "Get robot map action initiated in Robot plugin");

			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);

			RobotMapWebservicesManager.getInstance(context).getRobotMapData(robotId, new RobotMapDataDownloadListener() {

				@Override
				public void onMapDataDownloaded(String robotId, String mapId,
						String mapOverlay, String mapImage) {
					JSONArray robotMaps = new JSONArray();
					JSONObject jGetRobotMapNotification = new JSONObject();
					
					try {

						JSONObject overlayData = JsonHelper.createJsonFromFile(mapOverlay);
						if (overlayData != null) {
							jGetRobotMapNotification.put(JsonMapKeys.KEY_ROBOT_MAP_ID, mapId);
							jGetRobotMapNotification.put(JsonMapKeys.KEY_MAP_OVERLAY_INFO, overlayData);
							jGetRobotMapNotification.put(JsonMapKeys.KEY_MAP_IMAGE, FILE_PREFIX + mapImage);
							robotMaps.put(jGetRobotMapNotification);
							PluginResult getRobotMapPluginResult = new  PluginResult(PluginResult.Status.OK, robotMaps);
							success(getRobotMapPluginResult, callbackId);
						} else {
							JSONObject jsonError = getErrorJsonObject(ErrorTypes.ERROR_TYPE_UNKNOWN, "Invalid JSON file");
							PluginResult getRobotMapPluginResult = new  PluginResult(PluginResult.Status.ERROR, jsonError);
							error(getRobotMapPluginResult, callbackId);
						}

					}
					catch (JSONException e) {
						LogHelper.log(TAG, "Exception in getMapDataSuccess", e);
					}

				}

				@Override
				public void onMapDataDownloadError(String robotId, String mapId,
						String errMessage) {
					JSONObject jsonError = getErrorJsonObject(ErrorTypes.ERROR_TYPE_UNKNOWN, errMessage);
					PluginResult getRobotMapPluginResult = new  PluginResult(PluginResult.Status.ERROR, jsonError);
					error(getRobotMapPluginResult, callbackId);

				}
			});
		}


		private void setMapOverlayData(final Context context, final RobotJsonData robotParams, final String callbackId) {
			LogHelper.logD(TAG, "setMapOverlayData action initiated in Robot plugin");
			LogHelper.logD(TAG, "Input Params = " + robotParams);
			final String robotId = robotParams.getString(JsonMapKeys.KEY_ROBOT_ID);
			JSONObject overlayData = robotParams.getJsonObject(JsonMapKeys.KEY_MAP_OVERLAY_INFO);
			String overlayDataStr = overlayData.toString();	
			RobotMapWebservicesManager.getInstance(context).setRobotOverlayData(robotId, overlayDataStr, new UpdateRobotMapListener() {

				@Override
				public void onSuccess(String robot_map_id, String map_overlay_version, String map_blob_version) {
					LogHelper.logD(TAG, "Updated map successfully");
					// TODO: send the versions and ids to the upper layer.
					PluginResult getRobotMapPluginResult = new  PluginResult(PluginResult.Status.OK, "");
					success(getRobotMapPluginResult, callbackId);
					sendDataChangedCommand(context, robotId, RobotCommandPacketConstants.KEY_ROBOT_MAP_CHANGED);
				}

				@Override
				public void onServerError(String errMessage) {
					JSONObject jsonError = getErrorJsonObject(ErrorTypes.ERROR_TYPE_UNKNOWN, errMessage);
					PluginResult getRobotMapPluginResult = new  PluginResult(PluginResult.Status.ERROR, jsonError);
					error(getRobotMapPluginResult, callbackId);
				}

				@Override
				public void onNetworkError(String errMessage) {
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}
			});

		}

		private void updateAtlasMetaData(final Context context, final RobotJsonData robotParams, final String callbackId) {
			LogHelper.logD(TAG, "setAtlasMetaData action initiated in Robot plugin");
			LogHelper.logD(TAG, "Input Params = " + robotParams);
			final String robotId = robotParams.getString(JsonMapKeys.KEY_ROBOT_ID);
			//TODO: we calculate the atlasversion inside for now. Later we will store it in the Database.
			String atlasVersion = "";
			JSONObject atlasMetaData = robotParams.getJsonObject(JsonMapKeys.KEY_ALTAS_METADATA);
			String overlayDataStr = atlasMetaData.toString();	
			RobotAtlasWebservicesManager.getInstance(context).updateRobotAtlasData(robotId, atlasVersion, overlayDataStr, new AddUpdateRobotAtlasListener() {

				@Override
				public void onSuccess(String robot_atlas_id, String atlas_version) {
					LogHelper.logD(TAG, "atlas data uploaded successfully");
					// TODO: send the versions and ids to the upper layer.
					PluginResult getRobotMapPluginResult = new  PluginResult(PluginResult.Status.OK);
					success(getRobotMapPluginResult, callbackId);
					sendDataChangedCommand(context, robotId, RobotCommandPacketConstants.KEY_ROBOT_ATLAS_CHANGED);
				}

				@Override
				public void onServerError(String errMessage) {
					JSONObject jsonError = getErrorJsonObject(ErrorTypes.ERROR_SERVER_ERROR, errMessage);
					PluginResult getRobotMapPluginResult = new  PluginResult(PluginResult.Status.ERROR, jsonError);
					error(getRobotMapPluginResult, callbackId);
				}

				@Override
				public void onNetworkError(String errMessage) {
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
					
				}
			});

		}
		
		private void getRobotAtlasMetadata(final Context context, final RobotJsonData jsonData, final String callbackId) {

			LogHelper.logD(TAG, "getRobotAtlasMetadata action initiated in Robot plugin");

			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);

			RobotAtlasWebservicesManager.getInstance(context).getRobotAtlasData(robotId, new RobotAtlasDataDownloadListener() {

				@Override
				public void onAtlasDataDownloaded(String atlasId,
						String atlasFileUrl) {
					JSONArray robotMaps = new JSONArray();
					JSONObject jGetRobotAtlasNotification = new JSONObject();
					try {
						JSONObject overlayData = JsonHelper.createJsonFromFile(atlasFileUrl);
						if (overlayData != null) {
							jGetRobotAtlasNotification.put(JsonMapKeys.KEY_ATLAS_ID, atlasId);
							jGetRobotAtlasNotification.put(JsonMapKeys.KEY_ALTAS_METADATA, overlayData);
							robotMaps.put(jGetRobotAtlasNotification);
							PluginResult getRobotMapPluginResult = new  PluginResult(PluginResult.Status.OK, robotMaps);
							success(getRobotMapPluginResult, callbackId);
						} else {
							JSONObject jsonError = getErrorJsonObject(ErrorTypes.ERROR_TYPE_UNKNOWN, "Invalid JSON file");
							PluginResult getRobotAtlasPluginResult = new  PluginResult(PluginResult.Status.ERROR, jsonError);
							error(getRobotAtlasPluginResult, callbackId);
						}

					}
					catch (JSONException e) {
						LogHelper.log(TAG, "Exception in getMapDataSuccess", e);
					}

				}

				@Override
				public void onAtlasDataDownloadError(String mapId,
						String errMessage) {
					JSONObject jsonError = getErrorJsonObject(ErrorTypes.ERROR_TYPE_UNKNOWN, errMessage);
					PluginResult getRobotAtlasPluginResult = new  PluginResult(PluginResult.Status.ERROR, jsonError);
					error(getRobotAtlasPluginResult, callbackId);

				}
			});
		}
		

		//TODO: We are taking robotId. Analyse if taking atlas_id is a better option.
		private void getAtlasGridData(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "getAtlasGridData action initiated in Robot plugin");
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			String gridId = jsonData.getString(JsonMapKeys.KEY_ATLAS_GRID_ID);
			
			RobotAtlasGridWebservicesManager.getInstance(context).getAtlasGridData(robotId, gridId, new RobotGridDataDownloadListener() {
				
				@Override
				public void onGridDataDownloaded(String atlasId, String gridId, String fileUrl) {
					LogHelper.logD(TAG, "getAtlasGridData - onGridDataDownloaded - " + fileUrl);
					JSONObject jGetAtlasGridNotification = new JSONObject();
					JSONArray robotGrids = new JSONArray();
					if (fileUrl != null) {
						try {
							jGetAtlasGridNotification.put(JsonMapKeys.KEY_ATLAS_ID, atlasId);
							jGetAtlasGridNotification.put(JsonMapKeys.KEY_ATLAS_GRID_ID, gridId);
							jGetAtlasGridNotification.put(JsonMapKeys.KEY_ATLAS_GRID_DATA, FILE_PREFIX + fileUrl);						} 
						catch (JSONException e) {
							LogHelper.log(TAG, "Exception in getAtlasGridData", e);
						}
						robotGrids.put(jGetAtlasGridNotification);
						PluginResult getAtlasGridPluginResult = new  PluginResult(PluginResult.Status.OK, robotGrids);						
						success(getAtlasGridPluginResult, callbackId);						
					} else {
						JSONObject jsonError = getErrorJsonObject(ErrorTypes.ERROR_TYPE_UNKNOWN, "Invalid JSON file");
						PluginResult getAtlasGridPluginResult = new  PluginResult(PluginResult.Status.ERROR, jsonError);
						error(getAtlasGridPluginResult, callbackId);
					}
				}
				
				@Override
				public void onGridDataDownloadError(String atlasId, String gridId, String errMessage) {
					LogHelper.logD(TAG, "getAtlasGridData - onGridDataDownloadError - " + errMessage);
					JSONObject jsonError = getErrorJsonObject(ErrorTypes.ERROR_SERVER_ERROR, errMessage);
					PluginResult getRobotGridPluginResult = new  PluginResult(PluginResult.Status.ERROR, jsonError);
					error(getRobotGridPluginResult, callbackId);
				}
			});
		}

		private RobotManagerPluginMethods convertToInternalAction(String action) {
			LogHelper.logD(TAG, "convertToInternalAction - action = " + action);
			RobotManagerPluginMethods robotManagerPluginMethod = ACTION_MAP.get(action);
			return robotManagerPluginMethod;
		}


		private static class ActionTypes {
			public static final String DISCOVER_NEAR_BY_ROBOTS = "discoverNearByRobots";
			public static final String TRY_DIRECT_CONNECTION = "tryDirectConnection";
			public static final String TRY_DIRECT_CONNECTION2 = "tryDirectConnection2";
			public static final String SEND_COMMAND_TO_ROBOT = "sendCommandToRobot";
			public static final String SEND_COMMAND_TO_ROBOT2 = "sendCommandToRobot2";
			public static final String SET_ROBOT_SCHEDULE = "robotSetSchedule";
			public static final String SET_MAP_OVERLAY_DATA= "setMapOverlayData";
			public static final String GET_ROBOT_MAP = "getRobotMap";
			public static final String GET_ROBOT_SCHEDULE = "getSchedule";
			public static final String DISCONNECT_DIRECT_CONNECTION = "disconnectDirectConnection";
			public static final String GET_ROBOT_ATLAS_METADATA = "getRobotAtlasMetadata";
			public static final String UPDATE_ROBOT_ATLAS_METADATA = "updateRobotAtlasMetadata";
			public static final String GET_ATLAS_GRID_DATA = "getAtlasGridData";
			public static final String SET_ROBOT_NAME = "setRobotName";
			public static final String DELETE_ROBOT_SCHEDULE = "deleteScheduleData";
			public static final String GET_ROBOT_DETAIL = "getRobotDetail";
			public static final String SET_ROBOT_NAME_2 = "setRobotName2";
			public static final String GET_ROBOT_ONLINE_STATUS = "getRobotOnlineStatus";
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
					//TODO : write a function to form a list of all the robot infos discovered. So that while forming a TCP we would be able to extract the ip address.
					// For now sending the ip address to UI so as to get it back. Very imp to implement.
					//robot.put(JsonMapKeys.KEY_ROBOT_IP_ADDRESS, robotInfo.getRobotIpAddress());
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
			public void onRobotConnected() {
				PluginResult peerConnectionPluginResult = new  PluginResult(PluginResult.Status.OK);
				peerConnectionPluginResult.setKeepCallback(true);
				LogHelper.log(TAG, "Connected to peer");
				success(peerConnectionPluginResult, mCallBackId);
			}

			@Override
			public void onRobotDisconnected() {
				PluginResult peerConnectionPluginResult = new  PluginResult(PluginResult.Status.OK);
				LogHelper.log(TAG, "Disconnected from peer");
				peerConnectionPluginResult.setKeepCallback(false);
				success(peerConnectionPluginResult, mCallBackId);			
			}

			@Override
			public void errorInConnecting() {
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
			public void onServerError(String errorMessage) {
				LogHelper.logD(TAG, "Server Error: " + errorMessage);
				sendError(mCallbackId, ErrorTypes.ERROR_SERVER_ERROR, errorMessage);
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
}

