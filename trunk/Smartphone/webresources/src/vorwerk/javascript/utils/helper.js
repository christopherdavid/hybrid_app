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
            res = Math.max(this.getSize().width, this.getSize().height) > 1000 ? "high" : "low";
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
        amPmMarker = hour <= 12 ? ' am' : ' pm';
        
        if(hour > 12) {
            hour = hour - 12;
        }
    }
    // add leading zero
    hour = hour < 10 ? "0" + hour : hour;
    return hour + ':' + min + amPmMarker;
}
// helper class for robot state
var robotStateMachine = {
    lastState:"inactive",
    current:"inactive",
    callback: null,
    // switch back to the state before
    reset:function() {
        this.changestate(this.current, this.lastState);
    },
    triggerCallback:function() {
        this.changestate(this.lastState, this.current);
    },
    is:function(state) {
        return this.current == state;
    },
    stateBefore:function() {
        return this.lastState;
    },
    clean:function() {
        console.log("clean")
        this.changestate(this.current, "active");
    },
    disable:function() {
        console.log("disable")
        this.changestate(this.current, "disabled");
    },
    deactivate:function() {
        console.log("deactivate")
        this.changestate(this.current, "inactive");
    },
    pause:function() {
        console.log("pause")
        this.changestate(this.current, "paused");
    },
    wait:function() {
        console.log("wait");
        this.changestate(this.current, "waiting");
    },
    changestate: function(from, to) {
        this.lastState = from;
        this.current = to;
        if(this.callback != null && typeof this.callback == "function") {
            this.callback(from, to);
        }
    }
};
function createWaitMessageLoop(delayWakeUp, delayGetReady, robotId, lastGuid) {
    console.log("createWaitMessageLoop delayWakeUp: "+ delayWakeUp + " robotId: " + robotId)
    var callGuid = guid();
    var sWakeUp = "Waking Up in about " + (delayWakeUp + 2) + " seconds";
    var visibleTime = delayWakeUp > 10 ? 10 : delayWakeUp;
    var curRobot = app.communicationWrapper.getDataValue("selectedRobot");
    // check if robot is still selected and robot state hasn't changed otherwise stop recursion
    if(curRobot().robotId && curRobot().robotId() == robotId && robotStateMachine.is("waiting")) {
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
        // show this timer message only for cleaning view
        if(delayWakeUp > 0 && visibleTime == 10 && app.viewModel.screenId == "cleaning") {
            app.notification.showLoadingArea(true, notificationType.WAKEUP, sWakeUp, callGuid);
        
        // show last wake up message GLOBAL
        } else if (delayWakeUp > 0 && visibleTime <= 10) {
            app.notification.showLoadingArea(true, notificationType.WAKEUP, sWakeUp, callGuid);
        
        // show get ready message GLOBAL
        } else {
           app.notification.showLoadingArea(true, notificationType.GETREADY, "Get ready....", callGuid);
            window.setTimeout(function(){
                app.notification.showLoadingArea(false, notificationType.GETREADY, "", callGuid);
            }, delayGetReady*1000);
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
    var sWakeUp = "Waking Up....";
    var maxVisibleReadyMsg = 6;
    var curRobot = app.communicationWrapper.getDataValue("selectedRobot");
    if(curRobot().robotId && curRobot().robotId() == robotId) {
        robotStateMachine.wait();
        // each animation is at least 2s visible cause of notification bar settings
        if(expectedTimeToExecute - 4 > 0) {
            // 1/3 but max of maxVisibleReadyMsg
            delayGetReady = Math.min(Math.floor((expectedTimeToExecute - 4)/3) , maxVisibleReadyMsg);
            delayWakeUp = (expectedTimeToExecute - 2 - delayGetReady);
            createWaitMessageLoop(delayWakeUp, delayGetReady, robotId);
        } else {
            app.notification.showLoadingArea(true, notificationType.WAKEUP, sWakeUp, callGuidW);
            window.setTimeout(function(){
                app.notification.showLoadingArea(false, notificationType.WAKEUP, "", callGuidW);
            }, delayWakeUp * 1000);
            app.notification.showLoadingArea(true, notificationType.GETREADY, "Get ready....", callGuidR);
            window.setTimeout(function(){
                app.notification.showLoadingArea(false, notificationType.WAKEUP, "", callGuidR);
            }, delayGetReady);
        }
    }
}
