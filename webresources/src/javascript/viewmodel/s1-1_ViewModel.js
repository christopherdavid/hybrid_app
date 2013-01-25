resourceHandler.registerFunction('s1-1_ViewModel.js', 's1-1_ViewModel', function(parent) {
    console.log('instance created for: s1-1_ViewModel');
    var that = this;
    this.id = 's1-1_ViewModel';
    this.conditions = {};
    this.login = function() {
        that.conditions['login'] = true;
        parent.flowNavigator.next();
    };
    
    this.register = function() {
        that.conditions['register'] = true;
        parent.flowNavigator.next();
    };
    this.map = function() {
        that.conditions['map'] = true;
        parent.flowNavigator.next();
    };
    this.reload = function() {
        // remove conditions
        that.conditions = {};
    }
    this.schedule = function(){
    	that.conditions['schedule'] = true;
        parent.flowNavigator.next();
    }    
})
console.log('loaded file: s1-1_ViewModel.js');
