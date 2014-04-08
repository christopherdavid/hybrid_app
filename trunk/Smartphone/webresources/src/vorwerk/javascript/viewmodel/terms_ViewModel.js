resourceHandler.registerFunction('terms_ViewModel.js', function(parent) {
    console.log('instance created for: Terms_ViewModel');
    var that = this, termsScroll;
    this.conditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    
    this.init = function() {
       termsScroll = new iScroll('legalWrapperTerms',{
            hScrollbar : false,
            vScrollbar : false,
            bounce : true,
            vScroll : true,
            hScroll : true,
            momentum : true
        });
        
        $.get(parent.config.policyDocumentURL + 'documents/termsDocument_' + $.i18n.lng() + '.htm',
            function(data){
                $("#legalScrollerTerms").html(data);
                termsScroll.refresh();
        }).error(function() {
              $("#legalScrollerTerms").html("Document Not found.");
        }); 
         
        $(document).one("pageshow.legalTerms", function(e) {
            var tempLine = $("#topLineTerms");
            $("#legalWrapperTerms").css({
                "top":(tempLine.offset().top + tempLine.height())
            });
            termsScroll.refresh();
        });
        
        $(window).on("resize.legalTerms", function(e) {
            var tempLine = $("#topLineTerms");
            $("#legalWrapperTerms").css({
                "top":(tempLine.offset().top + tempLine.height())
            });
        
            termsScroll.refresh();
        });
    };
    
     this.deinit = function() {
        $(window).off(".legalTerms");
        termsScroll.destroy();
    };
    
    this.back = function() {
        parent.flowNavigator.previous();
    };
    
    this.reload = function() {
        this.conditions = {};
    };
    
});
console.log('loaded file: terms_ViewModel.js');
