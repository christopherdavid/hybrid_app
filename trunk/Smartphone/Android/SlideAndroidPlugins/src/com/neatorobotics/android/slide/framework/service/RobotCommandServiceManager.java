package com.neatorobotics.android.slide.framework.service;

import java.util.HashMap;

import android.content.Context;
import android.os.RemoteException;

import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.resultreceiver.NeatoRobotResultReceiver;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotNotificationsListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotStateListener;
import com.neatorobotics.android.slide.framework.robot.commands.request.RequestPacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotRequests;

public class RobotCommandServiceManager {
	private static final String TAG = RobotCommandServiceManager.class.getSimpleName();
	
	public static void sendCommandToPeer(Context context, String robotId, int commandId, HashMap<String, String> commandParams) {
		LogHelper.logD(TAG, "sendCommandToPeer called - RobotId = " + robotId);
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		RequestPacket request = RequestPacket.createRequestPacket(context, commandId, commandParams);
		RobotRequests requests = new RobotRequests();
		requests.addCommand(request);
		
		if (neatoService != null) {
			try {
				neatoService.sendCommand(robotId, requests, RobotPacketConstants.DISTRIBUTION_MODE_TYPE_PEER);
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Could not initiate sendCommandToPeer action ");
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");

		}
	}
	
	public static void sendCommandThroughServer(Context context, String robotId, int commandId, HashMap<String, String> commandParams) {
		LogHelper.logD(TAG, "sendCommandThroughServer called - RobotId = " + robotId);
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		RequestPacket request = RequestPacket.createRequestPacket(context, commandId, commandParams);
		RobotRequests requests = new RobotRequests();
		requests.addCommand(request);
		
		if (neatoService != null) {
			try {
				neatoService.sendCommand(robotId, requests, RobotPacketConstants.DISTRIBUTION_MODE_TYPE_XMPP);
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Could not initiate sendCommandThroughServer action ");
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");

		}
	}
	
	public static void discoverRobot(Context context, RobotDiscoveryListener listener) {
		LogHelper.logD(TAG, "Discovery action initiated internal");
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		NeatoRobotResultReceiver receiver = ApplicationConfig.getInstance(context).getRobotResultReceiver();
		if (receiver != null) {
			receiver.addDiscoveryListener(listener);
		}
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
		LogHelper.logD(TAG, "tryDirectConnection called");
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		NeatoRobotResultReceiver receiver = ApplicationConfig.getInstance(context).getRobotResultReceiver();
		if (receiver != null) {
			receiver.addPeerConnectionListener(listener);
		}
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
	
	public static void tryDirectConnectionWithIp(Context context, String robotId, String ip, RobotPeerConnectionListener listener) {
		LogHelper.logD(TAG, "tryDirectConnectionWithIp called with ip to connect: " + ip);
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		NeatoRobotResultReceiver receiver = ApplicationConfig.getInstance(context).getRobotResultReceiver();
		if (receiver != null) {
			receiver.addPeerConnectionListener(listener);
		}
		if (neatoService != null) {
			try {

				LogHelper.logD(TAG, "Service exists. Start peer connection: " + robotId);
				if (ip != null) {
					neatoService.connectToRobot3(robotId, ip);
				}
				else {
					neatoService.connectToRobot2(robotId);
				}
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
		NeatoRobotResultReceiver receiver = ApplicationConfig.getInstance(context).getRobotResultReceiver();
		
		if (receiver != null) {
			receiver.addPeerConnectionListener(listener);
		}
		
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
	
	// TODO: Need to implement  Listener.
	public static void disconnectDirectConnection(Context context, String robotId) {
		LogHelper.logD(TAG, "disconnect peer connection action initiated");
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
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
	
	public static boolean isRobotDirectConnected(Context context, String robotId) {
		LogHelper.logD(TAG, "isRobotDirectConnected action initiated");
		boolean isConnected = false;
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		if (neatoService != null) {
			try {
				LogHelper.logD(TAG, "Service exists. isRobotDirectConnected: " + robotId);
				isConnected = neatoService.isRobotDirectConnected(robotId);
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Remote exception in isRobotDirectConnected", e);
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");
		}
		return isConnected;
	}

	public static boolean isDirectConnectionExists(Context context) {
		LogHelper.logD(TAG, "isDirectConnectionExists action initiated");
		boolean isConnected = false;
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		if (neatoService != null) {
			try {
				LogHelper.logD(TAG, "Service exists. isDirectConnectionExists");
				isConnected = neatoService.isAnyPeerConnectionExists();
			} catch (RemoteException e) {
				LogHelper.logD(TAG, "Remote exception in isDirectConnectionExists");
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");
		}
		return isConnected;
	}
	
	public static void loginToXmpp(Context context) {
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
	
	public static void registerRobotStateNotificationListener(Context context, RobotStateListener listener) {
		LogHelper.logD(TAG, "registerRobotStateNotificationListener called");		
		NeatoRobotResultReceiver receiver = ApplicationConfig.getInstance(context).getRobotResultReceiver();
		if (receiver != null) {
			receiver.addRobotStateNotificationListener(listener);
		}
	}
	
	public static void registerRobotNotificationsListener(Context context, String robotId, RobotNotificationsListener listener) {
		LogHelper.logD(TAG, "registerRobotNotificationsListener called");		
		NeatoRobotResultReceiver receiver = ApplicationConfig.getInstance(context).getRobotResultReceiver();
		if (receiver != null) {
			receiver.addRobotNotificationsListener(listener);		
		}
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		if (neatoService != null) {
			try {
				neatoService.registerRobotNotifications(robotId);
			} catch (RemoteException ex) {
				LogHelper.logD(TAG, "RemoteException in registerRobotNotificationsListener", ex);
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");
		}
	}
	
	public static void unregisteredRobotNotificationsListener(Context context, String robotId) {
		LogHelper.logD(TAG, "unregisteredRobotNotificationsListener called");
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		if (neatoService != null) {
			try {
				neatoService.unregisterRobotNotifications(robotId);
			} catch (RemoteException ex) {
				LogHelper.logD(TAG, "RemoteException in unregisterFromRobotStatusChangeNotifications", ex);
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");
		}
	}
	
	public static void cleanUp(Context context) {
		LogHelper.logD(TAG, "cleanUp called");
		INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
		if (neatoService != null) {
			try {
				neatoService.cleanup();
			} catch (RemoteException ex) {
				LogHelper.logD(TAG, "RemoteException in cleanUp", ex);
			}
		} else {
			LogHelper.logD(TAG, "Service is not started!");
		}
	}
}
