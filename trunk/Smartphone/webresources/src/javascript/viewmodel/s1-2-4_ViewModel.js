resourceHandler.registerFunction('s1-2-4_ViewModel.js', function(parent) {
    console.log('instance created for: s1-2-4_ViewModel');
    var that = this;
    this.conditions = {};
    this.robotName = ko.observable('');
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
    this.isFilledOut = ko.computed(function() {
        return (that.robotName() != '');
    }, this);

    this.next = function() {
        parent.communicationWrapper.exec(RobotPluginManager.setRobotName, [that.bundle.robot.robotId, that.robotName()], that.robotNameSuccess, that.robotNameError);

        parent.flowNavigator.next();
    };
    
    this.robotNameSuccess = function(result) {
    	console.log("robotNameSuccess " + JSON.stringify(result));
    	that.conditions['robotNameValid'] = true;
    	that.bundle.robot.robotName = that.robotName();    	
    	
        // Set the robot as active one if required 
        if (that.bundle){
            parent.communicationWrapper.storeDataValue("activeRobot", that.bundle.robot);
        }

        parent.flowNavigator.next();
        //TODO replace with correct message
        parent.showLoadingArea(true, notificationType.HINT, "added " + that.robotName(), false);
    }
    
    this.robotNameError = function(error) {
    	console.log("robotName can't be set:" + error);
    }
    
    this.reload = function(){
    	that.conditions = {};
    }
})
console.log('loaded file: s1-2-4_ViewModel.js');
