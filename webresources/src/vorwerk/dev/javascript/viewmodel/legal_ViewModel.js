resourceHandler.registerFunction('legal_ViewModel.js', function(parent) {
    console.log('instance created for: legal_ViewModel');
    var that = this, myScroll;
    this.conditions = {};
    
    this.init = function() {
        
        myScroll = new iScroll('legalWrapper',{
            hScrollbar : false,
            vScrollbar : false,
            bounce : true,
            vScroll : true,
            hScroll : true,
            momentum : true
        });
        
        $(document).one("pageshow.legal", function(e) {
            var tempLine = $("#topLine");
            $("#legalWrapper").css({
                "top":(tempLine.offset().top + tempLine.height())
            });
            myScroll.refresh();
        });
        
        $(window).on("resize.legal", function(e) {
            var tempLine = $("#topLine");
            $("#legalWrapper").css({
                "top":(tempLine.offset().top + tempLine.height())
            });
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
    
    this.deinit = function() {
        $(window).off(".legal");
        myScroll.destroy();
    }
    
})
console.log('loaded file: legal_ViewModel.js');
