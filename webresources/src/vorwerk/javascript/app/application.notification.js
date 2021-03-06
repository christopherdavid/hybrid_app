/**
 * WorkflowNavigator stores the view model in history.
 * @class Represents a WorkflowNavigator
 * @param {object} parent Reference to the parent object.
 * @param {object} parent Reference to the parent workflow object.
 * @param {object} parent Reference to the parent dataTemplate object.
 */
function WorkflowNotification(parent) {
    console.log('create WorkflowNotification instance');
    var that = this;
    var $loadingSpinner, $notificationArea;
    var statusListener = {};
    this.messageStack = [];
    this.messageTimer = null;
    this.curHandledNotification = null;
    this.dialogStack = [];
    this.curHandledDialog = null;
    
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
                });
                
                $("#dialogPopup").removeClass("dialogType_1 dialogType_2 dialogType_3");
                $(".headerbar").removeClass("dialogType_1 dialogType_2 dialogType_3");
                //$("#dialogPopup .ui-bar-buttons").attr("class", "ui-bar-buttons");
                $("#dialogPopup .ui-bar-buttons").removeClass("buttons_1 buttons_2 buttons_3");
                // remove disabled style
                $("#dialogPopup .ui-bar-buttons .ui-btn").removeClass("ui-disabled");
                // remove event listener
                $("#dialogPopup .ui-bar-buttons .ui-btn").off(".dialog");
                
                // check if there is a closeCallback registered
                if(that.curHandledDialog.closeCallback != null && typeof that.curHandledDialog.closeCallback == "function") {
                    that.curHandledDialog.closeCallback();
                }
                
                that.curHandledDialog = null;
                that.handleDialogStack();
            }
        });
    };
    
    // registers to notifications which were only send if the app is running
    this.registerForRobotNotifications = function() {
        console.log("registerForRobotNotifications");
        RobotPluginManager.registerNotifications2(that.notificationStatusSuccess, that.notificationStatusError);
    };
    
    //{robotDataKeyId:"robotDataKeyId", robotId:"robotId", robotData:"robotData"}
    this.notificationStatusSuccess = function(result) {
        console.log("notificationStatusSuccess");
        console.log(JSON.stringify(result));
        if(result.robotDataKeyId && result.robotId && result.robotData) {
            var tempRobots = parent.communicationWrapper.getDataValue("robotList");
            var curRobot = parent.communicationWrapper.getDataValue("selectedRobot");
            var curState = null;
            if(result.robotData.robotCurrentState) {
                curState = result.robotData.robotCurrentState;
            } else if (result.robotData.robotCurrentStateDetails && result.robotData.robotCurrentStateDetails.robotCurrentState) {
                curState = result.robotData.robotCurrentStateDetails.robotCurrentState;
            }  
            
            // first check if notification is for current selected robot (due performance reason)
            if(curRobot().robotId && curRobot().robotId() == result.robotId) {
                console.log("notification for current robot");
                
                // parse robotStateParams
                if(isDefined(result.robotData, "robotCurrentStateDetails.robotStateParams")) {
                    parent.communicationWrapper.parseStateParameters(curRobot(), result.robotData.robotCurrentStateDetails.robotStateParams);
                }
                
                switch(result.robotDataKeyId) {
                        case ROBOT_CURRENT_DATA_CHANGED:
                            // check if state really changed 
                            console.log("app robot state:"+ curRobot().robotCurrentState());
                            console.log("notification robot state:"+ curState);
                            if(curState && (curState != curRobot().robotCurrentState())) {
                                // if there is a notification set robot back to online
                                curRobot().robotOnline(true);
                                curRobot().visualOnline(true);
                            
                                // state changed resolve wait deffer 
                                robotUiStateHandler.resolveWaitDeffer();
                            
                                if(curState == ROBOT_STATE_CLEANING) {
                                    // getRobotData: robotCurrentCleaningDetails, robotConfigInfo 
                                    var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.getRobotData, [curRobot().robotId(), ['robotCurrentCleaningDetails','robotConfigInfo']], { type: notificationType.NONE });
                                    tDeffer.done(function(categoryResult) {
                                        console.log("getRobotDataSuccess : " + JSON.stringify(categoryResult));
                                        // need to add a check if it's a valid category (in some cases got 0 from server)
                                        console.log("Cleaning Category : " + categoryResult.robotProfileData.robotCurrentCleaningDetails.robotCleaningCategory);
                                        
                                        if(categoryResult.robotProfileData.robotCurrentCleaningDetails.robotCleaningCategory == CLEANING_CATEGORY_MANUAL || categoryResult.robotProfileData.robotCurrentCleaningDetails.robotCleaningCategory == CLEANING_CATEGORY_ALL
                                            || categoryResult.robotProfileData.robotCurrentCleaningDetails.robotCleaningCategory == CLEANING_CATEGORY_SPOT) {
                                                curRobot().cleaningCategory(categoryResult.robotProfileData.robotCurrentCleaningDetails.robotCleaningCategory);
                                        } else {
                                            // set All as fallback
                                            curRobot().cleaningCategory(CLEANING_CATEGORY_ALL);
                                        }
                                        // update clock set
                                        if(categoryResult.robotProfileData.robotConfigInfo){
                                            var configInfo =  categoryResult.robotProfileData.robotConfigInfo;
                                            console.log("Clock Set Value :"+ JSON.stringify(categoryResult.robotProfileData.robotConfigInfo));
                                            if(typeof configInfo.ClkIsSet != "undefined") {
                                                curRobot().clockIsSet(parseInt(configInfo.ClkIsSet, 10));
                                            }
                                        }
                                        // update state
                                        parent.communicationWrapper.updateRobotStateWithCode(curRobot(), curState);
                                        
                                    });
                                    
                                    tDeffer.fail(function(categoryError) {
                                        // set All as fallback
                                        curRobot().cleaningCategory(CLEANING_CATEGORY_ALL);
                                        // update state
                                        parent.communicationWrapper.updateRobotStateWithCode(curRobot(), curState);
                                    });
                                } else {
                                    
                                    // update state
                                    parent.communicationWrapper.updateRobotStateWithCode(curRobot(), curState);
                                }
                            } else {
                                // refresh states if UI is not connecting nor waiting nor offline
                                if(curRobot().robotOnline() && robotUiStateHandler.current().ui() != ROBOT_UI_STATE_CONNECTING && robotUiStateHandler.current().ui() != ROBOT_UI_STATE_WAIT) {
                                    robotUiStateHandler.setVisualState(curRobot().robotCurrentState());
                                    robotUiStateHandler.resolveWaitDeffer();
                                }
                            }
                            
                            break;
                        case ROBOT_NAME_UPDATE:
                            //update name
                            if(result.robotData.robotName) {
                                curRobot().robotName(result.robotData.robotName);
                                if(robotUiStateHandler.current().ui() == ROBOT_UI_STATE_ROBOT_OFFLINE) {
                                    curRobot().displayName(result.robotData.robotName + " (" + $.i18n.t("robotStateCodes." + visualState[ROBOT_UI_STATE_ROBOT_OFFLINE]) + ")");
                                } else if(robotUiStateHandler.current().ui() == ROBOT_USER_MENU_STATE) {
                                    curRobot().displayName(result.robotData.robotName + " (" + $.i18n.t("robotStateCodes." + visualState[ROBOT_USER_MENU_STATE]) + ")");
                                } else {
                                    curRobot().displayName(result.robotData.robotName);
                                }
                            }
                            break;
                        case ROBOT_CONNECTED:
                            // if there is a notification set robot back to online
                            curRobot().robotOnline(true);
                            curRobot().visualOnline(true);
                            
                            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.startCleaning, [curRobot().robotId(), CLEANING_CATEGORY_MANUAL, 1, 1], { type: notificationType.NONE, message: ""});
                            tDeffer.done(function(result){
                                console.log("startManualModeSuccess" + JSON.stringify(result));
                            });
                            break;
                        case ROBOT_ONLINE_STATUS_CHANGED:
                            // attention: also been called periodically even if state didn't change
                            var onlineStatus = result.robotData.online;
                            console.log("Current Online Status data:" + JSON.stringify(result.robotData));
                            robotUiStateHandler.rejectWaitDeffer();
                            parent.communicationWrapper.updateRobotOnlineState(curRobot(), onlineStatus);
                            break;
                        case ROBOT_DISCONNECTED:
                            curRobot().connectionState(result.robotDataKeyId);
                            // if there is a notification set robot back to online
                            curRobot().robotOnline(true);
                            curRobot().visualOnline(true);
                            robotUiStateHandler.setVisualState(ROBOT_STATE_IDLE);
                            break;
                        case ROBOT_NOT_CONNECTED:
                            curRobot().connectionState(result.robotDataKeyId);
                            var dialogHeader =  $.i18n.t("messages.not_same_network.title");
                            var dialogText   =  $.i18n.t("messages.not_same_network.message");
                            that.showDialog(dialogType.ERROR, dialogHeader, dialogText, 
                                [{"label":$.i18n.t("common.ok"), "callback":function(e){
                                        that.closeDialog();
                                        // if there is a notification set robot back to online
                                        curRobot().robotOnline(true);
                                        curRobot().visualOnline(true);
                                        robotUiStateHandler.setVisualState(ROBOT_STATE_IDLE);
                                    }
                                }]);
                            
                            break;
                        // enable or disable scheduler
                        case ROBOT_SCHEDULE_STATE_CHANGED:
                            // convert to boolean no matter if result state is a string or boolean 
                            var scheduleState = result.robotData.scheduleState == 'false' ? !result.robotData.scheduleState : !!result.robotData.scheduleState; 
                            that.handleStatusListener(ROBOT_SCHEDULE_STATE_CHANGED, scheduleState);
                            break;
                        // schedule data updated
                        case ROBOT_SCHEDULE_UPDATED:
                            that.handleStatusListener(ROBOT_SCHEDULE_UPDATED, true);
                            break;
                        case ROBOT_COMMAND_FAILED:
                            console.log("ROBOT_COMMAND_FAILED "  + curRobot().crntErrorCode());
                            // show timeout message
                            var dialogHeader =  $.i18n.t("messages.waiting_timeout.title");
                            var dialogText =  $.i18n.t("messages.waiting_timeout.message");
                            var dialogTypeId = dialogType.WARNING;
                            // refresh states
                            robotUiStateHandler.setVisualState(curRobot().robotCurrentState());
                            robotUiStateHandler.rejectWaitDeffer();
                            // command failed, show last error code or timeout message 
                            if(curRobot().crntErrorCode() != 0 && curRobot().crntErrorCode() != ROBOT_UI_ERRORALERT_CLEAR  && curRobot().crntErrorCode() != 21236) {
                                var testTitle =  $.i18n.t("error.-" + curRobot().crntErrorCode() +".title");
                                // check if translation has been found for errorCode
                                if(testTitle.indexOf(curRobot().crntErrorCode()) == -1) {
                                    dialogTypeId = dialogType.ERROR;
                                    dialogHeader = testTitle;
                                    dialogText   =  $.i18n.t("error.-" + curRobot().crntErrorCode() +".message");
                                }
                            }
                            that.showDialog(dialogTypeId, dialogHeader, dialogText); 
                            break;
                    }
            // loop over robots and update state
            } else {
                // find robote with robotId in global binding object
                $.each(tempRobots(), function(index, item){
                    if(item.robotId() == result.robotId) {
                        // parse robotStateParams
                        if(isDefined(result.robotData, "robotCurrentStateDetails.robotStateParams")) {
                            parent.communicationWrapper.parseStateParameters(item, result.robotData.robotCurrentStateDetails.robotStateParams);
                        }
                          
                        switch(result.robotDataKeyId) {
                            case ROBOT_CURRENT_DATA_CHANGED:
                                // if there is a notification set robot back to online
                                item.robotOnline(true);
                                item.visualOnline(true);
                                // update state
                                parent.communicationWrapper.updateRobotStateWithCode(item, curState);
                                break;
                            case ROBOT_NAME_UPDATE:
                                //update name
                                if(result.robotData.robotName) {
                                    item.robotName(result.robotData.robotName);
                                    item.displayName(result.robotData.robotName);
                                }
                                break;
                            case ROBOT_ONLINE_STATUS_CHANGED:
                                // attention: also been called periodically even if state didn`t change
                                var onlineStatus = result.robotData.online;
                                console.log("Current Online Status data:" + JSON.stringify(result.robotData));
                                parent.communicationWrapper.updateRobotOnlineState(item, onlineStatus);
                                break;
                            case ROBOT_CONNECTED:
                            case ROBOT_DISCONNECTED:
                            case ROBOT_NOT_CONNECTED:
                                // if there is a notification set robot back to online
                                item.robotOnline(true);
                                item.visualOnline(true);
                                parent.communicationWrapper.updateRobotStateWithCode(item, ROBOT_STATE_IDLE);
                                item.connectionState(result.robotDataKeyId);
                                break;
                        }
                        // rest is not relevant for not selected robot
                        return false;
                    }
                });
            }
        }
    };
    
    this.notificationStatusError = function(error) {
        console.log("notificationStatusError " + JSON.stringify(error));
    };
    
    // registers for push notifications which also arrives if the app is not running or in background
    this.registerForRobotMessages = function() {
        console.log("registerForRobotMessages");
        RobotPluginManager.registerForRobotMessages(that.successNotifyPushMessage, that.errorNotifyPushMessage);
    };
    
    this.handleStatusListener = function(state, result) {
        if(statusListener[state] && statusListener[state].length > 0) {
            for(var i = 0; i < statusListener[state].length; i++) {
                console.log("found call callback with result: " + result);
                statusListener[state][i](result);
            }
        }
    };
    
    this.registerStatus = function(notficationId, callback) {
        if(!statusListener[notficationId]) {
            statusListener[notficationId] = [];
        }
        statusListener[notficationId].push(callback);
    };
    
    this.clearStatusListener = function() {
        statusListener = {};
    };
    
    this.successNotifyPushMessage = function(result) {
        console.log("successNotifyPushMessage " + JSON.stringify(result));
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
                    that.showPushMessage(robotName,result, false);
                    return false;
                }
            });
        }
        
        // robot wasn't found in list nor is the current one
        if(robotName == ""){
            // get robot details
            console.log("robot list not loaded yet, request robot details");
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.getRobotDetail, [result.robotId], { type: notificationType.NONE, message: ""}, true);
            tDeffer.done(function(subresult) {
                that.showPushMessage(subresult.robotName, result);
            });
        }
    };
    
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
                console.log("NOTIFICATION_CLEANING_DONE");
                var tempRobots = parent.communicationWrapper.getDataValue("robotList");
                $.each(tempRobots(), function(index, item){
                    if(item.robotId() == result.robotId) {
                        // if there is a notification set robot back to online
                        item.robotOnline(true);
                        item.visualOnline(true);
                        parent.communicationWrapper.updateRobotStateWithCode(item, ROBOT_STATE_IDLE);
                        return false;
                    }
                });
                if(isCurRobot) {
                    that.showLoadingArea(true,notificationType.HINT,translatedTitle);
                } else {
                    that.showDialog(dialogType.INFO, translatedTitle, translatedText);
                }
            break;
            case NOTIFICATION_ROBOT_CANCEL:
                that.showLoadingArea(true,notificationType.HINT,translatedText);
            break;
            case "22212":
                 that.showDialog(dialogType.ERROR, translatedTitle, translatedText);
                   var tempRobots = parent.communicationWrapper.getDataValue("robotList");
                    $.each(tempRobots(), function(index, item){
                    if(item.robotId() == result.robotId) {
                        // if there is a notification set robot back to online
                        item.robotOnline(true);
                        item.visualOnline(true);
                        parent.communicationWrapper.updateRobotStateWithCode(item, ROBOT_STATE_IDLE);
                        return false;
                    }
                });
            break;
            
            default:
                that.showDialog(dialogType.WARNING, "unhandled message", JSON.stringify(result));
        }
    };
    
    this.errorNotifyPushMessage = function(error) {
        console.log("errorNotifyPushMessage " + JSON.stringify(error));
    };
    
    /**
     * buttons: [{label:"ok", callback:callbackOk}, {label:"cancel", callback:callbackCancel}] 
     */
    this.showDialog = function(dialogType, textHeadline, textContent, buttons, closeCallback) {
        // wrap parameter in object and add type "js"
        that.dialogStack.push({
            "type":"js",
            "closeable": false,
            "onShow": null,
            "id":"#dialogPopup",
            "dialogType":dialogType,
            "textHeadline":textHeadline,
            "textContent":textContent,
            "buttons":buttons,
            "closeCallback":closeCallback||null
        });
        that.handleDialogStack();
    };
    
    this.showDomDialog = function(domId, blnCloseable, onShow) {
        blnCloseable = typeof blnCloseable != "undefined" ? blnCloseable :  false;
        onShow = typeof onShow != "undefined" ? onShow :  null;
        // wrap parameter in object and add type "dom"
        that.dialogStack.push({
            "type":"dom",
            "id":domId,
            "closeable": blnCloseable,
            "onShow": onShow
        });
        that.handleDialogStack();
    };
    
    this.handleDialogStack = function() {
        // check if currently a dialog is shown and could be closed
        if(that.curHandledDialog != null && that.curHandledDialog.closeable) {
            $(that.curHandledDialog.id).popup("close");
        } else if(that.curHandledDialog == null && that.dialogStack.length > 0) {
            that.curHandledDialog = that.dialogStack.shift();
            
            // show new message from stack
            if(that.curHandledDialog.type == "dom") {
                $(that.curHandledDialog.id).on( "popupafterclose", function( event, ui ) {
                    // remove event listener
                    $(that.curHandledDialog.id).off("popupafterclose");
                    that.curHandledDialog = null;
                    that.handleDialogStack();
                } );
                $(that.curHandledDialog.id).popup("open");
                if(that.curHandledDialog.onShow != null && typeof that.curHandledDialog.onShow == "function") {
                    that.curHandledDialog.onShow();
                }
            } else {
                that.showDialogWindow(that.curHandledDialog.dialogType,
                    that.curHandledDialog.textHeadline,
                    that.curHandledDialog.textContent,
                    that.curHandledDialog.buttons);
            }
        }
    };
    
    this.showDialogWindow = function(dialogType, textHeadline, textContent, buttons) {
        $("#dialogPopup").addClass("dialogType_" + dialogType);
        $(".headerbar").addClass("dialogType_" + dialogType);
        
        var popup = $("#dialogPopup");
        popup.find(".ui-bar-buttons");

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
            });
            
            //make all buttons the same width
            $("#dialogPopup .ui-btn").each(function(index ){
                 $(this).width(maxWidth);
            });

            
        } else {
            $("#dialogPopup .ui-bar-buttons").addClass("buttons_1");
            $("#dialogPopup .first-button").addClass("ui-last-child");
            $("#dialogPopup .first-button .ui-btn-text").text($.i18n.t("common.ok")); 
            $("#dialogPopup .first-button").on("click.dialog", function() {
                that.closeDialog();
            });
        }
        $("#dialogPopup").popup("open");
    };
    
    this.closeDialog = function(dialogType) {
        $("#dialogPopup").popup("close");
       
    };
    
    this.forceCloseDialog = function() {
        if(that.curHandledDialog != null) {
            // clear dialog stack and close current dialog
           console.log("Popup ID : "+ that.curHandledDialog.id);
           that.dialogStack.length = 0;
            if(that.curHandledDialog.id != "#spotSize"){
            	$(that.curHandledDialog.id).popup("close");
            }
            else {
            	$("#dialogPopup").popup("close");
            }
        } 
    };
    
    this.isDialogOpen = function() {
        return (that.curHandledDialog != null);
    };
        
    this.showNotification = function() {
        
    };
    
    // show each notification at least about 2s
    this.handleStack = function() {
        // check if currently a message is shown
        if(that.curHandledNotification == null && that.messageStack.length > 0) {
            // show new message from stack
            that.curHandledNotification = that.messageStack.shift();
            $notificationArea.notificationbar("show", that.curHandledNotification.message, that.curHandledNotification.type);
            that.messageTimer = window.setTimeout(function() { that.tryToHideNotification(); }, 2000);
        }
    };
    
    this.tryToHideNotification = function() {
        if(that.curHandledNotification != null) {
            if(that.curHandledNotification.finished) {
                window.clearTimeout(that.messageTimer);
                $notificationArea.notificationbar("hide", that.curHandledNotification.type);
                that.curHandledNotification = null;
                that.handleStack();
            } else {
                that.messageTimer = window.setTimeout(function() { that.tryToHideNotification(); }, 2000);
            }
        }
    };
    
    this.notificationDone = function(callGuid) {
        // first check if current handled notification is the on to finish
        if(that.curHandledNotification != null && that.curHandledNotification.callGuid == callGuid) {
            that.curHandledNotification.finished = true;
        } else {
            // loop through array and find notification
            $.each(that.messageStack, function(index, item) {
                if(item.callGuid == callGuid) {
                    item.finished = true;
                    return false;
                }
            });
        }
    };
    
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
            // cue the page loader
            switch(type) {
                case notificationType.SPINNER:
                    $loadingSpinner.show();
                    break;
                case notificationType.OPERATION:
                case notificationType.GETREADY:
                case notificationType.WAKEUP:
                    that.messageStack.push({type:type,message:message,callGuid:callGuid || "",finished:false});
                    that.handleStack();
                    break;
                case notificationType.HINT:
                    that.messageStack.push({type:type,message:message,callGuid:callGuid || "",finished:true});
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
    };
    
    /**
     * hides notifications and clears message stack 
     */
    this.reset = function() {
        window.clearTimeout(that.messageTimer);
        that.curHandledNotification = null;
        that.messageStack.length = 0;
        $notificationArea.notificationbar("hide", true);
    };
    
    /**
     * Shows an error message on the screen (and blocks all other interaction).
     * Whenever there's an error (i.e. "Wifi lost", "User name not valid") there will be an error Popup
     * in the middle of the screen with an "OK" button to be dismissed by the user. The other content on the screen is blocked.
     */
    this.showError = function(error) {
        var errorTitle = $.i18n.t("error.-501.title");
        var errorMessage = $.i18n.t("error.-501.message");
        var bShowDialog = true;
        if (error && error.errorCode) {
            console.log("errorCode: " + error.errorCode);
            var curRobot = parent.communicationWrapper.getDataValue("selectedRobot");
            var testTitle =  $.i18n.t("error." + error.errorCode +".title");
            
            // check if translation has been found for errorCode
            if(testTitle.indexOf(error.errorCode) == -1) {
                // replace it with default text
                errorTitle = testTitle;
                if(curRobot().robotName && typeof curRobot().robotName == "function" ) {
                    errorMessage =  $.i18n.t("error." + error.errorCode + ".message", {robotName:curRobot().robotName()});
                } else {
                    errorMessage =  $.i18n.t("error." + error.errorCode + ".message", {robotName:""});
                }
            }
            
            
            switch(error.errorCode) {
                case ERROR_ROBOT_NOT_PEER_CONNECTED:
                    // if there is a notification set robot back to online
                    curRobot().robotOnline(true);
                    curRobot().visualOnline(true);
                    parent.communicationWrapper.updateRobotStateWithCode(curRobot(), ROBOT_STATE_IDLE);
                    break;
                case ROBOT_ALREADY_CONNECTED:
                    curRobot().robotCurrentState(ROBOT_STATE_MANUAL_CLEANING);
                    break;
                case ERROR_NO_CLEANING_STATE_SET:
                    // don't show error message
                    bShowDialog = false;
                    break;
            }
        } else if (error && error.errorMessage) {
            console.log("errorMessage: " + error.errorMessage);
            errorMessage = error.errorMessage;
        } else {
            console.log("unhandled error");
        }
        if(bShowDialog) {
            that.showDialog(dialogType.ERROR, errorTitle, errorMessage);
        }
    };
}
