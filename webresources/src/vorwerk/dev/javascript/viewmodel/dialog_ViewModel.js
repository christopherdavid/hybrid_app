resourceHandler.registerFunction('dialog_ViewModel.js', function(parent) {
    console.log('instance created for: dialog_ViewModel');
    var that = this;
    this.conditions = {};
    
    this.init = function() {}
    this.reload = function() {
        // remove conditions
        that.conditions = {};
    };
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
    this.showInfo = function() {
        parent.notification.showDialog(dialogType.INFO, "INFO", "This is some text <span class='font_bold'>and bold</span> text");
    }
    this.showWarning = function() {
        parent.notification.showDialog(dialogType.WARNING, "WARNING", "This is some text <span class='font_bold'>and bold</span> text");
    }
    this.showError = function() {
        parent.notification.showDialog(dialogType.ERROR, "ERROR", "This is some text <span class='font_bold'>and bold</span> text");
    }
    
    this.showInfo2 = function() {
        parent.notification.showDialog(dialogType.INFO, "INFO", "This is some text <span class='font_bold'>and bold</span> text", [{label:"Yes"}, {label:"No"}]);
    }
    this.showWarning2 = function() {
        parent.notification.showDialog(dialogType.WARNING, "WARNING", "This is some text <span class='font_bold'>and bold</span> text", [{label:"Yes"}, {label:"No"}]);
    }
    this.showError2 = function() {
        parent.notification.showDialog(dialogType.ERROR, "ERROR", "This is some text <span class='font_bold'>and bold</span> text", [{label:"Yes"}, {label:"No"}]);
    }
    
    this.showInfo3 = function() {
        parent.notification.showDialog(dialogType.INFO, "INFO", "This is some text <span class='font_bold'>and bold</span> text", [{label:"Yes"}, {label:"No"},{label:"Maybe"}]);
    }
    this.showWarning3 = function() {
        parent.notification.showDialog(dialogType.WARNING, "WARNING", "This is some text <span class='font_bold'>and bold</span> text", [{label:"Yes"}, {label:"No"},{label:"Maybe"}]);
    }
    this.showError3 = function() {
        parent.notification.showDialog(dialogType.ERROR, "ERROR", "This is some text <span class='font_bold'>and bold</span> text", [{label:"Yes"}, {label:"No"},{label:"Maybe"}]);
    }
    
})
console.log('loaded file: dialog_ViewModel.js');
