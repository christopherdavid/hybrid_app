resourceHandler.registerFunction('settings_ViewModel.js', function(parent) {
    console.log('instance created for: settings_ViewModel');
    var that = this;
    this.conditions = {};
    this.backConditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    
    this.init = function() {
        // check if country is italy. if so change product logo
        var user = parent.communicationWrapper.getDataValue("user");
        var country = parent.communicationWrapper.getFromLocalStorage(user.email + "_country");
        if(country && country == "italy") {
            $("#menuPopupLogo").addClass("folletto");
        }
    };
    
    this.back = function() {
        parent.flowNavigator.previous();
    };

    this.robotManagement = function() {
        that.conditions['robotManagement'] = true;
        parent.flowNavigator.next();
    };

    this.userSettings = function() {
        that.conditions['userSettings'] = true;
        parent.flowNavigator.next();
    };
    
    this.generalSettings = function() {
        that.conditions['generalSettings'] = true;
        parent.flowNavigator.next();
    };
    
    this.terms = function() {
        that.conditions['terms'] = true;
        parent.flowNavigator.next();
    }
    
    this.about = function() {
        that.conditions['about'] = true;
        parent.flowNavigator.next();
    }
    
    this.weblink = function() {
        // open language specific link in new window
        var url = $.i18n.t("settings.page.link_url");
        openExternalLink(url);
    }

    this.reload = function() {
        // remove conditions
        that.conditions = {};
    }
    
    // popup links
    this.cleaning = function() {
        that.conditions['cleaning'] = true;
        parent.flowNavigator.next();
    }
    this.schedule = function() {
        that.conditions['schedule'] = true;
        parent.flowNavigator.next();
    }
    this.settings = function() {
        $("#menuPopup").popup("close");
    }
    
})
console.log('loaded file: settings_ViewModel.js');
