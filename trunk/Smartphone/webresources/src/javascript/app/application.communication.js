/**
 * WorkflowCommunication create the communication wrapper.
 * @class Represents a WorkflowCommunication
 * @param {object} parent Reference to the parent object.
 */
function WorkflowCommunication(parent) {
    console.log('create WorkflowCommunication instance')
    var that = this;
    this.dataValues = {};
    this.onCallbackReturn = function(success, result, storeKey){
        //console.log("result: " + JSON.stringify(result) + "\nstoreKey: " + storeKey)
        if (success){
            if (storeKey){
                that.dataValues[storeKey] = result;
            }
        }else{
            console.log("error: " + result.errorCode + " msg: " + result.errorMessage);
        }
        parent.showLoadingArea(false);
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
     * @param {string} [storeKey] optional key for data storage
     */
     this.exec = function(command, args, successCallback, errorCallback, storeKey) {
        
        parent.showLoadingArea(true);
        
        switch(args.length) {
            case 0:
                command(function(result) { that.onCallbackReturn(true, result, storeKey); successCallback(result)}, function(result) { that.onCallbackReturn(false, result, storeKey), errorCallback(result)});
            break;
            case 1:
                command(args[0], function(result) { that.onCallbackReturn(true, result, storeKey); successCallback(result)}, function(result) { that.onCallbackReturn(false, result, storeKey), errorCallback(result)});
            break;
            case 2:
                command(args[0],args[1], function(result) { that.onCallbackReturn(true, result, storeKey); successCallback(result)}, function(result) { that.onCallbackReturn(false, result, storeKey), errorCallback(result)});
            break;
            case 3:
                command(args[0],args[1],args[2], function(result) { that.onCallbackReturn(true, result, storeKey); successCallback(result)}, function(result) { that.onCallbackReturn(false, result, storeKey), errorCallback(result)});
            break;
            case 4:
                command(args[0],args[1],args[2],args[3], function(result) { that.onCallbackReturn(true, result, storeKey); successCallback(result)}, function(result) { that.onCallbackReturn(false, result, storeKey), errorCallback(result)});
            break;
            case 5:
                command(args[0],args[1],args[2],args[3],args[4], function(result) { that.onCallbackReturn(true, result, storeKey); successCallback(result)}, function(result) { that.onCallbackReturn(false, result, storeKey), errorCallback(result)});
            break;
            case 6:
                command(args[0],args[1],args[2],args[3],args[4],args[5], function(result) { that.onCallbackReturn(true, result, storeKey); successCallback(result)}, function(result) { that.onCallbackReturn(false, result, storeKey), errorCallback(result)});
            break;
            case 7:
                command(args[0],args[1],args[2],args[3],args[4],args[5],args[6], function(result) { that.onCallbackReturn(true, result, storeKey); successCallback(result)}, function(result) { that.onCallbackReturn(false, result, storeKey), errorCallback(result)});
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
