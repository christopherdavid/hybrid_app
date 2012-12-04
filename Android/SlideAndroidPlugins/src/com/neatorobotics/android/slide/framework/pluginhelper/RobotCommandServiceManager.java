package com.neatorobotics.android.slide.framework.pluginhelper;
//package com.neato.smartapps.pluginhelper;
//
//import android.content.Context;
//import android.os.RemoteException;
//
//import com.neato.smartapps.ApplicationConfig;
//import com.neato.smartapps.logger.LogHelper;
//import com.neato.smartapps.robot.commands.RobotDiscoveryListener;
//import com.neato.smartapps.service.INeatoRobotService;
//
//public class RobotCommandServiceManager {
//	private static final String TAG = RobotCommandServiceManager.class.getSimpleName();
//	
//	public static void sendCommand(Context context, int commandId, boolean useXmpp,
//			String callbackId) {
//		LogHelper.logD(TAG, "Send command action initiated in Robot plugin");
//		
//		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
//		if (neatoService != null) {
//			try {
//				neatoService.sendCommand(" ", commandId, useXmpp);
//			} catch (RemoteException e) {
//				LogHelper.logD(TAG, "Could not initiate sendCommand action");
//			}
//		}
//
//	}
//
//	public static void associateRobot(Context context, String email, String serialId,
//			String callbackId) {
//
//		LogHelper.logD(TAG, "Assocaite action initiated in Robot plugin");
//
//		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
//		
//		if (neatoService != null) {
//			try {
//				neatoService.associateRobot(serialId, email);
//			} catch (RemoteException e) {
//				LogHelper.logD(TAG, "Could not initiate associateRobot action");
//			}
//		}
//
//	}
//
//	public static void discoverRobot(Context context, 
//			String callbackId, RobotDiscoveryListener listener) {
//		LogHelper.logD(TAG, "Discovery action initiated in Robot plugin");
//		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
//		ApplicationConfig.getInstance(context).getRobotResultReceiver().addDiscoveryListener(listener);
//		if (neatoService != null) {
//			try {
//				neatoService.startDiscovery();
//			} catch (RemoteException e) {
//				LogHelper.logD(TAG, "Could not initiate discover action");
//			}
//		}
//	}
//
//}
