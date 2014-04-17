package com.neatorobotics.android.slide.framework.plugins;

import java.util.HashMap;
import java.util.Set;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.plugins.requests.user.AssociateRobotRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.ChangePasswordRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.CreateUserRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.DisassociateAllRobotsRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.DisassociateRobotRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.ForgotPasswordRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.GetAssociatedRobotsRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.GetNotificationSettingsRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.GetUserDetailsRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.LinkRobotRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.IsNotificationEnabledRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.IsUserLoggedInRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.IsUserValidatedRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.LoginUserRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.LogoutUserRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.ResendValidationMailRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.SetUserAccountDetailsRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.TurnNotificationOnOffRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.user.UserManagerRequest;

public class UserManagerPlugin extends Plugin {

    private static final String TAG = UserManagerPlugin.class.getSimpleName();

    private boolean mIsInitialized = false;

    private UserManagerRequest mLoginUserRequest = new LoginUserRequest();
    private UserManagerRequest mCreateUserRequest = new CreateUserRequest(
            CreateUserRequest.CREATE_USER_WITH_AUTH_EXTRA_PARAM);
    private UserManagerRequest mCreateUserRequestWithAuth = new CreateUserRequest(
            CreateUserRequest.CREATE_USER_WITH_AUTH);
    private UserManagerRequest mCreateUserRequestWithoutAuth = new CreateUserRequest(
            CreateUserRequest.CREATE_USER_WITHOUT_AUTH);
    private UserManagerRequest mIsUserValidatedRequest = new IsUserValidatedRequest();
    private UserManagerRequest mResendValidationMailRequest = new ResendValidationMailRequest();
    private UserManagerRequest mLogoutUserRequest = new LogoutUserRequest();
    private UserManagerRequest mGetUserDetailsRequest = new GetUserDetailsRequest();
    private UserManagerRequest mAssociateRobotRequest = new AssociateRobotRequest();
    private UserManagerRequest mLinkRobotRequest = new LinkRobotRequest();
    private UserManagerRequest mDisassociateRobotRequest = new DisassociateRobotRequest();
    private UserManagerRequest mIsUserLoggedInRequest = new IsUserLoggedInRequest();
    private UserManagerRequest mGetAssociatedRobotsRequest = new GetAssociatedRobotsRequest();
    private UserManagerRequest mDisassociateAllRobotsRequest = new DisassociateAllRobotsRequest();
    private UserManagerRequest mForgotPasswordRequest = new ForgotPasswordRequest();
    private UserManagerRequest mChangePasswordRequest = new ChangePasswordRequest();
    private UserManagerRequest mTurnNotificationOnOffRequest = new TurnNotificationOnOffRequest();
    private UserManagerRequest mIsNotificationEnabledRequest = new IsNotificationEnabledRequest();
    private UserManagerRequest mGetNotificationSettingsRequest = new GetNotificationSettingsRequest();
    private UserManagerRequest mSetUserAccountDetailsRequest = new SetUserAccountDetailsRequest();

    private final HashMap<String, UserManagerRequest> ACTION_COMMAND_MAP = new HashMap<String, UserManagerRequest>();

    void initializeIfRequired() {
        // If we add more action type, please ensure to add it into the
        // ACTION_MAP
        if (!mIsInitialized) {
            ACTION_COMMAND_MAP.put(ActionTypes.LOGIN, mLoginUserRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.CREATE_USER, mCreateUserRequestWithoutAuth);
            ACTION_COMMAND_MAP.put(ActionTypes.CREATE_USER2, mCreateUserRequestWithAuth);
            ACTION_COMMAND_MAP.put(ActionTypes.CREATE_USER3, mCreateUserRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.IS_USER_VALIDATED, mIsUserValidatedRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.RESEND_VALIDATION_MAIL, mResendValidationMailRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.LOGOUT, mLogoutUserRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.GET_USER_DETAILS, mGetUserDetailsRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.ASSOCIATE_ROBOT, mAssociateRobotRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.INITIATE_LINK_ROBOT, mLinkRobotRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.DISASSOCIATE_ROBOT, mDisassociateRobotRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.IS_USER_LOGGEDIN, mIsUserLoggedInRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.GET_ASSOCIATED_ROBOTS, mGetAssociatedRobotsRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.DISASSOCAITE_ALL_ROBOTS, mDisassociateAllRobotsRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.FORGET_PASSWORD, mForgotPasswordRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.CHANGE_PASSWORD, mChangePasswordRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.TURN_NOTIFICATION_ON_OFF, mTurnNotificationOnOffRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.IS_NOTIFICATION_ENABLED, mIsNotificationEnabledRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.GET_NOTIFICATION_SETTINGS, mGetNotificationSettingsRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.SET_USERACCOUNT_DETAILS, mSetUserAccountDetailsRequest);
            Set<String> keys = ACTION_COMMAND_MAP.keySet();
            for (String key : keys) {
                UserManagerRequest userCommand = ACTION_COMMAND_MAP.get(key);
                userCommand.initalize(cordova.getActivity(), this);
            }
            mIsInitialized = true;
        }
    }

    @Override
    public PluginResult execute(final String action, final JSONArray data, final String callbackId) {

        LogHelper.logD(TAG, "UserManagerPlugin execute with action :" + action);

        initializeIfRequired();

        if (!isValidAction(action)) {
            LogHelper.logD(TAG, "Action is not a valid action. Action = " + action);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.INVALID_ACTION);
            return pluginResult;
        }

        UserManagerRequest userCommand = ACTION_COMMAND_MAP.get(action);
        userCommand.execute(data, callbackId);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        return pluginResult;
    }

    // Private helper method to check, if we support the action
    // returns true, if we support the action, false otherwise
    private boolean isValidAction(String action) {
        return ACTION_COMMAND_MAP.containsKey(action);
    }

    private static class ActionTypes {
        public static final String LOGIN = "login";
        public static final String CREATE_USER = "createUser";
        public static final String CREATE_USER2 = "createUser2";
        public static final String CREATE_USER3 = "createUser3";
        public static final String RESEND_VALIDATION_MAIL = "resendValidationMail";
        public static final String IS_USER_VALIDATED = "isUserValidated";
        public static final String LOGOUT = "logout";
        public static final String IS_USER_LOGGEDIN = "isLoggedIn";
        public static final String GET_USER_DETAILS = "getUserDetails";
        public static final String ASSOCIATE_ROBOT = "associateRobot";
        public static final String INITIATE_LINK_ROBOT = "tryLinkingToRobot";
        public static final String GET_ASSOCIATED_ROBOTS = "getAssociatedRobots";
        public static final String DISASSOCIATE_ROBOT = "disassociateRobot";
        public static final String DISASSOCAITE_ALL_ROBOTS = "disassociateAllRobots";
        public static final String FORGET_PASSWORD = "forgetPassword";
        public static final String CHANGE_PASSWORD = "changePassword";
        public static final String TURN_NOTIFICATION_ON_OFF = "turnNotificationOnOff";
        public static final String IS_NOTIFICATION_ENABLED = "isNotificationEnabled";
        public static final String GET_NOTIFICATION_SETTINGS = "getNotificationSettings";
        public static final String SET_USERACCOUNT_DETAILS = "setUserAccountDetails";
    }
}
