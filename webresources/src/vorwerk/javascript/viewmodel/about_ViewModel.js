resourceHandler.registerFunction('about_ViewModel.js', function(parent) {
    console.log('instance created for: about_ViewModel');
    var that = this;
    this.conditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.version = ko.observable(parent.config.version);
    this.pluginVersion = ko.observable(parent.config.pluginVersion);
    this.ServerName = ko.observable(parent.config.serverName);
    
    this.init = function() {
    };
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
    this.reload = function() {
        this.conditions = {};
    };
    
});
console.log('loaded file: about_ViewModel.js');
