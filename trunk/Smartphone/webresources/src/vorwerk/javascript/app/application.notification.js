/**
 * WorkflowNavigator stores the view model in history.
 * @class Represents a WorkflowNavigator
 * @param {object} parent Reference to the parent object.
 * @param {object} parent Reference to the parent workflow object.
 * @param {object} parent Reference to the parent dataTemplate object.
 */
function WorkflowNotification(parent) {
    console.log('create WorkflowNotification instance')
    var that = this;
    var $loadingSpinner, $notificationArea;
    var statusListener = {};
    this.messageStack = [];
    this.messageTimer = null;
    this.curHandledNotification = null;
    
    
    this.init = function() {
        $loadingSpinner = $('#loadingArea');
        $notificationArea = $('#notificationArea');
        $("#dialogPopup").css("display", "block");
        $("#dialogPopup").trigger('create');
        $("#dialogPopup").popup();
        $("#dialogPopup").bind({
            popupafterclose: function(event, ui) { 
                //make buttons to size auto again, to later calculate the biggest one
                $("#dialogPopup .ui-btn").each(function(index ){
                     $(this).width("auto");
                })
                
                $("#dialogPopup").removeClass("dialogType_1 dialogType_2 dialogType_3");
                $(".headerbar").removeClass("dialogType_1 dialogType_2 dialogType_3");
                //$("#dialogPopup .ui-bar-buttons").attr("class", "ui-bar-buttons");
                $("#dialogPopup .ui-bar-buttons").removeClass("buttons_1 buttons_2 buttons_3");
                // remove disabled style
                $("#dialogPopup .ui-bar-buttons .ui-btn").removeClass("ui-disabled");
                // remove event listener
                $("#dialogPopup .ui-bar-buttons .ui-btn").off(".dialog");
            }
        });
    }
    
    // registers to notifications which were only send if the app is running
    this.registerForRobotNotifications = function() {
        console.log("registerForRobotNotifications");
        RobotPluginManager.registerNotifications2(that.notificationStatusSuccess, that.notificationStatusError);
    }
    
    //{robotDataKeyId:"robotDataKeyId", robotId:"robotId", robotData:"robotData"}
    this.notificationStatusSuccess = function(result) {
        console.log("notificationStatusSuccess");
        console.log(JSON.stringify(result));
        if(result.robotDataKeyId && result.robotId && result.robotData) {
            var tempRobots = parent.communicationWrapper.getDataValue("robotList");
            var curRobot = parent.communicationWrapper.getDataValue("selectedRobot");
            
            // first check if notification is for current selected robot (due performance reason)
            if(curRobot().robotId && curRobot().robotId() == result.robotId) {
                console.log("notification for current robot")
                switch(result.robotDataKeyId) {
                        case ROBOT_CURRENT_STATE_CHANGED:
                        case ROBOT_STATE_UPDATE:
                            var curState = result.robotData.robotCurrentState || result.robotData.robotStateUpdate;
                            // update state
                            parent.communicationWrapper.updateRobotStateWithCode(curRobot(), curState);
                            break;
                        case ROBOT_NAME_UPDATE:
                            //update name
                            if(result.robotData.robotName) {
                                curRobot().robotName(result.robotData.robotName);
                            }
                            break;
                        case ROBOT_CONNECTED:
                        case ROBOT_DISCONNECTED:
                            var curState = result.robotData.errorDriveResponseCode;
                            curRobot().connectionState(result.robotDataKeyId);
                            parent.communicationWrapper.updateRobotStateWithCode(curRobot(), curState);
                            break;
                        case ROBOT_NOT_CONNECTED:
                            var curState = result.robotData.errorDriveResponseCode;
                            curRobot().connectionState(result.robotDataKeyId);
                            var dialogHeader =  $.i18n.t("messages.not_same_network.title");
                            var dialogText   =  $.i18n.t("messages.not_same_network.message");
                            that.showDialog(dialogType.ERROR, dialogHeader, dialogText, 
                                [{"label":"OK", "callback":function(e){
                                        that.closeDialog();
                                        parent.communicationWrapper.updateRobotStateWithCode(curRobot(), curState);
                                    }
                                }]);
                            
                            break;
                    }
            // loop over robots and update state
            } else {
                // find robote with robotId in global binding object
                $.each(tempRobots(), function(index, item){
                    if(item.robotId() == result.robotId) {
                        switch(result.robotDataKeyId) {
                            case ROBOT_CURRENT_STATE_CHANGED:
                            case ROBOT_STATE_UPDATE:
                                var curState = result.robotData.robotCurrentState || result.robotData.robotStateUpdate;
                                // update state
                                parent.communicationWrapper.updateRobotStateWithCode(item, curState);
                                break;
                            case ROBOT_NAME_UPDATE:
                                //update name
                                if(result.robotData.robotName) {
                                    item.robotName(result.robotData.robotName);
                                }
                                break;
                            case ROBOT_CONNECTED:
                            case ROBOT_DISCONNECTED:
                            case ROBOT_NOT_CONNECTED:
                                var curState = result.robotData.errorDriveResponseCode;
                                parent.communicationWrapper.updateRobotStateWithCode(item, curState);
                                item.connectionState(result.robotDataKeyId);
                                break;
                        }
                        // rest is not relevant for not selected robot
                        return false;
                    }
                });
            }
        }
        //that.showDialog(dialogType.WARNING, "notificationStatusSuccess", JSON.stringify(result));
    }
    
    this.notificationStatusError = function(error) {
        console.log("notificationStatusError " + JSON.stringify(error));
    }
    
    // registers for push notifications which also arrives if the app is not running or in background
    this.registerForRobotMessages = function() {
        console.log("registerForRobotMessages");
        RobotPluginManager.registerForRobotMessages(that.successNotifyPushMessage, that.errorNotifyPushMessage);
    }
    
    this.registerStatus = function(notficationId, callback) {
        if(!statusListener[notficationId]) {
            statusListener[notficationId] = [];
        }
        statusListener[notficationId].push(callback);
    }
    
    this.clearStatusListener = function() {
        statusListener = {};
    }
    //TODO: add check for robotId 
    this.successNotifyPushMessage = function(result) {
        console.log("successNotifyPushMessage " + JSON.stringify(result))
        var tempRobots = parent.communicationWrapper.getDataValue("robotList");
        var curRobot = parent.communicationWrapper.getDataValue("selectedRobot");
        var robotName = "";
        
        if(curRobot().robotId && curRobot().robotId() == result.robotId) {
            robotName = curRobot().robotName();
            console.log("current selected robot: " + robotName);
            that.showPushMessage(robotName,result, true);
        } else if(tempRobots().length > 0) {
            // search in robot list for robotId
            $.each(tempRobots(), function(index, item) {
                if(item.robotId() == result.robotId) {
                    robotName = item.robotName();
                    console.log("found in robot list: " + robotName);
                    return false;
                }
            });
        }
        
        // robot wasn't found in list nor is the current one
        if(robotName == ""){
            // get robot details
            console.log("robot list not loaded yet, request robot details")
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.getRobotDetail, [result.robotId], { type: notificationType.NONE, message: ""}, true);
            tDeffer.done(function(subresult) {
                that.showPushMessage(subresult.robotName, result);
            });
        } else {
            that.showPushMessage(robotName,result, false);
        }
    }
    
    this.showPushMessage = function(robotName, result, isCurRobot) {
        var translatedTitle = $.i18n.t("messages." + result.notificationId + ".title") || "";
        var translatedText = $.i18n.t("messages." + result.notificationId + ".message", {robotName:robotName}) || "";
        
        switch(result.notificationId) {
            case NOTIFICATION_ROBOT_STUCK:
                that.showDialog(dialogType.WARNING, translatedTitle, translatedText);
            break;
            case NOTIFICATION_DIRT_BIN_FULL:
                that.showDialog(dialogType.WARNING, translatedTitle, translatedText);
            break;
            case NOTIFICATION_CLEANING_DONE:
                console.log("NOTIFICATION_CLEANING_DONE")
                if(isCurRobot) {
                    if(statusListener[NOTIFICATION_CLEANING_DONE] && statusListener[NOTIFICATION_CLEANING_DONE].length > 0) {
                        statusListener[NOTIFICATION_CLEANING_DONE][0](translatedText);
                    } else  {
                        that.showLoadingArea(true,notificationType.HINT,translatedText)
                    }
                } else {
                    that.showDialog(dialogType.INFO, translatedTitle, translatedText);
                }
            break;
            default:
                that.showDialog(dialogType.WARNING, "unhandled message", JSON.stringify(result));
        }
    }
    
    
    this.errorNotifyPushMessage = function(error) {
        console.log("errorNotifyPushMessage " + JSON.stringify(error));
    }
    
    /**
     * buttons: [{label:"ok", callback:callbackOk}, {label:"cancel", callback:callbackCancel}] 
     */
    this.showDialog = function(dialogType, textHeadline, textContent, buttons) {
                
        $("#dialogPopup").addClass("dialogType_" + dialogType);
        
        $(".headerbar").addClass("dialogType_" + dialogType);
        
        var popup = $("#dialogPopup");
        popup.find(".ui-bar-buttons")

        $("#dialogPopup .dialogPopupTitle")[0].innerHTML = textHeadline;
        $("#dialogPopup .dialogPopupContent")[0].innerHTML = textContent; 
        
        var maxWidth = 0;
        
        if(typeof buttons != "undefined" && buttons.length > 0) {
            $("#dialogPopup .ui-bar-buttons").addClass("buttons_" + buttons.length);
            $("#dialogPopup .ui-btn").each(function(index ){
                if(index < buttons.length) {
                     $(this).find("span.ui-btn-text").text(buttons[index].label);
                     // check for last button
                     if(index + 1 == buttons.length) {
                         $(this).addClass("ui-last-child");
                     } else {
                         $(this).removeClass("ui-last-child");
                     }
                     
                     //save the max width of the biggest button
                     maxWidth = Math.max(maxWidth, $(this).width());
                              
                     if(typeof buttons[index].callback != "undefined") {
                         $(this).on("click.dialog", function() {
                            // disable button to prevent multiple clicks
                            $(this).addClass("ui-disabled");
                            buttons[index].callback(event);
                            event.preventDefault();
                            event.stopPropagation();
                         });
                    } else {
                         $(this).on("click.dialog", function() {
                            that.closeDialog();
                            event.preventDefault();
                            event.stopPropagation();
                        });
                    }
                } else {
                    return false;
                }
            })
            
            //make all buttons the same width
            $("#dialogPopup .ui-btn").each(function(index ){
                 $(this).width(maxWidth);
            })

            
        } else {
            $("#dialogPopup .ui-bar-buttons").addClass("buttons_1");
            $("#dialogPopup .first-button").addClass("ui-last-child");
            $("#dialogPopup .first-button .ui-btn-text").text("Ok"); 
            $("#dialogPopup .first-button").on("click.dialog", function() {
                that.closeDialog();
            });
        }
        $("#dialogPopup").popup("open");
    }
    
    this.closeDialog = function(dialogType) {
        $("#dialogPopup").popup("close");
    }
    
    
    this.showNotification = function() {
        
    }
    // show each notification at least about 2s
    this.handleStack = function() {
        // check if currently a message is shown
        if(that.curHandledNotification == null && that.messageStack.length > 0) {
            // show new message from stack
            that.curHandledNotification = that.messageStack.shift();
            $notificationArea.notificationbar("show", that.curHandledNotification.message, that.curHandledNotification.type);
            that.messageTimer = window.setTimeout(function() { that.tryToHideNotification()}, 2000);
        }
    }
    
    this.tryToHideNotification = function() {
        if(that.curHandledNotification != null) {
            if(that.curHandledNotification.finished) {
                window.clearTimeout(that.messageTimer);
                $notificationArea.notificationbar("hide", that.curHandledNotification.type);
                that.curHandledNotification = null;
                that.handleStack();
            } else {
                that.messageTimer = window.setTimeout(function() { that.tryToHideNotification()}, 2000);
            }
        }
    }
    
    this.notificationDone = function(callGuid) {
        // first check if current handled notification is the on to finish
        if(that.curHandledNotification != null && that.curHandledNotification.callGuid == callGuid) {
            that.curHandledNotification.finished = true;
        } else {
            // loop through array and find notification
            $.each(that.messageStack, function(index, item) {
                if(item.callGuid == callGuid) {
                    item.finished = true;
                    return false
                }
            });
        }
        
    }
    
    /**
     * Shows a loading spinner indicating background activity.
     * There are two types of spinner. One is shown on the center of the screen and has no text.
     * The other one is a notification bar on top of the screen (below the header)
     * @param {boolean} show Flag to determine if the area should be shown or not
     * @param {enumeration} type The notificationType enumeration that determines which kind of indicator should be shown.
     * @param {string} message The message to be displayed when using OPERATION or HINT notification types.
     * @param {string} callGuid guid of the communication call which triggered a notification (default is empty)
     */
    this.showLoadingArea = function(show, type, message, callGuid) {
        console.log("showLoadingArea show: " + show + " type: " + type + " callGuid: " + callGuid||"");
        
        if (show) {
            // show the simple spinner by default
            if (!type) {
                $loadingSpinner.show();
            }
            //cue the page loader
            switch(type) {
                case notificationType.SPINNER:
                    $loadingSpinner.show();
                    break;
                case notificationType.OPERATION:
                case notificationType.GETREADY:
                case notificationType.WAKEUP:
                    that.messageStack.push({type:type,message:message,callGuid:callGuid || "",finished:false})
                    that.handleStack();
                    break;
                case notificationType.HINT:
                    that.messageStack.push({type:type,message:message,callGuid:callGuid || "",finished:true})
                    that.handleStack();
                    break;
            }
        } else {
            // hide the simple spinner by default
            if (!type) {
                $loadingSpinner.hide();
            }
            switch(type) {
                case notificationType.SPINNER:
                    $loadingSpinner.hide();
                    break;
                case notificationType.OPERATION:
                case notificationType.GETREADY:
                case notificationType.WAKEUP:
                    that.notificationDone(callGuid||"");
                    break;
            }
        }
    }
    
    /**
     * hides notifications and clears message stack 
     */
    this.reset = function() {
        window.clearTimeout(that.messageTimer);
        that.curHandledNotification = null;
        that.messageStack.length = 0;
        $notificationArea.notificationbar("hide", true);
    }
    
    
    /**
     * Shows an error message on the screen (and blocks all other interaction).
     * Whenever there's an error (i.e. "Wifi lost", "User name not valid") there will be an error Popup
     * in the middle of the screen with an "OK" button to be dismissed by the user. The other content on the screen is blocked.
     */
    //TODO: add error codes switch
    this.showError = function(error) {
        if (error && error.errorMessage) {
            that.showDialog(dialogType.ERROR, "Communication Error", error.errorMessage);
            if (error.errorCode) {
                console.log("error: " + error.errorCode + " msg: " + error.errorMessage);
            } else {
                console.log("error msg: " + error.errorMessage + "\n error object: " + JSON.stringify(error));
            }
        } else {
            that.showDialog(dialogType.ERROR, "Communication Error", "An Error occurred while contacting the server!");
            console.log("error object: " + JSON.stringify(error));
        }
    }
}