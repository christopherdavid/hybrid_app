resourceHandler.registerFunction('legalInformation_ViewModel.js', function(parent) {
    console.log('instance created for: legalInformation_ViewModel');
    var that = this, myScroll,
        user = parent.communicationWrapper.getDataValue("user");
    
    this.conditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.isAgreed = ko.observable(false);
    this.leftIcon = ko.observable(dataImage.BACK);
    this.rightIcon = ko.observable(dataImage.NEXT);
    this.nextEnabled = null;
    this.isRobotVisible = null;
    
    this.initComputed = function() {
        that.nextEnabled = ko.computed(function() {
            if(isDefined(that.bundle, "state") && that.bundle.state == pageState.EDIT) {
                return true;
            } else {
                return that.isAgreed();
            }
        }, this);
        
        that.isRobotVisible = ko.computed(function(){
            return (typeof(that.robot().robotName) != 'undefined');
        },this);
    };
    
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
            
            $("#privacy").click(function(){
               that.showagreement("privacy");
            });
                
            $("#terms").click(function(){
                that.showagreement("terms");
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
        
        if(isDefined(that.bundle, "state") && that.bundle.state == pageState.EDIT) {
            // The agreement will be selected if the user come from the user settings screen
            that.isAgreed(true);
            that.leftIcon(dataImage.CANCEL);
            that.rightIcon(dataImage.OK);
        }
        
        if(isDefined(that.bundle, "state") && that.bundle.state == pageState.CHANGE) {
            // The agreement will be selected if the user come from the user settings screen
            that.isAgreed(true);
            //that.rightIcon(dataImage.OK);
        }
    };
        
    // viewmodel deinit, destroy objects and remove event listener
    this.deinit = function() {
        $(window).off(".legal");
        that.nextEnabled.dispose();
        that.isRobotVisible.dispose();
        myScroll.destroy();
    };
    
    this.reload = function() {
        that.initComputed();
    };
    
    this.back = function() {
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
        if (that.isAgreed()) {
            that.conditions["valid"] = true;
            parent.flowNavigator.next(that.bundle);
        }
    };
    
    that.initComputed();
});
console.log('loaded file: legalInformation_ViewModel.js');
