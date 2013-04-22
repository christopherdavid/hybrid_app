package com.neatorobotics.android.slide.framework.webservice.user;

import java.io.IOException;
import java.util.HashMap;

import android.content.Context;

import com.neatorobotics.android.slide.framework.database.RobotHelper;
import com.neatorobotics.android.slide.framework.database.UserHelper;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.utils.DeviceUtils;
import com.neatorobotics.android.slide.framework.utils.TaskUtils;
import com.neatorobotics.android.slide.framework.utils.UserAttributes;
import com.neatorobotics.android.slide.framework.webservice.NeatoServerException;
import com.neatorobotics.android.slide.framework.webservice.UserUnauthorizedException;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotAssociationDisassociationResult;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotItem;
import com.neatorobotics.android.slide.framework.webservice.robot.RobotManager;

/*
 * NOTE: Here we assume WebServiceBaseRequestListener never be NULL,
 * We can check NULL value by creating WRAPPER class, passing input 
 * listener to this WRAPPER class object and calling wrapper class methods.  
 */
public class UserManager {	
	private static final String TAG = UserManager.class.getSimpleName();
	protected Context mContext;
	
	private static UserManager sUserManager;
	private static final Object INSTANCE_LOCK = new Object();
	
	private UserManager(Context context) {
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
	
	// TODO: Need to call the Web API
	public boolean isUserValidated(final String email) {
		return true;
	}	
	
	public void getUserDetails(final String email, final String auth_token, final WebServiceBaseRequestListener listener) {		
		Runnable task = new Runnable() {
			public void run() {				
				try {
					GetNeatoUserDetailsResult result = NeatoUserWebservicesHelper.getNeatoUserDetails(mContext, email , auth_token);
					listener.onReceived(result);
				}
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void loginUser(final String email, final String password, final WebServiceBaseRequestListener listener) {
		Runnable task = new Runnable() {
			public void run() {
				try {					
					LoginNeatoUserTokenResult loginResult = NeatoUserWebservicesHelper.loginNeatoUserToken(mContext, email, password);
					GetNeatoUserDetailsResult result = NeatoUserWebservicesHelper.getNeatoUserDetails(mContext, email , loginResult.mUserAuthToken);						
					UserHelper.saveLoggedInUserDetails(mContext, result.result, loginResult.mUserAuthToken);
					setUserAttributesOnServer(loginResult.mUserAuthToken, DeviceUtils.getUserAttributes(mContext));
					listener.onReceived(result);											
				}
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);

	}
	
	public void createUser(final String username, final String email, final String password, final WebServiceBaseRequestListener listener) {
		Runnable task = new Runnable() {
			public void run() {
				try {
					CreateNeatoUserResult createUserResult = NeatoUserWebservicesHelper.createNeatoUserRequestNative(mContext, username, email, password);
					GetNeatoUserDetailsResult result = NeatoUserWebservicesHelper.getNeatoUserDetails(mContext, email , createUserResult.result.user_handle);
					UserHelper.saveLoggedInUserDetails(mContext, result.result, createUserResult.result.user_handle);
					setUserAttributesOnServer(createUserResult.result.user_handle, DeviceUtils.getUserAttributes(mContext));
					listener.onReceived(result);
				}
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void associateRobot(final String robotId, final String emailId, final WebServiceBaseRequestListener listener) {		
		Runnable task = new Runnable() {
			public void run() {
				try {
					RobotAssociationDisassociationResult result = NeatoUserWebservicesHelper.associateNeatoRobotRequest(mContext, emailId, robotId);					
					// TODO: Add the associated robot in prefs list.
					// Keep a ArrayList of robotItems.
					RobotItem robotItem = RobotManager.getInstance(mContext).getRobotDetail(robotId);
					LogHelper.log(TAG, "RobotItem = " + robotItem);
					if (robotItem != null) {
						RobotHelper.saveRobotDetails(mContext, robotItem);	
						RobotHelper.setRobotToManage(mContext, robotItem);
						LogHelper.log(TAG, "Saving robot information");
						
						listener.onReceived(result);
					}
				}
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				}	
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void disassociateRobot(final String robotId, final String emailId, final WebServiceBaseRequestListener listener) {
		Runnable task = new Runnable() {			
			public void run() {
				try {
					RobotAssociationDisassociationResult result = NeatoUserWebservicesHelper.disassociateNeatoRobotRequest(mContext, emailId, robotId);					
					// Clear robot info from the DB
					RobotHelper.clearRobotDetails(mContext, robotId);
					listener.onReceived(result);										
				}
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				}	
				
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void disassociateAllRobots(final String email, final WebServiceBaseRequestListener listener) {
		Runnable task = new Runnable() {
			public void run() {		
				try {
					RobotAssociationDisassociationResult result = NeatoUserWebservicesHelper.dissociateAllNeatoRobotsRequest(mContext, email);										
					// Clear all robots info from the DB
					RobotHelper.clearAllUserAssociatedRobots(mContext);
					listener.onReceived(result);										
				}
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void getAssociatedRobots(final String email, final String authToken, final WebServiceBaseRequestListener listener) {
		Runnable task = new Runnable() {
			public void run() {
				try {
					GetUserAssociatedRobotsResult result = NeatoUserWebservicesHelper.getUserAssociatedRobots(mContext, email, authToken);
					// Save associated robots into the DB 
					RobotHelper.clearAllUserAssociatedRobots(mContext);
					RobotHelper.saveRobotDetails(mContext, result.result);					
					listener.onReceived(result);					
				}
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void sendMessageToRobot(final String userId, final String robotId, final String message) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					NeatoUserWebservicesHelper.sendMessageToRobotRequest(mContext, userId, robotId, message);
				}
				catch (UserUnauthorizedException ex) {
					LogHelper.log(TAG, "UserUnauthorizedException in sendMessageToRobot - " + ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					LogHelper.log(TAG, "NeatoServerException in sendMessageToRobot - " + ex.getErrorMessage());
				}
				catch (IOException ex) {					
					LogHelper.log(TAG, "IOException in sendMessageToRobot", ex);
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void forgetPassword(final String email, final WebServiceBaseRequestListener listener) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					ForgetPasswordResult result = NeatoUserWebservicesHelper.forgetPasswordRequest(mContext, email);
					listener.onReceived(result);
				}
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void changePassword(final String authToken, final String currentPassword, final String newPassword, final WebServiceBaseRequestListener listener) {
		Runnable task = new Runnable() {			
			@Override
			public void run() {
				try {
					ChangePasswordResult result = NeatoUserWebservicesHelper.changePasswordRequest(mContext, authToken, currentPassword, newPassword);
					listener.onReceived(result);
				}				
				catch (UserUnauthorizedException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					listener.onServerError(ex.getErrorMessage());
				}
				catch (IOException ex) {
					listener.onNetworkError(ex.getMessage());
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
	
	public void setUserAttributesOnServer(final String authToken, final UserAttributes userAttributes) {
		Runnable task = new Runnable() {
			public void run() {				
				try {
					HashMap<String, String> attributes = new HashMap<String, String>();
					attributes.put(NeatoUserWebServicesAttributes.SetUserAttributes.Attribute.ATTRIBUTE_NAME, userAttributes.deviceName);
					attributes.put(NeatoUserWebServicesAttributes.SetUserAttributes.Attribute.ATTRIBUTE_OPERATING_SYSTEM, userAttributes.osName);
					attributes.put(NeatoUserWebServicesAttributes.SetUserAttributes.Attribute.ATTRIBUTE_VERSION, userAttributes.osVersion);
					NeatoUserWebservicesHelper.setUserAttributeRequest(mContext, authToken, attributes);
				}
				catch (UserUnauthorizedException ex) {
					LogHelper.log(TAG, "UserUnauthorizedException in setUserAttributesOnServer - " + ex.getErrorMessage());
				}
				catch (NeatoServerException ex) {
					LogHelper.log(TAG, "NeatoServerException in setUserAttributesOnServer - " + ex.getErrorMessage());
				}
				catch (IOException ex) {					
					LogHelper.log(TAG, "IOException in setUserAttributesOnServer", ex);
				}
			}
		};
		TaskUtils.scheduleTask(task, 0);
	}
}
