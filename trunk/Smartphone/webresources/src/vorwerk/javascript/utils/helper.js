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
        ((poly[i].y <= pt.y && pt.y < poly[j].y) || (poly[j].y <= pt.y && pt.y < poly[i].y)) && (pt.x < (poly[j].x - poly[i].x) * (pt.y - poly[i].y) / (poly[j].y - poly[i].y) + poly[i].x) && ( c = !c)
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

var deviceSize =  (function() {
    var size, res;
    return {
        getSize: function() {
            size = {
                width: $('[data-role="page"]').first().width(),
                height: $('[data-role="page"]').first().height()
            }
            console.log("deviceSize " + JSON.stringify(size));
            this.getSize = function() {
                return size;
            }
            return size;
        },
        getResolution: function() {
            res = Math.max(this.getSize().width, this.getSize().height) > 950 ? "high" : "low";
            console.log("resolution " + res);
            this.getResolution = function() {
                return res;
            }
            return res;
        }
    }
}());

function localizeTime(time) {
    var timeFormat = $.i18n.t("pattern.time");
    var amPmMarker = "";
    var hour = time.split(":")[0];
    var min = time.split(":")[1]
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
function resizePopupButtons(buttonGroup) {
    var maxWidth = 0;
    
    //save the max width of the biggest button
    $(buttonGroup + " .ui-btn").each(function(index){
         maxWidth = Math.max(maxWidth, $(this).width());
    });
    
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
    current:null,
    
    subscribeToRobot:function(refRobot) {
        this.current().robot(refRobot().robotNewVirtualState());
        this.updateStates(refRobot().robotNewVirtualState());
        
        subscription = refRobot().robotNewVirtualState.subscribe(function(newValue) {
            // console.log("robotUiStateHandler robotNewVirtualState.subscribe " + newValue + " visualState " + visualState[newValue]);
            this.current().robot(newValue);
            this.current().ui(newValue);
            this.updateStates(newValue);
        }, this);
    },
    
    disposeFromRobot:function() {
        subscription.dispose();
    },
    // robotNewVirtualState register to robot state
    // ui state of the app
    setUiState: function(state) {
        if(visualState[state]) {
            this.current().ui(state);
            this.updateStates(state);
        }
    },
    updateStates: function(state) {
        this.current().messageIcon(visualState[state]);
        if(state == ROBOT_UI_STATE_WAIT) {
            this.current().messageText($.i18n.t("visualState.waiting_unknown"));
        } else if(state == ROBOT_UI_STATE_WAKEUP) {
            this.current().messageText($.i18n.t("visualState.wakeup_unknown"));
        } else {
            this.current().messageText($.i18n.t("visualState." + visualState[state]));
        }
        // set start button state
        this.setStarButtonState(state);
    },
    updateMessage: function(strText) {
        this.current().messageText(strText);
    },
    updateIcon: function(state) {
        this.current().messageIcon(visualState[state]);
    },
    setStarButtonState:function(state) {
        // start button only has 4 state:
        // - cleaning
        // - paused
        // - waiting
        // - stopped
        
        if(state == ROBOT_STATE_CLEANING || state == ROBOT_STATE_RESUMED) {
            this.current().startButton(visualState[ROBOT_STATE_CLEANING]);
        } else if(state == ROBOT_STATE_PAUSED || state == ROBOT_UI_STATE_WAIT) {
            this.current().startButton(visualState[state]);
        } else if(state == ROBOT_UI_STATE_CONNECTING || state == ROBOT_UI_STATE_WAKEUP || state == ROBOT_UI_STATE_GETREADY) {
            this.current().startButton(visualState[ROBOT_UI_STATE_WAIT]);
        } else if(state == ROBOT_STATE_MANUAL_CLEANING) {
            this.current().startButton(visualState[ROBOT_STATE_CLEANING]);
        } else {
            this.current().startButton(visualState[ROBOT_STATE_STOPPED]);
        }
    }
    
    // triggerCallback:function() {
        // this.changestate(this.lastState, this.current);
    // },
    // changestate: function(from, to) {
        // this.lastState = from;
        // this.current = to;
        // if(this.callback != null && typeof this.callback == "function") {
            // this.callback(from, to);
        // }
    // }
};
function createWaitMessageLoop(delayWakeUp, delayGetReady, robotId, lastGuid) {
    console.log("createWaitMessageLoop delayWakeUp: "+ delayWakeUp + " robotId: " + robotId)
    var callGuid = guid();
    var sWakeUpTime = delayWakeUp + 2;
    //  sWakeUp = "Waking Up in about " + (delayWakeUp + 2) + " seconds";
    var visibleTime = delayWakeUp > 10 ? 10 : delayWakeUp;
    var curRobot = app.communicationWrapper.getDataValue("selectedRobot");
    // check if robot is still selected and robot state hasn't changed otherwise stop recursion
    if(curRobot().robotId && curRobot().robotId() == robotId && robotUiStateHandler.current().ui() == ROBOT_UI_STATE_WAKEUP) {
        // call createWaitMessageLoop recursive till end
        if(delayWakeUp > 0) {
            window.setTimeout(function(){
                createWaitMessageLoop((delayWakeUp-10),delayGetReady, robotId, callGuid);
            }, visibleTime*1000);
        }
        
        // close last message            
        if(typeof lastGuid != "undefined") {
            app.notification.showLoadingArea(false, notificationType.WAKEUP, "", lastGuid);
        }
        
        // update state in robotUiStateHandler
        if(delayWakeUp > 0) {
            robotUiStateHandler.updateMessage($.i18n.t("visualState.waiting", {"wakeUp":sWakeUpTime}));
        } else {
            robotUiStateHandler.setUiState(ROBOT_UI_STATE_GETREADY);
        }
        
        // show notification bar in other views
        if(app.viewModel.screenId != "cleaning") {
            if(delayWakeUp > 0 && visibleTime <= 10) {
                app.notification.showLoadingArea(true, notificationType.WAKEUP, $.i18n.t("communication.wakeup_robot", {"wakeUp":sWakeUpTime}), callGuid);
            } else if(delayWakeUp <= 0) {
                app.notification.showLoadingArea(true, notificationType.WAKEUP, $.i18n.t("communication.getready_robot"), callGuid);
                window.setTimeout(function(){
                    app.notification.showLoadingArea(false, notificationType.GETREADY, "", callGuid);
                }, delayGetReady*1000);
            }
        }
    } else {
        if(typeof lastGuid != "undefined") {
            // close message
            app.notification.showLoadingArea(false, notificationType.WAKEUP, "", lastGuid);
        }
   }
}
function handleTimedMode(expectedTimeToExecute, robotId) {
    console.log("handleTimedMode");
    // calulate timing for animations
    var delayWakeUp = 1;
    var delayGetReady = 1;
    var callGuidW = guid(), callGuidR = guid();
    var maxVisibleReadyMsg = 6;
    var curRobot = app.communicationWrapper.getDataValue("selectedRobot");
    if(curRobot().robotId && curRobot().robotId() == robotId) {
        // set ui state
        robotUiStateHandler.setUiState(ROBOT_UI_STATE_WAKEUP);
        // each animation is at least 2s visible cause of notification bar settings
        if(expectedTimeToExecute - 4 > 0) {
            // 1/3 but max of maxVisibleReadyMsg
            delayGetReady = Math.min(Math.floor((expectedTimeToExecute - 4)/3) , maxVisibleReadyMsg);
            delayWakeUp = (expectedTimeToExecute - 2 - delayGetReady);
            createWaitMessageLoop(delayWakeUp, delayGetReady, robotId);
        } else {
            // show message only for cleaning view as status message 
            if(app.viewModel.screenId == "cleaning") {
                window.setTimeout(function(){
                    robotUiStateHandler.setUiState(ROBOT_UI_STATE_GETREADY);
                }, delayWakeUp * 1000);
            // otherwise global in notification bar
            } else {
                app.notification.showLoadingArea(true, notificationType.WAKEUP, $.i18n.t("communication.wakeup_unknown_robot"), callGuidW);
                window.setTimeout(function(){
                    app.notification.showLoadingArea(false, notificationType.WAKEUP, "", callGuidW);
                }, delayWakeUp * 1000);
                app.notification.showLoadingArea(true, notificationType.GETREADY, $.i18n.t("communication.getready_robot"), callGuidR);
                window.setTimeout(function(){
                    app.notification.showLoadingArea(false, notificationType.WAKEUP, "", callGuidR);
                }, delayGetReady);
            }
        }
    }
}