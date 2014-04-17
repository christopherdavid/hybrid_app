package com.neatorobotics.android.slide.framework.xmpp;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotCommandPacket;

public class XMPPNotificationListener {

    private static final String TAG = XMPPNotificationListener.class.getSimpleName();

    public void onConnectSucceeded() {
        LogHelper.log(TAG, "XMPP Connection Succeeded");
    }

    public void onLoginFailed() {
        LogHelper.log(TAG, "XMPP Login failed");
    }

    public void onLoginSucceeded() {
        LogHelper.log(TAG, "XMPP Login succeeded");
    }

    public void onConnectionReset() {
        LogHelper.log(TAG, "XMPP Connection reset");
    }

    public void onDisconnect() {
        LogHelper.log(TAG, "XMPP disconnected");
    }

    public void onDataReceived(String from, RobotCommandPacket packet) {
        LogHelper.log(TAG, "onDataReceived ");
    }

}
