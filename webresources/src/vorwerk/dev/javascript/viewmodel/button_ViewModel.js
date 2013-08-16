resourceHandler.registerFunction('button_ViewModel.js', function(parent) {
    console.log('instance created for: button_ViewModel');
    var that = this;
    this.conditions = {};
    
    this.init = function() {}
    this.reload = function() {
        // remove conditions
        that.conditions = {};
    };
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
})
console.log('loaded file: button_ViewModel.js');
