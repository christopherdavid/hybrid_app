package com.neatorobotics.android.slide.framework.plugins;

import java.util.HashMap;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import com.neatorobotics.android.slide.framework.json.JsonHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.pluginhelper.ScheduleJsonDataHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedScheduleGroup;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotDetailListener;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotOnlineStatusListener;
import com.neatorobotics.android.slide.framework.webservice.robot.SetRobotProfileDetailsListener;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.RobotAtlasWebservicesManager;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.RobotAtlasGridWebservicesManager;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.grid.listeners.RobotGridDataDownloadListener;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.listeners.AddUpdateRobotAtlasListener;
import com.neatorobotics.android.slide.framework.webservice.robot.atlas.listeners.RobotAtlasDataDownloadListener;
import com.neatorobotics.android.slide.framework.webservice.robot.map.RobotMapDataDownloadListener;
import com.neatorobotics.android.slide.framework.webservice.robot.map.RobotMapWebservicesManager;
import com.neatorobotics.android.slide.framework.webservice.robot.map.UpdateRobotMapListener;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.DeleteScheduleListener;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.GetScheduleListener;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.RobotSchedulerManager;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.ScheduleWebserviceListener;


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
		SET_ROBOT_NAME_2, TRY_DIRECT_CONNECTION2, SEND_COMMAND_TO_ROBOT2, ROBOT_ONLINE_STATUS};

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
		}

		private RobotPluginDiscoveryListener mRobotPluginDiscoveryListener;
		private ScheduleDetailsPluginListener mScheduleDetailsPluginListener;

		@Override
		public PluginResult execute(final String action, final JSONArray data, final String callbackId) {

			LogHelper.logD(TAG, "RobotManagerPlugin execute with action :" + action);

			// Plugin's execute method gets called in secondary thread. So Async tasks will fail
			// For now we run the execute commands in the UI thread and each request does its work in background
			// thread. We need to remove the Async tasks and run these requests in the secondary threads
			Activity currentActivity = cordova.getActivity();
			currentActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					handlePluginExecute(action, data, callbackId);
				}
			});

			PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(true);
			return pluginResult;
		}


		private void handlePluginExecute(String action, JSONArray data, String callbackId) {
			RobotJsonData jsonData = new RobotJsonData(data);
			Context context = cordova.getActivity();

			switch(convertToInternalAction(action)) {

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
			}
		}


		private void setRobotName(Context context, RobotJsonData jsonData,
				final String callbackId) {
			LogHelper.logD(TAG, "setRobotName action initiated in Robot plugin");	
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			String robotName = jsonData.getString(JsonMapKeys.KEY_ROBOT_NAME);
			
			RobotManager.getInstance(context).setRobotName(robotId, robotName, new SetRobotProfileDetailsListener() {
				
				@Override
				public void onComplete(RobotItem item) {
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);
				}

				@Override
				public void onServerError(String errMessage) {
					LogHelper.log(TAG, "Unable to update  robot profile");
					sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "Unable to update  robot profile");
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}
			});
		}
		
		private void setRobotName2(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "setRobotName2 action initiated in Robot plugin");	
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			String robotName = jsonData.getString(JsonMapKeys.KEY_ROBOT_NAME);
			
			RobotManager.getInstance(context).setRobotName(robotId, robotName, new SetRobotProfileDetailsListener() {
				
				@Override
				public void onComplete(RobotItem robotItem) {
					JSONObject robotJsonObj = getRobotDetailJsonObject(robotItem);
					PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, robotJsonObj);	
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);
				}

				@Override
				public void onServerError(String errMessage) {
					LogHelper.log(TAG, "setRobotName2 - onServerError. Error message = " + errMessage);
					JSONObject jsonError = getErrorJsonObject(ErrorTypes.ERROR_SERVER_ERROR, errMessage);
					PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, jsonError);
					pluginResult.setKeepCallback(false);
					error(pluginResult, callbackId);	
					
				}

				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "setRobotName2 - onNetworkError. Error message = " + errMessage);
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}
			});	
		}

		private void getRobotDetail(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "getRobotDetail action initiated in Robot plugin");	
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			
			RobotManager.getInstance(context).getRobotDetail(robotId, new RobotDetailListener() {
				
				@Override
				public void onRobotDetailReceived(RobotItem robotItem) {
					LogHelper.log(TAG, "getRobotDetail - onRobotDetailReceived");
					JSONObject robotJsonObj = getRobotDetailJsonObject(robotItem);
					PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, robotJsonObj);			
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);
				}
				
				@Override
				public void onServerError(String errMessage) {
					LogHelper.log(TAG, "getRobotDetail - onServerError");
					JSONObject jsonError = getErrorJsonObject(ErrorTypes.ERROR_SERVER_ERROR, errMessage);
					PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, jsonError);
					pluginResult.setKeepCallback(false);
					error(pluginResult, callbackId);
				}
				
				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "getRobotDetail - onNetworkError");
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}
			});
		}

		private void getRobotOnlineStatus(Context context, RobotJsonData jsonData, final String callbackId) {
			LogHelper.logD(TAG, "getRobotOnlineStatus action initiated in Robot plugin");	
			final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			
			RobotManager.getInstance(context).getRobotOnlineStatus(robotId, new RobotOnlineStatusListener() {
				@Override
				public void onComplete(boolean isOnline) {
					LogHelper.log(TAG, "getRobotOnlineStatus - onComplete = " + isOnline);
					JSONObject robotOnlineStatusJsonObj = getRobotOnlineStatusJsonObject(robotId, isOnline);
					PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, robotOnlineStatusJsonObj);			
					pluginResult.setKeepCallback(false);
					success(pluginResult, callbackId);
				}
				
				@Override
				public void onServerError(String errMessage) {
					LogHelper.log(TAG, "getRobotOnlineStatus - onServerError = " + errMessage);					
					sendError(callbackId, ErrorTypes.ERROR_SERVER_ERROR, errMessage);
				}
				
				@Override
				public void onNetworkError(String errMessage) {
					LogHelper.log(TAG, "getRobotOnlineStatus - onNetworkError = " + errMessage);
					sendError(callbackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
				}				
			});
		}
		
		private JSONObject getRobotDetailJsonObject(RobotItem robotItem) {
			JSONObject robotJsonObj = new JSONObject();
			try {
				robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, robotItem.getSerialNumber());
				robotJsonObj.put(JsonMapKeys.KEY_ROBOT_NAME, robotItem.getName());				
			}
			catch (JSONException e) {
				LogHelper.logD(TAG, "Exception in getRobotDetailJsonObject", e);
			}
			
			return robotJsonObj;
		}
		
		private JSONObject getRobotOnlineStatusJsonObject(String robotId, boolean online) {
			JSONObject robotJsonObj = new JSONObject();
			try {
				robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
				robotJsonObj.put(JsonMapKeys.KEY_ROBOT_ONLINE_STATUS, online);				
			}
			catch (JSONException e) {
				LogHelper.logD(TAG, "Exception in getRobotOnlineStatusJsonObject", e);
			}
			
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


		private void setAdvancedSchedule(Context context, RobotJsonData jsonData, String callbackId) {

			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			JSONArray scheduleArray= jsonData.getJsonArray("schedule");
			AdvancedScheduleGroup schedules = ScheduleJsonDataHelper.jsonToScheduleGroup(scheduleArray);
			RobotSchedulerManager schedulerManager = RobotSchedulerManager.getInstance(context);
			mScheduleDetailsPluginListener = new ScheduleDetailsPluginListener(callbackId);
			schedulerManager.sendRobotSchedule(schedules, robotId, mScheduleDetailsPluginListener);
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

		private void sendCommand2(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "Send command action initiated in Robot plugin " + jsonData.toString());
			int commandId = jsonData.getInt(JsonMapKeys.KEY_COMMAND_ID);
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			
			JSONObject commandParams = jsonData.getJsonObject(JsonMapKeys.KEY_COMMAND_PARAMETERS);
			HashMap<String, String> commadParamsMap = getCommandParams(commandParams);
			RobotCommandServiceManager.sendCommand2(context, robotId, commandId, commadParamsMap);
			// TODO: Success should be send in a thread way.
			PluginResult pluginStartResult = new PluginResult(PluginResult.Status.OK);
			pluginStartResult.setKeepCallback(false);
			success(pluginStartResult,callbackId);
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


		private void setMapOverlayData(Context context, final RobotJsonData robotParams, final String callbackId) {
			LogHelper.logD(TAG, "setMapOverlayData action initiated in Robot plugin");
			LogHelper.logD(TAG, "Input Params = " + robotParams);
			String robotId = robotParams.getString(JsonMapKeys.KEY_ROBOT_ID);
			JSONObject overlayData = robotParams.getJsonObject(JsonMapKeys.KEY_MAP_OVERLAY_INFO);
			String overlayDataStr = overlayData.toString();	
			RobotMapWebservicesManager.getInstance(context).setRobotOverlayData(robotId, overlayDataStr, new UpdateRobotMapListener() {

				@Override
				public void onSuccess(String robot_map_id, String map_overlay_version, String map_blob_version) {
					LogHelper.logD(TAG, "Updated map successfully");
					// TODO: send the versions and ids to the upper layer.
					PluginResult getRobotMapPluginResult = new  PluginResult(PluginResult.Status.OK, "");
					success(getRobotMapPluginResult, callbackId);
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

		private void updateAtlasMetaData(Context context, final RobotJsonData robotParams, final String callbackId) {
			LogHelper.logD(TAG, "setAtlasMetaData action initiated in Robot plugin");
			LogHelper.logD(TAG, "Input Params = " + robotParams);
			String robotId = robotParams.getString(JsonMapKeys.KEY_ROBOT_ID);
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

		// TODO - a lot clean up of code needed.
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

		private  class ScheduleDetailsPluginListener implements ScheduleWebserviceListener {
			private String mCallBackId;

			ScheduleDetailsPluginListener(String callbackId) {
				mCallBackId = callbackId;
			}

			@Override
			public void onSuccess() {
				LogHelper.log(TAG, "ScheduleDetailsPluginListener onSuccess Callback Id = " + mCallBackId);
				PluginResult scheduleRobotPluginResult = new  PluginResult(PluginResult.Status.OK);
				success(scheduleRobotPluginResult, mCallBackId);
			}

			@Override
			public void onNetworkError(String errMessage) {
				LogHelper.log(TAG, "ScheduleDetailsPluginListener onNetworkError Callback Id = " + mCallBackId);
				sendError(mCallBackId, ErrorTypes.ERROR_NETWORK_ERROR, errMessage);
			}

			@Override
			public void onServerError(String errMessage) {
				JSONObject error = getErrorJsonObject(0, "Server Error");
				LogHelper.log(TAG, "ScheduleDetailsPluginListener onServerError Callback Id = " + mCallBackId);
				PluginResult scheduleRobotPluginResult = new  PluginResult(PluginResult.Status.ERROR, error);
				error(scheduleRobotPluginResult, mCallBackId);
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
		
		
		private HashMap<String, String> getCommandParams(JSONObject jObject) {
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
}

