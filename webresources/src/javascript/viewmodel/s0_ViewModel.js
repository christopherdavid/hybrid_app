resourceHandler.registerFunction('s0_ViewModel.js', 's0_ViewModel', function(parent) {
    console.log('instance created for: s0_ViewModel');
    var that = this;
    this.id = 's0_ViewModel';
    this.conditions = {};
    this.startAreaControl = null;
    this.robotStateMachine = null;
    this.robot = ko.observable();

    this.logout = function() {        
        parent.communicationWrapper.exec(UserPluginManager.logout, [], that.successLogout, that.errorLogout);
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
        // React on start states of the robot and react accordingly
        var robotCommand = that.robotStateMachine.is("active") ? COMMAND_ROBOT_STOP : COMMAND_ROBOT_START;
        parent.communicationWrapper.exec(RobotPluginManager.sendCommandToRobot, [that.robot().robotId(), robotCommand, ""], that.startStopRobotSuccess, that.startStopRobotError);
    }

    this.startStopRobotSuccess = function(result) {
        if (that.robotStateMachine.is("active")){
            that.robotStateMachine.pause();
        } else{
            that.robotStateMachine.clean();
        }
    }

    this.startStopRobotError = function(error) {
        alert.log("error" + error);
        // TODO: update the state according to the error
    }

    this.cleaning = function() {
        that.conditions['cleaning'] = true;
        parent.flowNavigator.next();
    }

    this.remote = function() {
    	that.conditions['remote'] = true;
        parent.flowNavigator.next();
        // TODO: switch to remote view
    }

    this.schedule = function() {
        console.log("test");
        that.conditions['schedule'] = true;
        parent.flowNavigator.next();
        // TODO: switch to schedule workflow
    }

    this.settings = function() {
        // switch to settings workflow
        that.conditions['settings'] = true;
        parent.flowNavigator.next();
    }

    this.sendToBase = function() {
        // Send command that the robot should return to base
        parent.communicationWrapper.exec(RobotPluginManager.sendCommandToRobot, [that.robot().robotId(), COMMAND_SEND_BASE, ""], that.successSendToBase, that.errorSendToBase);
    };

    this.successSendToBase = function(result) {
        console.log("Robot sent to base!");
    }

    this.errorSendToBase = function(error) {
        console.log("Error:(Sending robot to base):" + error);
    }
    /**
     * Called when the viewmodel is initialized (after the view has been loaded, before bindings are applied)
     */
    this.init = function() {

        that.startAreaControl = new StartAreaControl($('#startArea'), $(".categoryArea"), $('#startBtn'), $(".categoryTable"), [$('#cleaning'), $('#remote'), $('#schedule'), $('#settings')]);
        that.startAreaControl.init();

        /**
         * finiteStateMachine
         * states:
         * - disabled: connection to robot isn't possible (for whatever reason)
         * - inactive: robot is available, but doesn't clean
         * - active: robot is active and cleaning
         * - pause: robot has been cleaning and now is in a pause cleaning state
         * - stop: robot will shutdown (or more precisely go into sleep mode)
         */
        that.robotStateMachine = StateMachine.create({
            initial : "inactive",

            events : [{
                name : "disable",
                from : "*",
                to : "disabled"
            }, {
                name : "deactivate",
                from : ["disabled", "stop"],
                to : "inactive"
            }, {
                name : "clean",
                from : ["inactive", "paused"],
                to : "active"
            }, {
                name : "pause",
                from : "active",
                to : "paused"
            }, {
                name : "stop",
                from : ["inactive", "active", "paused"],
                to : "stopped"
            }],

            callbacks : {
                onchangestate : function(event, from, to) {
                    that.startAreaControl.onStateChanged(to);
                }
            }
        });
      
        that.robot(ko.mapping.fromJS(parent.communicationWrapper.dataValues["activeRobot"]), null, that.robot);
    };

    this.reload = function() {
        // reset the conditions
        that.conditions = {}
    }

    this.deinit = function() {
        that.startAreaControl.deinit();
        that.startAreaControl = null;
    }
})
console.log('loaded file: s0_ViewModel.js');
