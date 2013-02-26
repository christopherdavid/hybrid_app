package com.neatorobotics.android.slide.framework.webservice.user;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import com.neatorobotics.android.slide.framework.AppConstants;
import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.DeviceUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.utils.UserAttributes;
import com.neatorobotics.android.slide.framework.webservice.NeatoWebserviceHelper;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotAssociationDisassociationResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.AssociatedRobotDetailsListener;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.UserDetailsListener;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.UserForgetPasswordListener;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.UserRobotAssociateDisassociateListener;
import com.neatorobotics.android.slide.framework.webservice.user.listeners.UserPasswordChangeListener;


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
					userItem = getUserDetail(email, auth_token);
				} 

				if (listener == null) {
					LogHelper.logD(TAG, "Callback interface is null in getUserDetail");
					return;
				} 
				else {
					if (userItem == null) {
						LogHelper.logD(TAG, "User item is null");
						if (result != null) {
							listener.onServerError(result.mMessage);
						}
						else {
							listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);
						}
					} 
					else {
						LogHelper.logD(TAG, "Sending user item to listener");
						LogHelper.logD(TAG, "Initialise inside User manager!");
						// myHandler.sendEmptyMessage(0);
						//InitHelper initHelper = new InitHelper(mContext);						
						
						UserHelper.saveLoggedInUserDetails(mContext, userItem, result.mUserAuthToken);

						LogHelper.logD(TAG, "After Initialise inside User manager!");
						listener.onUserDetailsReceived(userItem);
					}
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);

	}
	
	public void setUserAttributesOnServer(final String authToken, final UserAttributes userAttributes) {

		Runnable task = new Runnable() {
			public void run() {
				HashMap<String, String> attributes = new HashMap<String, String>();
				attributes.put(NeatoUserWebServicesAttributes.SetUserAttributes.Attribute.ATTRIBUTE_NAME, userAttributes.deviceName);
				attributes.put(NeatoUserWebServicesAttributes.SetUserAttributes.Attribute.ATTRIBUTE_OPERATING_SYSTEM, userAttributes.osName);
				attributes.put(NeatoUserWebServicesAttributes.SetUserAttributes.Attribute.ATTRIBUTE_VERSION, userAttributes.osVersion);
				NeatoUserWebservicesHelper.setUserAttributeRequest(mContext, authToken, attributes);
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
						listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);
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
			userItem = getUserDetail(email,response.mUserAuthToken);
			if (userItem != null) {
				UserHelper.saveLoggedInUserDetails(mContext, userItem, response.mUserAuthToken);
			}
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
					userItem = getUserDetail(email,auth_token);
				} 

				if (listener == null) {
					LogHelper.logD(TAG, "Callback interface is null in getUserDetail");
					return;
				} 
				else {
					if (userItem == null) {
						LogHelper.logD(TAG, "User item is null");
						if (result != null) {
							listener.onServerError(result.mMessage);
						}
						else {
							listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);
						}
					} else {
						LogHelper.logD(TAG, "Sending user item to listener");					
						UserHelper.saveLoggedInUserDetails(mContext, userItem, result.mResult.mUserHandle);
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
			userItem = getUserDetail(email,auth_token);
			if (userItem != null) {				
				UserHelper.saveLoggedInUserDetails(mContext, userItem, auth_token);
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
				RobotAssociationDisassociationResult result = NeatoUserWebservicesHelper.dissociateAllNeatoRobotsRequest(mContext, email);
				if (result != null) {
					if (result.success()) {
						// Clear all robots info from the DB
						RobotHelper.clearAllUserAssociatedRobots(mContext);
						
						if (listener != null) {
							listener.onComplete();
						}					
					}
					else {
						if (listener != null) {
							listener.onServerError(result.mMessage);
						}
					}
				}
				else {
					if (listener != null) {
						listener.onNetworkError(AppConstants.NETWORK_ERROR_STRING);
					}
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

	public void sendMessageToRobot(final String userId, final String robotId, final String message) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				NeatoUserWebservicesHelper.sendMessageToRobotRequest(mContext, userId, robotId, message);
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void forgetPassword(final String email, final UserForgetPasswordListener listener) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				ForgetPasswordResult result = NeatoUserWebservicesHelper.forgetPasswordRequest(mContext, email);
				if (result.success()) {
					listener.onComplete();
				} else {
					listener.onServerError(result.mStatus, result.mMessage);
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void changePassword(final String authToken, final String currentPassword, final String newPassword, final UserPasswordChangeListener listener) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				ChangePasswordResult result = NeatoUserWebservicesHelper.changePasswordRequest(mContext, authToken, currentPassword, newPassword);
				if (result.success()) {
					listener.onComplete();
				} else {
					listener.onServerError(result.mStatus, result.mMessage);
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
