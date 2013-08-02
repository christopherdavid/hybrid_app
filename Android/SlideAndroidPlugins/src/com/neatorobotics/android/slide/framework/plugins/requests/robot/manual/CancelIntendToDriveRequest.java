package com.neatorobotics.android.slide.framework.plugins.requests.robot.manual;

import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.drive.RobotDriveHelper;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.DeleteRobotProfileKeyResult;

public class CancelIntendToDriveRequest extends RobotManagerRequest {
	
	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		cancelIntendToDrive(mContext, jsonData, callbackId);
	}
	
	private void cancelIntendToDrive(final Context context, RobotJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "cancelIntendToDrive is called");
		final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
		
		RobotDriveHelper.getInstance(context).cancelRobotDriveRequest(robotId, new RobotRequestListenerWrapper(callbackId) {
			@Override
			public void onReceived(NeatoWebserviceResult result)
					 {
				if (result != null && result instanceof DeleteRobotProfileKeyResult) {
					DeleteRobotProfileKeyResult profileResult = (DeleteRobotProfileKeyResult) result;
					if (profileResult.result.success) {
						RobotDriveHelper.getInstance(context).untrackRobotDriveRequest(robotId);
						PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
						pluginResult.setKeepCallback(false);
						sendSuccessPluginResult(pluginResult, callbackId);
					}
					else {
						sendError(callbackId, ErrorTypes.ROBOT_UNABLE_TO_CANCEL_INTEND_TO_DRIVE, "Unable to cancel intend to drive");
					}
				}
				
			}
		});
	}
}
