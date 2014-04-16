resourceHandler.registerFunction('legalInformation_ViewModel.js', function(parent) {
    console.log('instance created for: legalInformation_ViewModel');
    var that = this, myScroll;
    this.conditions = {};
    var user = parent.communicationWrapper.getDataValue("user");
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.isLegalinfoEdit = ko.observable(false); 
    this.selectedSubscribe = ko.observable(false);
    this.isAgreed = ko.observable(false);
    
    this.isValid = ko.computed(function() {
        event.stopPropagation();
        return (typeof(that.selectedSubscribe()) != 'undefined');
    }, this);
    
    this.init = function() {
        myScroll = new iScroll('legalWrapper',{
            hScrollbar : false,
            vScrollbar : false,
            bounce : true,
            vScroll : true,
            hScroll : true,
            momentum : true
        });
         
        
        that.isLegalinfoEdit(that.bundle.userlogin);
		
        $(document).one("pageshow.legal", function(e) {
            var tempLine = $("#topLine");
            $("#legalWrapper").css({
                "top":(tempLine.offset().top + tempLine.height())
            });
        
        $("#privacy").click(function(){
           that.showagreement('privacy');
        });
            
        $("#terms").click(function(){
            that.showagreement('terms');
        });
        
            myScroll.refresh();
        });
        
        $(window).on("resize.legal", function(e) {
            var tempLine = $("#topLine");
            $("#legalWrapper").css({
                "top":(tempLine.offset().top + tempLine.height())
            });
        
            myScroll.refresh();
        });
        
        if(that.isLegalinfoEdit()) {
            if(typeof that.bundle.country == 'undefine') {
                that.selectedSubscribe(user.extra_param.optIn);
            }
            //that.isAgreed("true");
            if(that.bundle) {
                if(that.bundle.country == "") {
                    that.bundle.country(user.extra_param.countryCode);
                } else if(that.bundle.country == user.extra_param.countryCode) {
                    that.selectedSubscribe(user.extra_param.optIn);
                }
            }
            	
        }
        
    };
        
    this.deinit = function() {
        $(window).off(".legal");
        myScroll.destroy();
    };
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
    this.showagreement = function(doctype){
        that.conditions = {};
        if(doctype == 'privacy') {
            that.conditions['privacy'] = true;
        } else {
            that.conditions['terms'] = true;
        }
        parent.flowNavigator.next();
    };
    
	/*
	 * Called when user submits changes.
	 */
    this.submitChange = function() {
        event.stopPropagation();
		
		// check if legal information has been agreed on (then continue), otherwise show error dialog
        if (that.isAgreed()) {
            that.commitCountryEdit();
        } else {
			var translatedTitle = null;
			var translatedText = null;
		
			if (that.isLegalinfoEdit()) {
			    translatedTitle = $.i18n.t("legalInformation.page.change_notAccepted_title");
				translatedText = $.i18n.t("legalInformation.page.change_notAccepted_message", {email:that.bundle.email});
			} else {
				translatedTitle = $.i18n.t("legalInformation.page.register_notAccepted_title");
				translatedText = $.i18n.t("legalInformation.page.register_notAccepted_message", {email:that.bundle.email});
			}
			
			parent.notification.showDialog(dialogType.ERROR, translatedTitle, translatedText, [{"label":$.i18n.t("common.ok"), "callback":function(e){ parent.notification.closeDialog(); }}]);
        }    
    };
    
	/*
	 * Deliver changes to server.
	 */
    this.commitCountryEdit = function() { 
		if (that.isLegalinfoEdit()) {
			// edit country information
			var tDeffer = parent.communicationWrapper.exec(UserPluginManager.setUserAccountDetails, [
				user.email, 
				that.bundle.country, 
				that.selectedSubscribe()]
			);
			tDeffer.done(that.successCountryUpdate);
		} else {
			// create new account & submit country information
			var tDeffer = parent.communicationWrapper.exec(UserPluginManager.createUser3, [
				that.bundle.email, 
				that.bundle.password, 
				'default', 
				'', 
				{	"country_code":	that.bundle.country, 
					"opt_in":		that.selectedSubscribe()
				}],{}
			);
			tDeffer.done(that.successUserCreation);
			tDeffer.fail(that.errorRegister);
		}
    };
	
	this.errorRegister = function(error) {
        that.conditions['valid'] = false;
        console.log("errorRegister: " + JSON.stringify(error));
    };
	
	/*
	 * Updates user object, informs user that country information has been updated and continues navigation.
	 */
	this.successCountryUpdate = function() {
		// update user object
	    user.extra_param.countryCode = that.bundle.country;
        user.extra_param.optIn = that.selectedSubscribe();
        parent.communicationWrapper.setDataValue("user", user);
	
		// inform user
		var callGuid = guid();
		parent.notification.showLoadingArea(true, notificationType.HINT, $.i18n.t("legalInformation.page.edit_done_message", {email:that.bundle.email}), callGuid);
		
		// fade out after some time and contiune navigation
		window.setTimeout(function(){
			parent.notification.showLoadingArea(false, notificationType.HINT, "", callGuid);	
			that.navigate();
		}, 3000);
	};
	
    /*
     * Inform user that account has been created and show e mail validation information and enable further navigation (ok button).
	 */
	this.successUserCreation = function() {
		var translatedTitle = $.i18n.t("createAccount.page.registration_done_title");
        var translatedText = $.i18n.t("createAccount.page.registration_done_message", {email:that.bundle.email});
		
        parent.notification.showDialog(
			dialogType.INFO, 
			translatedTitle, 
			translatedText, [{ 
				"label":$.i18n.t("common.ok"), 
				"callback": that.leaveAccountCreation
			}]
		);
	};
	
    /*
	 * Navigate to next screen after user has been informed that account has been created.
	 */
	this.leaveAccountCreation = function() {
		parent.notification.closeDialog();
		
		var tDeffer = parent.communicationWrapper.exec(UserPluginManager.isUserValidated, [that.bundle.email],{});
		tDeffer.done(function(result) {
			if (result.validation_status == USER_STATUS_VALIDATED) {
				that.conditions['robotSelection'] = true;
				that.backgroundLogin();
			} else {
				// go to start screen if user hasn't validated his e mail so far
				that.conditions['start'] = true;
				parent.flowNavigator.next();
			}
		});
		tDeffer.fail(function(error) {
			console.log("Error: " + error.errorMessage);
		});
	};
    
    this.backgroundLogin = function() {
        // TODO: add validation check for entries
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.login, [that.bundle.email, that.bundle.password], {});
        tDeffer.done(that.sucessLogin);
        tDeffer.fail(that.errorLogin);
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
        parent.flowNavigator.next(robotScreenCaller.LOGIN);
    };

    this.errorLogin = function(error) {
        console.log("Error: " + error.errorMessage);
    };
    
    this.navigate = function(){
        if (typeof that.robot().robotId == 'undefined') {
            that.conditions['robotSelection'] = true;
        } else {
			that.conditions['userSettings'] = true;
        }
			
		parent.flowNavigator.next(robotScreenCaller.REGISTER);
    };
});
console.log('loaded file: legalInformation_ViewModel.js');
