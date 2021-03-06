package com.neatorobotics.android.slide.framework.pluginhelper;

//JSON data keys which will be used by javasacript to send the data in json array object.
public class JsonMapKeys {

    public static final String KEY_SUCCESS = "success";
    public static final String KEY_MESSAGE = "message";

    // Used by user plugin
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_CURRENT_PASSWORD = "currentPassword";
    public static final String KEY_NEW_PASSWORD = "newPassword";
    public static final String KEY_ALTERNATE_EMAIL = "alternate_email";
    public static final String KEY_EXTRA_PARAMS = "extra_param";
    public static final String KEY_VALIDATION_STATUS = "validation_status";

    // In the code when the country code and opt in status goes VIA plugin
    // result to
    // JS layer, camel case is used. While when it comes to the plugin layer
    // from
    // js layer, underscore separated is used. This is quite confusing.
    // But as changing the keys will impact the JS code in few places, keeping
    // it as-is.
    // We have to target making everything camel case. This will have changes in
    // couple of view models
    // and neatosmartapphelper.js.
    public static final String KEY_COUNTRYCODE = "country_code";
    public static final String KEY_COUNTRY_CODE_CAMEL_CASE = "countryCode";
    public static final String KEY_OPT_IN_CAMEL_CASE = "optIn";
    public static final String KEY_OPTIN = "opt_in";
    // Used by robot plugin
    public static final String KEY_COMMAND_ID = "commandId";
    public static final String KEY_COMMAND_PARAMETERS = "commandParams";
    public static final String KEY_ROBOT_ID = "robotId";
    public static final String KEY_ROBOT_NAME = "robotName";
    public static final String KEY_ROBOT_ONLINE_STATUS = "online";

    public static final String KEY_ROBOT_PROFILE_KEYS = "robotProfileKeys";
    public static final String KEY_ROBOT_PROFILE_DATA = "robotProfileData";
    // Cleaning API params
    public static final String KEY_CLEANING_CATEGORY = "cleaningCategory";
    public static final String KEY_SPOT_CLEANING_AREA_LENGTH = "spotCleaningAreaLength";
    public static final String KEY_SPOT_CLEANING_AREA_HEIGHT = "spotCleaningAreaHeight";

    // Used for error
    public static final String KEY_ERROR_CODE = "errorCode";
    public static final String KEY_ERROR_MESSAGE = "errorMessage";

    // Used by scheduler
    public static final String KEY_DAY = "day";
    public static final String KEY_SCHEDULE_GROUP = "scheduleGroup";
    public static final String KEY_SCHEDULE_UUID = "scheduleUUID";
    public static final String KEY_EVENTS = "events";
    public static final String KEY_START_TIME = "startTime";

    public static final String KEY_IS_SCHEDULE_ENABLED = "isScheduleEnabled";
    public static final String KEY_ENABLE_SCHEDULE = "enableSchedule";

    public static final String KEY_SCHEDULE_TYPE = "scheduleType";
    public static final String KEY_SCHEDULES = "schedules";
    public static final String KEY_SCHEDULE_ID = "scheduleId";
    public static final String KEY_SCHEDULE_EVENTS_LIST = "scheduleEventLists";
    public static final String KEY_SCHEDULE_EVENT_ID = "scheduleEventId";
    public static final String KEY_SCHEDULE_EVENT_DATA = "scheduleEventData";
    public static final String KEY_CLEANING_MODE = "cleaningMode";

    public static final String KEY_NOTIFICATION_ID = "notificationId";

    // Used by navigation APIs
    public static final String KEY_NAVIGATION_CONTROL_ID = "navigationControlId";
    public static final String KEY_FLAG_ON = "on";
    public static final String KEY_IS_PEER_CONNECTED = "isConnected";

    // Used by notifications APIs
    public static final String KEY_GLOBAL_NOTIFICATIONS = "global";
    public static final String KEY_NOTIFICATIONS = "notifications";
    public static final String KEY_NOTIFICATION_KEY = "key";
    public static final String KEY_NOTIFICATION_VALUE = "value";


    // Timed Mode
    public static final String KEY_EXPECTED_TIME_TO_EXECUTE = "expectedTimeToExecute";
    public static final String KEY_ROBOT_DATA_ID = "robotDataKeyId";
    public static final String KEY_ROBOT_DATA = "robotData";
    public static final String KEY_ROBOT_CURRENT_STATE = "robotCurrentState";
    public static final String KEY_SCHEDULE_STATE = "scheduleState";
    public static final String KEY_ROBOT_STATE_UPDATE = "robotStateUpdate";
    public static final String KEY_ROBOT_NEW_VIRTUAL_STATE = "robotNewVirtualState";

    // Drive Robot
    public static final String ERROR_DRIVE_RESPONSE_CODE = "errorDriveResponseCode";

    // Robot Error In sending Message - Id
    public static final String FAILED_COMMAND_ID = "failedCommandId";

    // Linking robot
    public static final String KEY_LINKING_CODE = "linkCode";
}
