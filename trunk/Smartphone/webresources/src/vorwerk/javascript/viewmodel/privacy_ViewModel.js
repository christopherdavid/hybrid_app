resourceHandler.registerFunction('privacy_ViewModel.js', function(parent) {
    console.log('instance created for: Privacy_ViewModel');
    var that = this, privacyScroll;
    this.conditions = {};
    this.robot = parent.communicationWrapper.getDataValue("selectedRobot");
    this.init = function() {
    
      privacyScroll = new iScroll('legalWrapperPrivacy',{
            hScrollbar : false,
            vScrollbar : false,
            bounce : true,
            vScroll : true,
            hScroll : true,
            momentum : false
        });
         
        $.get(parent.config.policyDocumentURL + 'documents/privacyDocument_' + $.i18n.lng() + '.htm',
            function(data){
                $("#legalScrollerPrivacy").html(data);
                privacyScroll.refresh();
            }).error(function() {
              $("#legalScrollerPrivacy").html("Document Not found.");
            });
        
         $(document).one("pageshow.legalPrivacy", function(e) {
            var tempLine = $("#topLinePrivacy");
            $("#legalWrapperPrivacy").css({
                "top":(tempLine.offset().top + tempLine.height())
            });
            privacyScroll.refresh();
            console.log('scroll refreshed');
        });
        
        $(window).on("resize.legalPrivacy", function(e) {
            var tempLine = $("#topLinePrivacy");
            $("#legalWrapperPrivacy").css({
                "top":(tempLine.offset().top + tempLine.height())
            });
            console.log('scroll resized');
            privacyScroll.refresh();
        });
       console.log('Document Loaded from : '+ parent.config.policyDocumentURL + 'documents/privacyDocument_' + $.i18n.lng() + '.htm'); 
      
       
    };
    
     this.deinit = function() {
        $(window).off(".legalPrivacy");
        privacyScroll.destroy();
    }
    
    this.back = function() {
        parent.flowNavigator.previous();
    };
    
    this.reload = function() {
        this.conditions = {};
    }
    
})
console.log('loaded file: privacy_ViewModel.js');
