resourceHandler.registerFunction('userSettings_ViewModel.js', function(parent) {
    console.log('instance created for: userSettings_ViewModel');
    var that = this;
    this.conditions = {};
    this.robot = ko.observable();
    
    this.showPassword = ko.observable(false);

    this.newPassword = ko.observable("");
    this.newPasswordRepeat = ko.observable("");
    this.oldPassword = ko.observable("");
    
    this.user = ko.observable();

    
    var PASSWORD_LENGTH = 6;

    
    this.passwordValid = ko.computed(function() {
        return !isPasswordValid();
    }, this);
    
    okEnabled = ko.computed(function() {
        return isOkButtonEnabled();
    }, this);
    
    function isOkButtonEnabled() {
        return that.newPassword().length != '' &&  that.oldPassword().length != '' && isPasswordValid();
    }
    
    function isPasswordValid() {
        if (that.newPasswordRepeat() == '')
            return true;
        return (that.newPasswordRepeat() == that.newPassword() && isPasswordComplex());
    }
    
    function isPasswordComplex() {
        // if (that.newPassword() == '')
            // return true;
        // return (that.newPassword().length >= PASSWORD_LENGTH);        return true;
    }
    
    this.init = function() {
        that.robot(ko.mapping.fromJS(parent.communicationWrapper.dataValues["activeRobot"]), null, that.robot);
        that.user(ko.mapping.fromJS(parent.communicationWrapper.dataValues["user"]), null, that.user);
    };
    
    this.changeRobot = function() {
        // Switch to robot selection dialog
        that.conditions['changeRobot'] = true;
        parent.flowNavigator.next(robotScreenCaller.CHANGE);
    };
    
    this.changePassword = function() {
        $("#changePasswordPopup").popup("open");
    };
    
    this.cancelEdit = function() {
        $("#changePasswordPopup").popup("close");
    }
    
    this.commitEdit = function() {
        console.log("change password of user with email: " + that.user().email() + " to " + that.newPassword());
        
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.changePassword, [that.user().email(), that.oldPassword(), that.newPassword()]);
        tDeffer.done(that.successChangePassword);
        tDeffer.fail(that.errorChangePassword);

        $("#changePasswordPopup").popup("close");
    }
    
    this.successChangePassword = function(result) {
        parent.communicationWrapper.dataValues["activeRobot"].robotName = that.newRobotName();
        console.log("result" + JSON.stringify(result));
    }
    this.errorChangePassword = function(error) {
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
console.log('loaded file: userSettings_ViewModel.js');
