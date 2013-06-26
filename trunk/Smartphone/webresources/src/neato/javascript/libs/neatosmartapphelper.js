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

var NOTIFICATIONS_GLOBAL_OPTION = "global";
var NOTIFICATION_ROBOT_STUCK = "101";
var NOTIFICATION_DIRT_BIN_FULL = "102";
var NOTIFICATION_CLEANING_DONE = "103";

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

// Robot state codes
    var ROBOT_STATE_UNKNOWN     = 10001;
    var ROBOT_STATE_CLEANING    = 10002;
    var ROBOT_STATE_IDLE        = 10003;
    var ROBOT_STATE_CHARGING    = 10004;
    var ROBOT_STATE_STOPPED     = 10005;
    var ROBOT_STATE_STUCK       = 10006;
    var ROBOT_STATE_PAUSED      = 10007;
    var ROBOT_STATE_RESUMED     = 10008;
    var ROBOT_STATE_ON_BASE     = 10009;

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
var ACTION_TYPE_REGISTER_FOR_ROBOT_MESSAGES     = "registerForRobotMessges";

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
var ACTION_TYPE_REGISTER_ROBOT_NOTIFICATIONS    = "registerRobotNotifications";
var ACTION_TYPE_UNREGISTER_ROBOT_NOTIFICATIONS  = "unregisterRobotNotifications";

// New schedule APis 
var ACTION_TYPE_UPDATE_SCHEDULE                 = "updateSchedule";
var ACTION_TYPE_DELETE_ROBOT_SCHEDULE_EVENT     = "deleteScheduleEvent";
var ACTION_TYPE_UPDATE_ROBOT_SCHEDULE_EVENT     = "updateScheduleEvent";
var ACTION_TYPE_GET_SCHEDULE_EVENT_DATA         = "getScheduleEventData";
var ACTION_TYPE_ADD_ROBOT_SCHEDULE_EVENT        = "addScheduleEventData";
var ACTION_TYPE_GET_SCHEDULE_EVENTS             = "getScheduleEvents";
var ACTION_TYPE_GET_SCHEDULE_DATA               = "getScheduleData";
var ACTION_TYPE_CREATE_SCHEDULE                 = "createSchedule";

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
var COMMAND_GET_ROBOT_STATE = 105;
var COMMAND_SEND_ROBOT_STATE = 106;
var COMMAND_PAUSE_CLEANING = 107;
var COMMAND_ENABLE_SCHEDULE = 108;
var COMMAND_DATA_CHANGED_ON_SERVER = 109;
var COMMAND_SET_ROBOT_TIME = 110;
var COMMAND_REGISTER_NOTIFICATIONS = 111;
var COMMAND_UNREGISTER_NOTIFICATIONS = 112;


var START_CLEAN_TYPE_HIGH = 1;
var START_CLEAN_TYPE_NORMAL = 2;
var START_CLEAN_TYPE_SPOT = 3;

// Cleaning Mode
var CLEANING_MODE_ECO = 1;
var CLEANING_MODE_NORMAL = 2;


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
                
                 callbackError({
                 "errorMessage":"Server Error",
                 "errorCode":1003
                 });
                
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
                   //callbackError({"status":-1,"message":"Method call failed the User Authentication"});

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
                    window.setTimeout(function() {
                    callbackSuccess({
                        "email" : "homer@uid.com",
                        "username" : "Homer",
                        "userId" : "82"
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
                }, 5000);
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
                window.setTimeout(function() {
                     callbackSuccess("OK");
                 }, 1000);
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
            var stateCode = ROBOT_STATE_ON_BASE;
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
                case "rr1001":
                 	stateCode = ROBOT_STATE_UNKNOWN
                    delay = 10000;
                    break;
                case "rr1002":
                 	stateCode = ROBOT_STATE_STUCK
                    delay = 10000;
                    break;
                case "demo123":
                 	stateCode = ROBOT_STATE_CHARGING
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
                        {   'errorMessage':'No schedule exists for the given robot.',
                            'errorCode': 1003
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
                                    'scheduleEventData': {'startTime':'0:00','day':1, 'cleaningMode':'2'}
                                }
                            );
                        }, 1500);
                    break;
                    case "5d31a4e8-5eca-41b8-87bc-fa10a13c4152":
                        window.setTimeout(function() {
                            callbackSuccess(
                                {   
                                    'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
                                    'scheduleEventId':'5d31a4e8-5eca-41b8-87bc-fa10a13c4152', 
                                    'scheduleEventData': {'startTime':'5:30','day':2, 'cleaningMode':'1'}
                                }
                            );
                        }, 1000);
                    break;
                    case "5d31a4e8-5eca-41b8-87bc-fa10a13c4151":
                        window.setTimeout(function() {
                            callbackSuccess(
                                {   
                                    'scheduleId': '955fe88b-061f-4cc0-9f2b-c4baa73b156a',
                                    'scheduleEventId':'5d31a4e8-5eca-41b8-87bc-fa10a13c4151', 
                                    'scheduleEventData': {'startTime':'1:30','day':0, 'cleaningMode':'1'}
                                }
                            );
                        }, 1000);
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

