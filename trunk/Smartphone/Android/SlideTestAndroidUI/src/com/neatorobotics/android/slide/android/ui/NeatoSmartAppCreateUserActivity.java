package com.neatorobotics.android.slide.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.neatorobotics.android.slide.framework.webservice.user.UserItem;
import com.neatorobotics.android.slide.framework.webservice.user.UserManager;

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
    
    class CreateUserTask extends AsyncTask<Void, Void, UserItem>  {
    	
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
    	protected UserItem doInBackground(Void... params) {
    		UserItem userItem = UserManager.getInstance(getApplicationContext()).createUser(mName, mEmailId, mPassword);
    		return userItem;
    	}

		@Override
		protected void onPostExecute(UserItem userItem) {
			if (userItem == null) {
				Toast.makeText(NeatoSmartAppCreateUserActivity.this, "Error in creating user", Toast.LENGTH_LONG).show();
				return;
			}
			else {
				Toast.makeText(NeatoSmartAppCreateUserActivity.this, "User created", Toast.LENGTH_LONG).show();
				
				Intent createUserIntent = new Intent(NeatoSmartAppCreateUserActivity.this , NeatoSmartAppTestActivity.class);
				startActivity(createUserIntent);
				setResult(RESULT_OK);
				finish();
				
			}		
		}
    }
}
