resourceHandler.registerFunction('legalInformation_ViewModel.js', function(parent) {
    console.log('instance created for: legalInformation_ViewModel');
    var that = this, myScroll;
    this.conditions = {};
    var user = parent.communicationWrapper.getDataValue("user");
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.isLegalinfoEdit = ko.observable(false); 
    this.selectedSubscribe = ko.observable();
    
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
            
            if(that.bundle) {
                if(that.bundle.country == "") {
                    that.bundle.country(user.extra_param.countryCode);
                } else if(that.bundle.country == user.extra_param.countryCode) {
                    that.selectedSubscribe(user.extra_param.optIn);
                }
            }
            	
        }
        
    }
    
    this.deinit = function() {
        $(window).off(".legal");
        myScroll.destroy();
    }
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.submitChange = function() {
        event.stopPropagation();
        that.commitCountryEdit()
    };

    this.successRegister = function(result) {
        that.conditions['valid'] = true;
        parent.communicationWrapper.setDataValue("user", result);
        //TODO: temporary store selected country in local storage till it could be stored on server 
        parent.communicationWrapper.saveToLocalStorage(that.bundle.email + "_country", that.bundle.country);
        var translatedTitle = $.i18n.t("legalInformation.page.registration_done_title");
        var translatedText = $.i18n.t("legalInformation.page.registration_done_message", {email:that.bundle.email});
        parent.notification.showDialog(dialogType.INFO, translatedTitle, translatedText, [{"label":$.i18n.t("common.ok"), "callback":function(e){ parent.notification.closeDialog(); parent.flowNavigator.next(robotScreenCaller.REGISTER);}}]);
    }

    this.errorRegister = function(error) {
        that.conditions['valid'] = false;
        console.log("errorRegister: " + JSON.stringify(error));
    }
    
     this.commitCountryEdit = function() {
        console.log("Commit new Country :" + that.bundle.country + " OPT IN Value :"+ that.selectedSubscribe());
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.setUserAccountDetails, [user.email, that.bundle.country, that.selectedSubscribe()]);
        tDeffer.done(that.successUserAccountDetails);
    }
    
    this.successUserAccountDetails = function(result) {
        console.log("result" + JSON.stringify(result));
        user.extra_param.countryCode = that.bundle.country;
        user.extra_param.optIn = that.selectedSubscribe();
        parent.communicationWrapper.setDataValue("user", user);
        var callGuid = guid();
         parent.notification.showLoadingArea(true, notificationType.HINT, $.i18n.t("legalInformation.page.edit_done_message", {email:that.bundle.email}), callGuid);
        window.setTimeout(function(){
            parent.notification.showLoadingArea(false, notificationType.HINT, "", callGuid);
            that.navigate();
        }, 3000);
      
    }
    
    this.navigate = function(){
        if(that.isLegalinfoEdit()) { 
            if (typeof that.robot().robotId == 'undefined') {
                that.conditions['robotSelection'] = true;
            } else {
                that.conditions['userSettings'] = true;
            }
        } else {
            that.conditions['start'] = true;
        } 
        parent.flowNavigator.next(robotScreenCaller.REGISTER);
    }

})
console.log('loaded file: legalInformation_ViewModel.js');
