resourceHandler.registerFunction('cleaning_ViewModel.js', function(parent) {
    console.log('instance created for: cleaning_ViewModel');
    var that = this, $spotPopup, 
        $leftSpotContainer,$rightSpotContainer,
        spotGridSize = {
            cellWidth:25,
            cellHeight:50,
            maxWidth:125,
            maxHeight:250,
        };
    this.conditions = {};
    this.startAreaControl = null;
    // set reference to helper class
    this.robotStateMachine = robotStateMachine;
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.robotServerState = that.robot().stateString;
    this.cleaningType = ko.observableArray([{
            id : "2",
            text : $.i18n.t("cleaning.page.cleaningType.2")
        }, {
            id : "3",
            text : $.i18n.t("cleaning.page.cleaningType.3")
        }, {
            id : "1",
            text : $.i18n.t("cleaning.page.cleaningType.1")
        }]);
    // set cleaning type "all" as default
    this.selectedType = ko.observable("2");
    
    this.cleaningMode = ko.observableArray([{
            id : "1",
            text : $.i18n.t("common.cleaningMode.1")
        }, {
            id : "2",
            text : $.i18n.t("common.cleaningMode.2")
        }]);
    // set cleaning mode "eco" as default
    this.selectedMode = ko.observable("1");
    
    this.isSpotSelected = ko.computed(function() {
         return (that.selectedType() == "3");
    }, this); 
    
    this.isRemoteSelected = ko.computed(function() {
         return (that.selectedType() == "1");
    }, this); 
    
    this.robotState = ko.observable("");
    
    this.waitingForRobot = ko.computed(function() {
         return (that.robotState() == "disabled" || that.robotState() == "waiting");
    }, this); 
    
    this.isRemoteDisabled = ko.computed(function() {
         return (that.selectedType() != "1" || that.robotState() != "active");
    }, this);
    
    this.isStopVisible = ko.computed(function() {
         return (that.robotState() == "active" ||  that.robotState() == "paused");
    }, this); 
    
    this.cleaningFrequency = ko.observableArray(["1","2"]);
    this.selectedFrequency = ko.observable("1");
        
    this.selectFrequency = function(value){
        that.selectedFrequency(value);
    }
        
    // register to selection change
    this.selectedType.subscribe(function(newValue) {
        if ( typeof newValue != "undefined" && newValue) {
            console.log("selected type: " + JSON.stringify(newValue));
            
            if(!that.robotStateMachine.is("inactive")) {
                // stop robot
                var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.stopCleaning, [that.robot().robotId()]);
                tDeffer.done(function(result) {
                    that.robotStateMachine.deactivate();
                });
                //tDeffer.fail();
            }
        }
    });
    
    // spot size popup
    this.newSpotSizeLength = ko.observable();
    this.newSpotSizeHeight = ko.observable();
    this.newSpotSize = ko.computed(function() {
         return (that.newSpotSizeLength()  + "x" + that.newSpotSizeHeight() + " m");
    }, this);  
    
    this.spotSizeLength = ko.observable(1);
    this.spotSizeHeight = ko.observable(1);
    this.spotSize = ko.computed(function() {
         return (that.spotSizeLength()  + "x" + that.spotSizeHeight());
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
        
        $spotPopup.popup("open");
    }
    this.popupOk = function() {
        $spotPopup.popup("close");
        
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.setSpotDefinition, [that.robot().robotId(), that.spotSizeLength(), that.spotSizeHeight()],
        { type: notificationType.OPERATION, message: "Set new Spotsize: " + that.newSpotSizeLength() + "x" +  that.newSpotSizeHeight() , bHide: true });
        tDeffer.done(function(result) {
            that.spotSizeLength(that.newSpotSizeLength());
            that.spotSizeHeight(that.newSpotSizeHeight());
        });
    }
    
    this.popupCancel = function() {
        $spotPopup.popup("close");
    }
    
    this.changeRobot = function() {
        // Switch to robot selection dialog
        that.conditions['changeRobot'] = true;
        parent.flowNavigator.next(robotScreenCaller.CHANGE);
    };

    this.startBtnClick = function() {
        var tDeffer = null;
        // React on start states of the robot and react accordingly
        if (that.robotStateMachine.is("inactive")){
            // start cleaning
            // robotId, cleaningCategoryId, cleaningModeId, cleaningModifier
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.startCleaning, [that.robot().robotId(),
            that.selectedType(), that.selectedMode(), that.selectedFrequency()]);
        } else  if (that.robotStateMachine.is("paused")){
            // resume cleaning
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.resumeCleaning, [that.robot().robotId()]);
        } else if (that.robotStateMachine.is("active")){
            // pause cleaning
            tDeffer = parent.communicationWrapper.exec(RobotPluginManager.pauseCleaning, [that.robot().robotId()]);
        }
        if(tDeffer != null) {
            tDeffer.done(that.startStopRobotSuccess);
            tDeffer.fail(that.startStopRobotError);
        }
    }
    
    this.startStopRobotSuccess = function(result) {
        console.log("startStopRobotSuccess " + JSON.stringify(result))
        // some delay
        if(result.expectedTimeToExecute && result.expectedTimeToExecute > 1) {
            that.handleTimedMode(result.expectedTimeToExecute, that.robot().robotId());
        // robot is connected to server
        } else {
            if (that.robotStateMachine.is("inactive")){
                that.robotStateMachine.clean();
                parent.communicationWrapper.updateRobotStateWithCode(that.robot(), ROBOT_STATE_CLEANING);
            } else if (that.robotStateMachine.is("active")){
                that.robotStateMachine.pause();
                parent.communicationWrapper.updateRobotStateWithCode(that.robot(), ROBOT_STATE_PAUSED);
            } else if (that.robotStateMachine.is("paused")){
                that.robotStateMachine.clean();
                parent.communicationWrapper.updateRobotStateWithCode(that.robot(), ROBOT_STATE_RESUMED);
            }
        }
    }

    this.startStopRobotError = function(error) {
        alert.log("error" + error);
        // TODO: update the state according to the error
    }
    
    // navigation menu actions
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
            { type: notificationType.OPERATION, message: $.i18n.t('communication.send_to_base')});
        tDeffer.done(that.successSendToBase);
        tDeffer.fail(that.errorSendToBase);
    };
    this.successSendToBase = function(result) {
        console.log("successSendToBase" + JSON.stringify(result));
    }
    this.errorSendToBase = function(error) {
        console.log("errorSendToBase" + JSON.stringify(error));
    }
    
    // stop robot button
    this.stopRobot = function() {
        // Send command that the robot should stop
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.stopCleaning, [that.robot().robotId()], 
            { type: notificationType.OPERATION, message: $.i18n.t('communication.stop_robot')});
        tDeffer.done(that.successStopRobot);
        tDeffer.fail(that.errorStopRobot);
    }
    this.successStopRobot = function(result) {
        console.log("successStopRobot" + JSON.stringify(result));
        if(result.expectedTimeToExecute && result.expectedTimeToExecute > 1) {
            that.handleTimedMode(result.expectedTimeToExecute, that.robot().robotId());
        // robot is connected to server
        } else {
            that.robotStateMachine.deactivate();
            parent.communicationWrapper.updateRobotStateWithCode(that.robot(), ROBOT_STATE_STOPPED);
        }
    }
    this.errorStopRobot = function(error) {
        console.log("errorStopRobot" + JSON.stringify(error));
    }
    
    /**
     * Called when the viewmodel is initialized (after the view has been loaded, before bindings are applied)
     */
    this.init = function() {
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
            that.robotServerState(resultText);
        });
        
        // getSpotDefinition
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.getSpotDefinition, [that.robot().robotId()]);
        tDeffer.done(that.successGetSpotDefinition);
        
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
        that.startAreaControl.isRemoteDisabled = this.isRemoteDisabled;
        
        // set callback for state changes
        that.robotStateMachine.callback = function(from, to) {
            //console.log("callback " + from + " to "  + to)
            that.startAreaControl.onStateChanged(to);
            that.robotState(to);
        };
        // get lasst state, this triggers callback
        that.robotStateMachine.triggerCallback();
        
        // click event listener for start button 
        $('#startBtn').on('startClick', that.startBtnClick);
        
        // pressed event listener for remote buttons
        $('#remote').on('remotePressed', that.remotePressed);
    }
    
    this.successGetSpotDefinition = function(result) {
        if(result.spotCleaningAreaLength > 0) {
            this.spotSizeLength = ko.observable(result.spotCleaningAreaLength);
        }
        if(result.spotCleaningAreaHeight > 0) {
            this.spotSizeHeight = ko.observable(result.spotCleaningAreaHeight);
        }
    }
    
    // handle remote pressed events
    this.remotePressed = function(event, button) {
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
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.driveRobot, [that.robot().robotId(), navigationControlId], { type: notificationType.NONE });
            //tDeffer.done(that.successDrive);
            tDeffer.fail(that.errorDrive);
            console.log("drive robot direction: " + navigationControlId);
        }
    }
    this.errorDrive = function(error) {
        console.log("Error:(Driving robot):" + error);
    }
    
    // viewmodel reload 
    this.reload = function() {
        // reset the conditions
        that.conditions = {}
    }
    
    // viewmodel deinit, destroy objects and remove event listener
    this.deinit = function() {
        that.startAreaControl.deinit();
        that.startAreaControl = null;
        $('#startBtn').off("startClick");
        $('#remote').off('remotePressed');
        that.robotStateMachine.callback = null;
    }
})
console.log('loaded file: cleaning_ViewModel.js');
