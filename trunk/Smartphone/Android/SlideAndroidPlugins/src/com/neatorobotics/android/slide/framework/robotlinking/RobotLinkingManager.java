package com.neatorobotics.android.slide.framework.robotlinking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationConstants;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationUtil;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RequestPacket;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotLinkInitiationResult;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebservicesHelper;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;

public class RobotLinkingManager {
	
	private static final String TAG = RobotLinkingManager.class.getSimpleName();
	private static final long DEFAULT_LINK_CODE_EXPIRY_IN_MS = 1000 * 60 * 5; // 5 minutes
	
	private static final long LINK_TIMER_EXPIRY_BUFFER_TIME  = (5 * 1000); // 5 seconds
	
	private static ArrayList<String> pendingLinkingRequests = new ArrayList<String>();
	
	private static long getLinkCodeExpiryTime(long serverTimerExpiry) {
		if (serverTimerExpiry > 0) {
			return ((serverTimerExpiry * 1000) + LINK_TIMER_EXPIRY_BUFFER_TIME); // server time is in seconds
		}
		return DEFAULT_LINK_CODE_EXPIRY_IN_MS;
	}
	
	public static void initiateLinkRobot(final Context context, final String linkCode, final String emailId, final WebServiceBaseRequestListener listener) {		
		Runnable task = new Runnable() {
			public void run() {
				try {
					RobotLinkInitiationResult initiateLink = NeatoUserWebservicesHelper.initiateLinkToRobot(context, emailId, linkCode);				
					listener.onReceived(initiateLink);
					trackLinkCode(linkCode);
					long expiryTime = initiateLink.result.expiry_time;
					String robotId = initiateLink.result.serial_number;
					
					long linkCodeExpiryTime = getLinkCodeExpiryTime(expiryTime);
					startLinkTimer(context, linkCode, robotId, linkCodeExpiryTime);
				}
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ErrorTypes.ERROR_TYPE_USER_UNAUTHORIZED, ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getStatusCode(), ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				}	
			}

		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public static void startLinkTimer(final Context context, final String linkCode, final String robotId, long expiryTimeInMs) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				if (isLinkingProcessPending(linkCode)) {
					untrackLinkCode(linkCode);
					notifyLinkingRejected(context, robotId, linkCode);
				}
			}
		};
		TaskUtils.scheduleTask(task, expiryTimeInMs);
	}
	
	public static void notifyLinkingData(Context context, RequestPacket request) {
		int commandId = request.getCommand();
		String robotId = request.getCommandParam(RobotCommandPacketConstants.KEY_ROBOT_ID);
		switch (commandId) {
		case RobotCommandPacketConstants.COMMAND_ROBOT_LINKING_SUCCESS:
			String successLinkCode = request.getCommandParam(RobotCommandPacketConstants.KEY_LINK_CODE);
			notifyLinkingSuccessful(context, robotId, successLinkCode);
			break;
		case RobotCommandPacketConstants.COMMAND_ROBOT_LINKING_REJECTED:
			String rejectedLinkCode = request.getCommandParam(RobotCommandPacketConstants.KEY_LINK_CODE);
			notifyLinkingRejected(context, robotId, rejectedLinkCode);
			break;
		case RobotCommandPacketConstants.COMMAND_ROBOT_NEW_LINK_FORMED:
			String emailId = request.getCommandParam(RobotCommandPacketConstants.KEY_EMAIL_ID);
			notifyNewUserLinked(context, robotId, emailId);
			break;
		default:
			break;
		}
	}
	
	private static void notifyLinkingSuccessful(Context context, String robotId, String successLinkCode) {
		LogHelper.log(TAG, "Linking successsful for robot: " + robotId);
		HashMap<String, String> data = new HashMap<String, String>();
		untrackLinkCode(successLinkCode);
		RobotNotificationUtil.notifyDataChanged(context, robotId, RobotNotificationConstants.ROBOT_LINKING_SUCCESS, data);
	}

	private static void untrackLinkCode(String successLinkCode) {
		pendingLinkingRequests.remove(successLinkCode);
	}
	
	private static void trackLinkCode(final String linkCode) {
		pendingLinkingRequests.add(linkCode);
	}
	
	private static void notifyLinkingRejected(Context context, String robotId, String linkCode) {
		LogHelper.log(TAG, "Linking rejected by robot: Link Code " + linkCode);
		HashMap<String, String> data = new HashMap<String, String>();
		data.put(JsonMapKeys.KEY_LINKING_CODE, linkCode);
		untrackLinkCode(linkCode);
		RobotNotificationUtil.notifyDataChanged(context, robotId, RobotNotificationConstants.ROBOT_LINKING_FAILURE, data);
	}
	
	private static void notifyNewUserLinked(Context context, String robotId, String emailId) {
		LogHelper.log(TAG, "New user linked to robot: " + robotId);
		HashMap<String, String> data = new HashMap<String, String>();
		RobotNotificationUtil.notifyDataChanged(context, robotId, RobotNotificationConstants.ROBOT_NEW_LINKING_FORMED, data);
	}
	
	private static boolean isLinkingProcessPending(String linkCode) {
		return pendingLinkingRequests.contains(linkCode);
	}
}
