resourceHandler.registerFunction('settings_ViewModel.js', function(parent) {
    console.log('instance created for: settings_ViewModel');
    var that = this;
    this.conditions = {};
    this.backConditions = {};
    this.robot = ko.observable();
    
    this.init = function() {
        that.robot(ko.mapping.fromJS(parent.communicationWrapper.dataValues["activeRobot"]), null, that.robot);
    };
    
    this.changeRobot = function() {
        // Switch to robot selection dialog
        that.conditions['changeRobot'] = true;
        parent.flowNavigator.next(robotScreenCaller.CHANGE);
    };

    this.back = function() {
        parent.flowNavigator.previous();
    };
    
    this.logout = function() {
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.logout, []);
        tDeffer.done(that.successLogout);
        tDeffer.fail(that.errorLogout);
    };
    this.successLogout = function(result) {
        that.backConditions['logout'] = true;
        parent.flowNavigator.previous();
    };

    this.errorLogout = function(error) {
        console.log("Error (Logout): " + error.errorMessage);
    }

    this.robotManagement = function() {
        that.conditions['robotManagement'] = true;
        parent.flowNavigator.next();
    };

    this.userSettings = function() {
        that.conditions['userSettings'] = true;
        parent.flowNavigator.next();
    };
    
    this.generalSettings = function() {
        that.conditions['generalSettings'] = true;
        parent.flowNavigator.next();
    };
    
    this.testArea = function() {
        that.conditions['test'] = true;
        parent.flowNavigator.next();
    }

    this.reload = function() {
        // remove conditions
        that.conditions = {};
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
    
})
console.log('loaded file: settings_ViewModel.js');
