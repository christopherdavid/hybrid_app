
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
var ACTION_TYPE_GET_ASSOCIATED_ROBOTS 			= "getAssociatedRobots"
var ACTION_TYPE_DISASSOCIATE_ROBOT 				= "disassociateRobot";
var ACTION_TYPE_DISASSOCAITE_ALL_ROBOTS 		= "disassociateAllRobots";
var ACTION_TYPE_FORGET_PASSWORD					= "forgetPassword";
var ACTION_TYPE_CHANGE_PASSWORD					= "changePassword";
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
var ACTION_TYPE_REGISTER_ROBOT_NOTIFICATIONS    = "registerRobotNotifications";
var ACTION_TYPE_UNREGISTER_ROBOT_NOTIFICATIONS  = "unregisterRobotNotifications";
var ACTION_TYPE_SET_SPOT_DEFINITION				= "setSpotDefinition";
var ACTION_TYPE_GET_SPOT_DEFINITION				= "getSpotDefinition";
var ACTION_TYPE_DRIVE_ROBOT						= "driveRobot";

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

/*
 * Name: loginUser
 * Login using the email and password provided. If credentials matches logs in and returns
 * the User JSON object
 * User JSON object – represents a user, and returned by methods dealing with users.
 * {userId:<userId>, email:<emailId>, username:<username>, alternate_email:<alternate_email>, validation_status:<validation_status>}
 * validation_status is will be one of the following value
 * USER_STATUS_VALIDATED, USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD or USER_STATUS_NOT_VALIDATED
 * If validation_status is USER_STATUS_NOT_VALIDATED, we should not allow user to use the application
 * In case user fails to login error is returned in callbackError
 * All errors are represented by JSON object 
 * {errorCode:<code>, errorMessage:<errorMessage>}
 */

UserMgr.prototype.loginUser = function(email, password, callbackSuccess, callbackError) {
	var loginArray = {'email':email, 'password':password};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_LOGIN, [loginArray]);
};

/*
 * Name: logoutUser
 * Logout the user from the system. Clear all the logged in user information locally
 * Does not contain any return value 
 */
UserMgr.prototype.logoutUser = function(callbackSuccess, callbackError) {
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_LOGOUT, []);
};

/*
 * Name: isUserLoggedIn
 * checks if a user is already logged in
 * returns true if user is logged in, false otherwise
 * Error callback is not called.
 */

UserMgr.prototype.isUserLoggedIn = function(email, callbackSuccess, callbackError) {
	var isUserLoggedInArray = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_ISLOGGEDIN, [isUserLoggedInArray]);
};


/*
 * Name: createUser
 * Creates a new user. This API does not trigger the email validation.
 * Server assumes whatever email is provided does exists. This API will be depreicated
 * Once we have the email validation infrastructure in place
 * Params
 *  - Email
 *  - password
 *  - UserName
 *  Returns the User JSON object if successful
 * In case of error callbackError gets called
 */
UserMgr.prototype.createUser = function(email, password, name, callbackSuccess, callbackError) {
	var registerArray = {'email':email, 'password':password, 'userName':name};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_CREATE_USER, [registerArray]);
};


/*
 * Name: createUser2
 * Creates a new user. This API will trigger the email validation.
 * Params
 *  - Email
 *  - password
 *  - UserName
 *  - alternateEmail (optional)
 *  Returns the User JSON object if successful
 * In case of error callbackError gets called
 */
UserMgr.prototype.createUser2 = function(email, password, name, alternateEmail, callbackSuccess, callbackError) {
	var registerArray = {'email':email, 'password':password, 'userName':name, 'alternate_email':alternateEmail};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_CREATE_USER2, [registerArray]);
};


/*
 * Name: resendValidationMail
 * Resend the validation email to the user email id

 * Params
 *  - Email
 *  - password
 *  - UserName
 *  Returns the User JSON object if successful
 * In case of error callbackError gets called
 */
UserMgr.prototype.resendValidationMail = function(email, callbackSuccess, callbackError) {
	var registerArray = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_RESEND_VALIDATION_MAIL, [registerArray]);
};


/*
 * Name: isUserValidated
 * checks if a user is validated or not
 * returns JSON in success callback
 * {"validation_status":<validation_status>, message:<message>}
 * validation_status is will be one of the following value
 * USER_STATUS_VALIDATED, USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD or USER_STATUS_NOT_VALIDATED
 * If validation_status is USER_STATUS_NOT_VALIDATED, we should not allow user to use the application
 * 
 */

UserMgr.prototype.isUserValidated = function(email, callbackSuccess, callbackError) {
	var isUserValidJsonArray = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_IS_USER_VALIDATED, [isUserValidJsonArray]);
};

/*
 * Name: changePassword
 * Change the password of the user
 * Params
 *  - email
 *  - user's current password
 *  - new password
 * returns None in case of success
 * In error case error callback gets called with the Error JSON object
 * 
 */
UserMgr.prototype.changePassword = function(email, currentPassword, newPassword, callbackSuccess, callbackError) {
	var changePassword = {'email':email, 'currentPassword':currentPassword, 'newPassword':newPassword};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_CHANGE_PASSWORD, [changePassword]);
};

/*
 * Name: forgetPassword
 * Server sends the email to the user email id
 * Params
 *  - email
 * returns None in case of success
 * In error case error callback gets called with the Error JSON object
 * 
 */
UserMgr.prototype.forgetPassword = function(email, callbackSuccess, callbackError) {
	var forgetPassword = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_FORGET_PASSWORD, [forgetPassword]);
};

/*
 * Name: getUserDetail
 * Returns the User JSON object
 * Params
 *  - email
 * Gets the user details information from server
 * 
 */
UserMgr.prototype.getUserDetail = function(email, callbackSuccess, callbackError) {
	var getUserDetailsArray = {'email': email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_USER_DETAILS, [getUserDetailsArray]);
};


/*
 * Name: associateRobotCommand
 * Associates the robot with the user. 
 * Returns none
 * Params
 *  - email
 *  - robotId (which needs to be associated)
 */

UserMgr.prototype.associateRobotCommand = function(email, robotId, callbackSuccess, callbackError) {
	var associateArray = {'email':email, 'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_ASSOCIATE_ROBOT, [associateArray]);
};

/*
 * Name: getAssociatedRobots
 * Get the associated robots list in JSON 
 * Returns a JSONArray of associated robots.
 * [{robotId:<robotId>,  robot_name: <robot_name>},{ robotId: <robotId>,  robot_name:<robot_name>}]
 * Params
 *  - email
 */

UserMgr.prototype.getAssociatedRobots = function(email, callbackSuccess, callbackError) {
	var getAssociatedRobotsArray = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ASSOCIATED_ROBOTS, [getAssociatedRobotsArray]);
};

/*
 * Name: disassociateRobot
 * disassociate the robot from the specified user 
 * Returns None
 * Params
 *  - email
 *  - robotId
 */

UserMgr.prototype.disassociateRobot = function(email, robotId, callbackSuccess, callbackError) {
	var disassociateRobotArray = {'email':email, 'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DISASSOCIATE_ROBOT, [disassociateRobotArray]);
};


/*
 * Name: disassociateAllRobots
 * disassociates all the robots from the specified user 
 * Returns None
 * Params
 *  - email
 */

UserMgr.prototype.disassociateAllRobots = function(email, callbackSuccess, callbackError) {
	var disassociateAllRobotsArray = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DISASSOCAITE_ALL_ROBOTS, [disassociateAllRobotsArray]);
};


// ***********************ROBOT PLUGIN METHODS ****************************

/*
 * Name: registerNotifications
 * Register for the notifications from the Robot. As of now this API
 * register for all type of notifications. 
 * Params
 *  - robotId
 *  Returns: None
 */

RobotMgr.prototype.registerNotifications = function(robotId, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId};
	
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_REGISTER_ROBOT_NOTIFICATIONS, [commandArray]);
};

/*
 * Name: unregisterNotifications
 * Unregister for the notifications from the Robot. 
 * Params
 *  - robotId
 *  Returns: None
 */

RobotMgr.prototype.unregisterNotifications = function(robotId, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId};
	
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_UNREGISTER_ROBOT_NOTIFICATIONS, [commandArray]);
};

/*
 * Name: discoverNearbyRobots
 * starts discovering nearby robots that are on the same subnet.
 * Params: None
 * Returns a JSONArray of discovered robots
 * [{robotId:<robotId>,  robot_name: <robot_name>},{ robotId: <robotId>,  robot_name:<robot_name>}]
 */

RobotMgr.prototype.discoverNearbyRobots = function(callbackSuccess, callbackError) {
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DISCOVER_NEARBY_ROBOTS, []);
};

/*
 * Name: tryDirectConnection (Deprecated because it uses the old port number to connect). Use tryDirectConnection2
 * tries to establish a direct peer-to-peer connection with the robot. 
 * The robot and the smart app need to be on the same subnet for this connection to work.
 * Params: Robot id
 * Returns an error if connection cannot be established
 */
RobotMgr.prototype.tryDirectConnection = function(robotId, callbackSuccess, callbackError) {
	var connectPeerCommandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_TRY_CONNECT_CONNECTION, [connectPeerCommandArray]);
};

/*
 * Name: tryDirectConnection2
 * tries to establish a direct peer-to-peer connection with the robot. 
 * The robot and the smart app need to be on the same subnet for this connection to work.
 * Params: Robot id
 * Returns an error if connection cannot be established
 */
RobotMgr.prototype.tryDirectConnection2 = function(robotId, callbackSuccess, callbackError) {
	var connectPeerCommandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_TRY_CONNECT_CONNECTION2, [connectPeerCommandArray]);
};


/*
 * Name: disconnectDirectConnection
 * tears down the existing direct connection (established via tryDirectConnection2/tryDirectConnection)
 * Params: Robot id
 */

RobotMgr.prototype.disconnectDirectConnection  = function(robotId, callbackSuccess, callbackError) {
	var disconnectPeerCommandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DISCONNECT_DIRECT_CONNETION, [disconnectPeerCommandArray]);
};



/*
 * Name: sendCommandToRobot (Deprecated because it uses the old command format). Use sendCommandToRobot2
 * sends a command to a specific robot
 * The robot and the smart app need to be on the same subnet for this connection to work.
 * Params: 
 *  Robot id
 *  Command Id – currently only 2 commands are supported: Start Cleaning (101) and Stop Cleaning (102). 
 *         Over time more commands (like Send To Base) will be added. 
 *         Currently always sends a command via the presence server (XMPP) – it does not use the direct connection, 
 *         but it may do so at a later stage.
 * Returns: None currently, but we may need to return the response from the command.
 */

RobotMgr.prototype.sendCommandToRobot = function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId, 'commandId':commandId, 'commandParams':commandParams};
	
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SEND_COMMAND_TO_ROBOT, [commandArray]);
};

/*
 * Name: sendCommandToRobot2
 * sends a command to a specific robot
 * The robot and the smart app need to be on the same subnet for this connection to work.
 * Params: 
 *  Robot id
 *  Command Id – currently only 2 commands are supported: Start Cleaning (101) and Stop Cleaning (102). 
 *         Over time more commands (like Send To Base) will be added. 
 *         NOTE: Though you can send "Start Cleaning" and "Stop Cleaning" commands using sendCommandToRobot2 but
 *         we have also exposed separate APIs. See "startCleaning", "stopCleaning", "pauseCleaning" and "resumeCleaning"
 *         Use specific API rather than calling "sendCommandToRobot2"
 *         Currently always sends a command via the presence server (XMPP) – it does not use the direct connection, 
 *         but it may do so at a later stage.
 * Returns: None currently, but we may need to return the response from the command.
 */

RobotMgr.prototype.sendCommandToRobot2 = function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
	var params = {'params': commandParams};
	var commandArray = {'robotId':robotId, 'commandId':commandId, 'commandParams':params};
	
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SEND_COMMAND_TO_ROBOT2, [commandArray]);
};


/*
 * Name: setRobotName (Deprecated use setRobotName2). setRobotName2 returns the updated Robot infomration
 * Set the Robot Name
 * Params: 
 *  Robot id
 *  Robot Name 
 *  Returns None
 */

RobotMgr.prototype.setRobotName = function(robotId, robotName, callbackSuccess, callbackError) {
	var setRobotName = {'robotId':robotId, 'robotName':robotName};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SET_ROBOT_NAME, [setRobotName]);
};

/*
 * Name: setRobotName2 
 * Set the Robot Name
 * Params: 
 *  Robot id
 *  Robot Name 
 *  Returns: Robot JSON object
 *  {robotId:<robotId>,  robot_name: <robot_name>}
 */
RobotMgr.prototype.setRobotName2 = function(robotId, robotName, callbackSuccess, callbackError) {
	var setRobotName = {'robotId':robotId, 'robotName':robotName};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SET_ROBOT_NAME_2, [setRobotName]);
};


/*
 * Name: getRobotDetail 
 * Get the robot details. As of now robot detail consists only robot name and robot id
 * Params: 
 *  Robot id
 *  Returns: Robot JSON object
 *  {robotId:<robotId>,  robot_name: <robot_name>}
 */

RobotMgr.prototype.getRobotDetail = function(robotId, callbackSuccess, callbackError) {
	var getRobotDetail = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ROBOT_DETAIL, [getRobotDetail]);
};

/*
 * Name: getRobotOnlineStatus 
 * Set the Robot Name
 * Params: 
 *  Robot id
 *  Returns: Robot JSON object
 *  {robotId:<robotId>,  online: true/false}
 */
RobotMgr.prototype.getRobotOnlineStatus = function(robotId, callbackSuccess, callbackError) {
	var getRobotStatus = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ROBOT_ONLINE_STATUS, [getRobotStatus]);
};

RobotMgr.prototype.setSchedule = function(robotId, scheduleType, jsonArray, callbackSuccess, callbackError) {
	var scheduleArray = {'robotId':robotId, 'scheduleType':scheduleType, 'schedule': jsonArray};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACIION_TYPE_SET_SCHEDULE, [scheduleArray]);
};

RobotMgr.prototype.getSchedule = function(robotId, scheduleType, callbackSuccess, callbackError) {
	var scheduleArray = {'robotId':robotId, 'scheduleType':scheduleType};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACIION_TYPE_GET_ROBOT_SCHEDULE, [scheduleArray]);
};

RobotMgr.prototype.deleteSchedule = function(robotId, scheduleType, callbackSuccess, callbackError) {
	var scheduleArray = {'robotId':robotId, 'scheduleType':scheduleType};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DELETE_ROBOT_SCHEDULE, [scheduleArray]);
};

RobotMgr.prototype.getMaps = function(robotId, callbackSuccess, callbackError) {
	var mapArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ROBOT_MAP, [mapArray]);
};

RobotMgr.prototype.setMapOverlayData = function(robotId, mapId, mapOverlayInfo, callbackSuccess, callbackError) {
	var mapArray = {'robotId':robotId, 'mapId':mapId, 'mapOverlayInfo':mapOverlayInfo};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SET_MAP_OVERLAY_DATA, [mapArray]);
};

// Atlas action types
RobotMgr.prototype.getRobotAtlasMetadata = function(robotId, callbackSuccess, callbackError) {
	var atlasArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ROBOT_ATLAS_METADATA, [atlasArray]);
};


RobotMgr.prototype.updateAtlasMetaData = function(robotId, atlasMetadata, callbackSuccess, callbackError) {
	var atlasArray = {'robotId':robotId, 'atlasMetadata':atlasMetadata};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_UPDATE_ROBOT_ATLAS_METADATA, [atlasArray]);
};

RobotMgr.prototype.getAtlasGridData = function(robotId, gridId, callbackSuccess, callbackError) {
	var getGridArray = {'robotId':robotId, 'gridId':gridId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ATLAS_GRID_DATA, [getGridArray]);
};

/*
 * Name: createSchedule
 * This API creates a new local schedule for the robot. Note that if you were already working with a local schedule, 
 * that data is lost, and this new schedule will be updated in subsequent calls. 
 * Params
 *  - robotId
 *  - scheduleType (SCHEDULE_TYPE_BASIC or SCHEDULE_TYPE_ADVANCED). As of now only SCHEDULE_TYPE_BASIC is supported
 * Returns: 
 * {'scheduleId':scheduleId, ‘robotId’: robotId, ‘scheduleType’: scheduleType}

 */

RobotMgr.prototype.createSchedule = function(robotId, scheduleType, callbackSuccess, callbackError) {
	var createSchedule = {'robotId':robotId, 'scheduleType':scheduleType};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_CREATE_SCHEDULE, [createSchedule]);
};

/*
 * Name: getScheduleEvents
 * getScheduleEvents makes a call to the server to get the Schedule Events for the robot. 
 * Once we fetch the schedule data from server, we cache this data locally and further changes are done on the local copy.
 * Params
 *  - robotId
 *  - scheduleType (SCHEDULE_TYPE_BASIC or SCHEDULE_TYPE_ADVANCED). As of now only SCHEDULE_TYPE_BASIC is supported
 * Returns: JSON Object of Schedule data
 * {'scheduleId':scheduleId, ‘robotId’: robotId, ‘scheduleType’: scheduleType, scheduleEventLists:’ scheduleEventLists}
 * ScheduleEventLists is a JSONArray with all the scheduleEventIds for the scheduleEvents of the schedule
 */

RobotMgr.prototype.getScheduleEvents = function(robotId, scheduleType, callbackSuccess, callbackError) {
	var getScheduleEvents = {'robotId':robotId, 'scheduleType':scheduleType};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_SCHEDULE_EVENTS, [getScheduleEvents]);
};

/*
 * Name: getScheduleEventData
 * getScheduleEventData gets the schedule data for the event. This API fetches the data from the local database
 * Params
 *  - scheduleId
 *  - scheduleEventId
 * Returns: JSON Object of Schedule data
 * {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId, 'scheduleEventData':scheduleEventData};
 * scheduleEventData for the basic event is a JSON object {'day':day, 'startTime': startTime, ‘cleaningMode’:cleaningMode}
 */


RobotMgr.prototype.getScheduleEventData = function(scheduleId, scheduleEventId, callbackSuccess, callbackError) {
	var getScheduleEventData = {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_SCHEDULE_EVENT_DATA, [getScheduleEventData]);
};

/*
 * Name: addScheduleEvent
 * addScheduleEvent adds a schedule event in the local copy of the robot schedule. 
 * No changes are made to the server data until updateSchedule is called
 * Params
 *  - scheduleId
 *  - scheduleEventData (returns from createBasicScheduleEventObject helper API)
 * Returns: {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId}
 * It returns the scheduleEventId of the added scheduleEvent for further tracking.
 */

RobotMgr.prototype.addScheduleEvent = function(scheduleId, scheduleEventData, callbackSuccess, callbackError) {
	var addScheduleEventData = {'scheduleId':scheduleId, 'scheduleEventData':scheduleEventData};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_ADD_ROBOT_SCHEDULE_EVENT, [addScheduleEventData]);
};

/*
 * Name: updateScheduleEvent
 * updateScheduleEvent API updates a schedule event in the local copy of the robot schedule. 
 * No changes are made to the server data until updateSchedule is called
 * Params
 *  - scheduleId
 *  - scheduleEventId - Schedule Event id
 *  - scheduleEventData (returns from createBasicScheduleEventObject helper API)
 * Returns: JSON object of the updated schedule Event
 * {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId}
 */

RobotMgr.prototype.updateScheduleEvent = function(scheduleId, scheduleEventId, scheduleEventData, callbackSuccess, callbackError) {
	var updateScheduleEvent = {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId, 'scheduleEventData':scheduleEventData};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_UPDATE_ROBOT_SCHEDULE_EVENT, [updateScheduleEvent]);
};

/*
 * Name: deleteScheduleEvent
 * deleteScheduleEvent API deletes a schedule event from the local copy of the robot schedule. 
 * No changes are made to the server data until updateSchedule is called
 * Params
 *  - scheduleId
 *  - scheduleEventId - Schedule Event id
 * Returns a JSON: {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId} of the deleted schedule event
 */


RobotMgr.prototype.deleteScheduleEvent = function(scheduleId, scheduleEventId, callbackSuccess, callbackError) {
	var deleteScheduleEvent = {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DELETE_ROBOT_SCHEDULE_EVENT, [deleteScheduleEvent]);
};


/*
 * Name: updateSchedule
 * updateSchedule API updates the server data with the local copy of the schedule.
 * Params
 *  - scheduleId
 * Returns a JSON: {“scheduleId” : scheduleId} with the scheduleId of the schedule updated.
 */

RobotMgr.prototype.updateSchedule = function(scheduleId, callbackSuccess, callbackError) {
	var updateSchedule = {'scheduleId':scheduleId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_UPDATE_SCHEDULE, [updateSchedule]);
};


/*
 * Name: getScheduleData
 * getScheduleData is just a helper API which returns the entire event items in a single call.
 * Params
 *  - scheduleId
 * Returns: a JSON Object;
 * : {'scheduleId':scheduleId, 'scheduleType': 'scheduleType', schedules:<schedulesArray> }
 * schedulesArray Object will be a JSONArray with the data of each scheduleEvents in schedule.
 * Example:
 *	[{day:1, startTime:12:00, 'cleaningMode':1} , {day:3, startTime:5:00, 'cleaningMode':1}]
 */

RobotMgr.prototype.getScheduleData = function(scheduleId, callbackSuccess, callbackError) {
	var getScheduleData = {'scheduleId':scheduleId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_SCHEDULE_DATA, [getScheduleData]);
};

/*
 * Name: setSpotDefinition
 * Saves spot definition to the DB.
 * Params
 *  - robotId : Value as string. (Must be a valid robotId)
 *  - spotCleaningAreaLength : Value as integer.
 *  - spotCleaningAreaHeight : Value as integer.
 * In case of success, callbackSuccess is called with response as OK.
 * In case of an error, callbackError gets called with error JSON below:
 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
 */
RobotMgr.prototype.setSpotDefinition = function(robotId, spotCleaningAreaLength, spotCleaningAreaHeight,
		callbackSuccess, callbackError) {
	var commandParams = {'robotId':robotId, 'spotCleaningAreaLength':spotCleaningAreaLength, 
			'spotCleaningAreaHeight':spotCleaningAreaHeight};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SET_SPOT_DEFINITION, [commandParams]);
};

/*
 * Name: getSpotDefinition
 * Gets spot definition from the DB.
 * Params
 *  - robotId: Value as string. (Must be a valid robotId)
 * In case of success, callbackSuccess is called with following JSON:
 * 	{'spotCleaningAreaLength':<area length>, 'spotCleaningAreaHeight':<area height>}
 * In case of an error, callbackError gets called with error JSON below:
 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
 */
RobotMgr.prototype.getSpotDefinition = function(robotId, callbackSuccess, callbackError) {
	var commandParams = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_SPOT_DEFINITION, [commandParams]);
};

/*
 * Name: driveRobot
 * Send "drive" command to the robot
 * Params
 *  - robotId: Value as string. (Must be a valid robotId)
 *  - navigationControlId: Valuse as integer. Value must (Must be one of
 *    NAVIGATION_CONTROL_1, NAVIGATION_CONTROL_2, NAVIGATION_CONTROL_3
 *    NAVIGATION_CONTROL_4, NAVIGATION_CONTROL_5 and NAVIGATION_CONTROL_BACK)
 * In case of success, callbackSuccess is called with response as OK.
 * In case of an error, callbackError is called with error JSON below:
 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
 */
RobotMgr.prototype.driveRobot = function(robotId, navigationControlId, callbackSuccess, callbackError) {
	var commandParams = {'robotId':robotId, 'navigationControlId':navigationControlId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DRIVE_ROBOT, [commandParams]);
};

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
 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
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

/*
 * Name: stopCleaning
 * Sends "stop cleaning" command to the robot. 
 * Params
 *  - robotId: Value as string. (Must be a valid robotId)
 * In case of success, callbackSuccess is called with response as OK.
 * In case of an error, callbackError is called with error JSON below:
 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
 */
RobotMgr.prototype.stopCleaning = function(robotId, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_STOP_CLEANING, [commandArray]);
};


/*
 * Name: pauseCleaning
 * Sends "pause cleaning" command to the robot. Robot needs to remember the 
 * parameters sent in startCleaning because in resumeCleaning we don't resend these params
 * Params
 *  - robotId: Value as string. (Must be a valid robotId)
 * In case of success, callbackSuccess is called with response as OK.
 * In case of an error, callbackError is called with error JSON below:
 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
 */

RobotMgr.prototype.pauseCleaning = function(robotId, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_PAUSE_CLEANING, [commandArray]);
};

/*
 * Name: resumeCleaning
 * Sends "pause cleaning" command to the robot. Robot needs to remember the 
 * parameters sent in startCleaning because in resumeCleaning we don't resend these params
 * Params
 *  - robotId: Value as string. (Must be a valid robotId)
 * In case of success, callbackSuccess is called with response as OK.
 * In case of an error, callbackError is called with error JSON below:
 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
 */

RobotMgr.prototype.resumeCleaning = function(robotId, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_RESUME_CLEANING, [commandArray]);
};

var UserPluginManager = (function() {
	return {
		/*
		 * Name: loginUser
		 * Login using the email and password provided. If credentials matches logs in and returns
		 * the User JSON object
		 * User JSON object – represents a user, and returned by methods dealing with users.
		 * {userId:<userId>, email:<emailId>, username:<username>, alternate_email:<alternate_email>, validation_status:<validation_status>}
		 * validation_status is will be one of the following value
		 * USER_STATUS_VALIDATED, USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD or USER_STATUS_NOT_VALIDATED
		 * If validation_status is USER_STATUS_NOT_VALIDATED, we should not allow user to use the application
		 * In case user fails to login error is returned in callbackError
		 * All errors are represented by JSON object 
		 * {errorCode:<code>, errorMessage:<errorMessage>}
		 */
		login: function(email, password , callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.loginUser(email, password, callbackSuccess, callbackError)
		},
	
		/*
		 * Name: logoutUser
		 * Logout the user from the system. Clear all the logged in user information locally
		 * Does not contain any return value 
		 */
		logout: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.logoutUser(callbackSuccess, callbackError)
		},
	
		/*
		 * Name: createUser
		 * Creates a new user. This API does not trigger the email validation.
		 * Server assumes whatever email is provided does exists. This API will be depreicated
		 * Once we have the email validation infrastructure in place
		 * Params
		 *  - Email
		 *  - password
		 *  - UserName
		 *  Returns the User JSON object if successful
		 * In case of error callbackError gets called
		 */
		createUser: function(email, password, name, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.createUser(email, password, name, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: createUser2
		 * Creates a new user. This API will trigger the email validation.
		 * Params
		 *  - Email
		 *  - password
		 *  - UserName
		 *  Returns the User JSON object if successful
		 * In case of error callbackError gets called
		 */
		
		createUser2: function(email, password, name, alternateEmail, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.createUser2(email, password, name, alternateEmail, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: resendValidationMail
		 * Resend the validation email to the user email id

		 * Params
		 *  - Email
		 *  - password
		 *  - UserName
		 *  Returns the User JSON object if successful
		 * In case of error callbackError gets called
		 */
		resendValidationMail: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.resendValidationMail(email, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: isUserValidated
		 * checks if a user is validated or not
		 * returns JSON in success callback
		 * {"validation_status":<validation_status>, message:<message>}
		 * validation_status is will be one of the following value
		 * USER_STATUS_VALIDATED, USER_STATUS_NOT_VALIDATED_IN_GRACE_PERIOD or USER_STATUS_NOT_VALIDATED
		 * If validation_status is USER_STATUS_NOT_VALIDATED, we should not allow user to use the application
		 * 
		 */
		
		isUserValidated: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.isUserValidated(email, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: isUserLoggedIn
		 * checks if a user is already logged in
		 * returns true if user is logged in, false otherwise
		 * Error callback is not called.
		 */

		isUserLoggedIn: function(email, callbackSuccess, callbackError) {	
			window.plugins.neatoPluginLayer.userMgr.isUserLoggedIn(email, callbackSuccess, callbackError);
		},
		
		
		/*
		 * Name: associateRobotCommand
		 * Associates the robot with the user. 
		 * Returns none
		 * Params
		 *  - email
		 *  - robotId (which needs to be associated)
		 */
		
		associateRobot: function(email, robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.associateRobotCommand(email, robotId, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: getUserDetail
		 * Returns the User JSON object
		 * Params
		 *  - email
		 * Gets the user details information from server
		 * 
		 */

		getUserDetail: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.getUserDetail(email, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: getAssociatedRobots
		 * Get the associated robots list in JSON 
		 * Returns a JSONArray of associated robots.
		 * [{robotId:<robotId>,  robot_name: <robot_name>},{ robotId: <robotId>,  robot_name:<robot_name>}]
		 * Params
		 *  - email
		 */
		getAssociatedRobots: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.getAssociatedRobots(email, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: disassociateRobot
		 * disassociate the robot from the specified user 
		 * Returns None
		 * Params
		 *  - email
		 *  - robotId
		 */

		disassociateRobot: function(email, robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.disassociateRobot(email, robotId, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: disassociateAllRobots
		 * disassociates all the robots from the specified user 
		 * Returns None
		 * Params
		 *  - email
		 */
		disassociateAllRobots: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.disassociateAllRobots(email, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: changePassword
		 * Change the password of the user
		 * Params
		 *  - email
		 *  - user's current password
		 *  - new password
		 * returns None in case of success
		 * In error case error callback gets called with the Error JSON object
		 * 
		 */
		changePassword: function(email, currentPassword, newPassword, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.changePassword(email, currentPassword, newPassword, callbackSuccess, callbackError);
		}, 
		

		/*
		 * Name: forgetPassword
		 * Server sends the email to the user email id
		 * Params
		 *  - email
		 * returns None in case of success
		 * In error case error callback gets called with the Error JSON object
		 * 
		 */

		forgetPassword: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.forgetPassword(email, callbackSuccess, callbackError);
		}
	}
}());


var RobotPluginManager = (function() {
	return {
		
		/*
		 * Name: discoverNearbyRobots
		 * starts discovering nearby robots that are on the same subnet.
		 * Params: None
		 * Returns a JSONArray of discovered robots
		 * [{robotId:<robotId>,  robot_name: <robot_name>},{ robotId: <robotId>,  robot_name:<robot_name>}]
		 */
		
		discoverNearbyRobots: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.discoverNearbyRobots(callbackSuccess, callbackError);
		},
		
		/*
		 * Name: tryDirectConnection (Deprecated because it uses the old port number to connect). Use tryDirectConnection2
		 * tries to establish a direct peer-to-peer connection with the robot. 
		 * The robot and the smart app need to be on the same subnet for this connection to work.
		 * Params: Robot id
		 * Returns an error if connection cannot be established
		 */
		
		tryDirectConnection: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.tryDirectConnection(robotId, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: tryDirectConnection2
		 * tries to establish a direct peer-to-peer connection with the robot. 
		 * The robot and the smart app need to be on the same subnet for this connection to work.
		 * Params: Robot id
		 * Returns an error if connection cannot be established
		 */
		
		tryDirectConnection2: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.tryDirectConnection2(robotId, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: disconnectDirectConnection
		 * tears down the existing direct connection (established via tryDirectConnection2/tryDirectConnection)
		 * Params: Robot id
		 */
		
		disconnectDirectConnection: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.disconnectDirectConnection(robotId, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: sendCommandToRobot (Deprecated because it uses the old command format). Use sendCommandToRobot2
		 * sends a command to a specific robot
		 * The robot and the smart app need to be on the same subnet for this connection to work.
		 * Params: 
		 *  Robot id
		 *  Command Id – currently only 2 commands are supported: Start Cleaning (101) and Stop Cleaning (102). 
		 *         Over time more commands (like Send To Base) will be added. 
		 *         Currently always sends a command via the presence server (XMPP) – it does not use the direct connection, 
		 *         but it may do so at a later stage.
		 * Returns: None currently, but we may need to return the response from the command.
		 */
		
		sendCommandToRobot: function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.sendCommandToRobot(robotId, commandId, commandParams, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: sendCommandToRobot2
		 * sends a command to a specific robot
		 * The robot and the smart app need to be on the same subnet for this connection to work.
		 * Params: 
		 *  Robot id
		 *  Command Id – currently only 2 commands are supported: Start Cleaning (101) and Stop Cleaning (102). 
		 *         Over time more commands (like Send To Base) will be added. 
		 *         NOTE: Though you can send "Start Cleaning" and "Stop Cleaning" commands using sendCommandToRobot2 but
		 *         we have also exposed separate APIs. See "startCleaning", "stopCleaning", "pauseCleaning" and "resumeCleaning"
		 *         Use specific API rather than calling "sendCommandToRobot2"
		 *         Currently always sends a command via the presence server (XMPP) – it does not use the direct connection, 
		 *         but it may do so at a later stage.
		 * Returns: None currently, but we may need to return the response from the command.
		 */
		
		sendCommandToRobot2: function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.sendCommandToRobot2(robotId, commandId, commandParams, callbackSuccess, callbackError);
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
		 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
		 */
		startCleaning: function(robotId, cleaningCategoryId, cleaningModeId, cleaningModifier,
					callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.startCleaning(robotId, cleaningCategoryId, cleaningModeId, cleaningModifier,
					callbackSuccess, callbackError);
		},
		
		/*
		 * Name: stopCleaning
		 * Sends "stop cleaning" command to the robot. 
		 * Params
		 *  - robotId: Value as string. (Must be a valid robotId)
		 * In case of success, callbackSuccess is called with response as OK.
		 * In case of an error, callbackError is called with error JSON below:
		 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
		 */
		stopCleaning: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.stopCleaning(robotId, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: pauseCleaning
		 * Sends "pause cleaning" command to the robot. This internally calls 
		 * sendCommandToRobot2 method of "window.plugins.neatoPluginLayer.robotMgr"
		 * Params
		 *  - robotId: Value as string. (Must be a valid robotId)
		 * In case of success, callbackSuccess is called with response as OK.
		 * In case of an error, callbackError is called with error JSON below:
		 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
		 */
		pauseCleaning: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.pauseCleaning(robotId, callbackSuccess, callbackError);
		},

		/*
		 * Name: resumeCleaning
		 * Sends "resume cleaning" command to the robot. This internally calls 
		 * sendCommandToRobot2 method of "window.plugins.neatoPluginLayer.robotMgr" 
		 * Params
		 *  - robotId: Value as string. (Must be a valid robotId)
		 * In case of success, callbackSuccess is called with response as OK.
		 * In case of an error, callbackError is called with error JSON below:
		 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
		 */	
		resumeCleaning: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.resumeCleaning(robotId, callbackSuccess, callbackError);
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
		 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
		*/	
		setSpotDefinition: function(robotId, spotCleaningAreaLength, spotCleaningAreaHeight, 
					callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setSpotDefinition(robotId, spotCleaningAreaLength, 
					spotCleaningAreaHeight, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: getSpotDefinition
		 * Saves the spot definition to the DB. This internally calls getSpotDefinition
		 * method of "window.plugins.neatoPluginLayer.robotMgr" 
		 * Params
		 *  - robotId: Value as string. (Must be a valid robotId)
		 * In case of success, callbackSuccess is called with following JSON:
		 * 	{'spotCleaningAreaLength':<area length>, 'spotCleaningAreaHeight':<area height>}
		 * In case of an error, callbackError is called with error JSON below:
		 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
		 */
		getSpotDefinition: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getSpotDefinition(robotId, callbackSuccess, callbackError);
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
		 * 	{'errorCode':<error code>, 'errorMessage':<error msg>}
		 */
		driveRobot: function(robotId, navigationControlId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.driveRobot(robotId, navigationControlId, 
					callbackSuccess, callbackError);
		},

		/*
		 * Name: setRobotName (Deprecated use setRobotName2). setRobotName2 returns the updated Robot infomration
		 * Set the Robot Name
		 * Params: 
		 *  Robot id
		 *  Robot Name 
		 *  Returns None
		 */
		setRobotName : function(robotId, robotName, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setRobotName(robotId, robotName, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: setRobotName2 
		 * Set the Robot Name
		 * Params: 
		 *  Robot id
		 *  Robot Name 
		 *  Returns: Robot JSON object
		 *  {robotId:<robotId>,  robot_name: <robot_name>}
		 */
		setRobotName2 : function(robotId, robotName, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setRobotName2(robotId, robotName, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: getRobotDetail 
		 * Get the robot details. As of now robot detail consists only robot name and robot id
		 * Params: 
		 *  Robot id
		 *  Returns: Robot JSON object
		 *  {robotId:<robotId>,  robot_name: <robot_name>}
		 */
		
		getRobotDetail : function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getRobotDetail(robotId, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: getRobotOnlineStatus 
		 * Set the Robot Name
		 * Params: 
		 *  Robot id
		 *  Returns: Robot JSON object
		 *  {robotId:<robotId>,  online: true/false}
		 */
		
		getRobotOnlineStatus : function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getRobotOnlineStatus(robotId, callbackSuccess, callbackError);
		},
		
		setSchedule: function(robotId, scheduleType, jsonArray, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setSchedule(robotId, scheduleType, jsonArray, callbackSuccess, callbackError);
		},
		
		getSchedule: function(robotId, scheduleType, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getSchedule(robotId, scheduleType,  callbackSuccess, callbackError);
		},
		
		deleteSchedule: function(robotId, scheduleType, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.deleteSchedule(robotId, scheduleType, callbackSuccess, callbackError);
		},
		
		getMaps: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getMaps(robotId, callbackSuccess, callbackError);
		}, 
		
		setMapOverlayData : function(robotId, mapId, mapOverlayInfo, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setMapOverlayData (robotId, mapId, mapOverlayInfo, callbackSuccess, callbackError);
		},
		
		// It will give the atlas xml data.
		getRobotAtlasMetadata: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getRobotAtlasMetadata(robotId, callbackSuccess, callbackError);
		},
		
				
		// It will update the atlas mapped to this robotId. The version of the xml is stored inside.
		updateAtlasMetaData: function(robotId, atlasMetadata, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.updateAtlasMetaData(robotId, atlasMetadata, callbackSuccess, callbackError);
		},
		
		// TODO: We are taking robotId. Analyse if taking atlasId is a better option.
		getAtlasGridData: function(robotId, gridId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getAtlasGridData(robotId, gridId, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: registerNotifications
		 * Register for the notifications from the Robot. As of now this API
		 * register for all type of notifications. 
		 * Params
		 *  - robotId
		 *  Returns: None
		 */
		registerNotifications: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.registerNotifications(robotId, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: unregisterNotifications
		 * Unregister for the notifications from the Robot. 
		 * Params
		 *  - robotId
		 *  Returns: None
		 */
		unregisterNotifications: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.unregisterNotifications(robotId, callbackSuccess, callbackError);
		},
		// New Schedule APIs being added:
		
		
		/*
		 * Name: createSchedule
		 * This API creates a new local schedule for the robot. Note that if you were already working with a local schedule, 
		 * that data is lost, and this new schedule will be updated in subsequent calls. 
		 * Params
		 *  - robotId
		 *  - scheduleType (SCHEDULE_TYPE_BASIC or SCHEDULE_TYPE_ADVANCED). As of now only SCHEDULE_TYPE_BASIC is supported
		 * Returns: 
		 * {'scheduleId':scheduleId, ‘robotId’: robotId, ‘scheduleType’: scheduleType}

		 */
		createSchedule: function(robotId, scheduleType, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.createSchedule(robotId, scheduleType, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: getScheduleEvents
		 * getScheduleEvents makes a call to the server to get the Schedule Events for the robot. 
		 * Once we fetch the schedule data from server, we cache this data locally and further changes are done on the local copy.
		 * Params
		 *  - robotId
		 *  - scheduleType (SCHEDULE_TYPE_BASIC or SCHEDULE_TYPE_ADVANCED). As of now only SCHEDULE_TYPE_BASIC is supported
		 * Returns: JSON Object of Schedule data
		 * {'scheduleId':scheduleId, ‘robotId’: robotId, ‘scheduleType’: scheduleType, scheduleEventLists:’ scheduleEventLists}
		 * ScheduleEventLists is a JSONArray with all the scheduleEventIds for the scheduleEvents of the schedule
		 */
		getScheduleEvents: function(robotId, scheduleType, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getScheduleEvents(robotId, scheduleType, callbackSuccess, callbackError);
		},

		/*
		 * Name: getScheduleEventData
		 * getScheduleEventData gets the schedule data for the event. This API fetches the data from the local database
		 * Params
		 *  - scheduleId
		 *  - scheduleEventId
		 * Returns: JSON Object of Schedule data
		 * {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId, 'scheduleEventData':scheduleEventData};
		 * scheduleEventData for the basic event is a JSON object {'day':day, 'startTime': startTime, ‘cleaningMode’:cleaningMode}
		 */

		getScheduleEventData: function(scheduleId, scheduleEventId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getScheduleEventData(scheduleId, scheduleEventId, callbackSuccess, callbackError);
		},
		
		
		/*
		 * Name: addScheduleEvent
		 * addScheduleEvent adds a schedule event in the local copy of the robot schedule. 
		 * No changes are made to the server data until updateSchedule is called
		 * Params
		 *  - scheduleId
		 *  - scheduleEventData (returns from createBasicScheduleEventObject helper API)
		 * Returns: {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId}
		 * It returns the scheduleEventId of the added scheduleEvent for further tracking.
		 */
		addScheduleEvent: function(scheduleId, scheduleEventData, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.addScheduleEvent(scheduleId, scheduleEventData, callbackSuccess, callbackError)
		},

		/*
		 * Name: updateScheduleEvent
		 * updateScheduleEvent API updates a schedule event in the local copy of the robot schedule. 
		 * No changes are made to the server data until updateSchedule is called
		 * Params
		 *  - scheduleId
		 *  - scheduleEventId - Schedule Event id
		 *  - scheduleEventData (returns from createBasicScheduleEventObject helper API)
		 * Returns: JSON object of the updated schedule Event
		 * {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId}
		 */
		updateScheduleEvent: function(scheduleId, scheduleEventId, scheduleEventData, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.updateScheduleEvent(scheduleId, scheduleEventId, scheduleEventData, callbackSuccess, callbackError);
		},

		/*
		 * Name: deleteScheduleEvent
		 * deleteScheduleEvent API deletes a schedule event from the local copy of the robot schedule. 
		 * No changes are made to the server data until updateSchedule is called
		 * Params
		 *  - scheduleId
		 *  - scheduleEventId - Schedule Event id
		 * Returns a JSON: {'scheduleId':scheduleId, 'scheduleEventId':scheduleEventId} of the deleted schedule event
		 */
		
		deleteScheduleEvent: function(scheduleId, scheduleEventId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.deleteScheduleEvent(scheduleId, scheduleEventId, callbackSuccess, callbackError);
		},

		
		/*
		 * Name: updateSchedule
		 * updateSchedule API updates the server data with the local copy of the schedule.
		 * Params
		 *  - scheduleId
		 * Returns a JSON: {“scheduleId” : scheduleId} with the scheduleId of the schedule updated.
		 */
		updateSchedule: function(scheduleId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.updateSchedule(scheduleId, callbackSuccess, callbackError);
		},
		
		/*
		 * Name: getScheduleData
		 * getScheduleData is just a helper API which returns the entire event items in a single call.
		 * Params
		 *  - scheduleId
		 * Returns: a JSON Object;
		 * : {'scheduleId':scheduleId, 'scheduleType': 'scheduleType', schedules:<schedulesArray> }
		 * schedulesArray Object will be a JSONArray with the data of each scheduleEvents in schedule.
		 * Example:
		 *	[{day:1, startTime:12:00, 'cleaningMode':1} , {day:3, startTime:5:00, 'cleaningMode':1}]
		 */
		getScheduleData: function(scheduleId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getScheduleData(scheduleId, callbackSuccess, callbackError);
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
		}	
}());


