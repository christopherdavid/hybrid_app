package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;
import com.neatorobotics.android.slide.framework.webservice.user.UserValidationHelper;

import android.content.Context;

public class CreateUserRequest extends UserManagerRequest {

    @Override
    public void execute(JSONArray data, String callbackId) {
        UserJsonData jsonData = new UserJsonData(data);
        createUser3(mContext, jsonData, callbackId);
    }

    private void createUser3(final Context context, final UserJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "Create User3 called");
        String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
        String alternateEmail = jsonData.getString(JsonMapKeys.KEY_ALTERNATE_EMAIL);
        String password = jsonData.getString(JsonMapKeys.KEY_PASSWORD);
        String name = jsonData.getString(JsonMapKeys.KEY_USER_NAME);

        // TODO: Do not use the direct json object from the js layer.
        // Change to extract the known params and then send to server.
        String extraParams = jsonData.getString(JsonMapKeys.KEY_EXTRA_PARAMS);

        LogHelper.log(TAG, "Extra parameters used : " + extraParams);
        UserManager.getInstance(context).createUser3(name, email, alternateEmail, password, extraParams,
                new UserRequestListenerWrapper(callbackId) {

                    @Override
                    public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {
                        UserItem userItem = getUserItemFromResponse(responseResult);

                        JSONObject userDetails = null;
                        if (userItem != null) {
                            userDetails = new JSONObject();
                            userDetails.put(JsonMapKeys.KEY_EMAIL, userItem.email);
                            userDetails.put(JsonMapKeys.KEY_ALTERNATE_EMAIL, userItem.alternate_email);
                            userDetails.put(JsonMapKeys.KEY_USER_NAME, userItem.name);
                            userDetails.put(JsonMapKeys.KEY_USER_ID, userItem.id);
                            int validationCode = UserValidationHelper
                                    .getUserValidationStatus(userItem.validation_status);
                            userDetails.put(JsonMapKeys.KEY_VALIDATION_STATUS, validationCode);

                            JSONObject jsonParam = new JSONObject();
                            if (userItem.extra_param != null) {
                                jsonParam.put(JsonMapKeys.KEY_COUNTRY_CODE_CAMEL_CASE,
                                        userItem.extra_param.country_code);
                                jsonParam.put(JsonMapKeys.KEY_OPT_IN_CAMEL_CASE, userItem.extra_param.opt_in);
                            } else {
                                LogHelper.log(TAG, "Extra parameters in the User item is null");
                            }
                            userDetails.put(JsonMapKeys.KEY_EXTRA_PARAMS, jsonParam);

                        }

                        return userDetails;
                    }
                });
    }
}
