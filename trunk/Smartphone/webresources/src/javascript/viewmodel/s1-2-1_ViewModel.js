resourceHandler.registerFunction('s1-2-1_ViewModel.js', 's1-2-1_ViewModel', function(parent) {
    console.log('instance created for: s1-2-1_ViewModel');
    var that = this;
    this.id = 's1-2-1_ViewModel';
    this.conditions = {};
    this.name = ko.observable('');
    this.email = ko.observable('');
    this.password = ko.observable('');
    this.password_verify = ko.observable('');

    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    this.isFilledOut = ko.computed(function() {
        return (this.name() != '' && this.email() != '' && this.password() != '' && this.password_verify() != '');
    }, this);

    this.next = function() {
        // TODO: add validation check for formular
        parent.setUserName(this.name());
        that.conditions['valid'] = true;
        parent.flowNavigator.next();
    };
})
console.log('loaded file: s1-2-1_ViewModel.js');
