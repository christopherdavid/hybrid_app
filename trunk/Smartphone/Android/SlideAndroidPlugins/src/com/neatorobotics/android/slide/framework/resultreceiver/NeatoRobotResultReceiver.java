package com.neatorobotics.android.slide.framework.resultreceiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDataListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppsEventConstants;

public class NeatoRobotResultReceiver extends ResultReceiver {
    private static final String TAG = NeatoRobotResultReceiver.class.getSimpleName();

    private RobotPeerConnectionListener mRobotPeerConnectionListener;
    private RobotDataListener mRobotDataListener;

    public NeatoRobotResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(final int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        LogHelper.log(TAG, "Result Code = " + resultCode);
        LogHelper.log(TAG, "Bundle = " + resultData);

        switch (resultCode) {

            case NeatoSmartAppsEventConstants.ROBOT_CONNECTED:
                if (mRobotPeerConnectionListener != null) {
                    String robotId = resultData.getString(RobotCommandPacketConstants.KEY_ROBOT_ID);
                    mRobotPeerConnectionListener.onRobotConnected(robotId);
                }
                break;

            case NeatoSmartAppsEventConstants.ROBOT_DISCONNECTED:
                if (mRobotPeerConnectionListener != null) {
                    String robotId = resultData.getString(RobotCommandPacketConstants.KEY_ROBOT_ID);
                    mRobotPeerConnectionListener.onRobotDisconnected(robotId);
                }
                break;

            case NeatoSmartAppsEventConstants.ROBOT_DATA_RECEIVED:
                break;

            case NeatoSmartAppsEventConstants.ROBOT_CONNECTION_ERROR:
                if (mRobotPeerConnectionListener != null) {
                    String robotId = resultData.getString(RobotCommandPacketConstants.KEY_ROBOT_ID);
                    mRobotPeerConnectionListener.errorInConnecting(robotId);
                }
                break;

            case NeatoSmartAppsEventConstants.ROBOT_DATA:
                LogHelper.logD(TAG, "ROBOT_DATA received");
                if (mRobotDataListener != null) {
                    String robotId = resultData.getString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID);
                    int dataCode = resultData.getInt(NeatoRobotResultReceiverConstants.ROBOT_DATA_KEY_CODE);
                    String data = resultData.getString(NeatoRobotResultReceiverConstants.ROBOT_DATA_KEY);
                    if (!TextUtils.isEmpty(robotId)) {
                        try {
                            mRobotDataListener.onDataReceived(robotId, dataCode, new JSONObject(data));
                        } catch (JSONException e) {
                            LogHelper.log(TAG, "The data sent is not syntaxtically correct :" + data);
                        }
                    }
                } else {
                    LogHelper.log(TAG, "mRobotDataListener is NULL");
                }
                break;
        }
    }

    public void addPeerConnectionListener(RobotPeerConnectionListener robotPeerConnectionListener) {
        mRobotPeerConnectionListener = robotPeerConnectionListener;
    }

    public void addRobotDataListener(RobotDataListener robotDataListener) {
        mRobotDataListener = robotDataListener;
    }
}
