
typedef void (^ServerHelperCompletionBlock)(id response, NSError *error);

#define HTTP_STATUS_CODE_USER_UNAUTHORIZED 401

#define SERVER_REPONSE_HANDLER_KEY @"key_server_response_handler"
#define UPDATE_AUTH_TOKEN_RESPONSE_HANDLER @"updateAuthTokenHandler:"
#define PUSH_NOTIFICATION_REGISTRATION_REPOSNE_HANDLER @"pushNotificationRegistrationHandler:request:"
#define PUSH_NOTIFICATION_UNREGISTRATION_REPOSNE_HANDLER @"pushNotificationUnregistrationHandler:"

#define PUSH_NOTIFICATION_DEVICE_TOKEN  @"deviceTokenForPush"
#define PUSH_NOTIFICATION_SERVER_TYPE   @"notification_server_type"
#define PUSH_NOTIFICATION_APPLICATION_ID   @"application_id"

#define SEND_COMMAND_RESPONSE_HANDLER @"sendCommandHandler:request:"
#define SET_USER_ATTRIBUTES_HANDLER @"setUserAttributesHandler:"
#define SET_PROFILE_DETAILS_HANDLER @"notifyScheduleUpdatedHandler:"

#define GET_IS_USER_VALIDATED_POST_STRING @"api_key=%@&email=%@"
#define GET_AUTH_TOKEN_NATIVE_POST_STRING @"api_key=%@&account_type=%@&email=%@&password=%@"
#define GET_RESEND_VALIDATION_EMAIL_POST_STRING @"api_key=%@&email=%@"
#define GET_USER_DETAILS_POST_STRING @"api_key=%@&email=%@&auth_token=%@"
#define GET_ROBOT_DETAILS_POST_STRING @"api_key=%@&serial_number=%@"
#define SET_ROBOT_USER_POST_STRING @"api_key=%@&email=%@&serial_number=%@"
#define GET_USER_LOGOUT_POST_STRING @"api_key=%@&email=%@&auth_token=%@"
#define UPDATE_AUTH_TOKEN_POST_STRING @"api_key=%@&auth_token=%@"
#define GET_ASSOCIATED_ROBOTS_POST_STRING @"api_key=%@&auth_token=%@&email=%@"
#define SET_ROBOT_PROFILE_POST_STRING @"api_key=%@&serial_number=%@&%@"
#define PROFILE_DATA_FORMAT @"&profile[%@]=%@"
#define DISSOCIATE_ALL_ROBOTS_POST_STRING @"api_key=%@&email=%@&serial_number=%@"
#define GET_ROBOT_ONLINE_STATUS_POST_STRING @"api_key=%@&serial_number=%@"

#define PUSH_NOTIFICATION_REGISTRATION_POST_STRING  @"api_key=%@&user_email=%@&device_type=%ld&registration_id=%@"
#define PUSH_NOTIFICATION_UNREGISTRATION_POST_STRING  @"api_key=%@&registration_id=%@"
#define CREATE_USER2_POST_STRING @"api_key=%@&name=%@&email=%@&alternate_email=%@&password=%@&account_type=%@&external_social_id=%@"
#define GET_FORGET_PASSWORD_POST_STRING @"api_key=%@&email=%@"
#define GET_CHANGE_PASSWORD_POST_STRING @"api_key=%@&auth_token=%@&password_old=%@&password_new=%@"
#define ENABLE_BASIC_SCHEDULE @"enable_basic_schedule"
#define ENABLE_ADVANCED_SCHEDULE @"enable_advanced_schedule"
#define ENABLE_DISABLE_SCHEDULE_POST_STRING @"api_key=%@&serial_number=%@&source_serial_number=%@&source_smartapp_id=%@&value_extra=%@&%@"
#define SET_USER_PUSH_NOTIFICATION_OPTION_POST_STRING @"api_key=%@&email=%@&json_object=%@"
#define GET_USER_PUSH_NOTIFICATION_OPTION_POST_STRING @"api_key=%@&email=%@"
#define IS_SCHEDULE_ENABLED_POST_STRING @"api_key=%@&serial_number=%@"
#define GET_ROBOT_VIRTUAL_ONLINE_STATUS_POST_STRING @"api_key=%@&serial_number=%@"
#define SET_ROBOT_PROFILE_DETAILS_3_POST_STRING @"api_key=%@&serial_number=%@&source_serial_number=%@&source_smartapp_id=%@&cause_agent_id=%@&value_extra=%@&notification_flag=%@%@"
#define GET_ROBOT_PROFILE_DETAILS_2_POST_STRING @"api_key=%@&serial_number=%@"
#define SET_USER_ATTRIBUTES_POST_STRING @"api_key=%@&auth_token=%@&profile[operating_system]=%@&profile[version]=%@&profile[name]=%@"
#define CLEAR_ROBOT_DATA_POST_STRING @"api_key=%@&serial_number=%@&email=%@&is_delete=%@"
#define CREATE_USER3_POST_STRING @"api_key=%@&name=%@&email=%@&alternate_email=%@&password=%@&account_type=%@&extra_param=%@"
#define SET_ACCOUNT_DETAILS_POST_STRING @"api_key=%@&email=%@&auth_token=%@%@"
#define LINK_ROBOT_POST_STRING @"api_key=%@&email=%@&linking_code=%@"

// SCHEDULE constants
#define GET_SCHEDULE_DATA_POST_STRING @"api_key=%@&robot_schedule_id=%@"
#define GET_SCHEDULE_DATA_RESPONSE_HANDLER @"gotScheduleResponseData:forScheduleId:"

#define GET_SCHEDULES_POST_STRING @"api_key=%@&serial_number=%@"
#define GET_SCHEDULES_RESPONSE_HANDLER @"gotSchedulesResponse:forRobotWithId:"
#define ROBOT_ID_SERVER_KEY @"robot_id_header_key"
#define SCHEDULE_ID_SERVER_KEY @"schedule_id_header_key"

#define GET_POST_ROBOT_SCHEDULE_STRING @"api_key=%@&serial_number=%@&schedule_type=%@&xml_data=%@"
#define GET_UPDATE_ROBOT_SCHEDULE_DATA_POST_STRING @"api_key=%@&robot_schedule_id=%@&schedule_type=%@&xml_data_version=%@&xml_data=%@"
#define GET_DELETE_SCHEDULE_POST_STRING @"api_key=%@&robot_schedule_id=%@"
#define GET_POST_ROBOT_SCHEDULE_RESPONSE_HANDLER @"postRobotScheduleHandler:"
#define GET_UPDATE_ROBOT_SCHEDULE_RESPONSE_HANDLER @"upadateRobotScheduleHandler:"
#define GET_DELETE_SCHEDULE_RESPONSE_HANDLER @"deleteScheduleDataResponseHandler:"
#define GET_SCHEDULE_BASED_ON_TYPE_POST_STRING @"api_key=%@&robot_serial_number=%@&schedule_type=%@"