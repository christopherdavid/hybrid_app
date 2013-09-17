resourceHandler.registerFunction('country_ViewModel.js', function(parent) {
    console.log('instance created for: country_ViewModel');
    var that = this, myScroll;
    this.conditions = {};
    
    this.init = function() {
        myScroll = new iScroll('countryWrapper',{
            hScrollbar : false,
            vScrollbar : false,
            bounce : true,
            vScroll : true,
            hScroll : true,
            momentum : true
        });
        
        $(document).one("pageshow.country", function(e) {
            myScroll.refresh();
        });
    }
    this.reload = function() {
        // remove conditions
        that.conditions = {};
    };
    
    this.back = function() {
        that.conditions['back'] = true;
        parent.flowNavigator.previous();
    };
    
})
console.log('loaded file: country_ViewModel.js');
