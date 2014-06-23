resourceHandler.registerFunction('start_ViewModel.js', function(parent) {
    console.log('instance created for: start_ViewModel');
    var that = this;
    this.conditions = {};
    
    this.init = function() {};
    
    this.login = function() {
        that.conditions['login'] = true;
        parent.flowNavigator.next();
    };

    this.register = function() {
        that.conditions['register'] = true;
        parent.flowNavigator.next({"state": pageState.REGISTER});
    };
    
    this.reload = function() {
        // remove conditions
        that.conditions = {};
    };
});
console.log('loaded file: start_ViewModel.js');
