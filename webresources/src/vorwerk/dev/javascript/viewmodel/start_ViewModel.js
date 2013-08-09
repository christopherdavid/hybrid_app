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
    
    
    this.header = function() {
        that.conditions['header'] = true;
        parent.flowNavigator.next();
    };

    this.form = function() {
        that.conditions['form'] = true;
        parent.flowNavigator.next();
    };
    
    this.list = function() {
        that.conditions['list'] = true;
        parent.flowNavigator.next();
    }
    
    this.dialog = function() {
        that.conditions['dialog'] = true;
        parent.flowNavigator.next();
    }
    
    
})
console.log('loaded file: start_ViewModel.js');
