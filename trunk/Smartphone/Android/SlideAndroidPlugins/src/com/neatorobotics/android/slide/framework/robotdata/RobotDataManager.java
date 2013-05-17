package com.neatorobotics.android.slide.framework.robotdata;

import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.resultreceiver.NeatoRobotResultReceiverConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDataListener;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacketUtils;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotPacketConstants;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppsEventConstants;
import com.neatorobotics.android.slide.framework.timemode.RobotCommandTimerHelper;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebServicesAttributes.SetRobotProfileDetails2.ProfileAttributeKeys;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.NeatoRobotDataWebservicesHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.SetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;

public class RobotDataManager {
	
	public static final String TAG = RobotDataManager.class.getSimpleName();
	private static final String EMPTY_STRING = "";
	
	//Used to send command. This will send the standard command format as value.
	public static void sendRobotCommand (Context context, String robotId, int commandId, HashMap<String, String> commandParams, WebServiceBaseRequestListener listener) {
		LogHelper.logD(TAG, "Send command action initiated sendRobotCommand - RobotSerialId = " + robotId);
		String robotPacketInXmlFormat =  RobotCommandPacketUtils.getRobotCommandPacket(context, commandId, commandParams, RobotPacketConstants.DISTRIBUTION_MODE_TYPE_TIME_MODE_SERVER);
		String keyType = RobotProfileConstants.getProfileKeyType(commandId);
		setRobotProfileParam(context, robotId, keyType, robotPacketInXmlFormat, listener);
	}
		
	private static void setRobotProfileParam (final Context context, final String robotId, final String key,  final String value, final WebServiceBaseRequestListener listener) {
		LogHelper.logD(TAG, "setRobotProfileParam called");
		LogHelper.logD(TAG, "Robot Id = " + robotId + " Key: " + key + "Value: "+ value);
		
		Runnable task = new Runnable() {
			public void run() {
				try {
					HashMap<String, String> profileParams = new HashMap<String, String>();
					profileParams.put(key, value);					
					SetRobotProfileDetailsResult2 result = NeatoRobotDataWebservicesHelper.setRobotProfileDetailsRequest2(context, robotId, profileParams);
					
					RobotCommandTimerHelper.getInstance(context).startCommandExpiryTimer(robotId);
					listener.onReceived(result);
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
	
	// Write logic to update the UI.
	public static void getServerData(final Context context, final String robotId) {
		
		//Disabling as of now. Should be enabled later.
		if (!AppConstants.isServerDataModeEnabled()) {
			return;
		}
		
		Runnable task = new Runnable() {
			public void run() {
				try {
					GetRobotProfileDetailsResult2 details = NeatoRobotDataWebservicesHelper.getRobotProfileDetailsRequest2(context, robotId, EMPTY_STRING);
					LogHelper.logD(TAG, "getServerData, retrieved profileDetails");
					consumeProfileParams(context, robotId, details);
				} catch (UserUnauthorizedException e) {
					LogHelper.log(TAG, "UserUnauthorizedException in getServerData", e);
				} catch (NeatoServerException e) {
					LogHelper.log(TAG, "NeatoServerException in getServerData", e);
				} catch (IOException e) {
					LogHelper.log(TAG, "IOException in getServerData", e);
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	//helper method to consume the profile data parameters.
	//TODO: Add database support to compare timestamps.
	private static void consumeProfileParams(Context context, String robotId, GetRobotProfileDetailsResult2 details) {
		if (details.success()) {
			notifyStateChanges(context, robotId, details);
			//Add other notification when supported.
		}
	}
	
	private static void notifyStateChanges(Context context, String robotId, GetRobotProfileDetailsResult2 details) {
		
		String virtualState = RobotProfileDataUtils.getRobotVirtualState(context, details);
		String currentState = RobotProfileDataUtils.getRobotCurrentState(context, details);
		String actualState = RobotProfileDataUtils.getActualState(virtualState, currentState);
		if (!TextUtils.isEmpty(currentState)) {
			notifyStateChange(context, robotId, currentState);
			LogHelper.log(TAG, "Current state Received from Web server: " + currentState);
		}
		
		if (!TextUtils.isEmpty(actualState)) {
			notifyStateUpdate(context, robotId, actualState);
		}
	}
	
	private static void robotCommandExpiryResetData(final Context context, final String robotId) {
		if (!AppConstants.isServerDataModeEnabled()) {
			return;
		}

		Runnable task = new Runnable() {
			public void run() {
				try {
					//Reset cleaningCommand so that robot does not fetch it. Also to update the UI of other smartapps.
					//TODO: Reset other values too when support added.
					NeatoRobotDataWebservicesHelper.resetRobotProfileValue(context, robotId, ProfileAttributeKeys.ROBOT_CLEANING_COMMAND);
					
					//Get the current profile parameters to reflect in the UI
					GetRobotProfileDetailsResult2  details = NeatoRobotDataWebservicesHelper.getRobotProfileDetailsRequest2(context, robotId, EMPTY_STRING);
					consumeProfileParams(context, robotId, details);
				} catch (UserUnauthorizedException e) {
					LogHelper.log(TAG, "UserUnauthorizedException in getServerData", e);
				} catch (NeatoServerException e) {
					LogHelper.log(TAG, "NeatoServerException in getServerData", e);
				} catch (IOException e) {
					LogHelper.log(TAG, "IOException in getServerData", e);
				}
			}					
		};
		TaskUtils.scheduleTask(task, 0);
	}

	public static void onCommandExpired(final Context context, final String robotId) {
		robotCommandExpiryResetData(context, robotId);
	}
		
	public static void addRobotDataChangedListener(Context context, RobotDataListener listener) {
		if (ApplicationConfig.getInstance(context) != null) {
			ApplicationConfig.getInstance(context).getRobotResultReceiver().addRobotDataListener(listener);
		}
	}
	
	public static void removeRobotDataChangedListener(Context context) {
		if (ApplicationConfig.getInstance(context) != null) {
			ApplicationConfig.getInstance(context).getRobotResultReceiver().addRobotDataListener(null);
		}
	}
	
	private static void notifyStateChange(Context context, String robotId, String currentState) {
		HashMap<String, String> stateData = new HashMap<String, String>();
		stateData.put(JsonMapKeys.KEY_ROBOT_CURRENT_STATE, currentState);
		notifyDataChanged(context, robotId, RobotProfileConstants.ROBOT_CURRENT_STATE_CHANGED, stateData);
	}
	
	private static void notifyStateUpdate(Context context, String robotId, String state) {
		LogHelper.log(TAG, "Robot State Update :" +state);
		HashMap<String, String> stateData = new HashMap<String, String>();
		stateData.put(JsonMapKeys.KEY_ROBOT_STATE_UPDATE, String.valueOf(state));
		notifyDataChanged(context, robotId, RobotProfileConstants.ROBOT_STATE_UPDATE, stateData);
	}
	//Helper method to send notification.
	private static void notifyDataChanged(Context context, String robotId, int keyCode, HashMap<String, String> data) {
		int resultCode = NeatoSmartAppsEventConstants.ROBOT_DATA;
		Bundle dataChanged = new Bundle();
		dataChanged.putString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID, robotId);
		dataChanged.putInt(NeatoRobotResultReceiverConstants.ROBOT_DATA_KEY_CODE, keyCode);
		dataChanged.putSerializable(NeatoRobotResultReceiverConstants.ROBOT_DATA_KEY, data);
		ApplicationConfig.getInstance(context).getRobotResultReceiver().send(resultCode, dataChanged);
	}
}
