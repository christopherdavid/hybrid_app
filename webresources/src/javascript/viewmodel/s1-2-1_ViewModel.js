resourceHandler.registerFunction('s1-2-1_ViewModel.js', 's1-2-1_ViewModel', function(parent) {
    console.log('instance created for: s1-2-1_ViewModel');
    var that = this;
    this.id = 's1-2-1_ViewModel';
    this.conditions = {};
    this.name = ko.observable('');
    this.email = ko.observable('');
    this.password = ko.observable('');
    this.password_verify = ko.observable('');
    this.nameValid = ko.computed(function(){return !isUsernameValid();},this);
    this.mailValid = ko.computed(function(){return !isEmailValid(); },this);
    this.passwordComplex = ko.computed(function(){return !isPasswordComplex(); },this);
    this.passwordValid = ko.computed(function(){return !isPasswordValid(); },this);
    
    var PASSWORD_LENGTH = 6;
    var USERNAME_LENGTH = 4;

    var emailRegEx = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
    this.isFilledOut = ko.computed(function() {
        return (this.name() != '' && this.email() != '' && this.password() != '' && this.password_verify() != '');
    }, this);
    
    
    this.isValid = ko.computed(function(){
    	
    	return isPasswordValid() && isEmailValid() && isUsernameValid() && isPasswordComplex();
    	
    },this);

    this.next = function() {
        // TODO: add validation check for formular        
        parent.communicationWrapper.exec(UserPluginManager.createUser, [that.email(), that.password(), that.name()], that.successRegister, that.errorRegister, {}, "user");
    };
    
    this.successRegister = function (result) {
    	that.conditions['valid'] = true;
        parent.flowNavigator.next(robotScreenCaller.REGISTER);
    }
    
    this.errorRegister = function(error) {
    	that.conditions['valid'] = false;
    	console.log("error: " + error);
    }
    
    function isUsernameValid(){
    	if(that.name() == '') return true;
    	return (that.name().length >= USERNAME_LENGTH);
    }
    
    function isPasswordComplex(){
    	if(that.password() == '') return true;
    	return (that.password().length >= PASSWORD_LENGTH);
    }
    
    function isPasswordValid(){
    	if(that.password_verify() == '') return true;
    	return (that.password_verify() == that.password() && isPasswordComplex());
    }
    
    function isEmailValid(){
    	if(that.email() == '') return true;
    	return (that.email() != '' && emailRegEx.test(that.email()));
    }
})
console.log('loaded file: s1-2-1_ViewModel.js');
