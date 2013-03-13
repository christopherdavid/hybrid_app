resourceHandler.registerFunction('s1-3-1_ViewModel.js', function(parent) {
    console.log('instance created for: s1-3-1_ViewModel');
    var that = this;
    this.conditions = {};
    this.email = ko.observable();
    this.password = ko.observable();
    /*this.email = ko.observable('uid1@demo.com');
    this.password = ko.observable('test123');*/
    

    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
    this.isFilledOut = ko.computed(function() {
        return (this.email() != '' && this.password() != '');
    }, this);
    
    this.passwordLost = function(){
    	// TODO: What will be triggered here
    	console.log("password lost");
    }

    this.next = function() {
        // TODO: add validation check for entries
        parent.communicationWrapper.exec(UserPluginManager.login, [that.email(), that.password()], that.sucessLogin, that.errorLogin, {}, "user");
    };
    
    this.sucessLogin = function(result) {        
        that.conditions['valid'] = true;
        console.log("result: " + result)
        parent.flowNavigator.next(robotScreenCaller.LOGIN);
    };
    
    this.errorLogin = function(error) {
        console.log("Error: " + error.errorMessage);
    };
    
    this.reload = function() {
    	this.conditions = {};
    }
})
console.log('loaded file: s1-3-1_ViewModel.js');
