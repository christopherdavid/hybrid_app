resourceHandler.registerFunction('terms_ViewModel.js', function(parent) {
    console.log('instance created for: terms_ViewModel');
    var that = this;
    this.conditions = {};
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
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
    this.reload = function() {
        this.conditions = {};
    }
    
})
console.log('loaded file: terms_ViewModel.js');
