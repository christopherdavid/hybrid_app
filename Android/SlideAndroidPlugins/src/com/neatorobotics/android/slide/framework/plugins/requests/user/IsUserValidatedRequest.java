package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.user.IsUserValidatedResult;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;
import com.neatorobotics.android.slide.framework.webservice.user.UserValidationHelper;

public class IsUserValidatedRequest extends UserManagerRequest {

    @Override
    public void execute(JSONArray data, String callbackId) {
        UserJsonData jsonData = new UserJsonData(data);
        isUserValidated(mContext, jsonData, callbackId);
    }

    private void isUserValidated(final Context context, UserJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "checking if user is authenticated");
        LogHelper.logD(TAG, "JSON String: " + jsonData);

        String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
        LogHelper.logD(TAG, "Email: " + email);

        UserManager.getInstance(context).isUserValidated(email, new UserRequestListenerWrapper(callbackId) {
            @Override
            public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
                JSONObject resultObj = new JSONObject();
                if ((responseResult != null) && (responseResult instanceof IsUserValidatedResult)) {
                    IsUserValidatedResult validationResult = (IsUserValidatedResult) responseResult;
                    int userValidationCode = UserValidationHelper
                            .getUserValidationStatus(validationResult.result.validation_status);
                    resultObj.put(JsonMapKeys.KEY_VALIDATION_STATUS, userValidationCode);
                    resultObj.put(JsonMapKeys.KEY_MESSAGE, validationResult.result.message);
                }

                return resultObj;
            }
        });
    }
}
