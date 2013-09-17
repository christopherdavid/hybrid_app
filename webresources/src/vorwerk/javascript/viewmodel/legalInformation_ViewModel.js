resourceHandler.registerFunction('legalInformation_ViewModel.js', function(parent) {
    console.log('instance created for: legalInformation_ViewModel');
    var that = this, myScroll;
    this.conditions = {};
    
    this.selectedSubscribe = ko.observable();
    
    this.isValid = ko.computed(function() {
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
    }
    
    this.deinit = function() {
        $(window).off(".legal");
        myScroll.destroy();
    }
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.next = function() {
        //TODO remove username from create user methode
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.createUser2, [that.bundle.email, that.bundle.pw, 'default', ''], {});
        tDeffer.done(that.successRegister);
        tDeffer.fail(that.errorRegister);
    };

    this.successRegister = function(result) {
        that.conditions['valid'] = true;
        parent.communicationWrapper.setDataValue("user", result);
        //TODO: temporary store selected country in local storage till it could be stored on server 
        parent.communicationWrapper.saveToLocalStorage(that.bundle.email + "_country", that.bundle.country);
        var translatedTitle = $.i18n.t("legalInformation.page.registration_done_title");
        var translatedText = $.i18n.t("legalInformation.page.registration_done_message", {email:that.bundle.email});
        parent.notification.showDialog(dialogType.INFO, translatedTitle, translatedText, [{"label":"Ok", "callback":function(e){ parent.notification.closeDialog(); parent.flowNavigator.next(robotScreenCaller.REGISTER);}}]);
    }

    this.errorRegister = function(error) {
        that.conditions['valid'] = false;
        console.log("errorRegister: " + JSON.stringify(error));
    }

})
console.log('loaded file: legalInformation_ViewModel.js');
