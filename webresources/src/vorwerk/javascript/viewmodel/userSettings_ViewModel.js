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
    
    
    var PASSWORD_LENGTH = 6;

    
    this.passwordValid = ko.computed(function() {
        return !isPasswordValid();
    }, this);
    
    okEnabled = ko.computed(function() {
        return isOkButtonEnabled();
    }, this);
    
    function isOkButtonEnabled() {
        return that.newPassword().length != '' &&  that.oldPassword().length != '' && isPasswordValid();
    }
    
    function isPasswordValid() {
        if (that.newPasswordRepeat() == '')
            return true;
        return (that.newPasswordRepeat() == that.newPassword() && isPasswordComplex());
    }
    
    function isPasswordComplex() {
        // if (that.newPassword() == '')
            // return true;
        // return (that.newPassword().length >= PASSWORD_LENGTH);        return true;
    }
    
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
    this.changeCountry = function() {
    	that.conditions['changeCountry'] = true;
        parent.flowNavigator.next({"userlogin":true});
    	
    }
    
     this.changeSubscription  = function() {
        that.conditions['changeSubscription'] = true;
        parent.flowNavigator.next({"country":that.selectedCountryCode(),"userlogin":true});
    }
    
       
    this.changePassword = function() {
        $("#changePasswordPopup").popup("open");
    };
    
    this.cancelEdit = function() {
        $("#changePasswordPopup").popup("close");
    }
    
    this.commitEdit = function() {
        console.log("change password of user with email: " + that.user().email() + " to " + that.newPassword());
        $("#changePasswordPopup").popup("close");
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.changePassword, [that.user().email(), that.oldPassword(), that.newPassword()]);
        tDeffer.done(that.successChangePassword);
    }
    
    this.successChangePassword = function(result) {
        console.log("result" + JSON.stringify(result));
        that.showPassword = ko.observable(false);
        that.newPassword = ko.observable("");
        that.newPasswordRepeat = ko.observable("");
        that.oldPassword = ko.observable("");
    }
    
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
    }
    
    this.back = function() {
      that.conditions['settings'] = true;
      parent.flowNavigator.next();
    };
    
    this.reload = function() {
        this.conditions = {};
    }
    
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
    }
    
     this.renderedCountries = function(element, data) {
        console.log("renderedCountries")
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
    }
    
})
console.log('loaded file: userSettings_ViewModel.js');
