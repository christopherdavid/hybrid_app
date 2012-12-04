package com.neatorobotics.android.slide.framework.webservice.user;

import java.lang.ref.WeakReference;

import android.content.Context;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;


public class UserManager {
	private static final String TAG = UserManager.class.getSimpleName();
	private Context mContext;

	private static UserManager sUserManager;
	private static final Object INSTANCE_LOCK = new Object();
	
	private UserManager(Context context)
	{
		mContext = context.getApplicationContext();
	}

	public static UserManager getInstance(Context context)
	{
		synchronized (INSTANCE_LOCK) {
			if (sUserManager == null) {
				sUserManager = new UserManager(context);
			}
		}

		return sUserManager;
	}




	public void getUserDetail(final String email, final String auth_token, UserDetailsListener listener)
	{
		final WeakReference<UserDetailsListener> userDetailListenerWeakRef = new WeakReference<UserDetailsListener>(listener);
		Runnable task = new Runnable() {

			public void run() {
				GetNeatoUserDetailsResult result = NeatoUserWebservicesHelper.getNeatoUserDetails(mContext, email , auth_token);
				UserDetailsListener userListener = userDetailListenerWeakRef.get();
				if (userListener == null) {
					LogHelper.logD(TAG, "Callback interface is null in getUserDetail");
					return;
				}
				if (result != null && result.success()) {
					UserItem userItem = convertUserDetailResultToUserItem(result);
					if (userListener != null) {
						userListener.onUserDetailsReceived(userItem);
					}
				}
				else {
					if (userListener != null) {
						userListener.onServerError();
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	public void loginUser(final String email, final String password, UserDetailsListener listener) {

		
		final WeakReference<UserDetailsListener> userDetailListenerWeakRef = new WeakReference<UserDetailsListener>(listener);
		Runnable task = new Runnable() {

			public void run() {
				UserItem userItem = null;
				LoginNeatoUserTokenResult result = NeatoUserWebservicesHelper.loginNeatoUserToken(mContext, email, password);
				if (result != null && result.success()) {
					String auth_token = result.mUserAuthToken;
					userItem = getUserDetail(email,auth_token);
				} 
				UserDetailsListener userListener = userDetailListenerWeakRef.get();

				if (userListener == null) {
					LogHelper.logD(TAG, "Callback interface is null in getUserDetail");
					return;
				} else {
					if (userItem == null) {
						LogHelper.logD(TAG, "User item is null");
						userListener.onServerError();
					} else {
						LogHelper.logD(TAG, "Sending user item to listener");
						LogHelper.logD(TAG, "Initialise inside User manager!");
						// myHandler.sendEmptyMessage(0);
						//InitHelper initHelper = new InitHelper(mContext);

						LogHelper.logD(TAG, "After Initialise inside User manager!");
						userListener.onUserDetailsReceived(userItem);
					}


				}
			}
		};
		TaskUtils.scheduleTask(task, 0);

	}

	public UserItem getUserDetail(final String email, final String auth_token)
	{
		UserItem userItem = null;
		GetNeatoUserDetailsResult result = NeatoUserWebservicesHelper.getNeatoUserDetails(mContext, email , auth_token);
		if (result != null && result.success()) {
			userItem = convertUserDetailResultToUserItem(result);
		}
		return userItem;
	}



	public UserItem loginUser(final String email, final String password) {
		UserItem userItem = null;
		LoginNeatoUserTokenResult response = NeatoUserWebservicesHelper.loginNeatoUserToken(mContext, email, password);
		if (response != null && response.success()) {
			String auth_token = response.mUserAuthToken;
			userItem = getUserDetail(email,auth_token);
		}
		return userItem;
	}

	public void createUser(final String username, final String email, final String password, UserDetailsListener listener) {


		final WeakReference<UserDetailsListener> userDetailListenerWeakRef = new WeakReference<UserDetailsListener>(listener);
		Runnable task = new Runnable() {

			public void run() {
				UserItem userItem = null;
				CreateNeatoUserResult result = NeatoUserWebservicesHelper.createNeatoUserRequestNative(mContext, username, email, password);
				if (result != null && result.success()) {
					String auth_token = result.mResult.mUserHandle;
					userItem = getUserDetail(email,auth_token);
				} 
				UserDetailsListener userListener = userDetailListenerWeakRef.get();

				if (userListener == null) {
					LogHelper.logD(TAG, "Callback interface is null in getUserDetail");
					return;
				} else {
					if (userItem == null) {
						LogHelper.logD(TAG, "User item is null");
						userListener.onServerError();
					} else {
						LogHelper.logD(TAG, "Sending user item to listener");
						userListener.onUserDetailsReceived(userItem);
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);

	}

	public UserItem createUser(final String username, final String email, final String password) {
		UserItem userItem = null;
		CreateNeatoUserResult result = NeatoUserWebservicesHelper.createNeatoUserRequestNative(mContext, username, email, password);
		if (result != null && result.success()) {
			String auth_token = result.mResult.mUserHandle;
			userItem = getUserDetail(email,auth_token);
		}
		return userItem;
	}

	private UserItem convertUserDetailResultToUserItem(GetNeatoUserDetailsResult result)
	{
		UserItem userItem = new UserItem();
		userItem.setId(result.mResult.mId);
		userItem.setName(result.mResult.mName);
		userItem.setEmail(result.mResult.mEmail);
		userItem.setChatId(result.mResult.mChat_id);
		userItem.setChatPwd(result.mResult.mChat_pwd);
		return userItem;
	}

}
