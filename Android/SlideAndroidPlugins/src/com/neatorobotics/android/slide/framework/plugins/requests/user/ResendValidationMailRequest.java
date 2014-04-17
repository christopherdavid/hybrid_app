package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.user.ResendValidationMailResult;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class ResendValidationMailRequest extends UserManagerRequest {

    @Override
    public void execute(JSONArray data, String callbackId) {
        UserJsonData jsonData = new UserJsonData(data);
        resendValidationMail(mContext, jsonData, callbackId);
    }

    private void resendValidationMail(final Context context, UserJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "resend Validation Mail called");
        LogHelper.logD(TAG, "JSON String: " + jsonData);

        String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
        LogHelper.logD(TAG, "Email: " + email);

        UserManager.getInstance(context).resendValidationMail(email, new UserRequestListenerWrapper(callbackId) {
            @Override
            public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
                JSONObject resultObj = new JSONObject();
                if ((responseResult != null) && (responseResult instanceof ResendValidationMailResult)) {
                    ResendValidationMailResult resendResult = (ResendValidationMailResult) responseResult;
                    resultObj.put(JsonMapKeys.KEY_MESSAGE, resendResult.result.message);
                }

                return resultObj;
            }
        });
    }
}
