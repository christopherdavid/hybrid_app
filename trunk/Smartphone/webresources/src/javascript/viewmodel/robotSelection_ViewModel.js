resourceHandler.registerFunction('robotSelection_ViewModel.js', function(parent) {
    console.log('instance created for: robotSelection_ViewModel');
    var that = this;
    this.title = ko.observable();
    this.conditions = {};
    this.backConditions = {};
    this.robots = ko.observableArray([]);
    this.isBackVisible = ko.observable(false);
    this.isDeleteVisible = ko.observable(false);
    this.selectedRobot = ko.observable();

    this.back = function() {
        parent.flowNavigator.previous();
    };

    this.logout = function() {
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.logout, []);
        tDeffer.done(that.successLogout);
        tDeffer.fail(that.errorLogout);
    };

    this.successLogout = function(result) {
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

    this.removeRobot = function(robot) {
        var userEmail = parent.communicationWrapper.dataValues["user"].email;
        //var tDeffer = parent.communicationWrapper.exec(UserPluginManager.disassociateRobot, [userEmail, robot.robotId()]);
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.disassociateAllRobots, [userEmail, robot.robotId()]);
        tDeffer.done(that.successRemoveRobot);
        tDeffer.fail(that.errorRemoveRobot);
    };

    this.successRemoveRobot = function(result) {
        console.log("Result" + result);

        // ToDo: notify the user

        // Query the new robot list on success
        that.getRobotList();
    };

    this.errorRemoveRobot = function(error) {
        console.log("Error: " + error.errorMessage);
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

        if (that.bundle == robotScreenCaller.MANAGE) {
            // Store the selected robot
            if (that.selectedRobot() == data) {
                that.selectedRobot(null);
            } else {
                that.selectedRobot(data);
            }
        } else {
            // store the selected robot within the communication model
            // data needs to converted back to plain JavaScript object using toJS
            parent.communicationWrapper.storeDataValue("activeRobot", ko.toJS(data));
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
        // Update the title of the screen depending on the context
        if (that.bundle) {

            var translationKey;

            switch(that.bundle) {

                case robotScreenCaller.REGISTER:
                    translationKey = "robotSelection.page.register";
                    break;
                case robotScreenCaller.LOGIN:
                    translationKey = "robotSelection.page.login";
                    break;
                default:
                    break;
            }

            var generatedTitle = "";

            if (translationKey) {
                generatedTitle += $.i18n.t(translationKey) + "<br><br>";
                //old version string with arguments for the now removed username
                //generatedTitle += $.i18n.t(translationKey, {postProcess:'sprintf', sprintf: [userName]}) + "<br><br>";
            }

            generatedTitle += $.i18n.t(that.getHintText());

            that.title(generatedTitle);
        }
    };

    this.updateButtons = function(isRefresh) {
        if (that.bundle) {
            that.isDeleteVisible(that.bundle == robotScreenCaller.MANAGE && that.robots().length > 0);

            if (isRefresh) {
                that.isBackVisible((that.bundle == robotScreenCaller.CHANGE || that.bundle == robotScreenCaller.MANAGE) && that.robots().length > 0);
            } else {
                that.isBackVisible((that.bundle == robotScreenCaller.CHANGE || that.bundle == robotScreenCaller.MANAGE));
            }
        }
    };

    this.getRobotList = function() {
        // Get the user's email for the robot query from the stored user data values
        var userEmail = parent.communicationWrapper.dataValues["user"].email;

        that.title("");

        var fetchingRobots = $.i18n.t('communication.fetch_robots');
        // Get the associated robots
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.getAssociatedRobots, [userEmail], {
            type : notificationType.OPERATION,
            message : fetchingRobots,
            callback : null
        }, "robots");
        tDeffer.done(that.successGetAssociatedRobots);
        tDeffer.fail(that.errorGetAssociatedRobots);

    };

    /**
     *  Called when the delete button has been pressed. A robot needs to be selected before
     */
    this.deleteRobot = function() {

        if (that.selectedRobot()) {
            // Remove the selected robot
            that.removeRobot(that.selectedRobot());
        }
    }

    this.init = function() {

        // Update the back button depending on the screen that opened this view
        that.updateButtons();

        // When we are in the register flow, simply show the screen without querying the robots
        if (that.bundle && that.bundle == robotScreenCaller.REGISTER) {
            that.updateScreenTitle();
        } else {
            that.getRobotList();
        }
    };

    this.successGetAssociatedRobots = function(result) {
        //ko.mapping.fromJS(result,null, that.robots);

        that.robots(ko.mapping.fromJS(result)());

        that.updateScreenTitle();
        that.updateButtons(true);
    };

    this.errorGetAssociatedRobots = function(error) {
        console.log("Error(Get AssociatedRobots)" + error.errorMessage);
        that.updateScreenTitle();
    };
})
console.log('loaded file: robotSelection_ViewModel.js');
