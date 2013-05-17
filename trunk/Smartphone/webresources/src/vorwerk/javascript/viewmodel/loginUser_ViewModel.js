resourceHandler.registerFunction('loginUser_ViewModel.js', function(parent) {
    console.log('instance created for: loginUser_ViewModel');
    var that = this;
    this.conditions = {};
    this.email = ko.observable();
    this.password = ko.observable();
    //this.email = ko.observable('demo1@demo.com');
    //this.password = ko.observable('demo123');
    // this.email = ko.observable('paul@uid.com');
    // this.password = ko.observable('uiduid');
    this.showPassword = ko.observable(false);
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };

    this.isFilledOut = ko.computed(function() {
        return (this.email() != '' && this.password() != '');
    }, this);

    this.passwordLost = function() {
        // TODO: What will be triggered here
        console.log("password lost");
    }

    this.next = function() {
        // TODO: add validation check for entries
        //parent.communicationWrapper.exec(UserPluginManager.login, [that.email(), that.password()], that.sucessLogin, that.errorLogin, {}, "user");
        var tDeffer = parent.communicationWrapper.exec(UserPluginManager.login, [that.email(), that.password()], {}, "user");
        tDeffer.done(that.sucessLogin);
        tDeffer.fail(that.errorLogin);
    };

    this.sucessLogin = function(result, notifyOptions) {
        that.conditions['valid'] = true;
        console.log("result: " + result);
        parent.notification.registerForRobotMessages();
        parent.communicationWrapper.saveToLocalStorage('username', that.email());
        parent.flowNavigator.next(robotScreenCaller.LOGIN);
    };

    this.errorLogin = function(error) {
        console.log("Error: " + error.errorMessage);
    };

    this.reload = function() {
        this.conditions = {};
    }
})
console.log('loaded file: loginUser_ViewModel.js');
