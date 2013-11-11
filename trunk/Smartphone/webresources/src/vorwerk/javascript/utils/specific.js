
/**
 * Opens a link in an external browser. 
 * @param {String} url the url which has to be opened 
 */
var openExternalLink = function (url) {
    window.open(url, "_blank");
};

function initDeviceConfig() {
    //app.config.pageTransition = "fade";
}

/**
 * Forces the desired orientation. 
 * @param {int} the orientation in degrees (0, 90 or "auto") 
 */
var forceRotation = function (orientation) {
    console.log("Orientation forced: " + orientation);
};

