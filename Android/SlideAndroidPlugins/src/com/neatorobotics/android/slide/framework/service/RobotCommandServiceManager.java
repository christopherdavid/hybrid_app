package com.neatorobotics.android.slide.framework.service;

import java.util.HashMap;

import android.content.Context;
import android.os.RemoteException;

import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.resultreceiver.NeatoRobotResultReceiver;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.robot.commands.request.RequestPacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotRequests;

public class RobotCommandServiceManager {
    private static final String TAG = RobotCommandServiceManager.class.getSimpleName();

    public static void sendCommandToPeer(Context context, String robotId, int commandId,
            HashMap<String, String> commandParams) {
        LogHelper.logD(TAG, "sendCommandToPeer called - RobotId = " + robotId);

        if (commandParams == null) {
            commandParams = new HashMap<String, String>();
        }
        LogHelper.log(TAG, "Adding secure pass key for communication");
        commandParams.put(RobotCommandPacketConstants.KEY_SECURE_PASS_KEY, NeatoPrefs.getDriveSecureKey(context));
        
        INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
        RequestPacket request = RequestPacket.createRequestPacket(context, commandId, commandParams);
        LogHelper.log(TAG, "Request Sending : " + request);
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

    public static void sendCommandThroughXmpp(Context context, String robotId, int commandId,
            HashMap<String, String> commandParams) {
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

    public static void tryDirectConnectionWithIp(Context context, String robotId, String ip,
            RobotPeerConnectionListener listener) {
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
                } else {
                    listener.errorInConnecting(robotId);
                }
            } catch (RemoteException e) {
                LogHelper.logD(TAG, "Could not initiate peer conneciton action");
            }
        } else {
            LogHelper.logD(TAG, "Service is not started!");
        }

    }

    // TODO: Need to implement Listener.
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

    public static void initiateXmppConnection(Context context) {
        if (!UserHelper.isUserLoggedIn(context)) {
            LogHelper.logD(TAG, "Not initiating xmpp login, user is not logged in.");
            return;
        }
        INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
        if (neatoService != null) {
            try {
                LogHelper.logD(TAG, "Login to XMPP.");
                neatoService.loginToXmpp();
            } catch (RemoteException e) {
                LogHelper.logD(TAG, "Could not initiate XMPP login conneciton action");
            }
        } else {
            LogHelper.logD(TAG, "Service is not started!");
        }
    }

    public static void loginXmppIfRequired(Context context) {
        if (!UserHelper.isUserLoggedIn(context)) {
            LogHelper.logD(TAG, "Not initiating xmpp login, user is not logged in.");
            return;
        }
        INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
        if (neatoService != null) {
            try {
                LogHelper.logD(TAG, "Service exists. Login to XMPP.");
                neatoService.loginToXmppIfRequired();
            } catch (RemoteException e) {
                LogHelper.logD(TAG, "Could not initiate XMPP login conneciton action");
            }
        } else {
            LogHelper.logD(TAG, "Service is not started!");
        }
    }
    
    public static void logoutXmpp(Context context) {
        INeatoRobotService neatoService = ApplicationConfig.getInstance(context).getRobotService();
        if (neatoService != null) {
            try {
                neatoService.logoutXmpp();
            } catch (RemoteException e) {
                LogHelper.logD(TAG, "Could not logout XMPP");
            }
        } else {
            LogHelper.logD(TAG, "Service is not started!");
        }
    }

    public static void cleanUpServiceConnections(Context context) {
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
