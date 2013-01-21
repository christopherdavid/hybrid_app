resourceHandler.registerFunction('s1-2-6_ViewModel.js', 's1-2-6_ViewModel', function(parent) {
    console.log('instance created for: s1-2-6_ViewModel');
    var that = this;
    this.id = 's1-2-6_ViewModel';
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
