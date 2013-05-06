resourceHandler.registerFunction('cleaning_ViewModel.js', function(parent) {
    console.log('instance created for: cleaning_ViewModel');
    var that = this, $spotPopup;
    this.conditions = {};
    this.startAreaControl = null;
    this.robotStateMachine = null;
    this.robot = ko.observable();
    this.robotServerState = ko.observable("sleeping");
    this.cleaningType = ko.observableArray([{
            id : "1",
            text : $.i18n.t("cleaning.page.cleaningType.1")
        }, {
            id : "2",
            text : $.i18n.t("cleaning.page.cleaningType.2")
        }, {
            id : "3",
            text : $.i18n.t("cleaning.page.cleaningType.3")
        }]);
    this.selectedType = ko.observable("2");
    
    this.cleaningMode = ko.observableArray([{
            id : "1",
            text : $.i18n.t("common.cleaningMode.1")
        }, {
            id : "2",
            text : $.i18n.t("common.cleaningMode.2")
        }]);
    this.selectedMode = ko.observable("1");
    
    this.isSpotSelected = ko.computed(function() {
         return (that.selectedType() == "3");
    }, this); 
    
    this.isRemoteSelected = ko.computed(function() {
         return (that.selectedType() == "1");
    }, this); 
    
    this.robotState = ko.observable("");
    this.isRemoteDisabled = ko.computed(function() {
         return (that.selectedType() != "1" || that.robotState() != "active");
    }, this);  
    
    this.cleaningFrequency = ko.observableArray(["1","2"]);
    this.selectedFrequency = ko.observable("1");
        
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
    
    this.newSpotSizeLength = ko.observable();
    this.newSpotSizeHeight = ko.observable();
    
    this.spotSizeLength = ko.observable("5");
    this.spotSizeHeight = ko.observable("3");
    
    this.spotSize = ko.computed(function() {
         return (that.spotSizeLength()  + "x" + that.spotSizeHeight());
    }, this); 
    
    this.editSpotSize = function() {
        // set currentSpot Size
        that.newSpotSizeLength(that.spotSizeLength());
        that.newSpotSizeHeight(that.spotSizeHeight());
        $spotPopup.popup("open");
    }
    this.popupOk = function() {
        $spotPopup.popup("close");
        
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.setSpotDefinition, [that.robot().robotId(), that.spotSizeLength(), that.spotSizeHeight()],
        { type: notificationType.OPERATION, message: "Set new Spotsize: " + that.newSpotSizeLength() + "x" +  that.newSpotSizeHeight() , callback: null, bHide: true });
        tDeffer.done(function(result) {
            that.spotSizeLength(that.newSpotSizeLength());
            that.spotSizeHeight(that.newSpotSizeHeight());
        });
    }
    
    this.popupCancel = function() {
        $spotPopup.popup("close");
    }

    this.logout = function() {
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.logout, []);
        tDeffer.done(that.successLogout);
        tDeffer.fail(that.errorLogout);
    };

    this.successLogout = function(result) {
        that.conditions['logout'] = true;
        parent.flowNavigator.next();
        
        // Clear the data values on logout.
        parent.communicationWrapper.clearDataValues();
    }

    this.errorLogout = function(error) {
        console.log("Error(Logout failed):" + error);
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
        if (that.robotStateMachine.is("inactive")){
            that.robotStateMachine.clean();
        } else if (that.robotStateMachine.is("active")){
            that.robotStateMachine.pause();
        } else if (that.robotStateMachine.is("paused")){
            that.robotStateMachine.clean();
        }
        // get new server state
        //that.getRobotState();
    }

    this.startStopRobotError = function(error) {
        alert.log("error" + error);
        // TODO: update the state according to the error
    }
    
    // popup links
    this.cleaning = function() {
        that.conditions['cleaning'] = true;
        parent.flowNavigator.next();
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
    
    this.sendToBase = function() {
        // Send command that the robot should return to base
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.sendCommandToRobot2, [that.robot().robotId(), COMMAND_SEND_BASE, {}]);
        tDeffer.done(that.successSendToBase);
        tDeffer.fail(that.errorSendToBase);
    };

    this.successSendToBase = function(result) {
        console.log("Robot sent to base!");
    }

    this.errorSendToBase = function(error) {
        console.log("Error:(Sending robot to base):" + error);
    }
    
     this.getRobotState = function() {
        // Send command that the robot should return to base
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.sendCommandToRobot2, [that.robot().robotId(), COMMAND_GET_ROBOT_STATE, {}], { type: notificationType.NONE });
        tDeffer.done(that.successGetRobotState);
    };
    
    this.successGetRobotState = function(result) {
        if(result.currentStateString) {
            that.robotServerState(result.currentStateString)
        }
    }
    
    /**
     * Called when the viewmodel is initialized (after the view has been loaded, before bindings are applied)
     */
    this.init = function() {
        that.robot(ko.mapping.fromJS(parent.communicationWrapper.dataValues["activeRobot"]), null, that.robot);
        //that.getRobotState();
        
        // getSpotDefinition
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.getSpotDefinition, [that.robot().robotId()]);
        tDeffer.done(that.successSendToBase);
        tDeffer.fail(that.errorSendToBase);
        
        $spotPopup = $("#spotSize");
        
        // prevent the default behavior of standard touch events
        document.addEventListener('touchmove.clean', function(e) {
            e.preventDefault();
        }, false);
        
        that.startAreaControl = new StartAreaControl($('#startArea'), $("#startContainer"),$('#eventArea'), $('#startBtn'),$('#remote'), [$('#remoteUp'), $('#remoteDown'), $('#remoteLeft'), $('#remoteRight'), $('#remoteDiagLeft'), $('#remoteDiagRight')]);
        that.startAreaControl.init();
        that.startAreaControl.isRemoteDisabled = this.isRemoteDisabled;
        
        /**
         * finiteStateMachine
         * states:
         * - disabled: connection to robot isn't possible (for whatever reason)
         * - inactive: robot is available, but doesn't clean
         * - active: robot is active and cleaning
         * - pause: robot has been cleaning and now is in a pause cleaning state
         */
        that.robotStateMachine = StateMachine.create({
            initial : "inactive",

            events : [{
                name : "disable",
                from : "*",
                to : "disabled"
            }, {
                name : "deactivate",
                from : ["disabled", "paused", "active"],
                to : "inactive"
            }, {
                name : "clean",
                from : ["inactive", "paused"],
                to : "active"
            }, {
                name : "pause",
                from : "active",
                to : "paused"
            }],

            callbacks : {
                onchangestate : function(event, from, to) {
                    that.startAreaControl.onStateChanged(to);
                    that.robotState(to);
                }
            }
        });
        
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
    
    this.reload = function() {
        // reset the conditions
        that.conditions = {}
    }

    this.deinit = function() {
        that.startAreaControl.deinit();
        that.startAreaControl = null;
    }
})
console.log('loaded file: cleaning_ViewModel.js');
