resourceHandler.registerFunction('robotActivationName_ViewModel.js', function(parent) {
    console.log('instance created for: robotActivationName_ViewModel');
    var that = this, tempRobot;
    this.conditions = {};
    this.robotName = ko.observable('');
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.robots = parent.communicationWrapper.getDataValue("robotList");
    
    this.init = function() {
        var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.getRobotDetail, [that.bundle.robot.robotId]);
        tDeffer.done(that.robotDetailSuccess);
    };
    
    // viewmodel deinit, destroy objects and remove event listener
    this.deinit = function() {
        that.isFilledOut.dispose();
    };
    
    this.robotDetailSuccess = function(result) {
        // temporary store robot details
        tempRobot = result;//{robotId:"robotId", robotName:"robotName"}
        that.robotName(tempRobot.robotName);
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
    };
    
    this.setRobotGoNext = function(robotName) {
        that.conditions['robotNameValid'] = true;
        that.bundle.robot.robotName = robotName;
        that.bundle.robot.displayName = robotName;
        // request state from server due some delay we need to use a 
        // separate API call because the user could navigate in the meantime 
        // to another screen
        //TODO: need to add the new robot to the selection list
        var tempRobot = ko.mapping.fromJS(that.bundle.robot);
        that.robots.push(tempRobot);
        that.robot(tempRobot);
        robotUiStateHandler.subscribeToRobot(parent.communicationWrapper.getDataValue("selectedRobot"));
        parent.communicationWrapper.updateRobotStateWithCode(that.robot(), that.robot().robotNewVirtualState());
        parent.communicationWrapper.getRobotState(that.bundle.robot.robotId);
        parent.communicationWrapper.getRobotOnline(that.bundle.robot.robotId);
        
        var msgTitle = $.i18n.t("dialogs.ROBOT_ADDED.title");
        var msgText = $.i18n.t("dialogs.ROBOT_ADDED.message");
        var msgButton = $.i18n.t("dialogs.ROBOT_ADDED.button_1");
        parent.notification.showDialog(dialogType.INFO, msgTitle, msgText, [{"label":msgButton, "callback":function(e){
            parent.flowNavigator.next();
        }}]);
    };

    this.robotSetNameError = function(error) {
        console.log("robotName can't be set:" + error);
    };

    this.reload = function() {
        that.conditions = {};
    };
});
console.log('loaded file: robotActivationName_ViewModel.js');
