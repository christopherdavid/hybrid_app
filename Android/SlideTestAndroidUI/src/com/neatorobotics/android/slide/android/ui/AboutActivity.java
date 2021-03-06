package com.neatorobotics.android.slide.android.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.utils.AppUtils;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebConstants;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;

/*
 * This activity displays debug information like application version, 
 * logged-in user name, server   
 */
public class AboutActivity extends Activity {

	// private static final String LOCAL_TAG = AboutActivity.class.getSimpleName();	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.about);
		
		showInfo();
	}

	private void showInfo() {
		showAppVersion();
		showLoggedInUserName();
		showServerInfo();
	}
	
	private void showAppVersion() {
		String version = AppUtils.getVersionWithBuildNumber(this);		
		
		TextView txtInfo = (TextView)findViewById(R.id.txt_about_appversion);
		txtInfo.setText(getString(R.string.text_about_app_name));
		txtInfo.append("\nBuild " + version);
	}
	
	private void showLoggedInUserName() {
		boolean loggedIn = UserHelper.isUserLoggedIn(getApplicationContext());
		if (loggedIn) {
			UserItem userItem = UserHelper.getLoggedInUserDetails(getApplicationContext());
			if (userItem != null) {
				TextView txtInfo = (TextView)findViewById(R.id.txt_about_user);
				String textUser = String.format(getString(R.string.text_about_user_info), userItem.getName(), userItem.getEmail());
				txtInfo.setText(textUser);
			}
		}
	}
	
	private void showServerInfo() {
		String serverName = null;
		
		int serverId = NeatoWebConstants.getServerId();
		switch (serverId) {
			case NeatoWebConstants.DEV_SERVER_ID:
				serverName = "Development";
				break;
			case NeatoWebConstants.STAGING_SERVER_ID:
				serverName = "Staging";
				break;
			case NeatoWebConstants.PROD_SERVER_ID:
				serverName = "Production";
				break;
		}
		
		if (!TextUtils.isEmpty(serverName)) {
			// String serverText = String.format("%s server in use", serverName);;
			TextView txtInfo = (TextView)findViewById(R.id.txt_about_server);
			txtInfo.setText(Html.fromHtml(String.format("<u><i>%s</i></u>", serverName)));			
			txtInfo.append(" server in use");
		}
	}
}
