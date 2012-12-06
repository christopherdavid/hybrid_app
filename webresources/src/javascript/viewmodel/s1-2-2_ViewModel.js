resourceHandler.registerFunction('s1-2-2_ViewModel.js', 's1-2-2_ViewModel', function(parent) {
    console.log('instance created for: s1-2-2_ViewModel');
    var that = this;
    this.id = 's1-2-2_ViewModel';
    this.conditions = {};
    var translationVars = {
        "s1-2-2.page.title": [parent.getUserName()]
    }
    this.getTranslationVars = function(labelKey) {
        console.log("called getTranslationVars for key: " + labelKey)
        return translationVars[labelKey] || [];
    }
    
    this.logout = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    this.addRobot = function() {
        that.conditions['addRobot'] = true;
        parent.flowNavigator.next();
    };
})
console.log('loaded file: s1-2-2_ViewModel.js');
