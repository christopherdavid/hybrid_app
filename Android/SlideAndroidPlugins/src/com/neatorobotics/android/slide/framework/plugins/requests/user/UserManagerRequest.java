package com.neatorobotics.android.slide.framework.plugins.requests.user;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.user.GetNeatoUserDetailsResult;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;

import android.content.Context;

public abstract class UserManagerRequest {

    protected final String TAG = getClass().getSimpleName();
    protected Context mContext;
    private WeakReference<Plugin> mPlugin;

    public void initalize(Context context, Plugin plugin) {
        mContext = context.getApplicationContext();
        mPlugin = new WeakReference<Plugin>(plugin);
    }

    protected UserItem getUserItemFromResponse(NeatoWebserviceResult responseResult) {
        UserItem userItem = null;
        if ((responseResult != null) && (responseResult instanceof GetNeatoUserDetailsResult)) {
            userItem = ((GetNeatoUserDetailsResult) responseResult).result;
        }

        return userItem;
    }

    protected JSONArray convertRobotItemsToJSONArray(ArrayList<RobotItem> robotList) {
        JSONArray robots = new JSONArray();
        if ((robotList != null) && (robotList.size() > 0)) {
            for (RobotItem item : robotList) {
                try {

                    JSONObject robot = new JSONObject();
                    robot.put(JsonMapKeys.KEY_ROBOT_ID, item.serial_number);
                    robot.put(JsonMapKeys.KEY_ROBOT_NAME, item.name);
                    robots.put(robot);
                } catch (JSONException e) {
                    LogHelper.log(TAG, "Exception in RobotDetailsPluginListener", e);
                }
            }
        } else {
            LogHelper.logD(TAG, "No robots associated");
        }
        return robots;
    }

    protected void sendError(String callbackId, int errorCode, String message) {
        JSONObject errorInfo = getErrorJsonObject(errorCode, message);
        PluginResult loginUserPluginResult = new PluginResult(PluginResult.Status.ERROR, errorInfo);
        sendErrorPluginResult(loginUserPluginResult, callbackId);
    }

    private JSONObject getErrorJsonObject(int errorCode, String errMessage) {
        JSONObject error = new JSONObject();
        try {
            error.put(JsonMapKeys.KEY_ERROR_CODE, errorCode);
            error.put(JsonMapKeys.KEY_ERROR_MESSAGE, errMessage);
        } catch (JSONException e) {
            LogHelper.logD(TAG, "Exception in getErrorJsonObject", e);
        }
        return error;
    }

    protected class UserRequestListenerWrapper implements WebServiceBaseRequestListener {
        protected String mCallbackId;

        public UserRequestListenerWrapper(String callbackId) {
            mCallbackId = callbackId;
        }

        @Override
        public void onServerError(int errorType, String errMessage) {
            LogHelper.logD(TAG, String.format("Server ErrorType = [%d] Message = %s", errorType, errMessage));
            sendError(mCallbackId, errorType, errMessage);
        }

        @Override
        public void onNetworkError(String errorMessage) {
            LogHelper.logD(TAG, "Network Error: " + errorMessage);
            sendError(mCallbackId, ErrorTypes.ERROR_NETWORK_ERROR, errorMessage);
        }

        @Override
        public void onReceived(NeatoWebserviceResult responseResult) {
            LogHelper.logD(TAG, "Request processed successfully");
            try {
                JSONObject resultObj = getResultObject(responseResult);
                if (resultObj != null) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, resultObj);
                    sendSuccessPluginResult(pluginResult, mCallbackId);
                } else {
                    LogHelper.logD(TAG, "Unknown Error");
                    sendError(mCallbackId, ErrorTypes.ERROR_TYPE_UNKNOWN, "Unknown Error");
                }
            } catch (JSONException ex) {
                LogHelper.logD(TAG, "JSON Error", ex);
                sendError(mCallbackId, ErrorTypes.JSON_PARSING_ERROR, ex.getMessage());
            }
        }

        public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
            return new JSONObject();
        }
    }

    protected void sendSuccessPluginResult(PluginResult result, String callbackId) {
        Plugin plugin = mPlugin.get();
        if (plugin != null) {
            plugin.success(result, callbackId);
        }
    }

    protected void sendErrorPluginResult(PluginResult result, String callbackId) {
        Plugin plugin = mPlugin.get();
        if (plugin != null) {
            plugin.error(result, callbackId);
        }
    }

    abstract public void execute(JSONArray data, String callbackId);
}
