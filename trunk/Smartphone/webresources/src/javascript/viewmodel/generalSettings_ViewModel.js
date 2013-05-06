resourceHandler.registerFunction('generalSettings_ViewModel.js', function(parent) {
    console.log('instance created for: generalSettings_ViewModel');
    var that = this;
    this.conditions = {};
    this.robot = ko.observable();
    
    this.selectedPushType = ko.observable();
    this.robotNeedsCleaning = ko.observable();
    this.cleaningDone = ko.observable();
    this.robotStuck = ko.observable();

    this.init = function() {
        that.robot(ko.mapping.fromJS(parent.communicationWrapper.dataValues["activeRobot"]), null, that.robot);
    };
    
    this.changeRobot = function() {
        // Switch to robot selection dialog
        that.conditions['changeRobot'] = true;
        parent.flowNavigator.next(robotScreenCaller.CHANGE);
    };
    
    this.selectedPushType.subscribe(function(newValue) {
        if(newValue == 'on')
        {        
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.registerNotifications, [that.robot().robotId()]);
        }
        else
        {
            var tDeffer = parent.communicationWrapper.exec(RobotPluginManager.unregisterNotifications, [that.robot().robotId()]);
        }

        tDeffer.done(that.successSetNotifications);
        tDeffer.fail(that.errorSetNotifications);
    });
    
    this.robotNeedsCleaning.subscribe(function(newValue) {
        console.log("robotNeedsCleaning=" + newValue);
    });
    
    this.cleaningDone.subscribe(function(newValue) {
        console.log("cleaningIsDone=" + newValue);
    });

    this.robotStuck.subscribe(function(newValue) {
        console.log("robotStuck=" + newValue);
    });


    
    this.successSetNotifications = function(result) {
        console.log("result" + JSON.stringify(result));
    }
    this.errorSetNotifications = function(error) {
        console.log("error" + JSON.stringify(error));
    }
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
    this.reload = function() {
        this.conditions = {};
    }
    
})
console.log('loaded file: generalSettings_ViewModel.js');
