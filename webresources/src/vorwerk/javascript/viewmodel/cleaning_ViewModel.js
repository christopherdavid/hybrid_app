resourceHandler.registerFunction('cleaning_ViewModel.js', function(parent) {
    console.log('instance created for: cleaning_ViewModel');
    var that = this, $spotPopup, 
        $spotResizer,$spotSelection,
        spotGridSize = {
            cellWidth:deviceSize.getResolution() == "high" ? 39 : 24,
            cellHeight:deviceSize.getResolution() == "high" ? 78 : 48,
            maxWidth:deviceSize.getResolution() == "high" ? 396 : 244,
            gridSpace:deviceSize.getResolution() == "high" ? 3 : 2,
            maxCol:4,
            maxRow:4
        },
        spotFactor = parseInt($.i18n.t("pattern.spotFactor"),10),
        spotUnit = $.i18n.t("pattern.spotUnit"),
        subscribeCategory, subscribeOnline;
    
    this.conditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    
    this.startAreaControl = null;
    
    this.currentUiState = robotUiStateHandler.current;
    
    this.visualSelectedCategory = ko.observable();
    
    // listener when robot category changes
    subscribeCategory = this.robot().cleaningCategory.subscribe(function(newValue) {
            console.log("cleaningCategory subscribe " + newValue);
            if(newValue != this.visualSelectedCategory() && that.robot().visualOnline()) {
                that.visualSelectedCategory(newValue);
                // close all dialogs
                parent.notification.forceCloseDialog();
            }
        }, this);
        
    // listener when robot online status changed    
    subscribeOnline = this.robot().visualOnline.subscribe(function(newValue) {
            if(!newValue) {
                // deselect category for offline mode
                that.visualSelectedCategory(-1);
                // close all dialogs
                parent.notification.forceCloseDialog();
            } else {
                that.visualSelectedCategory(that.robot().cleaningCategory());
            }
        }, this);
    
    // set cleaning mode "non-eco" as default
    this.ecoMode = ko.observable(false);
    
    this.cleaningMode = ko.observableArray([{
            id : CLEANING_MODE_ECO,
            text : $.i18n.t("common.cleaningMode." + keyString[CLEANING_MODE_ECO])
        }, {
            id : CLEANING_MODE_NORMAL,
            text : $.i18n.t("common.cleaningMode." + keyString[CLEANING_MODE_NORMAL])
        }]);
            
    
    this.robotState = ko.observable("");
    
    this.waitingForRobot = ko.computed(function() {
         return (!that.robot().visualOnline() || that.currentUiState().ui() == ROBOT_UI_STATE_CONNECTING || that.currentUiState().ui() == ROBOT_UI_STATE_WAIT);
    }, this);
    
    this.isRemoteEnabled = ko.computed(function() {
         return (that.robot().visualOnline() && that.visualSelectedCategory() == CLEANING_CATEGORY_MANUAL && that.currentUiState().robot() == ROBOT_STATE_MANUAL_CLEANING);
    }, this);
    
    this.isSendToBaseVisible = ko.computed(function() {
         // #270 Hide the send to base on manual category
         return (that.visualSelectedCategory() == CLEANING_CATEGORY_MANUAL);
    }, this);
    
    this.isSendToBaseEnabled = ko.computed(function() {
        return (!that.waitingForRobot() && (that.currentUiState().robot() == ROBOT_STATE_PAUSED ));
    }, this);
    
    this.isStopEnabled = ko.computed(function() {
        return (!that.waitingForRobot() && (that.currentUiState().robot() == ROBOT_STATE_CLEANING || 
                                             that.currentUiState().robot() == ROBOT_STATE_PAUSED   || 
                                             that.currentUiState().robot() == ROBOT_STATE_DOCK_PAUSED   ||
                                             that.currentUiState().robot() == ROBOT_STATE_MANUAL_CLEANING));
    }, this);
    
    this.isEcoEnabled = ko.computed(function() {
        return (!that.waitingForRobot() && (that.currentUiState().robot() != ROBOT_STATE_CLEANING && 
                                             that.currentUiState().robot() != ROBOT_STATE_PAUSED   && 
                                             that.currentUiState().robot() != ROBOT_STATE_MANUAL_CLEANING ));
    }, this);
    
    this.selectedFrequency = ko.observable("1");
    
        
    /**
     * Called when the viewmodel is initialized (after the view has been loaded, before bindings are applied)
     */
    this.init = function() {
        // check if country is italy. if so change product logo
        var user = parent.communicationWrapper.getDataValue("user");
        var uCountryCode = (user.extra_param && user.extra_param.countryCode) ? user.extra_param.countryCode : null;
        if(uCountryCode != null && uCountryCode == "IT") {
            $(document).one("pageshow.menuPopup", function(e) {
                $("#menuPopupLogo").addClass("folletto");
            });
        }
        
        parent.orientation.landscape = false;
        forceRotation('portrait');
        $spotResizer = $("#spotResizer");
        $spotResizer.draggable({
            grid : [spotGridSize.cellWidth,spotGridSize.cellHeight],
            containment: "parent",
            scroll:false,
            start : function(event, ui) {
                $spotResizer.toggleClass("spotResizeAnimation",false);
            },
            drag:function(event, ui) {
                that.newSpotSizeLength((ui.position.left / spotGridSize.cellWidth)+1);
                // invert values
                that.newSpotSizeHeight(spotGridSize.maxRow - (ui.position.top / spotGridSize.cellHeight));
            },
            stop : function(event, ui) {
                $spotResizer.toggleClass("spotResizeAnimation",true);
            }
        });
        $spotSelection = $("#spotSelection");
        
        // getSpotDefinition
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.getSpotDefinition, [that.robot().robotId()]);
        tDeffer.done(that.successGetSpotDefinition);
        
        // getRobotCurrentStateDetails
        var tDeffer2 = parent.communicationWrapper.exec(RobotPluginManager.getRobotCurrentStateDetails, [that.robot().robotId()]);
        tDeffer2.done(that.successGetRobotCurrentStateDetails);
        
        // get jquery object for spotSize popup
        $spotPopup = $("#spotSize");
        
        // prevent the default behavior of standard touch events
        document.addEventListener('touchmove.clean', function(e) {
            e.preventDefault();
        }, false);
        
        
        // create startAreaControl (start button with remote control functionality)
        that.startAreaControl = new StartAreaControl($('#startArea'), $("#startContainer"),$('#eventArea'), $('#startBtn'),$('#remote'), [$('#remoteUp'), $('#remoteDown'), $('#remoteLeft'), $('#remoteRight'), $('#remoteDiagLeft'), $('#remoteDiagRight')]);
        that.startAreaControl.init();
        // set reference to binding object
        that.startAreaControl.isRemoteEnabled = this.isRemoteEnabled;
        
        // set initial category
        if(!that.robot().visualOnline()) {
            that.visualSelectedCategory(-1);
        } else {
            that.visualSelectedCategory(that.robot().cleaningCategory());
        }
        
        // click event listener for start button 
        $('#startBtn').on('startClick', that.startBtnClick);
        
        // pressed event listener for remote buttons
        $('#remote').on('remotePressed', that.remotePressed);
        
        $('#remote').on('remoteReleased', that.remoteReleased);
        
        $(window).on("resize.cleaning", function() {
            that.updateLayout();
        });

        $(document).one("pageshow", function(event) {
            that.updateLayout();
        });        
       
    };
    
    // viewmodel reload 
    this.reload = function() {
        // reset the conditions
        that.conditions = {};
    };
    
    // viewmodel deinit, destroy objects and remove event listener
    this.deinit = function() {
        parent.orientation.landscape = true;
        forceRotation('fullSensor');
        that.startAreaControl.deinit();
        that.startAreaControl = null;
        $('#startBtn').off("startClick");
        $('#remote').off('remotePressed');
        $('#remote').off('remoteReleased');
        $(window).off(".cleaning");
        subscribeCategory.dispose();
        subscribeOnline.dispose();
        that.waitingForRobot.dispose();
        that.isRemoteEnabled.dispose();
        that.isSendToBaseVisible.dispose();
        that.isSendToBaseEnabled.dispose();
        that.isStopEnabled.dispose();
        that.isEcoEnabled.dispose();
        that.newSpotSize.dispose();
        that.spotSize.dispose();
        robotUiStateHandler.rejectWaitDeffer();
    };    
    
     
    this.updateLayout = function() {
        console.log("update Layout");
        $("#statusLine").css("bottom", $(".control-line").height() - 50);
    };
    
    this.successGetSpotDefinition = function(result) {
        if(result.spotCleaningAreaLength > 0 && result.spotCleaningAreaHeight > 0) {
            var convSize = convertSpotsize(result.spotCleaningAreaLength, result.spotCleaningAreaHeight, true);
            that.robot().spotCleaningAreaLength(convSize.length);
            that.robot().spotCleaningAreaHeight(convSize.height);
            this.spotSizeLength = ko.observable(convSize.length);
            this.spotSizeHeight = ko.observable(convSize.height);
        }
    };
    
    this.successGetRobotCurrentStateDetails = function(result) {
        // need to add a check if it's a valid category (in some cases got 0 from server)
        if(result.robotCurrentStateDetails && result.robotCurrentStateDetails.robotStateParams) {
            if(result.robotCurrentStateDetails.robotStateParams.robotCleaningCategory == CLEANING_CATEGORY_MANUAL || result.robotCurrentStateDetails.robotStateParams.robotCleaningCategory == CLEANING_CATEGORY_ALL
                || result.robotCurrentStateDetails.robotStateParams.robotCleaningCategory == CLEANING_CATEGORY_SPOT) {
                    that.robot().cleaningCategory(result.robotCurrentStateDetails.robotStateParams.robotCleaningCategory);
            } else {
                // set All as fallback
                that.robot().cleaningCategory(CLEANING_CATEGORY_ALL);
            }
            // parse robotStateParams
            parent.communicationWrapper.parseStateParameters(that.robot(), result.robotCurrentStateDetails.robotStateParams);
            if(that.robot().visualOnline()) {
                // refresh states
                robotUiStateHandler.setVirtualState(that.robot().robotNewVirtualState());
            }
        } else {
            // set All as fallback
            that.robot().cleaningCategory(CLEANING_CATEGORY_ALL);
        }
    };
    
    // everytime called when the user taps on an category item
    this.changeCategory = function(newValue) {
    	console.log("changeCategory " + JSON.stringify(newValue) + " robot cleaningCategory " + that.robot().cleaningCategory());
        // check if category has changed
        if(that.robot().cleaningCategory() != newValue) {
            // check if cleaning has already started
            if(that.robot().robotNewVirtualState() == ROBOT_STATE_CLEANING || 
               that.robot().robotNewVirtualState() == ROBOT_STATE_MANUAL_CLEANING ||
               that.robot().robotNewVirtualState() == ROBOT_STATE_PAUSED ) {
                
                parent.notification.showDialog(dialogType.WARNING, $.i18n.t("dialogs.CANCEL_CLEANING.title"), $.i18n.t("dialogs.CANCEL_CLEANING.message"), [
                    {label:$.i18n.t("dialogs.CANCEL_CLEANING.button_1"), "callback":function(e) {
                            // close dialog, stop robot and change category
                            parent.notification.closeDialog();
                            var tDeffer = that.stopRobot();
                            
                            tDeffer.done(function(result) {
                                console.log("robot stopped");                                
                                // set stopped state to switch speech bubble text depending on category
                                if(newValue == CLEANING_CATEGORY_SPOT) {
                                    // show spot popup
                                    that.editSpotSize();
                                } else {
                                    that.robot().cleaningCategory(newValue);
                                }
                            });
                            
                            tDeffer.fail(function(error) {
                                // reset selection
                                that.visualSelectedCategory(that.robot().cleaningCategory());
                            });
                        }
                    },
                    {label:$.i18n.t("dialogs.CANCEL_CLEANING.button_2"), "callback":function(e) {
                            // reset selection
                            that.visualSelectedCategory(that.robot().cleaningCategory());
                            parent.notification.closeDialog();
                        }
                    }
                ]);
            } else {
                // set state to switch speech bubble text depending on category
                if(newValue == CLEANING_CATEGORY_SPOT) {
                    // show spot popup
                    that.editSpotSize();
                } else {
                    that.robot().cleaningCategory(newValue);
                    // refresh states
                    robotUiStateHandler.setVirtualState(that.robot().robotNewVirtualState());
                }
            }
        } else { 
            if(newValue == CLEANING_CATEGORY_SPOT) {
                // show spot popup
                that.editSpotSize();
            }
        }
    };
    
    // handle remote pressed events
    this.remotePressed = function(event, button) {
        navigator.notification.vibrate(500);
        //console.log("remote button pressed:")
        
        //console.log(button)
        var navigationControlId = 0;
        switch(button.id) {
            // remote functionality
            case "remoteUp":
                navigationControlId = 3;
                break;
            case "remoteDown":
                navigationControlId = 6;
                break;
            case "remoteLeft":
                navigationControlId = 1;
                break;
            case "remoteRight":
                navigationControlId = 5;
                break;
            case "remoteDiagLeft":
                navigationControlId = 2;
                break;
            case "remoteDiagRight":
                navigationControlId = 4;
                break;
        }
        if(navigationControlId > 0) {
            // Send robot drive direction
            console.log("drive robot direction: " + navigationControlId);
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.driveRobot, [that.robot().robotId(), navigationControlId], { type: notificationType.NONE });
        }
    };
    
    this.remoteReleased = function(event) {
        console.log("remoteReleased");
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.driveRobot, [that.robot().robotId(), 7], { type: notificationType.NONE });
    };
    
    this.toggleEcoMode = function() {
        that.ecoMode(!that.ecoMode());
        var onoff = $.i18n.t("common." + (that.ecoMode() == true ? "on":"off"));
        var callGuid = guid();
        that.robot().cleaningMode((that.ecoMode() == true ? CLEANING_MODE_ECO:CLEANING_MODE_NORMAL));
        
        parent.notification.showLoadingArea(true, notificationType.HINT, $.i18n.t("communication.toggle_eco", {"ecoMode":onoff}), callGuid);
        window.setTimeout(function(){
            parent.notification.showLoadingArea(false, notificationType.HINT, "", callGuid);
        }, 1000);
        
    };
    
    // spot size popup
    this.newSpotSizeLength = ko.observable();
    this.newSpotSizeHeight = ko.observable();
    
    this.newSpotSize = ko.computed(function() {
         // check if spotSelection has been already initialized
         if(typeof $spotSelection != "undefined") {
             // update spot selection
             $spotSelection.css({
                "left": (spotGridSize.maxWidth/2 - (that.newSpotSizeLength() * spotGridSize.cellWidth)) + "px",
                "width" : ((that.newSpotSizeLength() * spotGridSize.cellWidth*2) - spotGridSize.gridSpace) + 'px',
                "height": ((that.newSpotSizeHeight() * spotGridSize.cellHeight) - spotGridSize.gridSpace) + 'px'
            });
        }
         
         return ((that.newSpotSizeLength()*spotFactor)  + "x" + (that.newSpotSizeHeight()*spotFactor) + " " + spotUnit);
    }, this);  
    
    this.spotSizeLength = ko.observable(1);
    this.spotSizeHeight = ko.observable(1);
    this.spotSize = ko.computed(function() {
         return ((that.spotSizeLength()*spotFactor)  + "x" + (that.spotSizeHeight()*spotFactor));
    }, this); 
    
    this.editSpotSize = function() {
        // set currentSpot Size
        that.newSpotSizeLength(that.spotSizeLength());
        that.newSpotSizeHeight(that.spotSizeHeight());
        // set position of spotResizer
        $spotResizer.css({
            // invert top because top 0 equals spotSizeHeight 5
            "top": ((spotGridSize.maxRow * spotGridSize.cellHeight) - (that.spotSizeHeight() * spotGridSize.cellHeight)) + "px",
            "left": ((that.spotSizeLength() - 1) * spotGridSize.cellWidth) + 'px'
        });
        
        parent.notification.showDomDialog("#spotSize", false, function(){
            resizePopupButtons("#spotSize .ui-bar-buttons", ($("#spotSize .spotWrap").width() - $("#spotSize .spotWrap .left-block").width() - 10));
        });
    };
    
    this.popupOk = function() {
        $spotPopup.popup("close");
        // set new category
        that.robot().cleaningCategory(that.visualSelectedCategory());
        var newSize = convertSpotsize(that.newSpotSizeLength(), that.newSpotSizeHeight()); 
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.setSpotDefinition, [that.robot().robotId(), newSize.length, newSize.height],
        { type: notificationType.OPERATION, message: "Set new Spotsize: " + (that.newSpotSizeLength()*spotFactor) + "x" +  (that.newSpotSizeHeight()*spotFactor) , bHide: true });
        
        tDeffer.done(function(result) {
            that.robot().spotCleaningAreaLength(that.newSpotSizeLength());
            that.robot().spotCleaningAreaHeight(that.newSpotSizeHeight());
            that.spotSizeLength(that.newSpotSizeLength());
            that.spotSizeHeight(that.newSpotSizeHeight());
            // refresh states
            robotUiStateHandler.setVirtualState(that.robot().robotNewVirtualState());
        });
    };
    
    this.popupCancel = function() {
        $spotPopup.popup("close");
        // reset selection
        that.visualSelectedCategory(that.robot().cleaningCategory());
    };
    
    // start button 
    this.startBtnClick = function() {
       	if(!that.robot().visualOnline()) {
       		return false; 
       	}
        navigator.notification.vibrate(500);
        var tDeffer = null;
        // first check manual cleaning
        if(that.visualSelectedCategory() == CLEANING_CATEGORY_MANUAL && that.robot().robotNewVirtualState() != ROBOT_STATE_MANUAL_CLEANING) {
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.intendToDrive, [that.robot().robotId()], 
                      { type: notificationType.OPERATION, message: $.i18n.t('communication.intend_drive')}, false, true);
        } else if(that.visualSelectedCategory() == CLEANING_CATEGORY_MANUAL && that.robot().robotNewVirtualState() == ROBOT_STATE_MANUAL_CLEANING) {
            that.stopRobot();
        } else if (that.robot().robotNewVirtualState() == ROBOT_STATE_PAUSED) {
            // resume cleaning
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.resumeCleaning, [that.robot().robotId()]);
        } else if (that.robot().robotNewVirtualState() == ROBOT_STATE_CLEANING) {
            // pause cleaning
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.pauseCleaning, [that.robot().robotId()]);
        } else {
            // start cleaning
            // robotId, cleaningCategoryId, cleaningModeId, cleaningModifier
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.startCleaning, [that.robot().robotId(),
                      that.visualSelectedCategory(), that.robot().cleaningMode(), that.robot().cleaningModifier()]);
        }
        if(tDeffer) {
            tDeffer.done(that.startPauseRobotSuccess);
	   }
    };
    
    this.startPauseRobotSuccess = function(result) {
        console.log("startPauseRobotSuccess " + JSON.stringify(result));
        // some delay
        var tDeffer = null;
        
        if(that.visualSelectedCategory() == CLEANING_CATEGORY_MANUAL && that.robot().robotNewVirtualState() != ROBOT_STATE_MANUAL_CLEANING) {
            robotUiStateHandler.setUiState(ROBOT_UI_STATE_CONNECTING);
        } else {
            // cleaning command send wait till state changed
            robotUiStateHandler.startWaitTimer();
        }
    };
    
    // send to base button
    this.sendToBase = function() {
        // Send command that the robot should return to base ot start position
        var sendTo = that.robot().dockHasBeenSeen() ? "send_to_base" : "send_to_start";
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.sendCommandToRobot2, [that.robot().robotId(), COMMAND_SEND_BASE, {}], 
            { type: notificationType.OPERATION, message: $.i18n.t('communication.' + sendTo,{'robotName':that.robot().robotName()}) });
        tDeffer.done(that.successSendToBase);
    };
    
    this.successSendToBase = function(result) {
        console.log("successSendToBase" + JSON.stringify(result));
        // cleaning command send wait till state changed
        robotUiStateHandler.startWaitTimer();
    };
    
    // stop robot button
    this.stopRobot = function() {
        navigator.notification.vibrate(500);
        var tDefer = null;
        var tDeferStop = null;
        // need a new deferred to return a new promise object
        var stopChecked = $.Deferred();
        
        if(that.visualSelectedCategory() == CLEANING_CATEGORY_MANUAL && that.robot().robotNewVirtualState() == ROBOT_STATE_MANUAL_CLEANING) {
            tDefer = parent.communicationWrapper.exec(RobotPluginManager.stopRobotDrive, [that.robot().robotId()], 
                      { type: notificationType.OPERATION, message: $.i18n.t('communication.stop_drive',{'robotName':that.robot().robotName()})});
        } 

        // Send command that the robot should stop
        tDeferStop = parent.communicationWrapper.exec(RobotPluginManager.stopCleaning, [that.robot().robotId()], 
                  { type: notificationType.OPERATION, message: $.i18n.t('communication.stop_robot',{'robotName':that.robot().robotName()})});
        
        // in case the command has been sent successfully we still have to wait for the robots response
        tDeferStop.done(function(result){
            var wDefer = robotUiStateHandler.startWaitTimer();
            wDefer.fail(function(error){
                stopChecked.reject({"state":"rejected"});
            });
            wDefer.done(function(result){
                stopChecked.resolve({"state":"resolved"});
            });
        });
        
        tDeferStop.fail(function(error){
            stopChecked.reject({"state":"rejected"});
        });
        
        return stopChecked.promise();
    };
    
    // navigation menu and menu actions
    this.showMenu = function() {
        parent.notification.showDomDialog("#menuPopup", true);
    };
    
    this.cleaning = function() {
        $("#menuPopup").popup("close");
    };
    
    this.schedule = function() {
        that.conditions['schedule'] = true;
        parent.flowNavigator.next();
    };
    
    this.settings = function() {
        // switch to settings workflow
        that.conditions['settings'] = true;
        parent.flowNavigator.next();
    };    
    
    // test different states
    this.stateTest = function(newState) {
        if(newState >= 1 && newState <= 8) {
            // robot states from API
            parent.notification.notificationStatusSuccess({
                "robotDataKeyId":ROBOT_CURRENT_STATE_CHANGED,
                "robotId":that.robot().robotId(),
                "robotData":{"robotCurrentState":newState}
            });
        } else if (newState == 11) {
            // online
            parent.notification.notificationStatusSuccess({
                "robotDataKeyId":ROBOT_ONLINE_STATUS_CHANGED,
                "robotId":that.robot().robotId(),
                "robotData":{"online":1}
            });
        } else if (newState == 12) {
            // offline
            parent.notification.notificationStatusSuccess({
                "robotDataKeyId":ROBOT_ONLINE_STATUS_CHANGED,
                "robotId":that.robot().robotId(),
                "robotData":{"online":0}
            });    
        } else if (newState >= 20001 && newState <= 20100) {
            // UI states
            robotUiStateHandler.setUiState(newState);
        }
    };    
    
});
console.log('loaded file: cleaning_ViewModel.js');
