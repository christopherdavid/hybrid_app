/**
 * WorkflowCommunication create the communication wrapper.
 * @class Represents a WorkflowCommunication
 * @param {object} parent Reference to the parent object.
 */
function WorkflowCommunication(parent) {
    console.log('create WorkflowCommunication instance');
    var that = this;
    this.dataValues = {};
    // stores the callbacks for the current view
    this.callbacks = {};
    // callbacks that were handled after the view has been unloaded
    this.staticCallbacks = {};
    
    this.onCallbackReturn = function(guid, success, result){
        console.log("guid: " + guid + " result: " + JSON.stringify(result) + "\nsuccess: " + success);
        // check if callback belongs to the current viewmodel, otherwise ignore it
        if(that.callbacks[guid]) {
            if(that.callbacks[guid].notifyOptions.type != notificationType.NONE && typeof that.callbacks[guid].notifyOptions.bHide == "undefined" || that.callbacks[guid].notifyOptions.bHide === true) {
                parent.notification.showLoadingArea(false, that.callbacks[guid].notifyOptions.type, "", guid);
            }
            if (success){
                that.callbacks[guid].oDeferred.resolve(result, that.callbacks[guid].notifyOptions);
            } else {
                // call reject on all listeners
                var errorHandled = $.Deferred();
                var errorHandledDone = errorHandled.promise();
                // add function to call reject on promise as last failCallback in the chain
                that.callbacks[guid].oDeferred.fail(function(error, notificationOptions, errorHandled) {
                    // if the state of the deferred already has been set nothing happens, the state doesn't change
                    errorHandled.reject();
                });
                // call reject which calls every failCallback in the chain and gives them the possibility to handle the error
                // if the error has been handled in failCallback the deferred must be notified using errorHandled.resolve()
                // this avoids the callback of fail and therefore doesn't shows the default error message 
                that.callbacks[guid].oDeferred.reject(result, that.callbacks[guid].notifyOptions, errorHandled);
                
                // handle promise object
                errorHandledDone.fail(function(error){
                    console.log("error not handled, show default error message");
                    // show error message
                    parent.notification.showError(result);
                });
            }
        // check if there was a static callback
        } else if(that.staticCallbacks[guid]) {
            if (success){
                that.staticCallbacks[guid].oDeferred.resolve(result, that.staticCallbacks[guid].notifyOptions);
            } else {
                that.staticCallbacks[guid].oDeferred.reject(result, that.staticCallbacks[guid].notifyOptions);
                parent.notification.showError(result);
            }
        }
    };
    
    /**
     * Calls a given command on the communication layer using the given arguments and callbacks.
     * This function will as a sideeffect make sure, that the loading animation is shown and
     * a notification is displayed in an error scenario. To save results in the communcation layer
     * dataValues array you can specify an (optional) storeKey.
     * @param {function} command The command to be called on the communication layer.
     * @param {array} args The arguments for the function call. If no arguments exists be sure to supply an empty array.
     * @param {object} notificationOptions The notification options containing the callback an the notification type.
     * @param {string} [bStatic] optional flag to handle callbacks after viewmodel changed
     * @return {object} returns the promise of the deferred object
     */
     this.exec = function(command, args, notificationOptions, bStatic, wifiCheck) {
        var oDeferred = $.Deferred();
        wifiCheck = typeof wifiCheck != "undefined" ? wifiCheck : false;
        var connectionChecked = $.Deferred();
        var connectionCheckedDone = connectionChecked.promise();
        
        that.connectionCheck(wifiCheck, connectionChecked);
        // connected
        connectionCheckedDone.done(function(result) {
            that.conectedExecute(oDeferred, command, args, notificationOptions, bStatic);
        });
        connectionCheckedDone.fail(function(error) {
            oDeferred.reject(error);
        });
        return oDeferred.promise();
    };
    
    this.connectionCheck = function(wifiCheck, conDeffer) {
        var networkState = navigator.network.connection.type;
        var dialogHeader = "";
        var dialogText = "";
        var hasCancel = wifiCheck ? true : false;
        
        if(networkState == Connection.UNKNOWN || networkState == Connection.NONE) {
            dialogHeader =  $.i18n.t("messages.no_connection.title");
            dialogText =  $.i18n.t("messages.no_connection.message");
            
        } else if(wifiCheck && networkState != Connection.WIFI) {
            dialogHeader =  $.i18n.t("messages.no_wifi.title");
            dialogText =  $.i18n.t("messages.no_wifi.message");
        }
        
        // show dialog
        if(dialogHeader != "" && !hasCancel) {
            parent.notification.showDialog(dialogType.ERROR, dialogHeader, dialogText, 
                [{"label":$.i18n.t("common.ok"), "callback":function(e){
                        parent.notification.closeDialog();
                        that.connectionCheck(wifiCheck, conDeffer);
                        }
                }]);
        } else if(dialogHeader != "" && hasCancel) {
            parent.notification.showDialog(dialogType.ERROR, dialogHeader, dialogText, 
                [{"label":$.i18n.t("common.ok"), "callback":function(e){
                    parent.notification.closeDialog();
                    that.connectionCheck(wifiCheck, conDeffer);
                 }},
                 {"label":$.i18n.t("common.cancel"), "callback":function(e){
                    parent.notification.closeDialog();
                    conDeffer.reject({"networkState":networkState, "state":"rejected"});
                 }}]);
        } else {
            conDeffer.resolve({"networkState":networkState, "state":"connected"});
        }
    };
    
    this.conectedExecute = function(oDeferred, command, args, notificationOptions, bStatic) {
        var notifyOptions = notificationOptions;
        var callGuid = guid();
        bStatic = typeof bStatic != "undefined" ? bStatic : false;
        
        if (!notificationOptions || !notificationOptions.type){
            notifyOptions = { type: notificationType.SPINNER, message: "" , bHide: true };
        }
        if(notifyOptions.type != notificationType.NONE) { 
            parent.notification.showLoadingArea(true, notifyOptions.type, notifyOptions.message, callGuid);
        }
        
        // store defferd object for callbacks and notification options (using guid) to avoid references to cordova plugin 
        if(!bStatic) {
            that.callbacks[callGuid] = {oDeferred:oDeferred, notifyOptions:notifyOptions};
        } else {
            that.staticCallbacks[callGuid] = {oDeferred:oDeferred, notifyOptions:notifyOptions};
        }
        
        //console.log("call guid: " + callGuid);
        switch(args.length) {
            case 0:
                command(function(result) { that.onCallbackReturn(callGuid, true, result);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 1:
                command(args[0], function(result) { that.onCallbackReturn(callGuid, true, result);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 2:
                command(args[0],args[1], function(result) { that.onCallbackReturn(callGuid, true, result);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 3:
                command(args[0],args[1],args[2], function(result) { that.onCallbackReturn(callGuid, true, result);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 4:
                command(args[0],args[1],args[2],args[3], function(result) { that.onCallbackReturn(callGuid, true, result);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 5:
                command(args[0],args[1],args[2],args[3],args[4], function(result) { that.onCallbackReturn(callGuid, true, result);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 6:
                command(args[0],args[1],args[2],args[3],args[4],args[5], function(result) { that.onCallbackReturn(callGuid, true, result);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 7:
                command(args[0],args[1],args[2],args[3],args[4],args[5],args[6], function(result) { that.onCallbackReturn(callGuid, true, result);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            
            default:
            alert("The communcation layer doesn't support this number of arguments: " + args.length);
        }
    };
    
    
    //TODO: add comments
    this.mapDataValue = function(key, dataValue) {
        that.dataValues[key](ko.mapping.fromJS(dataValue), null, that.dataValues[key]);
    };
    
    this.clearDataValues = function(){
      that.getDataValue("robotList")([]);
      that.getDataValue("selectedRobot")({});
      that.dataValues["scheduleId"] = null;
      that.dataValues["user"] = null;
      that.saveToLocalStorage('username', null);
      // remove offline class
      $("body").removeClass("offline"); 
    };
    
    this.setDataValue = function(key, dataValue){
        that.dataValues[key] = dataValue;
    };
    
    this.getDataValue = function(key) {
        return that.dataValues[key]||null;
    };
    
    this.removeDataValue = function(key) {
        delete that.dataValues[key];
    };
    
    /**
     * Add a value to the localStorage 
     */
    this.saveToLocalStorage = function(key, value) {
        // try to get value and save it in localStorage
        var tempStore = $.parseJSON( window.localStorage.getItem("cleaningAppStore")) || {};
        tempStore[key] = value;
        window.localStorage.setItem("cleaningAppStore", JSON.stringify(tempStore));
    };

    /**
     * @return value from localStorage or null 
     */
    this.getFromLocalStorage = function(key) {
        var tempStore = $.parseJSON( window.localStorage.getItem("cleaningAppStore")) || {};
        return tempStore[key] || null;
    };
    
    this.clearStaticCallbacks = function(staticKey) {
        that.staticCallbacks = {};
    };
    
    // this requests the robot state for each roboter 
    this.getRobotState = function(robotId) {
        // request cleaning state
        var tDeffer = that.exec(RobotPluginManager.getRobotCurrentState, [robotId], { type: notificationType.NONE }, true);
        tDeffer.done(that.successGetRobotState);
    };
    
    this.successGetRobotState = function(result) {
        if(result.robotCurrentState && result.robotId && result.robotNewVirtualState) {
            var tempRobots = that.getDataValue("robotList");
            // find robot with robotId in global binding object
            $.each(tempRobots(), function(index, item){
                if(item.robotId() == result.robotId) {
                    // update state
                    that.updateRobotStateWithCode(item, result.robotNewVirtualState, result.robotCurrentState);
                    return false;
                }
            });
        }
    };
    
    // this requests the robot online status of each robot
    this.getRobotOnline = function(robotId) {
        // request Online state
        var tDeffer = that.exec(RobotPluginManager.getRobotOnlineStatus, [robotId], { type: notificationType.NONE }, true);
        tDeffer.done(that.successGetRobotOnlineState);
    };
    
    this.successGetRobotOnlineState = function(result) {
    	console.log("Robot Online Success :"+ JSON.stringify(result));
    	if(result.robotId) {
            var tempRobots = that.getDataValue("robotList");
            // find robot with robotId in global binding object
            $.each(tempRobots(), function(index, item){
                if(item.robotId() == result.robotId) {
                    // update online state
                    that.updateRobotOnlineState(item, result.online);
         		    return false;
                }
            });
        }
    };
    
    this.updateRobotOnlineState = function(robot, online) {
        var curRobot = that.getDataValue("selectedRobot");
        var state =  $.i18n.t("robotStateCodes." + visualState[ROBOT_UI_STATE_ROBOT_OFFLINE]);
        var stateChanged = robot.robotOnline() != online; // 0:offline  1:online
        
        console.log("updateRobotOnlineState: "  + online + " for robot: " + robot.robotId() + " stateChanged " + stateChanged);
        
        robot.robotOnline(online == "1");
        
        if(stateChanged) {
            if (online == "0") {
                robot.stateString(state);
                // check if robot is the current robot 
                if(curRobot().robotId && robot.robotId() == curRobot().robotId()) {
                    robotUiStateHandler.setUiState(ROBOT_UI_STATE_ROBOT_OFFLINE);
                }
            // new state online:
            } else {
                robot.visualOnline(true);
                // set robot state to last internal state
                if(robot.robotNewVirtualState() != ROBOT_STATE_UNKNOWN) {
                    that.updateRobotStateWithCode(robot, robot.robotNewVirtualState());
                } else {
                    // robot state is unnokwn so we need to make a server request
                    that.getRobotState(robot.robotId());
                }
            }
        }
    };
    
    this.updateRobotStateWithCode = function(robot, virtualState, currentState) {
        // make sure virtualState is an integer
        virtualState = parseInt(virtualState, 10);
        // update state, make sure it's an valid state code and robot is online
        if(virtualState >= 1 && virtualState <= 8 && robot.robotNewVirtualState && robot.robotOnline()) {
            var curRobot = that.getDataValue("selectedRobot");
            var state = $.i18n.t("robotStateCodes." + visualState[virtualState]);
            var stateChanged = robot.robotNewVirtualState() != virtualState;
            console.log("updateRobotStateWithCode: "  + virtualState + " text: " + state + " for robot: " + robot.robotId());
            // update robot object
            robot.robotNewVirtualState(virtualState);
            robot.stateString(state);
            if(typeof currentState != "undefined") {
                // make sure currentState is an integer
                currentState = parseInt(currentState, 10);
                // make sure it's an valid state code
                if(currentState >= 1 && currentState <= 8) {
                    robot.robotCurrentState(currentState);
                }
            }
            
            if(virtualState == ROBOT_STATE_MANUAL_CLEANING) {
                robot.cleaningCategory(CLEANING_CATEGORY_MANUAL);
            }
            
            // check if robot is the current robot 
            if(curRobot().robotId && robot.robotId() == curRobot().robotId()) {
                // update state handler if state hasen't changed, this is necessary to update the UI initially
                if(!stateChanged) {
                    robotUiStateHandler.setVirtualState(virtualState);
                }
                
            }
            
        }
    };
    
    this.parseStateParameters = function(robot, parameters) {
        console.log("parseStateParameters for robot " + robot.robotId() + " parameters: " + JSON.stringify(parameters));
        if(typeof parameters.RobotIsDocked != "undefined") {
            robot.robotIsDocked(parseInt(parameters.RobotIsDocked, 10));
        }
        if(typeof parameters.ClockIsSet != "undefined") {
            robot.clockIsSet(parseInt(parameters.ClockIsSet, 10));
        }
        if(typeof parameters.DockHasBeenSeen != "undefined") {
            robot.dockHasBeenSeen(parseInt(parameters.DockHasBeenSeen, 10));
        }
        if(typeof parameters.IsCharging != "undefined") {
            robot.isCharging(parseInt(parameters.IsCharging, 10));
        }
        if(typeof parameters.CrntErrorCode != "undefined") {
            robot.crntErrorCode(parseInt(parameters.CrntErrorCode, 10));
        }
     };
}

