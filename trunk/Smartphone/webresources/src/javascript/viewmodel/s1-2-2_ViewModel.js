resourceHandler.registerFunction('s1-2-2_ViewModel.js', 's1-2-2_ViewModel', function(parent) {
    console.log('instance created for: s1-2-2_ViewModel');
    var that = this;
    this.id = 's1-2-2_ViewModel';
    this.title = ko.observable();
    this.conditions = {};
    this.backConditions = {};
    this.robots = ko.observableArray([]);
    this.isBackVisible = ko.observable(false);
    
    this.back = function(){
        parent.flowNavigator.previous();
    };

    this.logout = function() {
        parent.communicationWrapper.exec(UserPluginManager.logout, [], that.successLogout, that.errorLogout);
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

    this.reload = function() {
        that.conditions = {};
        that.backConditions = {};
    };

    /**
     * Called when a robot has been selected from the list.
     */
    this.robotSelected = function(data, event) {
        that.conditions['robotSelected'] = true;
        
        // store the selected robot within the communication model 
        // data needs to converted back to plain JavaScript object using toJS
        parent.communicationWrapper.storeDataValue("activeRobot", ko.toJS(data));
        parent.flowNavigator.next();
    };
    
    this.getHintText = function(){        
        if (that.robots().length > 0){
            return "s1-2-2.page.select_robot";
        }
        
        return "s1-2-2.page.no_robot";
    }
    
    this.updateScreenTitle = function() {
        // Update the title of the screen depending on the context
        if (that.bundle){
            
            var translationKey;
            
            switch(that.bundle){
                
                case robotScreenCaller.REGISTER:
                    translationKey = "s1-2-2.page.register";
                break;
                case robotScreenCaller.LOGIN:
                    translationKey = "s1-2-2.page.login";
                break;
                default:            
                break;
            }
            
            var userName = parent.communicationWrapper.dataValues["user"].username;
            
            var generatedTitle = "";
            
            if (translationKey){
                generatedTitle += $.i18n.t(translationKey, {postProcess:'sprintf', sprintf: [userName]}) + "<br><br>";                
            }
            
            generatedTitle += $.i18n.t(that.getHintText());
            
            that.title(generatedTitle);        
        }        
    };
    
    this.updateBackBtn = function(){
        if (that.bundle){
            that.isBackVisible(that.bundle == robotScreenCaller.CHANGE);
        }
    };

    this.init = function() {
        
        // Update the back button depending on the screen that opened this view
        that.updateBackBtn();

        // When we are in the register flow, simply show the screen without querying the robots
        if (that.bundle && that.bundle == robotScreenCaller.REGISTER){
            that.updateScreenTitle();
        }
        else{            
            // Get the user's email for the robot query from the stored user data values
            var userEmail = parent.communicationWrapper.dataValues["user"].email;
        
            that.title("");
            
            // Get the associated robots
            parent.communicationWrapper.exec(UserPluginManager.getAssociatedRobots, [userEmail], that.successGetAssociatedRobots, that.errorGetAssociatedRobots, "robots");
        }
    };

    this.successGetAssociatedRobots = function(result) {
        //ko.mapping.fromJS(result,null, that.robots);
        that.robots(ko.mapping.fromJS(result)());
        
        that.updateScreenTitle();
    };

    this.errorGetAssociatedRobots = function(error) {
        console.log("Error(Get AssociatedRobots)" + error.errorMessage);
        that.updateScreenTitle();
    };
})
console.log('loaded file: s1-2-2_ViewModel.js');
