/*
 * This file copies the UserPluginManager, RobotPluginManager for the desktop version of the application.
 * It is used for demo purposes only and WON'T be used when the application is running on a device.
 *
 */

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

var PLUGIN_JSON_KEYS = (function() {
    var keys = {
        'DISCOVERY_NOTIFICATION_KEY' : 'notificationType',
    };

    return {
        get : function(name) {
            return keys[name];
        }
    };
})();

//List of plugins.
var USER_MANAGEMENT_PLUGIN = "UserManagement";
var ROBOT_MANAGEMENT_PLUGIN = "RobotManagement";

//List of action types of USER manager
var ACTION_TYPE_LOGIN = "login";
var ACTION_TYPE_LOGOUT = "logout";
var ACTION_TYPE_ISLOGGEDIN = "isLoggedIn";
var ACTION_TYPE_CREATE_USER = "createUser";
var ACTION_TYPE_GET_USER_DETAILS = "getUserDetails";
var ACTION_TYPE_ASSOCIATE_ROBOT = "associateRobot";
var ACTION_TYPE_GET_ASSOCIATED_ROBOTS = "getAssociatedRobots"
var ACTION_TYPE_DISASSOCIATE_ROBOT = "disassociateRobot";
var ACTION_TYPE_DISASSOCAITE_ALL_ROBOTS = "disassociateAllRobots";

// List of actions types of Robot Manager
var ACTION_TYPE_DISCOVER_NEARBY_ROBOTS = "discoverNearByRobots";
var ACTION_TYPE_TRY_CONNECT_CONNECTION = "tryDirectConnection";
var ACTION_TYPE_SEND_COMMAND_TO_ROBOT = "sendCommandToRobot";
var ACIION_TYPE_SET_SCHEDULE = "robotSetSchedule";
var ACIION_TYPE_GET_ROBOT_SCHEDULE = "getSchedule";
var ACTION_TYPE_GET_ROBOT_MAP = "getRobotMap";
var ACTION_TYPE_SET_MAP_OVERLAY_DATA = "setMapOverlayData";
var ACTION_TYPE_DISCONNECT_DIRECT_CONNETION = "disconnectDirectConnection";
var ACTION_TYPE_GET_ROBOT_ATLAS_METADATA = "getRobotAtlasMetadata";
var ACTION_TYPE_UPDATE_ROBOT_ATLAS_METADATA = "updateRobotAtlasMetadata";
var ACTION_TYPE_GET_ATLAS_GRID_DATA = "getAtlasGridData";
var ACTION_TYPE_SET_ROBOT_NAME = "setRobotName";
var ACTION_TYPE_SET_ROBOT_NAME_2                = "setRobotName2";
var ACTION_TYPE_GET_ROBOT_DETAIL                = "getRobotDetail";
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

var UserPluginManager = ( function() {
        return {

            login : function(email, password, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.loginUser(email, password, callbackSuccess, callbackError)
                window.setTimeout(function() {
                    callbackSuccess({
                        "email" : "demo1@demo.com",
                        "username" : "demo",
                        "userId" : "48"
                    });
                }, 1000);
                /*
                 callbackError({
                 "errorMessage":"Server Error",
                 "errorCode":1003
                 });
                 */
            },

            logout : function(callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.logoutUser(callbackSuccess, callbackError)
                window.setTimeout(function() {
                    callbackSuccess("OK");
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
                }, 1000);
                //callbackSuccess(false);
            },
            associateRobot : function(email, robotId, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess("OK");
                }, 1000);
                //window.plugins.neatoPluginLayer.userMgr.associateRobotCommand(email, robotId, callbackSuccess, callbackError);

            },

            getUserDetail : function(email, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.getUserDetail(email, callbackSuccess, callbackError);
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
                window.setTimeout(function() {
                    callbackSuccess([]);
                }, 5000);
            },

            disassociateRobot : function(email, robotId, callbackSuccess, callbackError) {
                //window.plugins.neatoPluginLayer.userMgr.disassociateRobot(email, robotId, callbackSuccess, callbackError);
            	window.setTimeout(function() {
                    callbackSuccess("OK");
                }, 1000);
            },

            disassociateAllRobots : function(email, callbackSuccess, callbackError) {
                window.plugins.neatoPluginLayer.userMgr.disassociateAllRobots(email, callbackSuccess, callbackError);
            }
        }
    }());

var RobotPluginManager = ( function() {
	
		var schedulerEvents = [
		                       {
		                    	      "eventType":"cleaning",
		                    	      "day":4,
		                    	      "startTime":"01:30",
		                    	      "state":"local",
		                    	      "id":"2a6ba65e-45wq-4a31-b727-0e846e5c9845",
		                    	      "rooms":[
		                    	         {
		                    	            "id":"1",
		                    	            "coord":[
		                    	               {
		                    	                  "y":160,
		                    	                  "x":360
		                    	               },
		                    	               {
		                    	                  "y":160,
		                    	                  "x":525
		                    	               },
		                    	               {
		                    	                  "y":360,
		                    	                  "x":525
		                    	               },
		                    	               {
		                    	                  "y":360,
		                    	                  "x":375
		                    	               },
		                    	               {
		                    	                  "y":240,
		                    	                  "x":375
		                    	               },
		                    	               {
		                    	                  "y":240,
		                    	                  "x":360
		                    	               }
		                    	            ],
		                    	            "icon":0,
		                    	            "boundingBox":[
		                    	               360,
		                    	               160,
		                    	               525,
		                    	               360
		                    	            ],
		                    	            "color":1,
		                    	            "name":"Room2"
		                    	         }
		                    	      ]
		                    	   },
		                    	   {
		                    	      "eventType":"cleaning",
		                    	      "day":2,
		                    	      "startTime":"01:30",
		                    	      "state":"robot",
		                    	      "id":"2a6ba65e-aseb-4a31-b727-0e846e5c5012",
		                    	      "rooms":[
		                    	         {
		                    	            "id":"1",
		                    	            "coord":[
		                    	               {
		                    	                  "y":160,
		                    	                  "x":360
		                    	               },
		                    	               {
		                    	                  "y":160,
		                    	                  "x":525
		                    	               },
		                    	               {
		                    	                  "y":360,
		                    	                  "x":525
		                    	               },
		                    	               {
		                    	                  "y":360,
		                    	                  "x":375
		                    	               },
		                    	               {
		                    	                  "y":240,
		                    	                  "x":375
		                    	               },
		                    	               {
		                    	                  "y":240,
		                    	                  "x":360
		                    	               }
		                    	            ],
		                    	            "icon":0,
		                    	            "boundingBox":[
		                    	               360,
		                    	               160,
		                    	               525,
		                    	               360
		                    	            ],
		                    	            "color":1,
		                    	            "name":"Room2"
		                    	         },
		                    	         {
		                    	            "id":"0",
		                    	            "coord":[
		                    	               {
		                    	                  "y":0,
		                    	                  "x":0
		                    	               },
		                    	               {
		                    	                  "y":0,
		                    	                  "x":360
		                    	               },
		                    	               {
		                    	                  "y":240,
		                    	                  "x":360
		                    	               },
		                    	               {
		                    	                  "y":240,
		                    	                  "x":0
		                    	               }
		                    	            ],
		                    	            "icon":0,
		                    	            "boundingBox":[
		                    	               0,
		                    	               0,
		                    	               360,
		                    	               240
		                    	            ],
		                    	            "color":1,
		                    	            "name":"Room1"
		                    	         }
		                    	      ]
		                    	   },
		                    	   {
		                    	      "eventType":"quiet",
		                    	      "day":3,
		                    	      "startTime":"02:30",
		                    	      "endTime":"03:45",
		                    	      "id":"3333255e-a00f-422c-834d-06f1cba591cb"
		                    	   },
		                    	   {
		                    	      "eventType":"quiet",
		                    	      "day":2,
		                    	      "startTime":"02:30",
		                    	      "endTime":"03:45",
		                    	      "id":"3333255e-a00f-422c-834d-06f1cba591cb"
		                    	   },
		                    	   {
		                    	      "eventType":"quiet",
		                    	      "day":1,
		                    	      "startTime":"02:30",
		                    	      "endTime":"03:45",
		                    	      "id":"3333255e-a00f-422c-834d-06f1cba591cb"
		                    	   },
		                    	   {
		                    	      "eventType":"cleaning",
		                    	      "day":3,
		                    	      "startTime":"03:45",
		                    	      "state":"server",
		                    	      "id":"2a6ba65e-30eb-4a31-b727-0e846e5c5082",
		                    	      "rooms":[
		                    	         {
		                    	            "id":"0",
		                    	            "coord":[
		                    	               {
		                    	                  "y":0,
		                    	                  "x":0
		                    	               },
		                    	               {
		                    	                  "y":0,
		                    	                  "x":360
		                    	               },
		                    	               {
		                    	                  "y":240,
		                    	                  "x":360
		                    	               },
		                    	               {
		                    	                  "y":240,
		                    	                  "x":0
		                    	               }
		                    	            ],
		                    	            "icon":0,
		                    	            "boundingBox":[
		                    	               0,
		                    	               0,
		                    	               360,
		                    	               240
		                    	            ],
		                    	            "color":1,
		                    	            "name":"Room1"
		                    	         }
		                    	      ]
		                    	   },
		                    	   {
		                    	      "eventType":"quiet",
		                    	      "day":4,
		                    	      "startTime":"03:30",
		                    	      "endTime":"07:30",
		                    	      "id":"e89fc620-acde-4144-9dc6-4f09fbad2552"
		                    	   },
		                    	   {
		                    	      "eventType":"quiet",
		                    	      "day":6,
		                    	      "startTime":"00:00",
		                    	      "endTime":"05:15",
		                    	      "id":"5e7c00c3-2341-4f06-a446-5706e32f3336"
		                    	   }
		                    	];
	
	
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

            sendCommandToRobot : function(robotId, commandId, commandParams, callbackSuccess, callbackError) {
                window.setTimeout(function() {
                    callbackSuccess("OK");
                }, 1000);
                //window.plugins.neatoPluginLayer.robotMgr.sendCommandToRobot(robotId, commandId, commandParams, callbackSuccess, callbackError);
            },

            setRobotName : function(robotId, robotName, callbackSuccess, callbackError) {
              	 window.setTimeout(function() {
                     callbackSuccess("OK");
                 }, 1000);
                //window.plugins.neatoPluginLayer.robotMgr.setRobotName(robotId, robotName, callbackSuccess, callbackError);
            },
            
            setRobotName2 : function(robotId, robotName, callbackSuccess, callbackError) {
            window.plugins.neatoPluginLayer.robotMgr.setRobotName2(robotId, robotName, callbackSuccess, callbackError);
            },
            
            getRobotDetail : function(robotId, callbackSuccess, callbackError) {
                window.plugins.neatoPluginLayer.robotMgr.getRobotDetail(robotId, callbackSuccess, callbackError);
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
                }, 1000);
            }
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
        }
    }());

