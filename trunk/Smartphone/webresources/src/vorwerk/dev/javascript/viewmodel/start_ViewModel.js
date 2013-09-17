resourceHandler.registerFunction('start_ViewModel.js', function(parent) {
    console.log('instance created for: start_ViewModel');
    var that = this;
    this.conditions = {};
    
    this.init = function() {
        parent.config.viewPath = "";
    }
    this.reload = function() {
        // remove conditions
        that.conditions = {};
    };
    
    
    this.headerView = function() {
        that.conditions['header'] = true;
        parent.flowNavigator.next();
    };
    
    this.buttonView = function() {
        that.conditions['button'] = true;
        parent.flowNavigator.next();
    };
    
    this.daypickerView = function() {
        that.conditions['daypicker'] = true;
        parent.flowNavigator.next();
    }
    
    this.inputView = function() {
        that.conditions['inputView'] = true;
        parent.flowNavigator.next();
    };
    
    this.formView = function() {
        that.conditions['form'] = true;
        parent.flowNavigator.next();
    };
    
    this.listView = function() {
        that.conditions['listview'] = true;
        parent.flowNavigator.next();
    }
    
    this.dialogView = function() {
        that.conditions['dialogview'] = true;
        parent.flowNavigator.next();
    }
    
    this.countryView = function() {
        that.conditions['countryview'] = true;
        parent.flowNavigator.next();
    }
    
    this.legalView = function() {
        that.conditions['legalview'] = true;
        parent.flowNavigator.next();
    }
    
    this.animationView = function() {
        that.conditions['animationview'] = true;
        parent.flowNavigator.next();
    }
    
    
})
console.log('loaded file: start_ViewModel.js');
