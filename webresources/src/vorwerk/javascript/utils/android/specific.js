
/**
 * Opens a link in an external browser. 
 * @param {String} url the url which has to be opened 
 */
var openExternalLink = function (url) {
    navigator.app.loadUrl(url, { openExternal:true });
};

function initDeviceConfig() {
    
}

var screenOrientation = function() {}

screenOrientation.prototype.set = function(str, success, fail) {
    cordova.exec(null, null, "ScreenOrientation", "set", [str]);
};
navigator.screenOrientation = new screenOrientation();

var forceRotation = function (orientation) {
    navigator.screenOrientation.set(orientation);
}
