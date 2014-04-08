
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
             
OrientationPlugin.prototype.setAllowed = function(options) {
    PhoneGap.exec(null, null, "Orientation", "setAllowed", options);
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
    if(orientation == "fullSensor"){
        window.plugins.orientation.setAllowed([{pp:true, pd:true, ll:true, lr:true}]);
    } else if(orientation == "portrait"){
        window.plugins.orientation.setAllowed([{pp:true, pd:true, ll:false, lr:false}]);
    } else if(orientation == "landscape"){
        window.plugins.orientation.setAllowed([{pp:false, pd:false, ll:true, lr:true}]);
    }  
};