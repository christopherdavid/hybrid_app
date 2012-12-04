package com.neatorobotics.android.slide.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebConstants;
import com.neatorobotics.android.slide.framework.webservice.user.GetNeatoUserDetailsResult;
import com.neatorobotics.android.slide.framework.webservice.user.LoginNeatoUserTokenResult;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebservicesHelper;

public class NeatoSmartAppWelcomeScreen extends Activity{
	private static final String TAG = NeatoSmartAppLoginActivity.class.getSimpleName();
	
	private static final int REQUEST_CODE_LOGIN_ACTIVITY = 1001;
	private static final int REQUEST_CODE_CREATE_ACCOUNT_ACTIVITY = 1002;
	
	private static final String DEMO_USER_EMAIL = "demo1@demo.com";
	private static final String DEMO_USER_PASSWORD = "demo123";

	private ProgressBar mProgressView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcomescreen);
		
		mProgressView = (ProgressBar)findViewById(R.id.home_progress);
		NeatoWebConstants.setServerEnvironment(NeatoWebConstants.PROD_SERVER_ID);
		String emailId = NeatoPrefs.getUserEmailId(this);
        if (!TextUtils.isEmpty(emailId)) {
			Intent mainActivity = new Intent(NeatoSmartAppWelcomeScreen.this , NeatoSmartAppTestActivity.class);
			startActivity(mainActivity);
			finish();
			return;
        }
		
		// final TextView txt_page_title = (TextView) findViewById(R.id.txt_page_title);
		// txt_page_title.setText("Welcome to Neato!");
		
		Button btnGoToLogin = (Button)findViewById(R.id.goToLogin);
		btnGoToLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent loginIntent = new Intent(NeatoSmartAppWelcomeScreen.this , NeatoSmartAppLoginActivity.class);
				startActivityForResult(loginIntent, REQUEST_CODE_LOGIN_ACTIVITY);
			}
		});

		Button btnGoToRegister = (Button) findViewById(R.id.goToRegister);
		btnGoToRegister.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent createUserIntent = new Intent(NeatoSmartAppWelcomeScreen.this , NeatoSmartAppCreateUserActivity.class);				
				startActivityForResult(createUserIntent, REQUEST_CODE_CREATE_ACCOUNT_ACTIVITY);
				
			}
		});
		
		Button btnDemoLogin = (Button)findViewById(R.id.btn_demo_login);
		btnDemoLogin.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				showProgress();
				new LoginAsyncTask(DEMO_USER_EMAIL, DEMO_USER_PASSWORD).execute();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_CODE_CREATE_ACCOUNT_ACTIVITY:
				case REQUEST_CODE_LOGIN_ACTIVITY:			
					finish();
					break;		
			}
		}
	}
	
	private void showProgress() {
		mProgressView.setVisibility(View.VISIBLE);
	}
	
	private void closeProgress() {
		mProgressView.setVisibility(View.GONE);
	}
	
	// AsyncTask for login to the demo user account "demo1@demo.com"  
	private class LoginAsyncTask extends AsyncTask<Void, Void, GetNeatoUserDetailsResult>  {
		
		private String mEmailId;
		private String mPassword;
		
		public LoginAsyncTask(String emailId, String password) {
			mEmailId = emailId;
			mPassword = password;
		}
		
		@Override
		protected GetNeatoUserDetailsResult doInBackground(Void... params) {
			LoginNeatoUserTokenResult result = NeatoUserWebservicesHelper.loginNeatoUserToken(NeatoSmartAppWelcomeScreen.this, mEmailId, mPassword);
			if (result.success()) {
				Log.i(TAG, "LoginAsyncTask - Auth Token = " + result.mUserAuthToken);
				GetNeatoUserDetailsResult userDetailResult = NeatoUserWebservicesHelper.getNeatoUserDetails(NeatoSmartAppWelcomeScreen.this,
															mEmailId, result.mUserAuthToken);
				return userDetailResult;
			}
			return null;
		}

		@Override
		protected void onPostExecute(GetNeatoUserDetailsResult result) {
			closeProgress();
			if (result == null) {
				Toast.makeText(NeatoSmartAppWelcomeScreen.this, "Demo user login unsuccessful", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (result.success()) {
				NeatoPrefs.saveUserEmailId(NeatoSmartAppWelcomeScreen.this, result.mResult.mEmail);
				// Toast.makeText(NeatoSmartAppWelcomeScreen.this, "Demo user login Successful", Toast.LENGTH_SHORT).show();
				NeatoPrefs.saveJabberId(NeatoSmartAppWelcomeScreen.this, result.mResult.mChat_id);
				NeatoPrefs.saveJabberPwd(NeatoSmartAppWelcomeScreen.this, result.mResult.mChat_pwd);
				Intent createUserIntent = new Intent(NeatoSmartAppWelcomeScreen.this , NeatoSmartAppTestActivity.class);
				startActivity(createUserIntent);
				finish();
			} 
			else {
				Toast.makeText(NeatoSmartAppWelcomeScreen.this, result.mMessage, Toast.LENGTH_SHORT).show();
			}
		}
	}

}
