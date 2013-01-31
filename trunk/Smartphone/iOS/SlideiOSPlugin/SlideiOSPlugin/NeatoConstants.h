#import <Foundation/Foundation.h>


#define ACCOUNT_TYPE_NATIVE @"Native"
#define ACCOUNT_TYPE_FACEBOOK @"Facebook"

#define DEMO_USER_EMAIL  @"demo1@demo.com"
#define DEMO_USER_PASSWORD  @"demo123"

#define NEATO_XMPP_SERVER_ADDRESS @"rajatogo.com"

#define UDP_SMART_APPS_BROADCAST_PORT 12346
#define TIME_BEFORE_SOCKET_CLOSES 5

#define TCP_ROBOT_SERVER_SOCKET_PORT 4444

#define USER_HANDLE @"user_handle"

#define NEATO_RESPONSE_STATUS @"status"
#define NEATO_RESPONSE_MESSAGE @"message"
#define NEATO_RESPONSE_RESULT @"result"
#define NEATO_RESPONSE_ATLAS_ID @"atlas_id"
#define NEATO_RESPONSE_ATLAS_XML_DATA_URL @"xml_data_url"
#define NEATO_RESPONSE_ATLAS_VERSION @"version"
#define NEATO_RESPONSE_SUCCESS @"success"
#define NEATO_RESPONSE_ROBOT_ATLAS_ID @"robot_atlas_id"

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

#define NEATO_STAGING_SERVER @"Staging"
#define NEATO_PROD_SERVER @"Production"
#define NEATO_DEV_SERVER @"Development"

#define NEATO_KEY_APP_VERSION @"appVersion"
#define NEATO_KEY_LIB_VERSION @"libVersion"
#define NEATO_KEY_SERVER_USED @"serverUsed"

#define TAG_FIND_ROBOT_COMMAND 9001

#define NETWORK_CONNECTION_FAILURE_MSG @"Request failed!Please check your network settings."
// To switch to prod server, uncomment SWITCH_TO_PROD_SERVER variable
//#define SWITCH_TO_PROD_SERVER 1

#ifdef SWITCH_TO_PROD_SERVER

    #define NEATO_API_KEY @"1e26686d806d82144a71ea9a99d1b3169adaad917" 
    // Switch for create user\robot related API's
    #define NEATO_ROBOT_SERVER_PROD     1
    // Switch for robot's maps related API's
    #define ROBOT_MAPS_SERVER_PROD      1
    // Switch for robot's schedule related API's
    #define ROBOT_SCHEDULE_SERVER_PROD  1
    // Switch for robot's Atlas API
    #define ROBOT_ATLAS_SERVER_PROD  1

#else
    #define NEATO_API_KEY @"1e26686d806d82144a71ea9a99d1b3169adaad917" 
    // Logging would be 'ON' only in DEV mode.
    #define LOGGING_ENABLED             1
    #define ENABLE_DB_CREATION_IN_DOCUMENTS_DIR 1

    // For internal testing we are going to use Dev server
    // Uncomment flag SWITCH_TO_DEV_SERVER to enable DEV server.
    // By default we would be using Staging server.
    // DO NOT check-in code with this flag enabled.
    // #define SWITCH_TO_DEV_SERVER 1

#endif


#ifdef NEATO_ROBOT_SERVER_PROD
    // Will use API's at http://neato.rajatogo.com/wstest/
    #define NEATO_CREATE_USER_URL @"http://neato.rajatogo.com/api/rest/json/?method=user.create"
    #define NEATO_GET_USER_AUTH_TOKEN_URL @"http://neato.rajatogo.com/api/rest/json/?method=auth.get_user_auth_token"
    #define NEATO_GET_USER_DETAILS_URL @"http://neato.rajatogo.com/api/rest/json/?method=user.get_user_account_details"
    #define NEATO_LOGOUT_USER_URL @"http://neato.rajatogo.com/api/rest/json/?method=user.logout_auth_token"
    #define NEATO_CREATE_ROBOT_URL @"http://neato.rajatogo.com/api/rest/json/?method=robot.create"
    #define NEATO_GET_ROBOT_DETAILS_URL @"http://neato.rajatogo.com/api/rest/json/?method=robot.get_details"
    #define NEATO_SET_ROBOT_URL @"http://neato.rajatogo.com/api/rest/json/?method=robot.set_user"
    #define NEATO_GET_ASSOCIATED_ROBOTS_URL @"http://neato.rajatogo.com/api/rest/json/?method=user.get_associated_robots"
    #define NEATO_UPDATE_AUTH_TOKEN_URL @"http://neato.rajatogo.com/api/rest/json/?method=user.update_auth_token_expiry"
#elif SWITCH_TO_DEV_SERVER
    // Will use API's at http://neatodev.rajatogo.com/Server_Yii/Neato/wstest/
    #define NEATO_CREATE_USER_URL @"http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json?method=user.create"
    #define NEATO_GET_USER_AUTH_TOKEN_URL @"http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json?method=auth.get_user_auth_token"
    #define NEATO_GET_USER_DETAILS_URL @"http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json?method=user.get_user_account_details"
    #define NEATO_LOGOUT_USER_URL @"http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json?method=user.logout_auth_token"
    #define NEATO_CREATE_ROBOT_URL @"http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json?method=robot.create"
    #define NEATO_GET_ROBOT_DETAILS_URL @"http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json?method=robot.get_details"
    #define NEATO_SET_ROBOT_URL @"http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json?method=robot.set_user"
    #define NEATO_GET_ASSOCIATED_ROBOTS_URL @"http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json?method=user.get_associated_robots"
    #define NEATO_UPDATE_AUTH_TOKEN_URL @"http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json?method=user.update_auth_token_expiry"
#else
    // Will use API's at http://neatostaging.rajatogo.com/wstest/
    #define NEATO_CREATE_USER_URL @"http://neatostaging.rajatogo.com/api/rest/json/?method=user.create"
    #define NEATO_GET_USER_AUTH_TOKEN_URL @"http://neatostaging.rajatogo.com/api/rest/json/?method=auth.get_user_auth_token"
    #define NEATO_GET_USER_DETAILS_URL @"http://neatostaging.rajatogo.com/api/rest/json/?method=user.get_user_account_details"
    #define NEATO_LOGOUT_USER_URL @"http://neatostaging.rajatogo.com/api/rest/json/?method=user.logout_auth_token"
    #define NEATO_CREATE_ROBOT_URL @"http://neatostaging.rajatogo.com/api/rest/json/?method=robot.create"
    #define NEATO_GET_ROBOT_DETAILS_URL @"http://neatostaging.rajatogo.com/api/rest/json/?method=robot.get_details"
    #define NEATO_SET_ROBOT_URL @"http://neatostaging.rajatogo.com/api/rest/json/?method=robot.set_user"
    #define NEATO_GET_ASSOCIATED_ROBOTS_URL @"http://neatostaging.rajatogo.com/api/rest/json/?method=user.get_associated_robots"
    #define NEATO_UPDATE_AUTH_TOKEN_URL @"http://neatostaging.rajatogo.com/api/rest/json/?method=user.update_auth_token_expiry"
#endif


#ifdef ROBOT_ATLAS_SERVER_PROD
    #define NEATO_GET_ROBOT_ATLAS_DATA_URL @"http://neato.rajatogo.com/api/rest/json/?method=robot.get_atlas_data"
    #define NEATO_GET_ATLAS_GRID_METADATA_URL @"http://neato.rajatogo.com/api/rest/json/?method=robot.get_atlas_grid_metadata"
    #define NEATO_UPDATE_ATLAS_METADATA_URL @"http://neato.rajatogo.com/api/rest/json/?method=robot.update_atlas"
#elif SWITCH_TO_DEV_SERVER
    #define NEATO_GET_ROBOT_ATLAS_DATA_URL @"http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json?method=robot.get_atlas_data"
    #define NEATO_GET_ATLAS_GRID_METADATA_URL @"http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json?method=robot.get_atlas_grid_metadata"
    #define NEATO_UPDATE_ATLAS_METADATA_URL @"http://neatodev.rajatogo.com/Server_Yii/Neato/api/rest/json?method=robot.update_atlas"
#else
    #define NEATO_GET_ROBOT_ATLAS_DATA_URL @"http://neatostaging.rajatogo.com/api/rest/json/?method=robot.get_atlas_data"
    #define NEATO_GET_ATLAS_GRID_METADATA_URL @"http://neatostaging.rajatogo.com/api/rest/json/?method=robot.get_atlas_grid_metadata"
    #define NEATO_UPDATE_ATLAS_METADATA_URL @"http://neatostaging.rajatogo.com/api/rest/json/?method=robot.update_atlas"
#endif


#ifdef ROBOT_MAPS_SERVER_PROD
    // Will use API's at http://neato.rajatogo.com/wstest/robot_map.php
    // TODO: Define all urls related to maps API here

#else
    // Will use API's at http://neatostaging.rajatogo.com/wstest/robot_map.php
    // TODO: Define all urls related to maps API here

#endif



#ifdef ROBOT_SCHEDULE_SERVER_PROD
    // Will use API's at http://neato.rajatogo.com/wstest/robot_schedule.php
    // TODO: Define all urls related to schedule API here
#else
    // Will use API's at http://neatostaging.rajatogo.com/wstest/robot_schedule.php
    // TODO: Define all urls related to schedule API here
#endif

