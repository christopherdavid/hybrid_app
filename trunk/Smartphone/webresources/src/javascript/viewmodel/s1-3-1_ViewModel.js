resourceHandler.registerFunction('s1-3-1_ViewModel.js', 's1-3-1_ViewModel', function(parent) {
    console.log('instance created for: s1-3-1_ViewModel');
    var that = this;
    this.id = 's1-3-1_ViewModel';
    this.conditions = {};
    this.email = ko.observable('');
    this.password = ko.observable('');
    

    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    this.isFilledOut = ko.computed(function() {
        return (this.email() != '' && this.password() != '');
    }, this);

    this.next = function() {
        // TODO: add validation check for formular
        //PluginManager.loginUser(that.email(), that.password() , that.sucessLogin, that.errorLogin);
        // just to step trough workflow
        that.conditions['valid'] = true;
        parent.flowNavigator.next();
    };
    this.sucessLogin = function(result) {
        that.conditions['valid'] = true;
        console.log("result: " + result)
        parent.flowNavigator.next();
    };
    this.errorLogin = function(error) {
        alert("error: " + error);
    };
})
console.log('loaded file: s1-3-1_ViewModel.js');
