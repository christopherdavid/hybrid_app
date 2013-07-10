package com.neatorobotics.android.slide.framework.resultreceiver;

import java.util.HashMap;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDataListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotNotificationsListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPacketListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotStateListener;
import com.neatorobotics.android.slide.framework.robot.commands.request.ResponsePacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotRequests;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppsEventConstants;

public class NeatoRobotResultReceiver extends ResultReceiver
{
	private static final String TAG = NeatoRobotResultReceiver.class.getSimpleName();
	
	private RobotDiscoveryListener mRobotDiscoveryListener;
	private RobotPeerConnectionListener mRobotPeerConnectionListener;
	private RobotPacketListener mRobotResponseListener;
	private RobotNotificationsListener mRobotNotificationsListener;
	private RobotStateListener mRobotStateNotificationListener;
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
			case NeatoSmartAppsEventConstants.NEW_ROBOT_FOUND:
				RobotInfo robotInfo = resultData.getParcelable(NeatoRobotResultReceiverConstants.DISCOVERY_ROBOT_INFO);
				if (mRobotDiscoveryListener != null) {
					mRobotDiscoveryListener.onNewRobotFound(robotInfo);
				}
				break;
			
			case NeatoSmartAppsEventConstants.DISCOVERY_STARTED:
				if (mRobotDiscoveryListener != null) {
					mRobotDiscoveryListener.onDiscoveryStarted();
				}
				break;
			
			case NeatoSmartAppsEventConstants.DISCOVERY_END:
				if (mRobotDiscoveryListener != null) {
					mRobotDiscoveryListener.onDiscoveryFinished();
				}
				break;
		
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
			
			case NeatoSmartAppsEventConstants.ROBOT_PACKET_RECEIVED_ON_PEER_CONNETION: 
				handleRemotePacket(resultData);
				break;
				
			case NeatoSmartAppsEventConstants.ROBOT_STATE:
				LogHelper.logD(TAG, "Robot state data received");
				if (mRobotStateNotificationListener != null) {
					String robotId = resultData.getString(RobotCommandPacketConstants.KEY_ROBOT_ID);
					if (!TextUtils.isEmpty(robotId)) {
						mRobotStateNotificationListener.onStateReceived(robotId, resultData);
					}
				}
				break;
				
			case NeatoSmartAppsEventConstants.ROBOT_STATUS_NOTIFICATION:
				LogHelper.logD(TAG, "Robot status notification received");
				if (mRobotNotificationsListener != null) {
					String robotId = resultData.getString(RobotCommandPacketConstants.KEY_ROBOT_ID);
					if (!TextUtils.isEmpty(robotId)) {
						resultData.remove(RobotCommandPacketConstants.KEY_ROBOT_ID);
						mRobotNotificationsListener.onStatusChanged(robotId, resultData);
					}
				}
				break;
				
			case NeatoSmartAppsEventConstants.ROBOT_REGISTER_STATUS_NOTIFICATIONS:
				LogHelper.logD(TAG, "User registered for Robot status notifications received");
				if (mRobotNotificationsListener != null) {
					String robotId = resultData.getString(RobotCommandPacketConstants.KEY_ROBOT_ID);
					if (!TextUtils.isEmpty(robotId)) {
						resultData.remove(RobotCommandPacketConstants.KEY_ROBOT_ID);
						mRobotNotificationsListener.onRegister(robotId, resultData);
					}
				}
				break;
				
			case NeatoSmartAppsEventConstants.ROBOT_UNREGISTER_STATUS_NOTIFICATIONS:	
				LogHelper.logD(TAG, "User unregistered for Robot status notifications received");
				if (mRobotNotificationsListener != null) {
					String robotId = resultData.getString(RobotCommandPacketConstants.KEY_ROBOT_ID);
					if (!TextUtils.isEmpty(robotId)) {
						resultData.remove(RobotCommandPacketConstants.KEY_ROBOT_ID);
						mRobotNotificationsListener.onUnregister(robotId, resultData);
					}
				}
				break;
			case NeatoSmartAppsEventConstants.ROBOT_DATA:
				LogHelper.logD(TAG, "ROBOT_DATA received");
				if (mRobotDataListener != null) {
					String robotId = resultData.getString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID);
					int dataCode = resultData.getInt(NeatoRobotResultReceiverConstants.ROBOT_DATA_KEY_CODE);
					@SuppressWarnings("unchecked")
					HashMap<String, String> data = (HashMap<String, String>) resultData.getSerializable(NeatoRobotResultReceiverConstants.ROBOT_DATA_KEY);
					if (!TextUtils.isEmpty(robotId)) {
						mRobotDataListener.onDataReceived(robotId, dataCode, data);
					}
				} else {
					LogHelper.log(TAG, "mRobotDataListener is NULL");
				}
				break;
		}
	}

	private void handleRemotePacket(Bundle resultData) {
		if (isResponsePacket(resultData)) {
			ResponsePacket response = resultData.getParcelable(NeatoRobotResultReceiverConstants.KEY_REMOTE_RESPONSE_PACKET);
			LogHelper.logD(TAG, "Reponse received in resultReceiver: " + response);
			String robotId = resultData.getString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID);
			if (mRobotResponseListener != null) {
				mRobotResponseListener.onResponseReceived(robotId, response);
			} 
			else {
				LogHelper.log(TAG, "mRobotResponseListener is null");
			}
		} 
		else if (isRequestPacket(resultData)) {
			RobotRequests requests = resultData.getParcelable(NeatoRobotResultReceiverConstants.KEY_REMOTE_REQUEST_PACKET);
			LogHelper.logD(TAG, "Reponse received in resultReceiver: " + requests);
			String robotId = resultData.getString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID);
			if (mRobotResponseListener != null) {
				mRobotResponseListener.onRequestReceived(robotId, requests);
			} 
			else {
				LogHelper.log(TAG, "mRobotCommandListener is null");
			}
		}
	}
	
	private boolean isRequestPacket(Bundle bundle)
	{
		return bundle.containsKey(NeatoRobotResultReceiverConstants.KEY_REMOTE_REQUEST_PACKET);
	}
	
	private boolean isResponsePacket(Bundle bundle)
	{
		return bundle.containsKey(NeatoRobotResultReceiverConstants.KEY_REMOTE_RESPONSE_PACKET);
	}

	public void addDiscoveryListener(RobotDiscoveryListener robotDiscoveryListener) {
		mRobotDiscoveryListener = robotDiscoveryListener;
	}

	public void addPeerConnectionListener(RobotPeerConnectionListener robotPeerConnectionListener) {
		mRobotPeerConnectionListener = robotPeerConnectionListener;
	}

	public void addRobotResponseListener(RobotPacketListener listener) {
		mRobotResponseListener = listener;
	}
	
	public void addRobotNotificationsListener(RobotNotificationsListener robotNotificationsListener) {
		mRobotNotificationsListener = robotNotificationsListener;
	}
	
	public void addRobotStateNotificationListener(RobotStateListener robotStateListener) {
		mRobotStateNotificationListener = robotStateListener;
	}
	
	public void addRobotDataListener(RobotDataListener robotDataListener) {
		mRobotDataListener = robotDataListener;
	}
}
