resourceHandler.registerFunction('loginUser_ViewModel.js', function(parent) {
    console.log('instance created for: loginUser_ViewModel');
    var that = this;
    this.conditions = {};
    this.email = ko.observable("");
    this.password = ko.observable("");
    this.showPassword = ko.observable(false);
    this.emailInvalidGuid;

    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.isFilledOut = ko.computed(function() {
        return (this.email() != '' && this.password() != '');
    }, this);
    
    this.isEmailValid = ko.computed(function() {
        if (that.email() == '') {
            return true;
        } else {
            return parent.config.emailRegEx.test(that.email());
        }
    },this);

    // mark email input as invalid after some delay after user stops typing if input is not valid
    this.markEmailInvalid = ko.computed(function() {
        var validationResult = that.isEmailValid();

        if(!validationResult) {
            parent.notification.showLoadingArea(true, notificationType.HINT, $.i18n.t("validation.email"));
        }

        return !validationResult;
    }, this).extend({ throttle: 2000 });
    
    // viewmodel deinit, destroy objects and remove event listener
    this.deinit = function() {
        that.isFilledOut.dispose();
        that.isEmailValid.dispose();
        that.markEmailInvalid.dispose();
    };

    this.passwordLost = function() {
        console.log("password lost, show dialog");
        parent.notification.showDomDialog("#passwordLostPopup");
    };
    
    this.cancelReset = function() {
        $("#passwordLostPopup").popup("close");
    };
    
    this.commitReset = function() {
        console.log("reset password of user with email: " + that.email());
        $("#passwordLostPopup").popup("close");
        var sReset = $.i18n.t('communication.reset_password');
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.forgetPassword, [that.email()], 
            { type: notificationType.OPERATION, message: sReset});
        tDeffer.done(that.sucessReset);
        tDeffer.fail(that.errorReset);
    };
    
    this.sucessReset = function(result, notifyOptions) {
        console.log("result: " + result);
        var sReset = $.i18n.t('communication.reset_password_done');
        parent.notification.showLoadingArea(true, notificationType.HINT, sReset);
    };
    
    this.errorReset = function(error) {
        console.log("Error: " + error.errorMessage);
    };

    this.next = function() {
        // TODO: add validation check for entries
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.login, [that.email(), that.password()], {});
        tDeffer.done(that.sucessLogin);
        tDeffer.fail(that.errorLogin);
    };

    this.sucessLogin = function(result, notifyOptions) {
		if (result.validation_status == USER_STATUS_VALIDATED) {
		    that.conditions['valid'] = true;
			console.log("result: " + result);
			parent.communicationWrapper.setDataValue("user", result);
        
			// register for push notifications (from server)
			parent.notification.registerForRobotMessages();
        
			// register for notifications from robot if app is running
			parent.notification.registerForRobotNotifications();
        
			parent.communicationWrapper.saveToLocalStorage('username', that.email());
			parent.flowNavigator.next(robotScreenCaller.LOGIN);
		} else {
			translatedTitle = $.i18n.t("validation.error_title");
			translatedText = $.i18n.t("validation.error_message");
		
			parent.notification.showDialog(
				dialogType.ERROR, 
				translatedTitle, 
				translatedText, 
				[{
					"label":$.i18n.t("common.ok"), 
					"callback":function(e){ parent.notification.closeDialog(); }
				}]
			);
		}
    };

    this.errorLogin = function(error) {
        console.log("Error: " + error.errorMessage);
    };

    this.reload = function() {
        this.conditions = {};
    };
    
});
console.log('loaded file: loginUser_ViewModel.js');
