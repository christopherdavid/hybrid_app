resourceHandler.registerFunction('s6-1-1_ViewModel.js', function(parent) {
    console.log('instance created for: s6-1-1_ViewModel');
    var that = this;
    this.conditions = {};
    this.backConditions = {};

    this.isBackVisible = ko.observable(true);

    this.labelTitle = ko.observable($.i18n.t("s6-1-1.navi.title"));

    this.remoteControl

    /* <enviroment functions> */
    this.init = function() {
        that.remoteControl = new RemoteControl($('#remoteControlTarget'));
    }

    this.reload = function() {
        // remove conditions
        that.conditions = {};
    }

    this.deinit = function() {

    }
    /* </enviroment functions> */

    /* <actionbar functions> */
    this.back = function() {
        that.backConditions['home'] = true;
        parent.flowNavigator.previous();
    };
    /* </actionbar functions> */
})
console.log('loaded file: s4-1-1_ViewModel.js'); 