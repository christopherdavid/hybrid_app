resourceHandler.registerFunction('cleaning_ViewModel.js', function(parent) {
    console.log('instance created for: cleaning_ViewModel');
    var that = this, $spotPopup, 
        $leftSpotContainer,$rightSpotContainer,
        spotGridSize = {
            cellWidth:deviceSize.getResolution() == "high" ? 39 : 26,
            cellHeight:deviceSize.getResolution() == "high" ? 78 : 52,
            maxWidth:deviceSize.getResolution() == "high" ? 195 : 130,
            maxHeight:deviceSize.getResolution() == "high" ? 390 : 260,
        },
        spotFactor = parseInt($.i18n.t("pattern.spotFactor"),10),
        spotUnit = $.i18n.t("pattern.spotUnit");
    
    this.conditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    
    this.startAreaControl = null;
    
    this.currentUiState = robotUiStateHandler.current;
    
    this.visualSelectedCategory = ko.observable();
    
    // listener when robot category changes
    this.robot().cleaningCategory.subscribe(function(newValue) {
            console.log("cleaningCategory subscribe " + newValue)
            if(newValue != this.visualSelectedCategory()) {
                that.visualSelectedCategory(newValue);
            }
        }, this);
    
    // set cleaning mode "eco" as default
    this.ecoMode = ko.observable(true);
    
    this.cleaningMode = ko.observableArray([{
            id : "1",
            text : $.i18n.t("common.cleaningMode.1")
        }, {
            id : "2",
            text : $.i18n.t("common.cleaningMode.2")
        }]);
            
    
    this.robotState = ko.observable("");
    
    this.waitingForRobot = ko.computed(function() {
         return (that.currentUiState().ui() == ROBOT_UI_STATE_CONNECTING || that.currentUiState().ui() == ROBOT_UI_STATE_WAIT || that.currentUiState().ui() == ROBOT_UI_STATE_WAKEUP || that.currentUiState().ui() == ROBOT_UI_STATE_GETREADY || that.currentUiState().ui() == ROBOT_UI_STATE_DISABLED);
    }, this); 
    
    this.isRemoteEnabled = ko.computed(function() {
         return (that.visualSelectedCategory() == CLEANING_CATEGORY_MANUAL && that.currentUiState().robot() == ROBOT_STATE_MANUAL_CLEANING);
    }, this);
    
    this.isSendToBaseVisible = ko.computed(function() {
         return (that.visualSelectedCategory() == CLEANING_CATEGORY_ALL && (that.currentUiState().robot() == ROBOT_STATE_STOPPED || that.currentUiState().robot() == ROBOT_STATE_ON_BASE));
    }, this);
    
    this.isSendToBaseEnabled = ko.computed(function() {
        return (!that.waitingForRobot() && (that.currentUiState().robot() != ROBOT_STATE_ON_BASE && 
                                             that.currentUiState().robot() != ROBOT_STATE_CLEANING && 
                                             that.currentUiState().robot() != ROBOT_STATE_PAUSED   && 
                                             that.currentUiState().robot() != ROBOT_STATE_RESUMED  &&
                                             that.currentUiState().robot() != ROBOT_STATE_MANUAL_CLEANING ));
    }, this);
    
    this.isStopEnabled = ko.computed(function() {
         return (!that.waitingForRobot() && (that.currentUiState().robot() == ROBOT_STATE_CLEANING || 
                                             that.currentUiState().robot() == ROBOT_STATE_PAUSED   || 
                                             that.currentUiState().robot() == ROBOT_STATE_RESUMED  ||
                                             that.currentUiState().robot() == ROBOT_STATE_MANUAL_CLEANING ));
    }, this);
    
    this.isEcoEnabled = ko.computed(function() {
        return (!that.waitingForRobot() && (that.currentUiState().robot() != ROBOT_STATE_CLEANING && 
                                             that.currentUiState().robot() != ROBOT_STATE_PAUSED   && 
                                             that.currentUiState().robot() != ROBOT_STATE_RESUMED  &&
                                             that.currentUiState().robot() != ROBOT_STATE_MANUAL_CLEANING ));
    }, this);
    
    this.selectedFrequency = ko.observable("1");
    
        
    /**
     * Called when the viewmodel is initialized (after the view has been loaded, before bindings are applied)
     */
    this.init = function() {
        // check if country is italy. if so change product logo
        var user = parent.communicationWrapper.getDataValue("user");
        var country = parent.communicationWrapper.getFromLocalStorage(user.email + "_country");
        if(country && country == "italy") {
            $("#menuPopupLogo").addClass("folletto");
        }
        
        parent.orientation.landscape = false;
        forceRotation('portrait');
        
        $leftSpotContainer = $('#leftSpotContainer');
        $rightSpotContainer = $('#rightSpotContainer');
        
        $rightSpotContainer.resizable({
            gridWithEvent : [spotGridSize.cellWidth,spotGridSize.cellHeight],
            handles : "ne",
            maxHeight: spotGridSize.maxHeight,
            maxWidth:  spotGridSize.maxWidth,
            minHeight: spotGridSize.cellHeight,
            minWidth: spotGridSize.cellWidth,
            gridSnapEvent:function(event, ui, coords) {
                //console.log("gridSnapEvent");
                //console.log(coords)
                $leftSpotContainer.css({
                    "left": (spotGridSize.maxWidth - coords.w) + "px",
                    "width" : coords.w + 'px',
                    "height": coords.h + 'px'
                });
                that.newSpotSizeLength(coords.w/spotGridSize.cellWidth);
                that.newSpotSizeHeight(coords.h/spotGridSize.cellHeight);
            }
        });
        
        $rightSpotContainer.on('resize', function (e) {
            e.stopPropagation(); 
        });
        
        // register for push notifications type of NOTIFICATION_CLEANING_DONE
        parent.notification.registerStatus(NOTIFICATION_CLEANING_DONE, function(resultText) {
            that.robot().stateString(resultText);
        });
        
        // getSpotDefinition
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.getSpotDefinition, [that.robot().robotId()]);
        tDeffer.done(that.successGetSpotDefinition);
        
        // getRobotCleaningCategory
        var tDeffer2 = parent.communicationWrapper.exec(RobotPluginManager.getRobotCleaningCategory, [that.robot().robotId()]);
        tDeffer2.done(that.successGetRobotCleaningCategory);
        
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
        that.visualSelectedCategory(that.robot().cleaningCategory());
        
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
    }
    
    this.updateLayout = function() {
        console.log("update Layout")
        $("#statusLine").css("bottom", $(".control-line").height());
    }
    
    this.successGetSpotDefinition = function(result) {
        if(result.spotCleaningAreaLength > 0 && result.spotCleaningAreaHeight > 0) {
            var convSize = convertSpotsize(result.spotCleaningAreaLength, result.spotCleaningAreaHeight, true);
            that.robot().spotCleaningAreaLength(convSize.length);
            that.robot().spotCleaningAreaHeight(convSize.height);
            this.spotSizeLength = ko.observable(convSize.length);
            this.spotSizeHeight = ko.observable(convSize.height);
        }
    }
    
    // {cleaningCatageory: <1-Manual,2-All,3-Spot>,robotId:"robotId"}
    this.successGetRobotCleaningCategory = function(result) {
        that.robot().cleaningCategory(result.cleaningCatageory);
    }
    
    // everytime called when the user taps on an category item
    this.changeCategory = function(newValue) {
        console.log("changeCategory " + JSON.stringify(newValue) + " robot cleaningCategory " + that.robot().cleaningCategory());
        // check if category has changed
        if(that.robot().cleaningCategory() != newValue) {
            // check if cleaning has already started
            if(that.robot().robotNewVirtualState() == ROBOT_STATE_CLEANING || 
               that.robot().robotNewVirtualState() == ROBOT_STATE_MANUAL_CLEANING ||
               that.currentUiState().ui() == ROBOT_UI_STATE_CONNECTING ||
               that.robot().robotNewVirtualState() == ROBOT_STATE_PAUSED ||
               that.robot().robotNewVirtualState() == ROBOT_STATE_RESUMED ) {
                
                parent.notification.showDialog(dialogType.WARNING, $.i18n.t("dialogs.CANCEL_CLEANING.title"), $.i18n.t("dialogs.CANCEL_CLEANING.message"), [
                    {label:$.i18n.t("dialogs.CANCEL_CLEANING.button_1"), "callback":function(e) {
                            // close dialog, stop robot and change category
                            parent.notification.closeDialog();
                            var tDeffer = that.stopRobot();
                            
                            tDeffer.done(function(result) {
                                console.log("robot stopped");
                                that.robot().cleaningCategory(newValue);
                                // set stopped state to switch speech bubble text depending on category
                                robotUiStateHandler.setUiState(ROBOT_STATE_STOPPED);
                                if(newValue == CLEANING_CATEGORY_SPOT) {
                                    // show spot popup
                                    that.editSpotSize();
                                }
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
                that.robot().cleaningCategory(newValue);
                // set stopped state to switch speech bubble text depending on category
                robotUiStateHandler.setUiState(ROBOT_STATE_STOPPED);
                if(newValue == CLEANING_CATEGORY_SPOT) {
                    // show spot popup
                    that.editSpotSize();
                }
            }
        } else { 
            if(newValue == CLEANING_CATEGORY_SPOT) {
                // show spot popup
                that.editSpotSize();
            }
        }
    }
    
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
            robotUiStateHandler.setUiState(ROBOT_UI_STATE_CLEANING_TAP_MANUAL);
            // Send robot drive direction
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.driveRobot, [that.robot().robotId(), navigationControlId], { type: notificationType.NONE });
            console.log("drive robot direction: " + navigationControlId);
        }
    }
    
    this.remoteReleased = function(event, button) {
        robotUiStateHandler.setUiState(ROBOT_UI_STATE_CLEANING_MANUAL);
    }
    
    // viewmodel reload 
    this.reload = function() {
        // reset the conditions
        that.conditions = {}
    }
    
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
        $rightSpotContainer.off('resize');
        //that.robotUiStateMachine.callback = null;
    }
    
    
    this.toggleEcoMode = function() {
        that.ecoMode(!that.ecoMode());
        var onoff = $.i18n.t("common." + (that.ecoMode() == true ? "on":"off"));
        var callGuid = guid();
        that.robot().cleaningMode((that.ecoMode() == true ? CLEANING_MODE_ECO:CLEANING_MODE_NORMAL))
        
        parent.notification.showLoadingArea(true, notificationType.HINT, $.i18n.t("communication.toggle_eco", {"ecoMode":onoff}), callGuid);
        window.setTimeout(function(){
            parent.notification.showLoadingArea(false, notificationType.HINT, "", callGuid);
        }, 1000);
        
    }
    
    // spot size popup
    this.newSpotSizeLength = ko.observable();
    this.newSpotSizeHeight = ko.observable();
    this.newSpotSize = ko.computed(function() {
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
        
        $leftSpotContainer.css({
            "left": (spotGridSize.maxWidth - (that.spotSizeLength() * spotGridSize.cellWidth)) + "px",
            "width" : (that.spotSizeLength() * spotGridSize.cellWidth) + 'px',
            "height": (that.spotSizeHeight() * spotGridSize.cellHeight) + 'px'
        });
        $rightSpotContainer.css({
            "width" : (that.spotSizeLength() * spotGridSize.cellWidth) + 'px',
            "height": (that.spotSizeHeight() * spotGridSize.cellHeight) + 'px',
            "top": 'auto'
        });
        
        parent.notification.showDomDialog("#spotSize", false, function(){
            resizePopupButtons("#spotSize .ui-bar-buttons", ($("#spotSize .spotWrap").width() - $("#spotSize .spotWrap .left-block").width() - 10));
        });
    }
    this.popupOk = function() {
        $spotPopup.popup("close");
        var newSize = convertSpotsize(that.newSpotSizeLength(), that.newSpotSizeHeight()); 
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.setSpotDefinition, [that.robot().robotId(), newSize.length, newSize.height],
        { type: notificationType.OPERATION, message: "Set new Spotsize: " + (that.newSpotSizeLength()*spotFactor) + "x" +  (that.newSpotSizeHeight()*spotFactor) , bHide: true });
        tDeffer.done(function(result) {
            that.robot().spotCleaningAreaLength(that.newSpotSizeLength());
            that.robot().spotCleaningAreaHeight(that.newSpotSizeHeight());
            that.spotSizeLength(that.newSpotSizeLength());
            that.spotSizeHeight(that.newSpotSizeHeight());
            robotUiStateHandler.setUiState(ROBOT_UI_STATE_STOPPED_WAITED_SPOT);
        });
    }
    
    this.popupCancel = function() {
        $spotPopup.popup("close");
    }
    
    this.startBtnClick = function() {
        navigator.notification.vibrate(500);
        var tDeffer = null;
        // first check manual cleaning
        if(that.visualSelectedCategory() == CLEANING_CATEGORY_MANUAL && that.robot().robotNewVirtualState() != ROBOT_STATE_MANUAL_CLEANING) {
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.intendToDrive, [that.robot().robotId()], 
                      { type: notificationType.OPERATION, message: $.i18n.t('communication.intend_drive')}, false, true);
        } else if (that.robot().robotNewVirtualState() == ROBOT_STATE_PAUSED) {
            // resume cleaning
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.resumeCleaning, [that.robot().robotId()]);
        } else if (that.robot().robotNewVirtualState() == ROBOT_STATE_CLEANING ||
                   that.robot().robotNewVirtualState() == ROBOT_STATE_RESUMED) {
            // pause cleaning
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.pauseCleaning, [that.robot().robotId()]);
        } else if (that.robot().robotNewVirtualState() == ROBOT_STATE_MANUAL_CLEANING) {
            // pause drive
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.pauseCleaning, [that.robot().robotId()]);
        } else {
            // start cleaning
            // robotId, cleaningCategoryId, cleaningModeId, cleaningModifier
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.startCleaning, [that.robot().robotId(),
                      that.visualSelectedCategory(), that.robot().cleaningMode(), that.robot().cleaningModifier()]);
        }
        tDeffer.done(that.startStopRobotSuccess);
    }
    
    this.startStopRobotSuccess = function(result) {
        console.log("startStopRobotSuccess " + JSON.stringify(result))
        // some delay
        var tDeffer = null;
        if(result.expectedTimeToExecute && result.expectedTimeToExecute > 1) {
            handleTimedMode(result.expectedTimeToExecute, that.robot().robotId());
        // robot is connected to server
        } else {
            if(that.visualSelectedCategory() == CLEANING_CATEGORY_MANUAL && that.robot().robotNewVirtualState() != ROBOT_STATE_MANUAL_CLEANING) {
                robotUiStateHandler.setUiState(ROBOT_UI_STATE_CONNECTING);
                 tDeffer = parent.communicationWrapper.exec(RobotPluginManager.startCleaning, [that.robot().robotId(),
                      that.visualSelectedCategory(), that.robot().cleaningMode(), that.robot().cleaningModifier()]);
                 tDeffer.done(that.startManualModeSuccess);
                      
            } else if (that.robot().robotNewVirtualState() == ROBOT_STATE_PAUSED) {
                // resumed
                parent.communicationWrapper.updateRobotStateWithCode(that.robot(), ROBOT_STATE_RESUMED);
            } else if (that.robot().robotNewVirtualState() == ROBOT_STATE_CLEANING ||
                       that.robot().robotNewVirtualState() == ROBOT_STATE_RESUMED) {
                // paused
                parent.communicationWrapper.updateRobotStateWithCode(that.robot(), ROBOT_STATE_PAUSED);
            } else if (that.robot().robotNewVirtualState() == ROBOT_STATE_MANUAL_CLEANING) {
                // paused drive
                parent.communicationWrapper.updateRobotStateWithCode(that.robot(), ROBOT_STATE_PAUSED);
            } else {
                // started cleaning
                parent.communicationWrapper.updateRobotStateWithCode(that.robot(), ROBOT_STATE_CLEANING);
            }
        }
    }
    
     this.startManualModeSuccess = function(result) {
       console.log("startManualModeSuccess" + JSON.stringify(result));
     }
     
    // navigation menu and menu actions
    this.showMenu = function() {
        parent.notification.showDomDialog("#menuPopup", true);
    }
    this.cleaning = function() {
        $("#menuPopup").popup("close");
    }
    this.schedule = function() {
        that.conditions['schedule'] = true;
        parent.flowNavigator.next();
    }
    this.settings = function() {
        // switch to settings workflow
        that.conditions['settings'] = true;
        parent.flowNavigator.next();
    }
    
    // send to base button
    this.sendToBase = function() {
        // Send command that the robot should return to base
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.sendCommandToRobot2, [that.robot().robotId(), COMMAND_SEND_BASE, {}], 
            { type: notificationType.OPERATION, message: $.i18n.t('communication.send_to_base',{'robotName':that.robot().robotName()})});
        tDeffer.done(that.successSendToBase);
    };
    this.successSendToBase = function(result) {
        console.log("successSendToBase" + JSON.stringify(result));
         // some delay
        if(result.expectedTimeToExecute && result.expectedTimeToExecute > 1) {
            handleTimedMode(result.expectedTimeToExecute, that.robot().robotId());
        // robot is connected to server
        } else {
            // send to base
            parent.communicationWrapper.updateRobotStateWithCode(that.robot(), ROBOT_STATE_ON_BASE);
        }
    }
    
    // stop robot button
    this.stopRobot = function() {
        navigator.notification.vibrate(500);
        var tDeffer = null;
        var tDefferStop = null;
        if(that.visualSelectedCategory() == CLEANING_CATEGORY_MANUAL && that.robot().robotNewVirtualState() == ROBOT_STATE_MANUAL_CLEANING) {
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.stopRobotDrive, [that.robot().robotId()], 
                      { type: notificationType.OPERATION, message: $.i18n.t('communication.stop_drive',{'robotName':that.robot().robotName()})});
        } else if(that.visualSelectedCategory() == CLEANING_CATEGORY_MANUAL && that.currentUiState().ui() == ROBOT_UI_STATE_CONNECTING) {
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.cancelIntendToDrive, [that.robot().robotId()], 
                      { type: notificationType.OPERATION, message: $.i18n.t('communication.stop_drive',{'robotName':that.robot().robotName()})});
        } 
        //else {
            // Send command that the robot should stop
            tDefferStop = parent.communicationWrapper.exec(RobotPluginManager.stopCleaning, [that.robot().robotId()], 
                      { type: notificationType.OPERATION, message: $.i18n.t('communication.stop_robot',{'robotName':that.robot().robotName()})});
        //}
        
        tDefferStop.done(that.successStopRobot);
        return tDefferStop; 
    }
    this.successStopRobot = function(result) {
        console.log("successStopRobot" + JSON.stringify(result));
        if(result.expectedTimeToExecute && result.expectedTimeToExecute > 1 && (that.visualSelectedCategory() != CLEANING_CATEGORY_MANUAL)) {
            handleTimedMode(result.expectedTimeToExecute, that.robot().robotId());
        // robot is connected to server
        } else {
            parent.communicationWrapper.updateRobotStateWithCode(that.robot(), ROBOT_STATE_STOPPED);
        }
    }
    
    
    // test different states
    this.stateTest = function(newState) {
        if(newState >= 10001 && newState <= 10011) {
            // robot states from API
            parent.communicationWrapper.updateRobotStateWithCode(that.robot(), newState, newState);
        } else if (newState >= 20001 && newState <= 20007) {
            // UI states
            robotUiStateHandler.setUiState(newState)
        }
    }
    
    
})
console.log('loaded file: cleaning_ViewModel.js');
