resourceHandler.registerFunction('animation_ViewModel.js', function(parent) {
    console.log('instance created for: animation_ViewModel');
    var that = this;
    this.conditions = {};
    this.statusIcon = ko.observable("idle");
    
    this.init = function() {}
    this.reload = function() {
        // remove conditions
        that.conditions = {};
    };
    this.changeStatus = function(newValue) {
        that.statusIcon(newValue);
    }
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
})
console.log('loaded file: animation_ViewModel.js');
