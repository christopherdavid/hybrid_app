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
    
    
    this.init = function() {
        $loadingSpinner = $('#loadingArea');
        $notificationArea = $('#notificationArea');
        $("#dialogPopup").css("display", "block");
        $("#dialogPopup").trigger('create');
        $("#dialogPopup").popup();
        $("#dialogPopup").bind({
            popupafterclose: function(event, ui) { 
                $("#dialogPopup").removeClass("dialogType_1");
                $("#dialogPopup").removeClass("dialogType_2");
                $("#dialogPopup").removeClass("dialogType_3");
                $("#dialogPopup .ui-bar-buttons").removeClass("buttons_0");
                $("#dialogPopup .ui-bar-buttons").removeClass("buttons_1");
                $("#dialogPopup .ui-bar-buttons").removeClass("buttons_2");
                $("#dialogPopup .ui-bar-buttons").removeClass("buttons_3");
            }
        });
    }
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
    
    this.successNotifyPushMessage = function(result) {
        console.log("successNotifyPushMessage " + JSON.stringify(result))
        var translatedTitle = $.i18n.t("messages." + result.notificationId + ".title") || "";
        var translatedText = $.i18n.t("messages." + result.notificationId + ".message") || "";
        
        switch(result.notificationId) {
            case NOTIFICATION_ROBOT_STUCK:
                that.showDialog(dialogType.WARNING, translatedTitle, translatedText);
            break;
            case NOTIFICATION_DIRT_BIN_FULL:
                that.showDialog(dialogType.WARNING, translatedTitle, translatedText);
            break;
            case NOTIFICATION_CLEANING_DONE:
                console.log("NOTIFICATION_CLEANING_DONE")
                if(statusListener[NOTIFICATION_CLEANING_DONE] && statusListener[NOTIFICATION_CLEANING_DONE].length > 0) {
                    statusListener[NOTIFICATION_CLEANING_DONE][0](translatedText);
                } else  {
                    that.showLoadingArea(true,notificationType.HINT,translatedText)
                }
            break;
        }
    }
    
    this.errorNotifyPushMessage = function(result) {
        
    }
    
    /**
     * buttons: [{label:"ok", callback:callbackOk}, {label:"cancel", callback:callbackCancel}] 
     */
    this.showDialog = function(dialogType, textHeadline, textContent, buttons) {
                
        $("#dialogPopup").addClass("dialogType_" + dialogType);
        
        var popup = $("#dialogPopup");
        popup.find(".ui-bar-buttons")

        $("#dialogPopup .dialogPopupTitle")[0].innerHTML = textHeadline;
        $("#dialogPopup .dialogPopupContent")[0].innerHTML = textContent; 
        
        if(typeof buttons != "undefined" && buttons.length > 0) {
            $("#dialogPopup .ui-bar-buttons").addClass("buttons_" + buttons.length);
            $("#dialogPopup .ui-btn").each(function(index ){
                if(index < buttons.length) {
                     $(this).find("span.ui-btn-text").text(buttons[index].label);
                     $(this).click(function (e) {
                        console.log("button " + index)
                        buttons[index].callback(e);
                    });
                } else {
                    return false;
                }
            })
        } else {
            $("#dialogPopup .ui-bar-buttons").addClass("buttons_1");
            $("#dialogPopup .first-button .ui-btn-text").text("Ok"); 
            $("#dialogPopup .first-button").click(function (e) {
                that.closeDialog();
            });
        }
        $("#dialogPopup").popup("open");
    }
    
    this.closeDialog = function() {
        $("#dialogPopup").popup("close"); 
    }
    
    
    /**
     * Shows a loading spinner indicating background activity.
     * There are two types of spinner. One is shown on the center of the screen and has no text.
     * The other one is a notification bar on top of the screen (below the header)
     * @param {boolean} show Flag to determine if the area should be shown or not
     * @param {enumeration} type The notificationType enumeration that determines which kind of indicator should be shown.
     * @param {string} message The message to be displayed when using OPERATION or HINT notification types.
     * @param {boolean} force Flag that forces the action (i.e. hide the notification)
     */
    this.showLoadingArea = function(show, type, message, force) {
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
                case notificationType.HINT:
                    $notificationArea.notificationbar("show", message, type);
                    break;
            }
        } else {

            if (force) {
                // hide all notification displays
                $loadingSpinner.hide();
                $notificationArea.notificationbar("hide", type, force);
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
                    case notificationType.HINT:
                        $notificationArea.notificationbar("hide", type);
                        break;
                }
            }
        }
    }
    
    
    /**
     * Shows an error message on the screen (and blocks all other interaction).
     * Whenever there's an error (i.e. "Wifi lost", "User name not valid") there will be an error Popup
     * in the middle of the screen with an "OK" button to be dismissed by the user. The other content on the screen is blocked.
     */
    this.showError = function(error) {//errorTitle, errorText, callback) {

        //TODO fix that! there is no result...
        if (error && error.errorMessage) {
            alert("An Error occurred while contacting the server:\n" + error.errorMessage);
            if (error.errorCode) {
                console.log("error: " + error.errorCode + " msg: " + error.errorMessage);
            } else {
                console.log("error msg: " + error.errorMessage + "\n error object: " + JSON.stringify(error));
            }
        } else {
            alert("An Error occurred while contacting the server!");
            console.log("error object: " + JSON.stringify(error));
        }
    }
}
