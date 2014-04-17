package com.neatorobotics.android.slide.framework.plugins.requests.robot;

import java.lang.ref.WeakReference;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.ErrorTypes;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.robot.datamanager.SetRobotProfileDetailsResult3;
import com.neatorobotics.android.slide.framework.webservice.user.WebServiceBaseRequestListener;

import android.content.Context;

public abstract class RobotManagerRequest {

    protected final String TAG = getClass().getSimpleName();
    protected Context mContext;
    private WeakReference<Plugin> mPlugin;

    public void initalize(Context context, Plugin plugin) {
        mContext = context.getApplicationContext();
        mPlugin = new WeakReference<Plugin>(plugin);
    }

    protected void sendError(String callbackId, int errorCode, String message) {
        JSONObject errorInfo = getErrorJsonObject(errorCode, message);
        PluginResult errorPluginResult = new PluginResult(PluginResult.Status.ERROR, errorInfo);
        errorPluginResult.setKeepCallback(false);
        sendErrorPluginResult(errorPluginResult, callbackId);
    }

    protected JSONObject getErrorJsonObject(int errorCode, String errMessage) {
        JSONObject error = new JSONObject();
        try {
            error.put(JsonMapKeys.KEY_ERROR_CODE, errorCode);
            error.put(JsonMapKeys.KEY_ERROR_MESSAGE, errMessage);
        } catch (JSONException e) {
            LogHelper.logD(TAG, "Exception in getErrorJsonObject", e);
        }
        return error;
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

    protected class RobotRequestListenerWrapper implements WebServiceBaseRequestListener {
        private String mCallbackId;

        public RobotRequestListenerWrapper(String callbackId) {
            mCallbackId = callbackId;
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
                LogHelper.logD(TAG, "JSON Error");
                sendError(mCallbackId, ErrorTypes.JSON_PARSING_ERROR, ex.getMessage());
            }
        }

        public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
            return new JSONObject();
        }

        @Override
        public void onServerError(int errorType, String errorMessage) {
            LogHelper.logD(TAG, "Server Error: " + errorMessage);
            sendError(mCallbackId, errorType, errorMessage);
        }
    }

    public final class NoActionWebServiceRequestListener implements WebServiceBaseRequestListener {

        @Override
        public void onReceived(NeatoWebserviceResult responseResult) {
            LogHelper.log(TAG, "NoActionWebServiceRequestListener: onReceive responseResult = " + responseResult);
        }

        @Override
        public void onNetworkError(String errMessage) {
            LogHelper.log(TAG, "NoActionWebServiceRequestListener: onNetworkError errMessage = " + errMessage);
        }

        @Override
        public void onServerError(int errorType, String errMessage) {
            LogHelper.log(TAG, "NoActionWebServiceRequestListener: onServerError errMessage = " + errMessage);
        }
    }

    protected class RobotSetProfileDataRequestListener extends RobotRequestListenerWrapper {

        public RobotSetProfileDataRequestListener(String callbackId) {
            super(callbackId);
        }

        @Override
        public JSONObject getResultObject(NeatoWebserviceResult result) throws JSONException {
            JSONObject object = null;
            if (result != null && result instanceof SetRobotProfileDetailsResult3) {
                object = new JSONObject();
                SetRobotProfileDetailsResult3 profileResult = (SetRobotProfileDetailsResult3) result;
                object.put(JsonMapKeys.KEY_EXPECTED_TIME_TO_EXECUTE, profileResult.extra_params.expected_time);
            }
            return object;
        }
    }

    abstract public void execute(String action, JSONArray data, String callbackId);
}
