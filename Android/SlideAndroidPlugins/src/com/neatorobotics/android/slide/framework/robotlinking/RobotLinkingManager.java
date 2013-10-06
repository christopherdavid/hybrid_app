package com.neatorobotics.android.slide.framework.robotlinking;

import java.io.IOException;
import java.util.HashMap;
import android.content.Context;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationConstants;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotNotificationUtil;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RequestPacket;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebservicesHelper;
import com.neatorobotics.android.slide.framework.webservice.user.RobotLinkResult;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;

public class RobotLinkingManager {
	
	private static final String TAG = RobotLinkingManager.class.getSimpleName();
	
	public static void linkRobot(final Context context, final String linkCode, final String emailId, final WebServiceBaseRequestListener listener) {		
		Runnable task = new Runnable() {
			public void run() {
				try {
					RobotLinkResult initiateLink = NeatoUserWebservicesHelper.initiateLinkToRobot(context, emailId, linkCode);
					String robotId = initiateLink.result.serial_number;
					RobotManager.getInstance(context).getRobotDetailAndSave(robotId);
					listener.onReceived(initiateLink);
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
	
	public static void notifyLinkingData(Context context, RequestPacket request) {
		int commandId = request.getCommand();
		String robotId = request.getCommandParam(RobotCommandPacketConstants.KEY_ROBOT_ID);
		switch (commandId) {
		case RobotCommandPacketConstants.COMMAND_ROBOT_LINKING_SUCCESS:
			String successLinkCode = request.getCommandParam(RobotCommandPacketConstants.KEY_LINK_CODE);
			notifyLinkingSuccessful(context, robotId, successLinkCode);
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
		RobotNotificationUtil.notifyDataChanged(context, robotId, RobotNotificationConstants.ROBOT_LINKING_SUCCESS, data);
	}
	
	private static void notifyNewUserLinked(Context context, String robotId, String emailId) {
		LogHelper.log(TAG, "New user linked to robot: " + robotId);
		HashMap<String, String> data = new HashMap<String, String>();
		RobotNotificationUtil.notifyDataChanged(context, robotId, RobotNotificationConstants.ROBOT_NEW_LINKING_FORMED, data);
	}
}
