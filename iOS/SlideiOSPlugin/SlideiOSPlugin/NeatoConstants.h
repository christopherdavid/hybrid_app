#import <Foundation/Foundation.h>


#define ACCOUNT_TYPE_NATIVE @"Native"
#define ACCOUNT_TYPE_FACEBOOK @"Facebook"

#define DEMO_USER_EMAIL  @"demo1@demo.com"
#define DEMO_USER_PASSWORD  @"demo123"

#define UDP_SMART_APPS_BROADCAST_PORT 12346
#define TIME_BEFORE_SOCKET_CLOSES 5

#define TCP_ROBOT_SERVER_SOCKET_PORT 4444
#define TCP_ROBOT_SERVER_SOCKET_PORT2 49001

#define USER_HANDLE @"user_handle"

#define NEATO_RESPONSE_STATUS @"status"
#define NEATO_RESPONSE_MESSAGE @"message"
#define NEATO_RESPONSE_RESULT @"result"
#define NEATO_RESPONSE_XML_DATA_URL @"xml_data_url"
#define NEATO_RESPONSE_SUCCESS @"success"
#define NEATO_ROBOT_ONLINE_STATUS @"online"
#define NEATO_VALIDATION_STATUS @"validation_status"
#define NEATO_PROFILE_DETAILS @"profile_details"
#define NEATO_RESPONSE_EXTRA_PARAMS @"extra_params"
#define NEATO_SCHEDULE_ADVANCE @"Advanced"
#define NEATO_SCHEDULE_BASIC @"Basic"
#define NEATO_RESPONSE_EXPECTED_TIME @"expected_time"
#define NEATO_SCHEDULE_ADVANCE_INT 1
#define NEATO_SCHEDULE_BASIC_INT 0
#define NEATO_RESPONSE_EXTRA_PARAM @"extra_param"
#define NEATO_RESPONSE_EXPIRY_TIME @"expiry_time"
#define NEATO_RESPONSE_SERIAL_NUMBER @"serial_number"
#define NEATO_RESPONSE_CURRENT_STATE_DETAILS @"robotCurrentStateDetails"
#define NEATO_RESPONSE_CLEANING_CATEGORY @"robotCleaningCategory"
#define NEATO_RESPONSE_ROBOT_STATE_PARAMS @"robotStateParams"
#define NEATO_RESPONSE_PROFILE_DETAILS @"profile_details"

#define FIND_NEARBY_ROBOTS_BIND_PORT 48001
#define GET_IP_OF_SELECTED_ROBOT_FIND_PORT 48002
#define UDP_SMART_APPS_BROADCAST_NEW_SEND_PORT 48003

#define FIND_ROBOTS_COMMAND 5001
#define GET_ROBOT_IP_COMMAND 5001

#define SMART_APP_ERROR_DOMAIN @"NeatoSmartApp"

#define NEATO_RESPONSE_GRID_ID @"id_grid"
#define NEATO_RESPONSE_GRID_VERSION @"version"
#define NEATO_RESPONSE_GRID_BLOB_FILE_URL @"blob_data_file_name"

#define NEATO_STATUS_SUCCESS 0
#define NEATO_STATUS_ERROR  -1
#define KEY_NEATO_SERVER_ERROR @"error"
#define KEY_NEATO_SERVER_ERROR_CODE @"code"

#define NEATO_KEY_APP_VERSION @"appVersion"
#define NEATO_KEY_LIB_VERSION @"libVersion"
#define NEATO_KEY_SERVER_USED @"serverUsed"
#define NEATO_KEY_SCHEDULE_ENABLED @"enable_basic_schedule"
#define NEATO_KEY_SCHEDULE_ENABLED_2 @"enable_basic_schedule2"

#define PUSH_NOTIFICATION_CUSTOM_DATA_KEY   @"raw_data"

#define TAG_FIND_ROBOT_COMMAND 9001

#define STRING_TRUE @"true"
#define STRING_FALSE @"false"

// Notification constants
// Total notifications options:4 - global, robot stuck, needs clean and cleaning done.
#define TOTAL_NOTIFICATION_OPTIONS 4
#define KEY_NOTIFICATIONS @"notifications"
#define NOTIFICATION_ID_GLOBAL @"global"
#define NOTIFICATION_ID_ROBOT_STUCK @"101"
#define NOTIFICATION_ID_NEEDS_CLEAN @"102"
#define NOTIFICATON_ID_CLEANING_DONE @"103"
#define KEY_NOTIFICATION_KEY @"key"
#define KEY_NOTIFICATION_VALUE @"value"

// Command constants
#define COMMAND_START_ROBOT                 101
#define COMMAND_STOP_ROBOT                  102
#define COMMAND_PAUSE_CLEANING              107
#define COMMAND_SET_ROBOT_TIME              110
#define COMMAND_ENABLE_DISABLE_SCHEDULE     108
#define COMMAND_SEND_TO_BASE                104
#define COMMAND_RESUME_CLEANING             114
#define COMMAND_TURN_WIFI_ONOFF             117
#define COMMAND_DRIVE_ROBOT                 115
#define COMMAND_TURN_MOTOR_ONOFF            116
#define COMMAND_INTEND_TO_DRIVE             119

// Cleaning API params
#define KEY_CLEANING_CATEGORY @"cleaningCategory"
#define  KEY_SPOT_CLEANING_AREA_LENGTH @"spotCleaningAreaLength"
#define KEY_SPOT_CLEANING_AREA_HEIGHT @"spotCleaningAreaHeight"

// Cleaning Category
#define CLEANING_CATEGORY_MANUAL 1
#define CLEANING_CATEGORY_ALL 2
#define CLEANING_CATEGORY_SPOT 3

#define DEFAULT_SPOT_CLEANING_LENGTH 5
#define DEFAULT_SPOT_CLEANING_HEIGHT 3

// Timed Mode
#define TIMED_MODE_ENABLED 1
#define NOTIFICATION_FLAG_FALSE 0
#define NOTIFICATION_FLAG_TRUE 1

// Command expiry constants
#define COMMAND_EXPIRY_TIME 60

// Notification constants
#define NOTIFICATION_XMPP_DATA_CHANGE @"com.neato.plugin.xmppchange.robotDataChanged"
#define KEY_XMPP_MESSAGE @"XMPPMessage"
#define KEY_UI_UPDATE_DATA @"UIUpdateData"
#define KEY_CALLBACK_ID @"callBackId"
#define SUCCESS_CALLBACK @"successCallback"

// Robot TCP disconnection constants
#define NOTIFICATION_TCP_DISCONNECTION @"com.neato.plugin.tcp.disconnection"
#define KEY_DISCONNECTION_ERROR @"error"
#define KEY_TCP_FORCED_DISCONNECTED @"isForcedDisconnected"


// TODO:5001 is used at other places as well so confirm if it won't create a
// conflict.
#define COMMAND_ROBOT_PROFILE_DATA_CHANGED 5001


// Profile Detail keys
#define KEY_ROBOT_CURRENT_STATE @"robotCurrentState"
#define KEY_ROBOT_NEW_VIRTUAL_STATE @"robotNewVirtualState"
#define KEY_ROBOT_CLEANING_COMMAND @"cleaningCommand"
#define KEY_ROBOT_STATE_UPDATE @"robotStateUpdate"
#define KEY_SERIAL_NUMBER @"serial_number"
#define KEY_VALUE @"value"
#define KEY_TIMESTAMP @"timestamp"
#define KEY_NAME @"name"
#define KEY_ROBOT_NAME @"robotName"
#define KEY_ENABLE_BASIC_SCHEDULE @"enable_basic_schedule"
#define KEY_SCHEDULE_STATE @"scheduleState"
#define KEY_SCHEDULE_TYPE @"scheduleType"
#define KEY_ROBOT_SCHEDULE_UPDATED @"schedule_updated"
#define KEY_INTEND_TO_DRIVE @"intend_to_drive"
#define KEY_AVAILABLE_TO_DRIVE @"available_to_drive"
#define KEY_TURN_VACUUM_ONOFF @"vacuum_onoff"
#define KEY_TURN_WIFI_ONOFF @"wifi_onoff"
#define KEY_SERVER_COUNTRY_CODE @"country_code"
#define KEY_SERVER_OPT_IN @"opt_in"
#define KEY_ROBOT_CURRENT_STATE_DETAILS @"robotCurrentStateDetails"
#define KEY_ROBOT_NOTIFICATION_MESSAGE @"robotNotificationMsg"
#define KEY_ROBOT_ERROR_MESSAGE @"robotErrorMsg"
#define KEY_ROBOT_NOTIFICATION @"robotNotification"
#define KEY_ROBOT_ERROR @"robotError"
#define KEY_ROBOT_ONLINE_STATUS_DATA @"robotOnlineStatus"
#define KEY_FAILED_COMMAND_ID @"failedCommandId"

//Key Codes for profile data changes.
#define ROBOT_CURRENT_STATE_CHANGED_CODE 4001
#define ROBOT_STATE_UPDATE_CODE 4003
#define ROBOT_NAME_UPDATE 4004
#define ROBOT_SCHEDULE_STATE_CHANGED 4005
#define ROBOT_HAS_SCHEDULE_UPDATED 4006
#define ROBOT_IS_CONNECTED 4007
#define ROBOT_IS_DISCONNECTED 4008
#define ROBOT_ERROR_IN_CONNECTING 4009
#define ROBOT_NOTIFICATION_CODE 4013
#define ROBOT_ERROR_CODE 4014
#define ROBOT_ONLINE_STATUS_CHANGED_CODE 4015
#define ROBOT_COMMAND_FAILED  4016

#define KEY_ROBOT_ID @"robotId"
#define KEY_ROBOT_DATA_ID @"robotDataKeyId"
#define KEY_ROBOT_DATA @"robotData"

// Send command request constants.
#define KEY_COMMAND_ID @"commandId"
#define KEY_XML_COMMAND @"xmlCommand"

#define KEY_CHAT_ID @"chatId"
#define KEY_CAUSE_AGENT_ID @"causeAgentId"

// Drive robot constants.
#define KEY_DEVICE_ID @"device_id"
#define KEY_ROBOT_WIFI_ON_TIME_IN_MS @"wifi_on_time_ms"
#define KEY_NAVIGATION_CONTROL_ID @"navigationControlId"
#define KEY_IS_CONNECTED @"isConnected"
#define KEY_DRIVE_AVAILABLE_STATUS @"driveAvailableStatus"
#define KEY_ROBOT_IP_ADDRESS @"robotIpAddress"
#define KEY_ERROR_DRIVE_REASON_CODE @"errorDriveReasonCode"
#define ERROR_DRIVE_RESPONSE_CODE @"errorDriveResponseCode"

#define KEY_FORCED_DISCONNECTED @"forcedDisconnected"

// Request completion block
typedef void (^RequestCompletionBlockDictionary)(NSDictionary *result, NSError *error);

#define ENABLE_DEBUGGING
// To switch to Vorwerk's server, uncomment appropriate server type
// #define SERVER_TYPE_NEATO_STAGING
// #define SERVER_TYPE_NEATO_DEV

// #define SERVER_TYPE_VORWERK_STAGING
// #define SERVER_TYPE_VORWERK_DEV

// #define SERVER_TYPE_RAJATOGO_STAGING
// #define SERVER_TYPE_RAJATOGO_DEV


// #define SERVER_TYPE_VORWERK_BETA

#ifdef ENABLE_DEBUGGING
#define LOGGING_ENABLED
#define ENABLE_DB_CREATION_IN_DOCUMENTS_DIR
#endif

#define NOTIFICATION_PROFILE_TYPE_DISTRIBUTION                  @"DIST"
#define NOTIFICATION_PROFILE_TYPE_DEVELOPER                     @"DEV"


#ifdef PROFILE_DISTRIBUTION
#define NOTIFICATION_SERVER_TYPE  NOTIFICATION_PROFILE_TYPE_DISTRIBUTION
#else 
#define NOTIFICATION_SERVER_TYPE  NOTIFICATION_PROFILE_TYPE_DEVELOPER
#endif

#define SERVER_TYPE                 @"Staging (RAJATOGO)"
#define XMPP_SERVER_ADDRESS         @"rajatogo.com"
// Will use API's at http://neatostaging.rajatogo.com/wstest/
#define BASE_URL @"http://neatostaging.rajatogo.com/api/rest/json"
#define API_KEY @"1e26686d806d82144a71ea9a99d1b3169adaad917"

#ifdef SERVER_TYPE_VORWERK_BETA
// First undefine existing constants
#undef SERVER_TYPE
#undef XMPP_SERVER_ADDRESS
#undef BASE_URL
#undef API_KEY

#define SERVER_TYPE                 @"Beta Server (Vorwerk)"
#define XMPP_SERVER_ADDRESS         @"server-01.fut.emea.vr200.ksecosys.net"
// Will use API's at http://server-01.fut.emea.vr200.ksecosys.net/wstest/
#define BASE_URL @"https://server-01.fut.emea.vr200.ksecosys.net/api/rest/json"
#define API_KEY @"1e26686d806d82144a71ea9a99d1b3169adaad917"

#endif

#ifdef SERVER_TYPE_NEATO_STAGING
// First undefine existing constants
#undef SERVER_TYPE
#undef XMPP_SERVER_ADDRESS
#undef BASE_URL
#undef API_KEY

#define SERVER_TYPE                 @"Staging (Neato)"
#define XMPP_SERVER_ADDRESS         @"staging-smartapp.neatorobotics.com"
// Will use API's at https://staging-smartapp.neatorobotics.com/wstest/
#define BASE_URL @"https://staging-smartapp.neatorobotics.com/api/rest/json"
#define API_KEY @"1e26686d806d82144a71ea9a99d1b3169adaad917"
#endif


#ifdef SERVER_TYPE_NEATO_DEV

// First undefine existing constants
#undef SERVER_TYPE
#undef XMPP_SERVER_ADDRESS
#undef BASE_URL
#undef API_KEY

#define SERVER_TYPE                 @"Development (Neato)"
#define XMPP_SERVER_ADDRESS         @"rajatogo.com"
// Will use API's at http://neatodev.rajatogo.com/wstest/
#define BASE_URL @"http://neatodev.rajatogo.com/api/rest/json"
#define API_KEY @"1e26686d806d82144a71ea9a99d1b3169adaad917"
#endif

#ifdef SERVER_TYPE_VORWERK_STAGING

// First undefine existing constants
#undef SERVER_TYPE
#undef XMPP_SERVER_ADDRESS
#undef BASE_URL
#undef API_KEY

#define SERVER_TYPE                 @"Staging (Vorwerk)"
#define XMPP_SERVER_ADDRESS         @"rajatogo.com"
// Will use API's at http://neatostaging.rajatogo.com/wstest/
#define BASE_URL @"http://neatostaging.rajatogo.com/api/rest/json"
#define API_KEY @"1e26686d806d82144a71ea9a99d1b3169adaad917"
#endif


#ifdef SERVER_TYPE_VORWERK_DEV

// First undefine existing constants
#undef SERVER_TYPE
#undef XMPP_SERVER_ADDRESS
#undef BASE_URL
#undef API_KEY

#define SERVER_TYPE                 @"Development (Vorwerk)"
#define XMPP_SERVER_ADDRESS         @"rajatogo.com"
// Will use API's at http://neatodev.rajatogo.com/wstest/
#define BASE_URL @"http://neatodev.rajatogo.com/api/rest/json"
#define API_KEY @"1e26686d806d82144a71ea9a99d1b3169adaad917"
#endif


#ifdef SERVER_TYPE_RAJATOGO_STAGING

// First undefine existing constants
#undef SERVER_TYPE
#undef XMPP_SERVER_ADDRESS
#undef BASE_URL
#undef API_KEY

#define SERVER_TYPE                 @"Staging (RAJATOGO)"
#define XMPP_SERVER_ADDRESS         @"rajatogo.com"
// Will use API's at http://neatostaging.rajatogo.com/wstest/
#define BASE_URL @"http://neatostaging.rajatogo.com/api/rest/json"
#define API_KEY @"1e26686d806d82144a71ea9a99d1b3169adaad917"
#endif



#ifdef SERVER_TYPE_RAJATOGO_DEV

// First undefine existing constants
#undef SERVER_TYPE
#undef XMPP_SERVER_ADDRESS
#undef BASE_URL
#undef API_KEY

#define SERVER_TYPE                 @"Development (RAJATOGO)"
#define XMPP_SERVER_ADDRESS         @"neatodev.rajatogo.com"
// Will use API's at http://neatodev.rajatogo.com/wstest/
#define BASE_URL @"http://neatodev.rajatogo.com/api/rest/json"
#define API_KEY @"1e26686d806d82144a71ea9a99d1b3169adaad917"
#endif


#define NETWORK_CONNECTION_FAILURE_MSG @"Request failed!Please check your network settings."


//URL Constants
#define NEATO_CREATE_USER_URL @"method=user.create"
#define NEATO_GET_USER_AUTH_TOKEN_URL @"method=auth.get_user_auth_token"
#define NEATO_RESEND_VALIDATION_EMAIL_URL @"method=user.ResendValidationEmail"
#define NEATO_GET_USER_DETAILS_URL @"method=user.get_user_account_details"
#define NEATO_LOGOUT_USER_URL @"method=user.logout_auth_token"
#define NEATO_CREATE_ROBOT_URL @"method=robot.create"
#define NEATO_GET_ROBOT_DETAILS_URL @"method=robot.get_details"
#define NEATO_SET_ROBOT_URL @"method=robot.set_user"
#define NEATO_GET_ASSOCIATED_ROBOTS_URL @"method=user.get_associated_robots"
#define NEATO_UPDATE_AUTH_TOKEN_URL @"method=user.update_auth_token_expiry"
#define NEATO_SET_ROBOT_PROFILE_URL @"method=robot.set_profile_details"
#define NEATO_GET_ROBOT_ONLINE_STATUS_URL @"method=robot.is_online"
#define NEATO_DISSOCIATE_ALL_ROBOTS_URL @"method=user.disassociate_robot"
#define NEATO_REGISTER_FOR_PUSH_NOTIFICATION_URL @"method=message.notification_registration"
#define NEATO_UNREGISTER_FOR_PUSH_NOTIFICATION_URL @"method=message.notification_unregistration"
#define NEATO_IS_USER_VALIDATED_URL @"method=user.IsUserValidated"
#define NEATO_GET_ROBOT_PROFILE_DETAILS_URL @"method=robot.get_profile_details"
#define NEATO_GET_ROBOT_PROFILE_DETAILS_2_URL @"method=robot.get_profile_details2"
// TODO: These APIs have not been added to prod server.
#define NEATO_CREATE_USER2_URL @"method=user.create2"
#define NEATO_FORGET_PASSWORD_URL @"method=user.forget_password"
#define NEATO_CHANGE_PASSWORD_URL @"method=user.change_password"
#define NEATO_SET_ROBOT_PROFILE_2_URL @"method=robot.set_profile_details2"
#define NEATO_SET_PUSH_NOTIFICATION_OPTIONS_URL @"method=message.set_user_push_notification_options"
#define NEATO_GET_PUSH_NOTIFICATION_OPTIONS_URL @"method=message.get_user_push_notification_options"
#define NEATO_GET_ROBOT_VIRTUAL_ONLINE_STATUS_URL @"method=robot.is_robot_online_virtual"
#define NEATO_SET_ROBOT_PROFILE_DETAILS_3 @"method=robot.set_profile_details3"
#define NEATO_DELETE_ROBOT_PROFILE_KEY_2 @"method=robot.delete_robot_profile_key2"
//Schedule URL Constants
#define NEATO_GET_SCHEDULES_URL @"method=robotschedule.get_schedules"
#define NEATO_GET_SCHEDULE_DATA_URL @"method=robotschedule.get_data"
#define NEATO_POST_ROBOT_SCHEDULE_DATA @"method=robotschedule.post_data"
#define NEATO_UPDATE_ROBOT_SCHEDULE_DATA @"method=robotschedule.update_data"
#define NEATO_DELETE_SCHEDULE_DATA @"method=robotschedule.delete_data"
#define NEATO_GET_SCHEDULE_BASED_ON_TYPE @"method=robotschedule.get_schedule_based_on_type"
#define NEATO_SET_USER_ATTRIBUTES @"method=user.set_attributes"
#define NEATO_LINK_ROBOT_URL @"method=robot.link_to_robot"
#define NEATO_CLEAR_ROBOT_DATA_URL @"method=robot.clear_robot_association"
#define NEATO_CREATE_USER3_URL @"method=user.create3"
#define NEATO_SET_ACCOUNT_DETAILS @"method=user.set_account_details"

// Crittercism App IDs
#define CRITTERCISM_DEBUG_APP_ID @"5332916c40ec922f32000006" // using c_neato@rajatogo.com login
#define CRITTERCISM_RELEASE_APP_ID @"533277c3a6d3d77683000001" // using Neato account for crittercism

// Generic constants
#define KEY_LAST_USED_ROBOT_ID @"lastUsedRobotId"

// App UI state constants
#define ROBOT_STATE_INVALID                 0
#define ROBOT_STATE_IDLE                    1
#define ROBOT_STATE_USER_MENU               2
#define ROBOT_STATE_CLEANING                3
#define ROBOT_STATE_SUSPENDED_CLEANING      4
#define ROBOT_STATE_PAUSED                  5
#define ROBOT_STATE_MANUAL_CLEANING         6
#define ROBOT_STATE_RETURNING               7
