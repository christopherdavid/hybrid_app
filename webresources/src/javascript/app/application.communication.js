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
    
    this.onCallbackReturn = function(guid, success, result, notificationOptions, storeKey){
        //console.log("guid: " + guid + " result: " + JSON.stringify(result) + "\nstoreKey: " + storeKey)
        // check if callback belongs to the current viewmodel, otherwise ignore it
        if(that.callbacks[guid])  {
            // TODO: Hide loading indicator AFTER the success and error handling. For now
            //       put here to prevent error alert from not hiding the notification area.
            parent.showLoadingArea(false, notificationOptions.type);
            
            if (success){
                if (storeKey){
                    that.dataValues[storeKey] = result;
                }
                that.callbacks[guid].successCb(result);
            }else{
                parent.showError(result);
                that.callbacks[guid].errorCb(result);
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
     * @param {function} successCallback The callback called, when the communcation layer returns a success. 
     * @param {function} errorCallback The callback called, when the communcation layer returns an error.
     * @param {object} notificationOptions The notification options containing the callback an the notification type.
     * @param {string} [storeKey] optional key for data storage
     */
     this.exec = function(command, args, successCallback, errorCallback, notificationOptions, storeKey) {
        
        var notifyOptions = notificationOptions;
        if (!notificationOptions || !notificationOptions.type){
            notifyOptions = { type: notificationType.SPINNER, message: "" , callback: null };
        }
        
        parent.showLoadingArea(true, notifyOptions.type, notifyOptions.message);
        
        var callGuid = guid();
        // store callbacks for request (using guid) to avoid references to cordova plugin 
        that.callbacks[callGuid] = {successCb:successCallback, errorCb:errorCallback};
        
        //console.log("call guid: " + callGuid);
        switch(args.length) {
            case 0:
                command(function(result) { that.onCallbackReturn(callGuid, true, result, notifyOptions, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result, notifyOptions);});
            break;
            case 1:
                command(args[0], function(result) { that.onCallbackReturn(callGuid, true, result, notifyOptions, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result, notifyOptions);});
            break;
            case 2:
                command(args[0],args[1], function(result) { that.onCallbackReturn(callGuid, true, result, notifyOptions, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result, notifyOptions);});
            break;
            case 3:
                command(args[0],args[1],args[2], function(result) { that.onCallbackReturn(callGuid, true, result, notifyOptions, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result, notifyOptions);});
            break;
            case 4:
                command(args[0],args[1],args[2],args[3], function(result) { that.onCallbackReturn(callGuid, true, result, notifyOptions, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result, notifyOptions);});
            break;
            case 5:
                command(args[0],args[1],args[2],args[3],args[4], function(result) { that.onCallbackReturn(callGuid, true, result, notifyOptions, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result, notifyOptions);});
            break;
            case 6:
                command(args[0],args[1],args[2],args[3],args[4],args[5], function(result) { that.onCallbackReturn(callGuid, true, result, notifyOptions, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result, notifyOptions);});
            break;
            case 7:
                command(args[0],args[1],args[2],args[3],args[4],args[5],args[6], function(result) { that.onCallbackReturn(callGuid, true, result, notifyOptions, storeKey);}, function(result) { that.onCallbackReturn(callGuid, false, result, notifyOptions);});
            break;
            
            default:
            alert("The communcation layer doesn't support this number of arguments: " + args.length);
        }
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
