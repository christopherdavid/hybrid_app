resourceHandler.registerFunction('robotSelection_ViewModel.js', function(parent) {
    console.log('instance created for: robotSelection_ViewModel');
    var that = this;
    this.title = ko.observable();
    this.conditions = {};
    this.backConditions = {};
    this.robots = parent.communicationWrapper.getDataValue("robotList");
    this.isBackVisible = ko.observable(false);
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.isRobotVisible = ko.computed(function(){
        return (typeof(that.robot().robotName) != 'undefined');
    },this);
    
    this.selectedRobot = ko.observable();
    
    var userName = parent.communicationWrapper.getFromLocalStorage('username');
    
    this.init = function() {
        // show robot has been deleted message
        if (that.bundle && that.bundle == robotScreenCaller.DELETE) {
            var sDelete = $.i18n.t('communication.delete_robot_done', {robotName:that.robot().robotId()});
            parent.notification.showLoadingArea(true, notificationType.HINT, sDelete);
            // clear history
            parent.history.clear();
            // remove robot from local data
            that.robots.remove(that.robot());
            that.robot({});
        }
        
        // Update the back button depending on the screen that opened this view
        that.updateButtons();
        
        // When we are in the register flow, simply show the screen without querying the robots
        if (that.bundle && that.bundle == robotScreenCaller.REGISTER) {
            that.updateScreenTitle();
        } else if (that.robots().length == 0){
            that.getRobotList();
        } else {
            that.updateScreenTitle();
            that.updateButtons(true);
        }
    };

    this.back = function() {
        parent.flowNavigator.previous();
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
    }

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
        //parent.communicationWrapper.setDataValue("sel", ko.toJS(data));
        
        if (that.bundle == robotScreenCaller.CHANGE) {
            parent.flowNavigator.previous();
        } else {
            // store the selected robot within the communication model
            // data needs to converted back to plain JavaScript object using toJS
            parent.flowNavigator.next();
        }
    };

    this.getHintText = function() {
        if (that.robots().length > 0) {
            return "robotSelection.page.select_robot";
        }
        return "robotSelection.page.no_robot";
    }

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
        var generatedTitle = "";

        generatedTitle += $.i18n.t(translationKey, {postProcess:'sprintf', sprintf: [userName]}) + "<br>";
        generatedTitle += $.i18n.t(that.getHintText());
        
        that.title(generatedTitle);
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
        var unknownState = $.i18n.t("robotStateCodes." + ROBOT_STATE_UNKNOWN);
        // loop over all robots and add the state property
        $.each(result, function(index, item){
           // initially set state to unknown 
           item.stateCode = ROBOT_STATE_UNKNOWN;
           item.stateString = unknownState;
           // request state from server due some delay we need to use a 
           // separate API call because the user could navigate in the meantime 
           // to another screen
            parent.communicationWrapper.getRobotState(item.robotId);
        });
        that.robots(ko.mapping.fromJS(result)());

        that.updateScreenTitle();
        that.updateButtons(true);
        
        parent.communicationWrapper.saveToLocalStorage('robotList', that.robots());
    };

    this.errorGetAssociatedRobots = function(error) {
        console.log("Error(Get AssociatedRobots)" + error.errorMessage);
        that.updateScreenTitle();
    };
})
console.log('loaded file: robotSelection_ViewModel.js');
