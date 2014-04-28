resourceHandler.registerFunction('createAccount_ViewModel.js', function(parent) {
    console.log('instance created for: createAccount_ViewModel');
    var that = this;
    this.conditions = {};
    this.email = ko.observable('');
    this.password = ko.observable('');
    this.password_verify = ko.observable('');
    this.showPassword = ko.observable(false);


    // mark email input as invalid after some delay after user stops typing if input is not valid
    this.markInvalidEmail = ko.computed(function() {
        var validationResult = isEmailValid();

        if(!validationResult) {
            parent.notification.showLoadingArea(true, notificationType.HINT, $.i18n.t("validation.email"));
        }

        return !validationResult;
    }, this).extend({ throttle: 2000 });
    
    this.passwordValid = ko.computed(function() {
        return isPasswordValid();
    }, this);

    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.isFilledOut = ko.computed(function() {
        return (this.email() != '' && this.password() != '' && this.password().length == this.password_verify().length);
    }, this);

    this.isValid = ko.computed(function() {
        return isPasswordValid() && isEmailValid();
    }, this);
    
    this.isPasswordVerified = ko.observable(true);
    
    this.verifyPassword = function() {
        if(that.password_verify() != that.password()) {
            that.isPasswordVerified(false);
        }
    };
    
    this.next = function() {
        that.conditions['valid'] = true;
		
        var userBundle = {
			userlogin: 	false,
            email:		that.email(),
            password: 	that.password()
        };
		
        parent.flowNavigator.next( userBundle );
    };
    
    function isPasswordValid() {
        if (that.password_verify() == '') {
            return true;
        } else {
            if(that.password_verify() == that.password()) {
                that.isPasswordVerified(true);
            }
            return (that.password_verify() == that.password());
        }
    }

    // mark password input as invalid after some delay after user stops typing, if passwords don't concur
    this.markInvalidPassword = ko.computed(function() {
        var validationResult = that.isPasswordVerified();

        if(!validationResult) {
            parent.notification.showLoadingArea(true, notificationType.HINT, $.i18n.t("validation.password"));
        }

        return !validationResult;
    }, this).extend({ throttle: 2000 });

    function isEmailValid() {
        if (that.email() == '') {
            return true;
        } else {
            return parent.config.emailRegEx.test(that.email());
        }
    }
    
    // viewmodel deinit, destroy objects and remove event listener
    this.deinit = function() {
        that.markInvalidEmail.dispose();
        that.passwordValid.dispose();
        that.isFilledOut.dispose();
        that.isValid.dispose();
        that.markInvalidPassword.dispose();
    };

});
console.log('loaded file: createAccount_ViewModel.js');
