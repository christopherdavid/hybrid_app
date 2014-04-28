resourceHandler.registerFunction('robotSelection_ViewModel.js', function(parent) {
    console.log('instance created for: robotSelection_ViewModel');
    var that = this;
    this.title = ko.observable();
    this.message = ko.observable();
    this.conditions = {};
    this.backConditions = {};
    this.robots = parent.communicationWrapper.getDataValue("robotList");
    this.isBackVisible = ko.observable(false);
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.isRobotVisible = ko.computed(function(){
        return (typeof(that.robot().robotName) != 'undefined');
    },this);
    
    this.isLogoutVisible = ko.observable(false);
    
    this.selectedRobot = ko.observable();
    
    var userName = parent.communicationWrapper.getFromLocalStorage('username');
    
    this.init = function() {
    	
    	that.finishRegistration();
    		//return false;
    		
        // show robot has been deleted message
        if (that.bundle && that.bundle == robotScreenCaller.DELETE) {
            var sDelete = $.i18n.t('communication.delete_robot_done', {robotName:that.robot().robotName()});
            parent.notification.showLoadingArea(true, notificationType.HINT, sDelete);
            // un-ubscripbe from robot 
            robotUiStateHandler.disposeFromRobot();
            // clear history
            parent.history.clear();
            // remove robot from local data
            that.robots.remove(that.robot());
            // set up an empty robot
            //that.communicationWrapper.mapDataValue("selectedRobot", ko.observable({}));
            that.robot({});
        }
        
        // Update the back button depending on the screen that opened this view
        that.updateButtons();
        
        if(that.bundle && (that.bundle == robotScreenCaller.REGISTER || that.bundle == robotScreenCaller.LOGIN || that.bundle == robotScreenCaller.DELETE)) {
            that.isLogoutVisible(true);
        }
        if (that.robots().length == 0){
            that.getRobotList();
        } else {
            that.updateScreenTitle();
            that.updateButtons(true);
        }
    };
    
    this.deinit = function() {
        if (that.bundle && that.bundle == robotScreenCaller.DELETE) {
            // clear bundle
            that.bundle = null;
        }
        
        that.isRobotVisible.dispose();
    };

    this.back = function() {
        parent.flowNavigator.previous();
    };

    this.finishRegistration = function(){
        var user = parent.communicationWrapper.getDataValue("user");
        if(user != null) {
            if((user.extra_param.countryCode == null)||(user.extra_param.countryCode == "null")) {
                var translatedTitle = $.i18n.t("robotSelection.page.edit_title");
                var translatedText = $.i18n.t("robotSelection.page.edit_message");
                parent.notification.showDialog(dialogType.INFO, translatedTitle, translatedText, [{"label":$.i18n.t("common.next"), "callback":function(e){ parent.notification.closeDialog();  that.conditions['selectCountry'] = true; parent.flowNavigator.next({"userlogin":true});}}]);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
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

    this.addRobot = function() {
        that.conditions['addRobot'] = true;
        parent.flowNavigator.next(that.bundle);
    };

    this.reload = function() {
        that.conditions = {};
        that.backConditions = {};
        that.selectedRobot = ko.observable();
    };

    /**
     * Called when a robot has been selected from the list.
     */
    this.robotSelected = function(data, event) {
        that.conditions['robotSelected'] = true;
        that.robot(data);
        // subscribe to robot
        robotUiStateHandler.subscribeToRobot(parent.communicationWrapper.getDataValue("selectedRobot"));
        if(!that.robot().robotOnline()) {
            robotUiStateHandler.setUiState(ROBOT_UI_STATE_ROBOT_OFFLINE);
        } else {
            parent.communicationWrapper.updateRobotStateWithCode(that.robot(), data.robotNewVirtualState());
        }
        
        if (that.bundle == robotScreenCaller.CHANGE) {
            parent.flowNavigator.previous();
        } else {
            parent.flowNavigator.next();
        }
    };

    this.updateMessage = function() {
        var translationKey = "robotSelection.page.no_robot";
        if (that.robots().length > 0) {
            translationKey = "robotSelection.page.select_robot";
        }
        that.message($.i18n.t(translationKey));
    };

    this.updateScreenTitle = function() {
        var translationKey;
        
        // Update the title of the screen depending on the context
        if (that.bundle) {
            switch(that.bundle) {
                case robotScreenCaller.REGISTER:
                    translationKey = "robotSelection.page.register";
                    break;
                case robotScreenCaller.LOGIN:
                    translationKey = "robotSelection.page.login";
                    break;
                default:
                    translationKey = "robotSelection.page.logged_in";
            }
        } else {
            translationKey = "robotSelection.page.logged_in";
        }
        var generatedTitle = $.i18n.t(translationKey, {email:userName});
        that.title(generatedTitle);
        that.updateMessage();
    };

    this.updateButtons = function(isRefresh) {
        if (that.bundle) {
            if (isRefresh) {
                that.isBackVisible((that.bundle == robotScreenCaller.CHANGE || that.bundle == robotScreenCaller.MANAGE) && that.robots().length > 0);
            } else {
                that.isBackVisible((that.bundle == robotScreenCaller.CHANGE || that.bundle == robotScreenCaller.MANAGE));
            }
        }
    };

    this.getRobotList = function() {
        // Get the user's email for the robot query from the stored user data values
        var userEmail = parent.communicationWrapper.getDataValue("user").email;
        parent.communicationWrapper.clearStaticCallbacks();

        that.title("");

        var fetchingRobots = $.i18n.t('communication.fetch_robots');
        // Get the associated robots
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.getAssociatedRobots, [userEmail], {
            type : notificationType.OPERATION,
            message : fetchingRobots
        });
        tDeffer.done(that.successGetAssociatedRobots);
        tDeffer.fail(that.errorGetAssociatedRobots);

    };

    this.successGetAssociatedRobots = function(result) {
        var robotList = [];
        // loop over all robots and add the state property
        $.each(result, function(index, item){
           // create a robot object based on the robot structure
           var tempRobot = getRobotStruct();
           // fill properties
           tempRobot.robotId = item.robotId;
           tempRobot.robotName = item.robotName;
           tempRobot.displayName = item.robotName;
           robotList.push(tempRobot);
           
           // request state from server due some delay we need to use a 
           // separate API call because the user could navigate in the meantime 
           // to another screen
            parent.communicationWrapper.getRobotState(item.robotId);
            parent.communicationWrapper.getRobotOnline(item.robotId);
        });
        that.robots(ko.mapping.fromJS(robotList)());

        that.updateScreenTitle();
        that.updateButtons(true);
        
        parent.communicationWrapper.saveToLocalStorage('robotList', that.robots());
    };

    this.errorGetAssociatedRobots = function(error) {
        console.log("Error(Get AssociatedRobots)" + error.errorMessage);
        that.updateScreenTitle();
    };
});
console.log('loaded file: robotSelection_ViewModel.js');
