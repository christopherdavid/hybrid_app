package com.neatorobotics.android.slide.framework.plugins.requests.robot.command;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.robot.commands.RobotCommandPacketConstants;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;

public class CommandRequestUtils {
    private static final String TAG = CommandRequestUtils.class.getSimpleName();

    static HashMap<String, String> getCommandParams(JSONObject jObject) {
        HashMap<String, String> commandParamsMap = null;
        if (jObject == null) {
            return new HashMap<String, String>();
        }
        if (jObject.has(RobotCommandPacketConstants.KEY_COMMAND_PARAMS_TAG)) {
            // Command params are present. Need to parse the JSON object and
            // convert it to HashMap
            try {
                JSONObject commandParams = jObject.getJSONObject(RobotCommandPacketConstants.KEY_COMMAND_PARAMS_TAG);
                commandParamsMap = DataConversionUtils.jsonObjectToHashMap(commandParams);
            } catch (JSONException e) {
                LogHelper.log(TAG, "Exception in getCommandParams", e);
            }
        }
        return commandParamsMap;
    }
}
