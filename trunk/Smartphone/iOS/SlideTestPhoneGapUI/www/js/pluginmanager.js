var PluginManager = {
    
callNativeFunction: function (success, fail, pluginName, functionName, argumentsArray)
{
    return Cordova.exec( success, fail,
                        pluginName,
                        functionName,
                        [argumentsArray]);
}
};