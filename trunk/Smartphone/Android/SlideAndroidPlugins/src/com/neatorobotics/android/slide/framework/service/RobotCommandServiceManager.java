package com.neatorobotics.android.slide.framework.service;

import java.util.HashMap;

import android.content.Context;
import android.os.RemoteException;

import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotAssociateListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.robot.commands.request.RequestPacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotRequests;
import com.neatorobotics.android.slide.framework.utils.AppUtils;

public class RobotCommandServiceManager {
	private static final String TAG = RobotCommandServiceManager.class.getSimpleName();
	

	public static void sendCommand(Context context, String robotId, int commandId) {
		LogHelper.logD(TAG, "Send command action initiated in Robot plugin internal - RobotSerialId = " + robotId);

		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		if (neatoService != null) {
			try {
				neatoService.sendCommand(robotId, commandId);
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Could not initiate sendCommand action ");
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");

		}
	}

	
	public static void sendCommand2(Context context, String robotId, int commandId, HashMap<String, String> commandParams) {
		LogHelper.logD(TAG, "Send command action initiated in Robot plugin internal - RobotSerialId = " + robotId);

		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		RequestPacket request = createRequestPacket(context, commandId, commandParams);
		RobotRequests requests = new RobotRequests();
		requests.addCommand(request);
		if (neatoService != null) {
			try {
				neatoService.sendCommand2(robotId, requests);
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Could not initiate sendCommand action ");
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");

		}
	}
	
	private static RequestPacket createRequestPacket(Context context, int commandId, HashMap<String, String> commandParams)
	{
		RequestPacket request = null;
		if (commandParams != null) {
			request = RequestPacket.createRobotCommandWithParams(commandId, commandParams);
		} else {
			request = RequestPacket.createRobotCommand(commandId);
		}
		
		request.setRequestId(AppUtils.generateNewRequestId(context));
		request.setReplyToAddress(AppUtils.getLoggedInUserId(context));
		request.setTimestamp(String.valueOf(System.currentTimeMillis()));
		
		return request;
	}

	public static void associateRobot(Context context, String email, String robotId, RobotAssociateListener robotAssociateListener) {

		LogHelper.logD(TAG, "Associate action initiated in Robot plugin internal");

		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		ApplicationConfig.getInstance(context).getRobotResultReceiver().addRobotAssociationListener(robotAssociateListener);

		if (neatoService != null) {
			try {
				// neatoService.associateRobot(robotId, email);
			} catch (Exception e) {
				LogHelper.logD(TAG, "Could not initiate associateRobot action");
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");
			
		}

	}

	public static void discoverRobot(Context context, RobotDiscoveryListener listener) {
		LogHelper.logD(TAG, "Discovery action initiated internal");
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		ApplicationConfig.getInstance(context).getRobotResultReceiver().addDiscoveryListener(listener);
		if (neatoService != null) {
			try {
				
				LogHelper.logD(TAG, "Service exists. Starting discovery");
				neatoService.startDiscovery();
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Could not initiate discover action");
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");
			
		}
	}

	public static void tryDirectConnection(Context context, String robotId, RobotPeerConnectionListener listener) {
		LogHelper.logD(TAG, "Form peer connection action initiated");
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		//TODO discover ipaddress if not already stored. Right now discovering everytime. Later we can have a map with serial Id.
		ApplicationConfig.getInstance(context).getRobotResultReceiver().addPeerConnectionListener(listener);
		if (neatoService != null) {
			try {

				LogHelper.logD(TAG, "Service exists. Start peer connection: "+robotId);
				neatoService.connectToRobot(robotId);
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Could not initiate peer conneciton action");
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");

		}

	}
	
	public static void tryDirectConnection2(Context context, String robotId, RobotPeerConnectionListener listener) {
		LogHelper.logD(TAG, "tryDirectConnection2 called");
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();

		ApplicationConfig.getInstance(context).getRobotResultReceiver().addPeerConnectionListener(listener);
		if (neatoService != null) {
			try {

				LogHelper.logD(TAG, "Service exists. Start peer connection: " + robotId);
				neatoService.connectToRobot2(robotId);
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Could not initiate peer conneciton action");
			}
		} 
		else {
			LogHelper.logD(TAG, "Service is not started!");
		}

	}

	public static void disconnectDirectConnection(Context context, String robotId,
			RobotPeerConnectionListener listener) {
		LogHelper.logD(TAG, "disconnect peer connection action initiated");
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		ApplicationConfig.getInstance(context).getRobotResultReceiver().addPeerConnectionListener(listener);
		if (neatoService != null) {
			try {
				LogHelper.logD(TAG, "Service exists. close peer connection: " + robotId);
				neatoService.closePeerConnection(robotId);
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Could not disconnect peer conneciton action");
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");
		}
	}

	public static void loginToXmpp(Context context) {
		LogHelper.logD(TAG, "Form peer connection action initiated");
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();

		if (neatoService != null) {
			try {
				LogHelper.logD(TAG, "Service exists. Login to XMPP.");
				neatoService.loginToXmpp();
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Could not initiate XMPP login conneciton action");
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");
			
		}


	}
}
