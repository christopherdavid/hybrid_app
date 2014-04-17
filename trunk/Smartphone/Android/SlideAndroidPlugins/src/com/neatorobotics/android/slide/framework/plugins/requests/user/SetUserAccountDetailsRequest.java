package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceResult;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class SetUserAccountDetailsRequest extends UserManagerRequest {

    @Override
    public void execute(JSONArray data, String callbackId) {
        UserJsonData jsonData = new UserJsonData(data);
        setUserAccountDetails(mContext, jsonData, callbackId);
    }

    private void setUserAccountDetails(final Context context, UserJsonData jsonData, final String callbackId) {
        LogHelper.logD(TAG, "setUserDetails Called");
        LogHelper.logD(TAG, "JSON String: " + jsonData);

        String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
        final String CountryCode = jsonData.getString(JsonMapKeys.KEY_COUNTRYCODE);
        final String optin = jsonData.getString(JsonMapKeys.KEY_OPTIN);
        String authKey = NeatoPrefs.getNeatoUserAuthToken(context);
        LogHelper.logD(TAG, "Email:" + email + " authKey: " + authKey);

        UserManager.getInstance(context).setUserAccountDetails(email, authKey, CountryCode, optin,
                new UserRequestListenerWrapper(callbackId) {
                    @Override
                    public JSONObject getResultObject(NeatoWebserviceResult responseResult) throws JSONException {

                        JSONObject userAccountDetails = new JSONObject();
                        userAccountDetails.put(JsonMapKeys.KEY_COUNTRY_CODE_CAMEL_CASE, CountryCode);
                        userAccountDetails.put(JsonMapKeys.KEY_OPT_IN_CAMEL_CASE, optin);

                        return userAccountDetails;
                    }
                });
    }

}
