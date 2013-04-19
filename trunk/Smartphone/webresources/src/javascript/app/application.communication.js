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
    
    this.onCallbackReturn = function(guid, success, result, storeKey){
        //console.log("guid: " + guid + " result: " + JSON.stringify(result) + "\nstoreKey: " + storeKey)
        // check if callback belongs to the current viewmodel, otherwise ignore it
        if(that.callbacks[guid]) {
            // TODO: Hide loading indicator AFTER the success and error handling. For now
            //       put here to prevent error alert from not hiding the notification area.
            //parent.showLoadingArea(false, notificationOptions.type);
            if(typeof that.callbacks[guid].notifyOptions.bHide == "undefined" || that.callbacks[guid].notifyOptions.bHide === true) {
                parent.showLoadingArea(false, that.callbacks[guid].notifyOptions.type);
            }
            if (success){
                if (storeKey && result){
                    that.dataValues[storeKey] = result;
                }
                that.callbacks[guid].oDeferred.resolve(result, that.callbacks[guid].notifyOptions);
            } else {
                parent.showError(result);
                that.callbacks[guid].oDeferred.reject(result, that.callbacks[guid].notifyOptions);
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
     * @param {string} [storeKey] optional key for data storage
     * @return {object} returns the promise of the deferred object
     */
     this.exec = function(command, args, notificationOptions, storeKey) {
        var oDeferred = $.Deferred();
        var notifyOptions = notificationOptions;
        if (!notificationOptions || !notificationOptions.type){
            notifyOptions = { type: notificationType.SPINNER, message: "" , callback: null, bHide: true };
        }
        
        parent.showLoadingArea(true, notifyOptions.type, notifyOptions.message);
        
        var callGuid = guid();
        // store defferd object for callbacks and notification options (using guid) to avoid references to cordova plugin 
        that.callbacks[callGuid] = {oDeferred:oDeferred, notifyOptions:notifyOptions};
        
        //console.log("call guid: " + callGuid);
        switch(args.length) {
            case 0:
                command(function(result) { that.onCallbackReturn(callGuid, true, result, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 1:
                command(args[0], function(result) { that.onCallbackReturn(callGuid, true, result, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 2:
                command(args[0],args[1], function(result) { that.onCallbackReturn(callGuid, true, result, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 3:
                command(args[0],args[1],args[2], function(result) { that.onCallbackReturn(callGuid, true, result, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 4:
                command(args[0],args[1],args[2],args[3], function(result) { that.onCallbackReturn(callGuid, true, result, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 5:
                command(args[0],args[1],args[2],args[3],args[4], function(result) { that.onCallbackReturn(callGuid, true, result, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 6:
                command(args[0],args[1],args[2],args[3],args[4],args[5], function(result) { that.onCallbackReturn(callGuid, true, result, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            case 7:
                command(args[0],args[1],args[2],args[3],args[4],args[5],args[6], function(result) { that.onCallbackReturn(callGuid, true, result, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result);});
            break;
            
            default:
            alert("The communcation layer doesn't support this number of arguments: " + args.length);
        }
        return oDeferred.promise();
    };
    
    this.clearDataValues = function(){
      that.dataValues = {};  
    };
    
    this.storeDataValue = function(key, dataValue){
        that.dataValues[key] = dataValue;
    }
    
    this.removeDataValue = function(key) {
        delete that.dataValues[key];
    }
}
