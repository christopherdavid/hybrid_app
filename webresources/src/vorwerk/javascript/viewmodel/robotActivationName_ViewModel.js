resourceHandler.registerFunction('robotActivationName_ViewModel.js', function(parent) {
    console.log('instance created for: robotActivationName_ViewModel');
    var that = this;
    this.conditions = {};
    this.robotName = ko.observable('');
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");

    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.isFilledOut = ko.computed(function() {
        return (that.robotName() != '');
    }, this);

    this.next = function() {
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.setRobotName2, [that.bundle.robot.robotId, that.robotName()]);
        tDeffer.done(that.robotNameSuccess);
        tDeffer.fail(that.robotNameError);
    };

    this.robotNameSuccess = function(result) {
        console.log("robotNameSuccess " + JSON.stringify(result));
        that.conditions['robotNameValid'] = true;
        that.bundle.robot.robotName = that.robotName();
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

    this.robotNameError = function(error) {
        console.log("robotName can't be set:" + error);
    }

    this.reload = function() {
        that.conditions = {};
    }
})
console.log('loaded file: robotActivationName_ViewModel.js');
