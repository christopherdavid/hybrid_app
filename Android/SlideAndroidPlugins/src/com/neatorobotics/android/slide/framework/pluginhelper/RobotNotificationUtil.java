package com.neatorobotics.android.slide.framework.pluginhelper;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;

import com.neatorobotics.android.slide.framework.ApplicationConfig;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.resultreceiver.NeatoRobotResultReceiver;
import com.neatorobotics.android.slide.framework.resultreceiver.NeatoRobotResultReceiverConstants;
import com.neatorobotics.android.slide.framework.robot.commands.listeners.RobotDataListener;
import com.neatorobotics.android.slide.framework.service.NeatoSmartAppsEventConstants;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;

public class RobotNotificationUtil {

    private static final String TAG = RobotNotificationUtil.class.getSimpleName();

    public static void addRobotDataChangedListener(Context context, RobotDataListener listener) {
        NeatoRobotResultReceiver receiver = ApplicationConfig.getInstance(context).getRobotResultReceiver();
        if (receiver != null) {
            receiver.addRobotDataListener(listener);
        }
    }

    public static void removeRobotDataChangedListener(Context context) {
        NeatoRobotResultReceiver receiver = ApplicationConfig.getInstance(context).getRobotResultReceiver();
        if (receiver != null) {
            receiver.addRobotDataListener(null);
        }
    }

    public static void notifyDataChanged(Context context, String robotId, int keyCode, HashMap<String, String> data) {
        int resultCode = NeatoSmartAppsEventConstants.ROBOT_DATA;
        Bundle dataChanged = new Bundle();
        dataChanged.putString(NeatoRobotResultReceiverConstants.KEY_ROBOT_ID, robotId);
        dataChanged.putInt(NeatoRobotResultReceiverConstants.ROBOT_DATA_KEY_CODE, keyCode);
        dataChanged.putSerializable(NeatoRobotResultReceiverConstants.ROBOT_DATA_KEY, data);
        NeatoRobotResultReceiver receiver = ApplicationConfig.getInstance(context).getRobotResultReceiver();
        if (receiver != null) {
            receiver.send(resultCode, dataChanged);
        }
    }

    public static JSONObject getNotificationObject(String robotId, int dataCode, Map<String, String> data) {
        JSONObject robotData = new JSONObject();
        try {
            robotData.put(JsonMapKeys.KEY_ROBOT_DATA_ID, dataCode);
            robotData.put(JsonMapKeys.KEY_ROBOT_ID, robotId);
            robotData.put(JsonMapKeys.KEY_ROBOT_DATA, DataConversionUtils.mapToJsonObject(data));
        } catch (JSONException e) {
            robotData = null;
            LogHelper.log(TAG, "Error in getNotificationObject", e);
        }
        return robotData;
    }
}
