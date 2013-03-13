resourceHandler.registerFunction('s1-2-6_ViewModel.js', function(parent) {
    console.log('instance created for: s1-2-6_ViewModel');
    var that = this;
    this.conditions = {};
    
    this.back = function() {
        parent.flowNavigator.previous();
    };

    this.next = function() {
        that.conditions['homeScreen'] = true;
        parent.flowNavigator.next();
    };
    
    this.reload = function(){
    	that.conditions = {};
    }
})
console.log('loaded file: s1-2-6_ViewModel.js');
