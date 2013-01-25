resourceHandler.registerFunction('s3-1-1_ViewModel.js', 's3-1-1_ViewModel', function(parent) {
    console.log('instance created for: s3-1-1_ViewModel');
    var that = this;
    this.id = 's3-1-1_ViewModel';
    this.conditions = {};
    
    this.back = function(){
        parent.flowNavigator.previous();
    }

    this.roomManagement = function() {
        that.conditions['rooms'] = true;
        parent.flowNavigator.next();
    };

    this.robotManagement = function() {
        that.conditions['robots'] = true;
        parent.flowNavigator.next(robotScreenCaller.MANAGE);
    };
    
    this.testArea = function() {
        that.conditions['test'] = true;
        parent.flowNavigator.next();
    }

    this.reload = function() {
        // remove conditions
        that.conditions = {};
    }
})
console.log('loaded file: s3-1-1_ViewModel.js');
