package com.neatorobotics.android.slide.framework.plugins.requests.robot.profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.RobotJsonData;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.robot.schedule2.SchedulerConstants2;
import com.neatorobotics.android.slide.framework.robotdata.RobotProfileDataUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.GetRobotProfileDetailsResult2;
import com.neatorobotics.android.slide.framework.webservice.robot.schedule.RobotSchedulerManager2;

public class IsRobotScheduleEnabledRequest extends RobotManagerRequest {

	@Override
	public void execute(String action, JSONArray data, String callbackId) {
		RobotJsonData jsonData = new RobotJsonData(data);
		isScheduleEnabled(mContext, jsonData, callbackId);
	}

	private void isScheduleEnabled(final Context context, RobotJsonData jsonData, final String callbackId) {
		LogHelper.logD(TAG, "isScheduleEnabled action initiated in Robot plugin");
		final String robotId = jsonData.getString(JsonMapKeys.KEY_ROBOT_ID);
		final int scheduleType = jsonData.getInt(JsonMapKeys.KEY_SCHEDULE_TYPE);
		int scheduleTypeOnServer = SchedulerConstants2.convertToServerConstants(scheduleType);
		RobotSchedulerManager2.getInstance(context).isScheduleEnabled(robotId, scheduleTypeOnServer, new RobotRequestListenerWrapper(callbackId) {

			@Override
			public JSONObject getResultObject(
					NeatoWebserviceResult responseResult)
					throws JSONException {
				JSONObject jsonResult = null;
				if((responseResult != null) && (responseResult instanceof GetRobotProfileDetailsResult2)) {
					GetRobotProfileDetailsResult2 result = (GetRobotProfileDetailsResult2) responseResult;
					String scheduleState = RobotProfileDataUtils.getScheduleState(context, scheduleType, result);
					boolean isScheduleEnabled = Boolean.valueOf(scheduleState);
					jsonResult = new JSONObject();
					jsonResult.put(JsonMapKeys.KEY_IS_SCHEDULE_ENABLED, isScheduleEnabled);
					jsonResult.put(JsonMapKeys.KEY_SCHEDULE_TYPE, scheduleType);
					jsonResult.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
				}
				else {
					jsonResult = getErrorJsonObject(ErrorTypes.ERROR_TYPE_UNKNOWN, "response result is not of type is schedule enabled result");
				}
				return jsonResult;
			}
		});
	}

}
