package com.neatorobotics.android.slide.framework.webservice.user;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;

import com.neatorobotics.android.slide.framework.database.DBHelper;
import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.prefs.NeatoPrefs;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotAssociationDisassociationResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.AssociatedRobotDetailsListener;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.UserDetailsListener;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.UserRobotAssociateDisassociateListener;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.UserWebserviceListener;


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

	public void loginUser(final String email, final String password, final UserDetailsListener listener) {

		Runnable task = new Runnable() {

			public void run() {
				UserItem userItem = null;
				LoginNeatoUserTokenResult result = NeatoUserWebservicesHelper.loginNeatoUserToken(mContext, email, password);
				if (result != null && result.success()) {
					String auth_token = result.mUserAuthToken;
					NeatoPrefs.saveNeatoUserAuthToken(mContext, auth_token);
					userItem = getUserDetail(email, auth_token);
				} 

				if (listener == null) {
					LogHelper.logD(TAG, "Callback interface is null in getUserDetail");
					return;
				} 
				else {
					if (userItem == null) {
						LogHelper.logD(TAG, "User item is null");
						listener.onServerError();
					} else {
						LogHelper.logD(TAG, "Sending user item to listener");
						LogHelper.logD(TAG, "Initialise inside User manager!");
						// myHandler.sendEmptyMessage(0);
						//InitHelper initHelper = new InitHelper(mContext);

						LogHelper.logD(TAG, "After Initialise inside User manager!");
						listener.onUserDetailsReceived(userItem);
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

	public void getUserDetails(final String email, final String auth_token, final UserDetailsListener listener) {

		Runnable task = new Runnable() {

			public void run() {
				UserItem userItem = null;
				userItem = getUserDetail(email, auth_token);
				if (listener == null) {
					LogHelper.logD(TAG, "Callback interface is null in getUserDetail");
					return;
				} 
				else {
					if (userItem == null) {
						LogHelper.logD(TAG, "User item is null");
						listener.onServerError();
					} else {
						LogHelper.logD(TAG, "Sending user item to listener");
						listener.onUserDetailsReceived(userItem);
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	public UserItem loginUser(final String email, final String password) {
		UserItem userItem = null;
		LoginNeatoUserTokenResult response = NeatoUserWebservicesHelper.loginNeatoUserToken(mContext, email, password);
		if (response != null && response.success()) {
			String auth_token = response.mUserAuthToken;
			NeatoPrefs.saveNeatoUserAuthToken(mContext, auth_token);
			userItem = getUserDetail(email,auth_token);
		}
		return userItem;
	}

	public void createUser(final String username, final String email, final String password, final UserDetailsListener listener) {

		Runnable task = new Runnable() {

			public void run() {
				UserItem userItem = null;
				CreateNeatoUserResult result = NeatoUserWebservicesHelper.createNeatoUserRequestNative(mContext, username, email, password);
				if (result != null && result.success()) {
					String auth_token = result.mResult.mUserHandle;
					NeatoPrefs.saveNeatoUserAuthToken(mContext, auth_token);
					userItem = getUserDetail(email,auth_token);
				} 

				if (listener == null) {
					LogHelper.logD(TAG, "Callback interface is null in getUserDetail");
					return;
				} 
				else {
					if (userItem == null) {
						LogHelper.logD(TAG, "User item is null");
						listener.onServerError();
					} else {
						LogHelper.logD(TAG, "Sending user item to listener");
						listener.onUserDetailsReceived(userItem);
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
			NeatoPrefs.saveNeatoUserAuthToken(mContext, auth_token);
			userItem = getUserDetail(email,auth_token);
			if (userItem != null) {
				UserHelper.saveUserDetails(mContext, userItem);
			}
		}
		return userItem;
	}

	public void associateRobot(final String robotId, final String emailId, final UserRobotAssociateDisassociateListener listener) {
		Runnable task = new Runnable() {

			public void run() {
				RobotAssociationDisassociationResult result = NeatoUserWebservicesHelper.associateNeatoRobotRequest(mContext, emailId, robotId);
				if (result != null && result.success()) {
					// TODO: Add the associated robot in prefs list.
					// Keep a ArrayList of robotItems.
					RobotItem robotItem = RobotManager.getInstance(mContext).getRobotDetail(robotId);
					LogHelper.log(TAG, "RobotItem = " + robotItem);

					if (robotItem != null) {
						RobotHelper.saveRobotDetails(mContext, robotItem);	
						RobotHelper.setRobotToManage(mContext, robotItem);
						LogHelper.log(TAG, "Saving robot information");
					}
					if (listener != null) {
						listener.onComplete();
					}
				}
				else {
					if (listener != null) {
						listener.onServerError("Server Error");
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void disassociateRobot(final String robotId, final String emailId, final UserRobotAssociateDisassociateListener listener) {

		Runnable task = new Runnable() {

			public void run() {
				RobotAssociationDisassociationResult result = NeatoUserWebservicesHelper.disassociateNeatoRobotRequest(mContext, emailId, robotId);
				if (result != null && result.success()) {
					// Clear robot info from the DB
					RobotHelper.clearRobotDetails(mContext, robotId);
					if (listener != null) {
						listener.onComplete();
					}
				}
				else {
					if (listener != null) {
						listener.onServerError("Server Error");
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void disassociateAllRobots(final String email, final UserRobotAssociateDisassociateListener listener) {
		Runnable task = new Runnable() {

			public void run() {
				TaskUtils.sleep(1000);
				// Clear all user associated robots from the DB
				RobotHelper.clearAllUserAssociatedRobots(mContext);
				if (listener != null) {
					listener.onComplete();
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	
	public void getAssociatedRobots(final String email, final String authToken, final AssociatedRobotDetailsListener listener) {
		Runnable task = new Runnable() {

			public void run() {
				GetUserAssociatedRobotsResult result = NeatoUserWebservicesHelper.getUserAssociatedRobots(mContext, email, authToken);
				if (result != null && result.success()) {
					if (listener == null) {
						LogHelper.logD(TAG, "Callback interface is null in getRobotDetails");
						return;
					} 
					else {
						LogHelper.logD(TAG, "Sending associated robot details to RobotDetailsListener");
						ArrayList<RobotItem> associatedRobots = convertAssociatedRobotDetailIntoRoboItems(result);
						
						// Save associated robots into the DB 
						RobotHelper.clearAllUserAssociatedRobots(mContext);
						RobotHelper.saveRobotDetails(mContext, associatedRobots);
						
						listener.onRobotDetailsReceived(associatedRobots);
					}
				} 
				else {
					if (listener != null) {
						listener.onNetworkError(null);
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}

	private UserItem convertUserDetailResultToUserItem(GetNeatoUserDetailsResult result)
	{
		UserItem userItem = new UserItem();
		userItem.setId(result.mResult.mId);
		userItem.setName(result.mResult.mName);
		userItem.setEmail(result.mResult.mEmail);
		userItem.setChatId(result.mResult.mChat_id);
		userItem.setChatPwd(result.mResult.mChat_pwd);
		
		for (int i = 0; i < result.mResult.mRobots.size(); i++) {
			UserAssociatedRobot associatedRobot = result.mResult.mRobots.get(i);
			
			RobotItem robotItem = new RobotItem();
			robotItem.setId(associatedRobot.mId);
			robotItem.setName(associatedRobot.mName);
			robotItem.setSerialNumber(associatedRobot.mSerialNumber);
			robotItem.setChatId(associatedRobot.mChat_id);
			userItem.addAssociatedRobot(robotItem);
		}
		
		return userItem;
	}
	
	private ArrayList<RobotItem> convertAssociatedRobotDetailIntoRoboItems(GetUserAssociatedRobotsResult result) {
		ArrayList<RobotItem> associatedRobots = new ArrayList<RobotItem>();
		
		for (int i = 0; i < result.mResults.size(); i++) {
			RobotItem robotItem = new RobotItem();
			robotItem.setId(result.mResults.get(i).mId);
			robotItem.setName(result.mResults.get(i).mName);
			robotItem.setSerialNumber(result.mResults.get(i).mSerialNumber);
			robotItem.setChatId(result.mResults.get(i).mChat_id);
			associatedRobots.add(robotItem);
		}
		
		return associatedRobots;
	}

}
