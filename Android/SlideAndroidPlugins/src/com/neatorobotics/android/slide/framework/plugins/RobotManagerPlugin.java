package com.neatorobotics.android.slide.framework.plugins;

import java.util.HashMap;
import java.util.Set;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import com.neatorobotics.android.slide.framework.logger.LogHelper;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotClearDataRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.RobotManagerRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.command.GetRobotDataRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.command.GetSpotDefinationRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.command.PauseCleaningRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.command.ResumeCleaningRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.command.SendRobotCommandRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.command.SetSpotDefinationRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.command.StartCleaningRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.command.StopCleaningRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.manual.ConnectRobotRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.manual.IsRobotPeerConnectedRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.manual.RobotDriveRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.manual.StopRobotDriveRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.notification.RegisterDataChangeNotificationRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.notification.RegisterForPushMessagesRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.notification.UnRegisterDataChangeNotificationRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.notification.UnRegisterForPushMessagesRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.profile.EnableScheduleRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.profile.GetRobotCurrentStateRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.profile.GetRobotDetailRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.profile.IsRobotScheduleEnabledRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.profile.RobotGetOnlineStatusRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.profile.SetRobotNameRequest;
import com.neatorobotics.android.slide.framework.plugins.requests.robot.schedule.RobotScheduleRequest;

public class RobotManagerPlugin extends Plugin {

    private static final String TAG = RobotManagerPlugin.class.getSimpleName();

    private final HashMap<String, RobotManagerRequest> ACTION_COMMAND_MAP = new HashMap<String, RobotManagerRequest>();

    private boolean mIsInitialized = false;

    // Profile
    private RobotManagerRequest mSetRobotNameRequest = new SetRobotNameRequest();
    private RobotManagerRequest mGetRobotDetailRequest = new GetRobotDetailRequest();
    private RobotManagerRequest mRobotGetOnlineStatusRequest = new RobotGetOnlineStatusRequest();
    private RobotManagerRequest mIsRobotScheduleEnabledRequest = new IsRobotScheduleEnabledRequest();
    private RobotManagerRequest mEnabledScheduleRequest = new EnableScheduleRequest();
    
    private RobotManagerRequest mGetRobotCurrentStateRequest = new GetRobotCurrentStateRequest();

    // Notification
    private RobotManagerRequest mRegisterForPushMessagesRequest = new RegisterForPushMessagesRequest();
    private RobotManagerRequest mUnRegisterForPushMessagesRequest = new UnRegisterForPushMessagesRequest();
    private RobotManagerRequest mRegisterDataChangeNotification = new RegisterDataChangeNotificationRequest();
    private RobotManagerRequest mUnRegisterDataChangeNotification = new UnRegisterDataChangeNotificationRequest();

    // Commands
    private RobotManagerRequest mStartCleaningRequest = new StartCleaningRequest();
    private RobotManagerRequest mStopCleaningRequest = new StopCleaningRequest();
    private RobotManagerRequest mResumeCleaningRequest = new ResumeCleaningRequest();
    private RobotManagerRequest mPauseCleaningRequest = new PauseCleaningRequest();

    private RobotManagerRequest mSendRobotCommandRequest = new SendRobotCommandRequest();
    private RobotManagerRequest mSetSpotDefinationRequest = new SetSpotDefinationRequest();
    private RobotManagerRequest mGetSpotDefinationRequest = new GetSpotDefinationRequest();
    private RobotManagerRequest mGetRobotDataRequest = new GetRobotDataRequest();
    // Manual Commands
    private RobotManagerRequest mConnectRobotRequest = new ConnectRobotRequest();
    private RobotManagerRequest mIsRobotPeerConnectedRequest = new IsRobotPeerConnectedRequest();
    private RobotManagerRequest mRobotDriveRequest = new RobotDriveRequest();
    private RobotManagerRequest mStopRobotDriveRequest = new StopRobotDriveRequest();
    
    private RobotManagerRequest mRobotClearDataRequest = new RobotClearDataRequest();

    // Schedule
    private RobotManagerRequest mRobotScheduleRequest = new RobotScheduleRequest();

    void initializeIfRequired() {
        // If we add more action type, please ensure to add it into the
        // ACTION_COMMAND_MAP
        if (!mIsInitialized) {
            ACTION_COMMAND_MAP.put(ActionTypes.SET_ROBOT_NAME_2, mSetRobotNameRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.GET_ROBOT_DETAIL, mGetRobotDetailRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.GET_ROBOT_ONLINE_STATUS, mRobotGetOnlineStatusRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.IS_SCHEDULE_ENABLED, mIsRobotScheduleEnabledRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.ENABLE_SCHEDULE, mEnabledScheduleRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.SET_SPOT_DEFINITION, mSetSpotDefinationRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.GET_SPOT_DEFINITION, mGetSpotDefinationRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.GET_ROBOT_CURRENT_STATE, mGetRobotCurrentStateRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.REGISTER_FOR_ROBOT_MESSAGES, mRegisterForPushMessagesRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.UNREGISTER_FOR_ROBOT_MESSAGES, mUnRegisterForPushMessagesRequest);

            ACTION_COMMAND_MAP.put(ActionTypes.REGISTER_ROBOT_NOTIFICATIONS2, mRegisterDataChangeNotification);
            ACTION_COMMAND_MAP.put(ActionTypes.UNREGISTER_ROBOT_NOTIFICATIONS2, mUnRegisterDataChangeNotification);

            ACTION_COMMAND_MAP.put(ActionTypes.START_CLEANING, mStartCleaningRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.STOP_CLEANING, mStopCleaningRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.PAUSE_CLEANING, mPauseCleaningRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.RESUME_CLEANING, mResumeCleaningRequest);

            ACTION_COMMAND_MAP.put(ActionTypes.DRIVE_ROBOT, mRobotDriveRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.DIRECT_CONNECT_TO_ROBOT, mConnectRobotRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.STOP_ROBOT_DRIVE, mStopRobotDriveRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.IS_ROBOT_PEER_CONNECTED, mIsRobotPeerConnectedRequest);

            ACTION_COMMAND_MAP.put(ActionTypes.SEND_COMMAND_TO_ROBOT2, mSendRobotCommandRequest);

            ACTION_COMMAND_MAP.put(ActionTypes.GET_SCHEDULE_DATA, mRobotScheduleRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.GET_SCHEDULE_EVENTS, mRobotScheduleRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.ADD_ROBOT_SCHEDULE_EVENT, mRobotScheduleRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.GET_SCHEDULE_EVENT_DATA, mRobotScheduleRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.UPDATE_ROBOT_SCHEDULE_EVENT, mRobotScheduleRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.DELETE_ROBOT_SCHEDULE_EVENT, mRobotScheduleRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.UPDATE_SCHEDULE, mRobotScheduleRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.CREATE_SCHEDULE, mRobotScheduleRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.CLEAR_ROBOT_DATA, mRobotClearDataRequest);
            ACTION_COMMAND_MAP.put(ActionTypes.GET_ROBOT_DATA, mGetRobotDataRequest);

            Set<String> keys = ACTION_COMMAND_MAP.keySet();
            for (String key : keys) {
                RobotManagerRequest robotCommand = getRobotRequest(key);
                robotCommand.initalize(cordova.getActivity(), this);
            }
            mIsInitialized = true;
        }
    }

    @Override
    public PluginResult execute(final String action, final JSONArray data, final String callbackId) {

        initializeIfRequired();

        if (!isValidAction(action)) {
            LogHelper.logD(TAG, "Action is not a valid action. Action = " + action);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.INVALID_ACTION);
            return pluginResult;
        }

        LogHelper.logD(TAG, "RobotManagerPlugin execute with action :" + action);
        LogHelper.logD(TAG, "\tdata :" + data);

        RobotManagerRequest robotRequest = getRobotRequest(action);
        if (robotRequest != null) {
            robotRequest.execute(action, data, callbackId);
        }

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        return pluginResult;
    }

    // Private helper method to check, if we support the action
    // returns true, if we support the action, false otherwise
    private boolean isValidAction(String action) {
        return (ACTION_COMMAND_MAP.containsKey(action));
    }

    private RobotManagerRequest getRobotRequest(final String action) {
        return ACTION_COMMAND_MAP.get(action);
    }

    private static class ActionTypes {
        public static final String SEND_COMMAND_TO_ROBOT2 = "sendCommandToRobot2";
        public static final String GET_ROBOT_DETAIL = "getRobotDetail";
        public static final String SET_ROBOT_NAME_2 = "setRobotName2";
        public static final String GET_ROBOT_ONLINE_STATUS = "getRobotOnlineStatus";
        public static final String UPDATE_SCHEDULE = "updateSchedule";
        public static final String DELETE_ROBOT_SCHEDULE_EVENT = "deleteScheduleEvent";
        public static final String UPDATE_ROBOT_SCHEDULE_EVENT = "updateScheduleEvent";
        public static final String GET_SCHEDULE_EVENT_DATA = "getScheduleEventData";
        public static final String ADD_ROBOT_SCHEDULE_EVENT = "addScheduleEventData";
        public static final String GET_SCHEDULE_EVENTS = "getScheduleEvents";
        public static final String GET_SCHEDULE_DATA = "getScheduleData";
        public static final String CREATE_SCHEDULE = "createSchedule";
        public static final String SET_SPOT_DEFINITION = "setSpotDefinition";
        public static final String GET_SPOT_DEFINITION = "getSpotDefinition";
        public static final String START_CLEANING = "startCleaning";
        public static final String STOP_CLEANING = "stopCleaning";
        public static final String PAUSE_CLEANING = "pauseCleaning";
        public static final String RESUME_CLEANING = "resumeCleaning";
        public static final String DRIVE_ROBOT = "driveRobot";
        public static final String IS_SCHEDULE_ENABLED = "isScheduleEnabled";
        public static final String ENABLE_SCHEDULE = "enableSchedule";
        public static final String REGISTER_FOR_ROBOT_MESSAGES = "registerForRobotMessges";
        public static final String UNREGISTER_FOR_ROBOT_MESSAGES = "unregisterForRobotMessages";
        public static final String REGISTER_ROBOT_NOTIFICATIONS2 = "registerRobotNotifications2";
        public static final String UNREGISTER_ROBOT_NOTIFICATIONS2 = "unregisterRobotNotifications2";
        public static final String DIRECT_CONNECT_TO_ROBOT = "directConnectToRobot";
        public static final String STOP_ROBOT_DRIVE = "stopRobotDrive";
        public static final String IS_ROBOT_PEER_CONNECTED = "isRobotPeerConnected";
        public static final String CLEAR_ROBOT_DATA = "clearRobotData";
        public static final String GET_ROBOT_CURRENT_STATE = "getRobotCurrentState";
        public static final String GET_ROBOT_DATA = "getRobotData";

    }

}
