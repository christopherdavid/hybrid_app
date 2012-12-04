package com.neatorobotics.android.slide.framework.resultreceiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.model.RobotInfo;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotAssociateListener;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDiscoveryListener;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppService;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppsEventConstants;

public class NeatoRobotResultReceiver extends ResultReceiver
{
	private static final String TAG = NeatoRobotResultReceiver.class.getSimpleName();
	private RobotDiscoveryListener mRobotDiscoveryListener;
	private RobotAssociateListener mRobotAssociationListener;

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

		}
		else if (NeatoSmartAppsEventConstants.ROBOT_DISCONNECTED == resultCode) {

		}
		else if (NeatoSmartAppsEventConstants.ROBOT_DATA_RECEIVED == resultCode) {

		} 
		else if (NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_FAILED == resultCode) {
			if (mRobotAssociationListener != null) {
				String errMessage= "";
				if (resultData != null) {
					errMessage = resultData.getString(NeatoRobotResultReceiverConstants.RESULT_ASSOCIATION_ERROR_MESSAGE);
				}
				mRobotAssociationListener.associationError(errMessage);
			}
		} 
		else if (NeatoSmartAppsEventConstants.ROBOT_ASSOCIATION_STATUS_SUCCESS == resultCode) {
			if(mRobotAssociationListener != null) {
				mRobotAssociationListener.associationSuccess();
			}
		}
	}


	public void addDiscoveryListener(RobotDiscoveryListener robotDiscoveryListener) {
		mRobotDiscoveryListener = robotDiscoveryListener;
	}
	public void addRobotAssociationListener(RobotAssociateListener robotAssociationListener) {
		mRobotAssociationListener = robotAssociationListener;
	}


}
