resourceHandler.registerFunction('createAccount_ViewModel.js', function(parent) {
    console.log('instance created for: createAccount_ViewModel');
    var that = this;
    this.conditions = {};
    this.email = ko.observable('');
    this.password = ko.observable('');
    this.password_verify = ko.observable('');
    this.showPassword = ko.observable(false);
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.isFilledOut = ko.computed(function() {
        return (this.email() != '' && this.password() != '' && this.password().length == this.password_verify().length);
    }, this);

    this.isEmailValid = ko.computed(function() {
        if (that.email() == '') {
            return true;
        } else {
            return parent.config.emailRegEx.test(that.email());
        }
    },this);
    
    this.isThrottledEmailValid = ko.computed(function() {
        if(!that.isEmailValid()) {
            parent.notification.showLoadingArea(true, notificationType.HINT, $.i18n.t("validation.email"));
        }
        return that.isEmailValid();
    }, this).extend({ throttle: 2000 });

    // mark email input as invalid after some delay after user stops typing if input is not valid
    this.markEmailInvalid = ko.computed(function() {
        if(that.email() == ""){
            return false;
        } else if(that.email().length > 0 && that.isEmailValid()) {
            return false;
        } else if(typeof that.isThrottledEmailValid() != "undefined" && that.isThrottledEmailValid()) {
            return false;
        }
        return true;
    }, this);
    
    this.isPasswordValid = ko.computed(function() {
        if (that.password_verify() == '') {
            return true;
        } else {
            return (that.password_verify() == that.password());
        }
    }, this);
    
    this.isThrottledPasswordValid = ko.computed(function() {
        if(!that.isPasswordValid()) {
            parent.notification.showLoadingArea(true, notificationType.HINT, $.i18n.t("validation.password"));
        }
        return that.isPasswordValid();
    }, this).extend({ throttle: 2000 });
    
    // mark password input as invalid after some delay after user stops typing, if passwords don't concur
    this.markPasswordInvalid = ko.computed(function() {
        if(that.password_verify() == ""){
            return false;
        } else if(that.password_verify().length > 0 && that.isPasswordValid()) {
            return false;
        } else if(typeof that.isThrottledPasswordValid() != "undefined" && that.isThrottledPasswordValid()) {
            return false;
        }
        return true;
    }, this);

    this.isValid = ko.computed(function() {
        return that.isPasswordValid() && that.isEmailValid();
    }, this);
    
    
    this.next = function() {
        that.conditions['valid'] = true;
		
        var userBundle = {
			userlogin: 	false,
            email:		that.email(),
            password: 	that.password()
        };
		
        parent.flowNavigator.next( userBundle );
    };
    
    
    // viewmodel deinit, destroy objects and remove event listener
    this.deinit = function() {
        that.isFilledOut.dispose();
        that.isEmailValid.dispose();
        that.isThrottledEmailValid.dispose();        
        that.markEmailInvalid.dispose();
        that.isPasswordValid.dispose();
        that.isThrottledPasswordValid.dispose();
        that.markPasswordInvalid.dispose();
        that.isValid.dispose();
    };

});
console.log('loaded file: createAccount_ViewModel.js');
