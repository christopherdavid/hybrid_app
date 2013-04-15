resourceHandler.registerFunction('robotActivationDone_ViewModel.js', function(parent) {
    console.log('instance created for: robotActivationDone_ViewModel');
    var that = this;
    this.conditions = {};

    this.back = function() {
        parent.flowNavigator.previous();
    };

    this.next = function() {
        that.conditions['homeScreen'] = true;
        parent.flowNavigator.next();
    };

    this.reload = function() {
        that.conditions = {};
    }
})
console.log('loaded file: robotActivationDone_ViewModel.js');
