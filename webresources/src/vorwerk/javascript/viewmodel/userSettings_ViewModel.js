resourceHandler.registerFunction('userSettings_ViewModel.js', function(parent) {
    console.log('instance created for: userSettings_ViewModel');
    var that = this;
    this.conditions = {};
    this.backConditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    
    this.showPassword = ko.observable(false);
    this.newPassword = ko.observable("");
    this.newPasswordRepeat = ko.observable("");
    this.oldPassword = ko.observable("");
    
    this.user = ko.observable();
    this.selectedCountryCode = ko.observable('');
    this.selectedCountryLabel = ko.observable('');
    
    var countryOrder = $.map($.i18n.t("pattern.countryOrder").split(","), function(value){
        return value;
        });
    this.countries = ko.observableArray([]);
    var countriesRendered = false;
    
    this.isPasswordValid = ko.computed(function() {
        if (that.newPasswordRepeat() == '') {
            return true;
        } else {
            return (that.newPasswordRepeat() == that.newPassword());
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
        if(that.newPasswordRepeat() == ""){
            return false;
        } else if(that.newPasswordRepeat().length > 0 && that.isPasswordValid()) {
            return false;
        } else if(typeof that.isThrottledPasswordValid() != "undefined" && that.isThrottledPasswordValid()) {
            return false;
        }
        return true;
    }, this);
    
    
    this.okEnabled = ko.computed(function() {
        return that.newPassword().length != '' &&  that.oldPassword().length != '' && that.isPasswordValid();
    }, this);
    
    this.init = function() {
        var user = parent.communicationWrapper.getDataValue("user");
        var uCountryCode = (user.extra_param && user.extra_param.countryCode) ? user.extra_param.countryCode : null;
        that.user(ko.mapping.fromJS(user), null, that.user);
        
        if(uCountryCode && uCountryCode != "null" && $.inArray(uCountryCode, countryOrder) != -1) {
                that.selectedCountryCode(uCountryCode);
        } else {
            that.selectedCountryCode("0");
        }
        that.selectedCountryLabel($.i18n.t("common.countries." + that.selectedCountryCode()));
        this.initCountry(that.selectedCountryCode());
    };
    
    // viewmodel deinit, destroy objects and remove event listener
    this.deinit = function() {
        that.isPasswordValid.dispose();
        that.isThrottledPasswordValid.dispose();
        that.markPasswordInvalid.dispose();
        that.okEnabled.dispose();
        that.countries([]);
        countriesRendered = false;
    };
    
    this.changeCountry = function() {
    	that.conditions['changeCountry'] = true;
        parent.flowNavigator.next({"userlogin":true});
    };
    
    this.changeSubscription  = function() {
        that.conditions['changeSubscription'] = true;
        parent.flowNavigator.next({"country":that.selectedCountryCode(),"userlogin":true});
    };
       
    this.changePassword = function() {
        parent.notification.showDomDialog("#changePasswordPopup");
    };
    
    this.cancelEdit = function() {
        $("#changePasswordPopup").popup("close");
    };
    
    this.commitEdit = function() {
        console.log("change password of user with email: " + that.user().email() + " to " + that.newPassword());
        $("#changePasswordPopup").popup("close");
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.changePassword, [that.user().email(), that.oldPassword(), that.newPassword()]);
        tDeffer.done(that.successChangePassword);
    };
    
    this.successChangePassword = function(result) {
        console.log("result" + JSON.stringify(result));
        that.showPassword = ko.observable(false);
        that.newPassword = ko.observable("");
        that.newPasswordRepeat = ko.observable("");
        that.oldPassword = ko.observable("");
    };
    
    this.logout = function() {
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.logout, []);
        tDeffer.done(that.successLogout);
        tDeffer.fail(that.errorLogout);
    };
    
    this.successLogout = function(result) {
        // Clear the data values on logout.
        parent.communicationWrapper.clearDataValues();
        that.backConditions['logout'] = true;
        parent.flowNavigator.previous();
    };

    this.errorLogout = function(error) {
        console.log("Error (Logout): " + error.errorMessage);
    };
    
    this.back = function() {
      that.conditions['settings'] = true;
      parent.flowNavigator.next();
    };
    
    this.reload = function() {
        this.conditions = {};
    };
    
    this.initCountry = function(appCountry) {
        
       
        // fill country list
        for (var i = 0; i < countryOrder.length; i++) {
            that.countries.push({
                "label" : $.i18n.t("common.countries." + countryOrder[i]),
                "value" : countryOrder[i]
            });
        }
        // get country code of language string e.g. 'de-DE' -> 'DE,'en-GB' -> 'GB'
       // var appCountry = that.user().countryCode();
        
        // check if country has already been selected (back button in workflow was pressed)
        if(typeof that.selectedCountryCode() == 'undefined') {
            // check if appCountry is a selectable country otherwise select other
            if($.inArray(appCountry, countryOrder) != -1) {
                that.selectedCountryCode(appCountry);
            } else {
                that.selectedCountryCode("0");
            }
        }
    };
    
     this.renderedCountries = function(element, data) {
        console.log("renderedCountries");
        if(!countriesRendered) {
            // check if selected country is complete and control could be initialized
            if($(element).parent().children().length == that.countries().length) {
                $("#countrySelectionList").controlgroup();
                $("#countrySelectionList").attr("data-role", "controlgroup");
                $("input[type='radio']",element.parent).checkboxradio();
                $("#countrySelectionListContainer").data( "init", true );
                countriesRendered = true;
            }
        }
    };
    
});
console.log('loaded file: userSettings_ViewModel.js');
