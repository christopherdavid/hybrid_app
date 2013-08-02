package com.neatorobotics.android.slide.framework.plugins.requests.robot.manual;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.drive.RobotDriveHelper;
import com.neatorobotics.android.slide.framework.utils.NetworkConnectionUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;

public class IntendToDriveRequest extends RobotManagerRequest {
	
	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		intendToDrive(mContext, jsonData, callbackId);
	}
	
	private void intendToDrive(final Context context, RobotJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "intendToDrive is called");
		final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
		
		if (!NetworkConnectionUtils.isConnectedOverWiFi(context)) {
			LogHelper.logD(TAG, "Wifi connection isn't being used. Cannot drive");
			sendError(callbackId, ErrorTypes.ERROR_TYPE_WIFI_NOT_CONNECTED, "Drive Robot needs wifi connection");
			return;
		}
			
		RobotDriveHelper.getInstance(context).setRobotDriveRequest(robotId, new RobotSetProfileDataRequestListener(callbackId) {
			@Override
			public JSONObject getResultObject(NeatoWebserviceResult result)
					throws JSONException {
				RobotDriveHelper.getInstance(context).trackRobotDriveRequest(robotId);
				return super.getResultObject(result);
			}
		});
	}
}
