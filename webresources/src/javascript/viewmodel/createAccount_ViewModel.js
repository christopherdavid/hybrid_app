resourceHandler.registerFunction('createAccount_ViewModel.js', function(parent) {
    console.log('instance created for: createAccount_ViewModel');
    var that = this;
    this.conditions = {};
    this.email = ko.observable('');
    this.password = ko.observable('');
    this.password_verify = ko.observable('');
    this.showPassword = ko.observable(false);
    
    this.mailValid = ko.computed(function() {
        return !isEmailValid();
    }, this);
    
    this.passwordComplex = ko.computed(function() {
        return !isPasswordComplex();
    }, this);
    
    this.passwordValid = ko.computed(function() {
        return !isPasswordValid();
    }, this);

    var PASSWORD_LENGTH = 6;
    var USERNAME_LENGTH = 4;

    var emailRegEx = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
    // regex from happy: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/

    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.isFilledOut = ko.computed(function() {
        return (this.email() != '' && this.password() != '' && this.password_verify() != '');
    }, this);

    this.isValid = ko.computed(function() {
        return isPasswordValid() && isEmailValid() && isPasswordComplex();
    }, this);

    this.next = function() {
        //TODO remove username from create user methode
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.createUser, [that.email(), that.password(), 'default'], {}, "user");
        tDeffer.done(that.successRegister);
        tDeffer.fail(that.errorRegister);
    };

    this.successRegister = function(result) {
        that.conditions['valid'] = true;
        parent.flowNavigator.next(robotScreenCaller.REGISTER);
    }

    this.errorRegister = function(error) {
        that.conditions['valid'] = false;
        console.log("error: " + error);
    }
    function isPasswordComplex() {
        if (that.password() == '')
            return true;
        return (that.password().length >= PASSWORD_LENGTH);
    }

    function isPasswordValid() {
        if (that.password_verify() == '')
            return true;
        return (that.password_verify() == that.password() && isPasswordComplex());
    }

    function isEmailValid() {
        if (that.email() == '')
            return true;
        return (that.email() != '' && emailRegEx.test(that.email()));
    }

})
console.log('loaded file: createAccount_ViewModel.js');