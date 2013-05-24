
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

//robotNotifications2 keyCodes

// The current state of the robot
var ROBOT_CURRENT_STATE_CHANGED 	= 4001;
// The keyCode for the state update of the robot.
var ROBOT_STATE_UPDATE				= 4003;
// The keyCode for the name update of the robot.
var ROBOT_NAME_UPDATE  				= 4004;
// The keyCode for the schedulestate update of the robot.
var ROBOT_SCHEDULE_STATE_CHANGED 	= 4005;
// The keyCode for the schedule is updated notification
var ROBOT_SCHEDULE_UPDATED 			= 4006;

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
var ACTION_TYPE_LOGIN 							= "login";
var ACTION_TYPE_LOGOUT 							= "logout";
var ACTION_TYPE_ISLOGGEDIN 						= "isLoggedIn";
var ACTION_TYPE_CREATE_USER 					= "createUser";
var ACTION_TYPE_CREATE_USER2					= "createUser2";
var ACTION_TYPE_RESEND_VALIDATION_MAIL			= "resendValidationMail";
var ACTION_TYPE_IS_USER_VALIDATED				= "isUserValidated";
var ACTION_TYPE_GET_USER_DETAILS 				= "getUserDetails";
var ACTION_TYPE_ASSOCIATE_ROBOT					= "associateRobot";
var ACTION_TYPE_GET_ASSOCIATED_ROBOTS 			= "getAssociatedRobots";
var ACTION_TYPE_DISASSOCIATE_ROBOT 				= "disassociateRobot";
var ACTION_TYPE_DISASSOCAITE_ALL_ROBOTS 		= "disassociateAllRobots";
var ACTION_TYPE_FORGET_PASSWORD					= "forgetPassword";
var ACTION_TYPE_CHANGE_PASSWORD					= "changePassword";
var ACTION_TYPE_REGISTER_FOR_ROBOT_MESSAGES		= "registerForRobotMessges";
var ACTION_TYPE_UNREGISTER_FOR_ROBOT_MESSAGES	= "unregisterForRobotMessages";
// List of actions types of Robot Manager
var ACTION_TYPE_DISCOVER_NEARBY_ROBOTS 			= "discoverNearByRobots";
var ACTION_TYPE_TRY_CONNECT_CONNECTION 			= "tryDirectConnection";
var ACTION_TYPE_TRY_CONNECT_CONNECTION2			= "tryDirectConnection2";
var ACTION_TYPE_SEND_COMMAND_TO_ROBOT 			= "sendCommandToRobot";
var ACTION_TYPE_SEND_COMMAND_TO_ROBOT2 			= "sendCommandToRobot2";
var ACIION_TYPE_SET_SCHEDULE 					= "robotSetSchedule";
var ACIION_TYPE_GET_ROBOT_SCHEDULE 				= "getSchedule";
var ACTION_TYPE_GET_ROBOT_MAP 					= "getRobotMap";
var ACTION_TYPE_SET_MAP_OVERLAY_DATA  			= "setMapOverlayData";
var ACTION_TYPE_DISCONNECT_DIRECT_CONNETION		= "disconnectDirectConnection";
var ACTION_TYPE_GET_ROBOT_ATLAS_METADATA 		= "getRobotAtlasMetadata";
var ACTION_TYPE_UPDATE_ROBOT_ATLAS_METADATA		= "updateRobotAtlasMetadata";
var ACTION_TYPE_GET_ATLAS_GRID_DATA 			= "getAtlasGridData";
var ACTION_TYPE_SET_ROBOT_NAME					= "setRobotName";
var ACTION_TYPE_DELETE_ROBOT_SCHEDULE			= "deleteScheduleData";
var ACTION_TYPE_SET_ROBOT_NAME_2				= "setRobotName2";
var ACTION_TYPE_GET_ROBOT_DETAIL				= "getRobotDetail";
var ACTION_TYPE_GET_ROBOT_ONLINE_STATUS			= "getRobotOnlineStatus";
var ACTION_TYPE_GET_ROBOT_VIRTUAL_ONLINE_STATUS 	= "getRobotVirtualOnlineStatus";
var ACTION_TYPE_REGISTER_ROBOT_NOTIFICATIONS    	= "registerRobotNotifications";
var ACTION_TYPE_UNREGISTER_ROBOT_NOTIFICATIONS  	= "unregisterRobotNotifications";
var ACTION_TYPE_REGISTER_ROBOT_NOTIFICATIONS_2    	= "registerRobotNotifications2";
var ACTION_TYPE_UNREGISTER_ROBOT_NOTIFICATIONS_2  	= "unregisterRobotNotifications2";
var ACTION_TYPE_SET_SPOT_DEFINITION				= "setSpotDefinition";
var ACTION_TYPE_GET_SPOT_DEFINITION				= "getSpotDefinition";
var ACTION_TYPE_DRIVE_ROBOT						= "driveRobot";
var ACTION_TYPE_TURN_VACUUM_ON_OFF				= "turnVacuumOnOff";
var ACTION_TYPE_TURN_WIFI_ON_OFF				= "turnWiFiOnOff";
var ACTION_TYPE_TURN_NOTIFICATION_ON_OFF		= "turnNotificationOnOff";
var ACTION_TYPE_IS_NOTIFICATION_ENABLED			= "isNotificationEnabled";
var ACTION_TYPE_GET_NOTIFICATION_SETTINGS		= "getNotificationSettings";

var ACTION_TYPE_START_CLEANING					= "startCleaning";
var ACTION_TYPE_STOP_CLEANING					= "stopCleaning";
var ACTION_TYPE_PAUSE_CLEANING					= "pauseCleaning";
var ACTION_TYPE_RESUME_CLEANING					= "resumeCleaning";



// New schedule APis 
var ACTION_TYPE_UPDATE_SCHEDULE 				= "updateSchedule";
var ACTION_TYPE_DELETE_ROBOT_SCHEDULE_EVENT 	= "deleteScheduleEvent";
var ACTION_TYPE_UPDATE_ROBOT_SCHEDULE_EVENT 	= "updateScheduleEvent";
var ACTION_TYPE_GET_SCHEDULE_EVENT_DATA 		= "getScheduleEventData";
var ACTION_TYPE_ADD_ROBOT_SCHEDULE_EVENT 		= "addScheduleEventData";
var ACTION_TYPE_GET_SCHEDULE_EVENTS 			= "getScheduleEvents";
var ACTION_TYPE_GET_SCHEDULE_DATA 				= "getScheduleData";
var ACTION_TYPE_CREATE_SCHEDULE 				= "createSchedule";
var ACTION_TYPE_IS_SCHEDULE_ENABLED 			= "isScheduleEnabled";
var ACTION_TYPE_ENABLE_SCHEDULE				= "enableSchedule";

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
var CLEANING_MODE_ECO = 1;
var CLEANING_MODE_NORMAL = 2;

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

if(!window.plugins) {
	window.plugins = {};
}

if (!window.plugins.neatoPluginLayer) {
	window.plugins.neatoPluginLayer = new NeatoPluginLayer();
}

if (!window.plugins.neatoPluginLayer.userMgr) {
	window.plugins.neatoPluginLayer.userMgr = new UserMgr();
}

if (!window.plugins.neatoPluginLayer.robotMgr) {
	window.plugins.neatoPluginLayer.robotMgr = new RobotMgr();
}

function NeatoPluginLayer() {
	this.userMgr = null;
	this.robotMgr = null;
};


function UserMgr() {

};

function RobotMgr() {

};

/**
 * This API logs the user in using the email and password provided by the user.
 * <p>
 * on success this API returns a JSON Object
 * <br>{email:"emailAddress, userName:"userName", userId:"userId", validation_status:"validationStatus"}
 * <br>where emailAddress is user email address
 * <br>userName is the user's name
 * <br>userId is the user's Id 
 * <br>validation status would be among the following values
 * <br>USER_STATUS_VALIDATED - user is validated and logged into the app
 * <br>USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD - user is not validated but can log in for a brief amount of time into the app
 * <br>USER_STATUS_NOT_VALIDATED - user is not validated and cannot log into the app
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode
 * 
 * @param email				the email address of the user
 * @param password			the password of the user
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 * @returns					returns JSON Object
 */
UserMgr.prototype.loginUser = function(email, password, callbackSuccess, callbackError) {
	var loginArray = {'email':email, 'password':password};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_LOGIN, [loginArray]);
};

/**
 * This API logs the user out of the system. It also clears all the user logged in
 * information locally. This API does not return any value
 * 
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 */
UserMgr.prototype.logoutUser = function(callbackSuccess, callbackError) {
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_LOGOUT, []);
};

/**
 * This API checks if a user is already logged into the app
 * 
 * @param email				the email address of the user
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API 
 * @returns					true if the user is logged in, false otherwise
 */
UserMgr.prototype.isUserLoggedIn = function(email, callbackSuccess, callbackError) {
	var isUserLoggedInArray = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_ISLOGGEDIN, [isUserLoggedInArray]);
};

/**
 * This API creates a new user. It does not trigger email validation. Server
 * assumes whatever email is provided is already validated if it exists. Use
 * createUser2 to create user instead
 * <p>
 * on success this API returns a JSON Object
 * <br>{email:"emailAddress", userName:"userName", userId:"userId", validation_status:"validationStatus"}
 * <br>where emailAddress is the email address of the user
 * <br>userName is the user's name
 * <br>userId is the user id
 * <br>validation status can be among the following values
 * <br>USER_STATUS_VALIDATED - user is validated and logged into the app
 * <br>USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD - user is not validated but can log in for a brief amount of time into the app
 * <br>USER_STATUS_NOT_VALIDATED - user is not validated and cannot log into the app
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode
 * 
 * @param email				the email address of the user
 * @param password			the password of the user
 * @param name 				name of the user
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 * @returns					returns a json object
 * @deprecated				replaced by {@link #createUser2(email, password, name, alternateEmail, callbackSuccess, callbackError)}
 * @see						#createUser2(email, password, name, alternateEmail, callbackSuccess, callbackError)
 */
UserMgr.prototype.createUser = function(email, password, name, callbackSuccess, callbackError) {
	var registerArray = {'email':email, 'password':password, 'userName':name};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_CREATE_USER, [registerArray]);
};

/**
 * This API creates a new user by triggering email validation. Use 
 * this API to create new users.
 * <p>
 * on success this API returns a JSON Object
 * <br>{email:"emailAddress", alternate_email:"alternateEmailAddress", userName:"userName", userId:"userId", validation_status:"validationStatus"}
 * <br>where emailAddress is the email Address of the user
 * <br>alternateEmailAddress is the alternate email Address of the user
 * <br>userName is the name of the user
 * <br>userId is the user id of the user 
 * <br>validation status could be among the following values
 * <br>USER_STATUS_VALIDATED - user is validated and logged into the app
 * <br>USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD - user is not validated but can log in for a brief amount of time into the app
 * <br>USER_STATUS_NOT_VALIDATED - user is not validated and cannot log into the app
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode
 * 
 * @param email				the email address of the user
 * @param password			the password of the user
 * @param name				name of the user
 * @param alternateEmail	the alternate email id of the user (optional parameter)
 * @param callbackSuccess 	the success callback for this API
 * @param callbackError 	the error callback for this API
 * @returns					returns a json object
 */
UserMgr.prototype.createUser2 = function(email, password, name, alternateEmail, callbackSuccess, callbackError) {
	var registerArray = {'email':email, 'password':password, 'userName':name, 'alternate_email':alternateEmail};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_CREATE_USER2, [registerArray]);
};

/**
 * Resend the validation email to the user email id.
 * <p>
 * on success this API returns a JSON Object {message:"We have resent validation mail"}
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errorMessage:"errorMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode
 * 
 * @param email				the email address of the user
 * @param callbackSuccess 	the success callback for this API
 * @param callbackError 	the error callback for this API
 * @returns					a json object containing a message string
 */
UserMgr.prototype.resendValidationMail = function(email, callbackSuccess, callbackError) {
	var registerArray = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_RESEND_VALIDATION_MAIL, [registerArray]);
};

/**
 * This API checks if a user is validated or not
 * <p>
 * on success this API returns a JSON Object {validation_status:"validationStatus", message:"message"}
 * <br>validationStatus would be one among the following values
 * <br>USER_STATUS_VALIDATED - user is validated and logged into the app
 * <br>USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD - user is not validated but can log in for a brief amount of time into the app
 * <br>USER_STATUS_NOT_VALIDATED - user is not validated and cannot log into the app
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode
 * 
 * @param email				the email address of the user
 * @param callbackSuccess	the success callback for this API
 * @param callbackError 	the error callback for this API
 * @returns					a json object
 */
UserMgr.prototype.isUserValidated = function(email, callbackSuccess, callbackError) {
	var isUserValidJsonArray = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_IS_USER_VALIDATED, [isUserValidJsonArray]);
};

/**
 * This API changes the user's password.
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode 
 * 
 * @param email				the email address of the user
 * @param currentPassword 	the old password of the user
 * @param newPassword		the new password of the user
 * @param callbackSuccess 	the success callback of this API
 * @param callbackError 	the error callback of this API
 * @returns					a JSON Object on error
 */
UserMgr.prototype.changePassword = function(email, currentPassword, newPassword, callbackSuccess, callbackError) {
	var changePassword = {'email':email, 'currentPassword':currentPassword, 'newPassword':newPassword};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_CHANGE_PASSWORD, [changePassword]);
};

/**
 * This API takes user email address and asks server to send a mail to recover password
 * for this user.
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode
 * 
 * @param email				the email address of the user
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @returns					JSON Object on error
 */
UserMgr.prototype.forgetPassword = function(email, callbackSuccess, callbackError) {
	var forgetPassword = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_FORGET_PASSWORD, [forgetPassword]);
};

/**
 * This API returns a JSON Object containing user details.
 * <p>
 * result is {email:"emailAddress", userName:"userName", userId:"userId"}
 * <br>where emailAddress is user email address
 * <br>userName is the name of the user
 * <br>userId is the id of the user
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode
 * 
 * @param email				the email address of the user
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @returns					JSON Object
 */
UserMgr.prototype.getUserDetail = function(email, callbackSuccess, callbackError) {
	var getUserDetailsArray = {'email': email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_USER_DETAILS, [getUserDetailsArray]);
};

/**
 * This API associates a robot with a user
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode
 * 
 * @param email				the email address of the user
 * @param robotId			the robotId of the robot to be associated
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 * @returns					JSON Object on error
 */
UserMgr.prototype.associateRobotCommand = function(email, robotId, callbackSuccess, callbackError) {
	var associateArray = {'email':email, 'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_ASSOCIATE_ROBOT, [associateArray]);
};

/**
 * This API gets a list of associated robots for a user in JSON
 * <p>
 * This API returns on success a JSON Array of which each element is like
 * <br>{robotId:"robotId", robotName:"robotName"}
 * <br>where robotId is robot's id
 * <br>robotName is robot's name
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode 
 * 
 * @param email 			the email address of the user
 * @param callbackSuccess 	success callback of the API
 * @param callbackError 	error callback of the API
 * @returns					a JSON Array on success or a JSON Object on error
 */
UserMgr.prototype.getAssociatedRobots = function(email, callbackSuccess, callbackError) {
	var getAssociatedRobotsArray = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ASSOCIATED_ROBOTS, [getAssociatedRobotsArray]);
};

/**
 * Disassociate a robot from a specified user.
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode 
 * 
 * @param email 			the email address of the user
 * @param robotId			the robot Id of the robot (robot serial number)
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @returns					A JSON Object on error
 */
UserMgr.prototype.disassociateRobot = function(email, robotId, callbackSuccess, callbackError) {
	var disassociateRobotArray = {'email':email, 'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DISASSOCIATE_ROBOT, [disassociateRobotArray]);
};

/**
 * This API disassociates all the robots for the specified user
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode 
 * 
 * @param email 			the email address of the user
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @returns					a JSON Object on error
 */
UserMgr.prototype.disassociateAllRobots = function(email, callbackSuccess, callbackError) {
	var disassociateAllRobotsArray = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DISASSOCAITE_ALL_ROBOTS, [disassociateAllRobotsArray]);
};

/**
 * This API is used to switch on/off global and individual push notification settings
 * <br>Notification ID specifies three types of notifications currently supported
 * <br>101 - Robot needs cleaning
 * <br>102 - Cleaning is done
 * <br>103 - Robot is stuck
 * <p>
 * on success this API returns a JSON Object {key:"notificationId", value:"value"}
 * <br>where notificationId can be one of those described above
 * <br>value is the boolean value for that notification id (true/false)
 * <p> 
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode 
 * 
 * @param email					the email address of the user
 * @param notificationId		notification id
 * @param enableNotification 	boolean flag to turn notifications on or off
 * @param callbackSuccess 		success callback for the API
 * @param callbackError 		error callback for the API
 * @returns						a JSON Object
 */
UserMgr.prototype.turnNotificationOnoff = function(email, notificationId, enableNotification, 
		callbackSuccess, callbackError) {
	var commandParams = {'email':email, 'notificationId':notificationId, 'on':enableNotification};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_TURN_NOTIFICATION_ON_OFF, [commandParams]);
};

/**
 * This API fetches a single push notification setting (enable/disable) based on the notification id
 * <br>Notification ID specifies three types of notifications currently supported
 * <br>101 - Robot needs cleaning
 * <br>102 - Cleaning is done
 * <br>103 - Robot is stuck
 * <p>
 * on success this API returns a JSON Object {key:"notificationId", value:"value"}
 * <br>where notificationId can be one of those described above
 * <br>value is the boolean value for that notification id (true/false)
 * <p> 
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode 
 * 
 * @param email				the email address of the user
 * @param notificationId	the notification id
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @returns					a JSON Object
 */
UserMgr.prototype.isNotificationEnabled = function(email, notificationId, callbackSuccess, callbackError) {
	var commandParams = {'email':email, 'notificationId':notificationId};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_IS_NOTIFICATION_ENABLED, [commandParams]);
};

/**
 * This API fetches all global and individual push notification options setting 
 * <br>Notification ID specifies three types of notifications currently supported
 * <br>101 - Robot needs cleaning
 * <br>102 - Cleaning is done
 * <br>103 - Robot is stuck
 * <p>
 * on success this API returns a JSON Object
 * <br>{global:"global", notifications:[{key:101, value:"value"},{key:102, value:"value"},{key:103, value:"value"}]}
 * <br>where global is a boolean describing if notification type is glabal or not
 * <br>value is boolean value (true/false) for that particular notification id
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown error
 * <br>1002 - Network error
 * <br>1003 - Server error
 * <br>1004 - JSON Parsing error
 * <br>1014 - Unauthorized User error
 * <br>and errMessage is the message corresponding to the errorCode 
 * 
 * @param email				the email address of the user
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @returns					a JSON Object
 */
UserMgr.prototype.getNotificationSettings = function(email, callbackSuccess, callbackError) {	
	var commandParams = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_NOTIFICATION_SETTINGS, [commandParams]);
};

// ***********************ROBOT PLUGIN METHODS ****************************
/**
 * This API registers for notifications from the robot. For now it registers
 * all notifications for the robot i.e. robot needs cleaning, cleaning is done,
 * robot is stuck
 * <p>
 * The API calls Neato Smart App Service
 * 
 * @param robotId			the serial number of the robot
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 */
RobotMgr.prototype.registerNotifications = function(robotId, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId};
	
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_REGISTER_ROBOT_NOTIFICATIONS, [commandArray]);
};

/**
 * This API unregisters for notifications from the robot. Currently it 
 * unregisters for all notifications for the robot i.e. robot needs cleaning,
 * cleaning is done, and robot is stuck
 * <p>
 * This API calls Neato Smart App Service.
 * 
 * @param robotId			the serial number of the robot
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 */
RobotMgr.prototype.unregisterNotifications = function(robotId, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId};
	
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_UNREGISTER_ROBOT_NOTIFICATIONS, [commandArray]);
};

/**
 * This API registers for notifications. As of now this API registers for all type of
 * notifications. This API does not make a webservice call.
 * <p>
 * on success this API returns a JSON Object {robotDataKeyId:"robotDataKeyId", robotId:"robotId", robotData:"robotData"}
 * <br>where robotDataKeyId is the data key id for the robot
 * <br>robotId is the id of the robot
 * <br>robotData is the data sent by robot
 * 
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API 
 * @returns					a JSON Object on success
 */
RobotMgr.prototype.registerNotifications2 = function(callbackSuccess, callbackError) {
	var commandArray = {};
	
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_REGISTER_ROBOT_NOTIFICATIONS_2, [commandArray]);
};

/**
 * This API unregisters for all notifications from the robot.
 * 
 * @param callbackSuccess 	success callback for this API
 * @param callbackError  	error callback for this API
 */
RobotMgr.prototype.unregisterNotifications2 = function(callbackSuccess, callbackError) {
	var commandArray = {};
	
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_UNREGISTER_ROBOT_NOTIFICATIONS_2, [commandArray]);
};

/**
 * This API starts discovering nearby robots that are available and online.
 * This API calls Neato Smart App Service to discover robots.
 * 
 * @param callbackSuccess 	success callback for the API
 * @param callbackError  	error callback for the API
 */
RobotMgr.prototype.discoverNearbyRobots = function(callbackSuccess, callbackError) {
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DISCOVER_NEARBY_ROBOTS, []);
};

/**
 * This API tries to establish a direct peer-to-peer connection with the robot. For a successful
 * connection robot and smart app need to be on the same network. This API is deprecated. Please
 * use tryDirectConnection2 instead.
 * <p>
 * This API calls Neato Smart App Service to make a TCP connection with the robot. This API
 * returns an error if connection could not be established.
 * 
 * @param robotId			the serial number of the robot
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @deprecated				Replaced by {@link #tryDirectConnection2(robotId, callbackSuccess, callbackError)}
 * @see						#tryDirectConnection2(robotId, callbackSuccess, callbackError)
 */
RobotMgr.prototype.tryDirectConnection = function(robotId, callbackSuccess, callbackError) {
	var connectPeerCommandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_TRY_CONNECT_CONNECTION, [connectPeerCommandArray]);
};

/**
 * This API tries to establish a direct peer-to-peer connection with the robot. The robot
 * and smart app need to be on the same network for the connection to be successful
 * <p>
 * This API calls Neato Smart App Service to make a TCP connection with the robot. This API
 * returns an error if the connection could not be established.
 * 
 * @param robotId			the serial number of the robot
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 */
RobotMgr.prototype.tryDirectConnection2 = function(robotId, callbackSuccess, callbackError) {
	var connectPeerCommandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_TRY_CONNECT_CONNECTION2, [connectPeerCommandArray]);
};

/**
 * This API tears down the existing direct connection created via tryDirectConnection2
 * <p>
 * This API calls Neato Smart App Service to disconnect from the TCP connection created from 
 * tryDirectConnection2. 
 * 
 * @param robotId			the serial number of the robot
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 */
RobotMgr.prototype.disconnectDirectConnection  = function(robotId, callbackSuccess, callbackError) {
	var disconnectPeerCommandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DISCONNECT_DIRECT_CONNETION, [disconnectPeerCommandArray]);
};

/**
 * This API sends a command to a specific robot. The robot and smart app need to be on the
 * same network for successful connection. This API is deprecated. Please use sendCommandToRobot2
 * instead. This API sends command via the presence server (XMPP) and does not use direct connection
 * as of now
 * <p>
 * This API calls Neato Smart App Service to send command to robot. The command Id is the id of the
 * command to be executed on the robot. Currently supported commands are - 
 * <br>101 - Start Cleaning
 * <br>102 - Stop Cleaning
 * 
 * @param robotId			the serial number of the robot
 * @param commandId			command ID of the command to be executed on the robot.
 * @param commandParams		the json Object containing key value pairs related to the command to be executed
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @deprecated				Replaced by {@link #sendCommandToRobot2(robotId, commandId, commandParams, callbackSuccess, callbackError)}
 * @see						#sendCommandToRobot2(robotId, commandId, commandParams, callbackSuccess, callbackError)
 */
RobotMgr.prototype.sendCommandToRobot = function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId, 'commandId':commandId, 'commandParams':commandParams};
	
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SEND_COMMAND_TO_ROBOT, [commandArray]);
};

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
 * @param robotId			the serial number of the robot
 * @param commandId			command ID of the command to be executed on this robot.
 * @param commandParams 	the json object containing key value pairs related to the command to be executed. 
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 */
RobotMgr.prototype.sendCommandToRobot2 = function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
	var params = {'params': commandParams};
	var commandArray = {'robotId':robotId, 'commandId':commandId, 'commandParams':params};
	
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SEND_COMMAND_TO_ROBOT2, [commandArray]);
};

/**
 * This API sets the robot name. It is deprecated as of now. Please use setRobotName2 instead.
 * <p>
 * on error it returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * 
 * @param robotId			the serial number of the robot
 * @param robotName			the name of the robot
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @deprecated 				Replaced by {@link #setRobotName2(robotId, robotName, callbackSuccess, callbackError)}
 * @see						#setRobotName2(robotId, robotName, callbackSuccess, callbackError)
 */
RobotMgr.prototype.setRobotName = function(robotId, robotName, callbackSuccess, callbackError) {
	var setRobotName = {'robotId':robotId, 'robotName':robotName};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SET_ROBOT_NAME, [setRobotName]);
};

/**
 * This API sets the robot name.
 * <p>
 * on success this API returns a JSON Object {robotId:"robotId", robotName:"robotName"}
 * <br>where robotId is the serial number of the robot
 * <br>robotName is the new name of the robot
 * <p>
 * on error it returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * 
 * @param robotId			the serial number of the robot
 * @param robotName			the name of the robot
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @returns					a JSON Object
 */
RobotMgr.prototype.setRobotName2 = function(robotId, robotName, callbackSuccess, callbackError) {
	var setRobotName = {'robotId':robotId, 'robotName':robotName};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SET_ROBOT_NAME_2, [setRobotName]);
};

/**
 * This API gets the robot details.
 * <p>
 * on success this API returns a JSON Object {robotId:"robotId", robotName:"robotName"}
 * <br>where robotId is the robot's id
 * <br>robotName is the robot's name
 * <p>
 * on error it returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * 
 * @param robotId			the serial number of the robot
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 * @returns					a JSON Object
 */
RobotMgr.prototype.getRobotDetail = function(robotId, callbackSuccess, callbackError) {
	var getRobotDetail = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ROBOT_DETAIL, [getRobotDetail]);
};

/**
 * This API checks if a robot is online or not
 * <p>
 * on success this API returns a JSON Object {robotId:"robotId", online:"online"}
 * <br>where robotId is the serial number of the robot
 * <br>online is the boolean value (true/false) describing state of the robot
 * <p>
 * on error it returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * 
 * @param robotId			the serial number of the robot
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 * @returns					a JSON Object
 */
RobotMgr.prototype.getRobotOnlineStatus = function(robotId, callbackSuccess, callbackError) {
	var getRobotStatus = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ROBOT_ONLINE_STATUS, [getRobotStatus]);
};

/**
 * This API checks if a robot is online or not (timed mode implementation)
 * <p>
 * on success this API returns a JSON Object {robotId:"robotId", online:"online"}
 * <br>where robotId is the serial number of the robot
 * <br>online is the boolean value (true/false) describing the state of the robot
 * <p>
 * on error it returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * 
 * @param robotId			the serial number of the robot
 * @param callbackSuccess	success callback for the API
 * @param callbackError		error callback for the API
 * @returns					a json object
 */
RobotMgr.prototype.getRobotVirtualOnlineStatus = function(robotId, callbackSuccess, callbackError) {
	var getRobotStatus = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ROBOT_VIRTUAL_ONLINE_STATUS, [getRobotStatus]);
};

/**
 * This API is not supported as of now.
 * 
 * @param robotId			the serial number of the robot
 * @param scheduleType		the schedule type of the robot(e.g. Basic or Advanced)
 * @param jsonArray			the schedule Data to be set for the robot
 * @param callbackSuccess	success callback for the API
 * @param callbackError		error callback for the API
 */
RobotMgr.prototype.setSchedule = function(robotId, scheduleType, jsonArray, callbackSuccess, callbackError) {
	var scheduleArray = {'robotId':robotId, 'scheduleType':scheduleType, 'schedule': jsonArray};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACIION_TYPE_SET_SCHEDULE, [scheduleArray]);
};

/**
 * This API is not supported as of now
 * 
 * @param robotId			the serial number of the robot
 * @param scheduleType		the schedule type of the robot(e.g. Basic or Advanced)
 * @param callbackSuccess	success callback for the robot
 * @param callbackError		error callback for the robot
 */
RobotMgr.prototype.getSchedule = function(robotId, scheduleType, callbackSuccess, callbackError) {
	var scheduleArray = {'robotId':robotId, 'scheduleType':scheduleType};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACIION_TYPE_GET_ROBOT_SCHEDULE, [scheduleArray]);
};

/**
 * This API is not supported as of now
 * 
 * @param robotId			the serial number of the robot
 * @param scheduleType		the schedule type of the robot(e.g. Basic or Advanced)
 * @param callbackSuccess	success callback for the API
 * @param callbackError		error callback for the API
 */
RobotMgr.prototype.deleteSchedule = function(robotId, scheduleType, callbackSuccess, callbackError) {
	var scheduleArray = {'robotId':robotId, 'scheduleType':scheduleType};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DELETE_ROBOT_SCHEDULE, [scheduleArray]);
};

/**
 * This API is not supported as of now
 * 
 * @param robotId			the serial number of the robot
 * @param callbackSuccess	success callback for the API
 * @param callbackError		error callback for the API
 */
RobotMgr.prototype.getMaps = function(robotId, callbackSuccess, callbackError) {
	var mapArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ROBOT_MAP, [mapArray]);
};

/**
 * This API is not supported as of now
 * 
 * @param robotId			the serial number of the robot
 * @param mapId				the map id
 * @param mapOverlayInfo	the map overlay info
 * @param callbackSuccess	success callback for the API
 * @param callbackError		error callback for the API
 */
RobotMgr.prototype.setMapOverlayData = function(robotId, mapId, mapOverlayInfo, callbackSuccess, callbackError) {
	var mapArray = {'robotId':robotId, 'mapId':mapId, 'mapOverlayInfo':mapOverlayInfo};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SET_MAP_OVERLAY_DATA, [mapArray]);
};

// Atlas action types
/**
 * This API is not supported as of now
 * 
 * @param robotId			the serial number of the robot 
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API * 
 */
RobotMgr.prototype.getRobotAtlasMetadata = function(robotId, callbackSuccess, callbackError) {
	var atlasArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ROBOT_ATLAS_METADATA, [atlasArray]);
};

/**
 * This API is not supported as of now
 * 
 * @param robotId			the serial number of the robot
 * @param atlasMetadata		the atlas meta data
 * @param callbackSuccess	success callback for the API
 * @param callbackError		error callback for the API
 */
RobotMgr.prototype.updateAtlasMetaData = function(robotId, atlasMetadata, callbackSuccess, callbackError) {
	var atlasArray = {'robotId':robotId, 'atlasMetadata':atlasMetadata};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_UPDATE_ROBOT_ATLAS_METADATA, [atlasArray]);
};

/**
 * This API is not supported as of now
 * 
 * @param robotId			the serial number of the robot
 * @param gridId			the grid id	
 * @param callbackSuccess	success callback for the API
 * @param callbackError		error callback for the API
 */
RobotMgr.prototype.getAtlasGridData = function(robotId, gridId, callbackSuccess, callbackError) {
	var getGridArray = {'robotId':robotId, 'gridId':gridId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ATLAS_GRID_DATA, [getGridArray]);
};

/**
 * This API creates a new local schedule for the robot. If you were already working with a local schedule
 * that data is lost, and this new schedule will be updated in subsequent calls.
 * <p>
 * This API saves the schedule in the database and hence does not call webservice.
 * The schedule type can have values
 * <br>1 - SCHEDULE_TYPE_BASIC
 * <br>2 - SCHEDULE_TYPE_ADVANCED. 
 * <br>As of now only SCHEDULE_TYPE_BASIC is supported.
 * <p>
 * on success this API returns a JSON Object
 * <br>{scheduleId:"scheduleId", robotId:"robotId", scheduleType:"scheduleType"}
 * <br>where scheduleId is the schedule id for the schedule
 * <br>robotId is the robot serial number
 * <br>scheduleType is the schedule Type(SCHEDULE_TYPE_BASIC or SCHEDULE_TYPE_ADVANCED)
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error       
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * <br>1012 - JSON Creation Error
 * 
 * @param robotId 			the serial number of the robot
 * @param scheduleType 		the schedule type of the robot
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 * @returns					a JSON Object
 */
RobotMgr.prototype.createSchedule = function(robotId, scheduleType, callbackSuccess, callbackError) {
	var createSchedule = {'robotId':robotId, 'scheduleType':scheduleType};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_CREATE_SCHEDULE, [createSchedule]);
};

/**
 * This API makes a call to the server to get the Schedule Events for the robot. Once we fetch the schedule data
 * from server, we cache this data locally and further changes are done on the local copy
 * <p>
 * The schedule Type can have types -
 * <br>1 - SCHEDULE_TYPE_BASIC
 * <br>2 - SCHEDULE_TYPE_ADVANCED.
 * <br>As of now only SCHEDULE_TYPE_BASIC is supported
 * <p>
 * on success this API returns a JSON Object
 * <br>{scheduleId:"scheduleId", robotId:"robotId", scheduleType:"scheduleType", scheduleEventLists:"scheduleEventLists"}
 * <br>where scheduleId is the id of the schedule
 * <br>robotId is the serial number of the robot
 * <br>scheduleType is schedule Type (could be SCHEDULE_TYPE_BASIC or SCHEDULE_TYPE_ADVANCED)
 * <br>scheduleEventLists is a JSON Array with all schedule Event ids for the scheduleEvents of the schedule
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error       
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * <br>1012 - JSON Creation Error
 * 
 * @param robotId 			the serial number of the robot
 * @param scheduleType 		the schedule type of the schedule
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 * @returns					a JSON Object
 */
RobotMgr.prototype.getScheduleEvents = function(robotId, scheduleType, callbackSuccess, callbackError) {
	var getScheduleEvents = {'robotId':robotId, 'scheduleType':scheduleType};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_SCHEDULE_EVENTS, [getScheduleEvents]);
};

/**
 * This API gets the schedule data for the event. It fetches the data from the local database
 * and hence does not make a web service call.
 * <p>
 * on success this API returns a JSON Object
 * <br>{scheduleId:"scheduleId", scheduleEventId:"scheduleEventId", scheduleEventData:"scheduleEventData"}
 * <br>where scheduleId is the id of the schedule
 * <br>scheduleEventId is the event id of the schedule
 * <br>scheduleEventData is a JSON Object
 * <br>{day:"day", startTime:"startTime", cleaningMode:"cleaningMode"}
 * <br>where day is an integer value(0(DAY_SUNDAY) to 6(DAY_SATURDAY))
 * <br>startTime is time in format(hh:mm)
 * <br>cleaning Mode is (ECO or NORMAL)
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error       
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * <br>1012 - JSON Creation Error
 * 
 * @param scheduleId 		the schedule id of the schedule
 * @param scheduleEventId 	the schedule event id of the schedule
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 */
RobotMgr.prototype.getScheduleEventData = function(scheduleId, scheduleEventId, callbackSuccess, callbackError) {
	var getScheduleEventData = {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_SCHEDULE_EVENT_DATA, [getScheduleEventData]);
};

/**
 * This API adds a schedule event in the local copy of the robot schedule. No changes
 * are made to the server data until updateSchedule is called. It does not make a
 * web service call. It returns the scheduleEventId of the added scheduleEvent for 
 * further tracking.
 * <p>
 * on success this API returns a JSON Object
 * <br>{scheduleId:"scheduleId", scheduleEventId:"scheduleEventId"}
 * <br>where scheduleId is the id of the schedule
 * <br>scheduleEventId is the id of the added schedule Event
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error       
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * <br>1012 - JSON Creation Error
 * 
 * @param scheduleId 			the schedule id of the schedule
 * @param scheduleEventData 	the schedule event data for the schedule
 * @param callbackSuccess 		success callback for the API
 * @param callbackError 		error callback for the API
 * @returns						a JSON Object
 */
RobotMgr.prototype.addScheduleEvent = function(scheduleId, scheduleEventData, callbackSuccess, callbackError) {
	var addScheduleEventData = {'scheduleId':scheduleId, 'scheduleEventData':scheduleEventData};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_ADD_ROBOT_SCHEDULE_EVENT, [addScheduleEventData]);
};

/**
 * This API updates a schedule event in the local copy of the robot schedule. No changes are
 * made to the server data until updateSchedule is called
 * <p>
 * on success this API returns a JSON Object of updated schedule Event
 * <br>{scheduleId:"scheduleId", scheduleEventId:"scheduleEventId"}
 * <br>where scheduleId is the id of the schedule
 * <br>and schedule Event Id is the updated schedule event id
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error       
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * <br>1012 - JSON Creation Error
 * 
 * @param scheduleId 			the schedule id of the schedule
 * @param scheduleEventId 		the schedule event id of the schedule
 * @param scheduleEventData 	the schedule event data for the schedule event id
 * @param callbackSuccess 		success callback for the API
 * @param callbackError 		error callback for the API
 * @returns						a JSON Object
 */
RobotMgr.prototype.updateScheduleEvent = function(scheduleId, scheduleEventId, scheduleEventData, callbackSuccess, callbackError) {
	var updateScheduleEvent = {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId, 'scheduleEventData':scheduleEventData};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_UPDATE_ROBOT_SCHEDULE_EVENT, [updateScheduleEvent]);
};

/**
 * This API deletes a schedule event from the local copy of the robot schedule. No changes
 * are made to the server data until updateSchedule is called
 * <p>
 * on success this API returns a JSON Object
 * <br>{scheduleId:"scheduleId", scheduleEventId:"scheduleEventId"}
 * <br>where scheduleId is the id of the deleted schedule
 * <br>scheduleEventId is the event id of the deleted schedule
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error       
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * <br>1012 - JSON Creation Error
 * 
 * @param scheduleId 		the schedule id of the schedule
 * @param scheduleEventId 	the schedule event id of the schedule
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @returns					a JSON Object
 */
RobotMgr.prototype.deleteScheduleEvent = function(scheduleId, scheduleEventId, callbackSuccess, callbackError) {
	var deleteScheduleEvent = {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DELETE_ROBOT_SCHEDULE_EVENT, [deleteScheduleEvent]);
};

/**
 * This API updates the server data with the local copy of the schedule. If the 
 * schedule id does not exist it will create a new schedule with this id on the server.
 * <p>
 * on success this API returns a JSON Object {scheduleId:"scheduleId"}
 * <br>where scheduleId is the updated schedule ID
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error       
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * <br>1012 - JSON Creation Error
 * <br>1014 - User Unauthroized Error
 * 
 * @param scheduleId 		the schedule id of the schedule
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @returns					a JSON Object
 */
RobotMgr.prototype.updateSchedule = function(scheduleId, callbackSuccess, callbackError) {
	var updateSchedule = {'scheduleId':scheduleId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_UPDATE_SCHEDULE, [updateSchedule]);
};

/**
 * This API is a helper API which returns the entire event items in a single call.
 * <p>
 * on success this API returns a JSON Object
 * <br>{scheduleType:"scheduleType", scheduleId:"scheduleId", schedules:"scheduleArray"}
 * <br>where scheduleId is the id of the schedule
 * <br>scheduleType is the schedule type(SCHEDULE_TYPE_BASIC or SCHEDULE_TYPE_NORMAL)
 * <br>scheduleArray is a JSON Array with each schedule Event as a JSON Object like
 * <br>{day:"day", startTime:12:00, cleaningMode:"cleaningMode"}
 * <br>where day is an integer value(0(DAY_SUNDAY) to 6(DAY_SATURDAY))
 * <br>startTime is a time value(hh:mm)
 * <br>cleaning Mode is (ECO or NORMAL)
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error       
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * <br>1005 - Invalid Schedule Id
 * <br>1012 - JSON Creation Error
 * 
 * @param scheduleId 		the schedule id of the schedule
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @returns					a JSON Object
 */
RobotMgr.prototype.getScheduleData = function(scheduleId, callbackSuccess, callbackError) {
	var getScheduleData = {'scheduleId':scheduleId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_SCHEDULE_DATA, [getScheduleData]);
};

/**
 * This API saves the spot definition in the DB.
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1011 - DB error
 * <br>1013 - Invalid Parameter
 * 
 * @param robotId					the serial number of the robot 
 * @param spotCleaningAreaLength 	the spot cleaning area length(integer)
 * @param spotCleaningAreaHeight 	the spot cleaning area height(integer)
 * @param callbackSuccess 			success callback for the API
 * @param callbackError 			error callback for the API
 * @returns							json object on error
 */
RobotMgr.prototype.setSpotDefinition = function(robotId, spotCleaningAreaLength, spotCleaningAreaHeight,
		callbackSuccess, callbackError) {
	var commandParams = {'robotId':robotId, 'spotCleaningAreaLength':spotCleaningAreaLength, 
			'spotCleaningAreaHeight':spotCleaningAreaHeight};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SET_SPOT_DEFINITION, [commandParams]);
};

/**
 * This API gets spot definition from the DB. In case spot values are not set for this robot
 * default values od spot will be returned.
 * <p>
 * on success this API returns a JSON Object
 * <br>{spotCleaningAreaLength:"areaLength", spotCleaningAreaHeight:"areaHeight"}
 * <br>where areaLength is the spot area length
 * <br>areaHeight is the spot area height
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1011 - DB Error
 * <br>1012 - JSON Creation Error
 * 
 * @param robotId 			the serial number of the robot
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @returns					a JSON Object
 */
RobotMgr.prototype.getSpotDefinition = function(robotId, callbackSuccess, callbackError) {
	var commandParams = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_SPOT_DEFINITION, [commandParams]);
};

/**
 * This API sends drive command to the robot. This API calls Neato Smart App Service.
 * <p>
 * The navigation control id is an integer. It must be among the following values:
 * <br>1 - NAVIGATION_CONTROL_1
 * <br>2 - NAVIGATION_CONTROL_2
 * <br>3 - NAVIGATION_CONTROL_3
 * <br>4 - NAVIGATION_CONTROL_4
 * <br>5 - NAVIGATION_CONTROL_5
 * <br>6 - NAVIGATION_CONTROL_BACK
 * 
 * @param robotId 				the serial number of the robot
 * @param navigationControlId 	the navigation control id sent to the robot
 * @param callbackSuccess 		success callback for the API
 * @param callbackError 		error callback for the API
 */
RobotMgr.prototype.driveRobot = function(robotId, navigationControlId, callbackSuccess, callbackError) {
	var commandParams = {'robotId':robotId, 'navigationControlId':navigationControlId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DRIVE_ROBOT, [commandParams]);
};

/**
 * This API turns the vacuum of the robot on or off. This API calls Neato Smart App Service
 * 
 * @param robotId 			the serial number of the robot
 * @param on 				Integer value. Must be FLAG_ON or FLAG_OFF
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 */
RobotMgr.prototype.turnVaccumOnOff = function(robotId, on, callbackSuccess, callbackError) {
	var commandParams = {'robotId':robotId, 'on':on};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_TURN_VACUUM_ON_OFF, [commandParams]);
};

/**
 * This API turns the WiFi on or off on the robot. If WiFi is turned off then
 * duration must be specified (in secs). This API calls Neato Smart App Service
 * 
 * @param robotId 					the serial number of the robot
 * @param on 						Integer value. Must be 1(FLAG_ON) or 0(FLAG_OFF)
 * @param wiFiTurnOnDurationInSec 	Integer value (seconds)
 * @param callbackSuccess 			success callback for this API
 * @param callbackError 			error callback for this API
 */
RobotMgr.prototype.turnWiFiOnOff = function(robotId, on, wiFiTurnOnDurationInSec, callbackSuccess, callbackError) {
	var onoffInfo = {'flagOnOff': on, 'wiFiTurnOffDurationInSec': wiFiTurnOnDurationInSec};
	var params = {'params': onoffInfo};
	var commandArray = {'robotId':robotId, 'commandParams':params};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_TURN_WIFI_ON_OFF, [commandArray]);
};

/**
 * This API sends start cleaning command to the robot. This API calls Neato Smart App Service.
 * <p>
 * Cleaning Category Id value is a predefined integer and must be one of
 * <br>2 - CLEANING_CATEGORY_ALL
 * <br>3 - CLEANING_CATEGORY_SPOT
 * <br>1 - CLEANING_CATEGORY_MANUAL
 * <p>
 * Cleaning Mode Id value is a predefined integer and must be one of
 * <br>1 - CLEANING_MODE_ECO
 * <br>2 - CLEANING_MODE_NORMAL
 * 
 * @param robotId 				the serial number of the robot
 * @param cleaningCategoryId 	the cleaning category id of the robot
 * @param cleaningModeId 		the cleaning mode id of the robot
 * @param cleaningModifier 		the cleaning modifier of the robot. String value (e.g. 1, 2)
 * @param callbackSuccess 		success callback for this API
 * @param callbackError 		error callback for this API
 */
RobotMgr.prototype.startCleaning = function(robotId, cleaningCategoryId, cleaningModeId, cleaningModifier,
		callbackSuccess, callbackError) {
	
	var commandParams = {'cleaningCategory':cleaningCategoryId, 'cleaningMode':cleaningModeId, 
			'cleaningModifier':cleaningModifier, 'cleanMode':cleaningCategoryId};
	var params = {'params': commandParams};
	var commandArray = {'robotId':robotId, 'commandParams':params};
	
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_START_CLEANING, [commandArray]);
};

/**
 * This API sends stop cleaning command to the robot.This API calls Neato Smart App Service.
 * 
 * @param robotId 			the serial number of the robot
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 */
RobotMgr.prototype.stopCleaning = function(robotId, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_STOP_CLEANING, [commandArray]);
};

/**
 * This API sends pause cleaning command to the robot. Robot needs to remember
 * the parameters sent in startCleaning because in resumeCleaning we don't resend
 * these params. This API calls Neato Smart App Service
 * 
 * @param robotId 			the serial number of the robot
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 */
RobotMgr.prototype.pauseCleaning = function(robotId, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_PAUSE_CLEANING, [commandArray]);
};

/**
 * This API sends resume cleaning command to the robot. This API calls Neato Smart App Service.
 * 
 * @param robotId 			the serial number of the robot
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 */
RobotMgr.prototype.resumeCleaning = function(robotId, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_RESUME_CLEANING, [commandArray]);
};

/**
 * This API checks if a schedule is enabled on server for the specific robot
 * <p>
 * scheduleType is an integer value. The value can be
 * <br>1 - SCHEDULE_TYPE_BASIC
 * <br>2 - SCHEDULE_TYPE_ADVANCED.
 * <br>As of now only SCHEDULE_TYPE_BASIC is supported
 * <p>
 * on success this API returns a JSON Object
 * <br>{isScheduleEnabled:"isScheduleEnabled", scheduleType:"scheduleType", robotId:"robotId"}
 * <br>where isScheduleEnabled is boolean value (true/false) describing schedule state
 * <br>scheduleType is an integer value
 * <br>robotId is the serial number of the robot
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * 
 * @param robotId 			the serial number of the robot
 * @param scheduleType 		Integer value
 * @param callbackSuccess 	success callback for the API
 * @param callbackError 	error callback for the API
 * @returns					a JSON Object
 */
RobotMgr.prototype.isScheduleEnabled = function (robotId, scheduleType, callbackSuccess, callbackError) {
	var params = {'robotId':robotId, 'scheduleType':scheduleType};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_IS_SCHEDULE_ENABLED, [params]);
};

/**
 * This API enables or disables schedule on the server for the specific robot
 * <p>
 * scheduleType is an integer value. The value can be
 * <br>1 - SCHEDULE_TYPE_BASIC
 * <br>2 - SCHEDULE_TYPE_ADVANCED.
 * <br>As of now only SCHEDULE_TYPE_BASIC is supported
 * <p>
 * on success this API returns a JSON Object
 * <br>{isScheduleEnabled:"isScheduleEnabled", scheduleType:"scheduleType", robotId:"robotId"}
 * <br>isScheduleEnabled is boolean value (true/false)
 * <br>scheduleType is an integer value
 * <br>robotId is the serial number of the robot
 * <p>
 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
 * <br>where errorCode is the error type and it's values are
 * <br>1001 - Unknown Error
 * <br>1002 - Network Error
 * <br>1003 - Server Error
 * <br>1004 - JSON Parsing Error
 * 
 * @param robotId 			the serial number of the robot
 * @param scheduleType 		Integer value. Must be SCHEDULE_TYPE_BASIC or SCHEDULE_TYPE_ADVANCED
 * @param enable 			boolean value. true to enable schedule and false to disable schedule
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 * @returns					a JSON Object
 */
RobotMgr.prototype.enableSchedule = function (robotId, scheduleType, enable, callbackSuccess, callbackError) {
	var params = {'robotId':robotId, 'scheduleType':scheduleType, 'enableSchedule':enable};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_ENABLE_SCHEDULE, [params]);
};

/**
 * This API notifies push messages sent by server to UI.
 * 
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 */
RobotMgr.prototype.registerForRobotMessages = function(callbackSuccess, callbackError) {
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_REGISTER_FOR_ROBOT_MESSAGES, []);
};

/**
 * This API unregisters for push messages sent by server to UI.
 * 
 * @param callbackSuccess 	success callback for this API
 * @param callbackError 	error callback for this API
 */
RobotMgr.prototype.unregisterForRobotMessages = function(callbackSuccess, callbackError) {
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_UNREGISTER_FOR_ROBOT_MESSAGES, []);
};

var UserPluginManager = (function() {
	return {
		/**
		 * This API logs the user in using the email and password provided by the user.
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{email:"emailAddress, userName:"userName", userId:"userId", validation_status:"validationStatus"}
		 * <br>where emailAddress is user email address
		 * <br>userName is the user's name
		 * <br>userId is the user's Id 
		 * <br>validation status would be among the following values
		 * <br>USER_STATUS_VALIDATED - user is validated and logged into the app
		 * <br>USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD - user is not validated but can log in for a brief amount of time into the app
		 * <br>USER_STATUS_NOT_VALIDATED - user is not validated and cannot log into the app
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode
		 * 
		 * @param email				the email address of the user
		 * @param password			the password of the user
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 * @returns					returns JSON Object
		 */
		login: function(email, password , callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.loginUser(email, password, callbackSuccess, callbackError);
		},
	
		/**
		 * This API logs the user out of the system. It also clears all the user logged in
		 * information locally. This API does not return any value
		 * 
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 */
		logout: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.logoutUser(callbackSuccess, callbackError);
		},
	
		/**
		 * This API creates a new user. It does not trigger email validation. Server
		 * assumes whatever email is provided is already validated if it exists. Use
		 * createUser2 to create user instead
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{email:"emailAddress", userName:"userName", userId:"userId", validation_status:"validationStatus"}
		 * <br>where emailAddress is the email address of the user
		 * <br>userName is the user's name
		 * <br>userId is the user id
		 * <br>validation status can be among the following values
		 * <br>USER_STATUS_VALIDATED - user is validated and logged into the app
		 * <br>USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD - user is not validated but can log in for a brief amount of time into the app
		 * <br>USER_STATUS_NOT_VALIDATED - user is not validated and cannot log into the app
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode
		 * 
		 * @param email				the email address of the user
		 * @param password			the password of the user
		 * @param name 				name of the user
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 * @returns					returns a json object
		 * @deprecated				replaced by {@link #createUser2(email, password, name, alternateEmail, callbackSuccess, callbackError)}
		 * @see						#createUser2(email, password, name, alternateEmail, callbackSuccess, callbackError)
		 */
		createUser: function(email, password, name, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.createUser(email, password, name, callbackSuccess, callbackError);
		},
		
		/**
		 * This API creates a new user by triggering email validation. Use 
		 * this API to create new users.
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{email:"emailAddress", alternate_email:"alternateEmailAddress", userName:"userName", userId:"userId", validation_status:"validationStatus"}
		 * <br>where emailAddress is the email Address of the user
		 * <br>alternateEmailAddress is the alternate email Address of the user
		 * <br>userName is the name of the user
		 * <br>userId is the user id of the user 
		 * <br>validation status could be among the following values
		 * <br>USER_STATUS_VALIDATED - user is validated and logged into the app
		 * <br>USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD - user is not validated but can log in for a brief amount of time into the app
		 * <br>USER_STATUS_NOT_VALIDATED - user is not validated and cannot log into the app
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode
		 * 
		 * @param email				the email address of the user
		 * @param password			the password of the user
		 * @param name				name of the user
		 * @param alternateEmail	the alternate email id of the user (optional parameter)
		 * @param callbackSuccess 	the success callback for this API
		 * @param callbackError 	the error callback for this API
		 * @returns					returns a json object
		 */		
		createUser2: function(email, password, name, alternateEmail, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.createUser2(email, password, name, alternateEmail, callbackSuccess, callbackError);
		},
		
		/**
		 * Resend the validation email to the user email id.
		 * <p>
		 * on success this API returns a JSON Object {message:"We have resent validation mail"}
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errorMessage:"errorMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode
		 * 
		 * @param email				the email address of the user
		 * @param callbackSuccess 	the success callback for this API
		 * @param callbackError 	the error callback for this API
		 * @returns					a json object 
		 */
		resendValidationMail: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.resendValidationMail(email, callbackSuccess, callbackError);
		},
		
		/**
		 * This API checks if a user is validated or not
		 * <p>
		 * on success this API returns a JSON Object {validation_status:"validationStatus", message:"message"}
		 * <br>validationStatus would be one among the following values
		 * <br>USER_STATUS_VALIDATED - user is validated and logged into the app
		 * <br>USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD - user is not validated but can log in for a brief amount of time into the app
		 * <br>USER_STATUS_NOT_VALIDATED - user is not validated and cannot log into the app
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode
		 * 
		 * @param email				the email address of the user
		 * @param callbackSuccess	the success callback for this API
		 * @param callbackError 	the error callback for this API
		 * @returns					a json object
		 */		
		isUserValidated: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.isUserValidated(email, callbackSuccess, callbackError);
		},
		
		/**
		 * This API checks if a user is already logged into the app
		 * 
		 * @param email				the email address of the user
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API 
		 * @returns					true if the user is logged in, false otherwise
		 */
		isUserLoggedIn: function(email, callbackSuccess, callbackError) {	
			window.plugins.neatoPluginLayer.userMgr.isUserLoggedIn(email, callbackSuccess, callbackError);
		},		
		
		/**
		 * This API associates a robot with a user
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode
		 * 
		 * @param email				the email address of the user
		 * @param robotId			the robotId of the robot to be associated
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 * @returns					JSON Object on error
		 */		
		associateRobot: function(email, robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.associateRobotCommand(email, robotId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API returns a JSON Object containing user details.
		 * <p>
		 * result is {email:"emailAddress", userName:"userName", userId:"userId"}
		 * <br>where emailAddress is user email address
		 * <br>userName is the name of the user
		 * <br>userId is the id of the user
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode
		 * 
		 * @param email				the email address of the user
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @returns					JSON Object
		 */
		getUserDetail: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.getUserDetail(email, callbackSuccess, callbackError);
		},
		
		/**
		 * This API gets a list of associated robots for a user in JSON
		 * <p>
		 * This API returns on success a JSON Array of which each element is like
		 * <br>{robotId:"robotId", robotName:"robotName"}
		 * <br>where robotId is robot's id
		 * <br>robotName is robot's name
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode 
		 * 
		 * @param email 			the email address of the user
		 * @param callbackSuccess 	success callback of the API
		 * @param callbackError 	error callback of the API
		 * @returns					a JSON Array on success or a JSON Object on error
		 */
		getAssociatedRobots: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.getAssociatedRobots(email, callbackSuccess, callbackError);
		},
		
		/**
		 * Disassociate a robot from a specified user.
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode 
		 * 
		 * @param email 			the email address of the user
		 * @param robotId			the robot Id of the robot (robot serial number)
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @returns					A JSON Object on error
		 */
		disassociateRobot: function(email, robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.disassociateRobot(email, robotId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API disassociates all the robots for the specified user
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode 
		 * 
		 * @param email 			the email address of the user
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @returns					a JSON Object on error
		 */
		disassociateAllRobots: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.disassociateAllRobots(email, callbackSuccess, callbackError);
		},
		
		/**
		 * This API changes the user's password.
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode 
		 * 
		 * @param email				the email address of the user
		 * @param currentPassword 	the old password of the user
		 * @param newPassword		the new password of the user
		 * @param callbackSuccess 	the success callback of this API
		 * @param callbackError 	the error callback of this API
		 * @returns					a JSON Object on error
		 */
		changePassword: function(email, currentPassword, newPassword, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.changePassword(email, currentPassword, newPassword, callbackSuccess, callbackError);
		},		

		/**
		 * This API takes user email address and asks server to send a mail to recover password
		 * for this user.
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode
		 * 
		 * @param email				the email address of the user
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @returns					JSON Object on error
		 */
		forgetPassword: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.forgetPassword(email, callbackSuccess, callbackError);
		},		
		
		/**
		 * This API is used to switch on/off global and individual push notification settings
		 * <br>Notification ID specifies three types of notifications currently supported
		 * <br>101 - Robot needs cleaning
		 * <br>102 - Cleaning is done
		 * <br>103 - Robot is stuck
		 * <p>
		 * on success this API returns a JSON Object {key:"notificationId", value:"value"}
		 * <br>where notificationId can be one of those described above
		 * <br>value is the boolean value for that notification id (true/false)
		 * <p> 
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode 
		 * 
		 * @param email					the email address of the user
		 * @param notificationId		notification id
		 * @param enableNotification 	boolean flag to turn notifications on or off
		 * @param callbackSuccess 		success callback for the API
		 * @param callbackError 		error callback for the API
		 * @returns						a JSON Object
		 */
		turnNotificationOnoff: function(email, notificationId, enableNotification, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.turnNotificationOnoff(email, notificationId, enableNotification, 
						callbackSuccess, callbackError);
		},

		/**
		 * This API fetches a single push notification setting (enable/disable) based on the notification id
		 * <br>Notification ID specifies three types of notifications currently supported
		 * <br>101 - Robot needs cleaning
		 * <br>102 - Cleaning is done
		 * <br>103 - Robot is stuck
		 * <p>
		 * on success this API returns a JSON Object {key:"notificationId", value:"value"}
		 * <br>where notificationId can be one of those described above
		 * <br>value is the boolean value for that notification id (true/false)
		 * <p> 
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode 
		 * 
		 * @param email				the email address of the user
		 * @param notificationId	the notification id
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @returns					a JSON Object
		 */
		isNotificationEnabled: function(email, notificationId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.isNotificationEnabled(email, notificationId, 
						callbackSuccess, callbackError);
		},
		
		/**
		 * This API fetches all global and individual push notification options setting 
		 * <br>Notification ID specifies three types of notifications currently supported
		 * <br>101 - Robot needs cleaning
		 * <br>102 - Cleaning is done
		 * <br>103 - Robot is stuck
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{global:"global", notifications:[{key:101, value:"value"},{key:102, value:"value"},{key:103, value:"value"}]}
		 * <br>where global is a boolean describing if notification type is glabal or not
		 * <br>value is boolean value (true/false) for that particular notification id
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown error
		 * <br>1002 - Network error
		 * <br>1003 - Server error
		 * <br>1004 - JSON Parsing error
		 * <br>1014 - Unauthorized User error
		 * <br>and errMessage is the message corresponding to the errorCode 
		 * 
		 * @param email				the email address of the user
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @returns					a JSON Object
		 */
		getNotificationSettings: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.getNotificationSettings(email, callbackSuccess, callbackError);
		}
	}
}());


var RobotPluginManager = (function() {
	return {
		
		/**
		 * This API starts discovering nearby robots that are available and online.
		 * This API calls Neato Smart App Service to discover robots.
		 * 
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError  	error callback for the API
		 */		
		discoverNearbyRobots: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.discoverNearbyRobots(callbackSuccess, callbackError);
		},
		
		/**
		 * This API tries to establish a direct peer-to-peer connection with the robot. For a successful
		 * connection robot and smart app need to be on the same network. This API is deprecated. Please
		 * use tryDirectConnection2 instead.
		 * <p>
		 * This API calls Neato Smart App Service to make a TCP connection with the robot. This API
		 * returns an error if connection could not be established.
		 * 
		 * @param robotId			the serial number of the robot
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @deprecated				Replaced by {@link #tryDirectConnection2(robotId, callbackSuccess, callbackError)}
		 * @see						#tryDirectConnection2(robotId, callbackSuccess, callbackError)
		 */		
		tryDirectConnection: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.tryDirectConnection(robotId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API tries to establish a direct peer-to-peer connection with the robot. The robot
		 * and smart app need to be on the same network for the connection to be successful
		 * <p>
		 * This API calls Neato Smart App Service to make a TCP connection with the robot. This API
		 * returns an error if the connection could not be established.
		 * 
		 * @param robotId			the serial number of the robot
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 */		
		tryDirectConnection2: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.tryDirectConnection2(robotId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API tears down the existing direct connection created via tryDirectConnection2
		 * <p>
		 * This API calls Neato Smart App Service to disconnect from the TCP connection created from 
		 * tryDirectConnection2. 
		 * 
		 * @param robotId			the serial number of the robot
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 */		
		disconnectDirectConnection: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.disconnectDirectConnection(robotId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API sends a command to a specific robot. The robot and smart app need to be on the
		 * same network for successful connection. This API is deprecated. Please use sendCommandToRobot2
		 * instead. This API sends command via the presence server (XMPP) and does not use direct connection
		 * as of now
		 * <p>
		 * This API calls Neato Smart App Service to send command to robot. The command Id is the id of the
		 * command to be executed on the robot. Currently supported commands are - 
		 * <br>101 - Start Cleaning
		 * <br>102 - Stop Cleaning
		 * 
		 * @param robotId			the serial number of the robot
		 * @param commandId			command ID of the command to be executed on the robot.
		 * @param commandParams		the json Object containing key value pairs related to the command to be executed
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @deprecated				Replaced by {@link #sendCommandToRobot2(robotId, commandId, commandParams, callbackSuccess, callbackError)}
		 * @see						#sendCommandToRobot2(robotId, commandId, commandParams, callbackSuccess, callbackError)
		 */		
		sendCommandToRobot: function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.sendCommandToRobot(robotId, commandId, commandParams, callbackSuccess, callbackError);
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
		 * @param robotId			the serial number of the robot
		 * @param commandId			command ID of the command to be executed on this robot.
		 * @param commandParams 	the json object containing key value pairs related to the command to be executed. 
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 */		
		sendCommandToRobot2: function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.sendCommandToRobot2(robotId, commandId, commandParams, callbackSuccess, callbackError);
		},
		
		/**
		 * This API sends start cleaning command to the robot. This API calls Neato Smart App Service.
		 * <p>
		 * Cleaning Category Id value is a predefined integer and must be one of
		 * <br>2 - CLEANING_CATEGORY_ALL
		 * <br>3 - CLEANING_CATEGORY_SPOT
		 * <br>1 - CLEANING_CATEGORY_MANUAL
		 * <p>
		 * Cleaning Mode Id value is a predefined integer and must be one of
		 * <br>1 - CLEANING_MODE_ECO
		 * <br>2 - CLEANING_MODE_NORMAL
		 * 
		 * @param robotId 				the serial number of the robot
		 * @param cleaningCategoryId 	the cleaning category id of the robot
		 * @param cleaningModeId 		the cleaning mode id of the robot
		 * @param cleaningModifier 		the cleaning modifier of the robot. String value (e.g. 1, 2)
		 * @param callbackSuccess 		success callback for this API
		 * @param callbackError 		error callback for this API
		 */
		startCleaning: function(robotId, cleaningCategoryId, cleaningModeId, cleaningModifier,
					callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.startCleaning(robotId, cleaningCategoryId, cleaningModeId, cleaningModifier,
					callbackSuccess, callbackError);
		},
		
		/**
		 * This API sends stop cleaning command to the robot.This API calls Neato Smart App Service.
		 * 
		 * @param robotId 			the serial number of the robot
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 */
		stopCleaning: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.stopCleaning(robotId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API sends pause cleaning command to the robot. Robot needs to remember
		 * the parameters sent in startCleaning because in resumeCleaning we don't resend
		 * these params. This API calls Neato Smart App Service
		 * 
		 * @param robotId 			the serial number of the robot
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 */
		pauseCleaning: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.pauseCleaning(robotId, callbackSuccess, callbackError);
		},

		/**
		 * This API sends resume cleaning command to the robot. This API calls Neato Smart App Service.
		 * 
		 * @param robotId 			the serial number of the robot
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 */
		resumeCleaning: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.resumeCleaning(robotId, callbackSuccess, callbackError);
		},
	
		/**
		 * This API saves the spot definition in the DB.
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1011 - DB error
		 * <br>1013 - Invalid Parameter
		 * 
		 * @param robotId					the serial number of the robot 
		 * @param spotCleaningAreaLength 	the spot cleaning area length(integer)
		 * @param spotCleaningAreaHeight 	the spot cleaning area height(integer)
		 * @param callbackSuccess 			success callback for the API
		 * @param callbackError 			error callback for the API
		 * @returns							json object on error
		 */
		setSpotDefinition: function(robotId, spotCleaningAreaLength, spotCleaningAreaHeight, 
					callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setSpotDefinition(robotId, spotCleaningAreaLength, 
					spotCleaningAreaHeight, callbackSuccess, callbackError);
		},
		
		/**
		 * This API gets spot definition from the DB. In case spot values are not set for this robot
		 * default values od spot will be returned.
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{spotCleaningAreaLength:"areaLength", spotCleaningAreaHeight:"areaHeight"}
		 * <br>where areaLength is the spot area length
		 * <br>areaHeight is the spot area height
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1011 - DB Error
		 * <br>1012 - JSON Creation Error
		 * 
		 * @param robotId 			the serial number of the robot
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @returns					a JSON Object
		 */
		getSpotDefinition: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getSpotDefinition(robotId, callbackSuccess, callbackError);
		},

		/**
		 * This API sends drive command to the robot. This API calls Neato Smart App Service.
		 * <p>
		 * The navigation control id is an integer. It must be among the following values:
		 * <br>1 - NAVIGATION_CONTROL_1
		 * <br>2 - NAVIGATION_CONTROL_2
		 * <br>3 - NAVIGATION_CONTROL_3
		 * <br>4 - NAVIGATION_CONTROL_4
		 * <br>5 - NAVIGATION_CONTROL_5
		 * <br>6 - NAVIGATION_CONTROL_BACK
		 * 
		 * @param robotId 				the serial number of the robot
		 * @param navigationControlId 	the navigation control id sent to the robot
		 * @param callbackSuccess 		success callback for the API
		 * @param callbackError 		error callback for the API
		 */
		driveRobot: function(robotId, navigationControlId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.driveRobot(robotId, navigationControlId, 
					callbackSuccess, callbackError);
		},

		/**
		 * This API turns the vacuum of the robot on or off. This API calls Neato Smart App Service
		 * 
		 * @param robotId 			the serial number of the robot
		 * @param on 				Integer value. Must be FLAG_ON or FLAG_OFF
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 */
		turnVaccumOnOff: function(robotId, flag, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.turnVaccumOnOff(robotId, flag, callbackSuccess, callbackError);
		},

		/**
		 * This API turns the WiFi on or off on the robot. If WiFi is turned off then
		 * duration must be specified (in secs). This API calls Neato Smart App Service
		 * 
		 * @param robotId 					the serial number of the robot
		 * @param on 						Integer value. Must be 1(FLAG_ON) or 0(FLAG_OFF)
		 * @param wiFiTurnOnDurationInSec 	Integer value (seconds)
		 * @param callbackSuccess 			success callback for this API
		 * @param callbackError 			error callback for this API
		 */
		turnWiFiOnOff: function(robotId, flag, wiFiTurnOnDurationInSec, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.turnWiFiOnOff(robotId, flag, wiFiTurnOnDurationInSec, 
					callbackSuccess, callbackError);
		},
		
		/**
		 * This API sets the robot name. It is deprecated as of now. Please use setRobotName2 instead.
		 * <p>
		 * on error it returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * 
		 * @param robotId			the serial number of the robot
		 * @param robotName			the name of the robot
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @deprecated 				Replaced by {@link #setRobotName2(robotId, robotName, callbackSuccess, callbackError)}
		 * @see						#setRobotName2(robotId, robotName, callbackSuccess, callbackError)
		 */
		setRobotName : function(robotId, robotName, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setRobotName(robotId, robotName, callbackSuccess, callbackError);
		},
		
		/**
		 * This API sets the robot name.
		 * <p>
		 * on success this API returns a JSON Object {robotId:"robotId", robotName:"robotName"}
		 * <br>where robotId is the serial number of the robot
		 * <br>robotName is the new name of the robot
		 * <p>
		 * on error it returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * 
		 * @param robotId			the serial number of the robot
		 * @param robotName			the name of the robot
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @returns					a JSON Object
		 */
		setRobotName2 : function(robotId, robotName, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setRobotName2(robotId, robotName, callbackSuccess, callbackError);
		},
		
		/**
		 * This API gets the robot details.
		 * <p>
		 * on success this API returns a JSON Object {robotId:"robotId", robotName:"robotName"}
		 * <br>where robotId is the robot's id
		 * <br>robotName is the robot's name
		 * <p>
		 * on error it returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * 
		 * @param robotId			the serial number of the robot
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 * @returns					a JSON Object
		 */		
		getRobotDetail : function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getRobotDetail(robotId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API checks if a robot is online or not
		 * <p>
		 * on success this API returns a JSON Object {robotId:"robotId", online:"online"}
		 * <br>where robotId is the serial number of the robot
		 * <br>online is the boolean value (true/false) describing state of the robot
		 * <p>
		 * on error it returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * 
		 * @param robotId			the serial number of the robot
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 * @returns					a JSON Object
		 */		
		getRobotOnlineStatus : function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getRobotOnlineStatus(robotId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API checks if a robot is online or not (timed mode implementation)
		 * <p>
		 * on success this API returns a JSON Object {robotId:"robotId", online:"online"}
		 * <br>where robotId is the serial number of the robot
		 * <br>online is the boolean value (true/false) describing the state of the robot
		 * <p>
		 * on error it returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * 
		 * @param robotId			the serial number of the robot
		 * @param callbackSuccess	success callback for the API
		 * @param callbackError		error callback for the API
		 * @returns					a json object
		 */		
		getRobotVirtualOnlineStatus : function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getRobotVirtualOnlineStatus(robotId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API is not supported as of now.
		 * 
		 * @param robotId			the serial number of the robot
		 * @param scheduleType		the schedule type of the robot(e.g. Basic or Advanced)
		 * @param jsonArray			the schedule Data to be set for the robot
		 * @param callbackSuccess	success callback for the API
		 * @param callbackError		error callback for the API
		 */
		setSchedule: function(robotId, scheduleType, jsonArray, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setSchedule(robotId, scheduleType, jsonArray, callbackSuccess, callbackError);
		},
		
		/**
		 * This API is not supported as of now
		 * 
		 * @param robotId			the serial number of the robot
		 * @param scheduleType		the schedule type of the robot(e.g. Basic or Advanced)
		 * @param callbackSuccess	success callback for the robot
		 * @param callbackError		error callback for the robot
		 */
		getSchedule: function(robotId, scheduleType, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getSchedule(robotId, scheduleType,  callbackSuccess, callbackError);
		},
		
		/**
		 * This API is not supported as of now
		 * 
		 * @param robotId			the serial number of the robot
		 * @param scheduleType		the schedule type of the robot(e.g. Basic or Advanced)
		 * @param callbackSuccess	success callback for the API
		 * @param callbackError		error callback for the API
		 */
		deleteSchedule: function(robotId, scheduleType, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.deleteSchedule(robotId, scheduleType, callbackSuccess, callbackError);
		},
		
		/**
		 * This API is not supported as of now
		 * 
		 * @param robotId			the serial number of the robot
		 * @param callbackSuccess	success callback for the API
		 * @param callbackError		error callback for the API
		 */
		getMaps: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getMaps(robotId, callbackSuccess, callbackError);
		}, 
		
		/**
		 * This API is not supported as of now
		 * 
		 * @param robotId			the serial number of the robot
		 * @param mapId				the map id
		 * @param mapOverlayInfo	the map overlay info
		 * @param callbackSuccess	success callback for the API
		 * @param callbackError		error callback for the API
		 */
		setMapOverlayData : function(robotId, mapId, mapOverlayInfo, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setMapOverlayData (robotId, mapId, mapOverlayInfo, callbackSuccess, callbackError);
		},
		
		// It will give the atlas xml data.
		/**
		 * This API is not supported as of now
		 * 
		 * @param robotId			the serial number of the robot 
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API * 
		 */
		getRobotAtlasMetadata: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getRobotAtlasMetadata(robotId, callbackSuccess, callbackError);
		},		
				
		// It will update the atlas mapped to this robotId. The version of the xml is stored inside.
		/**
		 * This API is not supported as of now
		 * 
		 * @param robotId			the serial number of the robot
		 * @param atlasMetadata		the atlas meta data
		 * @param callbackSuccess	success callback for the API
		 * @param callbackError		error callback for the API
		 */
		updateAtlasMetaData: function(robotId, atlasMetadata, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.updateAtlasMetaData(robotId, atlasMetadata, callbackSuccess, callbackError);
		},
		
		// TODO: We are taking robotId. Analyse if taking atlasId is a better option.
		/**
		 * This API is not supported as of now
		 * 
		 * @param robotId			the serial number of the robot
		 * @param gridId			the grid id	
		 * @param callbackSuccess	success callback for the API
		 * @param callbackError		error callback for the API
		 */
		getAtlasGridData: function(robotId, gridId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getAtlasGridData(robotId, gridId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API registers for notifications from the robot. For now it registers
		 * all notifications for the robot i.e. robot needs cleaning, cleaning is done,
		 * robot is stuck
		 * <p>
		 * The API calls Neato Smart App Service
		 * 
		 * @param robotId			the serial number of the robot
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 */
		registerNotifications: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.registerNotifications(robotId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API unregisters for notifications from the robot. Currently it 
		 * unregisters for all notifications for the robot i.e. robot needs cleaning,
		 * cleaning is done, and robot is stuck
		 * <p>
		 * This API calls Neato Smart App Service.
		 * 
		 * @param robotId			the serial number of the robot
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 */
		unregisterNotifications: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.unregisterNotifications(robotId, callbackSuccess, callbackError);
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
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API 
		 * @returns					a JSON Object on success
		 */
		registerNotifications2: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.registerNotifications2(callbackSuccess, callbackError);
		},
		
		/**
		 * This API unregisters for all notifications from the robot.
		 * 
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError  	error callback for this API
		 */
		unregisterNotifications2: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.unregisterNotifications2(callbackSuccess, callbackError);
		},
		
		/**
		 * This API notifies push messages sent by server to UI.
		 * 
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 */
		registerForRobotMessages: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.registerForRobotMessages(callbackSuccess, callbackError);
		},
		
		/**
		 * This API unregisters for push messages sent by server to UI.
		 * 
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 */
		unregisterForRobotMessages: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.unregisterForRobotMessages(callbackSuccess, callbackError);
		},
		
		// New Schedule APIs being added:		
		/**
		 * This API creates a new local schedule for the robot. If you were already working with a local schedule
		 * that data is lost, and this new schedule will be updated in subsequent calls.
		 * <p>
		 * This API saves the schedule in the database and hence does not call webservice.
		 * The schedule type can have values
		 * <br>1 - SCHEDULE_TYPE_BASIC
		 * <br>2 - SCHEDULE_TYPE_ADVANCED. 
		 * <br>As of now only SCHEDULE_TYPE_BASIC is supported.
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{scheduleId:"scheduleId", robotId:"robotId", scheduleType:"scheduleType"}
		 * <br>where scheduleId is the schedule id for the schedule
		 * <br>robotId is the robot serial number
		 * <br>scheduleType is the schedule Type(SCHEDULE_TYPE_BASIC or SCHEDULE_TYPE_ADVANCED)
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error       
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * <br>1012 - JSON Creation Error
		 * 
		 * @param robotId 			the serial number of the robot
		 * @param scheduleType 		the schedule type of the robot
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 * @returns					a JSON Object
		 */
		createSchedule: function(robotId, scheduleType, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.createSchedule(robotId, scheduleType, callbackSuccess, callbackError);
		},
		
		/**
		 * This API makes a call to the server to get the Schedule Events for the robot. Once we fetch the schedule data
		 * from server, we cache this data locally and further changes are done on the local copy
		 * <p>
		 * The schedule Type can have types -
		 * <br>1 - SCHEDULE_TYPE_BASIC
		 * <br>2 - SCHEDULE_TYPE_ADVANCED.
		 * <br>As of now only SCHEDULE_TYPE_BASIC is supported
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{scheduleId:"scheduleId", robotId:"robotId", scheduleType:"scheduleType", scheduleEventLists:"scheduleEventLists"}
		 * <br>where scheduleId is the id of the schedule
		 * <br>robotId is the serial number of the robot
		 * <br>scheduleType is schedule Type (could be SCHEDULE_TYPE_BASIC or SCHEDULE_TYPE_ADVANCED)
		 * <br>scheduleEventLists is a JSON Array with all schedule Event ids for the scheduleEvents of the schedule
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error       
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * <br>1012 - JSON Creation Error
		 * 
		 * @param robotId 			the serial number of the robot
		 * @param scheduleType 		the schedule type of the schedule
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 * @returns					a JSON Object
		 */
		getScheduleEvents: function(robotId, scheduleType, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getScheduleEvents(robotId, scheduleType, callbackSuccess, callbackError);
		},

		/**
		 * This API gets the schedule data for the event. It fetches the data from the local database
		 * and hence does not make a web service call.
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{scheduleId:"scheduleId", scheduleEventId:"scheduleEventId", scheduleEventData:"scheduleEventData"}
		 * <br>where scheduleId is the id of the schedule
		 * <br>scheduleEventId is the event id of the schedule
		 * <br>scheduleEventData is a JSON Object
		 * <br>{day:"day", startTime:"startTime", cleaningMode:"cleaningMode"}
		 * <br>where day is an integer value(0(DAY_SUNDAY) to 6(DAY_SATURDAY))
		 * <br>startTime is time in format(hh:mm)
		 * <br>cleaning Mode is (ECO or NORMAL)
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error       
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * <br>1012 - JSON Creation Error
		 * 
		 * @param scheduleId 		the schedule id of the schedule
		 * @param scheduleEventId 	the schedule event id of the schedule
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 */
		getScheduleEventData: function(scheduleId, scheduleEventId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getScheduleEventData(scheduleId, scheduleEventId, callbackSuccess, callbackError);
		},		
		
		/**
		 * This API adds a schedule event in the local copy of the robot schedule. No changes
		 * are made to the server data until updateSchedule is called. It does not make a
		 * web service call. It returns the scheduleEventId of the added scheduleEvent for 
		 * further tracking.
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{scheduleId:"scheduleId", scheduleEventId:"scheduleEventId"}
		 * <br>where scheduleId is the id of the schedule
		 * <br>scheduleEventId is the id of the added schedule Event
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error       
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * <br>1012 - JSON Creation Error
		 * 
		 * @param scheduleId 			the schedule id of the schedule
		 * @param scheduleEventData 	the schedule event data for the schedule
		 * @param callbackSuccess 		success callback for the API
		 * @param callbackError 		error callback for the API
		 * @returns						a JSON Object
		 */
		addScheduleEvent: function(scheduleId, scheduleEventData, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.addScheduleEvent(scheduleId, scheduleEventData, callbackSuccess, callbackError);
		},

		/**
		 * This API updates a schedule event in the local copy of the robot schedule. No changes are
		 * made to the server data until updateSchedule is called
		 * <p>
		 * on success this API returns a JSON Object of updated schedule Event
		 * <br>{scheduleId:"scheduleId", scheduleEventId:"scheduleEventId"}
		 * <br>where scheduleId is the id of the schedule
		 * <br>and schedule Event Id is the updated schedule event id
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error       
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * <br>1012 - JSON Creation Error
		 * 
		 * @param scheduleId 			the schedule id of the schedule
		 * @param scheduleEventId 		the schedule event id of the schedule
		 * @param scheduleEventData 	the schedule event data for the schedule event id
		 * @param callbackSuccess 		success callback for the API
		 * @param callbackError 		error callback for the API
		 * @returns						a JSON Object
		 */
		updateScheduleEvent: function(scheduleId, scheduleEventId, scheduleEventData, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.updateScheduleEvent(scheduleId, scheduleEventId, scheduleEventData, callbackSuccess, callbackError);
		},

		/**
		 * This API deletes a schedule event from the local copy of the robot schedule. No changes
		 * are made to the server data until updateSchedule is called
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{scheduleId:"scheduleId", scheduleEventId:"scheduleEventId"}
		 * <br>where scheduleId is the id of the deleted schedule
		 * <br>scheduleEventId is the event id of the deleted schedule
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error       
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * <br>1012 - JSON Creation Error
		 * 
		 * @param scheduleId 		the schedule id of the schedule
		 * @param scheduleEventId 	the schedule event id of the schedule
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @returns					a JSON Object
		 */		
		deleteScheduleEvent: function(scheduleId, scheduleEventId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.deleteScheduleEvent(scheduleId, scheduleEventId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API updates the server data with the local copy of the schedule. If the 
		 * schedule id does not exist it will create a new schedule with this id on the server.
		 * <p>
		 * on success this API returns a JSON Object {scheduleId:"scheduleId"}
		 * <br>where scheduleId is the updated schedule ID
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error       
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * <br>1012 - JSON Creation Error
		 * <br>1014 - User Unauthroized Error
		 * 
		 * @param scheduleId 		the schedule id of the schedule
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @returns					a JSON Object
		 */
		updateSchedule: function(scheduleId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.updateSchedule(scheduleId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API is a helper API which returns the entire event items in a single call.
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{scheduleType:"scheduleType", scheduleId:"scheduleId", schedules:"scheduleArray"}
		 * <br>where scheduleId is the id of the schedule
		 * <br>scheduleType is the schedule type(SCHEDULE_TYPE_BASIC or SCHEDULE_TYPE_NORMAL)
		 * <br>scheduleArray is a JSON Array with each schedule Event as a JSON Object like
		 * <br>{day:"day", startTime:12:00, cleaningMode:"cleaningMode"}
		 * <br>where day is an integer value(0(DAY_SUNDAY) to 6(DAY_SATURDAY))
		 * <br>startTime is a time value(hh:mm)
		 * <br>cleaning Mode is (ECO or NORMAL)
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error       
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * <br>1005 - Invalid Schedule Id
		 * <br>1012 - JSON Creation Error
		 * 
		 * @param scheduleId 		the schedule id of the schedule
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @returns					a JSON Object
		 */
		getScheduleData: function(scheduleId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getScheduleData(scheduleId, callbackSuccess, callbackError);
		},
		
		/**
		 * This API checks if a schedule is enabled on server for the specific robot
		 * <p>
		 * scheduleType is an integer value. The value can be
		 * <br>1 - SCHEDULE_TYPE_BASIC
		 * <br>2 - SCHEDULE_TYPE_ADVANCED.
		 * <br>As of now only SCHEDULE_TYPE_BASIC is supported
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{isScheduleEnabled:"isScheduleEnabled", scheduleType:"scheduleType", robotId:"robotId"}
		 * <br>where isScheduleEnabled is boolean value (true/false) describing schedule state
		 * <br>scheduleType is an integer value
		 * <br>robotId is the serial number of the robot
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * 
		 * @param robotId 			the serial number of the robot
		 * @param scheduleType 		Integer value
		 * @param callbackSuccess 	success callback for the API
		 * @param callbackError 	error callback for the API
		 * @returns					a JSON Object
		 */
		isScheduleEnabled: function(robotId, scheduleType, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.isScheduleEnabled(robotId, scheduleType, callbackSuccess, callbackError);
		}, 
		
		/**
		 * This API enables or disables schedule on the server for the specific robot
		 * <p>
		 * scheduleType is an integer value. The value can be
		 * <br>1 - SCHEDULE_TYPE_BASIC
		 * <br>2 - SCHEDULE_TYPE_ADVANCED.
		 * <br>As of now only SCHEDULE_TYPE_BASIC is supported
		 * <p>
		 * on success this API returns a JSON Object
		 * <br>{isScheduleEnabled:"isScheduleEnabled", scheduleType:"scheduleType", robotId:"robotId"}
		 * <br>isScheduleEnabled is boolean value (true/false)
		 * <br>scheduleType is an integer value
		 * <br>robotId is the serial number of the robot
		 * <p>
		 * on error this API returns a JSON Object {errorCode:"errorCode", errMessage:"errMessage"}
		 * <br>where errorCode is the error type and it's values are
		 * <br>1001 - Unknown Error
		 * <br>1002 - Network Error
		 * <br>1003 - Server Error
		 * <br>1004 - JSON Parsing Error
		 * 
		 * @param robotId 			the serial number of the robot
		 * @param scheduleType 		Integer value. Must be SCHEDULE_TYPE_BASIC or SCHEDULE_TYPE_ADVANCED
		 * @param enable 			boolean value. true to enable schedule and false to disable schedule
		 * @param callbackSuccess 	success callback for this API
		 * @param callbackError 	error callback for this API
		 * @returns					a JSON Object
		 */
		enableSchedule: function(robotId, scheduleType, enable, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.enableSchedule(robotId, scheduleType, enable, callbackSuccess, callbackError);
		}		
	}
}());

var PluginManagerHelper =  (function() {
	return {
		addToAdvancedSchedule: function(scheduleJsonArray, day, startTime, endTime, eventType, area) {
			
			var schedule = {'day':day, 'startTime': startTime, 
					'endTime': endTime, 'eventType': eventType,
					'area':area};
			scheduleJsonArray.push(schedule);
			return scheduleJsonArray;
		},	
		getBasicScheduleEvent: function(day, startTime) {
			var schedule = {'day':day, 'startTime': startTime};
			return schedule;
		},
		
		/*
		 * Name: createBasicScheduleEventObject
		 * Helper method to form a BasicSchedule EventData
		 * Params
		 *  - day
		 *  - startTime
		 *  - cleaningMode
		 *  Returns: Basic Schedule JSON object
		 *  {'day':day, 'startTime': startTime, ‘cleaningMode’:cleaningMode}
		 */
		
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

var scheduleEventHelper =  (function() {
			return {

			removeEventId: function(eventList, eventId) {
				var l = eventList.length;
				var newArr = [];
				    for(var i = 0 ;i < l; i++)
				    {
				        if(eventList[i] == eventId) {
				        } else {
				        	 newArr.push(eventList[i]);
				        }
				    }
				return newArr;
			},
			
			addEventId: function(eventList, eventId) {
				eventList.push(eventId);
				return eventList;
			},
			
			getEventId: function(eventList, index) {
				return eventList[index];
			}
		};	
}());