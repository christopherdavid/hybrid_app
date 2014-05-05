/*
 * This file copies the UserPluginManager, RobotPluginManager for the desktop version of the application.
 * It is used for demo purposes only and WON'T be used when the application is running on a device.
 *
 */
var Connection = {
    UNKNOWN:1,
    ETHERNET:2,
    WIFI:3,
    CELL_2G:4,
    CELL_3G:5,
    CELL_4G:6,
    NONE:7
}
function switchConnection(newType) {
    navigator.network.connection.type = newType;
}
navigator.network = {};
navigator.network.connection = {
    type:Connection.ETHERNET
}
navigator.notification = {};
navigator.notification.vibrate = function(time) {
    console.log("vibrate for " + time + "ms")
}

var NOTIFICATION_DISCOVERY_STARTED = 1;
var NOTIFICATION_DISCOVERY_RESULT = 2;


var KEY_MAP_TYPE_XML = 1;
var KEY_MAP_TYPE_BLOB = 2;

var SCHEDULAR_EVENT_TYPE_QUIET = 0;
var SCHEDULAR_EVENT_TYPE_CLEAN = 1;

var USER_STATUS_VALIDATED = 0;
var USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD = -1;
var USER_STATUS_NOT_VALIDATED = -2;

var DAY_SUNDAY = 0;
var DAY_MONDAY = 1;
var DAY_TUEDAY = 2;
var DAY_WEDNESDAY = 3;
var DAY_THURSDAY = 4;
var DAY_FRIDAY = 5;
var DAY_SATURDAY = 6;

var SCHEDULE_TYPE_BASIC = 0;
var SCHEDULE_TYPE_ADVANCED = 1;

// Motor types
var MOTOR_TYPE_VACUUM = 101;
var MOTOR_TYPE_BRUSH  = 102;

//robotNotifications2 keyCodes

// The current state of the robot
var ROBOT_CURRENT_STATE_CHANGED     = 4001;
// The keyCode for the state update of the robot.
var ROBOT_STATE_UPDATE              = 4003;
// The keyCode for the name update of the robot.
var ROBOT_NAME_UPDATE               = 4004;
// The keyCode for the schedulestate update of the robot.
var ROBOT_SCHEDULE_STATE_CHANGED    = 4005;
// The keyCode for the schedule is updated notification
var ROBOT_SCHEDULE_UPDATED          = 4006;
// The keyCodes to denote that the robot peer connection has 
// been established/disconnected or not able to connect
var ROBOT_CONNECTED                 = 4007;
var ROBOT_DISCONNECTED              = 4008;
var ROBOT_NOT_CONNECTED             = 4009;

// The keyCode to denote about linking success
var ROBOT_LINKING_SUCCESS           = 4010
// The keyCode to denote about linking failure
var ROBOT_LINKING_FAILURE           = 4011

// The keyCode to denote that robot is linked to some other user.
// Whenever new user is linked to the robot
// then all associate users with that robot gets the notification
var ROBOT_NEW_LINKING_FORMED        = 4012

var ROBOT_MESSAGE_NOTIFICATION = 4013;
var ROBOT_MESSAGE_ERROR = 4014;

var ROBOT_ONLINE_STATUS_CHANGED = 4015;
var ROBOT_COMMAND_FAILED = 4016;



// Robot state codes
var ROBOT_STATE_UNKNOWN             = 0;
var ROBOT_STATE_IDLE                = 1;
var ROBOT_USER_MENU_STATE           = 2;
var ROBOT_STATE_CLEANING            = 3;
var ROBOT_STATE_SUSPENDED_CLEANING  = 4;
var ROBOT_STATE_PAUSED              = 5;
var ROBOT_STATE_MANUAL_CLEANING     = 6;
var ROBOT_STATE_RETURNING           = 7;

/*
var ROBOT_STATE_UNKNOWN     = 10001;
var ROBOT_STATE_CLEANING    = 10002;
var ROBOT_STATE_IDLE        = 10003;
var ROBOT_STATE_CHARGING    = 10004;
var ROBOT_STATE_STOPPED     = 10005;
var ROBOT_STATE_STUCK       = 10006;
var ROBOT_STATE_PAUSED      = 10007;
var ROBOT_STATE_RESUMED     = 10008;
var ROBOT_STATE_ON_BASE     = 10009;
// Manual Cleaning State Codes
var ROBOT_STATE_MANUAL_CLEANING     = 10010;
var ROBOT_STATE_MANUAL_PLAY_MODE    = 10011;
// user is in the menu state.
var ROBOT_USER_MENU_STATE = 10012;
*/

var PLUGIN_JSON_KEYS  =  (function() {
    var keys = {
            'DISCOVERY_NOTIFICATION_KEY': 'notificationType', 
        };

        return {
           get: function(name) { return keys[name]; }
       };
   })();


//List of plugins.
var USER_MANAGEMENT_PLUGIN = "UserManagement";
var ROBOT_MANAGEMENT_PLUGIN = "RobotManagement";

//List of action types of USER manager 
var ACTION_TYPE_LOGIN                           = "login";
var ACTION_TYPE_LOGOUT                          = "logout";
var ACTION_TYPE_ISLOGGEDIN                      = "isLoggedIn";
var ACTION_TYPE_CREATE_USER                     = "createUser";
var ACTION_TYPE_CREATE_USER2                    = "createUser2";
var ACTION_TYPE_RESEND_VALIDATION_MAIL          = "resendValidationMail";
var ACTION_TYPE_IS_USER_VALIDATED               = "isUserValidated";
var ACTION_TYPE_GET_USER_DETAILS                = "getUserDetails";
var ACTION_TYPE_ASSOCIATE_ROBOT                 = "associateRobot";
var ACTION_TYPE_GET_ASSOCIATED_ROBOTS           = "getAssociatedRobots";
var ACTION_TYPE_DISASSOCIATE_ROBOT              = "disassociateRobot";
var ACTION_TYPE_DISASSOCAITE_ALL_ROBOTS         = "disassociateAllRobots";
var ACTION_TYPE_FORGET_PASSWORD                 = "forgetPassword";
var ACTION_TYPE_CHANGE_PASSWORD                 = "changePassword";
var ACTION_TYPE_REGISTER_FOR_ROBOT_MESSAGES     = "registerForRobotMessges";
var ACTION_TYPE_UNREGISTER_FOR_ROBOT_MESSAGES   = "unregisterForRobotMessages";
// List of actions types of Robot Manager
var ACTION_TYPE_DISCOVER_NEARBY_ROBOTS          = "discoverNearByRobots";
var ACTION_TYPE_TRY_CONNECT_CONNECTION          = "tryDirectConnection";
var ACTION_TYPE_TRY_CONNECT_CONNECTION2         = "tryDirectConnection2";
var ACTION_TYPE_SEND_COMMAND_TO_ROBOT           = "sendCommandToRobot";
var ACTION_TYPE_SEND_COMMAND_TO_ROBOT2          = "sendCommandToRobot2";
var ACIION_TYPE_SET_SCHEDULE                    = "robotSetSchedule";
var ACIION_TYPE_GET_ROBOT_SCHEDULE              = "getSchedule";
var ACTION_TYPE_GET_ROBOT_MAP                   = "getRobotMap";
var ACTION_TYPE_SET_MAP_OVERLAY_DATA            = "setMapOverlayData";
var ACTION_TYPE_DISCONNECT_DIRECT_CONNETION     = "disconnectDirectConnection";
var ACTION_TYPE_GET_ROBOT_ATLAS_METADATA        = "getRobotAtlasMetadata";
var ACTION_TYPE_UPDATE_ROBOT_ATLAS_METADATA     = "updateRobotAtlasMetadata";
var ACTION_TYPE_GET_ATLAS_GRID_DATA             = "getAtlasGridData";
var ACTION_TYPE_SET_ROBOT_NAME                  = "setRobotName";
var ACTION_TYPE_DELETE_ROBOT_SCHEDULE           = "deleteScheduleData";
var ACTION_TYPE_SET_ROBOT_NAME_2                = "setRobotName2";
var ACTION_TYPE_GET_ROBOT_DETAIL                = "getRobotDetail";
var ACTION_TYPE_GET_ROBOT_ONLINE_STATUS         = "getRobotOnlineStatus";
var ACTION_TYPE_GET_ROBOT_VIRTUAL_ONLINE_STATUS     = "getRobotVirtualOnlineStatus";
var ACTION_TYPE_REGISTER_ROBOT_NOTIFICATIONS        = "registerRobotNotifications";
var ACTION_TYPE_UNREGISTER_ROBOT_NOTIFICATIONS      = "unregisterRobotNotifications";
var ACTION_TYPE_REGISTER_ROBOT_NOTIFICATIONS_2      = "registerRobotNotifications2";
var ACTION_TYPE_UNREGISTER_ROBOT_NOTIFICATIONS_2    = "unregisterRobotNotifications2";
var ACTION_TYPE_SET_SPOT_DEFINITION             = "setSpotDefinition";
var ACTION_TYPE_GET_SPOT_DEFINITION             = "getSpotDefinition";
var ACTION_TYPE_DRIVE_ROBOT                     = "driveRobot";
var ACTION_TYPE_IS_ROBOT_PEER_CONNECTED         = "isRobotPeerConnected";
var ACTION_TYPE_TURN_MOTOR_ON_OFF               = "turnMotorOnOff";
var ACTION_TYPE_TURN_MOTOR_ON_OFF2              = "turnMotorOnOff2";
var ACTION_TYPE_TURN_WIFI_ON_OFF                = "turnWiFiOnOff";
var ACTION_TYPE_TURN_NOTIFICATION_ON_OFF        = "turnNotificationOnOff";
var ACTION_TYPE_IS_NOTIFICATION_ENABLED         = "isNotificationEnabled";
var ACTION_TYPE_GET_NOTIFICATION_SETTINGS       = "getNotificationSettings";
var ACTION_TYPE_INTEND_TO_DRIVE_ROBOT           = "intendToDrive";
var ACTION_TYPE_STOP_ROBOT_DRIVE                = "stopRobotDrive";
var ACTION_TYPE_CANCEL_INTEND_TO_DRIVE          = "cancelIntendToDrive";

var ACTION_TYPE_START_CLEANING                  = "startCleaning";
var ACTION_TYPE_STOP_CLEANING                   = "stopCleaning";
var ACTION_TYPE_PAUSE_CLEANING                  = "pauseCleaning";
var ACTION_TYPE_RESUME_CLEANING                 = "resumeCleaning";



// New schedule APis 
var ACTION_TYPE_UPDATE_SCHEDULE                 = "updateSchedule";
var ACTION_TYPE_DELETE_ROBOT_SCHEDULE_EVENT     = "deleteScheduleEvent";
var ACTION_TYPE_UPDATE_ROBOT_SCHEDULE_EVENT     = "updateScheduleEvent";
var ACTION_TYPE_GET_SCHEDULE_EVENT_DATA         = "getScheduleEventData";
var ACTION_TYPE_ADD_ROBOT_SCHEDULE_EVENT        = "addScheduleEventData";
var ACTION_TYPE_GET_SCHEDULE_EVENTS             = "getScheduleEvents";
var ACTION_TYPE_GET_SCHEDULE_DATA               = "getScheduleData";
var ACTION_TYPE_CREATE_SCHEDULE                 = "createSchedule";
var ACTION_TYPE_IS_SCHEDULE_ENABLED             = "isScheduleEnabled";
var ACTION_TYPE_ENABLE_SCHEDULE             = "enableSchedule";
var ACTION_TYPE_GET_ROBOT_CLEANING_STATE                    = "getRobotCleaningState";

//List of keys to send data:

var KEY_EMAIL = 'email';
var KEY_PASSWORD = 'password';
var KEY_USER_NAME = 'userName';

//Used by robot plugin
var KEY_COMMAND = 'command';
var KEY_ROBOT_ID = 'robotId';
var KEY_USE_XMPP = 'useXMPP';
var KEY_ROBOT_NAME = "robotName";

var KEY_SCHEDULE_TYPE = "scheduleType";

var KEY_DAY = 'day';

var KEY_EVENT_TYPE = 'eventType';
var KEY_AREA = 'area';

var KEY_START_TIME = "startTime";
var KEY_END_TIME = "endTime";


//COMMAND IDS:
var COMMAND_ROBOT_START = 101;
var COMMAND_ROBOT_STOP = 102;
var COMMAND_ROBOT_JABBER_DETAILS = 103;
var COMMAND_SEND_BASE = 104;
var COMMAND_GET_ROBOT_STATE = 105;
var COMMAND_SEND_ROBOT_STATE = 106;
var COMMAND_PAUSE_CLEANING = 107;
var COMMAND_ENABLE_SCHEDULE = 108;
var COMMAND_DATA_CHANGED_ON_SERVER = 109;
var COMMAND_SET_ROBOT_TIME = 110;
var COMMAND_REGISTER_NOTIFICATIONS = 111;
var COMMAND_UNREGISTER_NOTIFICATIONS = 112;
var COMMAND_RESUME_CLEANING = 114;

// NOTE: Cleaning type is now referred as cleaning category with new
// names as listed below.
var START_CLEAN_TYPE_HIGH = 1;
var START_CLEAN_TYPE_NORMAL = 2;
var START_CLEAN_TYPE_SPOT = 3;

// Cleaning Category
var CLEANING_CATEGORY_MANUAL = 1;
var CLEANING_CATEGORY_ALL = 2;
var CLEANING_CATEGORY_SPOT = 3;

// Cleaning Mode
var CLEANING_MODE_ECO = 2;
var CLEANING_MODE_NORMAL = 1;

// Navigation Control Ids
var NAVIGATION_CONTROL_1 = 1;
var NAVIGATION_CONTROL_2 = 2;
var NAVIGATION_CONTROL_3 = 3;
var NAVIGATION_CONTROL_4 = 4;
var NAVIGATION_CONTROL_5 = 5;
var NAVIGATION_CONTROL_BACK = 6;
var FLAG_ON = 1;
var FLAG_OFF = 0;

//Special notification Ids - Must be 2 to power N (same values must be defined
//in Plug-in and Robot)
var NOTIFICATIONS_GLOBAL_OPTION = "global";
var NOTIFICATION_ROBOT_STUCK = "101";
var NOTIFICATION_DIRT_BIN_FULL = "102";
var NOTIFICATION_CLEANING_DONE = "103";
var NOTIFICATION_DIRT_BIN_MISSING = "20219";
var NOTIFICATION_PLUG_CABLE = "212";
var NOTIFICATION_ROBOT_CANCEL = "22000";


// List of Error Code values returned from the plugin.

/**
 * Authentication of the user failed.
 *  - This will occur when the email and/or the password is incorrect while Logging-in.
 *  - This will occur if the authentication token has expired.
 *  In this case the user will need to login again with the correct credentials.
 */
var ERROR_AUTHENTICATION_FAILED = -101;

/**
 * Email id entered does not match the usual format of the email id.
 * 
 */
var ERROR_INVALID_EMAIL_ID = -105

/**
 * Email id is already registered with the server.
 * - This will occur while creating a user account with already registered user email id.
 * The user needs to provide a email id which is not already registered.
 */
var ERROR_EMAIL_ALREADY_REGISTERED = -106;

/**
 * Create User failed due to some unknown server error.
 * - User should retry.
 */
var ERROR_CREATE_USER_FAILED_TRY_AGAIN = -108;


/**
 * Old password does not match to the current password of the user.
 * - To change the password, user needs to provide the current password. If entered password does not match
 * the current password, ERROR_OLD_PASSWORD_MISMATCH error is returned
 */
var ERROR_OLD_PASSWORD_MISMATCH = -110;

/**
 * Robot name value entered is empty
 * - This will occur when Robot name value is entered empty. 
 * User should enter a non-empty for the robot name.
 */
var ERROR_INVALID_ROBOT_ACCOUNT_DETAIL = -111;

/**
 * Email id not found on the server.
 * - This will occur when the user sends a forgot password request and email id given isn't registered on the server.
 */
var ERROR_EMAIL_NOT_REGISTERED = -112;

/**
 * Robot id not found on the server.
 * - This will occur while associating a robot with the user.
 */
var ERROR_ROBOT_NOT_REGISTERED = -114;

/**
 * Alternate email id entered does not match the usual format of the email id.
 */
var ERROR_INVALID_ALTERNATE_EMAIL_ID = -115

/**
 * Resend validation-email limit reached.
 * - This will occur when the validation email sending limit is reached.
 *   Currently the limit is 5.
 */
var ERROR_RESEND_VALIDATION_EMAIL_LIMIT_REACHED = -116;

/**
 * Email Id already validated.
 * - This will occur if the user requests to send a validation email, but the email is already validated on the server.
 */
var ERROR_EMAIL_ALREADY_VALIDATED = -117;

/**
 * Schedule version does not match. Please retrieve the latest schedule and then edit.
 * - This will occur if the user does not have the latest schedule and the user tries to edit it (Some other user edited 
 *   the schedule persay).
 *   The application should request for the latest schedule so that the user can then edit the same. 
 */
var ERROR_SCHEDULE_VERSION_MISMATCH = -129;

/**
 * Schedule type is not supported.
 * - This will occur if the application sends an invalid schedule type to the plugin layer.
 *   Currently supported schedule type is Basic schedule.
 */
var ERROR_INVALID_SCHEDULE_TYPE = -133;

/**
 * No schedule exists for given robot.
 * - This will occur if there is no schedule for the robot. The user/application should create a new schedule.
 */
var ERROR_NO_SCHEDULE_FOR_GIVEN_ROBOT = -159;

/**
 * Unknown error has occured. Please try again.
 */
var ERROR_TYPE_UNKNOWN = -501;

/**
 * No network connection. Please try again.
 */
var ERROR_NETWORK_CONNECTION_FAILURE = -502;

/**
 * Invalid schedule id for given robot.
 * - This will occur if the application sends an invalid schedule id to the plugin layer.
 */
var ERROR_INVALID_SCHEDULE_ID = -504;

/**
 * Invalid schedule event id for given robot.
 * - This will occur if the applicaiton sends an invalid schedule event id to the plugin layer.
 */
var ERROR_INVALID_SCHEDULE_EVENT_ID = -505;


/**
 *  This error type is returned 
 *  - when the robot is not peer connected and a drive command is given to the robot.
 *  - when the application tries to stop the drive for the robot, when it is already not connected.
 */
var ERROR_ROBOT_NOT_PEER_CONNECTED = -511;

/**
 * This error code is returned, 
 * - when the application tries to peer-connect to a robot when it is already directly connected to the same robot.
 * - When applicaiton tries to cancel intend to drive but the connection has already been formed.
 */
var ROBOT_ALREADY_CONNECTED = -512;

/**
 * This error code is returned when 
 * - the application tries to cancel the intend to drive for the robot, but no request is found given to
 *   drive the robot.
 */
var ROBOT_NO_INTEND_TO_DRIVE_REQUEST_FOUND = -514;

/**
 * This error code is returned when 
 * - the application tries to peer-connect to a robot when a different robot is directly connected to the user.
 */
var DIFFERENT_ROBOT_CONNECTION_EXISTS = -515;


var UserPluginManager = ( function() {
        return {

            login : function(email, password, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.loginUser(email, password, callbackSuccess, callbackError)
                window.setTimeout(function() {
                    callbackSuccess({
                        "email" : "demo1@demo.com",
                        "username" : "demo",
                        "userId" : "48",
						"validation_status" : USER_STATUS_VALIDATED,
                        "extra_param":{"countryCode":"GB","optIn":true}
                    });
                }, 1000);
                /*
                callbackError({
                 "errorMessage":"Type Error",
                 "errorCode":-101
                 });
                */
            },

            logout : function(callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.logoutUser(callbackSuccess, callbackError)
                window.setTimeout(function() {
                    callbackSuccess("OK");
                }, 1000);
            },
            createUser3: function(email, password, name, alternateEmail, optional, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.createUser(email, password, name, callbackSuccess, callbackError);
                window.setTimeout(function() {
                    callbackSuccess({
                        "email" : "homer@uid.com",
                        "username" : "Homer",
                        "userId" : "82",
                        "extra_param":{"countryCode":"GB","optIn":"true"}
                    });
                }, 1000);
            },
            createUser2: function(email, password, name, alternateEmail, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.createUser(email, password, name, callbackSuccess, callbackError);
                window.setTimeout(function() {
                    callbackSuccess({
                        "email" : "homer@uid.com",
                        "username" : "Homer",
                        "userId" : "82"
                    });
                }, 1000);
            },
            createUser : function(email, password, name, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.createUser(email, password, name, callbackSuccess, callbackError);
                window.setTimeout(function() {
                    callbackSuccess({
                        "email" : "homer@uid.com",
                        "username" : "Homer",
                        "userId" : "82"
                    });
                }, 1000);
                /*
                 callbackError({
                 "errorMessage":"Server Error",
                 "errorCode":1003
                 });
                 */
            },

            isUserLoggedIn : function(email, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.isUserLoggedIn(email, callbackSuccess, callbackError);
                window.setTimeout(function() {
                    callbackSuccess(true);
                   //callbackError({"status":-1,"message":"Method call failed the User Authentication"});

                }, 1000);
                //callbackSuccess(false);
            },
			
			isUserValidated : function(email, callbackSuccess, callbackError) {
				//window.plugins.neatoPluginLayer.userMgr.isUserValidated(email, callbackSuccess, callbackError);
				window.setTimeout(function() {
                    //callbackSuccess({validation_status:USER_STATUS_VALIDATED, message:"message"});
					callbackSuccess({validation_status:USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD, message:"message"});
                }, 1000);
			},
            
            associateRobot : function(email, robotId, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess("OK");
                }, 1000);
                //window.plugins.neatoPluginLayer.userMgr.associateRobotCommand(email, robotId, callbackSuccess, callbackError);
            },

            getUserDetail : function(email, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.getUserDetail(email, callbackSuccess, callbackError);
                // window.setTimeout(function() {
                    // callbackError({"status":-1,"message":"Method call failed the User Authentication"});
                // }, 1);                window.setTimeout(function() {
                    callbackSuccess({
                        "email" : "homer@uid.com",
                        "username" : "Homer",
                        "userId" : "82",
                        "extra_param":{"countryCode":"GB","optIn":"true","touAgreement":"true"}
                    });
                }, 1000);

            },

			setUserAccountDetails : function(email, countryCode, optIn, callbackSuccess, callbackError) {
				window.setTimeout(function() {
			                    callbackSuccess({
			                        "countryCode":"GB",
			                        "optIn":"true"
			                    });
			                }, 1000);
			},
            getAssociatedRobots : function(email, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.getAssociatedRobots(email, callbackSuccess, callbackError);
                window.setTimeout(function() {
                    callbackSuccess([{
                        "robotName" : "Map Demo",
                        "robotId" : "mapdemo123"
                    }, {
                        "robotName" : "Nexus One",
                        "robotId" : "rr1234"
                    }, {
                        "robotName" : "Nexus One",
                        "robotId" : "rr1001"
                    }, {
                        "robotName" : "Nexus One",
                        "robotId" : "rr1002"
                    }, {
                        "robotName" : "GT-I9000",
                        "robotId" : "demo123"
                    }, {
                        "robotName" : "XT910",
                        "robotId" : "rsl123"
                    }, {
                        "robotName" : "GT-I9000",
                        "robotId" : "rsl001"
                    }, {
                        "robotName" : "Galaxy Nexus",
                        "robotId" : "anewAndroidrobot"
                    }, {
                        "robotName" : "Galaxy Nexus",
                        "robotId" : "r1"
                    }, {
                        "robotName" : "GT-I9000",
                        "robotId" : "demo12"
                    }, {
                        "robotName" : "sdk",
                        "robotId" : "rsl2"
                    }, {
                        "robotName" : "test",
                        "robotId" : "NR_JAN_1"
                    }]);
                }, 1000);
                //no robots callback
                /*
                window.setTimeout(function() {
                    callbackSuccess([]);
                }, 2000);
                */
            },

            disassociateRobot : function(email, robotId, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.disassociateRobot(email, robotId, callbackSuccess, callbackError);
            	window.setTimeout(function() {
                    callbackSuccess("OK");
                }, 1000);
            },

            disassociateAllRobots : function(email, callbackSuccess, callbackError) {
                window.plugins.neatoPluginLayer.userMgr.disassociateAllRobots(email, callbackSuccess, callbackError);
            },
            
            changePassword: function(email, currentPassword, newPassword, callbackSuccess, callbackError) {
                window.plugins.neatoPluginLayer.userMgr.changePassword(email, currentPassword, newPassword, callbackSuccess, callbackError);
            }, 
            
            forgetPassword: function(email, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess(true);
                }, 1500);
            },
            
            
            turnNotificationOnoff: function(email, notificationId, enableNotification, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess("OK");
                }, 500);
            },
            
            getNotificationSettings: function(email, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess({"notifications":[{"value":false,"key":"102"},{"value":true,"key":"101"},{"value":false,"key":"103"}],"global":true});
                }, 500);
            }
            
        }
    }());

var RobotPluginManager = ( function() {
    /* neato example: Mon,Wen,Fri
    var schedulerEvents = {
        "schedules":[
            {"startTime": "10:30", "area":"Kitchen", "eventType":1, "day":[1,2,5], "endTime":"12:30"}
        ],
        "scheduleId":"262"
    }
    */
    /* neato example: Only Weekends */
    var schedulerEvents = {
        "schedules":[
            {"startTime": "12:00", "area":"Kitchen", "eventType":1, "day":[6,0], "endTime":"14:00"},
            {"startTime": "15:00", "area":"Garage", "eventType":1, "day":[6,0], "endTime":"18:00"},
        ],
        "scheduleId":"262"
    }
    
       return {
            discoverNearbyRobots : function(callbackSuccess, callbackError) {
                window.plugins.neatoPluginLayer.robotMgr.discoverNearbyRobots(callbackSuccess, callbackError);
            },
            tryDirectConnection : function(robotId, callbackSuccess, callbackError) {
                window.plugins.neatoPluginLayer.robotMgr.tryDirectConnection(robotId, callbackSuccess, callbackError);
            },
            disconnectDirectConnection : function(robotId, callbackSuccess, callbackError) {
                window.plugins.neatoPluginLayer.robotMgr.disconnectDirectConnection(robotId, callbackSuccess, callbackError);
            },
            
            getRobotCleaningCategory: function(robotId, callbackSuccess, callbackError) {
                 window.setTimeout(function() {
                     callbackSuccess({cleaningCatageory:2,robotId:robotId});
                 }, 1000);
            },

            sendCommandToRobot : function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess("OK");
                }, 1000);
                //window.plugins.neatoPluginLayer.robotMgr.sendCommandToRobot(robotId, commandId, commandParams, callbackSuccess, callbackError);
            },
            
            clearRobotData: function(email, robotId, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.disassociateRobot(email, robotId, callbackSuccess, callbackError);
                window.setTimeout(function() {
                    callbackSuccess("OK");
                }, 1000);
            },

            setRobotName : function(robotId, robotName, callbackSuccess, callbackError) {
              	 window.setTimeout(function() {
                     callbackSuccess("OK");
                 }, 1000);
                //window.plugins.neatoPluginLayer.robotMgr.setRobotName(robotId, robotName, callbackSuccess, callbackError);
            },
            
            setRobotName2 : function(robotId, robotName, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                     callbackSuccess({
                        "robotName" : robotName,
                        "robotId" : robotId
                    });
                 }, 1000);
            },
            
            getRobotDetail : function(robotId, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.robotMgr.getRobotDetail(robotId, callbackSuccess, callbackError);
                
                window.setTimeout(function() {
                    callbackSuccess({
                        "robotName" : "Map Demo",
                        "robotId" : "mapdemo123"
                    });
                }, 250);
            },            
            
            setSchedule : function(robotId, scheduleType, jsonArray, callbackSuccess, callbackError) {
                window.plugins.neatoPluginLayer.robotMgr.setSchedule(robotId, scheduleType, jsonArray, callbackSuccess, callbackError);
            },
            
            addEvent : function(robotId, scheduleType, eventArray, callbackSuccess, callbackError) {
            	$.each(eventArray, function(index, event){
            		schedulerEvents.push(event);
            	});
            	
            	callbackSuccess("OK");
            },
            
            removeEvent : function(robotId, scheduleType, eventArray, callbackSuccess, callbackError) {
            	$.each(eventArray, function(index, delEvent){
            		$.each(schedulerEvents, function(index, savedEvent){
	            		if(delEvent == savedEvent){
	            			schedulerEvents.splice(index,1);
	            			return true; //continue
	            		}
            		});
            	});
            	
            	callbackSuccess("OK");
            },

            getSchedule : function(robotId, scheduleType, callbackSuccess, callbackError) {
            	
            	if(scheduleType == 'advanced'){
            		
	            	window.setTimeout(function() {
	                    callbackSuccess(schedulerEvents);
	                }, 1000);
	            	
            	} else if(scheduleType == 'basic'){
            		
            		var days = new Array(0);
                	for(i = 0; i < 7; i++){
        				days.push({'day': i, 'time': '8:00'});
        			}
                	window.setTimeout(function() {
                        callbackSuccess(days);
                    }, 1000);
            	}
            	
            	
                //window.plugins.neatoPluginLayer.robotMgr.getSchedule(robotId, scheduleType, callbackSuccess, callbackError);
            },

            getMaps : function(robotId, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.robotMgr.getMaps(robotId, callbackSuccess, callbackError);
                window.setTimeout(function() {
                    callbackSuccess(testResult);
                }, 1000);
                /*
                 window.setTimeout(function() {
                 callbackError({
                 "errorMessage" : "Invalid JSON file",
                 "errorCode" : 1001
                 });
                 }, 2000);
                 */
            },
            /*
         * Name: startCleaning
         * Sends "start cleaning" command to the robot. 
         *  - robotId: Value as string. (Must be a valid robotId)
         *  - cleaningCategoryId: Value as predefined integer value (Must be one
         *    of CLEANING_CATEGORY_MANUAL, CLEANING_CATEGORY_ALL Or CLEANING_CATEGORY_SPOT)
         *  - cleaningModeId : Value as predefined integer value (Must be one CLEANING_MODE_ECO
         *    Or CLEANING_MODE_NORMAL)
         *  - cleaningModifier - Value as String (for e.g. 1, 2)
         * In case of success, callbackSuccess is called with response as OK.
         * In case of an error, callbackError is called with error JSON below:
         *  {'errorCode':<error code>, 'errorMessage':<error msg>}
         */
        startCleaning: function(robotId, cleaningCategoryId, cleaningModeId, cleaningModifier,
                    callbackSuccess, callbackError) {
             window.setTimeout(function() {
                callbackSuccess("OK");
                // timed mode
                //callbackSuccess({"expectedTimeToExecute":12});
            }, 1000);
        },
        
        /*
         * Name: stopCleaning
         * Sends "stop cleaning" command to the robot. 
         * Params
         *  - robotId: Value as string. (Must be a valid robotId)
         * In case of success, callbackSuccess is called with response as OK.
         * In case of an error, callbackError is called with error JSON below:
         *  {'errorCode':<error code>, 'errorMessage':<error msg>}
         */
        stopCleaning: function(robotId, callbackSuccess, callbackError) {
             window.setTimeout(function() {
                callbackSuccess("OK");
            }, 1000);
        },
        
        /**
         * This API sends a command to a specific robot. The robot and smart app have to be on the
         * same network for a successful connection. Though this can be used to send commands to the robot
         * other API for common commands are also exposed. Commands like "Start Cleaning", "Stop Cleaning"
         * "Pause Cleaning" and "Resume Cleaning" must be called from the cleaning specific API rather
         * than sendCommandToRobot2. Currently this API always sends command via a presence server (XMPP)
         * It does not use direct connection as of now.
         * <p>
         * This API calls Neato Smart App Service to send command to robot.The command id is the id of the
         * command to be executed on the robot. Currently supported commands are - 
         * <br>101 - Start Cleaning
         * <br>102 - Stop Cleaning
         * 
         * @param robotId           the serial number of the robot
         * @param commandId         command ID of the command to be executed on this robot.
         * @param commandParams     the json object containing key value pairs related to the command to be executed. 
         * @param callbackSuccess   success callback for the API
         * @param callbackError     error callback for the API
         */     
        sendCommandToRobot2: function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
            var stateCode = ROBOT_STATE_IDLE;
            var delay = 1000;
            switch(robotId) {
                case "mapdemo123":
                    stateCode = ROBOT_STATE_CLEANING
                    delay = 3000;
                    break;
                case "rr1234":
                    stateCode = ROBOT_STATE_PAUSED
                    delay = 10000;
                    break;
            }
            
            
            
             window.setTimeout(function() {
                callbackSuccess({
                    "dateTimeString":"06-06-2013 20:20",
                    "currentStateString":"paused",
                    "stateCode":stateCode,
                    "robotId": robotId
                });
            }, delay);
            
        },
        
        /**
         * This API gets the current state of the robot
         *  on success this API returns a JSON Object
         * <br>{robotCurrentState:"robotCurrentState", robotId:"robotId"}
         * <br>robotCurrentState is an integer value of the current actual state of the robot
         * <br>robotId is the serial number of the robot
         * <p>
         * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
         * @param robotId           the serial number of the robot
         * @param callbackSuccess   success callback for this API
         * @param callbackError     error callback for this API
         */

        getRobotCurrentStateDetails: function(robotId, callbackSuccess, callbackError) {
            //window.plugins.neatoPluginLayer.robotMgr.getRobotCurrentState(robotId, callbackSuccess, callbackError);
            window.setTimeout(function() {
                callbackSuccess({"robotId":robotId,
                "robotCurrentStateDetails":{                
                "robotCurrentState": "1", 
                    "robotStateParams": {
                     "CrntErrorCode": 22000,
                     "RobotIsDocked": 0,
                     "ClockIsSet": 0,
                     "DockHasBeenSeen": 0,
                     "IsCharging": 1,
                     "robotCleaningCategory": 0,
                     "robotCleaningMode": 1,
                     "robotCleaningModifier": 0,
                     "robotSpotCleaningAreaLength":0,
                     "robotSpotCleaningAreaHeight":0
                     }}})
             }, 100);
        },
        
        getRobotCurrentState: function(robotId, callbackSuccess, callbackError) {
            var stateCode = ROBOT_STATE_IDLE;
            var delay = 1000;
            switch(robotId) {
                case "mapdemo123": // Map Demo
                    stateCode = ROBOT_STATE_CLEANING
                    delay = 3000;
                    break;
                case "rr1234": // Nexus One
                    stateCode = ROBOT_STATE_PAUSED
                    delay = 10000;
                    break;
                case "testo":
                    stateCode = ROBOT_STATE_PAUSED
                    delay = 4000;
                    break;
            }
            
            
            
             window.setTimeout(function() {
                callbackSuccess({
                    "robotCurrentState":stateCode,
                    "robotNewVirtualState":stateCode,
                    "robotId": robotId
                });
            }, delay);
        },
        
        getRobotOnlineStatus: function(robotId, callbackSuccess, callbackError) {
            
             window.setTimeout(function() {
                callbackSuccess({
                    "online":true,
                    "robotId": robotId
                });
            }, 100);
        },
        /*
         * Name: pauseCleaning
         * Sends "pause cleaning" command to the robot. This internally calls 
         * sendCommandToRobot2 method of "window.plugins.neatoPluginLayer.robotMgr"
         * Params
         *  - robotId: Value as string. (Must be a valid robotId)
         * In case of success, callbackSuccess is called with response as OK.
         * In case of an error, callbackError is called with error JSON below:
         *  {'errorCode':<error code>, 'errorMessage':<error msg>}
         */
        pauseCleaning: function(robotId, callbackSuccess, callbackError) {
             window.setTimeout(function() {
                callbackSuccess("OK");
            }, 1000);
        },

        /*
         * Name: resumeCleaning
         * Sends "resume cleaning" command to the robot. This internally calls 
         * sendCommandToRobot2 method of "window.plugins.neatoPluginLayer.robotMgr" 
         * Params
         *  - robotId: Value as string. (Must be a valid robotId)
         * In case of success, callbackSuccess is called with response as OK.
         * In case of an error, callbackError is called with error JSON below:
         *  {'errorCode':<error code>, 'errorMessage':<error msg>}
         */ 
        resumeCleaning: function(robotId, callbackSuccess, callbackError) {
             window.setTimeout(function() {
                callbackSuccess("OK");
            }, 1000);
        },
    
        /*
         * Name: setSpotDefinition
         * Saves the spot definition to the DB. This internally calls setSpotDefinition
         * method of "window.plugins.neatoPluginLayer.robotMgr" 
         * Params
         *  - robotId : Value as string. (Must be a valid robotId)
         *  - spotCleaningAreaLength : Value as integer.
         *  - spotCleaningAreaHeight : Value as integer.
         * In case of success, callbackSuccess is called with response as OK.
         * In case of an error, callbackError is called with error JSON below:
         *  {'errorCode':<error code>, 'errorMessage':<error msg>}
        */  
        setSpotDefinition: function(robotId, spotCleaningAreaLength, spotCleaningAreaHeight, 
                    callbackSuccess, callbackError) {
             window.setTimeout(function() {
                callbackSuccess("OK");
            }, 1000);
        },
        
        /*
         * Name: getSpotDefinition
         * Saves the spot definition to the DB. This internally calls getSpotDefinition
         * method of "window.plugins.neatoPluginLayer.robotMgr" 
         * Params
         *  - robotId: Value as string. (Must be a valid robotId)
         * In case of success, callbackSuccess is called with following JSON:
         *  {'spotCleaningAreaLength':<area length>, 'spotCleaningAreaHeight':<area height>}
         * In case of an error, callbackError is called with error JSON below:
         *  {'errorCode':<error code>, 'errorMessage':<error msg>}
         */
        getSpotDefinition: function(robotId, callbackSuccess, callbackError) {
            // window.setTimeout(function() {
                // callbackError({"errorCode":1101, "errorMessage":"no spot size defined yet"});
            // }, 1000);
            window.setTimeout(function() {
                callbackSuccess({'spotCleaningAreaLength':2, 'spotCleaningAreaHeight':3});
            }, 1000);
            
        },

        /*
         * Name: driveRobot
         * Sends "drive" command to the robot. This internally calls driveRobot 
         * method of "window.plugins.neatoPluginLayer.robotMgr" 
         * Params
         *  - robotId: Value as string. (Must be a valid robotId)
         *  - navigationControlId: Valuse as integer. Value must (Must be one of
         *    NAVIGATION_CONTROL_1, NAVIGATION_CONTROL_2, NAVIGATION_CONTROL_3
         *    NAVIGATION_CONTROL_4, NAVIGATION_CONTROL_5 and NAVIGATION_CONTROL_6)
         * In case of success, callbackSuccess is called with response as OK.
         * In case of an error, callbackError is called with error JSON below:
         *  {'errorCode':<error code>, 'errorMessage':<error msg>}
         */
            driveRobot: function(robotId, navigationControlId, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess(true);
                }, 1000);
            },
            
            intendToDrive: function(robotId, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess(true);
                }, 1000);
            },
            cancelIntendToDrive: function(robotId, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess(true);
                }, 300);
            },
            stopRobotDrive: function(robotId, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess(true);
                }, 300);
            },

            setMapOverlayData : function(robotId, mapId, mapOverlayInfo, callbackSuccess, callbackError) {
                window.plugins.neatoPluginLayer.robotMgr.setMapOverlayData(robotId, mapId, mapOverlayInfo, callbackSuccess, callbackError);
            },

            // It will give the atlas xml data.
            getRobotAtlasMetadata : function(robotId, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.robotMgr.getRobotAtlasMetadata(robotId, callbackSuccess, callbackError);
                window.setTimeout(function() {
                    callbackSuccess([{
                        "atlasMetadata" : {
                            "geographies" : [{
                                "id" : "0",
                                "base" : [[158, 233, 192, 237], [503, 243, 537, 247]],
                                "nogo" : [[120,30,150,45],[65,110,85,140]],
                                "boundingBox" : [0, 0, 525, 360],
                                "visibleMap" : {
                                    "img" : ""
                                },
                                "name" : "TwoSimpleRooms",
                                "rooms" : [{
                                    "id" : "0",
                                    "coord" : [{
                                        "y" : 0,
                                        "x" : 0
                                    }, {
                                        "y" : 0,
                                        "x" : 360
                                    }, {
                                        "y" : 240,
                                        "x" : 360
                                    }, {
                                        "y" : 240,
                                        "x" : 0
                                    }],
                                    "icon" : "ICON.EMPTY",
                                    "boundingBox" : [0, 0, 360, 240],
                                    "color" : "COLOR.BLUE",
                                    "name" : "Room1"
                                }, {
                                    "id" : "1",
                                    "coord" : [{
                                        "y" : 160,
                                        "x" : 360
                                    }, {
                                        "y" : 160,
                                        "x" : 525
                                    }, {
                                        "y" : 360,
                                        "x" : 525
                                    }, {
                                        "y" : 360,
                                        "x" : 375
                                    }, {
                                        "y" : 240,
                                        "x" : 375
                                    }, {
                                        "y" : 240,
                                        "x" : 360
                                    }],
                                    "icon" : "ICON.EMPTY",
                                    "boundingBox" : [360, 160, 525, 360],
                                    "color" : "COLOR.BLUE",
                                    "name" : "Room2"
                                }]
                            }]
                        },
                        "atlasId" : "86"
                    }]);
                }, 1000);

            },

            // It will update the atlas mapped to this robotId. The version of the xml is stored inside.
            updateAtlasMetaData : function(robotId, atlasMetadata, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.robotMgr.updateAtlasMetaData(robotId, atlasMetadata, callbackSuccess, callbackError);
                callbackSuccess(true);
            },

            // TODO: We are taking robotId. Analyse if taking atlasId is a better option.
            getAtlasGridData : function(robotId, gridId, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.robotMgr.getAtlasGridData(robotId, gridId, callbackSuccess, callbackError);
                window.setTimeout(function() {
                    callbackSuccess(
                        //[{"gridId":"86","gridData":"file:///storage/emulated/0/Android/data/com.neatorobotics.android.slide.phonegap.ui/cache/atlas_data/86_nexus77/grid.xml","atlasId":"86"}]
                        [{"gridId":"86","gridData":"storage/emulated/0/Android/data/com.neatorobotics.android.slide.phonegap.ui/cache/atlas_data/86_nexus77/grid.xml","atlasId":"86"}]
                    );
                }, 4000);
            },
            
            registerNotifications: function(robotId, callbackSuccess, callbackError) {
              window.setTimeout(function() {
                    callbackSuccess(
                        {   'eventId': '20001',
                            'robotId': 'transformer23', 
                            'params': {"register" : true}
                        }
                    );
                }, 1000);
            },
            
            unregisterNotifications: function(robotId, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess(
                        {   'eventId': '20001',
                            'robotId': 'transformer23', 
                            'params': {"unregister" : true}
                        }
                    );
                }, 1000);
            },
            
            /**
             * This API registers for notifications. As of now this API registers for all type of
             * notifications. This API does not make a webservice call.
             * <p>
             * on success this API returns a JSON Object {robotDataKeyId:"robotDataKeyId", robotId:"robotId", robotData:"robotData"}
             * <br>where robotDataKeyId is the data key id for the robot
             * <br>robotId is the id of the robot
             * <br>robotData is the data sent by robot
             * 
             * @param callbackSuccess   success callback for this API
             * @param callbackError     error callback for this API 
             * @returns                 a JSON Object on success
             */
            registerNotifications2: function(callbackSuccess, callbackError) {
                // window.setTimeout(function() {
                    // callbackSuccess(true);
                // }, 1000);
            },
            
            /*
             * Name: registerForRobotMessages
             * Support to notify push messages sent by server to UI.
             * In error case error callback gets called with error JSON object
             */
            registerForRobotMessages: function(callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.robotMgr.registerForRobotMessages(callbackSuccess, callbackError);
                // window.setTimeout(function() {
                    // callbackSuccess(true);
                // }, 4000);
            },
            
            // New Schedule APIs being added:
            getScheduleEvents: function(robotId, scheduleType, callbackSuccess, callbackError) {
                /*
                window.setTimeout(function() {
                    callbackError(
                        {
                            "errorMessage":"No schedule data found for this robot",
                            "errorCode":-21228
                        }
                    );
                }, 500);
                */
                /*
                window.setTimeout(function() {
                    callbackError(
                        {
                            "errorMessage":"No schedule data found for this robot",
                            "errorCode":-159
                        }
                    );
                }, 500);
                */
                window.setTimeout(function() {
                    callbackSuccess(
                        {   'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
                            'robotId': 'transformer23', 
                            'scheduleType':0 , //0: basic 1:advanced 
                            'scheduleEventLists': [
                                "76d784e0-78a2-45e0-a67a-3f404eecafc8",
                                "5d31a4e8-5eca-41b8-87bc-fa10a13c4152",
                                "5d31a4e8-5eca-41b8-87bc-fa10a13c4151",
                                
                            ]
                        }
                    );
                }, 1000);
            },
            // updateScheduleEvent('955fe88b-061f-4cc0-9f2b-c4baa73b156a', '76d784e0-78a2-45e0-a67a-3f404eecafc8', {'startTime':'12:30','day':2}, callbackSuccess, callbackError)
            updateScheduleEvent: function(scheduleId, scheduleEventId, scheduleEventData, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.robotMgr.updateScheduleEvent(scheduleId, scheduleEventId, scheduleEventData, callbackSuccess, callbackError);
                callbackSuccess(true);
            },
            deleteScheduleEvent: function(scheduleId, scheduleEventId, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.robotMgr.deleteScheduleEvent(scheduleId, scheduleEventId, callbackSuccess, callbackError);
                switch(scheduleEventId) {
                    case "76d784e0-78a2-45e0-a67a-3f404eecafc8":
                        window.setTimeout(function() {
                            callbackSuccess(
                                {   
                                    'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
                                    'scheduleEventId':'76d784e0-78a2-45e0-a67a-3f404eecafc8'
                                }
                            );
                        }, 5000);
                    break;
                    case "5d31a4e8-5eca-41b8-87bc-fa10a13c4152":
                        window.setTimeout(function() {
                            callbackSuccess(
                                {   
                                    'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
                                    'scheduleEventId':'5d31a4e8-5eca-41b8-87bc-fa10a13c4152'
                                    
                                }
                            );
                        }, 1500);
                    break;
                    case "5d31a4e8-5eca-41b8-87bc-fa10a13c4151":
                        window.setTimeout(function() {
                            callbackSuccess(
                                {   
                                    'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
                                    'scheduleEventId':'5d31a4e8-5eca-41b8-87bc-fa10a13c4151'
                                    
                                }
                            );
                        }, 2500);
                    break;
                }
                
            },
            // addScheduleEvent('955fe88b-061f-4cc0-9f2b-c4baa73b156a', {'startTime':'10:30','day':1}, callbackSuccess, callbackError)
            addScheduleEvent: function(scheduleId, scheduleEventData, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess(
                        {   
                            'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
                            'scheduleEventId':'5d31a4e8-5eca-41b8-87bc-fa10a13c4152'
                        }
                    );
                }, 1000);
            },
            
            getScheduleEventData: function(scheduleId, scheduleEventId, callbackSuccess, callbackError) {
                // Sunday – 0
                // Monday– 1
                // Tuesday – 2
                // Wednesday – 3
                // Thursday – 4
                // Friday – 5
                // Saturday – 6
                switch(scheduleEventId) {
                    case "76d784e0-78a2-45e0-a67a-3f404eecafc8":
                        window.setTimeout(function() {
                            callbackSuccess(
                                {   
                                    'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
                                    'scheduleEventId':'76d784e0-78a2-45e0-a67a-3f404eecafc8', 
                                    'scheduleEventData': {'startTime':'0:00','day':1, 'cleaningMode':CLEANING_MODE_NORMAL}
                                }
                            );
                        }, 500);
                    break;
                    case "5d31a4e8-5eca-41b8-87bc-fa10a13c4152":
                        window.setTimeout(function() {
                            callbackSuccess(
                                {   
                                    'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
                                    'scheduleEventId':'5d31a4e8-5eca-41b8-87bc-fa10a13c4152', 
                                    'scheduleEventData': {'startTime':'5:30','day':2, 'cleaningMode':CLEANING_MODE_ECO}
                                }
                            );
                        }, 200);
                    break;
                    case "5d31a4e8-5eca-41b8-87bc-fa10a13c4151":
                        window.setTimeout(function() {
                            callbackSuccess(
                                {   
                                    'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
                                    'scheduleEventId':'5d31a4e8-5eca-41b8-87bc-fa10a13c4151', 
                                    'scheduleEventData': {'startTime':'1:30','day':0, 'cleaningMode':CLEANING_MODE_ECO}
                                }
                            );
                        }, 300);
                    break;
                }
                
            },
            createSchedule: function(robotId, scheduleType, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.robotMgr.createSchedule(robotId, scheduleType, callbackSuccess, callbackError);
                window.setTimeout(function() {
                    callbackSuccess(
                        {   'scheduleType':0 , //0: basic 1:advanced
                            'robotId': robotId,
                            'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a'
                        }
                    );
                }, 1000);
            },
            updateSchedule: function(scheduleId, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.robotMgr.updateSchedule(scheduleId, callbackSuccess, callbackError);
                window.setTimeout(function() {
                    callbackSuccess(true);
                }, 1000);
            },
            
            // helper functions
            getScheduleData: function(scheduleId, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess(
                        {   'scheduleType':0 , //0: basic 1:advanced
                            'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
                            'schedules': [
                                {'startTime':'10:30','day':1}
                            ]
                        }
                    );
                }, 1000);
            },
            
            isScheduleEnabled: function(robotId, scheduleType, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess(
                        {
                            'isScheduleEnabled':true,
                            'scheduleType':SCHEDULE_TYPE_BASIC,
                            'robotId':'transformer23'
                         }
                    );
                }, 1000);
            },
            
            enableSchedule: function(robotId, scheduleType, enable, callbackSuccess, callbackError) {
                //{isScheduleEnabled:"isScheduleEnabled", scheduleType:"scheduleType", robotId:"robotId"}
                window.setTimeout(function() {
                    callbackSuccess(
                        {
                            'isScheduleEnabled':!enable,
                            'scheduleType':SCHEDULE_TYPE_BASIC,
                            'robotId':'transformer23'
                         }
                    );
                }, 1000);
            },
        }
    }());

var PluginManagerHelper = ( function() {
        return {
            addToAdvancedSchedule : function(scheduleJsonArray, day, startTime, endTime, eventType, area) {

                var schedule = {
                    'day' : day,
                    'startTime' : startTime,
                    'endTime' : endTime,
                    'eventType' : eventType,
                    'area' : area
                };
                scheduleJsonArray.push(schedule);
                //scheduleJsonObject["schedule"] = schedule;
                return scheduleJsonArray;
            },
            getBasicScheduleEvent: function(day, startTime) {
                var schedule = {'day':day, 'startTime': startTime};
                return schedule;
            },
            createBasicScheduleEventObject: function(day, startTime, cleaningMode) {
                var schedule = {'day':day, 'startTime': startTime, 'cleaningMode':cleaningMode};
                return schedule;
            },
            
            getAdvancedScheduleEvent: function(day, startTime, endTime, eventType, area) {
                var schedule = {'day':day, 'startTime': startTime, 
                        'endTime': endTime, 'eventType': eventType,
                        'area':area};
                return schedule;
            }
        }
    }());

