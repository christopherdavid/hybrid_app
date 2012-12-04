//List of plugins.
var USER_MANAGEMENT_PLUGIN = "UserManagement";
var ROBOT_MANAGEMENT_PLUGIN = "RobotManagement";

//List of action types
var ACTIONTYPE_LOGIN = "login";
var ACTIONTYPE_LOGOUT = "logout";
var ACTIONTYPE_REGISTER = "register";
var ACTIONTYPE_DISCOVER = "discover";
var ACTIONTYPE_ASSOCIATE = "associate";
var ACTIONTYPE_START_CLEANING_COMMAND = "startCleaning";
var ACTIONTYPE_STOP_CLEANLING_COMMAND = "stopCleaning";

//List of keys to send data:

var KEY_EMAIL = 'email';
var KEY_PASSWORD = 'password';
var KEY_USER_NAME = 'username';

//Used by robot plugin
var KEY_COMMAND = 'command';
var KEY_ROBOT_SERIAL_ID = 'serialid';
var KEY_USE_XMPP = 'useXMPP';


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


UserMgr.prototype.registerUser = function(name , email, password , callbackSuccess, callbackError) {
	var registerArray = {'username': name, 'email': email, 'password': password};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTIONTYPE_REGISTER, [registerArray]);
};

RobotMgr.prototype.discoverRobot = function(callbackSuccess, callbackError) {

	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTIONTYPE_DISCOVER, []);
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

		discoverRobot: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.discoverRobot(callbackSuccess, callbackError);
		},

		associateRobot: function(serialId , callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.associateRobotCommand(serialId, callbackSuccess, callbackError);
		},

		startRobot: function(serialId, useXmppServer, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.startRobotCommand(serialId, useXmppServer, callbackSuccess, callbackError);
		},

		stopRobot: function(serialId, useXmppServer, callbackSuccess, callbackError) {	
			window.plugins.neatoPluginLayer.robotMgr.stopRobotCommand(serialId, useXmppServer, callbackSuccess, callbackError);
		}
	}
}());

