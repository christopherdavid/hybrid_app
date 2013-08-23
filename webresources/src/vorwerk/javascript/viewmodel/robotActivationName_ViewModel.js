resourceHandler.registerFunction('robotActivationName_ViewModel.js', function(parent) {
    console.log('instance created for: robotActivationName_ViewModel');
    var that = this, tempRobot;
    this.conditions = {};
    this.robotName = ko.observable('');
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    
    this.init = function() {
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.getRobotDetail, [that.bundle.robot.robotId]);
        tDeffer.done(that.robotDetailSuccess);
    }
    
    this.robotDetailSuccess = function(result) {
        // temporary store robot details
        tempRobot = result;//{robotId:"robotId", robotName:"robotName"}
        that.robotName(tempRobot.robotName);
    }

    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.isFilledOut = ko.computed(function() {
        return (that.robotName() != '');
    }, this);

    this.next = function() {
        // check if name needs to be changed on server
        if((tempRobot != null && tempRobot.robotName != that.robotName()) || tempRobot == null) {
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.setRobotName2, [that.bundle.robot.robotId, that.robotName()]);
            tDeffer.done(that.robotSetNameSuccess);
            tDeffer.fail(that.robotSetNameError);
        } else {
            console.log("robot name don't need to be changed");
            that.setRobotGoNext(tempRobot.robotName);
        }
    };

    this.robotSetNameSuccess = function(result) {
        console.log("robotSetNameSuccess " + JSON.stringify(result));
        that.setRobotGoNext(result.robotName);
    }
    
    this.setRobotGoNext = function(robotName) {
        that.conditions['robotNameValid'] = true;
        that.bundle.robot.robotName = robotName;
        var unknownState = $.i18n.t("robotStateCodes." + ROBOT_STATE_UNKNOWN);
        that.bundle.robot.stateCode = ROBOT_STATE_UNKNOWN;
        that.bundle.robot.stateString = unknownState;
        // request state from server due some delay we need to use a 
        // separate API call because the user could navigate in the meantime 
        // to another screen
        parent.communicationWrapper.getRobotState(that.bundle.robot.robotId);
        that.robot(ko.mapping.fromJS(that.bundle.robot), null, that.robot);
        parent.flowNavigator.next();
    }

    this.robotSetNameError = function(error) {
        console.log("robotName can't be set:" + error);
    }

    this.reload = function() {
        that.conditions = {};
    }
})
console.log('loaded file: robotActivationName_ViewModel.js');
