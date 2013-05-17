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
    
    this.terms = function() {
        that.conditions['terms'] = true;
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
        $("#menuPopup").popup("close");
    }
    
})
console.log('loaded file: settings_ViewModel.js');
