resourceHandler.registerFunction('robotActivationDone_ViewModel.js', function(parent) {
    console.log('instance created for: robotActivationDone_ViewModel');
    var that = this;
    this.conditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    
    this.init = function() {
        parent.notification.showLoadingArea(true, notificationType.HINT, $.i18n.t('communication.robot_added', {robotName:that.robot().robotName()}));
    }

    this.back = function() {
        parent.flowNavigator.previous();
    };

    this.next = function() {
        that.conditions['homeScreen'] = true;
        parent.flowNavigator.next();
    };

    this.reload = function() {
        that.conditions = {};
    }
})
console.log('loaded file: robotActivationDone_ViewModel.js');
