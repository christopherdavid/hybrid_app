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
import android.widget.Toast;

import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.webservice.user.CreateNeatoUserResult;
import com.neatorobotics.android.slide.framework.webservice.user.GetNeatoUserDetailsResult;
import com.neatorobotics.android.slide.framework.webservice.user.NeatoUserWebservicesHelper;
import com.neatorobotics.android.slide.framework.webservice.user.UserItem;

public class NeatoSmartAppCreateUserActivity extends Activity{
	private static final String TAG = NeatoSmartAppCreateUserActivity.class.getSimpleName();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.createuser);
		final EditText etUserName = (EditText)findViewById(R.id.editUserName);
        final EditText etUserEmailId = (EditText)findViewById(R.id.editUserEmailId);
        final EditText etUserPassword = (EditText)findViewById(R.id.editUserPassword);
        Button btnCreateUser = (Button)findViewById(R.id.btnCreateUser);
        
       
		// final TextView txt_page_title = (TextView) findViewById(R.id.txt_page_title);
		// txt_page_title.setText("Register User");
        
        btnCreateUser.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				CreateUserTask loginTask = new CreateUserTask(etUserName.getText().toString(), etUserEmailId.getText().toString(), etUserPassword.getText().toString());
				loginTask.execute();
			}
		});
        /*
		Button btnBackToWelcome = (Button) findViewById(R.id.btn_back);
		btnBackToWelcome.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent createUserIntent = new Intent(NeatoSmartAppCreateUserActivity.this , NeatoSmartAppWelcomeScreen.class);
				startActivity(createUserIntent);
				finish();

			}
		});
		*/
    }
    
    private static class CreateUserResultWrapper
    {
    	private CreateNeatoUserResult createUserResult;
    	private GetNeatoUserDetailsResult getUserDetailResult;
    }
    
     class CreateUserTask extends AsyncTask<Void, Void, CreateUserResultWrapper>  {
    	
    	private String mEmailId;
    	private String mPassword;
    	private String mName;
    	public CreateUserTask(String name, String emailId, String password)
    	{
    		mEmailId = emailId;
    		mPassword = password;
    		mName = name;
    	}
    	
    	@Override
    	protected CreateUserResultWrapper doInBackground(Void... params) {
    		CreateUserResultWrapper createUserResultWrapper = new CreateUserResultWrapper();
    		CreateNeatoUserResult result = NeatoUserWebservicesHelper.createNeatoUserRequestNative(NeatoSmartAppCreateUserActivity.this, mName, mEmailId, mPassword);
    		if ((result != null) && result.success()) {
        		createUserResultWrapper.createUserResult = result;
				Log.i(TAG, "User handle = " + result.mResult.mUserHandle);
				GetNeatoUserDetailsResult userDetailResult = NeatoUserWebservicesHelper.getNeatoUserDetails(NeatoSmartAppCreateUserActivity.this,
															mEmailId, result.mResult.mUserHandle);
				if ((userDetailResult != null) && userDetailResult.success()) {
					createUserResultWrapper.getUserDetailResult = userDetailResult;
				}
				return createUserResultWrapper;
			}
			return createUserResultWrapper;
			
    	}

		@Override
		protected void onPostExecute(CreateUserResultWrapper result) {
			super.onPostExecute(result);
			CreateNeatoUserResult createUserResult = result.createUserResult;
			GetNeatoUserDetailsResult getUserResult = result.getUserDetailResult;
			if (createUserResult == null) {
				Toast.makeText(NeatoSmartAppCreateUserActivity.this, "Error in creating user", Toast.LENGTH_LONG).show();
				return;
			}
			
			if (getUserResult == null) {
				Toast.makeText(NeatoSmartAppCreateUserActivity.this, "User created but failed to fetch user details. Please try and login", Toast.LENGTH_LONG).show();
				return;
			}
			
			if (getUserResult.success()) {
				Toast.makeText(NeatoSmartAppCreateUserActivity.this, "User created", Toast.LENGTH_LONG).show();
				UserItem userDetails = new UserItem();
				userDetails.setId(getUserResult.mResult.mId);
				userDetails.setChatId(getUserResult.mResult.mChat_id);
				userDetails.setChatPwd(getUserResult.mResult.mChat_pwd);
				userDetails.setEmail(getUserResult.mResult.mEmail);
				userDetails.setName(getUserResult.mResult.mName);
				UserHelper.saveLoggedInUserDetails(getApplicationContext(), userDetails);
				
				Intent createUserIntent = new Intent(NeatoSmartAppCreateUserActivity.this , NeatoSmartAppTestActivity.class);
				startActivity(createUserIntent);
				setResult(RESULT_OK);
				finish();
				
			} 
			else {
				Toast.makeText(NeatoSmartAppCreateUserActivity.this, getUserResult.mMessage, Toast.LENGTH_LONG).show();
			}			
		}
    }
}
