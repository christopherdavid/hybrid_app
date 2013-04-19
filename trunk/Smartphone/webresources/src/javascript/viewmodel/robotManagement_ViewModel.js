resourceHandler.registerFunction('robotManagement_ViewModel.js', function(parent) {
    console.log('instance created for: robotManagement_ViewModel');
    var that = this;
    this.conditions = {};
    this.robot = ko.observable();
    this.editName = ko.observable(false);
    this.newRobotName = ko.observable("");
    
    this.init = function() {
        that.robot(ko.mapping.fromJS(parent.communicationWrapper.dataValues["activeRobot"]), null, that.robot);
    };
    
    this.changeRobot = function() {
        // Switch to robot selection dialog
        that.conditions['changeRobot'] = true;
        parent.flowNavigator.next(robotScreenCaller.CHANGE);
    };
    
    this.changeName = function() {
        // set robot name
        that.newRobotName(that.robot().robotName());
        that.editName(true);
    };
    
    this.cancelEdit = function() {
        this.editName(false);
    }
    this.commitEdit = function() {
        console.log("change name of robot with id: " + that.robot().robotId() + " to " + that.newRobotName());
        //check if name has really changed
        if(that.robot().robotName() != that.newRobotName()) {
            that.robot().robotName(that.newRobotName());
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.setRobotName2, [that.robot().robotId(), that.newRobotName()]);
            tDeffer.done(that.successSetRobotName);
            tDeffer.fail(that.errorSetRobotName);
        }
        this.editName(false);
    }
    
    this.successSetRobotName = function(result) {
        parent.communicationWrapper.dataValues["activeRobot"].robotName = that.newRobotName();
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
        /*
        var userEmail = parent.communicationWrapper.dataValues["user"].email;
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.disassociateRobot, [userEmail, that.robot().robotId()]);
        tDeffer.done(that.successRemoveRobot);
        tDeffer.fail(that.errorRemoveRobot);
        */
    };
    
    this.successRemoveRobot = function(result) {
        console.log("Result" + result);
        
    };

    this.errorRemoveRobot = function(error) {
        console.log("Error: " + error);
    };

    this.reload = function() {
        this.conditions = {};
    }
})
console.log('loaded file: robotManagement_ViewModel.js');
