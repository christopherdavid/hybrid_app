resourceHandler.registerFunction('settings_ViewModel.js', function(parent) {
    console.log('instance created for: settings_ViewModel');
    var that = this;
    this.conditions = {};

    this.back = function() {
        parent.flowNavigator.previous();
    }

    this.robotManagement = function() {
        that.conditions['robots'] = true;
        parent.flowNavigator.next(robotScreenCaller.MANAGE);
    };

    this.userSettings = function() {
        that.conditions['user'] = true;
        parent.flowNavigator.next();
    };

    this.generalSettings = function() {
        that.conditions['general'] = true;
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
})
console.log('loaded file: settings_ViewModel.js');
