resourceHandler.registerFunction('robotActivationId_ViewModel.js', function(parent) {
    console.log('instance created for: robotActivationId_ViewModel');
    var that = this;
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
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.associateRobot, [email, that.robotId()]);
        tDeffer.done(that.robotIdSuccess);
        tDeffer.fail(that.robotIdError);
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
console.log('loaded file: robotActivationId_ViewModel.js');
