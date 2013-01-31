package com.neatorobotics.android.slide.android.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

public class NeatoSmartAppLoginActivity extends Activity{

	@SuppressWarnings("unused")
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
	
	private class LoginTask extends AsyncTask<Void, Void, UserItem>  {

		private String mEmailId;
		private String mPassword;
		public LoginTask(String emailId, String password)
		{
			mEmailId = emailId;
			mPassword = password;
		}
		
		@Override
		protected UserItem doInBackground(Void... params) {
			UserItem userItem = UserManager.getInstance(getApplicationContext()).loginUser(mEmailId, mPassword);
			return userItem;
		}

		@Override
		protected void onPostExecute(UserItem userItem) {			
			closeProgress();
			
			if (userItem == null) {
				Toast.makeText(NeatoSmartAppLoginActivity.this, "Login unsuccessful", Toast.LENGTH_LONG).show();
				return;
			}
			else {
				Intent createUserIntent = new Intent(NeatoSmartAppLoginActivity.this , NeatoSmartAppTestActivity.class);
				startActivity(createUserIntent);
				setResult(RESULT_OK);
				finish();
			}
		}
	}
}
