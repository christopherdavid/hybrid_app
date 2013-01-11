#import <Foundation/Foundation.h>


#define ACCOUNT_TYPE_NATIVE @"Native"
#define ACCOUNT_TYPE_FACEBOOK @"Facebook"

#define DEMO_USER_EMAIL  @"demo1@demo.com"
#define DEMO_USER_PASSWORD  @"demo123"

#define NEATO_XMPP_SERVER_ADDRESS @"50.116.10.113"

#define UDP_SMART_APPS_BROADCAST_PORT 12346
#define TIME_BEFORE_SOCKET_CLOSES 5

#define TCP_ROBOT_SERVER_SOCKET_PORT 4444

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

#else
    #define NEATO_API_KEY @"1e26686d806d82144a71ea9a99d1b3169adaad917" 
    // Logging would be 'ON' only in DEV mode.
    #define LOGGING_ENABLED             1

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

