
/**
 * Opens a link in an external browser. 
 * @param {String} url the url which has to be opened 
 */
var openExternalLink = function (url) {
    window.open(url, "_blank");
};

function initDeviceConfig() {
    app.config.pageTransition = "slide";
    app.config.device = DEVICETYPE.iOS;
}

/** 
 * Fixed header jumps around when iOS keyboard disappears
 * https://github.com/jquery/jquery-mobile/issues/5532  
 */
$(document).on('blur', 'input, textarea', function() {
    setTimeout(function() {
        window.scrollTo(document.body.scrollLeft, document.body.scrollTop);
    }, 0);
});

/**
 * Rotation handling in iOS
 *  
 * @param {Int} rotation
 */
function shouldRotateToOrientation (rotation) {
    switch (rotation) {
        //Portrait or PortraitUpsideDown
        case 0:
        case 180:
            return app.orientation.portrait;
        //LandscapeRight or LandscapeLeft
        case 90:
        case -90:
            return app.orientation.landscape;
    }
}


var OrientationPlugin = function() {};
             
OrientationPlugin.prototype.forceOrientation = function(options) {
    PhoneGap.exec(null, null, "Orientation", "forceOrientation", [null]);
};
    
OrientationPlugin.install = function() {
    if(!window.plugins) {
        window.plugins = {};
    }
        
    window.plugins.orientation = new OrientationPlugin();
};

/**
 * Add to PhoneGap constructor
 */
PhoneGap.addConstructor(OrientationPlugin.install);


var forceRotation = function (orientation) {
    OrientationPlugin.prototype.forceOrientation(orientation);
};