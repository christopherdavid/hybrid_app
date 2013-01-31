package com.neatorobotics.android.slide.framework.resultreceiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotAssociateListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPeerConnectionListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotPacketListener;
import com.neatorobotics.android.slide.framework.robot.commands.request.ResponsePacket;
import com.neatorobotics.android.slide.framework.robot.commands.request.RobotRequests;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppService;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppsEventConstants;

public class NeatoRobotResultReceiver extends ResultReceiver
{
	private static final String TAG = NeatoRobotResultReceiver.class.getSimpleName();
	private RobotDiscoveryListener mRobotDiscoveryListener;
	private RobotAssociateListener mRobotAssociationListener;
	private RobotPeerConnectionListener mRobotPeerConnectionListener;
	private RobotPacketListener mRobotResponseListener;

	public NeatoRobotResultReceiver(Handler handler) {
		super(handler);
	}


	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		super.onReceiveResult(resultCode, resultData);
		LogHelper.log(TAG, "Result Code = " + resultCode);
		LogHelper.log(TAG, "Bundle = " + resultData);

		if (NeatoSmartAppsEventConstants.NEW_ROBOT_FOUND == resultCode) {
			RobotInfo robotInfo = resultData.getParcelable(NeatoSmartAppService.DISCOVERY_ROBOT_INFO);
			if (mRobotDiscoveryListener != null) {
				mRobotDiscoveryListener.onNewRobotFound(robotInfo);
			}
		}
		else if (NeatoSmartAppsEventConstants.DISCOVERY_STARTED == resultCode) {
			if (mRobotDiscoveryListener != null) {
				mRobotDiscoveryListener.onDiscoveryStarted();
			}
		}
		else if (NeatoSmartAppsEventConstants.DISCOVERY_END == resultCode) {
			if (mRobotDiscoveryListener != null) {
				mRobotDiscoveryListener.onDiscoveryFinished();
			}
		}
		else if (NeatoSmartAppsEventConstants.ROBOT_CONNECTED == resultCode) {
			if (mRobotPeerConnectionListener != null) {
				mRobotPeerConnectionListener.onRobotConnected();
			}

		}
		else if (NeatoSmartAppsEventConstants.ROBOT_DISCONNECTED == resultCode) {
			if (mRobotPeerConnectionListener != null) {
				mRobotPeerConnectionListener.onRobotDisconnected();
			}
		}
		else if (NeatoSmartAppsEventConstants.ROBOT_DATA_RECEIVED == resultCode) {

		}
		else if (NeatoSmartAppsEventConstants.ROBOT_CONNECTION_ERROR == resultCode) {
			if (mRobotPeerConnectionListener != null) {
				mRobotPeerConnectionListener.errorInConnecting();
			}
		}
		else if (NeatoSmartAppsEventConstants.ROBOT_PACKET_RECEIVED_ON_PEER_CONNETION == resultCode) {
			handleRemotePacket(resultData);
		}
		else if (NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_FAILED == resultCode) {
			if (mRobotAssociationListener != null) {
				String errMessage= "";
				if (resultData != null) {
					errMessage = resultData.getString(NeatoRobotResultReceiverConstants.RESULT_ASSOCIATION_ERROR_MESSAGE);
				}
				mRobotAssociationListener.associationError(errMessage);
			} else {
				LogHelper.logD(TAG, "Association Listener is null");
			}
		} 
		else if (NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_SUCCESS == resultCode) {

			if (mRobotAssociationListener != null) {
				mRobotAssociationListener.associationSuccess();
			} 
			else {
				LogHelper.logD(TAG, "Association Listener is null");
			}
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
	public void addRobotAssociationListener(RobotAssociateListener robotAssociationListener) {
		mRobotAssociationListener = robotAssociationListener;
	}

	public void addPeerConnectionListener(RobotPeerConnectionListener robotPeerConnectionListener) {
		mRobotPeerConnectionListener = robotPeerConnectionListener;
	}

	public void addRobotResponseListener(RobotPacketListener listener) {
		mRobotResponseListener = listener;
	}
	
}
