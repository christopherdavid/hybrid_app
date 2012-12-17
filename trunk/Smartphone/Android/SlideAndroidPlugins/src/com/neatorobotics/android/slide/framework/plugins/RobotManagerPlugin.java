package com.neatorobotics.android.slide.framework.plugins;

import java.lang.ref.WeakReference;
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
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedScheduleGroup;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;
import com.neatorobotics.android.slide.framework.webservice.robot.map.RobotMapDataDownloadListener;
import com.neatorobotics.android.slide.framework.webservice.robot.map.RobotMapWebservicesManager;
import com.neatorobotics.android.slide.framework.webservice.robot.map.UpdateRobotMapListener;
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
		GET_ROBOT_SCHEDULE, DISCONNECT_DIRECT_CONNECTION};

		static {
			ACTION_MAP.put(ActionTypes.DISCOVER_NEAR_BY_ROBOTS, RobotManagerPluginMethods.DISCOVER_NEAR_BY_ROBOTS);
			ACTION_MAP.put(ActionTypes.TRY_DIRECT_CONNECTION, RobotManagerPluginMethods.TRY_DIRECT_CONNECTION);
			ACTION_MAP.put(ActionTypes.SEND_COMMAND_TO_ROBOT, RobotManagerPluginMethods.SEND_COMMAND_TO_ROBOT);
			ACTION_MAP.put(ActionTypes.SET_ROBOT_SCHEDULE, RobotManagerPluginMethods.SET_ROBOT_SCHEDULE);
			ACTION_MAP.put(ActionTypes.SET_MAP_OVERLAY_DATA, RobotManagerPluginMethods.SET_MAP_OVERLAY_DATA);
			ACTION_MAP.put(ActionTypes.GET_ROBOT_MAP, RobotManagerPluginMethods.GET_ROBOT_MAP);
			ACTION_MAP.put(ActionTypes.GET_ROBOT_SCHEDULE, RobotManagerPluginMethods.GET_ROBOT_SCHEDULE);
			ACTION_MAP.put(ActionTypes.DISCONNECT_DIRECT_CONNECTION, RobotManagerPluginMethods.DISCONNECT_DIRECT_CONNECTION);
		}

		/*private RobotPluginAssociateListener mRobotPluginAssociateListener;*/
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
			case SEND_COMMAND_TO_ROBOT:
				LogHelper.log(TAG, "ROBOTCOMMAND action initiated");
				sendCommand(context, jsonData, callbackId);
				// No need to wait for other callbacks
				break;
			case SET_ROBOT_SCHEDULE:
				LogHelper.log(TAG, "ADVANCED_SCHEDULE_ROBOT action initiated");
				setAdvancedSchedule(context, jsonData, callbackId);
				// TODO: handle with a listener
				break;
			case SET_MAP_OVERLAY_DATA:
				LogHelper.log(TAG, "UPDATE_ROBOT_MAP action initiated");
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
			case DISCONNECT_DIRECT_CONNECTION:

				LogHelper.log(TAG, "DISCONNECT_PEER action initiated");
				disconnectPeerConnection(context, jsonData, callbackId);
			}
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


		private void setAdvancedSchedule(Context context, RobotJsonData jsonData, String callbackId) {

			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			JSONArray scheduleArray= jsonData.getJsonArray("schedule");
			AdvancedScheduleGroup schedules = ScheduleJsonDataHelper.jsonToScheduleGroup(scheduleArray);
			RobotSchedulerManager schedulerManager = RobotSchedulerManager.getInstance(context);
			mScheduleDetailsPluginListener = new ScheduleDetailsPluginListener(callbackId);
			schedulerManager.sendRobotSchedule(schedules, robotId, mScheduleDetailsPluginListener);
		}

		// TODO : Write listener
		private void sendCommand(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "Send command action initiated in Robot plugin");
			int commandId = jsonData.getInt(JsonMapKeys.KEY_COMMAND_ID);
			String robot_id = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);

			JSONObject commandParams = jsonData.getJsonObject(JsonMapKeys.KEY_COMMAND_PARAMETERS);

			RobotCommandServiceManager.sendCommand(context, robot_id, commandId);
			//TODO: Success should be send in a thread way.
			PluginResult mpluginStartResult = new PluginResult(PluginResult.Status.OK);
			mpluginStartResult.setKeepCallback(false);
			success(mpluginStartResult,callbackId);
		} 


		private void discoverRobot(Context context, RobotJsonData jsonData, String callbackId) {
			LogHelper.logD(TAG, "Discovery action initiated in Robot plugin");
			mRobotPluginDiscoveryListener  = new RobotPluginDiscoveryListener(callbackId);
			RobotCommandServiceManager.discoverRobot(context, mRobotPluginDiscoveryListener);
		}


		// TODO: re-implement
		private void getRobotSchedule(Context context, RobotJsonData jsonData,
				String callbackId) {
			LogHelper.logD(TAG, "getRobotSchedule action initiated in Robot plugin");
			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
			//JSONObject result = getTempSchedule();
			//mFakeListener = new FakeListener(result, callbackId);
			//fakeNetworkDelay(mFakeListener);
		}

		private void getRobotMaps(final Context context, final RobotJsonData jsonData, final String callbackId) {

			LogHelper.logD(TAG, "Get robot map action initiated in Robot plugin");

			String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);

			RobotMapWebservicesManager.getInstance(context).getRobotMapData(robotId, new RobotMapDataDownloadListener() {

				@Override
				public void onMapDataDownloaded(String robotId, String mapId,
						String mapOverlay, String mapImage) {
					JSONObject jGetRobotMapNotification = new JSONObject();
					// TODO Auto-generated method stub
					try {

						JSONObject overlayData = JsonHelper.createJsonFromFile(mapOverlay);
						if (overlayData != null) {
							jGetRobotMapNotification.put(JsonMapKeys.KEY_ROBOT_MAP_ID, mapId);
							jGetRobotMapNotification.put(JsonMapKeys.KEY_MAP_OVERLAY_INFO, overlayData);
							jGetRobotMapNotification.put(JsonMapKeys.KEY_MAP_IMAGE, FILE_PREFIX + mapImage);
							PluginResult getRobotMapPluginResult = new  PluginResult(PluginResult.Status.OK, jGetRobotMapNotification);
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
				public void onError(String errMessage) {
					JSONObject jsonError = getErrorJsonObject(ErrorTypes.ERROR_TYPE_UNKNOWN, errMessage);
					PluginResult getRobotMapPluginResult = new  PluginResult(PluginResult.Status.ERROR, jsonError);
					error(getRobotMapPluginResult, callbackId);
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
			public static final String SEND_COMMAND_TO_ROBOT = "sendCommandToRobot";
			public static final String SET_ROBOT_SCHEDULE = "robotSetSchedule";
			public static final String SET_MAP_OVERLAY_DATA= "setMapOverlayData";
			public static final String GET_ROBOT_MAP = "getRobotMap";
			public static final String GET_ROBOT_SCHEDULE = "getSchedule";
			public static final String DISCONNECT_DIRECT_CONNECTION = "disconnectDirectConnection";
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
			public void onNetworkError() {
				JSONObject error = getErrorJsonObject(0, "Network Error");
				LogHelper.log(TAG, "ScheduleDetailsPluginListener onNetworkError Callback Id = " + mCallBackId);
				PluginResult scheduleRobotPluginResult = new  PluginResult(PluginResult.Status.ERROR, error);
				error(scheduleRobotPluginResult, mCallBackId);
			}

			@Override
			public void onServerError() {
				JSONObject error = getErrorJsonObject(0, "Server Error");
				LogHelper.log(TAG, "ScheduleDetailsPluginListener onServerError Callback Id = " + mCallBackId);
				PluginResult scheduleRobotPluginResult = new  PluginResult(PluginResult.Status.ERROR, error);
				error(scheduleRobotPluginResult, mCallBackId);
			}

		}
}

