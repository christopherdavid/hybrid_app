resourceHandler.registerFunction('s1-2-3_ViewModel.js', 's1-2-3_ViewModel', function(parent) {
    console.log('instance created for: s1-2-3_ViewModel');
    var that = this;
    this.id = 's1-2-3_ViewModel';
    this.conditions = {};
    this.robotId = ko.observable('');
    
    this.back = function() {
        parent.flowNavigator.previous();
    };
    
    this.isFilledOut = ko.computed(function() {
        return (that.robotId() != '');
    }, this);

    this.next = function() {
        // send the robotId to the server for validation
        var email = parent.communicationWrapper.dataValues["user"].email;
        parent.communicationWrapper.exec(UserPluginManager.associateRobot, [email, that.robotId()], that.robotIdSuccess, that.robotIdError);
    };
    
    this.robotIdSuccess = function(result) {
    	that.conditions['robotIdValid'] = true;
    	    	
    	// Pass the server robot and the caller to the next view
    	var robotBundle = {callerContext : that.bundle, robot : {robotId : that.robotId(), robotName : "TODO: Use the correct robot name!"}};
        parent.flowNavigator.next(robotBundle);
    }
    
    this.robotIdError = function(error) {
    	console.log("robotId invalid:" + error.errorMessage);
    }
    
    this.reload = function(){
    	that.conditions = {};
    }
})
console.log('loaded file: s1-2-3_ViewModel.js');
