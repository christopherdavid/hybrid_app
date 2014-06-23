resourceHandler.registerFunction('marketingInformation_ViewModel.js', function(parent) {
    console.log('instance created for: marketingInformation_ViewModel');
    var that = this, myScroll,
        user = parent.communicationWrapper.getDataValue("user");
        
    this.conditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.selectedSubscribe = ko.observable(true);
    this.leftIcon = ko.observable(dataImage.BACK);
    this.rightIcon = ko.observable(dataImage.NEXT);
    this.isRobotVisible = null;
    
    this.initComputed = function() {
        that.isRobotVisible = ko.computed(function(){
            return (typeof(that.robot().robotName) != 'undefined');
        },this);
    };
    
    this.init = function() {
        myScroll = new iScroll('marketWrapper',{
            hScrollbar : false,
            vScrollbar : false,
            bounce : true,
            vScroll : true,
            hScroll : true,
            momentum : true
        });
        
        $(document).one("pageshow.market", function(e) {
            var tempLine = $("#topLine");
            $("#marketWrapper").css({
                "top":(tempLine.offset().top + tempLine.height())
            });
            myScroll.refresh();
        });
        
        $(window).on("resize.market", function(e) {
            var tempLine = $("#topLine");
            $("#legalWrapper").css({
                "top":(tempLine.offset().top + tempLine.height())
            });
            
            myScroll.refresh();
        });
        
        if(isDefined(that.bundle, "state") && that.bundle.state == pageState.EDIT) {
            that.leftIcon(dataImage.CANCEL);
            that.rightIcon(dataImage.OK);
        }
        if(isDefined(that.bundle, "state") && that.bundle.state == pageState.CHANGE) {
            that.rightIcon(dataImage.OK);
        }
        if(isDefined(user,"extra_param.optIn")) {
            that.selectedSubscribe(user.extra_param.optIn);
        }
    };
    
    // viewmodel deinit, destroy objects and remove event listener
    this.deinit = function() {
        $(window).off(".market");
        that.isRobotVisible.dispose();
        myScroll.destroy();
    };
    
    this.reload = function() {
        that.initComputed();
    };
    
    this.back = function() {
        parent.flowNavigator.previous();
    };
    
    /*
	 * Called when user submits changes.
	 */
    this.submitChange = function() {
        event.stopPropagation();
        if(isDefined(that.bundle, "state") && (that.bundle.state == pageState.EDIT || that.bundle.state == pageState.CHANGE)) {
            // edit country information
            var tDeffer = parent.communicationWrapper.exec(UserPluginManager.setUserAccountDetails, [
                user.email, 
                that.bundle.country, 
                that.selectedSubscribe()]
            );
            tDeffer.done(that.successCountryUpdate);
        } else {
            // move to next screen
            that.conditions["valid"] = true;
            that.bundle.optIn = that.selectedSubscribe();
            parent.flowNavigator.next(that.bundle);
        }
        
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
            that.conditions['userSettings'] = true;
            parent.flowNavigator.next();
        }, 3000);
    };
    
    that.initComputed();
});
console.log('loaded file: marketingInformation_ViewModel.js');
