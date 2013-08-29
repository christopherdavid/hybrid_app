/**
 * WorkflowCommunication create the communication wrapper.
 * @class Represents a WorkflowCommunication
 * @param {object} parent Reference to the parent object.
 */
function WorkflowCommunication(parent) {
    console.log('create WorkflowCommunication instance')
    var that = this;
    this.dataValues = {};
    // stores the callbacks for the current view
    this.callbacks = {};
    // callbacks that were handled after the view has been unloaded
    this.staticCallbacks = {};
    
    this.onCallbackReturn = function(guid, success, result){
        console.log("guid: " + guid + " result: " + JSON.stringify(result) + "\nsuccess: " + success)
        // check if callback belongs to the current viewmodel, otherwise ignore it
        if(that.callbacks[guid]) {
            if(that.callbacks[guid].notifyOptions.type != notificationType.NONE && typeof that.callbacks[guid].notifyOptions.bHide == "undefined" || that.callbacks[guid].notifyOptions.bHide === true) {
                parent.notification.showLoadingArea(false, that.callbacks[guid].notifyOptions.type, "", guid);
            }
            if (success){
                that.callbacks[guid].oDeferred.resolve(result, that.callbacks[guid].notifyOptions);
            } else {
                parent.notification.showError(result);
                that.callbacks[guid].oDeferred.reject(result, that.callbacks[guid].notifyOptions);
            }
        // check if there was a static callback
        } else if(that.staticCallbacks[guid]) {
            if (success){
                that.staticCallbacks[guid].oDeferred.resolve(result, that.staticCallbacks[guid].notifyOptions);
            } else {
                parent.notification.showError(result);
                that.staticCallbacks[guid].oDeferred.reject(result, that.staticCallbacks[guid].notifyOptions);
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
     this.exec = function(command, args, notificationOptions, bStatic) {
        var oDeferred = $.Deferred();
        var networkState = navigator.network.connection.type;
        
        if(networkState != Connection.UNKNOWN && networkState != Connection.NONE) {
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
        } else {
            var noConnectionHeader =  $.i18n.t("messages.no_connection.title");
            var noConnectionText =  $.i18n.t("messages.no_connection.message");
            parent.notification.showDialog(dialogType.ERROR, noConnectionHeader, noConnectionText, 
                [{"label":"OK", "callback":function(e){
                    parent.notification.closeDialog();
                    oDeferred.reject({log:"connection error"});
                }}]);
            
        }
        return oDeferred.promise();
    };
    
    //TODO: add comments
    
    this.clearDataValues = function(){
      that.getDataValue("robotList")([]);
      that.getDataValue("selectedRobot")({});
      that.dataValues["scheduleId"] = null;
      that.saveToLocalStorage('username', null); 
    };
    
    this.setDataValue = function(key, dataValue){
        that.dataValues[key] = dataValue;
    }
    
    this.getDataValue = function(key) {
        return that.dataValues[key]||null;
    }
    
    this.removeDataValue = function(key) {
        delete that.dataValues[key];
    }
    
    /**
     * Add a value to the localStorage 
     */
    this.saveToLocalStorage = function(key, value) {
        // try to get value and save it in localStorage
        var tempStore = $.parseJSON( window.localStorage.getItem("cleaningAppStore")) || {};
        tempStore[key] = value;
        window.localStorage.setItem("cleaningAppStore", JSON.stringify(tempStore));
    }

    /**
     * @return value from localStorage or null 
     */
    this.getFromLocalStorage = function(key) {
        var tempStore = $.parseJSON( window.localStorage.getItem("cleaningAppStore")) || {};
        return tempStore[key] || null;
    }
    this.clearStaticCallbacks = function(staticKey) {
        that.staticCallbacks = {};
    }
    // this requests the robot state for each roboter 
    this.getRobotState = function(robotId) {
        // Send command that the robot should return to base
        var tDeffer = that.exec(RobotPluginManager.sendCommandToRobot2, [robotId, COMMAND_GET_ROBOT_STATE, {}], { type: notificationType.NONE }, true);
        tDeffer.done(that.successGetRobotState);
    };
    
    this.successGetRobotState = function(result) {
        if(result.stateCode && result.robotId) {
            var tempRobots = that.getDataValue("robotList");
            // find robote with robotId in global binding object
            var state = $.i18n.t("robotStateCodes." + result.stateCode);
            // loop over all robots and add the state property
            $.each(tempRobots(), function(index, item){
                if(item.robotId() == result.robotId) {
                    // update state
                    that.updateRobotStateWithCode(item, result.stateCode);
                    return false;
                }
            });
        }
    }
    
    this.updateRobotStateWithCode = function(robot, curState) {
        // make sure curState is an integer
        curState = parseInt(curState, 10);
        //update state, make sure it's an valid state code
        if(curState >= 10001 && curState <= 10009 && robot.stateCode) {
            var curRobot = that.getDataValue("selectedRobot");
            var state = $.i18n.t("robotStateCodes." + curState);
            console.log("updateRobotStateWithCode: "  + curState + " text: " + state + " for robot: " + robot.robotId())
            // update robot object
            robot.stateCode(curState);
            robot.stateString(state);
            
            // check if robot is the current robot and update state machine
            if(curRobot().robotId && robot.robotId() == curRobot().robotId()) {
                // update state machine
                switch(curState) {
                    case ROBOT_STATE_CLEANING:
                    case ROBOT_STATE_RESUMED:
                        robotStateMachine.clean();
                        break;
                    case ROBOT_STATE_PAUSED:
                        robotStateMachine.pause();
                        break;
                    default:
                        robotStateMachine.deactivate();
                }
            }
        }
    }
}

