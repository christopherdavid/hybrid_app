resourceHandler.registerFunction('generalSettings_ViewModel.js', function(parent) {
    console.log('instance created for: generalSettings_ViewModel');
    var that = this;
    var initDone = false;

    this.conditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.user = ko.observable();
    
    this.selectedPushType = ko.observable();
    this.robotNeedsCleaning = ko.observable();
    this.cleaningDone = ko.observable();
    this.robotStuck = ko.observable();

    this.init = function() {
        that.user(ko.mapping.fromJS(parent.communicationWrapper.getDataValue("user")), null, that.user);
        
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.getNotificationSettings, [that.user().email()]);
        tDeffer.done(that.successGetNotifications);
        tDeffer.fail(that.erroGSetNotifications);
    };
    
    this.successGetNotifications = function(result) {
        ko.utils.arrayForEach(result.notifications, function(item) {
            
            console.log("setting for key:" + item.key, " value:" + item.value); 
            
            if(item.key == NOTIFICATION_DIRT_BIN_FULL){
                that.robotNeedsCleaning(item.value == true ? 'on' : 'off');
            } else if(item.key == NOTIFICATION_CLEANING_DONE){
                that.cleaningDone(item.value == true ? 'on' : 'off');
            } else if(item.key == NOTIFICATION_ROBOT_STUCK){
                that.robotStuck(item.value == true ? 'on' : 'off');
            } 
            
        });
        
        that.selectedPushType(result.global ? 'on' : 'off');
        
        initDone = true;
    };
    
    this.errorGetNotifications = function(error) {
        console.log("error GET" + JSON.stringify(error));
    };
    
    this.changeRobot = function() {
        // Switch to robot selection dialog
        that.conditions['changeRobot'] = true;
        parent.flowNavigator.next(robotScreenCaller.CHANGE);
    };
    
    this.selectedPushType.subscribe(function(newValue) {      
        var onoff = newValue == 'on' ? true : false;
        console.log("robotNeedsCleaning=" + onoff);
        that.turnNotificationOnoff(NOTIFICATIONS_GLOBAL_OPTION, onoff);
    });
    
    this.robotNeedsCleaning.subscribe(function(newValue) {
        var onoff = newValue == 'on' ? true : false;
        console.log("robotNeedsCleaning=" + onoff);
        that.turnNotificationOnoff(NOTIFICATION_DIRT_BIN_FULL, onoff);
    });
    
    this.cleaningDone.subscribe(function(newValue) {
        var onoff = newValue == 'on' ? true : false;
        console.log("cleaningIsDone=" + onoff);
        that.turnNotificationOnoff(NOTIFICATION_CLEANING_DONE, onoff);
    });

    this.robotStuck.subscribe(function(newValue) {
        var onoff = newValue == 'on' ? true : false;
        console.log("robotStuck=" + onoff);
        that.turnNotificationOnoff(NOTIFICATION_ROBOT_STUCK, onoff);
    },this);

    this.turnNotificationOnoff = function(id, onoff) {
    
        if(initDone){
            console.log("turning on/off notification: " + id + ":" + onoff);
            var tDeffer = parent.communicationWrapper.exec(UserPluginManager.turnNotificationOnoff, [that.user().email(), id, onoff]);
            tDeffer.done(that.successSetNotifications);
            tDeffer.fail(that.errorSetNotifications);
        } else {
            console.log("waiting for init done.");
        }
                
    }
    
    this.successSetNotifications = function(result) {
        console.log("result" + JSON.stringify(result));
    }
    
    this.errorSetNotifications = function(error) {
        console.log("error" + JSON.stringify(error));
    }
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
    this.reload = function() {
        this.conditions = {};
    }
    
})
console.log('loaded file: generalSettings_ViewModel.js');
