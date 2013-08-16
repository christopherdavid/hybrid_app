resourceHandler.registerFunction('header_ViewModel.js', function(parent) {
    console.log('instance created for: header_ViewModel');
    var that = this;
    this.conditions = {};
    
    this.init = function() {}
    this.reload = function() {
        // remove conditions
        that.conditions = {};
    };
    
    this.titleText = ko.observable("Header");
    this.robotName = ko.observable("");
    this.showRobotName = ko.observable(false);
    
    this.leftButton = ko.observable("");
    this.rightButton = ko.observable("");
    this.showLeftButton = ko.observable(false);
    this.showLeftButtonIcon = ko.observable(false);
    this.showRightButton = ko.observable(false);
    this.showRightButtonIcon = ko.observable(false);
    
    
})
console.log('loaded file: header_ViewModel.js');
