
var NOTIFICATION_DISCOVERY_STARTED = 1;
var NOTIFICATION_DISCOVERY_RESULT = 2;

var SCHEDULAR_EVENT_TYPE_QUIET = 0;
var SCHEDULAR_EVENT_TYPE_CLEAN = 1;

var DAY_SUNDAY = 0;
var DAY_MONDAY = 1;
var DAY_TUEDAY = 2;
var DAY_WEDNESDAY = 3;
var DAY_THURSDAY = 4;
var DAY_FRIDAY = 5;
var DAY_SATURDAY = 6;


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

//List of action types
var ACTIONTYPE_LOGIN = "login";
var ACTIONTYPE_LOGOUT = "logout";
var ACTIONTYPE_REGISTER = "register";
var ACTIONTYPE_DISCOVER = "discover";
var ACTIONTYPE_ISLOGGEDIN = "isloggedin";
var ACTIONTYPE_CONNECTPEER = "connectPeer";
var ACTIONTYPE_ASSOCIATE = "associate";
var ACTIONTYPE_START_CLEANING_COMMAND = "startCleaning";
var ACTIONTYPE_STOP_CLEANLING_COMMAND = "stopCleaning";
var ACIIONTYPE_ADVANCED_SCHEDULING = "robotAdvancedSchedule";
var ACIIONTYPE_SEND_BASE = "sendBase";
//List of keys to send data:

var KEY_EMAIL = 'email';
var KEY_PASSWORD = 'password';
var KEY_USER_NAME = 'username';

//Used by robot plugin
var KEY_COMMAND = 'command';
var KEY_ROBOT_SERIAL_ID = 'serialid';
var KEY_USE_XMPP = 'useXMPP';
var KEY_ROBOT_NAME = "robot_name";
var KEY_ROBOT_IP_ADDRESS = "robot_ipaddress";

//Used in scheduling
var setAdvancedSchedule = {'serialid':serialId, 'day':day, 'startTime': startTime, 
		'endTime': endTime, 'eventType': eventType,
		'area':area};

var KEY_DAY = 'day';
var KEY_START_TIME_HRS = 'startTimeHrs';
var KEY_END_TIME_HRS = 'endTimeHrs';
var KEY_START_TIME_MINS = 'startTimeMins';
var KEY_END_TIME_MINS = 'endTimeMins';
var KEY_EVENT_TYPE = 'eventType';
var KEY_AREA = 'area';



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

UserMgr.prototype.loginUser = function(email, password, callbackSuccess, callbackError) {
	var loginArray = {'email': email, 'password': password};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTIONTYPE_LOGIN, [loginArray]);
};

UserMgr.prototype.logoutUser = function(callbackSuccess, callbackError) {
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTIONTYPE_LOGOUT, []);
};

UserMgr.prototype.isUserLoggedIn = function(callbackSuccess, callbackError) {
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTIONTYPE_ISLOGGEDIN, []);
};

UserMgr.prototype.registerUser = function(name , email, password , callbackSuccess, callbackError) {
	var registerArray = {'username': name, 'email': email, 'password': password};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTIONTYPE_REGISTER, [registerArray]);
};

RobotMgr.prototype.discoverRobot = function(callbackSuccess, callbackError) {

	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTIONTYPE_DISCOVER, []);
};

RobotMgr.prototype.connectPeerRobotCommand = function(ipaddress ,callbackSuccess, callbackError) {
	var connectPeerCommandArray = {'robot_ipaddress':ipaddress};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTIONTYPE_CONNECTPEER, [connectPeerCommandArray]);
};


RobotMgr.prototype.startRobotCommand = function(serialId, useXmppServer,callbackSuccess, callbackError) {
	var startCommandArray = {'serialid':serialId, 'useXMPP': useXmppServer};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTIONTYPE_START_CLEANING_COMMAND, [startCommandArray]);
};

RobotMgr.prototype.stopRobotCommand = function(serialId, useXmppServer, callbackSuccess, callbackError) {
	var stopCommandArray = {'serialid':serialId, 'useXMPP': useXmppServer};

	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTIONTYPE_STOP_CLEANLING_COMMAND, [stopCommandArray]);
};

RobotMgr.prototype.associateRobotCommand = function(serialId, callbackSuccess, callbackError) {
	var associateArray = {'serialid':serialId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTIONTYPE_ASSOCIATE, [associateArray]);
};
var KEY_START_TIME_MINS = 'startTimeMins';
var KEY_END_TIME_MINS = 'endTimeMins';

RobotMgr.prototype.setAdvancedSchedule = function(serialId, day, startTimeHrs, endTimeHrs, startTimeMins, endTimeMins, eventType, area, callbackSuccess, callbackError) {
	var setAdvancedSchedule = {'serialid':serialId, 'day':day, 'startTimeHrs': startTimeHrs, 
								'endTimeHrs': endTimeHrs, 'startTimeMins': startTimeMins, 'endTimeMins':endTimeMins, 'eventType': eventType,
								'area':area};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACIIONTYPE_ADVANCED_SCHEDULING, [setAdvancedSchedule]);
};

RobotMgr.prototype.sendToBase = function(callbackSuccess, callbackError) {
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACIIONTYPE_SEND_BASE, []);
};



var PluginManager =  (function() {
	return {
		loginUser: function(email, password , callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.loginUser(email, password, callbackSuccess, callbackError)
		},
		logoutUser: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.logoutUser(callbackSuccess, callbackError)
		},
		

		registerUser: function(name , email, password , callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.registerUser(name , email, password , callbackSuccess, callbackError);
		},
		
		isUserLoggedIn: function(callbackSuccess, callbackError) {
			
			window.plugins.neatoPluginLayer.userMgr.isUserLoggedIn(callbackSuccess, callbackError);
		},

		discoverRobot: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.discoverRobot(callbackSuccess, callbackError);
		},
		connectPeer: function(ipAddress, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.connectPeerRobotCommand(ipAddress, callbackSuccess, callbackError);
		},
		associateRobot: function(serialId , callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.associateRobotCommand(serialId, callbackSuccess, callbackError);
		},

		startRobot: function(serialId, useXmppServer, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.startRobotCommand(serialId, useXmppServer, callbackSuccess, callbackError);
		},

		stopRobot: function(serialId, useXmppServer, callbackSuccess, callbackError) {	
			window.plugins.neatoPluginLayer.robotMgr.stopRobotCommand(serialId, useXmppServer, callbackSuccess, callbackError);
		},
		
		setAdvancedSchedule: function(serialId, day, startTimeHrs, endTimeHrs, startTimeMins, endTimeMins, eventType, area, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setAdvancedSchedule(serialId, day, startTimeHrs, endTimeHrs, startTimeMins, endTimeMins, eventType, area, callbackSuccess, callbackError);
		},
		
		sendToBase: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.sendToBase(callbackSuccess, callbackError);
		}
		
	}
}());

