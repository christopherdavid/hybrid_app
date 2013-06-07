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
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.createUser2, [that.email(), that.password(), 'default'], {});
        tDeffer.done(that.successRegister);
        tDeffer.fail(that.errorRegister);
    };

    this.successRegister = function(result) {
        that.conditions['valid'] = true;
        parent.communicationWrapper.setDataValue("user", result);
        var translatedTitle = $.i18n.t("createAccount.page.registration_done_title");
        var translatedText = $.i18n.t("createAccount.page.registration_done_message");
        parent.notification.showDialog(dialogType.WARNING, translatedTitle, translatedText, [{"label":"Ok", "callback":function(e){ parent.notification.closeDialog(); parent.flowNavigator.next(robotScreenCaller.REGISTER);}}]);
    }

    this.errorRegister = function(error) {
        that.conditions['valid'] = false;
        console.log("error: " + error);
    }
    function isPasswordComplex() {
        // if (that.password() == '')
            // return true;
        // return (that.password().length >= PASSWORD_LENGTH);        return true;
    }

    function isPasswordValid() {
        if (that.password_verify() == '')
            return true;
        return (that.password_verify() == that.password() && isPasswordComplex());
    }

    function isEmailValid() {
        if (that.email() == '')
            return true;
        return (that.email() != '' && parent.config.emailRegEx.test(that.email()));
    }

})
console.log('loaded file: createAccount_ViewModel.js');
