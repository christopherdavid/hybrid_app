/**
 * isPointInPoly checks if a polygon contains an specified point
 * @param {Array} array with point objects, each point must have the properties
 *                x and y
 * @param {Object} point object which must have the properties x and y
 * @return {Boolean} return true if polygon contains the point
 */
function isPointInPoly(poly, pt) {
    var c = false;
    for (var i = -1, l = poly.length, j = l - 1; ++i < l; j = i) {
        ((poly[i].y <= pt.y && pt.y < poly[j].y) || (poly[j].y <= pt.y && pt.y < poly[i].y)) && (pt.x < (poly[j].x - poly[i].x) * (pt.y - poly[i].y) / (poly[j].y - poly[i].y) + poly[i].x) && ( c = !c);
    }
    return c;
}


/**
 * Creates a unique id for identification purposes. 
 * @return {String} returns an uniqueId 
 */
var guid = function () {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
             .toString(16)
             .substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
};

/**
 *  checks if the object is defined and not null and contains the property
 *  @param {Object} object which should be checked 
 *  @param {String} property which should be checked against the object 
 *  @return {Boolean} returns true if object and properties are defined 
 */
function isDefined(object, property) {
    if(object != null) {
        var props = property.split(".");
        for(var i=0; i < props.length; i++) {
            if(typeof object[props[i]] == "undefined") {
                return false;
            }
        }
        return true;
    }
    return false;
};

var deviceSize =  (function() {
    var size, res, orientation;
    $(window).on("resize.deviceSize", function() {
        //that.updateLayout();
        console.log("deviceSize resize event");
        // update size
        size = {
            width: $(window).width(),
            height: $(window).height()
        };
        
        if(size.width > size.height) {
            orientation = deviceSize.ORIENTATION.landscape;
        } else {
            orientation = deviceSize.ORIENTATION.portrait;
        }
        console.log("size " + JSON.stringify(size) + " orientation " + orientation);
    });
    
    return {
        ORIENTATION: {
            "landscape":0,
            "portrait":1
        },
        getOrientation: function() {
            if(orientation == null) {
                if(this.getSize().width > this.getSize().height) {
                    orientation = deviceSize.ORIENTATION.landscape;
                } else {
                    orientation = deviceSize.ORIENTATION.portrait;
                }
            }
            this.getOrientation = function() {
                return orientation;
            };
            return orientation;
        },
        getSize: function() {
            size = {
                width: $(window).width(),
                height: $(window).height()
            };
            console.log("deviceSize " + JSON.stringify(size));
            this.getSize = function() {
                return size;
            };
            return size;
        },
        getResolution: function() {
            res = Math.max(this.getSize().width, this.getSize().height) > 950 ? "high" : "low";
            console.log("resolution " + res);
            this.getResolution = function() {
                return res;
            };
            return res;
        }
    };
}());

function localizeTime(time) {
    var timeFormat = $.i18n.t("pattern.time");
    var amPmMarker = "";
    var hour = time.split(":")[0];
    var min = time.split(":")[1];
    if (timeFormat == "hhiiA") {
        amPmMarker = hour < 12 ? ' am' : ' pm';
        
        if(hour > 12) {
            hour = hour - 12;
        }
    }
    // add leading zero
    hour = hour < 10 ? "0" + hour : hour;
    return {time:(hour + ':' + min), marker:amPmMarker};
}


/**
 * convert the spot size (meter or feet) into centimeters for robot 
 * or with reverse set to true convert it back to meter/feet  
 * @param {Object} spotL the spot length
 * @param {Object} spotH the spot height
 * @param {Object} reverse indicates conversion mode if false or not set 
 *                 converts to cm if true into m or ft   
 */
function convertSpotsize(spotL, spotH, reverse) {
    var spotConverter = parseFloat($.i18n.t("pattern.spotConverter"));
    var tempSize = {
        length:1,
        height:1
    };
    // we need to make sure that converting from ft/m to cm and back we have the 
    // right multiplier. thats why on revert we always round to 100cm 
    // baseMultiplier to have a clean base
    if(typeof reverse != "undefined" && reverse === true) {
        tempSize.length = convertToBase(spotL, spotConverter);
        tempSize.height = convertToBase(spotH, spotConverter);
    } else {
      tempSize.length = (spotL * spotConverter);
      tempSize.height = (spotH * spotConverter);
    }
    return tempSize;
}
function convertToBase(value, spotConverter) {
    var baseValue = 1;
    var baseMultiplier = 100;
    // ensure that we always get a clean base 1-5
    // first figure out if value conforms to the current format (spotConverter)
    if(value%spotConverter > 0) {
        // different format
        if(spotConverter < baseMultiplier) {
            // round down
            baseValue = Math.max(1, Math.floor(value / spotConverter));
        } else {
            // round up
            baseValue = Math.min(5, Math.ceil(value / spotConverter));
        }
    } else {
        // same format
        baseValue = (value / spotConverter);
    }
    return baseValue;
} 
/*
 *  This function ensures that each button in the specified buttonGroup 
 *  have all the same width  
 */
function resizePopupButtons(buttonGroup, maxContainer) {
    var maxWidth = 0;
    
    //save the max width of the biggest button
    $(buttonGroup + " .ui-btn").each(function(index){
         maxWidth = Math.max(maxWidth, $(this).width());
    });
    
    // check if there is a max container width 
    if(typeof maxContainer != "undefined") {
        maxWidth = Math.min(maxWidth, parseInt(maxContainer/2));
    }
    
    //make all buttons the same width
    $(buttonGroup + " .ui-btn").each(function(index){
         $(this).width(maxWidth);
    });
    
}

/**
 * helper class for robot UI state
 * handles the robotNewVirtualState and the UI state of the app (e.g. waiting, error)
 * and stores the state according to enum visualState
 * This is more than a common state maching because it also contains logic for to switch the state
 * and also registers to other states.
 * The UI could bind to robotUiStateHandler.current which is an observable
 */
var robotUiStateHandler = {
    subscription: null,
    subscriptionOnline:null,
    current:null,
    waitTimer:null,
    waitTimeout:60000,
    waitDeffer:null,
    
    subscribeToRobot:function(refRobot) {
        this.current().robot(refRobot().robotNewVirtualState());
        this.updateStates(refRobot().robotNewVirtualState());
        
        subscription = refRobot().robotNewVirtualState.subscribe(function(newValue) {
            this.setVirtualState(newValue);
        }, this);
        
        subscriptionOnline = refRobot().visualOnline.subscribe(function(newValue) {
            this.setVisualOnlineState(newValue);
        }, this); 
    },
    
    setVirtualState:function(state) {
        console.log("setVirtualState " + state + " currentRobot state " + this.current().robot()); 
        var curRobot = app.communicationWrapper.getDataValue("selectedRobot");
        var newUiState = state;
        
        if(state == ROBOT_STATE_PAUSED || state == ROBOT_STATE_IDLE || state == ROBOT_STATE_CLEANING) {
            // create new UI state depending on category
            newUiState = "2" + curRobot().cleaningCategory() + "00" + state;
        }
        
        if(state == ROBOT_STATE_IDLE) {
            if(curRobot().isCharging() == 1) {
                newUiState = ROBOT_UI_STATE_CHARGING;
            } else if(curRobot().robotIsDocked() == 1) {
                newUiState = ROBOT_UI_STATE_ON_BASE;
            }
        }
        
        if(state == ROBOT_STATE_RETURNING) {
            if(curRobot().dockHasBeenSeen() == 1) {
                newUiState = ROBOT_UI_STATE_RETURN_TO_BASE;
            } else {
                newUiState = ROBOT_UI_STATE_RETURN_TO_START;
            }
        }
        
        if(state == ROBOT_STATE_DOCK_PAUSED) {
            newUiState = ROBOT_UI_STATE_DOCK_PAUSED;
        }        
        
        console.log("newUiState " + newUiState + ": " + visualState[newUiState]);
        this.current().robot(state);
        this.current().ui(newUiState);
        this.updateStates(newUiState);
    },
    
    disposeFromRobot:function() {
        subscription.dispose();
        subscriptionOnline.dispose();
    },

    // ui state of the app
    setUiState: function(state) {
        if(visualState[state]) {
            this.current().ui(state);
            this.updateStates(state);
        }
    },
    updateStates: function(state) {
        var curRobot = app.communicationWrapper.getDataValue("selectedRobot");
        this.current().messageIcon(visualState[state]);
        
        if(state == ROBOT_UI_STATE_IDLE_SPOT || state == ROBOT_UI_STATE_PAUSED_SPOT || state == ROBOT_UI_STATE_CLEANING_SPOT) {
            var message = $.i18n.t("visualState." + visualState[state]);
            message += " " + curRobot().spotCleaningAreaLength() + "x" + curRobot().spotCleaningAreaHeight() + " " + $.i18n.t("pattern.spotUnit");
            this.current().messageText(message);
        } else if(state == ROBOT_UI_STATE_ROBOT_OFFLINE) {
            this.current().messageText($.i18n.t("visualState." + visualState[state]));
            // change robot name
            curRobot().displayName(curRobot().robotName() + " (" + $.i18n.t("robotStateCodes." + visualState[state]) + ")");
            curRobot().visualOnline(false);
            // show notification
            //TODO: add check if current view cleaning and don't show notification there
            var translatedText = $.i18n.t("communication." + visualState[state], {robotName:curRobot().robotName()});
            app.notification.showLoadingArea(true,notificationType.HINT,translatedText);
        } else if(state == ROBOT_USER_MENU_STATE) {
            this.current().messageText($.i18n.t("visualState." + visualState[state]));
            // change robot name
            curRobot().displayName(curRobot().robotName() + " (" + $.i18n.t("robotStateCodes." + visualState[state]) + ")");
            curRobot().visualOnline(false);
            // show notification
            //TODO: add check if current view cleaning and don't show notification there 
            var translatedText = $.i18n.t("communication." + visualState[state], {robotName:curRobot().robotName()});
            app.notification.showLoadingArea(true,notificationType.HINT,translatedText);
        } else {
            this.current().messageText($.i18n.t("visualState." + visualState[state]));
        }
        // set start button state
        this.setStartButtonState(state);
    },
    updateMessage: function(strText) {
        this.current().messageText(strText);
    },
    updateIcon: function(state) {
        this.current().messageIcon(visualState[state]);
    },
    
    setVisualOnlineState:function(onlineState) {
        console.log("setVisualOnlineState " + onlineState); 
        var curRobot = app.communicationWrapper.getDataValue("selectedRobot");
        // offline
        if(!onlineState) {             
            // add offline class
            $("body").addClass("offline");
        // online            
        } else {
            // remove offline class
            $("body").removeClass("offline");
            // update robot name
            curRobot().displayName(curRobot().robotName());
        }
    },
    
    
    setStartButtonState:function(state) {
        // start button only has 5 states:
        // - cleaning
        // - paused
        // - waiting
        // - stopped
        // - offline
        
        if(state == ROBOT_STATE_CLEANING || state == ROBOT_UI_STATE_CLEANING_ALL || state == ROBOT_UI_STATE_CLEANING_MANUAL || state == ROBOT_UI_STATE_CLEANING_SPOT || state == ROBOT_UI_STATE_RETURN_TO_BASE || state == ROBOT_UI_STATE_RETURN_TO_START) {
            this.current().startButton(visualState[ROBOT_STATE_CLEANING]);
        } else if(state == ROBOT_STATE_PAUSED || state == ROBOT_UI_STATE_PAUSED_ALL || state == ROBOT_UI_STATE_PAUSED_MANUAL || state == ROBOT_UI_STATE_PAUSED_SPOT || state == ROBOT_UI_STATE_DOCK_PAUSED || state == ROBOT_STATE_DOCK_PAUSED) {
            this.current().startButton(visualState[ROBOT_STATE_PAUSED]);
        } else if(state == ROBOT_UI_STATE_CONNECTING || state == ROBOT_UI_STATE_WAIT || state == ROBOT_STATE_SUSPENDED_CLEANING) {
            this.current().startButton(visualState[ROBOT_UI_STATE_WAIT]);
        } else if(state == ROBOT_STATE_MANUAL_CLEANING) {
            this.current().startButton(visualState[ROBOT_STATE_MANUAL_CLEANING]);
        } else if(state == ROBOT_UI_STATE_ROBOT_OFFLINE || state == ROBOT_USER_MENU_STATE) {
            this.current().startButton(visualState[ROBOT_UI_STATE_ROBOT_OFFLINE]);
        } else {
            this.current().startButton(visualState[ROBOT_STATE_IDLE]);
        }
    },
    
    startWaitTimer:function() {
        console.log("startWaitTimer");
        this.resolveWaitDeffer();
        this.waitDeffer = $.Deferred();
        robotUiStateHandler.setUiState(ROBOT_UI_STATE_WAIT);
        
        return this.waitDeffer.promise();
    },
    
    resolveWaitDeffer:function() {
        // cancel old timer and resolve deffer
        //window.clearTimeout(this.waitTimer);
        if(this.waitDeffer) {
            this.waitDeffer.resolve({"state":"resolved"});
            this.waitDeffer = null;
        }
    },
    
    rejectWaitDeffer:function() {
        if(this.waitDeffer) {
            this.waitDeffer.reject({"state":"rejected"});
            this.waitDeffer = null;
        }
    }
};

