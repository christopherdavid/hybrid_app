resourceHandler.registerFunction('robotManagement_ViewModel.js', function(parent) {
    console.log('instance created for: robotManagement_ViewModel');
    var that = this;
    this.conditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.newRobotName = ko.observable("");
    
    this.init = function() {
    };
    
    this.changeRobot = function() {
        // Switch to robot selection dialog
        that.conditions['changeRobot'] = true;
        parent.flowNavigator.next(robotScreenCaller.CHANGE);
    };
    
    this.changeName = function() {
        // set robot name
        that.newRobotName(that.robot().robotName());
        //open popup
        $("#changeNamePopup").popup("open");
    };
    
    this.cancelEdit = function() {
        $("#changeNamePopup").popup("close");
    }
    
    this.commitEdit = function() {
        console.log("change name of robot with id: " + that.robot().robotId() + " to " + that.newRobotName());
        //check if name has really changed
        if(that.robot().robotName() != that.newRobotName()) {
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.setRobotName2, [that.robot().robotId(), that.newRobotName()]);
            tDeffer.done(that.successSetRobotName);
            tDeffer.fail(that.errorSetRobotName);
        }
        
        $("#changeNamePopup").popup("close");
    }
    
    this.successSetRobotName = function(result) {
        that.robot().robotName(that.newRobotName());
        console.log("result" + JSON.stringify(result));
    }
    this.errorSetRobotName = function(error) {
        console.log("error" + JSON.stringify(error));
    }
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
    this.deleteRobot = function() {
        // show delete warning message 
        parent.notification.showDialog(dialogType.WARNING,$.i18n.t('dialogs.ROBOT_DELETE.title',{robotName:that.robot().robotId()}), "", 
            [{label:$.i18n.t('dialogs.ROBOT_DELETE.button_1'), callback:that.commitDelete}, {label:$.i18n.t('dialogs.ROBOT_DELETE.button_2')}]);
    };
    
    this.commitDelete = function() {
        var userEmail = parent.communicationWrapper.getDataValue("user").email;
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.disassociateRobot, [userEmail, that.robot().robotId()]);
        tDeffer.done(that.successRemoveRobot);
        tDeffer.fail(that.errorRemoveRobot);
    }
    
    this.successRemoveRobot = function(result) {
        console.log("Result" + result);
        that.conditions['changeRobot'] = true;
        parent.flowNavigator.next(robotScreenCaller.DELETE);
    };

    this.errorRemoveRobot = function(error) {
        console.log("Error: " + error);
    };

    this.reload = function() {
        this.conditions = {};
    }
})
console.log('loaded file: robotManagement_ViewModel.js');
