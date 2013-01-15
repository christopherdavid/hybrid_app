package com.neatorobotics.android.slide.android.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.webservice.user.GetNeatoUserDetailsResult;
import com.neatorobotics.android.slide.framework.webservice.user.LoginNeatoUserTokenResult;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebservicesHelper;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;

public class NeatoSmartAppLoginActivity extends Activity{

	private static final String TAG = NeatoSmartAppLoginActivity.class.getSimpleName();
	
	private ProgressBar mProgressView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		// final TextView txt_page_title = (TextView) findViewById(R.id.txt_page_title);
		// txt_page_title.setText("Enter Login Credentials");
		
		mProgressView = (ProgressBar)findViewById(R.id.login_progress);
		
		final EditText etUserEmailId = (EditText)findViewById(R.id.editUserEmailId);
		final EditText etUserPassword = (EditText)findViewById(R.id.editUserPassword);
		Button btnLogin = (Button)findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showProgress();
				LoginTask loginTask = new LoginTask(etUserEmailId.getText().toString(), etUserPassword.getText().toString());
				loginTask.execute();
			}
		});
		/*
		Button btnBackToWelcome = (Button) findViewById(R.id.btn_back);
		btnBackToWelcome.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent createUserIntent = new Intent(NeatoSmartAppLoginActivity.this , NeatoSmartAppWelcomeScreen.class);
				startActivity(createUserIntent);
				finish();

			}
		});*/

	}
	
	private void showProgress() {
		mProgressView.setVisibility(View.VISIBLE);
	}
	
	private void closeProgress() {
		mProgressView.setVisibility(View.GONE);
	}
	
	private class LoginTask extends AsyncTask<Void, Void, GetNeatoUserDetailsResult>  {

		private String mEmailId;
		private String mPassword;
		public LoginTask(String emailId, String password)
		{
			mEmailId = emailId;
			mPassword = password;
		}
		
		@Override
		protected GetNeatoUserDetailsResult doInBackground(Void... params) {
			LoginNeatoUserTokenResult result = NeatoUserWebservicesHelper.loginNeatoUserToken(NeatoSmartAppLoginActivity.this, mEmailId, mPassword);
			if (result.success()) {
				Log.i(TAG, "Auth Token = " + result.mUserAuthToken);
				NeatoPrefs.saveNeatoUserAuthToken(getApplicationContext(), result.mUserAuthToken);
				GetNeatoUserDetailsResult userDetailResult = NeatoUserWebservicesHelper.getNeatoUserDetails(NeatoSmartAppLoginActivity.this,
															mEmailId, result.mUserAuthToken);
				return userDetailResult;
			}
			return null;
		}

		@Override
		protected void onPostExecute(GetNeatoUserDetailsResult result) {
			super.onPostExecute(result);
			closeProgress();
			if (result == null) {
				Toast.makeText(NeatoSmartAppLoginActivity.this, "Login unsuccessful", Toast.LENGTH_LONG).show();
				return;
			}
			if (result.success()) {				
				
				UserItem userDetails = new UserItem();
				userDetails.setId(result.mResult.mId);
				userDetails.setChatId(result.mResult.mChat_id);
				userDetails.setChatPwd(result.mResult.mChat_pwd);
				userDetails.setEmail(result.mResult.mEmail);
				userDetails.setName(result.mResult.mName);
				UserHelper.saveLoggedInUserDetails(getApplicationContext(), userDetails);
				
				Intent createUserIntent = new Intent(NeatoSmartAppLoginActivity.this , NeatoSmartAppTestActivity.class);
				startActivity(createUserIntent);
				setResult(RESULT_OK);
				finish();
			} 
			else {
				Toast.makeText(NeatoSmartAppLoginActivity.this, result.mMessage, Toast.LENGTH_LONG).show();
			}
		}
	}
}