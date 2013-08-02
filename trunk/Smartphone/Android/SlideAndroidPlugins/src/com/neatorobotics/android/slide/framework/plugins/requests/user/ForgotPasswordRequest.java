package com.neatorobotics.android.slide.framework.plugins.requests.user;

import org.json.JSONArray;

import android.content.Context;

import com.neatorobotics.android.slide.framework.pluginhelper.JsonMapKeys;
import com.neatorobotics.android.slide.framework.pluginhelper.UserJsonData;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class ForgotPasswordRequest extends UserManagerRequest {

	@Override
	public void execute(JSONArray data, String callbackId) {
		UserJsonData jsonData = new UserJsonData(data);
		forgetPassword(mContext, jsonData, callbackId);
	}
	
	private void forgetPassword (Context context, UserJsonData jsonData, final String callbackId) {
		String email = jsonData.getString(JsonMapKeys.KEY_EMAIL);
		UserManager.getInstance(context).forgetPassword(email, new UserRequestListenerWrapper(callbackId));		
	}
}
