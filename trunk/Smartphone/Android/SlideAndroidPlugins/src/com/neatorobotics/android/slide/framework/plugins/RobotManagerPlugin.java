package com.neatorobotics.android.slide.framework.plugins;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;
import android.os.Handler;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotAssociateListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.service.RobotCommandServiceManager;


public class RobotManagerPlugin extends Plugin{
	private static final String TAG = RobotManagerPlugin.class.getSimpleName();
	private Context mContext;
	enum Methods { DISCOVER, ASSOCIATE, START_CLEANING, STOP_CLEANING, ROBOTCOMMAND};
	private RobotPluginDiscoveryListener mRobotPluginDiscoveryListener;
	private RobotPluginAssociateListener mRobotPluginAssociateListener;
	Handler mHandler;
	@Override
	public PluginResult execute(String action, JSONArray data, String callBackid) {

		LogHelper.logD(TAG, "RobotManagerPlugin execute with action :" + action);   
		UserJsonData jsonData = new UserJsonData(data);
		mContext = cordova.getActivity();
		String mCallbackId = callBackid;


		switch(determineAction(action)) {

		case DISCOVER:
			LogHelper.log(TAG, "DISCOVER action initiated");
			PluginResult mpluginDiscoverResult = new PluginResult(PluginResult.Status.NO_RESULT);
			mpluginDiscoverResult.setKeepCallback(true);
			discoverRobot(mContext, jsonData , mCallbackId);
			return mpluginDiscoverResult;

		case ASSOCIATE:
			LogHelper.log(TAG, "ASSOCIATE action initiated");
			PluginResult mpluginAssocaiteResult = new PluginResult(PluginResult.Status.NO_RESULT);
			mpluginAssocaiteResult.setKeepCallback(true);
			associateRobot(mContext, jsonData, mCallbackId);
			return mpluginAssocaiteResult;
			
		case START_CLEANING:
			LogHelper.log(TAG, "Start cleaning command initiated");
			startCleaning(mContext, jsonData, mCallbackId);
			PluginResult mpluginstartResult = new PluginResult(PluginResult.Status.NO_RESULT);
			mpluginstartResult.setKeepCallback(true);
			return mpluginstartResult;

		case STOP_CLEANING:
			LogHelper.log(TAG, "Stop cleaning command initiated");
			stopCleaning(mContext, jsonData, mCallbackId);
			PluginResult mpluginStopResult = new PluginResult(PluginResult.Status.NO_RESULT);
			mpluginStopResult.setKeepCallback(true);
			return mpluginStopResult;

		case ROBOTCOMMAND:
			LogHelper.log(TAG, "ROBOTCOMMAND action initiated");
			PluginResult mpluginRobotCommandResult = new PluginResult(PluginResult.Status.NO_RESULT);
			mpluginRobotCommandResult.setKeepCallback(false);
			sendCommand(mContext, jsonData, mCallbackId);
			return mpluginRobotCommandResult;

		default:
			return new PluginResult(PluginResult.Status.INVALID_ACTION);
		}
	}




	//TODO : Write listener
	private void sendCommand(Context context, UserJsonData jsonData,
			String callbackId) {
		LogHelper.logD(TAG, "Send command action initiated in Robot plugin");
		int mCommandId = jsonData.getInt(JsonMapKeys.KEY_COMMAND);
		boolean mUseXmpp = jsonData.getBoolean(JsonMapKeys.KEY_ROBOT_SERIAL_ID);
		RobotCommandServiceManager.sendCommand(context, mCommandId, mUseXmpp);
	} 

	//TODO : Write listener
	private void associateRobot(Context context, UserJsonData jsonData,
			String callbackId) {

		String mEmail = NeatoPrefs.getUserEmailId(mContext);

		String mSerialId = jsonData.getString(JsonMapKeys.KEY_ROBOT_SERIAL_ID);
		LogHelper.logD(TAG, "Associate action initiated in Robot plugin for serial Id: "+mSerialId+ " and email id: "+mEmail);

		mRobotPluginAssociateListener = new RobotPluginAssociateListener(callbackId);
		RobotCommandServiceManager.associateRobot(context, mEmail, mSerialId, mRobotPluginAssociateListener);
	}

	private void discoverRobot(Context context, UserJsonData jsonData,
			String callbackId) {
		LogHelper.logD(TAG, "Discovery action initiated in Robot plugin");
		mRobotPluginDiscoveryListener = new RobotPluginDiscoveryListener(callbackId);
		RobotCommandServiceManager.discoverRobot(context, mRobotPluginDiscoveryListener);
	}
	
	private void startCleaning(Context context, UserJsonData jsonData,
			String callbackId) {
		String serialId = jsonData.getString(JsonMapKeys.KEY_ROBOT_SERIAL_ID);
		boolean useXmppServer = jsonData.getBoolean(JsonMapKeys.KEY_USE_XMPP); 
		LogHelper.logD(TAG, "Start action - Robot plugin for serial Id: "+serialId+ ". Use server: " + useXmppServer);
		RobotCommandServiceManager.sendCommand(mContext, RobotCommandPacketConstants.COMMAND_ROBOT_START, useXmppServer);
		
		//TODO: Once we introduce replies from the robot as ACK to commands we will have to put
		// listeners to send SUCCESS or ERROR plugin result. right now assuming that 
		// its always success.

		PluginResult mpluginStartResult = new PluginResult(PluginResult.Status.OK);
		mpluginStartResult.setKeepCallback(false);
		success(mpluginStartResult,callbackId);

	}
	
	private void stopCleaning(Context context, UserJsonData jsonData,
			String callbackId) {
		String serialId = jsonData.getString(JsonMapKeys.KEY_ROBOT_SERIAL_ID);
		boolean useXmppServer = jsonData.getBoolean(JsonMapKeys.KEY_USE_XMPP); 
		LogHelper.logD(TAG, "Stop action - Robot plugin for serial Id: "+serialId+ ". Use server: " + useXmppServer);
		RobotCommandServiceManager.sendCommand(mContext, RobotCommandPacketConstants.COMMAND_ROBOT_STOP, useXmppServer);
	
		//TODO: Once we introduce replies from the robot as ACK to commands we will have to put
		// listeners to send SUCCESS or ERROR plugin result. right now assuming that 
		// its always success.
		
		PluginResult mpluginStopResult = new PluginResult(PluginResult.Status.OK);
		mpluginStopResult.setKeepCallback(false);
		success(mpluginStopResult,callbackId);


	}

	private Methods determineAction(String action){
		if(action.equals(ActionTypes.DISCOVER))
			return Methods.DISCOVER;
		if(action.equals(ActionTypes.ASSOCIATE))
			return Methods.ASSOCIATE;
		if(action.equals(ActionTypes.ROBOTCOMMAND))
			return Methods.ROBOTCOMMAND;
		if(action.equals(ActionTypes.START_CLEANING_COMMAND))
			return Methods.START_CLEANING;
		if(action.equals(ActionTypes.STOP_CLEANING_COMMAND))
			return Methods.STOP_CLEANING;
		return null;
	}


	class ActionTypes {
		public static final String DISCOVER = "discover";
		public static final String ASSOCIATE = "associate";
		public static final String ROBOTCOMMAND = "robotCommand";
		public static final String START_CLEANING_COMMAND = "startCleaning";
		public static final String STOP_CLEANING_COMMAND = "stopCleaning";
	}

	class ERROR_TYPES {
		public static final int ERROR_INVALID_ARGUMENT=1;
		public static final int ERROR_INVALID_ACTION=2;

	}

	class RobotPluginDiscoveryListener implements RobotDiscoveryListener {

		String mCallBackId;
		RobotPluginDiscoveryListener(String callBackId) {
			mCallBackId = callBackId;
		}
		@Override
		public void onNewRobotFound(RobotInfo robotInfo) {
			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.OK, "New robot found: " +robotInfo.getRobotName());
			loginUserPluginResult.setKeepCallback(true);
			success(loginUserPluginResult, mCallBackId);

		}
		@Override
		public void onDiscoveryStarted() {
			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.OK, "Discovery started");
			loginUserPluginResult.setKeepCallback(true);
			success(loginUserPluginResult, mCallBackId);

		}

		@Override
		public void onDiscoveryFinished() {
			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.OK, "Discovery ended");
			loginUserPluginResult.setKeepCallback(false);
			success(loginUserPluginResult, mCallBackId);

		}
		@Override
		public void discoveryError() {
			PluginResult loginUserPluginResult = new  PluginResult(PluginResult.Status.ERROR);
			error(loginUserPluginResult, mCallBackId);

		}

	}

	class RobotPluginAssociateListener implements RobotAssociateListener {

		String mCallBackId;
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
}
