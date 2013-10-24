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
            // TODO: need to be defined how 'other' could be stored on server
            console.log("select other country select GB as temporary fallback")
            that.selectedCountryCode("GB");
        }
        that.selectedCountryLabel($.i18n.t("common.countries." + that.selectedCountryCode()));
        
    };
    this.changeCountry = function() {
        //TODO: will be added if API to update the country code is available
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
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
    this.reload = function() {
        this.conditions = {};
    }
    
})
console.log('loaded file: userSettings_ViewModel.js');
