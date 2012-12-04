package com.neatorobotics.android.slide.framework.service;

import android.content.Context;
import android.os.RemoteException;

import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotAssociateListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;

public class RobotCommandServiceManager {
	private static final String TAG = RobotCommandServiceManager.class.getSimpleName();
	
	public static void sendCommand(Context context, int commandId, boolean useXmpp) {
		LogHelper.logD(TAG, "Send command action initiated in Robot plugin internal");
		
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		if (neatoService != null) {
			try {
				neatoService.sendCommand(" ", commandId, useXmpp);
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Could not initiate sendCommand action ");
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");
			
		}

	}

	public static void associateRobot(Context context, String email, String serialId, RobotAssociateListener robotAssociateListener) {

		LogHelper.logD(TAG, "Associate action initiated in Robot plugin internal");

		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		ApplicationConfig.getInstance(context).getRobotResultReceiver().addRobotAssociationListener(robotAssociateListener);

		if (neatoService != null) {
			try {
				neatoService.associateRobot(serialId, email);
			} catch (RemoteException e) {
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

	public static void formPeerConnection(Context context, String IpAddress,RobotPeerConnectionListener listener) {
		LogHelper.logD(TAG, "Form peer connection action initiated");
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		ApplicationConfig.getInstance(context).getRobotResultReceiver().addPeerConnectionListener(listener);
		if (neatoService != null) {
			try {
				
				LogHelper.logD(TAG, "Service exists. Start peer connection: "+IpAddress);
				
				neatoService.connectToRobot(IpAddress);
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Could not initiate peer conneciton action");
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");
			
		}

	}

	public static void sendToBase(Context context) {
		
	}
}
