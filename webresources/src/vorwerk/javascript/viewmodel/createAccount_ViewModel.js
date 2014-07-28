resourceHandler.registerFunction('createAccount_ViewModel.js', function(parent) {
    console.log('instance created for: createAccount_ViewModel');
    var that = this;
    this.conditions = {};
    this.email = ko.observable('');
    this.password = ko.observable('');
    this.password_verify = ko.observable('');
    this.showPassword = ko.observable(false);
    
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
    
    this.back = function() {
        parent.flowNavigator.previous();
    };
    
    this.next = function() {
        // create new account & submit country information
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.createUser3, [
            that.email(), that.password(), "default","", 
                {   "country_code": that.bundle.country, 
                    "opt_in":       that.bundle.optIn
                }]);
        
        tDeffer.done(that.successUserCreation);
    };
    
    /*
     * Inform user that account has been created and show e mail validation information and enable further navigation (ok button).
     */
    this.successUserCreation = function() {
        var translatedTitle = $.i18n.t("createAccount.page.registration_done_title");
        var translatedText = $.i18n.t("createAccount.page.registration_done_message", {email:that.email()});
        
        parent.notification.showDialog(
            dialogType.INFO,
            translatedTitle,
            translatedText,
            [],
            that.leaveAccountCreation
        );
    };
    
    /*
     * Navigate to next screen after user has been informed that account has been created.
     */
    this.leaveAccountCreation = function() {
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.isUserValidated, [that.email()],{});
        tDeffer.done(function(result) {
            if (result.validation_status == USER_STATUS_VALIDATED) {
                that.backgroundLogin();
            } else {
                // go to start screen if user hasn't validated his email so far
                that.conditions['start'] = true;
                parent.flowNavigator.next();
            }
        });
        tDeffer.fail(function(error, notificationOptions, errorHandled) {
            errorHandled.resolve();
            // go to start screen if there is an error in during validation check
            that.conditions['start'] = true;
            parent.flowNavigator.next();
        });
    };
    
    this.backgroundLogin = function(result) {
        // TODO: add validation check for entries
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.login, [that.email(), that.password()], {});
        tDeffer.done(that.sucessLogin);
        
        tDeffer.fail(function(error, notificationOptions, errorHandled) {
            errorHandled.resolve();
            // go to start screen if there is an error in background login
            that.conditions['start'] = true;
            parent.flowNavigator.next();
        });
    };

    this.sucessLogin = function(result, notifyOptions) {
        that.conditions['robotSelection'] = true;
        console.log("result: " + result);
        parent.communicationWrapper.setDataValue("user", result);
        user = parent.communicationWrapper.getDataValue("user");
        
        // register for push notifications (from server)
        parent.notification.registerForRobotMessages();
        
        // register for notifications from robot if app is running
        parent.notification.registerForRobotNotifications();
        
        parent.communicationWrapper.saveToLocalStorage('username', user.email);
        that.conditions['robotSelection'] = true;
        parent.flowNavigator.next(robotScreenCaller.LOGIN);
    };
    

});
console.log('loaded file: createAccount_ViewModel.js');
