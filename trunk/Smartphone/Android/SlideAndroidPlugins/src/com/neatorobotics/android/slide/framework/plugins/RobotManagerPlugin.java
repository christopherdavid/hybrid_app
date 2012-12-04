package com.neatorobotics.android.slide.framework.plugins;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotAssociateListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.robot.schedule.AdvancedRobotSchedule;
import com.neatorobotics.android.slide.framework.robot.schedule.ScheduleTimeObject;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants.Day;
import com.neatorobotics.android.slide.framework.robot.schedule.SchedulerConstants.SchedularEvent;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.RobotSchedulerManager;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.ScheduleWebserviceListener;


public class RobotManagerPlugin extends Plugin {
	
	private static final String TAG = RobotManagerPlugin.class.getSimpleName();
	
	
	private static final HashMap<String, RobotManagerPluginMethods> ACTION_MAP = new HashMap<String, RobotManagerPluginMethods>();

	// If we add more action type, please ensure to add it into the ACTION_MAP
	private enum RobotManagerPluginMethods { DISCOVER, FORM_PEER_CONNECTION, ASSOCIATE, START_CLEANING, STOP_CLEANING, ROBOTCOMMAND, ADVANCED_SCHEDULE_ROBOT, SEND_BASE_COMMAND};
	
	static {
		ACTION_MAP.put(ActionTypes.DISCOVER, RobotManagerPluginMethods.DISCOVER);
		ACTION_MAP.put(ActionTypes.FORM_PEER_CONNECTION, RobotManagerPluginMethods.FORM_PEER_CONNECTION);
		ACTION_MAP.put(ActionTypes.ASSOCIATE, RobotManagerPluginMethods.ASSOCIATE);
		ACTION_MAP.put(ActionTypes.START_CLEANING_COMMAND, RobotManagerPluginMethods.START_CLEANING);
		ACTION_MAP.put(ActionTypes.STOP_CLEANING_COMMAND, RobotManagerPluginMethods.STOP_CLEANING);
		ACTION_MAP.put(ActionTypes.ROBOTCOMMAND, RobotManagerPluginMethods.ROBOTCOMMAND);
		ACTION_MAP.put(ActionTypes.ADVANCED_SCHEDULE_ROBOT, RobotManagerPluginMethods.ADVANCED_SCHEDULE_ROBOT);
		ACTION_MAP.put(ActionTypes.SEND_BASE_COMMAND, RobotManagerPluginMethods.SEND_BASE_COMMAND);
	}
	
	private RobotPluginAssociateListener mRobotPluginAssociateListener;
	private RobotPluginDiscoveryListener mRobotPluginDiscoveryListener;
	private ScheduleDetailsPluginListener mScheduleDetailsPluginListener;

	@Override
	public PluginResult execute(final String action, final JSONArray data, final String callbackId) {

		LogHelper.logD(TAG, "RobotManagerPlugin execute with action :" + action);
		
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
		UserJsonData jsonData = new UserJsonData(data);
		Context context = cordova.getActivity();

		switch(convertToInternalAction(action)) {

			case DISCOVER:
				LogHelper.log(TAG, "DISCOVER action initiated");
				discoverRobot(context, jsonData , callbackId);
				break;
			case FORM_PEER_CONNECTION:	
				LogHelper.log(TAG, "Form Peer Connection action initiated");
				formPeerConnection(context, jsonData , callbackId);
				break;
			case ASSOCIATE:
				LogHelper.log(TAG, "ASSOCIATE action initiated");
				associateRobot(context, jsonData, callbackId);
				break;
			case START_CLEANING:
				LogHelper.log(TAG, "Start cleaning command initiated");
				startCleaning(context, jsonData, callbackId);
				break;
			case STOP_CLEANING:
				LogHelper.log(TAG, "Stop cleaning command initiated");
				stopCleaning(context, jsonData, callbackId);
				break;
			case ROBOTCOMMAND:
				LogHelper.log(TAG, "ROBOTCOMMAND action initiated");
				sendCommand(context, jsonData, callbackId);
				// No need to wait for other callbacks
				PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
				pluginResult.setKeepCallback(false);
				success(pluginResult, callbackId);
				break;
			case ADVANCED_SCHEDULE_ROBOT:
				LogHelper.log(TAG, "ADVANCED_SCHEDULE_ROBOT action initiated");
				setAdvancedSchedule(context, jsonData, callbackId);
				// TODO: handle with a listener
				PluginResult advancedScheduleResult = new PluginResult(PluginResult.Status.OK);
				advancedScheduleResult.setKeepCallback(false);
				success(advancedScheduleResult, callbackId);
				break;
			case SEND_BASE_COMMAND:
				LogHelper.log(TAG, "SEND base command initiated");
				sendToBase(context, jsonData, callbackId);
				break;

		}
	}

	private void formPeerConnection(Context context, UserJsonData jsonData, String callbackId) {
		LogHelper.logD(TAG, "Form peer connection action initiated in Robot plugin");
		// String mSerialId = jsonData.getString(JsonMapKeys.KEY_ROBOT_SERIAL_ID);
		String ipAddress = jsonData.getString(JsonMapKeys.KEY_ROBOT_IP_ADDRESS);
		RobotCommandServiceManager.formPeerConnection(context, ipAddress, new RobotPluginPeerConnectionListener(callbackId));
	}
	
	private void sendToBase(Context context, UserJsonData jsonData, String callbackId) {
		LogHelper.logD(TAG, "sendToBase action initiated in Robot plugin");
		// String mSerialId = jsonData.getString(JsonMapKeys.KEY_ROBOT_SERIAL_ID);
		// TODO: remove the useXMpp flag.
		RobotCommandServiceManager.sendCommand(context, RobotCommandPacketConstants.COMMAND_SEND_BASE, false);

		//TODO: Once we introduce replies from the robot as ACK to commands we will have to put
		// listeners to send SUCCESS or ERROR plugin result. right now assuming that 
		// its always success.

		PluginResult pluginStartResult = new PluginResult(PluginResult.Status.OK);
		pluginStartResult.setKeepCallback(false);
		success(pluginStartResult,callbackId);

	}
	
	private void setAdvancedSchedule(Context context, UserJsonData jsonData, String callbackId) {
		
		//TODO : put checks to see if the hrs and mins lie between 0-24 and 0-60 respectively.
		//TODO: multiple schedules
		ArrayList<Day> days = new ArrayList<Day>();
		int day = jsonData.getInt(JsonMapKeys.KEY_DAY);
	
		Day dayMap = SchedulerConstants.detrmineDay(day);
		days.add(dayMap);
		String serialId = jsonData.getString(JsonMapKeys.KEY_ROBOT_SERIAL_ID);
		int startHrs = jsonData.getInt(JsonMapKeys.KEY_START_TIME_HRS);
		int startMins = jsonData.getInt(JsonMapKeys.KEY_START_TIME_MINS);
		int endHrs = jsonData.getInt(JsonMapKeys.KEY_END_TIME_HRS);
		int endMins = jsonData.getInt(JsonMapKeys.KEY_END_TIME_MINS);
		int eventType = jsonData.getInt(JsonMapKeys.KEY_EVENT_TYPE);
		SchedularEvent event = SchedulerConstants.detrmineEvent(eventType);
		String area = jsonData.getString(JsonMapKeys.KEY_AREA);
		ScheduleTimeObject startTime = new ScheduleTimeObject(startHrs, startMins);
		ScheduleTimeObject endTime = new ScheduleTimeObject(endHrs, endMins);
		
		AdvancedRobotSchedule schedule = new AdvancedRobotSchedule(days, startTime, endTime, area, event);
		
		RobotSchedulerManager schedulerManager = RobotSchedulerManager.getInstance(context);
		mScheduleDetailsPluginListener = new ScheduleDetailsPluginListener(callbackId);

		schedulerManager.sendRobotSchedule(schedule, serialId, mScheduleDetailsPluginListener);
	}

	//TODO : Write listener
	private void sendCommand(Context context, UserJsonData jsonData, String callbackId) {
		LogHelper.logD(TAG, "Send command action initiated in Robot plugin");
		int commandId = jsonData.getInt(JsonMapKeys.KEY_COMMAND);
		boolean useXmpp = jsonData.getBoolean(JsonMapKeys.KEY_ROBOT_SERIAL_ID);
		RobotCommandServiceManager.sendCommand(context, commandId, useXmpp);
	} 

	//TODO : Write listener
	private void associateRobot(Context context, UserJsonData jsonData, String callbackId) {

		String email = NeatoPrefs.getUserEmailId(context);
		String serialId = jsonData.getString(JsonMapKeys.KEY_ROBOT_SERIAL_ID);
		LogHelper.logD(TAG, "Associate action initiated in Robot plugin for serial Id: " + serialId + " and email id: " + email);
		
		
		mRobotPluginAssociateListener = new RobotPluginAssociateListener(callbackId);
		RobotCommandServiceManager.associateRobot(context, email, serialId, mRobotPluginAssociateListener);
		
	}

	private void discoverRobot(Context context, UserJsonData jsonData, String callbackId) {
		LogHelper.logD(TAG, "Discovery action initiated in Robot plugin");
		mRobotPluginDiscoveryListener  = new RobotPluginDiscoveryListener(callbackId);
		RobotCommandServiceManager.discoverRobot(context, mRobotPluginDiscoveryListener);
	}

	private void startCleaning(Context context, UserJsonData jsonData, String callbackId) {
		String serialId = jsonData.getString(JsonMapKeys.KEY_ROBOT_SERIAL_ID);
		boolean useXmppServer = jsonData.getBoolean(JsonMapKeys.KEY_USE_XMPP); 
		LogHelper.logD(TAG, "Start action - Robot plugin for serial Id: " + serialId + ". Use server: " + useXmppServer);
		RobotCommandServiceManager.sendCommand(context, RobotCommandPacketConstants.COMMAND_ROBOT_START, useXmppServer);

		//TODO: Once we introduce replies from the robot as ACK to commands we will have to put
		// listeners to send SUCCESS or ERROR plugin result. right now assuming that 
		// its always success.

		PluginResult mpluginStartResult = new PluginResult(PluginResult.Status.OK);
		mpluginStartResult.setKeepCallback(false);
		success(mpluginStartResult,callbackId);

	}

	private void stopCleaning(Context context, UserJsonData jsonData, String callbackId) {
		
		String serialId = jsonData.getString(JsonMapKeys.KEY_ROBOT_SERIAL_ID);
		boolean useXmppServer = jsonData.getBoolean(JsonMapKeys.KEY_USE_XMPP); 
		LogHelper.logD(TAG, "Stop action - Robot plugin for serial Id: "+serialId+ ". Use server: " + useXmppServer);
		RobotCommandServiceManager.sendCommand(context, RobotCommandPacketConstants.COMMAND_ROBOT_STOP, useXmppServer);

		//TODO: Once we introduce replies from the robot as ACK to commands we will have to put
		// listeners to send SUCCESS or ERROR plugin result. right now assuming that 
		// its always success.

		PluginResult mpluginStopResult = new PluginResult(PluginResult.Status.OK);
		mpluginStopResult.setKeepCallback(false);
		success(mpluginStopResult,callbackId);
	}

	private RobotManagerPluginMethods convertToInternalAction(String action) {
		
		RobotManagerPluginMethods robotManagerPluginMethod = ACTION_MAP.get(action);
		return robotManagerPluginMethod;
	}


	private static class ActionTypes {
		public static final String DISCOVER = "discover";
		public static final String FORM_PEER_CONNECTION = "connectPeer";
		public static final String ASSOCIATE = "associate";
		public static final String ROBOTCOMMAND = "robotCommand";
		public static final String START_CLEANING_COMMAND = "startCleaning";
		public static final String STOP_CLEANING_COMMAND = "stopCleaning";
		public static final String SEND_BASE_COMMAND = "sendBase";
		public static final String ADVANCED_SCHEDULE_ROBOT = "robotAdvancedSchedule";
	}

	private static class ERROR_TYPES {
		public static final int ERROR_INVALID_ARGUMENT = 1;
		public static final int ERROR_INVALID_ACTION = 2;

	}

	// TODO - a lot clean up of code needed.
	private class RobotPluginDiscoveryListener implements RobotDiscoveryListener {

		private static final String ROBOT_ITEM_KEY = "robot";
		private static final String ROBOTS_LIST_KEY = "robots";
		private static final String DISCOVERY_NOTIFICATION_STATE = "notificationType";
		private static final String DISCOVERY_STARTED = "1";
		private static final String DISCOVERY_FINISHED = "2";
		private String mCallBackId;
		private JSONObject jRobotList = new JSONObject();

		RobotPluginDiscoveryListener(String callBackId) {
			mCallBackId = callBackId;
		}

		@Override
		public void onNewRobotFound(RobotInfo robotInfo) {
			//Instead of sending notifications for every robot found, we would make a list and send it at one shot.
			LogHelper.log(TAG, "Robot Found with name: " + robotInfo.getRobotName());
			addRobotToList(robotInfo);
		}
		
		@Override
		public void onDiscoveryStarted() {
			LogHelper.log(TAG, "Discovery started");

			JSONObject jRobotDiscoveryStartedNotification = new JSONObject();
			
			try {
				jRobotDiscoveryStartedNotification.put(DISCOVERY_NOTIFICATION_STATE, DISCOVERY_STARTED);
			} 
			catch (JSONException e) {
				LogHelper.log(TAG, "Exception in onDiscoveryStarted", e);
			}
			
			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.OK,jRobotDiscoveryStartedNotification.toString());
			loginUserPluginResult.setKeepCallback(true);
			success(loginUserPluginResult, mCallBackId);

		}

		@Override
		public void onDiscoveryFinished() {
			LogHelper.log(TAG, "Discovery Finished");
			JSONObject jRobotListResult = new JSONObject();
			try {
				jRobotListResult.put(DISCOVERY_NOTIFICATION_STATE, DISCOVERY_FINISHED);
				jRobotListResult.put(ROBOTS_LIST_KEY, jRobotList);
			} catch (JSONException e) {
				LogHelper.log(TAG, "Exception in onDiscoveryFinished", e);
			}

			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.OK, jRobotListResult.toString());
			loginUserPluginResult.setKeepCallback(false);
			success(loginUserPluginResult, mCallBackId);

		}
		@Override
		public void discoveryError() {
			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.ERROR);
			error(loginUserPluginResult, mCallBackId);

		}

		public void addRobotToList(RobotInfo robotInfo) {
			try {
				jRobotList.put(ROBOT_ITEM_KEY, robotJsonObject(robotInfo));
			} catch (JSONException e) {
				LogHelper.log(TAG, "Exception in addRobotToList", e);
			}
		}
		
		public JSONObject robotJsonObject(RobotInfo robotInfo) {
			JSONObject robot = new JSONObject();
			try {
				robot.put(JsonMapKeys.KEY_ROBOT_NAME, robotInfo.getRobotName());
				robot.put(JsonMapKeys.KEY_ROBOT_SERIAL_ID, robotInfo.getSerialId());
				//TODO : write a function to form a list of all the robot infos discovered. So that while forming a TCP we would be able to extract the ip address.
				// For now sending the ip address to UI so as to get it back. Very imp to implement.
				robot.put(JsonMapKeys.KEY_ROBOT_IP_ADDRESS, robotInfo.getRobotIpAddress());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return robot;
		}
	}

	private class RobotPluginAssociateListener implements RobotAssociateListener {

		private String mCallBackId;
		
		RobotPluginAssociateListener(String callbackId) {
			mCallBackId = callbackId;
		}
		
		@Override
		public void associationSuccess() {
			PluginResult associateRobotPluginResult = new  PluginResult(PluginResult.Status.OK);
			LogHelper.log(TAG, "Association robot success");
			success(associateRobotPluginResult, mCallBackId);
		}

		@Override
		public void associationError(String errMessage) {
			PluginResult associateRobotPluginResult = new  PluginResult(PluginResult.Status.ERROR, errMessage);
			LogHelper.log(TAG, "Association robot Error: " +errMessage);
			error(associateRobotPluginResult, mCallBackId);
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
		
	} 
	
	private static class ScheduleDetailsPluginListener implements ScheduleWebserviceListener {
		private String mCallBackId;
		
		ScheduleDetailsPluginListener(String callbackId) {
			mCallBackId = callbackId;
		}

		@Override
		public void onSuccess() {
			LogHelper.log(TAG, "ScheduleDetailsPluginListener onSuccess Callback Id = " + mCallBackId);
		}

		@Override
		public void onNetworkError() {
			LogHelper.log(TAG, "ScheduleDetailsPluginListener onNetworkError Callback Id = " + mCallBackId);
		}

		@Override
		public void onServerError() {
			LogHelper.log(TAG, "ScheduleDetailsPluginListener onServerError Callback Id = " + mCallBackId);
		}

	}
}

