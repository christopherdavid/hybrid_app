
var NOTIFICATION_DISCOVERY_STARTED = 1;
var NOTIFICATION_DISCOVERY_RESULT = 2;


var KEY_MAP_TYPE_XML = 1;
var KEY_MAP_TYPE_BLOB = 2;

var SCHEDULAR_EVENT_TYPE_QUIET = 0;
var SCHEDULAR_EVENT_TYPE_CLEAN = 1;

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
var ACTION_TYPE_GET_USER_DETAILS 				= "getUserDetails";
var ACTION_TYPE_ASSOCIATE_ROBOT					= "associateRobot";
var ACTION_TYPE_GET_ASSOCIATED_ROBOTS 			= "getAssociatedRobots"
var ACTION_TYPE_DISASSOCIATE_ROBOT 				= "disassociateRobot";
var ACTION_TYPE_DISASSOCAITE_ALL_ROBOTS 		= "disassociateAllRobots";
// Debug method
var ACTION_TYPE_DEBUG_GET_CONFIG_DETAILS 		= "debugGetConfigDetails";

// List of actions types of Robot Manager
var ACTION_TYPE_DISCOVER_NEARBY_ROBOTS 			= "discoverNearByRobots";
var ACTION_TYPE_TRY_CONNECT_CONNECTION 			= "tryDirectConnection";
var ACTION_TYPE_SEND_COMMAND_TO_ROBOT 			= "sendCommandToRobot";
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

//List of keys to send data:

var KEY_EMAIL = 'email';
var KEY_PASSWORD = 'password';
var KEY_USER_NAME = 'username';

//Used by robot plugin
var KEY_COMMAND = 'command';
var KEY_ROBOT_ID = 'robotId';
var KEY_USE_XMPP = 'useXMPP';
var KEY_ROBOT_NAME = "robotName";
var KEY_ROBOT_IP_ADDRESS = "robotIpaddress";

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
	var loginArray = {'email':email, 'password':password};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
                 ACTION_TYPE_LOGIN,[loginArray]);
};

UserMgr.prototype.logoutUser = function(callbackSuccess, callbackError) {
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_LOGOUT, []);
};

UserMgr.prototype.isUserLoggedIn = function(email, callbackSuccess, callbackError) {
	var isUserLoggedInArray = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_ISLOGGEDIN, [isUserLoggedInArray]);
};

UserMgr.prototype.createUser = function(email, password, name, callbackSuccess, callbackError) {
	var registerArray = {'email':email, 'password':password, 'userName':name};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_CREATE_USER, [registerArray]);
};

UserMgr.prototype.getUserDetail = function(email, callbackSuccess, callbackError) {
	var getUserDetailsArray = {'email': email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_USER_DETAILS, [getUserDetailsArray]);
};

UserMgr.prototype.associateRobotCommand = function(email, robotId, callbackSuccess, callbackError) {
	var associateArray = {'email':email, 'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_ASSOCIATE_ROBOT, [associateArray]);
};

UserMgr.prototype.getAssociatedRobots = function(email, callbackSuccess, callbackError) {
	var getAssociatedRobotsArray = {'email':email};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_GET_ASSOCIATED_ROBOTS, [getAssociatedRobotsArray]);
};

UserMgr.prototype.disassociateRobot = function(email, robotId, callbackSuccess, callbackError) {
	var disassociateRobotArray = {email:'email', 'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DISASSOCIATE_ROBOT, [disassociateRobotArray]);
};

UserMgr.prototype.disassociateAllRobots = function(email, callbackSuccess, callbackError) {
	var disassociateAllRobotsArray = {email:'email'};
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DISASSOCAITE_ALL_ROBOTS, [disassociateAllRobotsArray]);
};

// Debug method.
UserMgr.prototype.debugGetConfigurationDetails = function(callbackSuccess, callbackError) {
	cordova.exec(callbackSuccess, callbackError, USER_MANAGEMENT_PLUGIN,
                 ACTION_TYPE_DEBUG_GET_CONFIG_DETAILS, []);
};

// ***********************ROBOT PLUGIN METHODS ****************************

RobotMgr.prototype.discoverNearbyRobots = function(callbackSuccess, callbackError) {
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DISCOVER_NEARBY_ROBOTS, []);
};

RobotMgr.prototype.tryDirectConnection = function(robotId, callbackSuccess, callbackError) {
	var connectPeerCommandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_TRY_CONNECT_CONNECTION, [connectPeerCommandArray]);
};

RobotMgr.prototype.disconnectDirectConnection  = function(robotId, callbackSuccess, callbackError) {
	var disconnectPeerCommandArray = {'robotId':robotId};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_DISCONNECT_DIRECT_CONNETION, [disconnectPeerCommandArray]);
};


RobotMgr.prototype.sendCommandToRobot = function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
	var commandArray = {'robotId':robotId, 'commandId':commandId, 'commandParams':commandParams};
	
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SEND_COMMAND_TO_ROBOT, [commandArray]);
};

RobotMgr.prototype.setRobotName = function(robotId, robotName, callbackSuccess, callbackError) {
	var setRobotName = {'robotId':robotId, 'robotName':robotName};
	cordova.exec(callbackSuccess, callbackError, ROBOT_MANAGEMENT_PLUGIN,
			ACTION_TYPE_SET_ROBOT_NAME, [setRobotName]);
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

RobotMgr.prototype.deleteSchedule = function(robotId, callbackSuccess, callbackError) {
	var scheduleArray = {'robotId':robotId};
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

var UserPluginManager = (function() {
	return {
		
		login: function(email, password , callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.loginUser(email, password, callbackSuccess, callbackError)
		},
	
		logout: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.logoutUser(callbackSuccess, callbackError)
		},
	
		createUser: function(email, password, name, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.createUser(email, password, name, callbackSuccess, callbackError);
		},
		
		isUserLoggedIn: function(email, callbackSuccess, callbackError) {	
			window.plugins.neatoPluginLayer.userMgr.isUserLoggedIn(email, callbackSuccess, callbackError);
		},
		
		associateRobot: function(email, robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.associateRobotCommand(email, robotId, callbackSuccess, callbackError);
		},
		
		getUserDetail: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.getUserDetail(email, callbackSuccess, callbackError);
		},
		
		getAssociatedRobots: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.getAssociatedRobots(email, callbackSuccess, callbackError);
		},
		
		disassociateRobot: function(email, robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.disassociateRobot(email, robotId, callbackSuccess, callbackError);
		},
		
		disassociateAllRobots: function(email, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.userMgr.disassociateAllRobots(email, callbackSuccess, callbackError);
		},
        // Debug method. May not exposed in the finished product
        debugGetConfigurationDetails: function(callbackSuccess, callbackError) {
            window.plugins.neatoPluginLayer.userMgr.debugGetConfigurationDetails(callbackSuccess, callbackError);
        }
	}
}());


var RobotPluginManager = (function() {
	return {
		discoverNearbyRobots: function(callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.discoverNearbyRobots(callbackSuccess, callbackError);
		},
		tryDirectConnection: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.tryDirectConnection(robotId, callbackSuccess, callbackError);
		},
		disconnectDirectConnection: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.disconnectDirectConnection(robotId, callbackSuccess, callbackError);
		},
		
		sendCommandToRobot: function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.sendCommandToRobot(robotId, commandId, commandParams, callbackSuccess, callbackError);
		},
		
		setRobotName : function(robotId, robotName, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setRobotName(robotId, robotName, callbackSuccess, callbackError);
		},
		
		setSchedule: function(robotId, scheduleType, jsonArray, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.setSchedule(robotId, scheduleType, jsonArray, callbackSuccess, callbackError);
		},
		
		getSchedule: function(robotId, scheduleType, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.getSchedule(robotId, scheduleType,  callbackSuccess, callbackError);
		},
		
		deleteSchedule: function(robotId, callbackSuccess, callbackError) {
			window.plugins.neatoPluginLayer.robotMgr.deleteSchedule(robotId, callbackSuccess, callbackError);
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
	}
}());

