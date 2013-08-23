resourceHandler.registerFunction('form_ViewModel.js', function(parent) {
    console.log('instance created for: form_ViewModel');
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
console.log('loaded file: form_ViewModel.js');
